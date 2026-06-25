package org.air_controller.sensor.solar_events;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import org.air_controller.sensor.MyPosition;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

// https://github.com/mikereedell/sunrisesunsetlib-java
public class SunriseSunset {

    public SolarEvent solarEventsFrom(LocalDate date) {
        final Location location = new Location(MyPosition.latitude(), MyPosition.longitude());
        final SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "UTC");
        final Calendar calendarDate = toCalendar(date);
        final ZonedDateTime sunset = toZonedDate(calculator.getOfficialSunsetCalendarForDate(calendarDate));
        final ZonedDateTime sunrise = toZonedDate(calculator.getOfficialSunriseCalendarForDate(calendarDate));
        return new SolarEvent(sunrise, sunset);
    }

    private Calendar toCalendar(LocalDate date) {
        final ZonedDateTime zonedDate = date.atTime(12, 0)
                .atZone(ZoneOffset.UTC);
        return GregorianCalendar.from(zonedDate);
    }

    private ZonedDateTime toZonedDate(Calendar eventTime) {
        return eventTime.toInstant().atZone(ZoneOffset.UTC);
    }
}
