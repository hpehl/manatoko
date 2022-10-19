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

import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.LocalCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.commands.infinispan.cache.AddLocalCache;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CACHE_CONTAINER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.EXPIRATION;
import static org.jboss.hal.dmr.ModelDescriptionConstants.LOCKING;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MODE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MODULE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STATISTICS_ENABLED;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TRANSACTION;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.ACQUIRE_TIMEOUT;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_UPDATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CONCURRENCY_LEVEL;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.INTERVAL;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.ISOLATION;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.LC_UPDATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.LIFESPAN;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.LOCAL_CACHE_ITEM;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.MAX_IDLE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.componentAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.localCacheAddress;

@Manatoko
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled // TODO Fix failing tests
class LocalCacheTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(cacheContainerAddress(CC_UPDATE));
        client.apply(new AddLocalCache.Builder(LC_UPDATE).cacheContainer(CC_UPDATE).build());
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page LocalCachePage page;

    @BeforeEach
    void prepare() {
        Map<String, String> params = new HashMap<>();
        params.put(CACHE_CONTAINER, CC_UPDATE);
        params.put(NAME, LC_UPDATE);
        page.navigate(params);
        console.verticalNavigation().selectPrimary(Ids.LOCAL_CACHE + "-" + Ids.ITEM);
    }

    // ------------------------------------------------------ attributes

    @Test
    void attributesEdit() throws Exception {
        console.verticalNavigation().selectPrimary(LOCAL_CACHE_ITEM);
        page.getLocalCacheTabs().select("local-cache-tab");
        FormFragment form = page.getConfigurationForm();

        String moduleName = Random.name();
        crud.update(localCacheAddress(CC_UPDATE, LC_UPDATE), form,
                f -> {
                    f.text(MODULE, moduleName);
                    f.flip(STATISTICS_ENABLED, true);
                },
                resourceVerifier -> {
                    resourceVerifier.verifyAttribute(MODULE, moduleName);
                    resourceVerifier.verifyAttribute(STATISTICS_ENABLED, true);

                });
    }

    @Test
    void attributesReset() throws Exception {
        console.verticalNavigation().selectPrimary(LOCAL_CACHE_ITEM);
        page.getLocalCacheTabs().select("local-cache-tab");
        FormFragment form = page.getConfigurationForm();
        crud.reset(localCacheAddress(CC_UPDATE, LC_UPDATE), form);
    }

    // ------------------------------------------------------ expiration

    @Test
    void expiration1Edit() throws Exception {
        console.verticalNavigation().selectPrimary(LOCAL_CACHE_ITEM);
        page.getLocalCacheTabs().select("local-cache-cache-component-expiration-tab");
        FormFragment form = page.getExpirationForm();

        crud.update(componentAddress(CC_UPDATE, LC_UPDATE, EXPIRATION), form,
                f -> {
                    f.number(INTERVAL, 1);
                    f.number(LIFESPAN, 2);
                    f.number(MAX_IDLE, 3);
                },
                resourceVerifier -> {
                    resourceVerifier.verifyAttribute(INTERVAL, 1L);
                    resourceVerifier.verifyAttribute(LIFESPAN, 2L);
                    resourceVerifier.verifyAttribute(MAX_IDLE, 3L);
                });
    }

    @Test
    void expiration2Reset() throws Exception {
        console.verticalNavigation().selectPrimary(LOCAL_CACHE_ITEM);
        page.getLocalCacheTabs().select("local-cache-cache-component-expiration-tab");
        FormFragment form = page.getExpirationForm();
        crud.reset(componentAddress(CC_UPDATE, LC_UPDATE, EXPIRATION), form);
    }

    @Test
    void expiration3Remove() throws Exception {
        console.verticalNavigation().selectPrimary(LOCAL_CACHE_ITEM);
        page.getLocalCacheTabs().select("local-cache-cache-component-expiration-tab");
        FormFragment form = page.getExpirationForm();
        crud.deleteSingleton(componentAddress(CC_UPDATE, LC_UPDATE, EXPIRATION), form);
    }

    // ------------------------------------------------------ locking

    @Test
    void locking1Edit() throws Exception {
        console.verticalNavigation().selectPrimary(LOCAL_CACHE_ITEM);
        page.getLocalCacheTabs().select("local-cache-cache-component-locking-tab");
        FormFragment form = page.getLockingForm();

        crud.update(componentAddress(CC_UPDATE, LC_UPDATE, LOCKING), form,
                f -> {
                    f.number(ACQUIRE_TIMEOUT, 1);
                    f.number(CONCURRENCY_LEVEL, 100);
                    f.select(ISOLATION, "NONE");
                },
                resourceVerifier -> {
                    resourceVerifier.verifyAttribute(ACQUIRE_TIMEOUT, 1L);
                    resourceVerifier.verifyAttribute(CONCURRENCY_LEVEL, 100);
                    resourceVerifier.verifyAttribute(ISOLATION, "NONE");
                });
    }

    @Test
    void locking2Reset() throws Exception {
        console.verticalNavigation().selectPrimary(LOCAL_CACHE_ITEM);
        page.getLocalCacheTabs().select("local-cache-cache-component-locking-tab");
        FormFragment form = page.getLockingForm();
        crud.reset(componentAddress(CC_UPDATE, LC_UPDATE, LOCKING), form);
    }

    @Test
    void locking3Remove() throws Exception {
        console.verticalNavigation().selectPrimary(LOCAL_CACHE_ITEM);
        page.getLocalCacheTabs().select("local-cache-cache-component-locking-tab");
        FormFragment form = page.getLockingForm();
        crud.deleteSingleton(componentAddress(CC_UPDATE, LC_UPDATE, LOCKING), form);
    }

    // ------------------------------------------------------ transaction

    @Test
    void transaction1Edit() throws Exception {
        console.verticalNavigation().selectPrimary(LOCAL_CACHE_ITEM);
        page.getLocalCacheTabs().select("local-cache-cache-component-transaction-tab");
        FormFragment form = page.getTransactionForm();

        crud.update(componentAddress(CC_UPDATE, LC_UPDATE, TRANSACTION), form,
                f -> {
                    f.select(LOCKING, "OPTIMISTIC");
                    f.select(MODE, "BATCH");
                },
                resourceVerifier -> {
                    resourceVerifier.verifyAttribute(LOCKING, "OPTIMISTIC");
                    resourceVerifier.verifyAttribute(MODE, "BATCH");
                });
    }

    @Test
    void transaction2Reset() throws Exception {
        console.verticalNavigation().selectPrimary(LOCAL_CACHE_ITEM);
        page.getLocalCacheTabs().select("local-cache-cache-component-transaction-tab");
        FormFragment form = page.getTransactionForm();
        crud.reset(componentAddress(CC_UPDATE, LC_UPDATE, TRANSACTION), form);
    }

    @Test
    void transaction3Remove() throws Exception {
        console.verticalNavigation().selectPrimary(LOCAL_CACHE_ITEM);
        page.getLocalCacheTabs().select("local-cache-cache-component-transaction-tab");
        FormFragment form = page.getTransactionForm();
        crud.deleteSingleton(componentAddress(CC_UPDATE, LC_UPDATE, TRANSACTION), form);
    }
}
