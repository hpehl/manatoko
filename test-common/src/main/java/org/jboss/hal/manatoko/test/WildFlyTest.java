/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.hal.manatoko.test;

import org.jboss.hal.manatoko.container.HalContainer;
import org.jboss.hal.manatoko.container.WildFlyConfiguration;
import org.jboss.hal.manatoko.container.WildFlyContainer;
import org.jboss.hal.manatoko.container.WildFlyVersion;
import org.jboss.hal.manatoko.environment.Environment;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.hal.dmr.ModelDescriptionConstants.ALLOWED_ORIGINS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.LIST_ADD_OPERATION;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.VALUE;

@Testcontainers
public class WildFlyTest extends ManatokoTest {

    private static final Logger logger = LoggerFactory.getLogger(WildFlyTest.class);

    @Container protected static WildFlyContainer wildFly = WildFlyContainer
            .version(WildFlyVersion._26, WildFlyConfiguration.STANDALONE);

    @BeforeAll
    static void setupWildFly() {
        if (HalContainer.instance() != null) {
            HalContainer.instance().connectTo(wildFly);
            if (Environment.instance().local()) {
                String url = HalContainer.instance().url();
                OnlineManagementClient client = wildFly.managementClient();
                Administration administration = new Administration(client);
                Operations operations = new Operations(client);
                Address address = Address.coreService("management").and("management-interface", "http-interface");
                try {
                    operations.invoke(LIST_ADD_OPERATION, address,
                            Values.of(NAME, ALLOWED_ORIGINS).and(VALUE, url));
                    administration.reload();
                    logger.info("Added {} as allowed origin to {}", url, wildFly);
                } catch (Exception e) {
                    logger.error("Unable to add {} as allowed origin to {}: {}", url, wildFly, e.getMessage());
                }
            }
        }
    }
}
