package org.air_controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationBuilderTest {

    @Test
    void testWhenCreateApplicationIncompleteThenThrow() {
        assertThrows(IllegalStateException.class, () -> new  ApplicationBuilder().build());
    }
}