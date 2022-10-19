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
package org.jboss.hal.testsuite.test.configuration.messaging.server.connections.pooled.connection.factory;

import org.jboss.dmr.ModelNode;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddMessagingServer;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.EmptyState;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.test.configuration.messaging.server.connections.AbstractServerConnectionsTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.hal.dmr.ModelDescriptionConstants.ALIAS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ATTRIBUTES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CLEAR_TEXT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CONNECTORS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CREDENTIAL_REFERENCE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DISCOVERY_GROUP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.EE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ENTRIES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PASSWORD;
import static org.jboss.hal.dmr.ModelDescriptionConstants.POOLED_CONNECTION_FACTORY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STORE;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_SERVER;
import static org.jboss.hal.resources.Ids.TAB;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CALL_TIMEOUT;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_CHANNEL;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_DG_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.POOL_CONN_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.POOL_CONN_CREATE_ENTRY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.POOL_CONN_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.POOL_CONN_TRY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.POOL_CONN_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.connectionFactoryAddress;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.jgroupsDiscoveryGroupAddress;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.pooledConnectionFactoryAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class PooledConnectionFactoryTest extends AbstractServerConnectionsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();
    private static OnlineManagementClient client;
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        operations = new Operations(client);
        operations.add(jgroupsDiscoveryGroupAddress(SRV_UPDATE, JGROUPS_DG_UPDATE), Values.of(JGROUPS_CHANNEL, EE))
                .assertSuccess();
        new Administration(client).reloadIfRequired();
        operations.add(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE),
                Values.ofList(ENTRIES, Random.name()).and(DISCOVERY_GROUP, JGROUPS_DG_UPDATE)).assertSuccess();
        operations.add(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_TRY_UPDATE),
                Values.ofList(ENTRIES, Random.name()).and(DISCOVERY_GROUP, JGROUPS_DG_UPDATE)).assertSuccess();
        operations.add(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_DELETE),
                Values.ofList(ENTRIES, Random.name()).and(DISCOVERY_GROUP, JGROUPS_DG_UPDATE)).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void pooledConnectionFactoryCreate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ITEM));
        TableFragment table = page.getPooledConnectionFactoryTable();
        FormFragment form = page.getPooledConnectionFactoryForm();
        table.bind(form);
        crudOperations.create(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, POOL_CONN_CREATE);
                    formFragment.text(DISCOVERY_GROUP, JGROUPS_DG_UPDATE);
                    formFragment.list(ENTRIES).add(POOL_CONN_CREATE_ENTRY);
                });
    }

    @Test
    void pooledConnectionFactoryTryCreate() {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ITEM));
        TableFragment table = page.getPooledConnectionFactoryTable();
        FormFragment form = page.getPooledConnectionFactoryForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, POOL_CONN_CREATE, DISCOVERY_GROUP);
    }

    @Test
    void pooledConnectionFactoryUpdate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ITEM));
        page.getPooledFormsTab().select(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ATTRIBUTES, TAB));

        TableFragment table = page.getPooledConnectionFactoryTable();
        FormFragment form = page.getPooledConnectionFactoryForm();
        table.bind(form);
        table.select(POOL_CONN_UPDATE);
        crudOperations.update(connectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), form,
                formFragment -> {
                    formFragment.number(CALL_TIMEOUT, 123L);
                    formFragment.flip("use-topology-for-load-balancing", false);
                },
                verifier -> {
                    verifier.verifyAttribute(CALL_TIMEOUT, 123L);
                    verifier.verifyAttribute("use-topology-for-load-balancing", false);
                });

    }

    @Test
    void pooledConnectionFactoryTryUpdate() {
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ITEM));
        TableFragment table = page.getPooledConnectionFactoryTable();
        FormFragment form = page.getPooledConnectionFactoryForm();
        table.bind(form);
        table.select(POOL_CONN_TRY_UPDATE);
        crudOperations.updateWithError(form, f -> f.list(CONNECTORS).add(Random.name()), DISCOVERY_GROUP);
    }

    @Test
    void pooledConnectionFactoryRemove() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ITEM));
        TableFragment table = page.getPooledConnectionFactoryTable();
        FormFragment form = page.getPooledConnectionFactoryForm();
        table.bind(form);

        crudOperations.delete(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_DELETE), table, POOL_CONN_DELETE);
    }

    // tests the credential-reference form

    @Test
    void pooledConnectionFactoryTryAddCredentialReferenceRequires() throws Exception {
        operations.undefineAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), PASSWORD);
        operations.undefineAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE),
                CREDENTIAL_REFERENCE);
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ITEM));

        TableFragment table = page.getPooledConnectionFactoryTable();
        FormFragment form = page.getPooledConnectionFactoryCRForm();
        table.bind(form);
        table.select(POOL_CONN_UPDATE);
        // the order of UI navigation is important
        // first select the table item, then navigate to the tab
        page.getPooledFormsTab().select(CREDENTIAL_REFERENCE_TAB);
        form.emptyState().mainAction();

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
    void pooledConnectionFactoryTryAddCredentialReferenceEmpty() throws Exception {
        operations.undefineAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), PASSWORD);
        operations.undefineAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE),
                CREDENTIAL_REFERENCE);
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ITEM));

        TableFragment table = page.getPooledConnectionFactoryTable();
        FormFragment form = page.getPooledConnectionFactoryCRForm();
        EmptyState emptyState = form.emptyState();
        table.bind(form);
        table.select(POOL_CONN_UPDATE);
        page.getPooledFormsTab().select(CREDENTIAL_REFERENCE_TAB);
        emptyState.mainAction();

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
    void pooledConnectionFactoryTryAddCredentialReferenceAlternatives() throws Exception {
        operations.undefineAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), PASSWORD);
        operations.undefineAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE),
                CREDENTIAL_REFERENCE);
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ITEM));

        TableFragment table = page.getPooledConnectionFactoryTable();
        FormFragment form = page.getPooledConnectionFactoryCRForm();
        EmptyState emptyState = form.emptyState();
        table.bind(form);
        table.select(POOL_CONN_UPDATE);
        page.getPooledFormsTab().select(CREDENTIAL_REFERENCE_TAB);
        emptyState.mainAction();

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
    void pooledConnectionFactoryAddCredentialReference() throws Exception {
        operations.undefineAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), PASSWORD);
        operations.undefineAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE),
                CREDENTIAL_REFERENCE);
        // navigate again, to reload the page as new data were added with the operations above
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ITEM));

        TableFragment table = page.getPooledConnectionFactoryTable();
        FormFragment form = page.getPooledConnectionFactoryCRForm();
        EmptyState emptyState = form.emptyState();
        table.bind(form);
        table.select(POOL_CONN_UPDATE);
        page.getPooledFormsTab().select(CREDENTIAL_REFERENCE_TAB);
        emptyState.mainAction();
        String clearText = Random.name();
        AddResourceDialogFragment addResource = console.addResourceDialog();
        addResource.getForm().text(CLEAR_TEXT, clearText);
        addResource.add();

        console.verifySuccess();
        new ResourceVerifier(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), client)
                .verifyAttribute(CREDENTIAL_REFERENCE + "." + CLEAR_TEXT, clearText);
    }

    @Test
    void pooledConnectionFactoryTryUpdateCredentialReferenceAlternatives() throws Exception {
        operations.undefineAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), PASSWORD);
        ModelNode cr = new ModelNode();
        cr.get(CLEAR_TEXT).set(Random.name());
        operations.writeAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), CREDENTIAL_REFERENCE,
                cr);
        // navigate again, to reload the page as new data were added with the operations above
        page.navigateAgain(SERVER, SRV_UPDATE);

        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ITEM));
        page.getPooledFormsTab().select(CREDENTIAL_REFERENCE_TAB);

        TableFragment table = page.getPooledConnectionFactoryTable();
        FormFragment form = page.getPooledConnectionFactoryCRForm();
        table.bind(form);
        table.select(POOL_CONN_UPDATE);

        crudOperations.updateWithError(form, f -> f.text(STORE, Random.name()), STORE);
    }

    @Test
    void pooledConnectionFactoryTryUpdateCredentialReferenceEmpty() throws Exception {
        operations.undefineAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), PASSWORD);
        ModelNode cr = new ModelNode();
        cr.get(CLEAR_TEXT).set(Random.name());
        operations.writeAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), CREDENTIAL_REFERENCE,
                cr);
        // navigate again, to reload the page as new data were added with the operations above
        page.navigateAgain(SERVER, SRV_UPDATE);

        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ITEM));
        page.getPooledFormsTab().select(CREDENTIAL_REFERENCE_TAB);

        TableFragment table = page.getPooledConnectionFactoryTable();
        FormFragment form = page.getPooledConnectionFactoryCRForm();
        table.bind(form);
        table.select(POOL_CONN_UPDATE);

        crudOperations.updateWithError(form, f -> f.clear(CLEAR_TEXT), STORE, CLEAR_TEXT);
    }

    @Test
    void pooledConnectionFactoryRemoveCredentialReference() throws Exception {
        operations.undefineAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), PASSWORD);
        ModelNode cr = new ModelNode();
        cr.get(CLEAR_TEXT).set(Random.name());
        operations.writeAttribute(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), CREDENTIAL_REFERENCE,
                cr);
        // navigate again, to reload the page as new data were added with the operations above
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_SERVER, POOLED_CONNECTION_FACTORY, ITEM));
        page.getPooledFormsTab().select(CREDENTIAL_REFERENCE_TAB);

        TableFragment table = page.getPooledConnectionFactoryTable();
        FormFragment form = page.getPooledConnectionFactoryCRForm();
        table.bind(form);
        table.select(POOL_CONN_UPDATE);

        crudOperations.deleteSingleton(pooledConnectionFactoryAddress(SRV_UPDATE, POOL_CONN_UPDATE), form,
                resourceVerifier -> resourceVerifier.verifyAttributeIsUndefined(CREDENTIAL_REFERENCE));
    }
}
