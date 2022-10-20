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
package org.jboss.hal.testsuite.test.configuration.transaction;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.TransactionFixtures;
import org.jboss.hal.testsuite.page.configuration.TransactionPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;

@Manatoko
@Testcontainers
class ConfigurationTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);
    private static Operations operations;

    @BeforeAll
    static void setupModel() {
        operations = new Operations(wildFly.managementClient());
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page TransactionPage page;

    @BeforeEach
    public void prepare() {
        page.navigate();
        console.verticalNavigation()
                .selectPrimary(Ids.build("tx", "attributes", "config", "item"));
    }

    @Test
    public void editDefaultTimeout() throws Exception {
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getConfigurationForm(),
                TransactionFixtures.DEFAULT_TIMEOUT,
                Random.number());
    }

    @Test
    public void toggleEnableTsmStatus() throws Exception {
        boolean enableTsmStatus = operations
                .readAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS, TransactionFixtures.ENABLE_TSM_STATUS)
                .booleanValue();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getConfigurationForm(),
                TransactionFixtures.ENABLE_TSM_STATUS,
                !enableTsmStatus);
    }

    @Test
    public void toggleJournalStoreEnableAsyncIO() throws Exception {
        boolean journalStoreEnableAsyncIO = operations.readAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS,
                TransactionFixtures.JOURNAL_STORE_ENABLE_ASYNC_IO)
                .booleanValue();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getConfigurationForm(), formFragment -> {
            formFragment.flip(TransactionFixtures.USE_JOURNAL_STORE, !journalStoreEnableAsyncIO);
            formFragment.flip(TransactionFixtures.JOURNAL_STORE_ENABLE_ASYNC_IO, !journalStoreEnableAsyncIO);
        }, resourceVerifier -> resourceVerifier.verifyAttribute(TransactionFixtures.JOURNAL_STORE_ENABLE_ASYNC_IO,
                !journalStoreEnableAsyncIO));
    }

    @Test
    public void toggleJTS() throws Exception {
        boolean jts = operations.readAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS, TransactionFixtures.JTS)
                .booleanValue();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getConfigurationForm(),
                TransactionFixtures.JTS,
                !jts);
    }

    @Test
    public void editNodeIdentifier() throws Exception {
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getConfigurationForm(), "node-identifier",
                Random.name());
    }

    // TODO: recent wildfly uses an expression for this value and the flip operation should switch to normal mode
    // before flipping
    @Test
    @Disabled
    public void toggleStatisticsEnabled() throws Exception {
        boolean statisticsEnabled = operations
                .readAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS, TransactionFixtures.STATISTICS_ENABLED)
                .booleanValue();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getConfigurationForm(),
                TransactionFixtures.STATISTICS_ENABLED,
                !statisticsEnabled);
    }

    @Test
    public void toggleUseJournalStore() throws Exception {
        boolean useJournalStore = operations
                .readAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS, TransactionFixtures.USE_JOURNAL_STORE)
                .booleanValue();
        boolean journalStoreEnableAsyncIO = operations.readAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS,
                TransactionFixtures.JOURNAL_STORE_ENABLE_ASYNC_IO)
                .booleanValue();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getConfigurationForm(), formFragment -> {
            formFragment.flip(TransactionFixtures.USE_JOURNAL_STORE, !useJournalStore);
            if (journalStoreEnableAsyncIO) {
                formFragment.flip(TransactionFixtures.JOURNAL_STORE_ENABLE_ASYNC_IO, false);
            }
        }, resourceVerifier -> resourceVerifier.verifyAttribute(TransactionFixtures.USE_JOURNAL_STORE,
                !useJournalStore));
    }
}
