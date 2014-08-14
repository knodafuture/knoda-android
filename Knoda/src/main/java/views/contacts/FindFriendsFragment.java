package views.contacts;

/**
 * Created by jeffcailteux on 8/14/14.
 */

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.knoda.knoda.R;

import butterknife.InjectView;
import butterknife.OnClick;
import helpers.TypefaceSpan;
import pubsub.LoginFlowDoneEvent;
import unsorted.PagerSlidingTabStrip;
import views.core.BaseDialogFragment;

public class FindFriendsFragment extends BaseDialogFragment {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @InjectView(R.id.findfriends_title)
    TextView title;

    @OnClick(R.id.wall_close)
    public void close() {
        bus.post(new LoginFlowDoneEvent());
        dismissFade();
    }

    @InjectView(R.id.findfriends_submit)
    Button submitBtn;


    public FindFriendsFragment() {
    }

    public static FindFriendsFragment newInstance() {
        FindFriendsFragment fragment = new FindFriendsFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_findfriends, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SpannableString s = new SpannableString("FIND FRIENDS");
        s.setSpan(new TypefaceSpan(getActivity(), "KronaOne-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(s);

    }

}
