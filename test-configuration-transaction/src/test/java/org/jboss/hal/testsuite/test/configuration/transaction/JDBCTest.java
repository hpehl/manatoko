/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.hal.testsuite.test.configuration.transaction;

import java.util.function.Consumer;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.TransactionFixtures;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.TransactionPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.commands.datasources.AddDataSource;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;

@Manatoko
@Testcontainers
class JDBCTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);
    static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        operations = new Operations(wildFly.managementClient());
        wildFly.managementClient().apply(new AddDataSource.Builder<>(TransactionFixtures.JDBC_DATASOURCE)
                .jndiName("java:/" + TransactionFixtures.JDBC_DATASOURCE)
                .driverName("h2")
                .connectionUrl("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1")
                .enableAfterCreate()
                .build());
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page TransactionPage page;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation()
                .selectPrimary(Ids.build("tx", "jdbc", "config", "item"));
    }

    @Test
    void toggleJDBCActionStoreDropTable() throws Exception {
        boolean jdbcActionStoreDropTable = operations.readAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS,
                TransactionFixtures.JDBC_ACTION_STORE_DROP_TABLE)
                .booleanValue();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getJdbcForm(),
                toggleWithUseJDBCStore(TransactionFixtures.JDBC_ACTION_STORE_DROP_TABLE, !jdbcActionStoreDropTable),
                resourceVerifier -> resourceVerifier.verifyAttribute(TransactionFixtures.JDBC_ACTION_STORE_DROP_TABLE,
                        !jdbcActionStoreDropTable));
    }

    @Test
    void editJDBCActionStoreTablePrefix() throws Exception {
        String jdbcActionStoreTablePrefix = Random.name();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getJdbcForm(),
                editWithUseJDBCStore(TransactionFixtures.JDBC_ACTION_STORE_TABLE_PREFIX,
                        jdbcActionStoreTablePrefix),
                resourceVerifier -> resourceVerifier.verifyAttribute(TransactionFixtures.JDBC_ACTION_STORE_TABLE_PREFIX,
                        jdbcActionStoreTablePrefix));
    }

    @Test
    void toggleJDBCCommunicationStoreDropTable() throws Exception {
        boolean jdbcCommunicationStoreDropTable = operations.readAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS,
                TransactionFixtures.JDBC_COMMUNICATION_STORE_DROP_TABLE)
                .booleanValue();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getJdbcForm(),
                toggleWithUseJDBCStore(TransactionFixtures.JDBC_COMMUNICATION_STORE_DROP_TABLE,
                        !jdbcCommunicationStoreDropTable),
                resourceVerifier -> resourceVerifier.verifyAttribute(
                        TransactionFixtures.JDBC_COMMUNICATION_STORE_DROP_TABLE,
                        !jdbcCommunicationStoreDropTable));
    }

    @Test
    void editJDBCCommunicationStoreTablePrefix() throws Exception {
        String jdbcCommunicationStoreTablePrefix = Random.name();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getJdbcForm(),
                editWithUseJDBCStore(TransactionFixtures.JDBC_COMMUNICATION_STORE_TABLE_PREFIX,
                        jdbcCommunicationStoreTablePrefix),
                resourceVerifier -> resourceVerifier.verifyAttribute(
                        TransactionFixtures.JDBC_COMMUNICATION_STORE_TABLE_PREFIX,
                        jdbcCommunicationStoreTablePrefix));
    }

    @Test
    void toggleJDBCStoreStoreDropTable() throws Exception {
        boolean jdbcStateStoreDropTable = operations.readAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS,
                TransactionFixtures.JDBC_STATE_STORE_DROP_TABLE)
                .booleanValue();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getJdbcForm(),
                toggleWithUseJDBCStore(TransactionFixtures.JDBC_STATE_STORE_DROP_TABLE, !jdbcStateStoreDropTable),
                resourceVerifier -> resourceVerifier.verifyAttribute(TransactionFixtures.JDBC_STATE_STORE_DROP_TABLE,
                        !jdbcStateStoreDropTable));
    }

    @Test
    void editJDBCStoreStoreTablePrefix() throws Exception {
        String jdbcStateStoreTablePrefix = Random.name();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getJdbcForm(),
                editWithUseJDBCStore(TransactionFixtures.JDBC_STATE_STORE_TABLE_PREFIX,
                        jdbcStateStoreTablePrefix),
                resourceVerifier -> resourceVerifier.verifyAttribute(TransactionFixtures.JDBC_STATE_STORE_TABLE_PREFIX,
                        jdbcStateStoreTablePrefix));
    }

    private Consumer<FormFragment> toggleWithUseJDBCStore(String attributeName, boolean value) {
        return formFragment -> {
            if (value) {
                formFragment.flip(TransactionFixtures.USE_JDBC_STORE, value);
            }
            formFragment.flip(attributeName, value);
            if (value) {
                formFragment.text(TransactionFixtures.JDBC_STORE_DATASOURCE, TransactionFixtures.JDBC_DATASOURCE);
            }
        };
    }

    private Consumer<FormFragment> editWithUseJDBCStore(String attributeName, String value) {
        return formFragment -> {
            formFragment.flip(TransactionFixtures.USE_JDBC_STORE, true);
            formFragment.text(attributeName, value);
            formFragment.text(TransactionFixtures.JDBC_STORE_DATASOURCE, TransactionFixtures.JDBC_DATASOURCE);
        };
    }
}
