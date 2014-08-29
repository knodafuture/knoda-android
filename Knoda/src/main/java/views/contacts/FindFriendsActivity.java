package views.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knoda.knoda.R;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Collection;
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
import models.GroupInvitation;
import models.KnodaInfo;
import models.ServerError;
import models.SocialAccount;
import models.User;
import models.UserContact;
import models.UserContacts;
import networking.NetworkCallback;
import pubsub.LoginFlowDoneEvent;
import unsorted.Logger;
import unsorted.PagerSlidingTabStrip;
import views.core.BaseActivity;

public class FindFriendsActivity extends BaseActivity {


    public HashMap<String, KnodaInfo> following = new HashMap<String, KnodaInfo>();
    public HashMap<String, KnodaInfo> followingFacebook = new HashMap<String, KnodaInfo>();
    public HashMap<String, KnodaInfo> followingTwitter = new HashMap<String, KnodaInfo>();
    public HashMap<String, GroupInvitation> inviting = new HashMap<String, GroupInvitation>();


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
    public FacebookManager facebookManager;
    public TwitterManager twitterManager;

    @OnClick(R.id.wall_close)
    public void close() {
        finish();
        bus.post(new LoginFlowDoneEvent());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_findfriends);
        ButterKnife.inject(this);
        bus.register(this);
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
                                //submit invitations and follow requests here

//                                networkingManager.followUsers(following,new NetworkListCallback<FollowUser>() {
//                                    @Override
//                                    public void completionHandler(ArrayList<FollowUser> object, ServerError error) {
//
//                                    }
//                                });
//                                networkingManager.sendInvitations(inviting, new NetworkCallback<GroupInvitation>() {
//                                    @Override
//                                    public void completionHandler(GroupInvitation object, ServerError error) {
//
//                                    }
//                                });

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

    private class getContactsTask extends AsyncTask<Void, Void, Collection<UserContact>> {
        public ArrayList<UserContact> contacts;

        @Override
        protected Collection<UserContact> doInBackground(Void... params) {
            return getContacts().values();
        }

        @Override
        protected void onPostExecute(Collection<UserContact> contacts1) {
            localContacts = new UserContacts();
            localContacts.contacts = contacts1;
            progressDialog.hide();
            setupUI();
        }
    }


    public void addFBAccount() {
        spinner.show();
        facebookManager.openSession(this, new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    spinner.hide();
                    errorReporter.showError(error);
                    return;
                }

                userManager.addSocialAccount(object, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User user, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            //parent.errorReporter.showError(error);
                            return;
                        }
                        recreate();
                        //updateUser(user);
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

    public void finishAddingTwitterAccount() {
        Logger.log("FINISHING TWITTER -------");
        spinner.show();
        twitterManager.getSocialAccount(new NetworkCallback<SocialAccount>() {
            @Override
            public void completionHandler(SocialAccount object, ServerError error) {
                if (error != null) {
                    //errorReporter.showError(error);
                    spinner.hide();
                    return;
                }

                userManager.addSocialAccount(object, new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User user, ServerError error) {
                        spinner.hide();
                        if (error != null) {
                            errorReporter.showError(error);
                            return;
                        }
                        //updateUser(user);
                    }
                });
            }
        });
    }


}

