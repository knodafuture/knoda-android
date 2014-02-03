package helpers;

import android.view.View;

/**
 * Created by nick on 2/3/14.
 */
public class AdapterHelper {

    public static View getConvertViewSafely(View convertView, Class clazz) {
        if (convertView == null || convertView.getClass() != clazz)
            return null;

        return convertView;

    }

}
