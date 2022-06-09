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
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.stateTransferAddress;

@Manatoko
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
class StateTransferTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE_STATE_TRANSFER = "scattered-cache-" + Random.name();
    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and(TRANSPORT, JGROUPS));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE_STATE_TRANSFER));
        operations.removeIfExists(stateTransferAddress(CACHE_CONTAINER, SCATTERED_CACHE_STATE_TRANSFER));
    }

    @Inject CrudOperations crud;
    @Inject Console console;
    @Page ScatteredCachePage page;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate(CACHE_CONTAINER, SCATTERED_CACHE_STATE_TRANSFER);
        console.verticalNavigation().selectPrimary("scattered-cache-item");
        form = page.getStateTransferForm();
    }

    @Test
    void create() throws Exception {
        crud.createSingleton(stateTransferAddress(CACHE_CONTAINER, SCATTERED_CACHE_STATE_TRANSFER), form);
    }

    @Test
    void remove() throws Exception {
        crud.deleteSingleton(stateTransferAddress(CACHE_CONTAINER, SCATTERED_CACHE_STATE_TRANSFER), form);
    }

    @Test
    void editChunkSize() throws Exception {
        crud.update(stateTransferAddress(CACHE_CONTAINER, SCATTERED_CACHE_STATE_TRANSFER), form, "chunk-size", 123);
    }

    @Test
    void editTimeout() throws Exception {
        crud.update(stateTransferAddress(CACHE_CONTAINER, SCATTERED_CACHE_STATE_TRANSFER), form, "timeout", 789L);
    }
}
