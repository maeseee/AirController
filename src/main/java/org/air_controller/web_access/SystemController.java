package org.air_controller.web_access;

import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class SystemController {

    @Autowired
    private AirControllerService airControllerService;

    @GetMapping("/currentState/freshAir")
    public ResponseEntity<OutputState> getCurrentStateForFreshAir() {
        final Optional<OutputState> currentOutputState = airControllerService.getCurrentStateForFreshAir()
                .map(SystemAction::outputState);
        return generateResponse(currentOutputState);
    }

    @GetMapping("/currentState/indoorClimateDataPoint")
    public ResponseEntity<ClimateDataPointDTO> getCurrentIndoorClimateDataPoint() {
        final Optional<ClimateDataPointDTO> currentClimateDataPoint = airControllerService.getCurrentIndoorClimateDataPoint();
        return generateResponse(currentClimateDataPoint);
    }

    @GetMapping("/currentState/total_confidence")
    public ResponseEntity<Double> getCurrentTotalConfidence() {
        final Optional<Double> currentTotalConfidence = airControllerService.getCurrentTotalConfidence();
        return generateResponse(currentTotalConfidence);
    }

    @GetMapping("/currentState/confidences")
    public ResponseEntity<Map<String, Double>> getCurrentConfidences() {
        final Optional<Map<String, Double>> currentConfidences = airControllerService.getCurrentConfidences();
        return generateResponse(currentConfidences);
    }

    private <T> ResponseEntity<T> generateResponse(Optional<T> resultOptional) {
        return resultOptional.map(result -> new  ResponseEntity<>(result, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
