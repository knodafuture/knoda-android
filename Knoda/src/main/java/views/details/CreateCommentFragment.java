package views.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import org.joda.time.DateTime;

import butterknife.InjectView;
import factories.GsonF;
import helpers.EditTextDoneCallback;
import helpers.EditTextHelper;
import models.Comment;
import models.Prediction;
import models.ServerError;
import networking.NetworkCallback;
import pubsub.NewCommentEvent;
import views.addprediction.MessageCounter;
import views.core.BaseFragment;


public class CreateCommentFragment extends BaseFragment {

    @InjectView(R.id.add_comment_body_edittext)
    EditText bodyEditText;
    @InjectView(R.id.add_comment_counter_textview)
    TextView messageCounterTextView;
    private Prediction prediction;
    private MessageCounter messageCounter;
    private boolean inProgress;

    public CreateCommentFragment() {
    }

    public static CreateCommentFragment newInstance(Prediction prediction) {
        CreateCommentFragment fragment = new CreateCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PREDICTION", GsonF.actory().toJson(prediction));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prediction = GsonF.actory().fromJson(getArguments().getString("PREDICTION"), Prediction.class);
        setHasOptionsMenu(true);
        setTitle("COMMENT");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.submit, menu);
        menu.removeGroup(R.id.default_menu_group);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_submit)
            submitComment();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_comment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showKeyboard(bodyEditText);
        messageCounter = new MessageCounter(bodyEditText, messageCounterTextView, 300);

        EditTextHelper.assignDoneListener(bodyEditText, new EditTextDoneCallback() {
            @Override
            public void onDone() {
                submitComment();
            }
        });
    }

    private void submitComment() {
        if (!validate() || inProgress)
            return;

        spinner.show();
        inProgress = true;

        Comment comment = new Comment();
        comment.text = bodyEditText.getText().toString();
        comment.creationDate = new DateTime();

        if (prediction.challenge != null)
            comment.challenge = prediction.challenge;
        comment.username = userManager.getUser().username;
        comment.userId = userManager.getUser().userId;
        comment.userAvatar = userManager.getUser().avatar;
        comment.predictionId = prediction.id;

        networkingManager.addComment(comment, new NetworkCallback<Comment>() {
            @Override
            public void completionHandler(Comment object, ServerError error) {
                spinner.hide();
                inProgress = false;
                if (error != null)
                    errorReporter.showError(error);
                else {
                    bus.post(new NewCommentEvent(object));
                    FlurryAgent.logEvent("CREATE_COMMENT");
                    popFragment();
                }
            }
        });
    }

    private boolean validate() {
        if (bodyEditText.getText().toString().length() == 0) {
            errorReporter.showError("Please enter a comment");
            return false;
        }
        return true;
    }
}
