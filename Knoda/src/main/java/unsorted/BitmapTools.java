package unsorted;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ThumbnailUtils;

public class BitmapTools {


    public static Bitmap getclip(Bitmap bitmap) {
        return getclipSized(bitmap, bitmap.getWidth(), bitmap.getHeight());
    }

    public static Bitmap getclipSized(Bitmap bitmap, int width, int height) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap output2 = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Canvas canvas2 = new Canvas(output2);

        final Paint paint = new Paint();
        final Paint paint2 = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());


        paint.setAntiAlias(true);
        paint2.setAntiAlias(true);

        paint.setColor(Color.parseColor("#EFEFEF"));
        canvas.drawBitmap(output, rect, rect, paint);
        canvas.drawCircle((bitmap.getWidth() / 2), (bitmap.getHeight() / 2),
                (bitmap.getWidth() / 2), paint);


        canvas2.drawARGB(0, 0, 0, 0);
        canvas2.drawCircle((bitmap.getWidth() / 2), (bitmap.getHeight() / 2),
                (bitmap.getWidth() / 2) - 2, paint2);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas2.drawBitmap(bitmap, 2,2, paint2);

        return ThumbnailUtils.extractThumbnail(overlay(output,output2), width, height);
    }

    private static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }
}