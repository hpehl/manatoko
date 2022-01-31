package org.jboss.hal.manatoko.noop;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.hal.manatoko.test.ManatokoTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class NoopDroneTest extends ManatokoTest {

    private static final Logger logger = LoggerFactory.getLogger(NoopDroneTest.class);

    @Drone WebDriver browser;

    @Test
    void noop() {
        assertNotNull(browser);
        logger.info("All systems up and running. Web driver current URL: {}", browser.getCurrentUrl());
    }
}
