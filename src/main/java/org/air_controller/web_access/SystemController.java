package org.air_controller.web_access;

import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class SystemController {

    @Autowired
    private AirControllerService airControllerService;

    @GetMapping("/currentState/freshAir")
    public ResponseEntity<OutputState> getCurrentStateForFreshAir() {
        final Optional<SystemAction> lastAction = airControllerService.getCurrentStateForFreshAir();
        return lastAction
                .map(systemAction -> new ResponseEntity<>(systemAction.outputState(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("/currentState/climateDataPoint")
    public ResponseEntity<ClimateDataPointDTO> getCurrentClimateDataPoint() {
        final Optional<ClimateDataPointDTO> currentClimateDataPoint = airControllerService.getCurrentClimateDataPoint();
        return currentClimateDataPoint
                .map(dataPoint -> new ResponseEntity<>(dataPoint, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("/currentState/total_confidence")
    public ResponseEntity<Double> getCurrentTotalConfidence() {
        final Optional<Double> currentTotalConfidence = airControllerService.getCurrentTotalConfidence();
        return currentTotalConfidence
                .map(confidence -> new ResponseEntity<>(confidence, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
