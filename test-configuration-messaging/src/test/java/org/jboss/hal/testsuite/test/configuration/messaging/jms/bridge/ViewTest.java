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
package org.jboss.hal.testsuite.test.configuration.messaging.jms.bridge;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.dmr.ModelNode;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddJmsBridge;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.EmptyState;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.page.configuration.MessagingJmsBridgePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CLEAR_TEXT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MODULE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SELECTOR;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOURCE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOURCE_CREDENTIAL_REFERENCE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOURCE_PASSWORD;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TARGET;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TARGET_CREDENTIAL_REFERENCE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TARGET_PASSWORD;
import static org.jboss.hal.resources.Ids.JMS_BRIDGE;
import static org.jboss.hal.resources.Ids.TAB;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMS_BRIDGE_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMS_BRIDGE_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.jmsBridgeAddress;

@Manatoko
@Testcontainers
class ViewTest {

    private static final String NESTED_ATTRIBUTE_DELIMITER = ".";
    private static final String anyString = Random.name();
    private static Operations operations;
    private static OnlineManagementClient client;

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        operations = new Operations(client);
        client.apply(new AddJmsBridge(JMS_BRIDGE_UPDATE), new AddJmsBridge(JMS_BRIDGE_DELETE));
    }

    @Page MessagingJmsBridgePage page;
    @Inject Console console;
    @Inject CrudOperations crudOperations;

    @Test
    public void editAttribute() throws Exception {
        page.navigateAgain(NAME, JMS_BRIDGE_UPDATE);
        FormFragment form = page.getAttributesForm();
        crudOperations.update(jmsBridgeAddress(JMS_BRIDGE_UPDATE), form, SELECTOR, anyString);
    }

    @Test
    public void tryEditAttribute() {
        page.navigateAgain(NAME, JMS_BRIDGE_UPDATE);
        FormFragment form = page.getAttributesForm();
        crudOperations.updateWithError(form, f -> f.clear(MODULE), MODULE);
    }

    @Test
    public void editSourceAttribute() throws Exception {
        page.navigateAgain(NAME, JMS_BRIDGE_UPDATE);
        page.getTabs().select(Ids.build(JMS_BRIDGE, SOURCE, TAB));
        FormFragment form = page.getSourceForm();
        crudOperations.update(jmsBridgeAddress(JMS_BRIDGE_UPDATE), form, "source-user", anyString);
    }

    @Test
    public void addSourceCredentialReference() throws Exception {
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), SOURCE_PASSWORD);
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), SOURCE_CREDENTIAL_REFERENCE);
        page.navigateAgain(NAME, JMS_BRIDGE_UPDATE);
        page.getTabs().select(Ids.build(JMS_BRIDGE, SOURCE_CREDENTIAL_REFERENCE, TAB));
        EmptyState emptyState = page.getSourceCredentialReferenceForm().emptyState();
        emptyState.mainAction();
        AddResourceDialogFragment dialog = console.addResourceDialog();
        dialog.getForm().text(CLEAR_TEXT, anyString);
        dialog.add();
        console.verifySuccess();
        new ResourceVerifier(jmsBridgeAddress(JMS_BRIDGE_UPDATE), client).verifyAttribute(
                SOURCE_CREDENTIAL_REFERENCE + NESTED_ATTRIBUTE_DELIMITER + CLEAR_TEXT, anyString);
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), SOURCE_CREDENTIAL_REFERENCE);
    }

    @Test
    public void tryAddSourceCredentialReference() throws Exception {
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), SOURCE_PASSWORD);
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), SOURCE_CREDENTIAL_REFERENCE);
        page.navigate(NAME, JMS_BRIDGE_UPDATE);
        page.getTabs().select(Ids.build(JMS_BRIDGE, SOURCE_CREDENTIAL_REFERENCE, TAB));
        EmptyState emptyState = page.getSourceCredentialReferenceForm().emptyState();
        emptyState.mainAction();
        AddResourceDialogFragment dialog = console.addResourceDialog();
        dialog.getPrimaryButton().click();
        dialog.getForm().expectError(CLEAR_TEXT);
        dialog.getSecondaryButton().click();
    }

    @Test
    public void editSourceCredentialReference() throws Exception {
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), SOURCE_PASSWORD);
        ModelNode cr = new ModelNode();
        cr.get(CLEAR_TEXT).set(anyString);
        operations.writeAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), SOURCE_CREDENTIAL_REFERENCE, cr);
        page.navigateAgain(NAME, JMS_BRIDGE_UPDATE);
        page.getTabs().select(Ids.build(JMS_BRIDGE, SOURCE_CREDENTIAL_REFERENCE, TAB));
        FormFragment form = page.getSourceCredentialReferenceForm();
        String randomText = Random.name();
        crudOperations.update(jmsBridgeAddress(JMS_BRIDGE_UPDATE), form, f -> f.text(CLEAR_TEXT, randomText),
                verifier -> verifier.verifyAttribute(
                        SOURCE_CREDENTIAL_REFERENCE + NESTED_ATTRIBUTE_DELIMITER + CLEAR_TEXT,
                        randomText));
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), SOURCE_CREDENTIAL_REFERENCE);
    }

    @Test
    public void editSourcePasswordWhenCRExists() throws Exception {
        ModelNode cr = new ModelNode();
        cr.get(CLEAR_TEXT).set(anyString);
        operations.writeAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), SOURCE_CREDENTIAL_REFERENCE, cr);
        page.navigateAgain(NAME, JMS_BRIDGE_UPDATE);
        page.getTabs().select(Ids.build(JMS_BRIDGE, SOURCE, TAB));
        FormFragment form = page.getSourceForm();
        form.edit();
        form.text(SOURCE_PASSWORD, anyString);
        form.trySave();
        form.expectError(SOURCE_PASSWORD);
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), SOURCE_CREDENTIAL_REFERENCE);
    }

    @Test
    public void editCRWhenSourcePasswordExists() throws Exception {
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), SOURCE_CREDENTIAL_REFERENCE);
        operations.writeAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), SOURCE_PASSWORD, anyString);
        page.navigate(NAME, JMS_BRIDGE_UPDATE);
        page.getTabs().select(Ids.build(JMS_BRIDGE, SOURCE_CREDENTIAL_REFERENCE, TAB));
        EmptyState emptyState = page.getSourceCredentialReferenceForm().emptyState();
        emptyState.mainAction();
        // there should be a confirmation dialog asking to undefine the source-password attribute
        console.confirmationDialog().getSecondaryButton().click();
    }

    @Test
    public void editTargetAttribute() throws Exception {
        page.navigate(NAME, JMS_BRIDGE_UPDATE);
        page.getTabs().select(Ids.build(JMS_BRIDGE, "target", TAB));
        FormFragment form = page.getTargetForm();
        crudOperations.update(jmsBridgeAddress(JMS_BRIDGE_UPDATE), form, "target-user", anyString);
    }

    @Test
    public void addTargetCredentialReference() throws Exception {
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), TARGET_CREDENTIAL_REFERENCE);
        page.navigateAgain(NAME, JMS_BRIDGE_UPDATE);
        page.getTabs().select(Ids.build(JMS_BRIDGE, TARGET_CREDENTIAL_REFERENCE, TAB));
        EmptyState emptyState = page.getTargetCredentialReferenceForm().emptyState();
        emptyState.mainAction();
        AddResourceDialogFragment dialog = console.addResourceDialog();
        dialog.getForm().text(CLEAR_TEXT, anyString);
        dialog.add();
        console.verifySuccess();
        new ResourceVerifier(jmsBridgeAddress(JMS_BRIDGE_UPDATE), client).verifyAttribute(
                TARGET_CREDENTIAL_REFERENCE + NESTED_ATTRIBUTE_DELIMITER + CLEAR_TEXT, anyString);
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), TARGET_CREDENTIAL_REFERENCE);
    }

    @Test
    public void tryAddTargetCredentialReference() throws Exception {
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), TARGET_CREDENTIAL_REFERENCE);
        page.navigateAgain(NAME, JMS_BRIDGE_UPDATE);
        page.getTabs().select(Ids.build(JMS_BRIDGE, TARGET_CREDENTIAL_REFERENCE, TAB));
        EmptyState emptyState = page.getTargetCredentialReferenceForm().emptyState();
        emptyState.mainAction();
        AddResourceDialogFragment dialog = console.addResourceDialog();
        dialog.getPrimaryButton().click();
        dialog.getForm().expectError(CLEAR_TEXT);
        dialog.getSecondaryButton().click();
    }

    @Test
    public void editTargetCredentialReference() throws Exception {
        ModelNode cr = new ModelNode();
        cr.get(CLEAR_TEXT).set(anyString);
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), TARGET_PASSWORD);
        operations.writeAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), TARGET_CREDENTIAL_REFERENCE, cr);
        page.navigateAgain(NAME, JMS_BRIDGE_UPDATE);
        page.getTabs().select(Ids.build(JMS_BRIDGE, TARGET_CREDENTIAL_REFERENCE, TAB));
        FormFragment form = page.getTargetCredentialReferenceForm();
        String randomText = Random.name();
        crudOperations.update(jmsBridgeAddress(JMS_BRIDGE_UPDATE), form, f -> f.text(CLEAR_TEXT, randomText),
                v -> v.verifyAttribute(TARGET_CREDENTIAL_REFERENCE + NESTED_ATTRIBUTE_DELIMITER + CLEAR_TEXT,
                        randomText));
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), TARGET_CREDENTIAL_REFERENCE);
    }

    @Test
    public void editTargetPasswordWhenCRExists() throws Exception {
        ModelNode cr = new ModelNode();
        cr.get(CLEAR_TEXT).set(anyString);
        operations.writeAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), TARGET_CREDENTIAL_REFERENCE, cr);
        page.navigateAgain(NAME, JMS_BRIDGE_UPDATE);
        page.getTabs().select(Ids.build(JMS_BRIDGE, TARGET, TAB));
        FormFragment form = page.getTargetForm();
        form.edit();
        form.text(TARGET_PASSWORD, anyString);
        form.trySave();
        form.expectError(TARGET_PASSWORD);
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), TARGET_CREDENTIAL_REFERENCE);
    }

    @Test
    public void editCRWhenTargetPasswordExists() throws Exception {
        operations.undefineAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), TARGET_CREDENTIAL_REFERENCE);
        operations.writeAttribute(jmsBridgeAddress(JMS_BRIDGE_UPDATE), TARGET_PASSWORD, anyString);
        page.navigate(NAME, JMS_BRIDGE_UPDATE);
        page.getTabs().select(Ids.build(JMS_BRIDGE, TARGET_CREDENTIAL_REFERENCE, TAB));
        EmptyState emptyState = page.getTargetCredentialReferenceForm().emptyState();
        emptyState.mainAction();
        // there should be a confirmation dialog asking to undefine the target-password attribute
        console.confirmationDialog().getSecondaryButton().click();
    }
}
