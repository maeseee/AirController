package org.air_controller.rules;

public interface Rule {
    String name();

    Confidence turnOnConfidence();
}
