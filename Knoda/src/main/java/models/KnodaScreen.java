package models;

import android.graphics.drawable.Drawable;

/**
 * Created by nick on 1/13/14.
 */

public class KnodaScreen implements Comparable<KnodaScreen>{

    public Integer order;
    public String displayName;
    public Drawable drawable;

    public KnodaScreen(Integer order, String displayName, Drawable drawable) {
        this.order = order;
        this.displayName = displayName;
        this.drawable = drawable;
    }

    @Override
    public int compareTo(KnodaScreen screen) {
        return this.order.compareTo(screen.order);
    }
}
