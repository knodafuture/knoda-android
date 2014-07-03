package views.group;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

import models.Group;
import models.Member;
import models.MembershipType;

/**
 * Created by nick on 4/6/14.
 */
public class MembershipCell extends RelativeLayout {

    private static final int SWIPE_MIN_DISTANCE = 60;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    TextView textView;
    RelativeLayout removeButton;
    RelativeLayout confirmRemoveButton;
    GestureDetector gestureDetector;
    long animationTime;
    Member member;
    int position;
    MembershipCellCallbacks callbacks;

    public MembershipCell(Context context, MembershipCellCallbacks callbacks) {
        super(context);
        this.callbacks = callbacks;
        initView(context);
    }

    public MembershipCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_cell_member, this);
        textView = (TextView) findViewById(R.id.member_cell_name_textview);
        removeButton = (RelativeLayout) findViewById(R.id.member_cell_remove);
        confirmRemoveButton = (RelativeLayout) findViewById(R.id.member_cell_confirm_remove);


        animationTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

        gestureDetector = new GestureDetector(new GestureListener());


        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                remove();
            }
        });

        confirmRemoveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmRemove();
            }
        });
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        confirmRemoveButton.setX(getWidth());
        confirmRemoveButton.setVisibility(VISIBLE);
    }

    private void remove() {
        confirmRemoveButton.animate().x(getWidth() - confirmRemoveButton.getWidth()).setDuration(animationTime).setListener(null);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    private void confirmRemove() {
        hideConfirmRemove();
        callbacks.memberRemovedAtPosition(position);
    }

    public void setMember(Member member, int position, Group group) {
        this.position = position;
        this.member = member;

        if (group.myMembership != null && group.myMembership.role == MembershipType.OWNER && !group.owner.equals(member.userId))
            removeButton.setVisibility(VISIBLE);
        else
            removeButton.setVisibility(INVISIBLE);

        textView.setText(member.username);

    }

    private void hideConfirmRemove() {
        confirmRemoveButton.animate().x(getWidth()).setDuration(animationTime).setListener(null);
        setOnTouchListener(null);
    }
    public interface MembershipCellCallbacks {
        void memberRemovedAtPosition(int position);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                hideConfirmRemove();
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                hideConfirmRemove();
                return true;
            }
            return false;
        }
    }
}
