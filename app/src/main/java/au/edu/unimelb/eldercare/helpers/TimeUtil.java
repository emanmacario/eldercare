package au.edu.unimelb.eldercare.helpers;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import java.util.Date;
import java.util.Locale;

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
     * @return current time
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * Returns a formatted string displaying the total number of
     * minutes and seconds, given a total duration in seconds.
     * @param totalSeconds the total number of seconds elapsed
     * @return formatted time string
     */
    public static String formatTimeDuration(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
