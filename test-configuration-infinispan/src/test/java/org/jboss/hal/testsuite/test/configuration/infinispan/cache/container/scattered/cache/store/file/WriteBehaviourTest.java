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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.store.file;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
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
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.ALLOW_RESOURCE_SERVICE_RESTART;
import static org.jboss.hal.dmr.ModelDescriptionConstants.JGROUPS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TRANSPORT;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.BEHIND;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.THROUGH;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.WRITE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.fileStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

@Manatoko
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled // TODO Fix failing tests
class WriteBehaviourTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE = "scattered-cache-" + Random.name();
    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, FULL_HA);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and(TRANSPORT, JGROUPS));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE));
        operations.headers(Values.of(ALLOW_RESOURCE_SERVICE_RESTART, true))
                .add(fileStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE));
    }

    @Inject CrudOperations crud;
    @Inject Console console;
    @Page ScatteredCachePage page;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate(CACHE_CONTAINER, SCATTERED_CACHE);
        console.waitNoNotification();
        console.verticalNavigation().selectPrimary("scattered-cache-store-item");
        page.getFileStoreTab().select(ScatteredCachePage.FILE_STORE_WRITE_BEHAVIOUR_TAB);
        form = page.getFileStoreWriteBehindForm();
    }

    @Test
    void change1ToWriteBehind() throws Exception {
        console.waitNoNotification();
        page.switchBehaviour();
        new ResourceVerifier(fileStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE).and(WRITE, BEHIND),
                client).verifyExists();
    }

    @Test
    void change2ModificationQueueSize() throws Exception {
        console.waitNoNotification();
        crud.update(fileStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE).and(WRITE, BEHIND), form,
                "modification-queue-size", Random.number());
    }

    @Test
    void change3ThreadPoolSize() throws Exception {
        console.waitNoNotification();
        crud.update(fileStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE).and(WRITE, BEHIND), form,
                "thread-pool-size", Random.number());
    }

    @Test
    void change4ToWriteThrough() throws Exception {
        console.waitNoNotification();
        page.switchBehaviour();
        new ResourceVerifier(fileStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE).and(WRITE, THROUGH),
                client).verifyExists();
    }
}
