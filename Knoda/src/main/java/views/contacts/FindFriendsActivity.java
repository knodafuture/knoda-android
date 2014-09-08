package views.contacts;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.knoda.knoda.R;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import adapters.FindFriendsPagerAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import helpers.TypefaceSpan;
import managers.FacebookManager;
import managers.NetworkingManager;
import managers.SharedPrefManager;
import managers.TwitterManager;
import managers.UserManager;
import models.FollowUser;
import models.GroupInvitation;
import models.KnodaInfo;
import models.ServerError;
import models.SocialAccount;
import models.User;
import models.UserContact;
import models.UserContacts;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.LoginFlowDoneEvent;
import unsorted.Logger;
import unsorted.PagerSlidingTabStrip;
import views.core.BaseActivity;

public class FindFriendsActivity extends BaseActivity {


    public HashMap<String, KnodaInfo> following = new HashMap<String, KnodaInfo>();
    public HashMap<String, KnodaInfo> followingFacebook = new HashMap<String, KnodaInfo>();
    public HashMap<String, KnodaInfo> followingTwitter = new HashMap<String, KnodaInfo>();
    public HashMap<String, GroupInvitation> inviting = new HashMap<String, GroupInvitation>();
    public FacebookManager facebookManager;
    public TwitterManager twitterManager;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.findfriends_title)
    TextView title;
    @InjectView(R.id.findfriends_container)
    LinearLayout container;
    ViewPager mViewPager;
    @InjectView(R.id.findfriends_submit)
    Button submitBtn;
    Bus bus = new Bus();
    UserContacts localContacts;
    ProgressDialog progressDialog;

    int invitesubmits = 0;

    @OnClick(R.id.wall_close)
    public void close() {
        if (userManager.getUser().phoneNumber == null || userManager.getUser().phoneNumber.length() == 0) {
            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String phone = manager.getLine1Number();

            LayoutInflater li = getLayoutInflater();
            final View postView = li.inflate(R.layout.dialog_upload_phone, null);
            ((TextView) postView.findViewById(R.id.dialog_phone_tv)).setText("Now that you've connected with some friends, make it easier for them to find you on Knoda by allowing us to have your number. We promise not to call after midnight (or ever).");
            final EditText msg = (EditText) postView.findViewById(R.id.message);
            if (phone != null && phone.length() > 0)
                msg.setText(phone);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Phone Number")
                    .setView(postView)
                    .setCancelable(false)
                    .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            bus.post(new LoginFlowDoneEvent());
                        }
                    })
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String phone = msg.getText().toString();
                            User u = userManager.getUser();
                            u.phoneNumber = stripChars(phone);
                            spinner.show();
                            Toast.makeText(FindFriendsActivity.this, "Setting " + u.phoneNumber, Toast.LENGTH_SHORT).show();
                            userManager.updateUser(u, new NetworkCallback<User>() {
                                @Override
                                public void completionHandler(User object, ServerError error) {
                                    if (error == null) {
                                        userManager.refreshUser(new NetworkCallback<User>() {
                                            @Override
                                            public void completionHandler(User object, ServerError error) {
                                                spinner.hide();
                                                finish();
                                                bus.post(new LoginFlowDoneEvent());
                                            }
                                        });
                                    } else {
                                        spinner.hide();
                                        finish();
                                        bus.post(new LoginFlowDoneEvent());
                                    }
                                }
                            });
                        }
                    })
                    .show();
        } else {
            spinner.hide();
            finish();
            bus.post(new LoginFlowDoneEvent());
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        bus.register(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_findfriends);
        ButterKnife.inject(this);

        networkingManager = new NetworkingManager(this);
        networkingManager.sharedPrefManager = new SharedPrefManager(this);
        userManager = new UserManager(networkingManager, sharedPrefManager);
        facebookManager = new FacebookManager(userManager, networkingManager);
        twitterManager = new TwitterManager();

        progressDialog = new ProgressDialog(FindFriendsActivity.this);
        progressDialog.setMessage("Getting Contacts");
        progressDialog.show();
        refreshUser();

        SpannableString s = new SpannableString("FIND FRIENDS");
        s.setSpan(new TypefaceSpan(this, "KronaOne-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(s);
        setSubmitBtnText();

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int onedp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        final int onesp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, getResources().getDisplayMetrics());
        Point size = new Point();
        display.getSize(size);
        tabs.setIndicatorHeight(onedp * 4);
        tabs.setTextSize(onesp * 16);
        tabs.setBackgroundColor(getResources().getColor(R.color.knodaLightGreen));
        tabs.setIndicatorColorResource(R.color.knodaDarkGreen);
        tabs.setTabWidth((int) (size.x * 1.0f / 3));

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FindFriendsActivity.this);
                builder.setTitle("Contact Submission")
                        .setMessage("Are you ready to submit " + inviting.size() + " invitations and " + (following.size() + followingFacebook.size() + followingTwitter.size()) + " follow requests?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println("Following:");
                                for (String s : following.keySet()) {
                                    System.out.println(s);
                                }
                                for (String s : followingFacebook.keySet()) {
                                    System.out.println(s);
                                }
                                for (String s : followingTwitter.keySet()) {
                                    System.out.println(s);
                                }
                                System.out.println("Inviting:");
                                for (String s : inviting.keySet()) {
                                    System.out.println(s);
                                }

                                //combine all hashsets
                                following.putAll(followingFacebook);
                                following.putAll(followingTwitter);

                                spinner.show();
                                //submit invitations and follow requests here
                                networkingManager.followUsers(following.values(), new NetworkListCallback<FollowUser>() {
                                    @Override
                                    public void completionHandler(ArrayList<FollowUser> object, ServerError error) {
                                        invitesubmits++;
                                        if (invitesubmits == 2) {
                                            if (error == null) {
                                                Toast.makeText(FindFriendsActivity.this, "Invitations and follow requests sent successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                            spinner.hide();
                                            close();
                                        }
                                    }
                                });

                                networkingManager.sendInvitations(inviting.values(), new NetworkCallback<GroupInvitation>() {
                                    @Override
                                    public void completionHandler(GroupInvitation object, ServerError error) {
                                        invitesubmits++;
                                        if (invitesubmits == 2) {
                                            if (error == null) {
                                                Toast.makeText(FindFriendsActivity.this, "Invitations and follow requests sent successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                            spinner.hide();
                                            close();
                                        }
                                    }
                                });


                            }
                        })
                        .show();
            }
        });

    }

    private void refreshUser() {
        userManager.refreshUser(new NetworkCallback<User>() {
            @Override
            public void completionHandler(User object, ServerError error) {
                if (error == null) {
                    getContactsTask task = new getContactsTask();
                    task.execute();
                } else {
                    refreshUser();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null)
            progressDialog.dismiss();
    }


    public void setSubmitBtnText() {
        submitBtn.setText("Follow (" + (following.size() + followingFacebook.size() + followingTwitter.size()) + ") + Invite (" + inviting.size() + ")");
    }

    public void setupUI() {
        if (mViewPager != null) {
            container.removeView(mViewPager);
        }
        mViewPager = new ViewPager(this);
        mViewPager.setId(2000 + new Random().nextInt(1000));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        container.addView(mViewPager);
        FindFriendsPagerAdapter adapter = new FindFriendsPagerAdapter(getFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        tabs.setViewPager(mViewPager);
    }


    private HashMap<Long, UserContact> getContacts() {
        HashMap<Long, UserContact> localContacts = new HashMap<Long, UserContact>();
        ContentResolver resolver = getContentResolver();
        Cursor c = resolver.query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.CONTACT_ID, ContactsContract.Data.HAS_PHONE_NUMBER, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.DATA1, ContactsContract.Data.MIMETYPE},
                ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND (" + ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=?)",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                ContactsContract.Data.CONTACT_ID);

        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndex(ContactsContract.Data.CONTACT_ID));
            String name = c.getString(c.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            String data1 = c.getString(c.getColumnIndex(ContactsContract.Data.DATA1));
            String type = c.getString(c.getColumnIndex(ContactsContract.Data.MIMETYPE));
            //System.out.println(id + ", name=" + name + ", data1=" + data1 + ",datatype=" + type);
            if (localContacts.get(id) != null) {
                UserContact userContact = localContacts.get(id);
                if (type.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                    userContact.phones.add(stripChars(data1));
                } else if (type.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                    userContact.emails.add(data1);
                }
            } else {
                UserContact userContact = new UserContact();
                userContact.contact_id = name;
                if (type.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                    if (stripChars(data1).length() < 7)
                        continue;
                    userContact.phones.add(stripChars(data1));
                } else if (type.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                    userContact.emails.add(data1);
                }
                localContacts.put(id, userContact);
            }
        }
        return localContacts;
    }

    public String stripChars(String s) {
        return s.replaceAll("[^\\d]", "");
    }

    public void addFBAccount() {
        spinner.show();
        facebookManager.openSession(this, new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    spinner.hide();
                    errorReporter.showError(error);
                    Toast.makeText(FindFriendsActivity.this, "Error connecting to Facebook", Toast.LENGTH_SHORT).show();
                    return;
                }
                System.out.println("account:" + object + " " + object.providerName);

                userManager.addSocialAccount(object, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User user, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            Toast.makeText(FindFriendsActivity.this, "Error connecting to Facebook", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        recreate();
                    }
                });
            }
        });
    }

    public void addTwitterAccount() {
        if (twitterManager.hasAuthInfo()) {
            finishAddingTwitterAccount();
        } else {
            spinner.show();
            sharedPrefManager.setTwitterAuthScreen("findfriends");
            twitterManager.openSession(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        spinner.hide();

        if (sharedPrefManager.getTwitterAuthScreen().equals("findfriends")) {
            sharedPrefManager.setTwitterAuthScreen("");
            finishAddingTwitterAccount();
        }

    }

    public void finishAddingTwitterAccount() {
        Logger.log("FINISHING TWITTER -------");
        spinner.show();
        twitterManager.getSocialAccount(new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    Toast.makeText(FindFriendsActivity.this, "Error connecting to Twitter", Toast.LENGTH_SHORT).show();
                    spinner.hide();
                    return;
                }

                userManager.addSocialAccount(object, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User user, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            Toast.makeText(FindFriendsActivity.this, "Error connecting to Twitter", Toast.LENGTH_SHORT).show();
                            errorReporter.showError(error);
                            return;
                        }
                        //recreate();
                    }
                });
            }
        });
    }

    private class getContactsTask extends AsyncTask<Void, Void, Collection<UserContact>> {
        public ArrayList<UserContact> contacts;

        @Override
        protected Collection<UserContact> doInBackground(Void... params) {
            return getContacts().values();
        }

        @Override
        protected void onPostExecute(Collection<UserContact> contacts1) {
            localContacts = new UserContacts();
            ArrayList<UserContact> sortList = new ArrayList<UserContact>(contacts1);
            Collections.sort(sortList, new Comparator<UserContact>() {
                @Override
                public int compare(UserContact lhs, UserContact rhs) {
                    return lhs.contact_id.toLowerCase().compareTo(rhs.contact_id.toLowerCase());
                }
            });
            localContacts.contacts = sortList;
            progressDialog.hide();
            setupUI();
        }
    }


}

