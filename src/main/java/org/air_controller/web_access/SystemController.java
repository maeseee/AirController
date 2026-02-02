package org.air_controller.web_access;

import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
import org.air_controller.web_access.card.CardView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class SystemController {

    private final AirControllerService airControllerService;

    public SystemController(AirControllerService airControllerService) {
        this.airControllerService = airControllerService;
    }

    @GetMapping("/currentState/freshAir")
    public ResponseEntity<OutputState> getCurrentStateForFreshAir() {
        final Optional<OutputState> currentOutputState = airControllerService.getCurrentStateForFreshAir()
                .map(SystemAction::outputState);
        return generateResponse(currentOutputState);
    }

    @GetMapping("/cardViews/indoor")
    public ResponseEntity<CardView> getIndoorCardGroup() {
        final CardView currentClimateDataPoint = airControllerService.getIndoorCardGroup();
        return generateResponse(currentClimateDataPoint);
    }

    @GetMapping("/cardViews/outdoor")
    public ResponseEntity<CardView> getOutdoorCardGroup() {
        final CardView currentClimateDataPoint = airControllerService.getOutdoorCardGroup();
        return generateResponse(currentClimateDataPoint);
    }

    @GetMapping("/cardViews/confidence")
    public ResponseEntity<CardView> getConfidenceCardGroup() {
        final CardView currentClimateDataPoint = airControllerService.getConfidenceCardGroup();
        return generateResponse(currentClimateDataPoint);
    }

    @GetMapping("/cardViews/statistics")
    public ResponseEntity<CardView> getStatisticsCardGroup() {
        final CardView currentClimateDataPoint = airControllerService.getStatisticsCardGroup();
        return generateResponse(currentClimateDataPoint);
    }

    private <T> ResponseEntity<T> generateResponse(Optional<T> resultOptional) {
        return resultOptional.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ResponseEntity<CardView> generateResponse(CardView cardView) {
        if (cardView.cards().isEmpty()) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(cardView, HttpStatus.OK);
    }
}
