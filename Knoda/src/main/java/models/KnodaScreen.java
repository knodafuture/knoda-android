package models;

import android.graphics.drawable.Drawable;

/**
 * Created by nick on 1/13/14.
 */

public class KnodaScreen implements Comparable<KnodaScreen>{

    public enum KnodaScreenOrder {
        HOME, ACTIVITY, GROUP, HISTORY, BADGES, PROFILE
    }

    public KnodaScreenOrder order;
    public String displayName;
    public Drawable drawable;

    public KnodaScreen(KnodaScreenOrder order, String displayName, Drawable drawable) {
        this.order = order;
        this.displayName = displayName;
        this.drawable = drawable;
    }

    @Override
    public int compareTo(KnodaScreen screen) {
        return this.order.ordinal() > screen.order.ordinal() ? +1 : this.order.ordinal() < screen.order.ordinal() ? -1 : 0;
    }
}
