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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.page.configuration.ScatteredCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.JGROUPS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MODULE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TRANSPORT;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CONSISTENT_HASH_STRATEGY;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class AttributesTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE = "scattered-cache-" + Random.name();
    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and(TRANSPORT, JGROUPS));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE));
    }

    @Inject CrudOperations crud;
    @Inject Console console;
    @Page ScatteredCachePage page;

    @BeforeEach
    void prepare() {
        page.navigate(CACHE_CONTAINER, SCATTERED_CACHE);
        console.verticalNavigation().selectPrimary("scattered-cache-item");
    }

    @Test
    void editBiasLifeSpan() throws Exception {
        crud.update(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getConfigurationForm(),
                "bias-lifespan", (long) Random.number());
    }

    @Test
    void editConsistentHashStrategy() throws Exception {
        String currentHashStrategy = operations.readAttribute(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE),
                CONSISTENT_HASH_STRATEGY)
                .stringValue();
        List<String> hashStrategies = new ArrayList<>(Arrays.asList("INTER_CACHE", "INTRA_CACHE"));
        hashStrategies.remove(currentHashStrategy);
        crud.update(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getConfigurationForm(),
                formFragment -> formFragment.select(CONSISTENT_HASH_STRATEGY, hashStrategies.get(0)),
                resourceVerifier -> resourceVerifier.verifyAttribute(CONSISTENT_HASH_STRATEGY, hashStrategies.get(0)));
    }

    @Test
    void editInvalidationBatchSize() throws Exception {
        crud.update(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getConfigurationForm(),
                "invalidation-batch-size", Random.number());
    }

    @Test
    void editModule() throws Exception {
        crud.update(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getConfigurationForm(), MODULE);
    }

    @Test
    void editRemoteTimeout() throws Exception {
        crud.update(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getConfigurationForm(),
                "remote-timeout", (long) Random.number());
    }

    @Test
    void editSegments() throws Exception {
        crud.update(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getConfigurationForm(),
                "segments", Random.number());
    }

    @Test
    void toggleStatisticsEnabled() throws Exception {
        boolean statisticsEnabled = operations
                .readAttribute(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE), "statistics-enabled")
                .booleanValue();
        crud.update(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getConfigurationForm(),
                "statistics-enabled", !statisticsEnabled);
    }
}
