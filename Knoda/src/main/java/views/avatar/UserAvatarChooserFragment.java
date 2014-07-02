package views.avatar;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.camera.CropImageIntentBuilder;
import com.android.volley.RequestQueue;
import com.knoda.knoda.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.InjectView;
import butterknife.OnClick;
import models.ServerError;
import models.User;
import networking.NetworkCallback;
import unsorted.Logger;
import views.core.BaseDialogFragment;
import views.core.MainActivity;
import views.login.SignupConfirmFragment;

/**
 * Created by nick on 6/11/14.
 */
public class UserAvatarChooserFragment extends BaseDialogFragment {


    public static final int AVATAR_SIZE = 1000;
    public static final int CAMERA_RESULT = 187;
    public static final int CROP_RESULT = 1827323;
    public static final int GALLERY_RESULT = 12312312;
    public static final String FROM_CAMERA_FILENAME = "FROMCAMERA";
    public static final String CROP_RESULT_FILENAME = "CROPRESULT";
    public File cropResultFile;
    public boolean uploadInProgress = false;
    @InjectView(R.id.avatar_imageview)
    ImageView imageView;
    @InjectView(R.id.avatar_done)
    Button doneButton;
    private boolean madeInitialSelection = false;
    private File cameraOutputFile;
    private RequestQueue requestQueue;
    private boolean cancelable;

    @OnClick(R.id.avatar_done)
    void onDone() {
        submit();
    }

    @OnClick(R.id.avatar_gallery_button)
    void onGallery() {
        startGallery();
    }

    @OnClick(R.id.avatar_take_photo_button)
    void onCamera() {
        startCamera();
    }

    @OnClick(R.id.avatar_skip)
    void onSkip() {
        useDefault();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        updateBackground();
        return inflater.inflate(R.layout.fragment_user_avatar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cameraOutputFile = new File(getActivity().getExternalFilesDir(null), FROM_CAMERA_FILENAME);
        cropResultFile = new File(getActivity().getExternalFilesDir(null), CROP_RESULT_FILENAME);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.log("ACTIVITY RESULT");
        if (requestCode == CAMERA_RESULT && resultCode == Activity.RESULT_OK) {
            startCrop(Uri.fromFile(cameraOutputFile));
        } else if (requestCode == CROP_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                showCroppedImage();
            } else {
                cropResultFile = null;
            }
        } else if (requestCode == GALLERY_RESULT && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            startCrop(imageUri);
        }
    }

    public void startCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraOutputFile));
        cameraIntent.putExtra("return-data", true);
        startActivityForResult(cameraIntent, CAMERA_RESULT);
    }

    public void startGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/png");
        startActivityForResult(Intent.createChooser(galleryIntent, "Select a profile picture"), GALLERY_RESULT);
    }


    public void startCrop(Uri sourceUri) {
        CropImageIntentBuilder builder = new CropImageIntentBuilder(AVATAR_SIZE, AVATAR_SIZE, Uri.fromFile(cropResultFile));
        builder.setSourceImage(sourceUri);
        startActivityForResult(builder.getIntent(getActivity()), CROP_RESULT);
    }

    public void showCroppedImage() {
        Bitmap bitmap = BitmapFactory.decodeFile(cropResultFile.getPath());
        imageView.setImageBitmap(bitmap);

        if (bitmap != null)
            doneButton.setVisibility(View.VISIBLE);
        else
            doneButton.setVisibility(View.INVISIBLE);
    }

    protected void useDefault() {
        AssetManager assetManager = getActivity().getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(getRandomOctopus());
            File outFile = new File(getActivity().getExternalFilesDir(null), CROP_RESULT_FILENAME);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (IOException e) {
            Log.e("tag", "Failed to copy asset file: " + CROP_RESULT_FILENAME, e);
        }
        Bitmap bitmap = BitmapFactory.decodeFile(cropResultFile.getPath());
        imageView.setImageBitmap(bitmap);
        submit();

    }

    public void submit() {
        if (uploadInProgress || cropResultFile == null)
            return;

        spinner.show();

        networkingManager.uploadUserAvatar(cropResultFile, new NetworkCallback<User>() {
            @Override
            public void completionHandler(User object, ServerError error) {
                spinner.hide();
                uploadInProgress = false;
                userManager.refreshUser(new NetworkCallback<User>() {
                    @Override
                    public void completionHandler(User object, ServerError error) {
                        if (error != null)
                            errorReporter.showError("Please try again later");
                        finish();
                    }
                });
            }
        });
    }

    public void finish() {
        ((MainActivity) getActivity()).doLogin();
        dismiss();

        SignupConfirmFragment f = SignupConfirmFragment.newInstance();
        f.show(getActivity().getFragmentManager(), "confirm");
    }

    private String getRandomOctopus() {
        int random = 1 + (int) (Math.random() * ((4) + 1));
        return "avatar_" + random + ".png";
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
