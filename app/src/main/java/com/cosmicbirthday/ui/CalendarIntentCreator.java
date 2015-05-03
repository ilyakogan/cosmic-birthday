package com.cosmicbirthday.ui;

import android.content.Intent;
import android.provider.CalendarContract;

import org.joda.time.DateTime;

public class CalendarIntentCreator {
    public Intent createIntentToInsertAllDayEvent(String title, DateTime time) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.ALL_DAY, 1)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time.getMillis())
                //.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, time.getMillis())
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        return intent;
    }
}
