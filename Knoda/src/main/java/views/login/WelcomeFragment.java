package views.login;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.knoda.knoda.R;

import butterknife.InjectView;
import views.core.BaseFragment;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class WelcomeFragment extends BaseFragment {

    @InjectView(R.id.button_sign_in)
    Button signInButton;

    @InjectView(R.id.button_sign_up)
    Button signUpButton;

    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }
    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.removeGroup(R.id.default_menu_group);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().hide();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpOnClickListeners();
        final View swipeMore = (View) view.findViewById(R.id.swipe_more);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_flipper);
        final ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == (adapter.getCount() - 1)) {
                      swipeMore.setVisibility(View.INVISIBLE);
                } else {
                    swipeMore.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void setUpOnClickListeners() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment fragment = LoginFragment.newInstance();
                pushFragment(fragment);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpFragment fragment = SignUpFragment.newInstance();
                pushFragment(fragment);
            }
        });
    }


    private class ImagePagerAdapter extends PagerAdapter {
        private int[] mImages = new int[] {
                R.drawable.splash_logo,
                R.drawable.no_content_placeholder
        };

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = getActivity();
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(mImages[position]);
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }
}
