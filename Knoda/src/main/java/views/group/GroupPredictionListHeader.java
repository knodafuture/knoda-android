package views.group;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.knoda.knoda.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import models.Group;

public class GroupPredictionListHeader extends RelativeLayout {
    public interface GroupPredictionListHeaderDelegate {
        void onRankings();
        void onSettings();
    }
    public Group group;

    private GroupPredictionListHeaderDelegate delegate;

    @OnClick(R.id.group_rankings_container) void onRankings() {
        delegate.onRankings();
    }
    @OnClick(R.id.group_settings_container) void onSettings() {
        delegate.onSettings();
    }
    public GroupPredictionListHeader(Context context) {
        super(context);
        initView(context);
    }

    public GroupPredictionListHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public GroupPredictionListHeader(Context context, GroupPredictionListHeaderDelegate delegate) {
        super(context);
        this.delegate = delegate;
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_group_prediction_list_header, this);
        ButterKnife.inject(this);
    }
}
