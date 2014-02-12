package views.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

/**
 * Created by nick on 2/11/14.
 */
public class SearchUserCell extends RelativeLayout {

    public NetworkImageView imageView;
    public TextView textView;

    public SearchUserCell(Context context) {
        super(context);
        initView(context);
    }

    public SearchUserCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_cell_user, this);
        setBackgroundColor(getResources().getColor(R.color.lightGray));
        imageView = (NetworkImageView)findViewById(R.id.user_cell_avatar_imageview);
        textView = (TextView)findViewById(R.id.user_cell_username_textview);

    }
}
