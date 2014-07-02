package views.group;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

import models.InvitationHolder;

/**
 * Created by nick on 4/7/14.
 */
public class InvitationsListCell extends RelativeLayout {

    private static final int SWIPE_MIN_DISTANCE = 60;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    TextView nameTextView;
    TextView metaDataTextView;
    RelativeLayout removeButton;
    RelativeLayout confirmRemoveButton;
    ImageView knodaImageView;
    GestureDetector gestureDetector;
    long animationTime;
    int position;
    InvitationsCellCallbacks callbacks;
    InvitationHolder holder;

    public InvitationsListCell(Context context, InvitationsCellCallbacks callbacks) {
        super(context);
        this.callbacks = callbacks;
        initView(context);
    }

    public InvitationsListCell(Context context) {
        super(context);
        initView(context);
    }

    public InvitationsListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_cell_invitations, this);
        nameTextView = (TextView) findViewById(R.id.invitations_cell_name_textview);
        metaDataTextView = (TextView) findViewById(R.id.invitations_cell_metadata_textview);
        removeButton = (RelativeLayout) findViewById(R.id.invitations_cell_remove);
        confirmRemoveButton = (RelativeLayout) findViewById(R.id.invitations_cell_confirm_remove);
        knodaImageView = (ImageView) findViewById(R.id.invitations_cell_knoda_user_icon);
        animationTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

        gestureDetector = new GestureDetector(new GestureListener());


        if (callbacks != null)
            removeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    remove();
                }
            });
        else
            removeButton.setVisibility(INVISIBLE);

        confirmRemoveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmRemove();
            }
        });
    }

    public void setInvitationHolder(InvitationHolder holder, int position) {
        this.holder = holder;
        this.position = position;

        if (holder.isKnodaUser()) {
            knodaImageView.setVisibility(VISIBLE);
            metaDataTextView.setVisibility(GONE);
        } else {
            knodaImageView.setVisibility(INVISIBLE);
            metaDataTextView.setVisibility(VISIBLE);
        }

        nameTextView.setText(holder.getName());
        metaDataTextView.setText(holder.getMetadataString());
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
        callbacks.invitationRemovedAtPosition(position);
    }

    private void hideConfirmRemove() {
        confirmRemoveButton.animate().x(getWidth()).setDuration(animationTime).setListener(null);
        setOnTouchListener(null);
    }
    public interface InvitationsCellCallbacks {
        void invitationRemovedAtPosition(int position);
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
