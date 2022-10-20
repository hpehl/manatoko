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
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.SelectFragment;
import org.jboss.hal.testsuite.page.configuration.DistributableWebPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.commands.infinispan.cache.AddLocalCache;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CACHE_CONTAINER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.INFINISPAN;
import static org.jboss.hal.dmr.ModelDescriptionConstants.LOCAL;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ROUTING;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.SUBSYSTEM_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_READ;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.LC_READ;
import static org.jboss.hal.testsuite.test.configuration.distributableweb.DistributableWebOperations.addCacheContainer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Switch from local to infinispan routing
 */
@Manatoko
@Testcontainers
class DistributableWebInfinispanRoutingTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(SUBSYSTEM_ADDRESS.and(ROUTING, LOCAL));
        addCacheContainer(client, operations, CC_READ);
        client.apply(new AddLocalCache.Builder(LC_READ).cacheContainer(CC_READ).build());
    }

    @Page DistributableWebPage page;
    @Inject Console console;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary("dw-routing-item");
        form = page.getRoutingForm();
    }

    @Test
    void switchToInfinispan() {
        console.waitNoNotification();
        SelectFragment select = page.getSwitchRouting();
        if (select != null) {
            select.select(Names.INFINISPAN, INFINISPAN);
            AddResourceDialogFragment dialog = console.addResourceDialog();
            dialog.getForm().text(CACHE_CONTAINER, CC_READ);
            dialog.add();
            console.verifySuccess();
            assertEquals(CC_READ, form.text(CACHE_CONTAINER));
        } else {
            fail("Select control to switch routing not found!");
        }
    }
}
