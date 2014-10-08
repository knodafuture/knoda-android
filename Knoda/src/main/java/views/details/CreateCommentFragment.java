package views.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import unsorted.AutoCompleteAdapter;
import views.addprediction.MessageCounter;
import views.core.BaseFragment;
import views.core.MainActivity;


public class CreateCommentFragment extends BaseFragment {

    @InjectView(R.id.add_comment_body_edittext)
    AutoCompleteTextView bodyEditText;
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
        menu.clear();
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
        ((MainActivity) getActivity()).hideNavbar();
        showKeyboard(bodyEditText);
        messageCounter = new MessageCounter(bodyEditText, messageCounterTextView, 300);

        EditTextHelper.assignDoneListener(bodyEditText, new EditTextDoneCallback() {
            @Override
            public void onDone() {
                submitComment();
            }
        });


        final AutoCompleteAdapter adapter = new AutoCompleteAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, (MainActivity) getActivity());
        bodyEditText.setAdapter(adapter);
        bodyEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String autoword = adapter.getItem(position);
                String[] words = adapter.allwords.split(" ");
                String all = "";
                for (int x = 0; x < words.length - 1; x++) {
                    String s = words[x];
                    all += s + " ";
                }
                all += words[words.length - 1].substring(0, 1);
                all += autoword;
                bodyEditText.setText(all);
                bodyEditText.setSelection(all.length());
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
                    FlurryAgent.logEvent("CREATE_COMMENT");
                    bus.post(new NewCommentEvent(object));
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).showNavbar();
    }
}
