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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.local.cache;

import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.LocalCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CACHE_CONTAINER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.EXPIRATION;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_UPDATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.INTERVAL;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.LC_UPDATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.LIFESPAN;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.LOCAL_CACHE_ITEM;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.MAX_IDLE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.componentAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.localCacheAddress;

@Manatoko
@Testcontainers
class ExpirationEditTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(cacheContainerAddress(CC_UPDATE));
        operations.add(localCacheAddress(CC_UPDATE, LC_UPDATE));
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
        console.verticalNavigation().selectPrimary(LOCAL_CACHE_ITEM);
    }

    @Test
    void edit() throws Exception {
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
}
