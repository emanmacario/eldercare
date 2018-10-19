package au.edu.unimelb.eldercare.helpers;

import android.content.Context;
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


    /*
     * Copyright 2012 Google Inc.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = getCurrentTime();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
}
