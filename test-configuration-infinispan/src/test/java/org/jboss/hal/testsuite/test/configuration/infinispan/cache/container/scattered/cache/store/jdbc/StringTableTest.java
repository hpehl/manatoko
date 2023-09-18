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
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.ScatteredCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.jdbcStoreAddress;

@Manatoko
@Testcontainers
class StringTableTest {

    private static final Address STRING_TABLE_ADDRESS = jdbcStoreAddress(CC_CREATE, SC_CREATE)
            .and("table", "string");
    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        JdbcStoreSetup.setup(wildFly, operations -> {
        });
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page ScatteredCachePage page;
    Operations operations;
    FormFragment form;

    @BeforeEach
    void prepare() {
        operations = new Operations(wildFly.managementClient());
        page.navigate(CC_CREATE, SC_CREATE);
        console.verticalNavigation().selectPrimary("scattered-cache-store-item");
        page.selectJdbcStoreStringTable();
        form = page.getJdbcStoreStringTableForm();
    }

    @Test
    void editPrefix() throws Exception {
        crudOperations.update(STRING_TABLE_ADDRESS, form, "prefix");
    }

    @Test
    void editIDColumnName() throws Exception {
        String idColumnName = Random.name();
        crudOperations.update(STRING_TABLE_ADDRESS, form,
                formFragment -> formFragment.textByLabel("ID Column / ID Column Name", idColumnName),
                resourceVerifier -> resourceVerifier.verifyAttribute("id-column.name", idColumnName));
    }

    @Test
    void editIDColumnType() throws Exception {
        String idColumnType = Random.name();
        crudOperations.update(STRING_TABLE_ADDRESS, form,
                formFragment -> formFragment.textByLabel("ID Column / ID Column Type", idColumnType),
                resourceVerifier -> resourceVerifier.verifyAttribute("id-column.type", idColumnType));
    }

    @Test
    void editDataColumnName() throws Exception {
        String dataColumnName = Random.name();
        crudOperations.update(STRING_TABLE_ADDRESS, form,
                formFragment -> formFragment.textByLabel("Data Column / Data Column Name", dataColumnName),
                resourceVerifier -> resourceVerifier.verifyAttribute("data-column.name", dataColumnName));
    }

    @Test
    void editDataColumnType() throws Exception {
        String dataColumnType = Random.name();
        crudOperations.update(STRING_TABLE_ADDRESS, form,
                formFragment -> formFragment.textByLabel("Data Column / Data Column Type", dataColumnType),
                resourceVerifier -> resourceVerifier.verifyAttribute("data-column.type", dataColumnType));
    }

    @Test
    void editTimestampColumnName() throws Exception {
        String timestampColumnName = Random.name();
        crudOperations.update(STRING_TABLE_ADDRESS, form,
                formFragment -> formFragment.textByLabel("Timestamp Column / Timestamp Column Name", timestampColumnName),
                resourceVerifier -> resourceVerifier.verifyAttribute("timestamp-column.name", timestampColumnName));
    }

    @Test
    void editTimestampColumnType() throws Exception {
        String timeStampColumnName = Random.name();
        crudOperations.update(STRING_TABLE_ADDRESS, form,
                formFragment -> formFragment.textByLabel("Timestamp Column / Timestamp Column Type", timeStampColumnName),
                resourceVerifier -> resourceVerifier.verifyAttribute("timestamp-column.type", timeStampColumnName));
    }

    @Test
    void editFetchSize() throws Exception {
        crudOperations.update(STRING_TABLE_ADDRESS, form, "fetch-size", Random.number());
    }
}
