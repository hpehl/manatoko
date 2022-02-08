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
package org.jboss.hal.testsuite.test.configuration.messaging.server;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.EmptyState;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.page.configuration.MessagingServerPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ALIAS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ATTRIBUTES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CLEAR_TEXT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.GROUP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MANAGEMENT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PATH;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SECURITY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STORE;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_SERVER;
import static org.jboss.hal.resources.Ids.MESSAGING_SERVER_BINDING_DIRECTORY;
import static org.jboss.hal.resources.Ids.MESSAGING_SERVER_DIRECTORY_ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_SERVER_JOURNAL_DIRECTORY;
import static org.jboss.hal.resources.Ids.MESSAGING_SERVER_LARGE_MESSAGES_DIRECTORY;
import static org.jboss.hal.resources.Ids.MESSAGING_SERVER_PAGING_DIRECTORY;
import static org.jboss.hal.resources.Ids.TAB;
import static org.jboss.hal.testsuite.Message.valueMustBeMasked;
import static org.jboss.hal.testsuite.Message.valueMustBeUnmasked;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.APPLICATION_DOMAIN;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BINDINGS_DIRECTORY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CLUSTER_CREDENTIAL_REFERENCE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONNECTION_TTL_OVERRIDE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ELYTRON_DOMAIN;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMX_DOMAIN;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JOURNAL_BINDING_TABLE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JOURNAL_DIRECTORY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JOURNAL_FILE_OPEN_TIMEOUT;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.LARGE_MESSAGES_DIRECTORY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.PAGING_DIRECTORY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.PERSISTENCE_ENABLED;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.serverAddress;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.serverPathAddress;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Manatoko
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled // TODO Fix failing tests
class ViewTest {

