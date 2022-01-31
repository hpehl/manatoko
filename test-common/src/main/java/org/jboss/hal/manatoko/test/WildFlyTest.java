package org.jboss.hal.manatoko.test;

import org.jboss.hal.manatoko.container.HalContainer;
import org.jboss.hal.manatoko.container.WildFlyConfiguration;
import org.jboss.hal.manatoko.container.WildFlyContainer;
import org.jboss.hal.manatoko.container.WildFlyVersion;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class WildFlyTest extends ManatokoTest {

    @Container
    protected static WildFlyContainer wildFly = WildFlyContainer
            .version(WildFlyVersion._26, WildFlyConfiguration.STANDALONE);

    @BeforeAll
    static void setupWildFly() {
        if (HalContainer.instance() != null) {
            HalContainer.instance().connectTo(wildFly);
        }
    }
}
