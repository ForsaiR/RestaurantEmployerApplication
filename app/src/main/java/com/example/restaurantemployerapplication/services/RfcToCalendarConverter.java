package com.example.restaurantemployerapplication.services;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class RfcToCalendarConverter {
    public static Calendar convert(String rfcString) {
        Instant instant = Instant.parse(rfcString);
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        return GregorianCalendar.from(zdt);
    }
}
