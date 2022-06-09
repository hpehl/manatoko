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
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.model.AvailablePortFinder;
import org.jboss.hal.testsuite.model.ModelNodeGenerator;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.page.configuration.ScatteredCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.ALLOW_RESOURCE_SERVICE_RESTART;
import static org.jboss.hal.dmr.ModelDescriptionConstants.JGROUPS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TRANSPORT;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.BEHIND;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SOCKET_BINDINGS;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.THROUGH;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.WRITE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.hotrodStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.remoteCacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

@Manatoko
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled // TODO Fix failing tests
class WriteBehaviourTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE = "scattered-cache-" + Random.name();
    private static final String REMOTE_CC = "rcc-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING = "rem-socket-binding-" + Random.name();
    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        String remoteCluster = "remote-cluster-" + Random.name();
        client.apply(new AddRemoteSocketBinding(REMOTE_SOCKET_BINDING, "localhost",
                AvailablePortFinder.getNextAvailableTCPPort()));
        operations.batch(new Batch().add(remoteCacheContainerAddress(REMOTE_CC),
                Values.of("default-remote-cluster", remoteCluster))
                .add(remoteCacheContainerAddress(REMOTE_CC).and("remote-cluster", remoteCluster),
                        Values.of(SOCKET_BINDINGS,
                                new ModelNodeGenerator.ModelNodeListBuilder().addAll(REMOTE_SOCKET_BINDING).build())));
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and(TRANSPORT, JGROUPS));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE));
        operations.headers(Values.of(ALLOW_RESOURCE_SERVICE_RESTART, true))
                .add(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE),
                        Values.of("remote-cache-container", REMOTE_CC));
    }

    @Inject private CrudOperations crud;
    @Inject private Console console;
    @Page private ScatteredCachePage page;
    private FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate(CACHE_CONTAINER, SCATTERED_CACHE);
        console.waitNoNotification();
        console.verticalNavigation().selectPrimary("scattered-cache-store-item");
        form = page.getHotrodStoreWriteBehindForm();
    }

    @Test
    void change1ToWriteBehind() throws Exception {
        console.waitNoNotification();
        page.switchBehaviour();
        new ResourceVerifier(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE).and(WRITE, BEHIND),
                client).verifyExists();
    }

    @Test
    void change2ModificationQueueSize() throws Exception {
        console.waitNoNotification();
        crud.update(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE).and(WRITE, BEHIND), form,
                "modification-queue-size", Random.number());
    }

    @Test
    void change3ThreadPoolSize() throws Exception {
        console.waitNoNotification();
        crud.update(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE).and(WRITE, BEHIND), form,
                "thread-pool-size", Random.number());
    }

    @Test
    void change4ToWriteThrough() throws Exception {
        page.switchBehaviour();
        new ResourceVerifier(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE).and(WRITE, THROUGH),
                client).verifyExists();
    }
}
