package org.air_controller.web_access.card_view;

import org.air_controller.sensor.solar_events.SolarEvent;
import org.air_controller.sensor.solar_events.SunriseSunset;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SolarEventsService implements CardViewService {

    private final SunriseSunset sunriseSunset = new SunriseSunset();

    @Override
    public CardView getCardView() {
        final LocalDate today = LocalDate.now();
        final SolarEvent solarEvents = sunriseSunset.solarEventsFrom(today);
        final List<CardItem> cardItems = solarEvents.getCardItems();
        return new CardView("", cardItems);
    }
}
