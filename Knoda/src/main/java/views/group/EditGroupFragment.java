package views.group;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import factories.GsonF;
import models.Group;
import models.ServerError;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.GroupChangedEvent;
import views.avatar.GroupAvatarChooserActivity;
import views.core.BaseFragment;

/**
 * A fragment with a Google +1 button.
 * Use the {@link EditGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class EditGroupFragment extends BaseFragment {

    private Group group;
    @InjectView(R.id.add_group_name_edittext)
    EditText nameEditText;

    @InjectView(R.id.add_group_description_edittext)
    EditText descriptionEditText;

    @InjectView(R.id.add_group_avatar_imageview)
    NetworkImageView avatarImageView;

    private File avatarFile;
    private static final int PHOTO_RESULT_CODE = 123123129;

    @OnClick(R.id.add_group_avatar_imageview) void onClickAvatar() {
        avatarImageView.setEnabled(false);
        Intent intent = new Intent(getActivity(), GroupAvatarChooserActivity.class);
        intent.putExtra("cancelable", true);
        startActivityForResult(intent, PHOTO_RESULT_CODE);
    }

    public static EditGroupFragment newInstance(Group group) {
        EditGroupFragment fragment = new EditGroupFragment();
        Bundle args = new Bundle();
        args.putString("GROUP", GsonF.actory().toJson(group));
        fragment.setArguments(args);
        return fragment;
    }
    public EditGroupFragment() {}
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.submit, menu);
        menu.removeGroup(R.id.default_menu_group);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_submit)
            submitGroup();

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        group = GsonF.actory().fromJson(getArguments().getString("GROUP"), Group.class);
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }
    private boolean validate() {
        String errorMessage = null;
        if (nameEditText.getText().length() == 0)
            errorMessage = "Please enter a name.";
        else if (descriptionEditText.getText().length() == 0)
            errorMessage = "Please select a description.";
        if (errorMessage != null) {
            errorReporter.showError(errorMessage);
            return false;
        }
        return true;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_group, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("EDIT GROUP");

        if (group.avatar != null && group.avatar.small != null)
            avatarImageView.setImageUrl(group.avatar.small, networkingManager.getImageLoader());
        else
            avatarImageView.setImageResource(R.drawable.groups_avatar_default);
        nameEditText.setText(group.name);
        descriptionEditText.setText(group.description);
    }

    private void submitGroup() {

        hideKeyboard();
        if (!validate())
            return;
        group.name = nameEditText.getText().toString();
        group.description = descriptionEditText.getText().toString();

        spinner.show();

        networkingManager.updateGroup(group, new NetworkCallback<Group>() {
            @Override
            public void completionHandler(Group object, ServerError error) {
                if (avatarFile != null) {
                    networkingManager.uploadGroupAvatar(object.id, avatarFile, new NetworkCallback<Group>() {
                        @Override
                        public void completionHandler(Group group, ServerError error) {
                            if (error != null) {
                                errorReporter.showError(error);
                                spinner.hide();
                            } else {
                                finish(group);
                            }
                        }
                    });
                } else {
                    finish(object);
                }
            }
        });
    }

    private void finish(final Group group) {
        userManager.refreshGroups(new NetworkListCallback<Group>() {
            @Override
            public void completionHandler(ArrayList<Group> object, ServerError error) {
                bus.post(new GroupChangedEvent(group));
                popFragment();
                spinner.hide();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        avatarImageView.setEnabled(true);
        if (data == null) {
            return;
        }
        if (data.getExtras().containsKey(MediaStore.EXTRA_OUTPUT)) {
            String avatarPath = data.getExtras().getString(MediaStore.EXTRA_OUTPUT);
            avatarFile = new File(avatarPath);
            Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
            avatarImageView.setImageBitmap(bitmap);
        }
    }

}
