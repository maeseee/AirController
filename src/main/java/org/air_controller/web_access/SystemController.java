package org.air_controller.web_access;

import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
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
    public ResponseEntity<CardGroup> getIndoorCardGroup() {
        final CardGroup currentClimateDataPoint = airControllerService.getIndoorCardGroup();
        return generateResponse(currentClimateDataPoint);
    }

    @GetMapping("/cardViews/outdoor")
    public ResponseEntity<CardGroup> getOutdoorCardGroup() {
        final CardGroup currentClimateDataPoint = airControllerService.getOutdoorCardGroup();
        return generateResponse(currentClimateDataPoint);
    }

    @GetMapping("/cardViews/confidence")
    public ResponseEntity<CardGroup> getConfidenceCardGroup() {
        final CardGroup currentClimateDataPoint = airControllerService.getConfidenceCardGroup();
        return generateResponse(currentClimateDataPoint);
    }

    @GetMapping("/statistics/onPercentageFromTheLast24Hours")
    @Deprecated
    public ResponseEntity<Double> getOnPercentageFromTheLast24Hours() {
        final double onPercentage = airControllerService.getOnPercentageFromTheLast24Hours();
        return new ResponseEntity<>(onPercentage, HttpStatus.OK);
    }

    @GetMapping("/cardViews/statistics")
    public ResponseEntity<CardGroup> getStatisticsCardGroup() {
        final CardGroup currentClimateDataPoint = airControllerService.getStatisticsCardGroup();
        return generateResponse(currentClimateDataPoint);
    }

    private <T> ResponseEntity<T> generateResponse(Optional<T> resultOptional) {
        return resultOptional.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ResponseEntity<CardGroup> generateResponse(CardGroup cardGroup) {
        if (cardGroup.cards().isEmpty()) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(cardGroup, HttpStatus.OK);
    }
}
