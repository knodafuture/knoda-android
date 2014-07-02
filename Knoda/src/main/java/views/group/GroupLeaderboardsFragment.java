package views.group;

import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

import java.util.Random;

import adapters.LeaderboardPagerAdapter;
import butterknife.OnClick;
import factories.GsonF;
import models.Group;
import pubsub.ChangeGroupEvent;
import views.core.BaseFragment;

public class GroupLeaderboardsFragment extends BaseFragment {
    public Group group;
    private ViewPager mViewPager;
    private View view;

    public GroupLeaderboardsFragment() {
    }

    public static GroupLeaderboardsFragment newInstance(Group group) {
        GroupLeaderboardsFragment fragment = new GroupLeaderboardsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GROUP", GsonF.actory().toJson(group));
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.board_1)
    public void onBoard1Click() {
        mViewPager.setCurrentItem(0);
    }

    @OnClick(R.id.board_2)
    public void onBoard2Click() {
        mViewPager.setCurrentItem(1);
    }

    @OnClick(R.id.board_3)
    public void onBoard3Click() {
        mViewPager.setCurrentItem(2);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = GsonF.actory().fromJson(getArguments().getString("GROUP"), Group.class);
        bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_leaderboards, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(group.name.toUpperCase());
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new ChangeGroupEvent(group));
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.groups_leaderboards_container);
        if (mViewPager != null) {
            ll.removeView(mViewPager);
        }
        mViewPager = new ViewPager(getActivity().getApplicationContext());
        mViewPager.setId(2000 + group.id + new Random().nextInt(100));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int white = getResources().getColor(R.color.white);
                int green = getResources().getColor(R.color.knodaLighterGreen);
                if (position == 0) {
                    ((TextView) view.findViewById(R.id.board_1)).setTextColor(white);
                    ((TextView) view.findViewById(R.id.board_2)).setTextColor(green);
                    ((TextView) view.findViewById(R.id.board_3)).setTextColor(green);
                } else if (position == 1) {
                    ((TextView) view.findViewById(R.id.board_1)).setTextColor(green);
                    ((TextView) view.findViewById(R.id.board_2)).setTextColor(white);
                    ((TextView) view.findViewById(R.id.board_3)).setTextColor(green);
                } else if (position == 2) {
                    ((TextView) view.findViewById(R.id.board_1)).setTextColor(green);
                    ((TextView) view.findViewById(R.id.board_2)).setTextColor(green);
                    ((TextView) view.findViewById(R.id.board_3)).setTextColor(white);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ll.addView(mViewPager);
        FragmentPagerAdapter adapter = new LeaderboardPagerAdapter(getFragmentManager(), group);
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.post(new ChangeGroupEvent(null));
    }

}
