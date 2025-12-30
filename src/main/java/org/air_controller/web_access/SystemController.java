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

    private final AirControllerRepository airControllerRepository = new AirControllerRepository();

    @GetMapping("/currentState/freshAir")
    public ResponseEntity<OutputState> getCurrentStateForFreshAir() {
        final Optional<SystemAction> lastAction = airControllerRepository.getCurrentStateForFreshAir();
        return lastAction
                .map(systemAction -> new ResponseEntity<>(systemAction.outputState(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
