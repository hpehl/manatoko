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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.store.hotrod;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddRemoteSocketBinding;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.model.AvailablePortFinder;
import org.jboss.hal.testsuite.model.ModelNodeGenerator;
import org.jboss.hal.testsuite.page.configuration.ScatteredCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SOCKET_BINDINGS;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.hotrodStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.remoteCacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class AttributesTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE = "scattered-cache-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING = "remote-socket-binding-" + Random.name();
    private static final String REMOTE_CLUSTER = "remote-cluster-" + Random.name();
    private static final String REMOTE_CACHE_CONTAINER = "remote-cache-container-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING_EDIT = "remote-socket-binding-edit-" + Random.name();
    private static final String REMOTE_CLUSTER_EDIT = "remote-cluster-edit-" + Random.name();
    private static final String REMOTE_CACHE_CONTAINER_EDIT = "remote-cache-container-edit-" + Random.name();
    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        client.apply(new AddRemoteSocketBinding(REMOTE_SOCKET_BINDING, "localhost",
                AvailablePortFinder.getNextAvailableTCPPort()));
        operations.batch(new Batch().add(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER),
                Values.of("default-remote-cluster", REMOTE_CLUSTER))
                .add(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER).and("remote-cluster", REMOTE_CLUSTER),
                        Values.of(SOCKET_BINDINGS,
                                new ModelNodeGenerator.ModelNodeListBuilder().addAll(REMOTE_SOCKET_BINDING).build())));
        client.apply(new AddRemoteSocketBinding(REMOTE_SOCKET_BINDING_EDIT, "localhost",
                AvailablePortFinder.getNextAvailableTCPPort()));
        operations.batch(new Batch().add(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_EDIT),
                Values.of("default-remote-cluster", REMOTE_CLUSTER_EDIT))
                .add(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_EDIT).and("remote-cluster",
                        REMOTE_CLUSTER_EDIT),
                        Values.of(SOCKET_BINDINGS,
                                new ModelNodeGenerator.ModelNodeListBuilder().addAll(REMOTE_SOCKET_BINDING).build())));
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and("transport", "jgroups"));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE));
        operations.headers(Values.of("allow-resource-service-restart", true))
                .add(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE),
                        Values.of("remote-cache-container", REMOTE_CACHE_CONTAINER));
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page ScatteredCachePage page;

    @BeforeEach
    void navigate() {
        page.navigate(CACHE_CONTAINER, SCATTERED_CACHE);
        console.verticalNavigation().selectPrimary("scattered-cache-store-item");
    }

    @Test
    void editCacheConfiguration() throws Exception {
        console.waitNoNotification();
        crudOperations.update(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getHotrodStoreAttributesForm(),
                "cache-configuration");
    }

    @Test
    void toggleFetchState() throws Exception {
        console.waitNoNotification();
        boolean fetchState = operations.readAttribute(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE),
                "fetch-state")
                .booleanValue(true);
        crudOperations.update(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getHotrodStoreAttributesForm(),
                "fetch-state", !fetchState);
    }

    @Test
    void editMaxBatchSize() throws Exception {
        console.waitNoNotification();
        crudOperations.update(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getHotrodStoreAttributesForm(),
                "max-batch-size", Random.number());
    }

    @Test
    void togglePassivation() throws Exception {
        console.waitNoNotification();
        boolean passivation = operations.readAttribute(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE),
                "passivation")
                .booleanValue(true);
        crudOperations.update(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getHotrodStoreAttributesForm(),
                "passivation", !passivation);
    }

    @Test
    void togglePreload() throws Exception {
        console.waitNoNotification();
        boolean preload = operations.readAttribute(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), "preload")
                .booleanValue(false);
        crudOperations.update(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getHotrodStoreAttributesForm(),
                "preload", !preload);
    }

    @Test
    void editProperties() throws Exception {
        console.waitNoNotification();
        crudOperations.update(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getHotrodStoreAttributesForm(),
                "properties", Random.properties());
    }

    @Test
    void togglePurge() throws Exception {
        console.waitNoNotification();
        boolean purge = operations.readAttribute(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), "purge")
                .booleanValue(true);
        crudOperations.update(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getHotrodStoreAttributesForm(),
                "purge", !purge);
    }

    @Test
    void editRemoteCacheContainer() throws Exception {
        console.waitNoNotification();
        crudOperations.update(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getHotrodStoreAttributesForm(),
                "remote-cache-container", REMOTE_CACHE_CONTAINER_EDIT);
    }

    @Test
    void toggleShared() throws Exception {
        console.waitNoNotification();
        boolean shared = operations.readAttribute(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), "shared")
                .booleanValue(false);
        crudOperations.update(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getHotrodStoreAttributesForm(),
                "shared", !shared);
    }
}
