package helpers.blur;

/**
 * Created by nick on 6/18/14.
 */

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;


/**
 * Simple example of ScriptIntrinsicBlur Renderscript gaussion blur.
 * In production always use this algorithm as it is the fastest on Android.
 */
public class RenderScriptGaussianBlur {
    private RenderScript rs;

    public RenderScriptGaussianBlur(RenderScript rs) {
        this.rs = rs;
    }

    public Bitmap blur(int radius, Bitmap bitmapOriginal) {
        final Allocation input = Allocation.createFromBitmap(rs, bitmapOriginal);
        final Allocation output = Allocation.createTyped(rs, input.getType());

        Bitmap outputBitmap = Bitmap.createBitmap(bitmapOriginal.getWidth(), bitmapOriginal.getHeight(),
                Bitmap.Config.ARGB_8888);

        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //script.setRadius(radius);
        script.setRadius(25.f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(outputBitmap);
        bitmapOriginal.recycle();
        return outputBitmap;
    }
}