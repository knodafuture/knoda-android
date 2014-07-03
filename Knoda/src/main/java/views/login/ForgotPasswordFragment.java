package views.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.knoda.knoda.R;

import butterknife.InjectView;
import butterknife.OnClick;
import models.BaseModel;
import models.ForgotPasswordRequest;
import models.ServerError;
import networking.NetworkCallback;
import views.core.BaseDialogFragment;

public class ForgotPasswordFragment extends BaseDialogFragment {
    @InjectView(R.id.forgot_username_edittext)
    EditText editText;

    public ForgotPasswordFragment() {
    }

    public static ForgotPasswordFragment newInstance() {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        return fragment;
    }

    @OnClick(R.id.forgot_close)
    void onClose() {
        dismissFade();
    }

    @OnClick(R.id.submit_button)
    void onSubmitClick() {
        submit();
    }

    @OnClick(R.id.wall_login)
    void onLoginClick() {
        LoginFragment f = LoginFragment.newInstance();
        f.show(getFragmentManager(), "login");
        dismissFade();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        updateBackground();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    public void submit() {

        if (!validate())
            return;

        hideKeyboard();
        spinner.show();

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.login = editText.getText().toString();

        networkingManager.sendForgotPasswordRequest(request, new NetworkCallback<BaseModel>() {
            @Override
            public void completionHandler(BaseModel object, ServerError error) {
                spinner.hide();
                if (error != null) {
                    errorReporter.showError("We were unable to reset your password. Are you sure your email address has been registered on Knoda?");
                } else {
                    errorReporter.showError("A link to reset your password was sent to your email");
                    dismissFade();
                }
            }
        });

    }

    public boolean validate() {
        if (editText.getText().length() == 0) {
            errorReporter.showError("Please enter your email address.");
            return false;
        }

        return true;
    }
}
