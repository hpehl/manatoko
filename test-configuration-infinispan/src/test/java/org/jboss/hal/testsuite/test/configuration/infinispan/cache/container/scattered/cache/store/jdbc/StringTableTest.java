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
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.jdbcStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class StringTableTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE = "scattered-cache-" + Random.name();
    private static final String DATA_SOURCE = "data-source-to-be-created-" + Random.name();
    private static final Address STRING_TABLE_ADDRESS = jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE).and("table",
            "string");
    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and("transport", "jgroups"));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE));
        client.apply(
                new AddDataSource.Builder<>(DATA_SOURCE).driverName("h2").jndiName(Random.jndiName()).connectionUrl(
                        DataSourceFixtures.h2ConnectionUrl(Random.name())).build());
        operations.headers(Values.of("allow-resource-service-restart", true))
                .add(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), Values.of("data-source", DATA_SOURCE));
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
    void editPrefix() throws Exception {
        console.waitNoNotification();
        crudOperations.update(STRING_TABLE_ADDRESS, page.getJdbcStoreStringTableForm(), "prefix");
    }

    @Test
    void editIDColumnName() throws Exception {
        console.waitNoNotification();
        String idColumnName = Random.name();
        crudOperations.update(STRING_TABLE_ADDRESS, page.getJdbcStoreStringTableForm(),
                formFragment -> formFragment.textByLabel("ID Column Name", idColumnName),
                resourceVerifier -> resourceVerifier.verifyAttribute("id-column.name", idColumnName));
    }

    @Test
    void editIDColumnType() throws Exception {
        console.waitNoNotification();
        String idColumnType = Random.name();
        crudOperations.update(STRING_TABLE_ADDRESS, page.getJdbcStoreStringTableForm(),
                formFragment -> formFragment.textByLabel("ID Column Type", idColumnType),
                resourceVerifier -> resourceVerifier.verifyAttribute("id-column.type", idColumnType));
    }

    @Test
    void editDataColumnName() throws Exception {
        console.waitNoNotification();
        String dataColumnName = Random.name();
        crudOperations.update(STRING_TABLE_ADDRESS, page.getJdbcStoreStringTableForm(),
                formFragment -> formFragment.textByLabel("Data Column Name", dataColumnName),
                resourceVerifier -> resourceVerifier.verifyAttribute("data-column.name", dataColumnName));
    }

    @Test
    void editDataColumnType() throws Exception {
        console.waitNoNotification();
        String dataColumnType = Random.name();
        crudOperations.update(STRING_TABLE_ADDRESS, page.getJdbcStoreStringTableForm(),
                formFragment -> formFragment.textByLabel("Data Column Type", dataColumnType),
                resourceVerifier -> resourceVerifier.verifyAttribute("data-column.type", dataColumnType));
    }

    @Test
    void editTimestampColumnName() throws Exception {
        console.waitNoNotification();
        String timestampColumnName = Random.name();
        crudOperations.update(STRING_TABLE_ADDRESS, page.getJdbcStoreStringTableForm(),
                formFragment -> formFragment.textByLabel("Timestamp Column Name", timestampColumnName),
                resourceVerifier -> resourceVerifier.verifyAttribute("timestamp-column.name", timestampColumnName));
    }

    @Test
    void editTimestampColumnType() throws Exception {
        console.waitNoNotification();
        String timeStampColumnName = Random.name();
        crudOperations.update(STRING_TABLE_ADDRESS, page.getJdbcStoreStringTableForm(),
                formFragment -> formFragment.textByLabel("Timestamp Column Type", timeStampColumnName),
                resourceVerifier -> resourceVerifier.verifyAttribute("timestamp-column.type", timeStampColumnName));
    }

    @Test
    void editFetchSize() throws Exception {
        console.waitNoNotification();
        crudOperations.update(STRING_TABLE_ADDRESS, page.getJdbcStoreStringTableForm(), "fetch-size", Random.number());
    }
}
