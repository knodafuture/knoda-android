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

import adapters.LeaderboardPagerAdapter;
import factories.GsonF;
import models.Group;
import views.core.BaseFragment;

public class GroupLeaderboardsFragment extends BaseFragment {
    public Group group;

    public static GroupLeaderboardsFragment newInstance(Group group) {
        GroupLeaderboardsFragment fragment = new GroupLeaderboardsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GROUP", GsonF.actory().toJson(group));
        fragment.setArguments(bundle);
        return fragment;
    }

    public GroupLeaderboardsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = GsonF.actory().fromJson(getArguments().getString("GROUP"), Group.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_group_leaderboards, container, false);
        LinearLayout ll = (LinearLayout)view.findViewById(R.id.groups_leaderboards_container);
        ViewPager mViewPager = new ViewPager(getActivity().getApplicationContext());
        mViewPager.setId(20000 + group.id);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int white = getResources().getColor(R.color.white);
                int green = getResources().getColor(R.color.knodaLighterGreen);
                if (position == 0) {
                    ((TextView)view.findViewById(R.id.board_1)).setTextColor(white);
                    ((TextView)view.findViewById(R.id.board_2)).setTextColor(green);
                    ((TextView)view.findViewById(R.id.board_3)).setTextColor(green);
                } else if (position == 1) {
                    ((TextView)view.findViewById(R.id.board_1)).setTextColor(green);
                    ((TextView)view.findViewById(R.id.board_2)).setTextColor(white);
                    ((TextView)view.findViewById(R.id.board_3)).setTextColor(green);
                } else if (position == 2) {
                    ((TextView)view.findViewById(R.id.board_1)).setTextColor(green);
                    ((TextView)view.findViewById(R.id.board_2)).setTextColor(green);
                    ((TextView)view.findViewById(R.id.board_3)).setTextColor(white);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ll.addView(mViewPager);
        FragmentPagerAdapter adapter = new LeaderboardPagerAdapter(getFragmentManager(), group);
        mViewPager.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(group.name.toUpperCase());
    }
}
