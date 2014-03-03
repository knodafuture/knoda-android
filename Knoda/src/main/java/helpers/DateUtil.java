package helpers;

import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * Created by nick on 2/1/14.
 */
public class DateUtil {

    public static String getPeriodString(DateTime date) {
        DateTime now = new DateTime();

        Period period;
        if (date.isBeforeNow())
            period = new Period(date, now);
        else
            period = new Period(now, date);

        if (period.getYears() != 0) {
            String suffix = period.getYears() > 1 ? "yrs" : "yr";
            return period.getYears() + suffix;
        } else if (period.getMonths() != 0)
            return period.getMonths() + "mo";
        else if (period.getDays() != 0 || period.getWeeks() != 0) {
            int days = period.getWeeks() != 0 ? period.getWeeks() * 7 + period.getDays(): period.getDays();
            return days + "d";
        }
        else if (period.getHours() != 0)
            return period.getHours() + "h";
        else if (period.getMinutes() != 0)
            return period.getMinutes() + "m";
        else
            return period.getSeconds() + "s";

    }
}
