package com.example.restaurantemployerapplication;

import com.example.restaurantemployerapplication.services.RfcToCalendarConverter;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class RfcConverterTest {
    @Test
    public void Should_ReturnCalendar_When_StringRfcCorrect() {
        // Given
        String rfcString = "2020-05-19T16:00:00Z";
        long rfcTimestamp = 1589904000;

        // When
        Calendar calendar = RfcToCalendarConverter.convert(rfcString);

        // Then
        assertEquals(rfcTimestamp, calendar.toInstant().getEpochSecond());
    }
}
