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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import static org.jboss.hal.dmr.ModelDescriptionConstants.INFINISPAN;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class ScatteredCacheFinderTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE_CREATE = "scattered-cache-create-" + Random.name();
    private static final String SCATTERED_CACHE_DELETE = "scattered-cache-delete-" + Random.name();
    private static final String SCATTERED_CACHE_VIEW = "scattered-cache-view-" + Random.name();
    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and("transport", "jgroups"));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE_VIEW));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE_DELETE));
        new Administration(client).reloadIfRequired();
    }

    @Inject Console console;
    ColumnFragment cacheColumn;

    @BeforeEach
    void prepare() {
        cacheColumn = console.finder(NameTokens.CONFIGURATION,
                configurationSubsystemPath(INFINISPAN).append(Ids.CACHE_CONTAINER, Ids.cacheContainer(CACHE_CONTAINER)))
                .column("cache");
    }

    @Test
    void create() throws Exception {
        cacheColumn.dropdownAction("cache-add-actions", "scattered-cache-add");
        AddResourceDialogFragment addResourceDialogFragment = console.addResourceDialog();
        addResourceDialogFragment.getForm().text("name", SCATTERED_CACHE_CREATE);
        addResourceDialogFragment.add();
        console.verifySuccess();
        assertTrue(cacheColumn.containsItem(scatteredCacheId(SCATTERED_CACHE_CREATE)),
                "Newly created scattered cache should be present in the cache column");
        new ResourceVerifier(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE_CREATE), client).verifyExists();
    }

    private static String scatteredCacheId(String scatteredCacheName) {
        return Ids.build("scattered-cache", scatteredCacheName);
    }

    @Test
    void view() {
        cacheColumn.selectItem(scatteredCacheId(SCATTERED_CACHE_VIEW)).view();
        console.verify(new PlaceRequest.Builder().nameToken("scattered-cache")
                .with("cache-container", CACHE_CONTAINER)
                .with("name", SCATTERED_CACHE_VIEW)
                .build());
    }

    @Test
    void delete() throws Exception {
        assertTrue(cacheColumn.containsItem(scatteredCacheId(SCATTERED_CACHE_DELETE)),
                "Scattered cache to be removed should be present in the column before removal");
        cacheColumn.selectItem(scatteredCacheId(SCATTERED_CACHE_DELETE)).dropdown().click("Remove");
        console.confirmationDialog().confirm();
        console.verifySuccess();
        assertFalse(cacheColumn.containsItem(scatteredCacheId(SCATTERED_CACHE_DELETE)),
                "Recently removed scattered cache should not be present in the column anymore");
        new ResourceVerifier(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE_DELETE),
                client).verifyDoesNotExist();
    }
}
