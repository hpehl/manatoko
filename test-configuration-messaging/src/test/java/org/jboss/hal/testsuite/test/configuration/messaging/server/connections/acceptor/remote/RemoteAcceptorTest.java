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
package org.jboss.hal.testsuite.test.configuration.messaging.server.connections.acceptor.remote;

import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.command.AddMessagingServer;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.test.configuration.messaging.server.connections.AbstractServerConnectionsTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.GROUP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTPS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_ACCEPTOR;
import static org.jboss.hal.resources.Ids.MESSAGING_REMOTE_ACCEPTOR;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCEPTOR_REMOTE_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCEPTOR_REMOTE_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCEPTOR_REMOTE_TRY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCEPTOR_REMOTE_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.acceptorRemoteAddress;

@Manatoko
@Testcontainers
class RemoteAcceptorTest extends AbstractServerConnectionsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(acceptorRemoteAddress(SRV_UPDATE, ACCEPTOR_REMOTE_UPDATE), Values.of(SOCKET_BINDING, HTTP))
                .assertSuccess();
        operations.add(acceptorRemoteAddress(SRV_UPDATE, ACCEPTOR_REMOTE_TRY_UPDATE), Values.of(SOCKET_BINDING, HTTP))
                .assertSuccess();
        operations.add(acceptorRemoteAddress(SRV_UPDATE, ACCEPTOR_REMOTE_DELETE), Values.of(SOCKET_BINDING, HTTP))
                .assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void acceptorRemoteCreate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_REMOTE_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorRemoteTable();
        FormFragment form = page.getAcceptorRemoteForm();
        table.bind(form);

        crudOperations.create(acceptorRemoteAddress(SRV_UPDATE, ACCEPTOR_REMOTE_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, ACCEPTOR_REMOTE_CREATE);
                    formFragment.text(SOCKET_BINDING, HTTP);
                });
    }

    @Test
    void acceptorRemoteTryCreate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_REMOTE_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorRemoteTable();
        FormFragment form = page.getAcceptorRemoteForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, ACCEPTOR_REMOTE_CREATE, SOCKET_BINDING);
    }

    @Test
    void acceptorRemoteUpdate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_REMOTE_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorRemoteTable();
        FormFragment form = page.getAcceptorRemoteForm();
        table.bind(form);
        table.select(ACCEPTOR_REMOTE_UPDATE);
        crudOperations.update(acceptorRemoteAddress(SRV_UPDATE, ACCEPTOR_REMOTE_UPDATE), form, SOCKET_BINDING, HTTPS);
    }

    @Test
    void acceptorRemoteTryUpdate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_REMOTE_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorRemoteTable();
        FormFragment form = page.getAcceptorRemoteForm();
        table.bind(form);
        table.select(ACCEPTOR_REMOTE_TRY_UPDATE);
        crudOperations.updateWithError(form, f -> f.clear(SOCKET_BINDING), SOCKET_BINDING);
    }

    @Test
    void acceptorRemoteRemove() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_REMOTE_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorRemoteTable();
        FormFragment form = page.getAcceptorRemoteForm();
        table.bind(form);

        crudOperations.delete(acceptorRemoteAddress(SRV_UPDATE, ACCEPTOR_REMOTE_DELETE), table, ACCEPTOR_REMOTE_DELETE);
    }

}
