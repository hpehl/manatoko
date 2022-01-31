package org.jboss.hal.manatoko.test;

import org.jboss.hal.manatoko.container.HalContainer;
import org.jboss.hal.manatoko.container.WildFlyContainer;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jboss.hal.manatoko.container.WildFlyVersion._26;

@Testcontainers
public class WildFlyTest extends ManatokoTest {

    @Container
    protected static WildFlyContainer wildFly = WildFlyContainer.version(_26);

    @BeforeAll
    static void setupWildFly() {
        if (HalContainer.instance() != null) {
            HalContainer.instance().connectTo(wildFly);
        }
    }
}
