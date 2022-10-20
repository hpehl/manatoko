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
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.MailPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.IMAP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.JNDI_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.OUTBOUND_SOCKET_BINDING_REF;
import static org.jboss.hal.dmr.ModelDescriptionConstants.POP3;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SMTP;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.MAIL_SMTP;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.SESSION_CREATE;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.serverAddress;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.sessionAddress;

@Manatoko
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
class MailServerCreateTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(sessionAddress(SESSION_CREATE), Values.of(JNDI_NAME, Random.jndiName(SESSION_CREATE)));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page MailPage page;
    TableFragment table;

    @BeforeEach
    void prepare() {
        page.navigate(NAME, SESSION_CREATE);
        console.verticalNavigation().selectPrimary(Ids.MAIL_SERVER_ITEM);
        table = page.getMailServerTable();
    }

    @Test
    void createIMAP() throws Exception {
        createServer(IMAP);
    }

    @Test
    void createPOP3() throws Exception {
        createServer(POP3);
    }

    @Test
    void createSMTP() throws Exception {
        createServer(SMTP);
    }

    private void createServer(String type) throws Exception {
        crud.create(serverAddress(SESSION_CREATE, type), table,
                form -> form.text(OUTBOUND_SOCKET_BINDING_REF, MAIL_SMTP));
    }
}
