package com.example.restaurantemployerapplication.data.model;

import com.example.restaurantemployerapplication.services.RfcToCalendarConverter;
import com.tamagotchi.tamagotchiserverprotocol.models.VisitTime;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

public class FullVisitTime {
    private Calendar start;
    private Calendar end;

    public FullVisitTime(VisitTime visitTime) {
        this(visitTime.getStart(), visitTime.getEnd());
    }

    private FullVisitTime(String start, String end) {
        try {
            this.start = RfcToCalendarConverter.convert(start);
            this.end = RfcToCalendarConverter.convert(end);
        } catch (ParseException e) {
            this.start = null;
            this.end = null;
        }
    }

    public FullVisitTime(Calendar start, Calendar end) {
        this.start = start;
        this.end = end;
    }

    public Calendar getStart() {
        return start;
    }

    public Calendar getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FullVisitTime that = (FullVisitTime) o;
        return Objects.equals(start, that.start) &&
                Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), start, end);
    }
}
