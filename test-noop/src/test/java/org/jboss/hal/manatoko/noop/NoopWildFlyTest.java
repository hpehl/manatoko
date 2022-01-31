package org.jboss.hal.manatoko.noop;

import java.io.IOException;

import javax.inject.Inject;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.hal.manatoko.test.WildFlyTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ArquillianExtension.class)
class NoopWildFlyTest extends WildFlyTest {

    private static final Logger logger = LoggerFactory.getLogger(NoopWildFlyTest.class);

    @Test
    void noop() {
        assertNotNull(wildFly.managementClient());
        logger.info("All systems up and running. Management client: {}", wildFly.managementClient());
    }
}
