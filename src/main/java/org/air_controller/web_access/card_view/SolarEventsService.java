package org.air_controller.web_access.card_view;

import org.air_controller.sensor.moon_phases.MoonPhaseCalculator;
import org.air_controller.sensor.solar_events.SolarEvent;
import org.air_controller.sensor.solar_events.SunriseSunset;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
public class SolarEventsService implements CardViewService {

    private final SunriseSunset sunriseSunset = new SunriseSunset();
    private final MoonPhaseCalculator moonPhaseCalculator = new MoonPhaseCalculator();

    @Override
    public CardView getCardView() {
        final LocalDate today = LocalDate.now();
        final SolarEvent solarEvents = sunriseSunset.solarEventsFrom(today);
        final List<CardItem> cardItems = Stream
                .concat(solarEvents.getCardItems().stream(), Stream.of(moonPhaseCalculator.nextFullMoonCardItem()))
                .toList();
        return new CardView("", cardItems);
    }
}
