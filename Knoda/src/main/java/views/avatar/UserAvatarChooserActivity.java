package views.avatar;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import models.ServerError;
import models.User;
import networking.NetworkCallback;

public class UserAvatarChooserActivity extends AvatarChooserActivity {
    @Override
    public void submit() {
        if (uploadInProgress)
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
                        User u = userManager.getUser();
                        if (error != null)
                            errorReporter.showError("Please try again later");
                        finishAndReturnResult();
                    }
                });
            }
        });
    }

    @Override
    protected void useDefault() {
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(getRandomOctopus());
            File outFile = new File(getExternalFilesDir(null), CROP_RESULT_FILENAME);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + CROP_RESULT_FILENAME, e);
        }
        showCroppedImage();
    }

    private String getRandomOctopus() {
        int random = 1 + (int)(Math.random() * ((4) + 1));
        return "avatar_" + random + ".png";
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}


