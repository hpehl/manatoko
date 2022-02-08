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
package org.jboss.hal.testsuite.test.configuration.infinispan.remote.cache.container.configuration;

import java.io.IOException;

import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.TabsFragment;
import org.jboss.hal.testsuite.model.ModelNodeGenerator;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.test.configuration.infinispan.remote.cache.container.AbstractRemoteCacheContainerTest;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebElement;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.resources.CSS.btnDefault;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.nearCacheAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.remoteCacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.remoteClusterAddress;

@Manatoko
@Testcontainers
class NearCacheTest extends AbstractRemoteCacheContainerTest {

    private static final String REMOTE_CACHE_CONTAINER_NEAR_CACHE_CREATE = "remote-cache-container-with-near-cache-to-be-created-"
            + Random.name();
    private static final String REMOTE_SOCKET_BINDING_NEAR_CACHE_CREATE = "remote-socket-binding-near-cache-create-"
            + Random.name();

    private static final String REMOTE_CACHE_CONTAINER_NEAR_CACHE_EDIT = "remote-cache-container-with-near-cache-to-be-edited-"
            + Random.name();
    private static final String REMOTE_SOCKET_BINDING_NEAR_CACHE_EDIT = "remote-socket-binding-near-cache-edit-"
            + Random.name();

    private static final String REMOTE_CACHE_CONTAINER_NEAR_CACHE_DELETE = "remote-cache-container-with-near-cache-to-be-deleted-"
            + Random.name();
    private static final String REMOTE_SOCKET_BINDING_NEAR_CACHE_DELETE = "remote-socket-binding-near-cache-delete-"
            + Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING_NEAR_CACHE_CREATE);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING_NEAR_CACHE_EDIT);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING_NEAR_CACHE_DELETE);
        createRemoteCacheContainer(operations, REMOTE_CACHE_CONTAINER_NEAR_CACHE_CREATE,
                REMOTE_SOCKET_BINDING_NEAR_CACHE_CREATE,
                false);
        createRemoteCacheContainer(operations, REMOTE_CACHE_CONTAINER_NEAR_CACHE_EDIT,
                REMOTE_SOCKET_BINDING_NEAR_CACHE_EDIT, true);
        createRemoteCacheContainer(operations, REMOTE_CACHE_CONTAINER_NEAR_CACHE_DELETE,
                REMOTE_SOCKET_BINDING_NEAR_CACHE_DELETE,
                true);
        new Administration(client).reloadIfRequired();
    }

    private static void createRemoteCacheContainer(Operations operations, String name, String socketBinding,
            boolean shouldCreateNearCache)
            throws IOException {
        String remoteCluster = Random.name();
        Batch batch = new Batch().add(remoteCacheContainerAddress(name), Values.of("default-remote-cluster", remoteCluster))
                .add(remoteClusterAddress(name, remoteCluster),
                        Values.of("socket-bindings",
                                new ModelNodeGenerator.ModelNodeListBuilder().addAll(socketBinding).build()));
        if (shouldCreateNearCache) {
            batch.add(nearCacheAddress(name));
        }
        operations.batch(batch)
                .assertSuccess();
    }

    @Test
    public void enable() throws Exception {
        navigateToNearCache(REMOTE_CACHE_CONTAINER_NEAR_CACHE_CREATE);
        TabsFragment nearCacheTab = page.getConfigurationTab();
        toggleNearCache(nearCacheTab);
        new ResourceVerifier(nearCacheAddress(REMOTE_CACHE_CONTAINER_NEAR_CACHE_CREATE), client).verifyExists();
    }

    private void navigateToNearCache(String remoteCacheContainerName) {
        page.navigate(NAME, remoteCacheContainerName);
        console.verticalNavigation().selectPrimary("rcc-item");
    }

    private void toggleNearCache(TabsFragment tabsFragment) {
        tabsFragment.select("rcc-near-cache-tab");
        WebElement switchNearCacheElement = tabsFragment.getRoot()
                .findElement(ByJQuery.selector("." + btnDefault + ":visible"));
        console.scrollIntoView(switchNearCacheElement);
        switchNearCacheElement.click();
    }

    @Test
    public void editMaxEntries() throws Exception {
        navigateToNearCache(REMOTE_CACHE_CONTAINER_NEAR_CACHE_EDIT);
        crudOperations.update(nearCacheAddress(REMOTE_CACHE_CONTAINER_NEAR_CACHE_EDIT), page.getNearCacheForm(),
                "max-entries", Random.number());
    }

    @Test
    public void disable() throws Exception {
        navigateToNearCache(REMOTE_CACHE_CONTAINER_NEAR_CACHE_DELETE);
        TabsFragment nearCacheTab = page.getConfigurationTab();
        toggleNearCache(nearCacheTab);
        new ResourceVerifier(nearCacheAddress(REMOTE_CACHE_CONTAINER_NEAR_CACHE_DELETE), client).verifyDoesNotExist();
        new ResourceVerifier(
                remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_NEAR_CACHE_DELETE).and("near-cache", "none"), client)
                        .verifyExists();
    }
}
