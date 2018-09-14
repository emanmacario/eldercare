package au.edu.unimelb.eldercare.helpers;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import java.util.Date;

/**
 * Utility class for helping with time and
 * date conversions and time representations
 */
public class TimeUtil {

    /**
     * Returns a string formatted to display the current local time
     * @param time current UNIX time in seconds
     * @return timeString
     */
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

    /**
     * Returns the current UNIX time in seconds
     * @return
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis() / 1000L;
    }
}
