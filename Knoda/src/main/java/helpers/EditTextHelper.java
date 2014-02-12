package helpers;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by nick on 1/20/14.
 */

public class EditTextHelper {

    public static void assignNextEditText(final EditText first, final EditText second) {
        TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    second.requestFocus();
                }
                return true;
            }
        };

        first.setOnEditorActionListener(listener);
    }

    public static void assignDoneListener(EditText editText, final EditTextDoneCallback callback) {
        TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    callback.onDone();
                }

                return true;
            }
        };

        editText.setOnEditorActionListener(listener);
    }

    public static void assignSearchListener(EditText editText, final EditTextDoneCallback callback) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH)
                    callback.onDone();
                return true;
            }
        });
    }

}

