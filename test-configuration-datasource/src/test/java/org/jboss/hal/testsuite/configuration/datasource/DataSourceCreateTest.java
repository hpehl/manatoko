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
package org.jboss.hal.testsuite.configuration.datasource;

import java.util.function.Consumer;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.creaper.ResourceVerifier;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.WizardFragment;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.testcontainers.junit.jupiter.Container;
import org.jboss.hal.testsuite.test.Manatoko;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.commands.datasources.AddDataSource;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.arquillian.graphene.Graphene.waitModel;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CONNECTION_URL;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DATASOURCES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DRIVER_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.JNDI_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PASSWORD;
import static org.jboss.hal.dmr.ModelDescriptionConstants.USER_NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.DATA_SOURCE_CREATE_CUSTOM;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.DATA_SOURCE_CREATE_EXISTING;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.DATA_SOURCE_CREATE_H2;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.DATA_SOURCE_CREATE_TEST_CANCEL;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.DATA_SOURCE_CREATE_TEST_CHANGE;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.DATA_SOURCE_CREATE_TEST_FINISH;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.H2_CONNECTION_URL;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.H2_DRIVER;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.H2_JNDI_NAME;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.H2_NAME;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.H2_PASSWORD;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.H2_USER_NAME;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.dataSourceAddress;
import static org.jboss.hal.testsuite.fixtures.DataSourceFixtures.h2ConnectionUrl;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Manatoko
@Testcontainers
class DataSourceCreateTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);
    static final String H2_CSS_SELECTOR = "input[type=radio][name=template][value=h2]";
    static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        client.apply(new AddDataSource.Builder<>(DATA_SOURCE_CREATE_EXISTING)
                .driverName("h2")
                .jndiName(Random.jndiName(DATA_SOURCE_CREATE_EXISTING))
                .connectionUrl(h2ConnectionUrl(DATA_SOURCE_CREATE_EXISTING))
                .build());
    }

    @Drone WebDriver browser;
    @Inject Console console;
    ColumnFragment column;

    @BeforeEach
    void prepare() {
        column = console.finder(NameTokens.CONFIGURATION, configurationSubsystemPath(DATASOURCES)
                .append(Ids.DATA_SOURCE_DRIVER, Ids.asId(Names.DATASOURCES)))
                .column(Ids.DATA_SOURCE_CONFIGURATION);
    }

    /**
     * Create a data source that already exists
     */
    @Test
    void createExisting() {
        column.dropdownAction(Ids.DATA_SOURCE_ADD_ACTIONS, Ids.DATA_SOURCE_ADD);
        WizardFragment wizard = console.wizard();
        wizard.getRoot().findElement(By.cssSelector(H2_CSS_SELECTOR)).click();
        wizard.next(Ids.DATA_SOURCE_NAMES_FORM);
        FormFragment namesForms = wizard.getForm(Ids.DATA_SOURCE_NAMES_FORM);
        namesForms.clear(NAME);
        namesForms.text(NAME, DATA_SOURCE_CREATE_EXISTING);
        wizard.next();

        namesForms.expectError(NAME);
        wizard.cancel();
    }

    /**
     * Create a custom H2 data source
     */
    @Test
    void createCustom() throws Exception {
        // choose template
        column.dropdownAction(Ids.DATA_SOURCE_ADD_ACTIONS, Ids.DATA_SOURCE_ADD);
        WizardFragment wizard = console.wizard();
        wizard.getRoot().findElement(By.cssSelector("input[type=radio][name=template][value=custom]")).click();
        wizard.next(Ids.DATA_SOURCE_NAMES_FORM);

        // attributes
        FormFragment namesForms = wizard.getForm(Ids.DATA_SOURCE_NAMES_FORM);
        namesForms.text(NAME, DATA_SOURCE_CREATE_CUSTOM);
        String jndiName = Random.jndiName(DATA_SOURCE_CREATE_CUSTOM);
        namesForms.text(JNDI_NAME, jndiName);
        wizard.next(Ids.DATA_SOURCE_DRIVER_FORM);

        // JDBC driver
        FormFragment driverForm = wizard.getForm(Ids.DATA_SOURCE_DRIVER_FORM);
        driverForm.text(DRIVER_NAME, H2_DRIVER);
        wizard.next(Ids.DATA_SOURCE_CONNECTION_FORM);

        // connection
        FormFragment connectionForm = wizard.getForm(Ids.DATA_SOURCE_CONNECTION_FORM);
        String connectionUrl = h2ConnectionUrl(DATA_SOURCE_CREATE_CUSTOM);
        connectionForm.text(CONNECTION_URL, connectionUrl);
        wizard.next(Ids.DATA_SOURCE_TEST_CONNECTION);

        // test connection
        wizard.next(Ids.DATA_SOURCE_REVIEW_FORM); // do nothing here

        // review
        FormFragment reviewForm = wizard.getForm(Ids.DATA_SOURCE_REVIEW_FORM);
        assertEquals(DATA_SOURCE_CREATE_CUSTOM, reviewForm.value(NAME));
        assertEquals(jndiName, reviewForm.value(JNDI_NAME));
        assertEquals(connectionUrl, reviewForm.value(CONNECTION_URL));
        assertEquals(H2_DRIVER, reviewForm.value(DRIVER_NAME));

        // do it
        wizard.finishStayOpen();
        wizard.verifySuccess();
        wizard.close();

        String itemId = Ids.dataSourceConfiguration(DATA_SOURCE_CREATE_CUSTOM, false);
        assertTrue(column.containsItem(itemId));
        assertTrue(column.isSelected(itemId));
        new ResourceVerifier(dataSourceAddress(DATA_SOURCE_CREATE_CUSTOM), client).verifyExists();
    }

    /**
     * Create a data source using the H2 template
     */
    @Test
    void createH2() throws Exception {
        column.dropdownAction(Ids.DATA_SOURCE_ADD_ACTIONS, Ids.DATA_SOURCE_ADD);
        WizardFragment wizard = console.wizard();
        wizard.getRoot().findElement(By.cssSelector(H2_CSS_SELECTOR)).click();
        wizard.next(Ids.DATA_SOURCE_NAMES_FORM);
        wizard.next(Ids.DATA_SOURCE_DRIVER_FORM);
        wizard.next(Ids.DATA_SOURCE_CONNECTION_FORM);
        wizard.next(Ids.DATA_SOURCE_TEST_CONNECTION);
        wizard.next(Ids.DATA_SOURCE_REVIEW_FORM); // do nothing here

        FormFragment reviewForm = wizard.getForm(Ids.DATA_SOURCE_REVIEW_FORM);
        reviewForm.showSensitive(USER_NAME);
        reviewForm.showSensitive(PASSWORD);
        assertEquals(H2_NAME, reviewForm.value(NAME));
        assertEquals(H2_JNDI_NAME, reviewForm.value(JNDI_NAME));
        assertEquals(H2_CONNECTION_URL, reviewForm.value(CONNECTION_URL));
        assertEquals(H2_DRIVER, reviewForm.value(DRIVER_NAME));
        assertEquals(H2_USER_NAME, reviewForm.value(USER_NAME));
        assertEquals(H2_PASSWORD, reviewForm.value(PASSWORD));

        wizard.finishStayOpen();
        wizard.verifySuccess();
        wizard.close();

        String itemId = Ids.dataSourceConfiguration(DATA_SOURCE_CREATE_H2, false);
        assertTrue(column.containsItem(itemId));
        assertTrue(column.isSelected(itemId));
        new ResourceVerifier(dataSourceAddress(DATA_SOURCE_CREATE_H2), client).verifyExists();
    }

    /**
     * Create a data source, test the connection and cancel the wizard
     */
    @Test
    void createTestCancel() throws Exception {
        Administration administration = new Administration(client);
        administration.reloadIfRequired();

        column.dropdownAction(Ids.DATA_SOURCE_ADD_ACTIONS, Ids.DATA_SOURCE_ADD);
        WizardFragment wizard = console.wizard();
        wizard.getRoot().findElement(By.cssSelector(H2_CSS_SELECTOR)).click();
        wizard.next(Ids.DATA_SOURCE_NAMES_FORM);
        FormFragment namesForms = wizard.getForm(Ids.DATA_SOURCE_NAMES_FORM);
        namesForms.clear(NAME);
        namesForms.text(NAME, DATA_SOURCE_CREATE_TEST_CANCEL);
        namesForms.clear(JNDI_NAME);
        namesForms.text(JNDI_NAME, Random.jndiName(DATA_SOURCE_CREATE_TEST_CANCEL));
        wizard.next(Ids.DATA_SOURCE_DRIVER_FORM);
        wizard.next(Ids.DATA_SOURCE_CONNECTION_FORM);
        wizard.next(Ids.DATA_SOURCE_TEST_CONNECTION);

        browser.findElement(By.id(Ids.DATA_SOURCE_TEST_CONNECTION)).click();
        wizard.verifySuccess(waitModel());
        handlePossibleException(wizard, WizardFragment::cancel, "not possible to click Cancel button in the wizard!");

        String itemId = Ids.dataSourceConfiguration(DATA_SOURCE_CREATE_TEST_CANCEL, false);
        Assertions.assertFalse(column.containsItem(itemId));
        new ResourceVerifier(dataSourceAddress(DATA_SOURCE_CREATE_TEST_CANCEL), client).verifyDoesNotExist();
    }

    /**
     * Create a data source, test the connection and finish the wizard
     */
    @Test
    void createTestFinish() throws Exception {
        column.dropdownAction(Ids.DATA_SOURCE_ADD_ACTIONS, Ids.DATA_SOURCE_ADD);
        WizardFragment wizard = console.wizard();
        wizard.getRoot().findElement(By.cssSelector(H2_CSS_SELECTOR)).click();
        wizard.next(Ids.DATA_SOURCE_NAMES_FORM);
        FormFragment namesForms = wizard.getForm(Ids.DATA_SOURCE_NAMES_FORM);
        namesForms.clear(NAME);
        namesForms.text(NAME, DATA_SOURCE_CREATE_TEST_FINISH);
        namesForms.clear(JNDI_NAME);
        String jndiName = Random.jndiName(DATA_SOURCE_CREATE_TEST_FINISH);
        namesForms.text(JNDI_NAME, jndiName);
        wizard.next(Ids.DATA_SOURCE_DRIVER_FORM);
        wizard.next(Ids.DATA_SOURCE_CONNECTION_FORM);
        wizard.next(Ids.DATA_SOURCE_TEST_CONNECTION);

        browser.findElement(By.id(Ids.DATA_SOURCE_TEST_CONNECTION)).click();
        wizard.verifySuccess(waitModel());
        try {
            wizard.next(Ids.DATA_SOURCE_REVIEW_FORM);
        } catch (TimeoutException e) {
            fail("HAL-1440: the Review page of the wizard didn't appear!");
            e.printStackTrace();
        }

        FormFragment reviewForm = wizard.getForm(Ids.DATA_SOURCE_REVIEW_FORM);
        reviewForm.showSensitive(USER_NAME);
        reviewForm.showSensitive(PASSWORD);
        assertEquals(DATA_SOURCE_CREATE_TEST_FINISH, reviewForm.value(NAME));
        assertEquals(jndiName, reviewForm.value(JNDI_NAME));
        assertEquals(H2_CONNECTION_URL, reviewForm.value(CONNECTION_URL));
        assertEquals(H2_DRIVER, reviewForm.value(DRIVER_NAME));
        assertEquals(H2_USER_NAME, reviewForm.value(USER_NAME));
        assertEquals(H2_PASSWORD, reviewForm.value(PASSWORD));

        wizard.finishStayOpen();
        wizard.verifySuccess();
        wizard.close();

        String itemId = Ids.dataSourceConfiguration(DATA_SOURCE_CREATE_TEST_FINISH, false);
        assertTrue(column.containsItem(itemId));
        assertTrue(column.isSelected(itemId));
        new ResourceVerifier(dataSourceAddress(DATA_SOURCE_CREATE_TEST_FINISH), client).verifyExists();
    }

    /**
     * Create a data source, test the connection, make changes and finish the wizard
     */
    @Test
    void createTestChange() throws Exception {
        column.dropdownAction(Ids.DATA_SOURCE_ADD_ACTIONS, Ids.DATA_SOURCE_ADD);
        WizardFragment wizard = console.wizard();
        wizard.getRoot().findElement(By.cssSelector(H2_CSS_SELECTOR)).click();
        wizard.next(Ids.DATA_SOURCE_NAMES_FORM);
        FormFragment namesForms = wizard.getForm(Ids.DATA_SOURCE_NAMES_FORM);
        namesForms.clear(NAME);
        namesForms.text(NAME, DATA_SOURCE_CREATE_TEST_CHANGE);
        namesForms.clear(JNDI_NAME);
        String jndiName = Random.jndiName(DATA_SOURCE_CREATE_TEST_CHANGE);
        namesForms.text(JNDI_NAME, jndiName);
        wizard.next(Ids.DATA_SOURCE_DRIVER_FORM);
        wizard.next(Ids.DATA_SOURCE_CONNECTION_FORM);
        wizard.next(Ids.DATA_SOURCE_TEST_CONNECTION);

        browser.findElement(By.id(Ids.DATA_SOURCE_TEST_CONNECTION)).click();
        wizard.verifySuccess(waitModel());
        handlePossibleException(wizard, wzd -> wzd.back(Ids.DATA_SOURCE_CONNECTION_FORM),
                "not possible to click Back button in the wizard!");

        FormFragment connectionForm = wizard.getForm(Ids.DATA_SOURCE_CONNECTION_FORM);
        connectionForm.clear(USER_NAME);
        connectionForm.text(USER_NAME, "changed");

        wizard.next(Ids.DATA_SOURCE_TEST_CONNECTION);
        wizard.next(Ids.DATA_SOURCE_REVIEW_FORM);

        FormFragment reviewForm = wizard.getForm(Ids.DATA_SOURCE_REVIEW_FORM);
        reviewForm.showSensitive(USER_NAME);
        reviewForm.showSensitive(PASSWORD);
        assertEquals(DATA_SOURCE_CREATE_TEST_CHANGE, reviewForm.value(NAME));
        assertEquals(jndiName, reviewForm.value(JNDI_NAME));
        assertEquals(H2_CONNECTION_URL, reviewForm.value(CONNECTION_URL));
        assertEquals(H2_DRIVER, reviewForm.value(DRIVER_NAME));
        assertEquals("changed", reviewForm.value(USER_NAME));
        assertEquals(H2_PASSWORD, reviewForm.value(PASSWORD));

        wizard.finishStayOpen();
        wizard.verifySuccess();
        wizard.close();

        String itemId = Ids.dataSourceConfiguration(DATA_SOURCE_CREATE_TEST_CHANGE, false);
        assertTrue(column.containsItem(itemId));
        assertTrue(column.isSelected(itemId));
        new ResourceVerifier(dataSourceAddress(DATA_SOURCE_CREATE_TEST_CHANGE), client)
                .verifyExists()
                .verifyAttribute(USER_NAME, "changed");
    }

    private void handlePossibleException(WizardFragment wizard, Consumer<WizardFragment> action, String failMessage) {
        try {
            action.accept(wizard);
        } catch (InvalidElementStateException e) {
            // clean up opened wizard first
            if (wizard.getRoot().isDisplayed()) {
                wizard.close();
            }
            fail("HAL-1440: " + failMessage);
            e.printStackTrace();
        }
    }
}
