package com.example.restaurantemployerapplication.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RfcToCalendarConverter {
    public static Calendar convert(String rfcString) throws ParseException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;

            OffsetDateTime offsetDateTime = OffsetDateTime.parse(rfcString, timeFormatter);

            Date date = Date.from(Instant.from(offsetDateTime));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } else {
            return toCalendar(rfcString);
        }
    }

    /**
     * Transform ISO 8601 string to Calendar.
     */
    private static Calendar toCalendar(final String iso8601string)
            throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        String replacedTimezoneString = iso8601string.replace("Z", "+00:00");
        String stringWithoutColon;
        try {
            stringWithoutColon = replacedTimezoneString.substring(0, 22) + replacedTimezoneString.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(stringWithoutColon);
        } catch (ParseException e) {
            try {
                stringWithoutColon = replacedTimezoneString.substring(0, 25) + replacedTimezoneString.substring(26);  // to get rid of the ":"
                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(stringWithoutColon);
            } catch (Exception ex) {
                throw ex;
            }
        }

        calendar.setTime(date);
        return calendar;
    }

    /**
     * Transform Calendar to ISO 8601 string.
     */
    private static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /**
     * Get current date and time formatted as ISO 8601 string.
     */
    public static String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }
}
