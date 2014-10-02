package views.avatar;

import android.content.Intent;
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
import android.widget.ImageView;

import com.android.camera.CropImageIntentBuilder;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.knoda.knoda.R;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import views.core.BaseActivity;

public abstract class AvatarChooserActivity extends BaseActivity {

    public static final int AVATAR_SIZE = 1000;
    public static final int CAMERA_RESULT = 187;
    public static final int CROP_RESULT = 1827323;
    public static final int GALLERY_RESULT = 12312312;
    public static final String FROM_CAMERA_FILENAME = "FROMCAMERA";
    public static final String CROP_RESULT_FILENAME = "CROPRESULT";
    public File cropResultFile;
    public boolean uploadInProgress = false;
    @InjectView(R.id.photo_chooser_imageview)
    ImageView imageView;
    private boolean madeInitialSelection = false;
    private File cameraOutputFile;
    private RequestQueue requestQueue;
    private boolean cancelable;
    private boolean shownWindow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_chooser);
        requestQueue = Volley.newRequestQueue(this);

        //updateBackground();

        ButterKnife.inject(this);
        restoreActionBar();

        registerForContextMenu(imageView);

        cameraOutputFile = new File(getExternalFilesDir(null), FROM_CAMERA_FILENAME);
        cropResultFile = new File(getExternalFilesDir(null), CROP_RESULT_FILENAME);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContextMenu(imageView);
            }
        });


        Bundle extras = getIntent().getExtras();
        cancelable = extras != null && extras.getBoolean("cancelable");
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (hasFocus && !madeInitialSelection && !shownWindow) {
            shownWindow = true;
            openContextMenu(imageView);
        }

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
        menu.findItem(R.id.action_cancel).setVisible(cancelable);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_from_camera:
                startCamera();
                break;
            case R.id.action_none:
                useDefault();
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
        if (cancelable)
            super.onBackPressed();
    }


    public abstract void submit();

    public void finishAndReturnResult() {
        Intent intent = getIntent();
        if (cropResultFile == null)
            setResult(RESULT_CANCELED, intent);
        else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropResultFile.getPath());
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    public void finishAndReturnDefaultResult() {
        Intent intent = getIntent();
        if (cropResultFile == null)
            setResult(RESULT_CANCELED, intent);
        else
            setResult(RESULT_OK, intent);
        finish();
    }

    private void restoreActionBar() {
        //getActionBar().setHomeButtonEnabled(false);
        //getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {
            shownWindow = true;
            startCrop(Uri.fromFile(cameraOutputFile));
        } else if (requestCode == CROP_RESULT) {
            shownWindow = true;
            if (resultCode == RESULT_OK) {
                if (showFinalCropped()) {
                    showCroppedImage();
                } else {
                    submit();
                }
            } else {
                cropResultFile = null;
                openContextMenu(imageView);
            }
        } else if (requestCode == GALLERY_RESULT && resultCode == RESULT_OK) {
            shownWindow = true;
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

//    public void updateBackground() {
//        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.container);
//
//        if (relativeLayout == null) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    updateBackground();
//                }
//            }, 10);
//        } else {
//            relativeLayout.post(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Display display = getWindowManager().getDefaultDisplay();
//                        DisplayMetrics displayMetrics = new DisplayMetrics();
//                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                        Point size = new Point();
//                        display.getSize(size);
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                        Bitmap bitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
//                        bitmap.eraseColor(getResources().getColor(R.color.knodaLightGreenTransparent));
//
//                        RenderScriptGaussianBlur blur = new RenderScriptGaussianBlur(RenderScript.create(relativeLayout.getContext()));
//                        bitmap = blur.blur(15, bitmap);
//                        BitmapDrawable d = new BitmapDrawable(getResources(), Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight()));
//
//                        relativeLayout.setBackgroundDrawable(d);
//                    } catch (Exception e) {
//                        Log.e("Knoda Error", e.getMessage());
//                    }
//                }
//            });
//        }
//    }

    protected abstract void useDefault();

    protected boolean showFinalCropped() {
        return true;
    }


}
