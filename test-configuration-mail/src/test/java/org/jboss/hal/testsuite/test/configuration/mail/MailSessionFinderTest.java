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
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.creaper.ResourceVerifier;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
import org.jboss.hal.testsuite.page.Places;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import static org.jboss.hal.dmr.ModelDescriptionConstants.JNDI_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MAIL;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.SESSION_CREATE;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.SESSION_DELETE;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.SESSION_READ;
import static org.jboss.hal.testsuite.fixtures.MailFixtures.sessionAddress;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.junit.Assert.assertFalse;

@Manatoko
@Testcontainers
class MailSessionFinderTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(sessionAddress(SESSION_READ), Values.of(JNDI_NAME, Random.jndiName(SESSION_READ)));
        operations.add(sessionAddress(SESSION_DELETE), Values.of(JNDI_NAME, Random.jndiName(SESSION_DELETE)));
    }

    @Inject Console console;
    ColumnFragment column;

    @BeforeEach
    void setUp() {
        column = console.finder(NameTokens.CONFIGURATION, configurationSubsystemPath(MAIL))
                .column(Ids.MAIL_SESSION);
    }

    @Test
    void create() throws Exception {
        AddResourceDialogFragment dialog = column.add();
        dialog.getForm().text(NAME, SESSION_CREATE);
        dialog.getForm().text(JNDI_NAME, Random.jndiName(SESSION_CREATE));
        dialog.add();

        console.verifySuccess();
        new ResourceVerifier(sessionAddress(SESSION_CREATE), client)
                .verifyExists();
    }

    @Test
    void select() {
        column.selectItem(Ids.mailSession(SESSION_READ));
        PlaceRequest place = Places.finderPlace(NameTokens.CONFIGURATION,
                configurationSubsystemPath(MAIL).append(Ids.MAIL_SESSION, Ids.mailSession(SESSION_READ)));
        console.verify(place);
    }

    @Test
    void view() {
        column.selectItem(Ids.mailSession(SESSION_READ)).view();
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.MAIL_SESSION)
                .with(NAME, SESSION_READ)
                .build();
        console.verify(placeRequest);
    }

    @Test
    void delete() throws Exception {
        column.selectItem(Ids.mailSession(SESSION_DELETE))
                .dropdown()
                .click("Remove");
        console.confirmationDialog().confirm();

        console.verifySuccess();
        assertFalse(column.containsItem(Ids.mailSession(SESSION_DELETE)));
        new ResourceVerifier(sessionAddress(SESSION_DELETE), client)
                .verifyDoesNotExist();
    }
}
