package org.air_controller.web_access.card_view;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardViewController {

    private final CardViewService service;

    public CardViewController(CardViewService service) {
        this.service = service;
    }

    @GetMapping("/cardViews/indoor")
    public ResponseEntity<CardView> getIndoorCardGroup() {
        final CardView currentClimateDataPoint = service.getIndoorCardView();
        return generateResponse(currentClimateDataPoint);
    }

    @GetMapping("/cardViews/outdoor")
    public ResponseEntity<CardView> getOutdoorCardGroup() {
        final CardView currentClimateDataPoint = service.getOutdoorCardView();
        return generateResponse(currentClimateDataPoint);
    }

    @GetMapping("/cardViews/confidence")
    public ResponseEntity<CardView> getConfidenceCardGroup() {
        final CardView currentClimateDataPoint = service.getConfidenceCardView();
        return generateResponse(currentClimateDataPoint);
    }

    @GetMapping("/cardViews/statistics")
    public ResponseEntity<CardView> getStatisticsCardGroup() {
        final CardView currentClimateDataPoint = service.getStatisticsCardView();
        return generateResponse(currentClimateDataPoint);
    }


    private ResponseEntity<CardView> generateResponse(CardView cardView) {
        if (cardView.cards().isEmpty()) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(cardView, HttpStatus.OK);
    }
}
