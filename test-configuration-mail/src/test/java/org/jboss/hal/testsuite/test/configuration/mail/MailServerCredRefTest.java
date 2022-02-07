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
package org.jboss.hal.testsuite.test.configuration.mail;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.dmr.CredentialReference;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.MailPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.ALIAS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CLEAR_TEXT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CREDENTIAL_REFERENCE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.JNDI_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.OUTBOUND_SOCKET_BINDING_REF;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SMTP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STORE;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.MAIL_SMTP;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.SESSION_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.serverAddress;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.sessionAddress;

@Manatoko
@Testcontainers
class MailServerCredRefTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(sessionAddress(SESSION_UPDATE), Values.of(JNDI_NAME, Random.jndiName(SESSION_UPDATE)));
        operations.add(serverAddress(SESSION_UPDATE, SMTP), Values.of(OUTBOUND_SOCKET_BINDING_REF, MAIL_SMTP));
        operations.writeAttribute(serverAddress(SESSION_UPDATE, SMTP),
                CREDENTIAL_REFERENCE, CredentialReference.storeAlias());
    }

    @Inject Console console;
    @Page MailPage page;
    @Inject CrudOperations crud;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate(NAME, SESSION_UPDATE);
        console.waitNoNotification();
        console.verticalNavigation().selectPrimary(Ids.MAIL_SERVER_ITEM);

        page.getMailServerTabs().select(Ids.build(Ids.MAIL_SERVER, CREDENTIAL_REFERENCE, Ids.TAB));
        form = page.getMailServerCrForm();
        page.getMailServerTable().bind(form);
        page.getMailServerTable().select(SMTP.toUpperCase());
    }

    @Test
    void updateAliasWithoutStore() {
        crud.updateWithError(form, f -> {
            f.clear(STORE);
            f.text(ALIAS, Random.name());
            f.clear(CLEAR_TEXT);
        }, STORE);
    }

    @Test
    void updateStoreWithoutAlias() {
        crud.updateWithError(form, f -> {
            f.text(STORE, Random.name());
            f.clear(ALIAS);
            f.clear(CLEAR_TEXT);
        }, ALIAS);
    }

    @Test
    void updateStoreAndClearText() {
        crud.updateWithError(form, f -> {
            f.text(STORE, Random.name());
            f.text(ALIAS, Random.name());
            f.text(CLEAR_TEXT, Random.name());
        }, STORE, CLEAR_TEXT);
    }

    @Test
    void updateStoreAndAlias() throws Exception {
        String store = Ids.build(STORE, Random.name());
        String alias = Ids.build(ALIAS, Random.name());

        crud.update(serverAddress(SESSION_UPDATE, SMTP), form,
                f -> {
                    f.text(STORE, store);
                    f.text(ALIAS, alias);
                    f.clear(CLEAR_TEXT);
                },
                resourceVerifier -> {
                    resourceVerifier.verifyAttribute(CredentialReference.fqName(STORE), store);
                    resourceVerifier.verifyAttribute(CredentialReference.fqName(ALIAS), alias);
                });
    }

    @Test
    void updateClearText() throws Exception {
        String clearText = Ids.build(CLEAR_TEXT, Random.name());

        crud.update(serverAddress(SESSION_UPDATE, SMTP), form,
                f -> {
                    f.clear(STORE);
                    f.clear(ALIAS);
                    f.text(CLEAR_TEXT, clearText);
                },
                resourceVerifier -> resourceVerifier.verifyAttribute(CredentialReference.fqName(CLEAR_TEXT),
                        clearText));
    }

    @Test
    void zzzDelete() throws Exception {
        crud.deleteSingleton(serverAddress(SESSION_UPDATE, SMTP), form,
                resourceVerifier -> resourceVerifier.verifyAttributeIsUndefined(CREDENTIAL_REFERENCE));
    }

}
