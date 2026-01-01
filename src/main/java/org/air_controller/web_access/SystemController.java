package org.air_controller.web_access;

import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

// TODO make more restrict
@CrossOrigin(origins = "*")
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
}
