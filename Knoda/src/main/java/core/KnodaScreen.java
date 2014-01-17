package core;

import java.util.HashMap;

/**
 * Created by nick on 1/13/14.
 */
public enum KnodaScreen {
    HOME(0);

    private final Integer value;
    private static final HashMap<Integer, KnodaScreen> mMap = new HashMap<Integer, KnodaScreen>();

    static  {
        mMap.put(0, HOME);
    }

    private KnodaScreen(final int newValue) {
        value = newValue;
    }

    public Integer getValue() {
        return value;
    }

    public static KnodaScreen get(Integer index) {
        return mMap.get(index);
    }
}
