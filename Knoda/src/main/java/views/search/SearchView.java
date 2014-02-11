package views.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.knoda.knoda.R;

import helpers.EditTextDoneCallback;
import helpers.EditTextHelper;

/**
 * Created by nick on 2/11/14.
 */
public class SearchView extends RelativeLayout {

    public EditText searchField;

    private ImageView cancelButton;

    private SearchViewCallbacks callbacks;

    public interface SearchViewCallbacks {
        void onSearch(String searchText);
    }

    public SearchView(Context context) {
        super(context);
        initView(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }


    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_search, this);
    }

    @Override
    public void onFinishInflate() {
        searchField = (EditText)findViewById(R.id.search_view_searchfield);
        cancelButton = (ImageView)findViewById(R.id.search_view_clear);

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                searchField.getEditableText().clear();
            }
        });

        EditTextHelper.assignDoneListener(searchField, new EditTextDoneCallback() {
            @Override
            public void onDone() {
                if (callbacks != null)
                    callbacks.onSearch(searchField.getText().toString());
            }
        });
    }

    public void setCallbacks(SearchViewCallbacks callbacks) {
        this.callbacks = callbacks;
    }

}
