package org.air_controller.sensor.solar_events;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import org.air_controller.sensor.MyPosition;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

// https://github.com/mikereedell/sunrisesunsetlib-java
public class SunriseSunset {

    public SolarEvent solarEventsFrom(ZonedDateTime date) {
        Location location = new Location(MyPosition.latitude(), MyPosition.longitude());
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "UTC");
        Calendar calendar = GregorianCalendar.from(date);

        Calendar sunsetCalendar = calculator.getOfficialSunsetCalendarForDate(calendar);
        ZonedDateTime sunset = sunsetCalendar.toInstant()
                .atZone(ZoneId.of("UTC"));

        Calendar sunriseCalendar = calculator.getOfficialSunriseCalendarForDate(calendar);
        ZonedDateTime sunrise = sunriseCalendar.toInstant()
                .atZone(ZoneId.of("UTC"));

        return new SolarEvent(sunrise, sunset);
    }
}
