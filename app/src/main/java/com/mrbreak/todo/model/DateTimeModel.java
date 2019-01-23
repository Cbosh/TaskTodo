package com.mrbreak.todo.model;

import android.text.format.DateUtils;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeModel {

    public static String convertFromEpochTime(long timeLong, boolean isCreate) {
        long timeNow = System.currentTimeMillis();

        // get day in relative time
        CharSequence timeDayRelative;
        timeDayRelative = DateUtils.getRelativeTimeSpanString(timeLong, timeNow, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);

        // get hour in 24 hour time
        Format hourFormatter = new SimpleDateFormat("HH:mm");
        String timeHour = hourFormatter.format(timeLong);

        //+ " at " + timeHour;
        return "Created " + timeDayRelative;
    }

    public static int getDaysDifference(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null) {
            return 0;
        }

        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }
}
