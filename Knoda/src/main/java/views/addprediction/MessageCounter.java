package views.addprediction;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by nick on 2/10/14.
 */
public class MessageCounter {

    public Integer charactersRemaining;

    public MessageCounter(final EditText editText, final TextView textView, final Integer maxCharacters) {

        this.charactersRemaining = maxCharacters;

        textView.setText(charactersRemaining.toString());
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxCharacters)});

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                charactersRemaining = maxCharacters - editText.getText().length();
                textView.setText(charactersRemaining.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

}
