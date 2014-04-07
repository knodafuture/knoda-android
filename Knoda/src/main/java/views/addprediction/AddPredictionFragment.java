package views.addprediction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.InjectView;
import butterknife.OnClick;
import helpers.EditTextDoneCallback;
import helpers.EditTextHelper;
import models.Group;
import models.Prediction;
import models.ServerError;
import models.Tag;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.NewPredictionEvent;
import views.core.BaseFragment;
import factories.GsonF;

public class AddPredictionFragment extends BaseFragment {

    @InjectView(R.id.add_prediction_body_edittext)
    EditText bodyEditText;

    @InjectView(R.id.add_prediction_resolution_date_edittext)
    EditText resolutionDateEditText;

    @InjectView(R.id.add_prediction_resolution_time_edittext)
    EditText resolutionTimeEditText;

    @InjectView(R.id.add_prediction_vote_date_edittext)
    EditText voteDateEditText;

    @InjectView(R.id.add_prediction_vote_time_edittext)
    EditText voteTimeEditText;

    @OnClick(R.id.add_prediction_topic_view) void onTopicClicked() {
        hideKeyboard();
        if (topicsDialog != null)
            topicsDialog.show();
    }

    @OnClick(R.id.add_prediction_group_view) void onGroupClicked() {
        hideKeyboard();
        if (groupsDialog != null)
            groupsDialog.show();
    }

    @InjectView(R.id.add_prediction_topic_textview)
    TextView topicTextView;

    @InjectView(R.id.add_prediction_group_textview)
    TextView groupTextView;

    @InjectView(R.id.add_prediction_counter_textview)
    TextView bodyCounterTextView;

    @InjectView(R.id.add_prediction_user_avatar)
    NetworkImageView avatarImageView;


    private DateTimePicker resolutionDatePicker;
    private DateTimePicker votingDatePicker;

    private ArrayList<Tag> tags;
    private Tag selectedTag;
    private AlertDialog topicsDialog;
    private Group selectedGroup;
    private AlertDialog groupsDialog;

    private MessageCounter bodyCounter;
    private Group group;


    public static AddPredictionFragment newInstance(Group group) {
        AddPredictionFragment fragment = new AddPredictionFragment();
        Bundle bundle = new Bundle();
        if (group != null) {
            bundle.putString("GROUP", GsonF.actory().toJson(group));
        }
        fragment.setArguments(bundle);
        return fragment;
    }
    public AddPredictionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey("GROUP")) {
            group = GsonF.actory().fromJson(getArguments().getString("GROUP"), Group.class);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.submit, menu);
        menu.removeGroup(R.id.default_menu_group);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_submit)
            submitPrediction();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_prediction, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle("PREDICT");

        votingDatePicker = new DateTimePicker(voteDateEditText, voteTimeEditText,Calendar.getInstance(), new DateTimePicker.OnCalenderChangedListener() {
            @Override
            public void onCalenderChanged(Calendar calendar) {
                resolutionDatePicker.setMinimumCalender(calendar);
            }
        });
        resolutionDatePicker = new DateTimePicker(resolutionDateEditText, resolutionTimeEditText, Calendar.getInstance(), null);

        networkingManager.getTags(new NetworkListCallback<Tag>() {
            @Override
            public void completionHandler(ArrayList<Tag> object, ServerError error) {
                if (error == null) {
                    tags = object;
                    buildTopicsDialog();
                }
            }
        });

        buildGroupsDialog();
        if (group != null) {
            selectedGroup = userManager.getGroupById(group.id);
            groupTextView.setText(group.name);
        }

        avatarImageView.setImageUrl(userManager.getUser().avatar.small, networkingManager.getImageLoader());

        bodyCounter = new MessageCounter(bodyEditText, bodyCounterTextView, 300);

        EditTextHelper.assignDoneListener(bodyEditText, new EditTextDoneCallback() {
            @Override
            public void onDone() {
                hideKeyboard();
            }
        });
        FlurryAgent.logEvent("Add_Prediction_Screen");
    }

    private void buildTopicsDialog() {
        String[] items = new String[tags.size()];

        for (int i = 0; i < tags.size(); i++) {
            items[i] = tags.get(i).name;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick a category")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {
                        onTopicSelected(tags.get(index));
                    }
                });
        topicsDialog = builder.create();
    }

    private void onTopicSelected(Tag tag) {
        selectedTag = tag;
        topicTextView.setText(tag.name);
    }

    private void buildGroupsDialog() {
        String[] items = new String[userManager.groups.size()+1];
        items[0] = "Public";
        for (int i = 0; i < userManager.groups.size(); i++) {
            items[i+1] = userManager.groups.get(i).name;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Public")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            selectedGroup = null;
                            groupTextView.setText("Public");
                        } else {
                            selectedGroup = userManager.groups.get(i-1);
                            groupTextView.setText(userManager.groups.get(i-1).name);
                        }
                    }
                });
        groupsDialog = builder.create();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    private void submitPrediction() {

        hideKeyboard();
        if (!validate())
            return;

        Prediction prediction = new Prediction();

        prediction.body = bodyEditText.getText().toString();
        prediction.tags.add(selectedTag.name);
        if (selectedGroup != null) {
            prediction.groupId = selectedGroup.id;
        }
        prediction.expirationDate = votingDatePicker.getDateTime();
        prediction.resolutionDate = resolutionDatePicker.getDateTime();

        spinner.show();

        networkingManager.submitPrediction(prediction, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                spinner.hide();
                if (error != null) {
                    errorReporter.showError(error);
                } else {
                    bus.post(new NewPredictionEvent(object));
                    popFragment();
                }
            }
        });

    }

    private boolean validate() {
        String errorMessage = null;

        DateTime votingDate = votingDatePicker.getDateTime();
        DateTime resolutionDate = resolutionDatePicker.getDateTime();
        DateTime now = new DateTime();

        if (bodyEditText.getText().length() == 0)
            errorMessage = "Please enter a prediction.";
        else if (selectedTag == null)
            errorMessage = "Please select a category.";
        else if (resolutionDate.isBefore(votingDate))
            errorMessage = "You can't Knoda Future before the voting deadline.";
        else if (resolutionDate.isBefore(now) || votingDate.isBefore(now))
            errorMessage = "You can't end voting or resolve your prediction in the past";

        if (errorMessage != null) {
            errorReporter.showError(errorMessage);
            return false;
        }

        return true;
    }
}
