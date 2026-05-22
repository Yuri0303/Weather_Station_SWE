package org.example.domainmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertRuleTest {
    private AlertRule alertRule;

    @BeforeEach
    void setUp() {
        alertRule = new AlertRule(0, 0, 15f, 50f, SensorType.TEMPERATURE);
    }

    @Test
    void isViolatedBy_lowerBoundViolated() {
        Measurement measurement = new Measurement(0, 0, 14f, null);
        assertTrue(alertRule.isViolatedBy(measurement));
    }

    @Test
    void isViolatedBy_upperBoundViolated() {
        Measurement measurement = new Measurement(0, 0, 51f, null);
        assertTrue(alertRule.isViolatedBy(measurement));
    }

    @Test
    void isViolatedBy_valueInRange() {
        Measurement measurement = new Measurement(0, 0, 35f, null);
        assertFalse(alertRule.isViolatedBy(measurement));
    }

    @Test
    void isViolatedBy_valueEqualsLowerBound() {
        Measurement measurement = new Measurement(0, 0, 15f, null);
        assertFalse(alertRule.isViolatedBy(measurement));
    }

    @Test
    void isViolatedBy_valueEqualsUpperBound() {
        Measurement measurement = new Measurement(0, 0, 50f, null);
        assertFalse(alertRule.isViolatedBy(measurement));
    }
}