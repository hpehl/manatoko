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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.store.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.DataSourceFixtures;
import org.jboss.hal.testsuite.page.configuration.ScatteredCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.commands.datasources.AddDataSource;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.jdbcStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class AttributesTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE = "scattered-cache-" + Random.name();
    private static final String DATA_SOURCE_CREATE = "data-source-to-be-created-" + Random.name();
    private static final String DATA_SOURCE_EDIT = "data-source-to-be-edited-" + Random.name();
    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, FULL_HA);
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and("transport", "jgroups"));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE));
        client.apply(
                new AddDataSource.Builder<>(DATA_SOURCE_CREATE).driverName("h2").jndiName(Random.jndiName())
                        .connectionUrl(
                                DataSourceFixtures.h2ConnectionUrl(Random.name()))
                        .build());
        operations.headers(Values.of("allow-resource-service-restart", true))
                .add(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), Values.of("data-source", DATA_SOURCE_CREATE));
        client.apply(
                new AddDataSource.Builder<>(DATA_SOURCE_EDIT).driverName("h2").jndiName(Random.jndiName())
                        .connectionUrl(
                                DataSourceFixtures.h2ConnectionUrl(Random.name()))
                        .build());
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page ScatteredCachePage page;

    @BeforeEach
    void prepare() {
        page.navigate(CACHE_CONTAINER, SCATTERED_CACHE);
        console.verticalNavigation().selectPrimary("scattered-cache-store-item");
    }

    @Test
    void editDataSource() throws Exception {
        console.waitNoNotification();
        crudOperations.update(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getJdbcStoreAttributesForm(),
                "data-source", DATA_SOURCE_EDIT);
    }

    @Test
    void editDialect() throws Exception {
        console.waitNoNotification();
        List<String> dialects = new ArrayList<>(
                Arrays.asList("MARIA_DB", "MY_SQL", "POSTGRES", "DERBY", "HSQL", "H2", "SQLITE", "DB2", "DB2_390",
                        "INFORMIX",
                        "INTERBASE", "FIREBIRD", "SQL_SERVER", "ACCESS", "ORACLE", "SYBASE"));
        String currentDialect = operations.readAttribute(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), "dialect")
                .stringValue("");
        dialects.remove(currentDialect);
        String dialect = dialects.get(Random.number(0, dialects.size()));
        crudOperations.update(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getJdbcStoreAttributesForm(),
                formFragment -> formFragment.select("dialect", dialect),
                resourceVerifier -> resourceVerifier.verifyAttribute("dialect", dialect));
    }

    @Test
    void toggleFetchState() throws Exception {
        console.waitNoNotification();
        boolean fetchState = operations.readAttribute(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), "fetch-state")
                .booleanValue(true);
        crudOperations.update(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getJdbcStoreAttributesForm(),
                "fetch-state", !fetchState);
    }

    @Test
    void editMaxBatchSize() throws Exception {
        console.waitNoNotification();
        crudOperations.update(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getJdbcStoreAttributesForm(),
                "max-batch-size", Random.number());
    }

    @Test
    void togglePassivation() throws Exception {
        console.waitNoNotification();
        crudOperations.update(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getJdbcStoreAttributesForm(),
                "max-batch-size", Random.number());
    }

    @Test
    void togglePreload() throws Exception {
        console.waitNoNotification();
        boolean preload = operations.readAttribute(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), "preload")
                .booleanValue(false);
        crudOperations.update(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getJdbcStoreAttributesForm(),
                "preload", !preload);
    }

    @Test
    void editProperties() throws Exception {
        console.waitNoNotification();
        crudOperations.update(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getJdbcStoreAttributesForm(),
                "properties", Random.properties());
    }

    @Test
    void togglePurge() throws Exception {
        console.waitNoNotification();
        boolean purge = operations.readAttribute(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), "purge")
                .booleanValue(true);
        crudOperations.update(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getJdbcStoreAttributesForm(),
                "purge", !purge);
    }

    @Test
    void toggleShared() throws Exception {
        console.waitNoNotification();
        boolean shared = operations.readAttribute(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), "shared")
                .booleanValue(false);
        crudOperations.update(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getJdbcStoreAttributesForm(),
                "shared", !shared);
    }
}
