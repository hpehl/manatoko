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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.configuration;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.ScatteredCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.JGROUPS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TRANSPORT;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.lockingAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

@Manatoko
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
class LockingTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE_LOCKING = "scattered-cache-" + Random.name();

    @Container static Browser browser = new Browser();
    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and(TRANSPORT, JGROUPS));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE_LOCKING));
        // scattered-cache=*/component=locking is automatically created
        // remove it to later create it
        operations.removeIfExists(lockingAddress(CACHE_CONTAINER, SCATTERED_CACHE_LOCKING));
    }

    @Inject CrudOperations crud;
    @Inject Console console;
    @Page ScatteredCachePage page;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate(CACHE_CONTAINER, SCATTERED_CACHE_LOCKING);
        console.verticalNavigation().selectPrimary("scattered-cache-item");
        form = page.getLockingForm();
    }

    @Test
    void create() throws Exception {
        crud.createSingleton(lockingAddress(CACHE_CONTAINER, SCATTERED_CACHE_LOCKING), form);
    }

    @Test
    void remove() throws Exception {
        crud.deleteSingleton(lockingAddress(CACHE_CONTAINER, SCATTERED_CACHE_LOCKING), form);
    }

    @Test
    void editAcquireTimeout() throws Exception {
        crud.update(lockingAddress(CACHE_CONTAINER, SCATTERED_CACHE_LOCKING), form, "acquire-timeout", 123L);
    }

    @Test
    void editConcurrencyLevel() throws Exception {
        crud.update(lockingAddress(CACHE_CONTAINER, SCATTERED_CACHE_LOCKING), form, "concurrency-level", 324);
    }

    @Test
    void editToggleStriping() throws Exception {
        boolean striping = operations.readAttribute(lockingAddress(CACHE_CONTAINER, SCATTERED_CACHE_LOCKING), "stripping")
                .booleanValue(false);
        crud.update(lockingAddress(CACHE_CONTAINER, SCATTERED_CACHE_LOCKING), form, "striping", !striping);
    }
}
