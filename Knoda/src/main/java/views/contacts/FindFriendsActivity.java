package views.contacts;

import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import managers.NetworkingManager;
import managers.SharedPrefManager;
import models.UserContact;
import models.UserContacts;
import pubsub.LoginFlowDoneEvent;
import unsorted.PagerSlidingTabStrip;
import views.core.BaseActivity;

public class FindFriendsActivity extends BaseActivity {


    public ArrayList<UserContact> following = new ArrayList<UserContact>();
    public ArrayList<UserContact> inviting = new ArrayList<UserContact>();
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
        tabs.setBackgroundColor(0xE0E0E0);
        tabs.setTabWidth((int) (size.x * 1.0f / 3));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Contacts");
        progressDialog.show();
        getContactsTask task = new getContactsTask();
        task.execute();

        //setupUI();

    }

    public void setSubmitBtnText() {
        submitBtn.setText("Follow (" + following.size() + ") + Invite (" + inviting.size() + ")");
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

//        for (Long l : localContacts.keySet()) {
//            UserContact userContact = localContacts.get(l);
//            System.out.print(userContact.contact_id + ", ");
//            if (userContact.phones.size() > 0) {
//                for (String s : userContact.phones)
//                    System.out.print(s + ", ");
//            }
//            if (userContact.emails.size() > 0) {
//                for (String s : userContact.emails)
//                    System.out.print(s + ", ");
//            }
//            System.out.println();
//        }

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


}

