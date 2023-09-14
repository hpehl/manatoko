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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.jboss.hal.dmr.ModelDescriptionConstants.INFINISPAN;
import static org.jboss.hal.dmr.ModelDescriptionConstants.JGROUPS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TRANSPORT;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_READ;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SC_DELETE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SC_READ;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Manatoko
@Testcontainers
class ScatteredCacheFinderTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(cacheContainerAddress(CC_READ));
        operations.add(cacheContainerAddress(CC_READ).and(TRANSPORT, JGROUPS));
        operations.add(scatteredCacheAddress(CC_READ, SC_READ));
        operations.add(scatteredCacheAddress(CC_READ, SC_DELETE));
        new Administration(client).reloadIfRequired();
    }

    @Inject Console console;
    ColumnFragment cacheColumn;

    @BeforeEach
    void prepare() {
        cacheColumn = console.finder(NameTokens.CONFIGURATION,
                configurationSubsystemPath(INFINISPAN).append(Ids.CACHE_CONTAINER, Ids.cacheContainer(CC_READ)))
                .column("cache");
    }

    @Test
    void create() throws Exception {
        cacheColumn.dropdownAction("cache-add-actions", "scattered-cache-add");
        AddResourceDialogFragment addResourceDialogFragment = console.addResourceDialog();
        addResourceDialogFragment.getForm().text("name", SC_CREATE);
        addResourceDialogFragment.add();
        console.verifySuccess();
        assertTrue(cacheColumn.containsItem(scatteredCacheId(SC_CREATE)),
                "Newly created scattered cache should be present in the cache column");
        new ResourceVerifier(scatteredCacheAddress(CC_READ, SC_CREATE), client).verifyExists();
    }

    private static String scatteredCacheId(String scatteredCacheName) {
        return Ids.build("scattered-cache", scatteredCacheName);
    }

    @Test
    void view() {
        cacheColumn.selectItem(scatteredCacheId(SC_READ)).view();
        console.verify(new PlaceRequest.Builder().nameToken("scattered-cache")
                .with("cache-container", CC_READ)
                .with("name", SC_READ)
                .build());
    }

    @Test
    void delete() throws Exception {
        assertTrue(cacheColumn.containsItem(scatteredCacheId(SC_DELETE)),
                "Scattered cache to be removed should be present in the column before removal");
        cacheColumn.selectItem(scatteredCacheId(SC_DELETE)).dropdown().click("Remove");
        console.confirmationDialog().confirm();
        console.verifySuccess();
        waitGui().until().element(By.id(scatteredCacheId(SC_DELETE))).is().not().present();
        assertFalse(cacheColumn.containsItem(scatteredCacheId(SC_DELETE)),
                "Recently removed scattered cache should not be present in the column anymore");
        new ResourceVerifier(scatteredCacheAddress(CC_READ, SC_DELETE),
                client).verifyDoesNotExist();
    }
}
