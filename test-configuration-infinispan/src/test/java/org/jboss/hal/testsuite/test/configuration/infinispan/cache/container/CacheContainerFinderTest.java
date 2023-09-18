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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.page.Places;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.jboss.hal.dmr.ModelDescriptionConstants.INFINISPAN;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_DELETE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_READ;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Manatoko
@Testcontainers
class CacheContainerFinderTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);
    static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(cacheContainerAddress(CC_READ));
        operations.add(cacheContainerAddress(CC_DELETE));
    }

    @Inject Console console;
    ColumnFragment column;

    @BeforeEach
    void prepare() {
        column = console.finder(NameTokens.CONFIGURATION, configurationSubsystemPath(INFINISPAN))
                .column(Ids.CACHE_CONTAINER);
    }

    @Test
    void create() throws Exception {
        column.dropdownAction(Ids.CACHE_CONTAINER_ADD_ACTIONS, Ids.CACHE_CONTAINER_ADD);
        AddResourceDialogFragment dialog = console.addResourceDialog();
        dialog.getForm().text(NAME, CC_CREATE);
        dialog.add();

        console.verifySuccess();
        assertTrue(column.containsItem(Ids.cacheContainer(CC_CREATE)));
        new ResourceVerifier(cacheContainerAddress(CC_CREATE), client).verifyExists();
    }

    @Test
    void read() {
        assertTrue(column.containsItem(Ids.cacheContainer(CC_READ)));
    }

    @Test
    void select() {
        column.selectItem(Ids.cacheContainer(CC_READ));
        PlaceRequest placeRequest = Places.finderPlace(NameTokens.CONFIGURATION,
                configurationSubsystemPath(INFINISPAN)
                        .append(Ids.CACHE_CONTAINER, Ids.cacheContainer(CC_READ)));
        console.verify(placeRequest);
    }

    // TODO Enable once https://issues.redhat.com/browse/HAL-1902 has been fixed
    // @Test
    void view() {
        try {
            column.selectItem(Ids.cacheContainer(CC_READ)).view();
        } catch (TimeoutException e) {
            fail("Not possible to open Cache container detail probably due to https://issues.jboss.org/browse/HAL-1442");
        }

        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.CACHE_CONTAINER)
                .with(NAME, CC_READ)
                .build();
        console.verify(placeRequest);
    }

    @Test
    void delete() throws Exception {
        column.selectItem(Ids.cacheContainer(CC_DELETE)).dropdown().click("Remove");
        console.confirmationDialog().confirm();

        console.verifySuccess();
        waitGui().until().element(By.id(Ids.cacheContainer(CC_DELETE))).is().not().present();
        assertFalse(column.containsItem(Ids.cacheContainer(CC_DELETE)));
        new ResourceVerifier(cacheContainerAddress(CC_DELETE), client).verifyDoesNotExist();
    }
}
