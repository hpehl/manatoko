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
package org.jboss.hal.testsuite.test.configuration.socketbinding;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.SocketBindingPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.HOST;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PORT;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.LOCALHOST;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.OUTBOUND_REMOTE_CREATE;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.OUTBOUND_REMOTE_DELETE;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.OUTBOUND_REMOTE_PORT;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.OUTBOUND_REMOTE_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.SOURCE_PORT;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.STANDARD_SOCKETS;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.outboundRemoteAddress;

@Manatoko
@Testcontainers
class OutboundRemoteTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(outboundRemoteAddress(STANDARD_SOCKETS, OUTBOUND_REMOTE_UPDATE),
                Values.of(HOST, LOCALHOST).and(PORT, OUTBOUND_REMOTE_PORT));
        operations.add(outboundRemoteAddress(STANDARD_SOCKETS, OUTBOUND_REMOTE_DELETE),
                Values.of(HOST, LOCALHOST).and(PORT, OUTBOUND_REMOTE_PORT + 1));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page SocketBindingPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate(NAME, STANDARD_SOCKETS);
        console.verticalNavigation().selectPrimary(Ids.SOCKET_BINDING_GROUP_OUTBOUND_REMOTE + "-" + Ids.ITEM);

        table = page.getOutboundRemoteTable();
        form = page.getOutboundRemoteForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(outboundRemoteAddress(STANDARD_SOCKETS, OUTBOUND_REMOTE_CREATE), table, form -> {
            form.text(NAME, OUTBOUND_REMOTE_CREATE);
            form.text(HOST, LOCALHOST);
            form.number(PORT, OUTBOUND_REMOTE_PORT - 1);
        });
    }

    @Test
    void update() throws Exception {
        table.select(OUTBOUND_REMOTE_UPDATE);
        crud.update(outboundRemoteAddress(STANDARD_SOCKETS, OUTBOUND_REMOTE_UPDATE), form, PORT, 1234);
    }

    @Test
    void updateInvalidPort() {
        table.select(OUTBOUND_REMOTE_UPDATE);
        crud.updateWithError(form, SOURCE_PORT, -1);
    }

    @Test
    void delete() throws Exception {
        crud.delete(outboundRemoteAddress(STANDARD_SOCKETS, OUTBOUND_REMOTE_DELETE), table, OUTBOUND_REMOTE_DELETE);
    }
}
