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
package org.jboss.hal.testsuite.test.configuration.messaging.server.clustering;

import org.jboss.dmr.ModelNode;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddMessagingServer;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.EmptyState;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.ALIAS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ATTRIBUTES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.BRIDGE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CLEAR_TEXT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CREDENTIAL_REFERENCE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DISCOVERY_GROUP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTP_CONNECTOR;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PASSWORD;
import static org.jboss.hal.dmr.ModelDescriptionConstants.QUEUE_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STATIC_CONNECTORS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STORE;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_SERVER;
import static org.jboss.hal.resources.Ids.TAB;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BRIDGE_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BRIDGE_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BRIDGE_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CHECK_PERIOD;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.bridgeAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class BridgeTest extends AbstractClusteringTest {

    private static final Values BRIDGE_PARAMS = Values.of(QUEUE_NAME, Random.name())
            .andList(STATIC_CONNECTORS, HTTP_CONNECTOR);

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);
    private static OnlineManagementClient client;
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        operations = new Operations(client);
        operations.add(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), BRIDGE_PARAMS).assertSuccess();
        operations.add(bridgeAddress(SRV_UPDATE, BRIDGE_DELETE), BRIDGE_PARAMS).assertSuccess();
    }

    @BeforeEach
    void navigate() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void bridgeCreate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, BRIDGE, ITEM));
        TableFragment table = page.getBridgeTable();
        FormFragment form = page.getBridgeForm();
        table.bind(form);

        crudOperations.create(bridgeAddress(SRV_UPDATE, BRIDGE_CREATE), table, f -> {
            f.text(NAME, BRIDGE_CREATE);
            f.text(QUEUE_NAME, Random.name());
            f.text(DISCOVERY_GROUP, Random.name());
        });
    }

    @Test
    void bridgeTryCreate() {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, BRIDGE, ITEM));
        TableFragment table = page.getBridgeTable();
        FormFragment form = page.getBridgeForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, f -> f.text(NAME, BRIDGE_CREATE), QUEUE_NAME);
    }

    @Test
    void bridgeUpdate() throws Exception {
        operations.undefineAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), CREDENTIAL_REFERENCE);
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, BRIDGE, ITEM));
        TableFragment table = page.getBridgeTable();
        FormFragment form = page.getBridgeForm();
        table.bind(form);
        table.select(BRIDGE_UPDATE);
        page.getBridgeFormsTab().select(Ids.build(MESSAGING_SERVER, BRIDGE, ATTRIBUTES, TAB));
        crudOperations.update(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), form, CHECK_PERIOD, 123L);
    }

    @Test
    void bridgeRemove() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, BRIDGE, ITEM));
        TableFragment table = page.getBridgeTable();
        FormFragment form = page.getBridgeForm();
        table.bind(form);

        crudOperations.delete(bridgeAddress(SRV_UPDATE, BRIDGE_DELETE), table, BRIDGE_DELETE);
    }

    // tests the credential-reference form of bridge

    @Test
    void bridgeTryAddCredentialReferenceRequires() throws Exception {
        operations.undefineAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), PASSWORD);
        operations.undefineAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), CREDENTIAL_REFERENCE);
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, BRIDGE, ITEM));

        TableFragment table = page.getBridgeTable();
        FormFragment form = page.getBridgeCRForm();
        table.bind(form);
        table.select(BRIDGE_UPDATE);
        // the order of UI navigation is important
        // first select the table item, then navigate to the tab
        page.getBridgeFormsTab().select(CREDENTIAL_REFERENCE_TAB);
        form.emptyState().mainAction();
        console.confirmationDialog().getPrimaryButton().click();

        AddResourceDialogFragment addResource = console.addResourceDialog();
        addResource.getForm().text(STORE, Random.name());
        addResource.getPrimaryButton().click();
        try {
            addResource.getForm().expectError(ALIAS);
        } finally {
            addResource.getSecondaryButton().click(); // close dialog to cleanup
        }
    }

    @Test
    void bridgeTryAddCredentialReferenceEmpty() throws Exception {
        operations.undefineAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), PASSWORD);
        operations.undefineAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), CREDENTIAL_REFERENCE);
        page.navigate(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, BRIDGE, ITEM));

        TableFragment table = page.getBridgeTable();
        FormFragment form = page.getBridgeCRForm();
        EmptyState emptyState = form.emptyState();
        table.bind(form);
        table.select(BRIDGE_UPDATE);
        page.getBridgeFormsTab().select(CREDENTIAL_REFERENCE_TAB);
        emptyState.mainAction();
        console.confirmationDialog().getPrimaryButton().click();

        AddResourceDialogFragment addResource = console.addResourceDialog();
        addResource.getPrimaryButton().click();
        try {
            addResource.getForm().expectError(STORE);
            addResource.getForm().expectError(CLEAR_TEXT);
        } finally {
            addResource.getSecondaryButton().click(); // close dialog to cleanup
        }
    }

    @Test
    void bridgeTryAddCredentialReferenceAlternatives() throws Exception {
        operations.undefineAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), PASSWORD);
        operations.undefineAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), CREDENTIAL_REFERENCE);
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, BRIDGE, ITEM));

        TableFragment table = page.getBridgeTable();
        FormFragment form = page.getBridgeCRForm();
        EmptyState emptyState = form.emptyState();
        table.bind(form);
        table.select(BRIDGE_UPDATE);
        page.getBridgeFormsTab().select(CREDENTIAL_REFERENCE_TAB);
        emptyState.mainAction();
        console.confirmationDialog().getPrimaryButton().click();

        AddResourceDialogFragment addResource = console.addResourceDialog();
        addResource.getForm().text(STORE, Random.name());
        addResource.getForm().text(CLEAR_TEXT, Random.name());
        addResource.getPrimaryButton().click();
        try {
            addResource.getForm().expectError(STORE);
            addResource.getForm().expectError(CLEAR_TEXT);
        } finally {
            addResource.getSecondaryButton().click(); // close dialog to cleanup
        }
    }

    @Test
    void bridgeAddCredentialReference() throws Exception {
        operations.undefineAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), PASSWORD);
        operations.undefineAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), CREDENTIAL_REFERENCE);
        // navigate again, to reload the page as new data were added with the operations above

        page.navigateAgain(SERVER, SRV_UPDATE);
        String clearText = Random.name();
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, BRIDGE, ITEM));
        TableFragment table = page.getBridgeTable();
        FormFragment form = page.getBridgeCRForm();
        EmptyState emptyState = form.emptyState();
        table.bind(form);
        table.select(BRIDGE_UPDATE);
        page.getBridgeFormsTab().select(CREDENTIAL_REFERENCE_TAB);
        emptyState.mainAction();
        console.confirmationDialog().getPrimaryButton().click();

        AddResourceDialogFragment addResource = console.addResourceDialog();
        addResource.getForm().text(CLEAR_TEXT, clearText);
        addResource.add();

        console.verifySuccess();
        new ResourceVerifier(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), client)
                .verifyAttribute(CREDENTIAL_REFERENCE + "." + CLEAR_TEXT, clearText);

    }

    @Test
    void bridgeTryUpdateCredentialReferenceAlternatives() throws Exception {
        operations.undefineAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), PASSWORD);
        ModelNode cr = new ModelNode();
        cr.get(CLEAR_TEXT).set(Random.name());
        operations.writeAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), CREDENTIAL_REFERENCE, cr);
        // navigate again, to reload the page as new data were added with the operations above
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, BRIDGE, ITEM));

        TableFragment table = page.getBridgeTable();
        FormFragment form = page.getBridgeCRForm();
        table.bind(form);
        table.select(BRIDGE_UPDATE);
        page.getBridgeFormsTab().select(CREDENTIAL_REFERENCE_TAB);

        crudOperations.updateWithError(form, f -> f.text(STORE, Random.name()), STORE);
    }

    @Test
    void bridgeTryUpdateCredentialReferenceEmpty() throws Exception {
        operations.undefineAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), PASSWORD);
        ModelNode cr = new ModelNode();
        cr.get(CLEAR_TEXT).set(Random.name());
        operations.writeAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), CREDENTIAL_REFERENCE, cr);
        // navigate again, to reload the page as new data were added with the operations above
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, BRIDGE, ITEM));

        TableFragment table = page.getBridgeTable();
        FormFragment form = page.getBridgeCRForm();
        table.bind(form);
        table.select(BRIDGE_UPDATE);
        page.getBridgeFormsTab().select(CREDENTIAL_REFERENCE_TAB);

        crudOperations.updateWithError(form, f -> f.clear(CLEAR_TEXT), STORE, CLEAR_TEXT);
    }

    @Test
    void bridgeRemoveCredentialReference() throws Exception {
        operations.undefineAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), PASSWORD);
        ModelNode cr = new ModelNode();
        cr.get(CLEAR_TEXT).set(Random.name());
        operations.writeAttribute(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), CREDENTIAL_REFERENCE, cr);
        // navigate again, to reload the page as new data were added with the operations above
        page.navigateAgain(SERVER, SRV_UPDATE);

        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, BRIDGE, ITEM));

        TableFragment table = page.getBridgeTable();
        FormFragment form = page.getBridgeCRForm();
        table.bind(form);
        table.select(BRIDGE_UPDATE);
        page.getBridgeFormsTab().select(CREDENTIAL_REFERENCE_TAB);

        crudOperations.deleteSingleton(bridgeAddress(SRV_UPDATE, BRIDGE_UPDATE), form,
                resourceVerifier -> resourceVerifier.verifyAttributeIsUndefined(CREDENTIAL_REFERENCE));
    }

}