    private static final String ID_DELIMITER = "-";
    private static final String JOURNAL = "journal";

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        Batch batchSrvUpd = new Batch();
        batchSrvUpd.add(serverAddress(SRV_UPDATE));
        batchSrvUpd.add(serverPathAddress(SRV_UPDATE, BINDINGS_DIRECTORY), Values.of(PATH, Random.name()));
        batchSrvUpd.add(serverPathAddress(SRV_UPDATE, JOURNAL_DIRECTORY), Values.of(PATH, Random.name()));
        batchSrvUpd.add(serverPathAddress(SRV_UPDATE, LARGE_MESSAGES_DIRECTORY), Values.of(PATH, Random.name()));
        batchSrvUpd.add(serverPathAddress(SRV_UPDATE, PAGING_DIRECTORY), Values.of(PATH, Random.name()));
        operations.batch(batchSrvUpd);
    }

    @Page MessagingServerPage page;
    @Inject Console console;
    @Inject CrudOperations crudOperations;

    @BeforeEach
    void setUp() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    // --------------- attributes tab

    @Test
    void attributesConnectionTTLOverride() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, ATTRIBUTES, TAB));
        FormFragment form = page.getAttributesForm();

        crudOperations.update(serverAddress(SRV_UPDATE), form, CONNECTION_TTL_OVERRIDE, 123456L);
    }

    @Test
    void attributesPersistenceEnabled() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, ATTRIBUTES, TAB));
        FormFragment form = page.getAttributesForm();

        crudOperations.update(serverAddress(SRV_UPDATE), form, PERSISTENCE_ENABLED, false);
    }

    // --------------- management tab

    @Test
    void managementMaskJmxDomain() {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, GROUP, MANAGEMENT, TAB));
        FormFragment form = page.getManagementForm();
        String message = valueMustBeMasked(JMX_DOMAIN, form.value(JMX_DOMAIN));
        assertTrue(form.isMasked(JMX_DOMAIN), message);
    }

    @Test
    void managementUnmaskJmxDomain() {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, GROUP, MANAGEMENT, TAB));
        FormFragment form = page.getManagementForm();
        form.showSensitive(JMX_DOMAIN);
        String message = valueMustBeUnmasked(JMX_DOMAIN, form.value(JMX_DOMAIN));
        assertFalse(form.isMasked(JMX_DOMAIN), message);
    }

    // --------------- security tab

    @Test
    void securityUpdateElytronDomain() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, GROUP, SECURITY, TAB));
        FormFragment form = page.getSecurityForm();

        crudOperations.update(serverAddress(SRV_UPDATE), form, ELYTRON_DOMAIN, APPLICATION_DOMAIN);
    }

    // --------------- journal tab

    @Test
    void updateJournal() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, GROUP, JOURNAL, TAB));

        FormFragment form = page.getJournalForm();
        String tableName = Random.name();
        int timeout = Random.number(1, 59);
        crudOperations.update(serverAddress(SRV_UPDATE), form,
                formFragment -> {
                    formFragment.text(JOURNAL_BINDING_TABLE, tableName);
                    formFragment.number(JOURNAL_FILE_OPEN_TIMEOUT, timeout);
                }, verifier -> {
                    verifier.verifyAttribute(JOURNAL_BINDING_TABLE, tableName);
                    verifier.verifyAttribute(JOURNAL_FILE_OPEN_TIMEOUT, timeout);
                });
    }

    @Test
    void updateJournalFileOpenTimeoutNegative() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, GROUP, JOURNAL, TAB));
        FormFragment form = page.getJournalForm();
        crudOperations.update(serverAddress(SRV_UPDATE), form, JOURNAL_FILE_OPEN_TIMEOUT, -1);
    }

    @Test
    void updateJournalFileOpenTimeoutZero() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, GROUP, JOURNAL, TAB));
        FormFragment form = page.getJournalForm();
        crudOperations.update(serverAddress(SRV_UPDATE), form, JOURNAL_FILE_OPEN_TIMEOUT, 0);
    }

    @Test
    void updateJournalFileOpenTimeoutPositive() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, GROUP, JOURNAL, TAB));
        FormFragment form = page.getJournalForm();
        crudOperations.update(serverAddress(SRV_UPDATE), form, JOURNAL_FILE_OPEN_TIMEOUT, 123);
    }

    @Test
    void updateJournalFileOpenTimeoutInvalid() {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, GROUP, JOURNAL, TAB));
        FormFragment form = page.getJournalForm();
        crudOperations.updateWithError(form, JOURNAL_FILE_OPEN_TIMEOUT, "nan");
    }

    // --------------- cluster tab
    @Test
    void clusterPassword() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, GROUP, "cluster", TAB));
        FormFragment form = page.getClusterForm();
        String passwd = Random.name();
        crudOperations.update(serverAddress(SRV_UPDATE), form, "cluster-password", passwd);
    }

    // --------------- cluster credential reference tab
    @Test
    void clusterCredentialReferenceAddInvalid() {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, CLUSTER_CREDENTIAL_REFERENCE, TAB));
        EmptyState emptyState = page.getClusterCredentialReferenceEmptyState();
        waitGui().until().element(emptyState.getRoot()).is().visible();
        emptyState.mainAction();
        console.confirmationDialog().getPrimaryButton().click();
        AddResourceDialogFragment addResource = console.addResourceDialog();
        addResource.getForm().text(STORE, Random.name());
        console.waitNoNotification();
        addResource.getPrimaryButton().click();
        try {
            addResource.getForm().expectError(ALIAS);
        } finally {
            addResource.getSecondaryButton().click(); // close dialog to cleanup
        }
    }

    @Test
    void clusterCredentialReferenceAddSuccess() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, CLUSTER_CREDENTIAL_REFERENCE, TAB));
        EmptyState emptyState = page.getClusterCredentialReferenceEmptyState();
        waitGui().until().element(emptyState.getRoot()).is().visible();
        emptyState.mainAction();
        console.confirmationDialog().getPrimaryButton().click();

        String passwd = Random.name();
        AddResourceDialogFragment addResource = console.addResourceDialog();
        addResource.getForm().text(CLEAR_TEXT, passwd);
        addResource.add();

        console.verifySuccess();
        new ResourceVerifier(serverAddress(SRV_UPDATE), client)
                .verifyAttribute(CLUSTER_CREDENTIAL_REFERENCE + "." + CLEAR_TEXT, passwd);
    }

    @Test
    void clusterCredentialReferenceEditInvalid() {
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, CLUSTER_CREDENTIAL_REFERENCE, TAB));
        FormFragment form = page.getClusterCredentialReferenceForm();
        crudOperations.updateWithError(form, f -> f.text(ALIAS, Random.name()), STORE);
    }

    @Test
    void clusterCredentialReferenceRemove() throws Exception {
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(MESSAGING_SERVER + ID_DELIMITER + ITEM);
        page.getTab().select(Ids.build(MESSAGING_SERVER, CLUSTER_CREDENTIAL_REFERENCE, TAB));
        FormFragment form = page.getClusterCredentialReferenceForm();
        crudOperations.deleteSingleton(serverAddress(SRV_UPDATE), form,
                resourceVerifier -> resourceVerifier.verifyAttributeIsUndefined(CLUSTER_CREDENTIAL_REFERENCE));
    }

    // --------------- directory / Binding

    @Test
    void bindingDirectory1Update() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_BINDING_DIRECTORY, ITEM));

        FormFragment form = page.getBindingDirectoryForm();
        crudOperations.update(serverPathAddress(SRV_UPDATE, BINDINGS_DIRECTORY), form, PATH);
    }

    @Test
    void bindingDirectory2Reset() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_BINDING_DIRECTORY, Ids.ITEM));

        FormFragment form = page.getBindingDirectoryForm();
        crudOperations.reset(serverPathAddress(SRV_UPDATE, BINDINGS_DIRECTORY), form);
    }

    @Test
    void bindingDirectory3Remove() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_BINDING_DIRECTORY, Ids.ITEM));

        FormFragment form = page.getBindingDirectoryForm();
        crudOperations.deleteSingleton(serverPathAddress(SRV_UPDATE, BINDINGS_DIRECTORY), form);
    }

    @Test
    void bindingDirectory4Add() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_BINDING_DIRECTORY, Ids.ITEM));

        FormFragment form = page.getBindingDirectoryForm();
        crudOperations.createSingleton(serverPathAddress(SRV_UPDATE, BINDINGS_DIRECTORY), form);
    }

    // --------------- directory / Journal

    @Test
    void journalDirectory1Update() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_JOURNAL_DIRECTORY, ITEM));

        FormFragment form = page.getJournalDirectoryForm();
        crudOperations.update(serverPathAddress(SRV_UPDATE, JOURNAL_DIRECTORY), form, PATH);
    }

    @Test
    void journalDirectory2Reset() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_JOURNAL_DIRECTORY, Ids.ITEM));

        FormFragment form = page.getJournalDirectoryForm();
        crudOperations.reset(serverPathAddress(SRV_UPDATE, JOURNAL_DIRECTORY), form);
    }

    @Test
    void journalDirectory3Remove() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_JOURNAL_DIRECTORY, Ids.ITEM));

        FormFragment form = page.getJournalDirectoryForm();
        crudOperations.deleteSingleton(serverPathAddress(SRV_UPDATE, JOURNAL_DIRECTORY), form);
    }

    @Test
    void journalDirectory4Add() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_JOURNAL_DIRECTORY, Ids.ITEM));

        FormFragment form = page.getJournalDirectoryForm();
        if (!form.isBlank()) {
            form.remove();
        }
        crudOperations.createSingleton(serverPathAddress(SRV_UPDATE, JOURNAL_DIRECTORY), form);
    }

    // --------------- directory / Large Messages

    @Test
    void largeMessagesDirectory1Update() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_LARGE_MESSAGES_DIRECTORY, ITEM));

        FormFragment form = page.getLargeMessagesDirectoryForm();
        crudOperations.update(serverPathAddress(SRV_UPDATE, LARGE_MESSAGES_DIRECTORY), form, PATH);
    }

    @Test
    void largeMessagesDirectory2Reset() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_LARGE_MESSAGES_DIRECTORY, Ids.ITEM));

        FormFragment form = page.getLargeMessagesDirectoryForm();
        crudOperations.reset(serverPathAddress(SRV_UPDATE, LARGE_MESSAGES_DIRECTORY), form);
    }

    @Test
    void largeMessagesDirectory3Remove() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_LARGE_MESSAGES_DIRECTORY, Ids.ITEM));

        FormFragment form = page.getLargeMessagesDirectoryForm();
        crudOperations.deleteSingleton(serverPathAddress(SRV_UPDATE, LARGE_MESSAGES_DIRECTORY), form);
    }

    @Test
    void largeMessagesDirectory4Add() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_LARGE_MESSAGES_DIRECTORY, Ids.ITEM));

        FormFragment form = page.getLargeMessagesDirectoryForm();
        crudOperations.createSingleton(serverPathAddress(SRV_UPDATE, LARGE_MESSAGES_DIRECTORY), form);
    }

    // --------------- directory / Paging

    @Test
    void pagingDirectory1Update() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_PAGING_DIRECTORY, ITEM));

        FormFragment form = page.getPagingDirectoryForm();
        crudOperations.update(serverPathAddress(SRV_UPDATE, PAGING_DIRECTORY), form, PATH);
    }

    @Test
    void pagingDirectory2Reset() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_PAGING_DIRECTORY, Ids.ITEM));

        FormFragment form = page.getPagingDirectoryForm();
        crudOperations.reset(serverPathAddress(SRV_UPDATE, PAGING_DIRECTORY), form);
    }

    @Test
    void pagingDirectory3Remove() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_PAGING_DIRECTORY, Ids.ITEM));

        FormFragment form = page.getPagingDirectoryForm();
        crudOperations.deleteSingleton(serverPathAddress(SRV_UPDATE, PAGING_DIRECTORY), form);
    }

    @Test
    void pagingDirectory4Add() throws Exception {
        console.verticalNavigation()
                .selectSecondary(MESSAGING_SERVER_DIRECTORY_ITEM,
                        Ids.build(MESSAGING_SERVER_PAGING_DIRECTORY, Ids.ITEM));

        FormFragment form = page.getPagingDirectoryForm();
        crudOperations.createSingleton(serverPathAddress(SRV_UPDATE, PAGING_DIRECTORY), form);
    }
}
