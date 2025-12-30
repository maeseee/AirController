package org.air_controller.web_access;

import org.air_controller.persistence.MariaDatabase;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemPart;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AirControllerService {

    private final SystemActionDbAccessor airFlowDbAccessor = new SystemActionDbAccessor(new MariaDatabase(), SystemPart.AIR_FLOW);

    public Optional<SystemAction> getCurrentStateForFreshAir() {
        return airFlowDbAccessor.getMostCurrentState();
    }
}
