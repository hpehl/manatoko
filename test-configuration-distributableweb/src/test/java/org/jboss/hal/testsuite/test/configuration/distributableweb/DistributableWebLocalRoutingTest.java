/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.hal.testsuite.test.configuration.distributableweb;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Names;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.SelectFragment;
import org.jboss.hal.testsuite.page.configuration.DistributableWebPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CACHE_CONTAINER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.INFINISPAN;
import static org.jboss.hal.dmr.ModelDescriptionConstants.LOCAL;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ROUTING;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.SUBSYSTEM_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_READ;
import static org.jboss.hal.testsuite.test.configuration.distributableweb.DistributableWebOperations.addCacheContainer;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Switch from infinispan to local routing
 */
@Manatoko
@Testcontainers
class DistributableWebLocalRoutingTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, FULL);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        addCacheContainer(client, operations, CC_READ);
        operations.add(SUBSYSTEM_ADDRESS.and(ROUTING, INFINISPAN), Values.of(CACHE_CONTAINER, CC_READ));
    }

    @Page DistributableWebPage page;
    @Inject Console console;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary("dw-routing-item");
    }

    @Test
    void switchToInfinispan() {
        console.waitNoNotification();
        SelectFragment select = page.getSwitchRouting();
        if (select != null) {
            select.select(Names.LOCAL, LOCAL);
            console.verifySuccess();
        } else {
            fail("Select control to switch routing not found!");
        }
    }
}
