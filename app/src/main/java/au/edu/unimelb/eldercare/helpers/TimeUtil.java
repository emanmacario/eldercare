package au.edu.unimelb.eldercare.helpers;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import java.util.Date;

/**
 * Utility class for helping with time conversions
 */
public class TimeUtil {

    public static String createTimeString(long time) {

        time *= 1000L;
        Date date = new Date(time);

        String timeString;
        if (DateUtils.isToday(time)) {
            timeString = "Today\n" + DateFormat.format("h:mm a", date).toString();
        } else {
            timeString = DateFormat.format("dd/MM/yy\nh:mm a", date).toString();
        }
        return timeString;
    }
}
