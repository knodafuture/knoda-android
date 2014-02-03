package helpers;

import android.widget.AbsListView;

/**
 * Created by nick on 2/3/14.
 */
public class ListenerHelper {

    public static AbsListView.OnScrollListener concatListeners(final AbsListView.OnScrollListener one, final AbsListView.OnScrollListener two) {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                one.onScrollStateChanged(absListView, i);
                two.onScrollStateChanged(absListView, i);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                one.onScroll(absListView, i, i2, i3);
                two.onScroll(absListView, i, i2, i3);

            }
        };
    }
}
