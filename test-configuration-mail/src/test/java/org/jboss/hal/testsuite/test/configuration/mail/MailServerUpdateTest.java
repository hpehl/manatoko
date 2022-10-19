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
package org.jboss.hal.testsuite.test.configuration.mail;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.model.CredentialReference;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.CREDENTIAL_REFERENCE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.JNDI_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.OUTBOUND_SOCKET_BINDING_REF;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PASSWORD;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SMTP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.USERNAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.MAIL_SMTP;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.SECRET;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.SESSION_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.serverAddress;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.sessionAddress;

@Manatoko
@Testcontainers
class MailServerUpdateTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @Container static Browser browser = new Browser();

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
    @Inject CrudOperations crud;
    @Page MailPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate(NAME, SESSION_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.MAIL_SERVER_ITEM);
        table = page.getMailServerTable();
        form = page.getMailServerAttributesForm();
        table.bind(form);
    }

    @Test
    void reset() {
        table.select(SMTP.toUpperCase());
    }

    @Test
    void update() throws Exception {
        table.select(SMTP.toUpperCase());
        crud.update(serverAddress(SESSION_UPDATE, SMTP), form, USERNAME, SECRET);
    }

    @Test
    void updateConflictWithCredRef() {
        table.select(SMTP.toUpperCase());
        crud.updateWithError(form, PASSWORD, SECRET);
    }
}
