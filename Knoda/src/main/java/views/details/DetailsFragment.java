package views.details;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapters.CommentAdapter;
import adapters.PagingAdapter;
import adapters.TallyAdapter;
import butterknife.OnClick;
import factories.GsonF;
import models.BaseModel;
import models.Comment;
import models.Contest;
import models.Prediction;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.NewCommentEvent;
import pubsub.PredictionChangeEvent;
import views.contests.ContestDetailFragment;
import views.core.BaseListFragment;
import views.core.MainActivity;
import views.predictionlists.AnotherUsersProfileFragment;
import views.predictionlists.CategoryFragment;
import views.predictionlists.GroupPredictionListFragment;

public class DetailsFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<Comment>, TallyAdapter.TallyAdapterDatasource,
        TallyAdapter.TallyAdapterDelegate, DetailsActionbar.DetailsActionBarDelegate,
        CommentAdapter.CommentAdapterDelegate, DetailsHeaderView.DetailsHeaderViewDelegate {

    private CommentAdapter commentAdapter;
    private TallyAdapter tallyAdapter;
    private DetailsHeaderView headerview;
    private DetailsActionbar actionbar;

    private Prediction prediction;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance(Prediction prediction) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PREDICTION", GsonF.actory().toJson(prediction));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Subscribe
    public void newComment(NewCommentEvent event) {
        prediction.commentCount++;
        headerview.setPrediction(prediction);
    }

    @OnClick(R.id.details_action_add_comment)
    void onComment() {
        CreateCommentFragment fragment = CreateCommentFragment.newInstance(prediction);
        pushFragment(fragment);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prediction = GsonF.actory().fromJson(getArguments().getString("PREDICTION"), Prediction.class);
        bus.register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        bus.post(new PredictionChangeEvent(prediction));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("DETAILS");
        FlurryAgent.logEvent("Prediction_Details_Screen");
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        return view;
    }

    @Override
    public PagingAdapter getAdapter() {
        return commentAdapter;
    }

    @Override
    public void onListViewCreated(ListView listView) {
        commentAdapter = new CommentAdapter(getActivity(), this, this, networkingManager.getImageLoader(), bus);
        tallyAdapter = new TallyAdapter(getActivity(), this, this);

        headerview = new DetailsHeaderView(getActivity(), this);
        headerview.setPrediction(prediction);

        if (prediction.userAvatar != null)
            headerview.predictionCell.avatarImageView.setImageUrl(prediction.userAvatar.small, networkingManager.getImageLoader());

        actionbar = new DetailsActionbar(getActivity(), this);

        commentAdapter.setActionBar(actionbar);
        commentAdapter.setHeader(headerview);

        tallyAdapter.setActionBar(actionbar);
        tallyAdapter.setHeader(headerview);

        tallyAdapter.loadPage(0);
    }

    @Override
    public void onInit(DetailsActionbar actionbar) {
        if (prediction.hasGroup()) {
            ((ImageView) actionbar.findViewById(R.id.details_action_share_imageview)).setImageResource(R.drawable.action_shareicon_inactive);
        }
        if (prediction.hasGroup() && (userManager.getGroupById(prediction.groupId) != null)) {
            actionbar.findViewById(R.id.details_action_similar_clickable).setVisibility(View.GONE);
            actionbar.findViewById(R.id.details_action_group_clickable).setVisibility(View.VISIBLE);
        } else if (prediction.contest_id != null) {
            actionbar.findViewById(R.id.details_action_similar_clickable).setVisibility(View.GONE);
            actionbar.findViewById(R.id.details_action_group_clickable).setVisibility(View.VISIBLE);
            ((TextView) actionbar.findViewById(R.id.details_action_group_textview)).setText("VIEW CONTEST");
        } else {
            actionbar.findViewById(R.id.details_action_similar_clickable).setVisibility(View.VISIBLE);
            actionbar.findViewById(R.id.details_action_group_clickable).setVisibility(View.GONE);
        }
    }

    @Override
    public void getObjectsAfterObject(Comment comment, final NetworkListCallback<Comment> callback) {

        Integer lastId = comment == null ? 0 : comment.id;

        networkingManager.getCommentsForPredictionWithLastId(this.prediction.id, lastId, new NetworkListCallback<Comment>() {
            @Override
            public void completionHandler(ArrayList<Comment> object, ServerError error) {
                callback.completionHandler(object, error);
            }
        });
    }

    @Override
    public void getAgreedUsers(NetworkListCallback<User> callback) {
        networkingManager.getAgreedUsers(prediction.id, callback);
    }

    @Override
    public void getDisagreedUsers(NetworkListCallback<User> callback) {
        networkingManager.getDisagreedUsers(prediction.id, callback);
    }

    @Override
    public void onUserClicked(User user) {
        AnotherUsersProfileFragment fragment = AnotherUsersProfileFragment.newInstance(user.userId);
        pushFragment(fragment);
    }

    @Override
    public void onUserClicked(Integer userId) {
        AnotherUsersProfileFragment fragment = AnotherUsersProfileFragment.newInstance(userId);
        pushFragment(fragment);
    }

    @Override
    public void onComments() {
        adapter = commentAdapter;
        listView.setAdapter(commentAdapter);
        adapter.loadPage(0);
    }

    @Override
    public void onTally() {
        adapter = tallyAdapter;
        listView.setAdapter(tallyAdapter);
    }

    @Override
    public void onSimilar() {
        CategoryFragment fragment = CategoryFragment.newInstance(prediction.tags.get(0));
        pushFragment(fragment);
    }

    @Override
    public void onGroup() {
        if (prediction.groupId != null) {
            GroupPredictionListFragment fragment = GroupPredictionListFragment.newInstance(userManager.getGroupById(prediction.groupId));
            pushFragment(fragment);
        } else if (prediction.contest_id != null) {
            spinner.show();
            networkingManager.getContest(prediction.contest_id, new NetworkCallback<Contest>() {
                @Override
                public void completionHandler(Contest object, ServerError error) {
                    spinner.hide();
                    if (error == null) {
                        ContestDetailFragment fragment = ContestDetailFragment.newInstance(object);
                        pushFragment(fragment);
                    }
                }
            });

        }

    }

    @Override
    public void onShare() {
        if (prediction.hasGroup()) {
            errorReporter.showError("Hold on, this is a private group prediction. You won't be able to share it with the world.");
            return;
        }

        if (userManager.getUser().getTwitterAccount() == null && userManager.getUser().getFacebookAccount() == null) {
            showDefaultShare();
            return;
        }

        List<String> listItems = new ArrayList<String>();


        if (userManager.getUser().getTwitterAccount() != null) {
            listItems.add("Twitter");
        }
        if (userManager.getUser().getFacebookAccount() != null) {
            listItems.add("Facebook");
        }

        listItems.add("Other");

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("How would you like to share?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == items.length - 1) {
                    showDefaultShare();
                } else if (items[i].equals("Twitter")) {
                    twitterShare();
                } else if (items[i].equals("Facebook")) {
                    facebookShare();
                }
            }
        });
        builder.create().show();


    }

    public void twitterShare() {
        spinner.show();
        networkingManager.sharePredictionOnTwitter(prediction, new NetworkCallback<BaseModel>() {
            @Override
            public void completionHandler(BaseModel object, ServerError error) {
                spinner.hide();
            }
        });
    }

    public void facebookShare() {
        spinner.show();
        networkingManager.sharePredictionOnFacebook(prediction, new NetworkCallback<BaseModel>() {
            @Override
            public void completionHandler(BaseModel object, ServerError error) {
                spinner.hide();
            }
        });
    }

    public void showDefaultShare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String suffix = " via @KNODAfuture " + prediction.shortUrl;
        int predictionLength = 139 - suffix.length();
        String text = "";
        if (prediction.body.length() > predictionLength) {
            text = prediction.body.substring(0, predictionLength - 3) + "..." + suffix;
        } else {
            text = prediction.body + suffix;
        }
        share.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(share, "How would you like to share?"));
    }

    @Override
    public void onAgree() {
        spinner.show();

        networkingManager.agreeWithPrediction(prediction.id, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                spinner.hide();
                if (error != null)
                    return;

                prediction = object;
                checkComments(true);
                headerview.setPrediction(prediction);
            }
        });
        guestContest(prediction);
        FlurryAgent.logEvent("Agree_Button_Tapped");
    }

    @Override
    public void onDisagree() {
        spinner.show();

        networkingManager.disagreeWithPrediction(prediction.id, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                spinner.hide();

                if (error != null)
                    return;

                prediction = object;
                checkComments(false);
                headerview.setPrediction(prediction);
            }
        });
        guestContest(prediction);
        FlurryAgent.logEvent("Disagree_Button_Tapped");
    }

    public void checkComments(boolean agree) {
        for (int i = 0; i != commentAdapter.getCount() - 2; i++) {
            Comment comment = commentAdapter.getItem(i);
            if (comment != null && comment.userId == userManager.getUser().id && comment.challenge != null) {
                comment.challenge.agree = agree;
            }
        }
        commentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBS() {
        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .setMessage("Don't be lame. Tell the truth. It's more fun this way. Is this really the wrong outcome?")
                .create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(sendBS(alert));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }

    private View.OnClickListener sendBS(final AlertDialog dialog) {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                spinner.show();

                networkingManager.sendBS(prediction.id, new NetworkCallback<Prediction>() {
                    @Override
                    public void completionHandler(Prediction object, ServerError error) {
                        spinner.hide();

                        if (error != null)
                            return;

                        prediction = object;
                        headerview.setPrediction(prediction);
                    }
                });
                FlurryAgent.logEvent("BS_Button_Tapped");
            }
        };

    }

    @Override
    public void onDestroy() {
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        super.onDestroy();
    }

    @Override
    public void onOutcome(boolean outcome) {
        spinner.show();

        networkingManager.sendOutcome(prediction.id, outcome, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                spinner.hide();
                if (error != null)
                    return;
                prediction = object;
                headerview.setPrediction(prediction);
            }
        });
    }

    @Override
    public void onIDK() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), updateResolutionDate(), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DateTime dt = new DateTime();
        dialog.getDatePicker().setTag(false);
        dialog.getDatePicker().setMinDate(dt.plusDays(1).getMillis());
        dialog.setTitle("When will you know?");
        dialog.show();
        FlurryAgent.logEvent("UNFINISHED_BUTTON_TAPPED");

    }

    private final DatePickerDialog.OnDateSetListener updateResolutionDate() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //prevent from being called twice
                if (datePicker.getTag() != null && (Boolean) datePicker.getTag() == true) {
                    return;
                }
                System.out.println("date changed");
                Prediction update = new Prediction();
                update.id = prediction.id;
                datePicker.setTag(true);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, 12, 0);
                update.resolutionDate = new DateTime(calendar.getTime());
                spinner.show();

                networkingManager.updatePrediction(update, new NetworkCallback<Prediction>() {
                    @Override
                    public void completionHandler(Prediction object, ServerError error) {
                        spinner.hide();
                        if (error != null)
                            errorReporter.showError(error);
                        else {
                            prediction = object;
                            headerview.setPrediction(prediction);
                        }
                    }
                });
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public String noContentString() {
        return "Be the first to comment.";
    }

    private void guestContest(Prediction prediction) {
        if (prediction.contest_id == null)
            return;
        if (userManager.getUser() == null || userManager.getUser().guestMode) {
            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
            b.setTitle("Show me the contest!")
                    .setMessage("This prediction is part of a contest. To be eligible to win, you must be a registered Knoda user. Sign up now for your chance to win!")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) getActivity()).showLogin("Giddy Up!", "Now we're talking! Choose an option below to sign-up and start participating in contests.");
                        }
                    })
                    .show();
        }
    }
}
