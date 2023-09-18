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
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.ScatteredCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.hotrodStoreAddress;

@Manatoko
@Testcontainers
class AttributesTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        HotrodStoreSetup.setup(wildFly, operations -> {
        });
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page ScatteredCachePage page;
    Operations operations;
    FormFragment form;

    @BeforeEach
    void navigate() {
        operations = new Operations(wildFly.managementClient());
        page.navigate(CC_CREATE, SC_CREATE);
        console.verticalNavigation().selectPrimary("scattered-cache-store-item");
        page.selectHotrodStoreAttributes();
        form = page.getHotrodStoreAttributesForm();
    }

    @Test
    void editCacheConfiguration() throws Exception {
        crudOperations.update(hotrodStoreAddress(CC_CREATE, SC_CREATE), form, "cache-configuration");
    }

    @Test
    void editMaxBatchSize() throws Exception {
        crudOperations.update(hotrodStoreAddress(CC_CREATE, SC_CREATE), form, "max-batch-size", Random.number());
    }

    @Test
    void togglePassivation() throws Exception {
        boolean passivation = operations.readAttribute(hotrodStoreAddress(CC_CREATE, SC_CREATE), "passivation")
                .booleanValue(true);
        crudOperations.update(hotrodStoreAddress(CC_CREATE, SC_CREATE), form, "passivation", !passivation);
    }

    @Test
    void togglePreload() throws Exception {
        boolean preload = operations.readAttribute(hotrodStoreAddress(CC_CREATE, SC_CREATE), "preload")
                .booleanValue(false);
        crudOperations.update(hotrodStoreAddress(CC_CREATE, SC_CREATE), form, "preload", !preload);
    }

    @Test
    void editProperties() throws Exception {
        crudOperations.update(hotrodStoreAddress(CC_CREATE, SC_CREATE), form, "properties", Random.properties());
    }

    @Test
    void togglePurge() throws Exception {
        boolean purge = operations.readAttribute(hotrodStoreAddress(CC_CREATE, SC_CREATE), "purge")
                .booleanValue(true);
        crudOperations.update(hotrodStoreAddress(CC_CREATE, SC_CREATE), form, "purge", !purge);
    }

    @Test
    void editRemoteCacheContainer() throws Exception {
        crudOperations.update(hotrodStoreAddress(CC_CREATE, SC_CREATE), form, "remote-cache-container",
                HotrodStoreSetup.REMOTE_CACHE_CONTAINER_EDIT);
    }

    @Test
    void toggleShared() throws Exception {
        boolean shared = operations.readAttribute(hotrodStoreAddress(CC_CREATE, SC_CREATE), "shared")
                .booleanValue(false);
        crudOperations.update(hotrodStoreAddress(CC_CREATE, SC_CREATE), form, "shared", !shared);
    }
}
