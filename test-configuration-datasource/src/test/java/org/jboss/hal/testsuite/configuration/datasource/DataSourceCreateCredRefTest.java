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
package org.jboss.hal.testsuite.configuration.datasource;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddCredentialStore;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.WizardFragment;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.ALIAS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CLEAR_TEXT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DATASOURCES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PASSWORD;
import static org.jboss.hal.dmr.ModelDescriptionConstants.READ_ALIASES_OPERATION;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STORE;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.DATA_SOURCE_CREATE_H2_UNIQUE;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.H2_PASSWORD;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.dataSourceAddress;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CREDENTIAL_STORE_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.credentialStoreAddress;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the creation of a new H2 database with a reference to a new alias in an existing credential store. The alias specified
 * during the creation of the database must be added to the existing credential store.
 */
@Manatoko
@Testcontainers
class DataSourceCreateCredRefTest {

    @Container static Browser browser = new Browser();

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);
    static final String H2_CSS_SELECTOR = "input[type=radio][name=template][value=h2]";
    static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        client.apply(new AddCredentialStore(CREDENTIAL_STORE_CREATE));
    }

    @Inject Console console;
    ColumnFragment column;

    @BeforeEach
    void prepare() {
        column = console.finder(NameTokens.CONFIGURATION, configurationSubsystemPath(DATASOURCES)
                .append(Ids.DATA_SOURCE_DRIVER, Ids.asId(Names.DATASOURCES)))
                .column(Ids.DATA_SOURCE_CONFIGURATION);
    }

    /**
     * Creates a new H2 database with a credential reference to an existing credential store. Verifies that the database has
     * been created and the alias is part of the credential store.
     */
    @Test
    void createH2() throws Exception {
        column.dropdownAction(Ids.DATA_SOURCE_ADD_ACTIONS, Ids.DATA_SOURCE_ADD);
        WizardFragment wizard = console.wizard();
        wizard.getRoot().findElement(By.cssSelector(H2_CSS_SELECTOR)).click();
        wizard.next(Ids.DATA_SOURCE_NAMES_FORM);
        FormFragment namesForms = wizard.getForm(Ids.DATA_SOURCE_NAMES_FORM);
        namesForms.text(NAME, DATA_SOURCE_CREATE_H2_UNIQUE);

        wizard.next(Ids.DATA_SOURCE_DRIVER_FORM);
        wizard.next(Ids.DATA_SOURCE_CONNECTION_FORM);
        String alias = Random.name();
        FormFragment connectionForm = wizard.getForm(Ids.DATA_SOURCE_CONNECTION_FORM);
        connectionForm.clear(PASSWORD);
        connectionForm.text(STORE, CREDENTIAL_STORE_CREATE);
        connectionForm.text(ALIAS, alias);
        connectionForm.text(CLEAR_TEXT, H2_PASSWORD);

        wizard.next(Ids.DATA_SOURCE_TEST_CONNECTION);
        wizard.next(Ids.DATA_SOURCE_REVIEW_FORM); // do nothing here
        wizard.finishStayOpen();
        wizard.verifySuccess();
        wizard.close();

        String itemId = Ids.dataSourceConfiguration(DATA_SOURCE_CREATE_H2_UNIQUE, false);
        assertTrue(column.containsItem(itemId));
        assertTrue(column.isSelected(itemId));
        Operations operations = new Operations(client);
        new ResourceVerifier(dataSourceAddress(DATA_SOURCE_CREATE_H2_UNIQUE), client).verifyExists();
        new ResourceVerifier(credentialStoreAddress(CREDENTIAL_STORE_CREATE), client).verifyTrue(
                "Alias not present in credential store",
                () -> {
                    ModelNodeResult result = operations.invoke(READ_ALIASES_OPERATION,
                            credentialStoreAddress(CREDENTIAL_STORE_CREATE));
                    if (result.isSuccess() && !result.value().asList().isEmpty()) {
                        String actualAlias = result.value().asList().get(0).asString();
                        return alias.equals(actualAlias);
                    }
                    return false;
                });
    }
}
