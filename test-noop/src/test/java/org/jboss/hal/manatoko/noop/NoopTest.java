package org.jboss.hal.manatoko.noop;

import org.jboss.hal.manatoko.test.ManatokoTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NoopTest extends ManatokoTest {

    private static final Logger logger = LoggerFactory.getLogger(NoopTest.class);

    @Test
    void noop() {
        assertTrue(true);
        logger.info("All systems up and running.");
    }
}
