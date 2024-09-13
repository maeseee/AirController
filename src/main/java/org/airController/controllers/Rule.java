package org.airController.controllers;

import org.airController.rules.Percentage;

public interface Rule {
    String name();

    Percentage turnOn();
}
