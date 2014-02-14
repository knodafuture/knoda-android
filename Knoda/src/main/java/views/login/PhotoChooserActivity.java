package views.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.camera.CropImageIntentBuilder;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.knoda.knoda.R;

import org.apache.http.entity.ContentType;

import java.io.File;

import javax.inject.Inject;

import builders.MultipartRequestBuilder;
import butterknife.ButterKnife;
import butterknife.InjectView;
import managers.NetworkingManager;
import managers.UserManager;
import models.User;
import networking.MultipartRequest;
import unsorted.Logger;
import views.core.BaseActivity;

public class PhotoChooserActivity extends BaseActivity {

    @InjectView(R.id.photo_chooser_imageview) ImageView imageView;
    @InjectView(R.id.photo_chooser_progress_view) FrameLayout progressView;

    @Inject
    public UserManager userManager;

    private boolean madeInitialSelection = false;


    private static final int AVATAR_SIZE = 1000;
    private static final int CAMERA_RESULT = 187;
    private static final int CROP_RESULT = 1827323;
    private static final int GALLERY_RESULT = 12312312;

    private static final String FROM_CAMERA_FILENAME = "FROMCAMERA";
    private static final String CROP_RESULT_FILENAME = "CROPRESULT";

    private File cameraOutputFile;
    private File cropResultFile;

    private RequestQueue requestQueue;

    private boolean uploadInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_chooser);
        requestQueue = Volley.newRequestQueue(this);

        restoreActionBar();

        ButterKnife.inject(this);

        registerForContextMenu(imageView);

        cameraOutputFile = new File(getExternalFilesDir(null), FROM_CAMERA_FILENAME);
        cropResultFile = new File(getExternalFilesDir(null), CROP_RESULT_FILENAME);

        progressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (hasFocus && !madeInitialSelection)
            openContextMenu(imageView);

        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submit, menu);
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.action_submit)
            return super.onOptionsItemSelected(item);

        submit();
        return true;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_picture, menu);
        menu.setHeaderTitle("Select a profile picture");
        Bundle extras = getIntent().getExtras();
        if(extras == null || !extras.getBoolean("change_picture"))
        {
            menu.findItem(R.id.action_cancel).setVisible(false);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_from_camera:
                startCamera();
                break;
            case R.id.action_none:
                break;
            case R.id.action_from_gallery:
                startGallery();
                break;
            case R.id.action_cancel:
                finish();
                break;
        }

        madeInitialSelection = true;
        return true;
    }

    @Override
    public void onBackPressed() {

    }

    private void showSpinner() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        progressView.setVisibility(View.VISIBLE);
        progressView.setAnimation(fadeIn);
    }

    private void submit() {

        if (uploadInProgress)
            return;


        showSpinner();

        String url = NetworkingManager.baseUrl;
        url += "profile.json?auth_token=" + getAuthToken();

        MultipartRequestBuilder builder = MultipartRequestBuilder.create().forUrl(url);
        builder.addErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showError();
            }
        });

        builder.addListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                User u = new Gson().fromJson(s, User.class);
                userManager.user = u;
                finish();
            }
        });


        builder.addFilePart("user[avatar]", cropResultFile, ContentType.APPLICATION_OCTET_STREAM, "image.jpg");

        MultipartRequest req = builder.build();
        Logger.log(req.toString());
        requestQueue.add(builder.build());
    }

    private void showError() {
        progressView.setAlpha(0);
        progressView.setVisibility(View.INVISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please try again later.").setPositiveButton("Ok", null);
        builder.create().show();
    }

    private String getAuthToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        return sharedPreferences.getString("SAVEDAUTHTOKEN", null);
    }

    private void restoreActionBar() {
        getActionBar().setHomeButtonEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {
            startCrop(Uri.fromFile(cameraOutputFile));
        } else if (requestCode == CROP_RESULT && resultCode == RESULT_OK) {
            showCroppedImage();
        } else if (requestCode == GALLERY_RESULT && resultCode == RESULT_OK) {
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
        startActivityForResult(builder.getIntent(this), CROP_RESULT);
    }

    public void showCroppedImage() {
        Bitmap bitmap = BitmapFactory.decodeFile(cropResultFile.getPath());
        imageView.setImageBitmap(bitmap);
    }
}


