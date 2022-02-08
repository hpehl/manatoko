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
package org.jboss.hal.testsuite.test.configuration.messaging.server.connections.acceptor.remote;

import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
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
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_REM_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_REM_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_REM_TRY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_REM_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.acceptorRemoteAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class RemoteAcceptorTest extends AbstractServerConnectionsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        createServer(operations, SRV_UPDATE);
        operations.add(acceptorRemoteAddress(SRV_UPDATE, ACCP_REM_UPDATE), Values.of(SOCKET_BINDING, HTTP))
                .assertSuccess();
        operations.add(acceptorRemoteAddress(SRV_UPDATE, ACCP_REM_TRY_UPDATE), Values.of(SOCKET_BINDING, HTTP))
                .assertSuccess();
        operations.add(acceptorRemoteAddress(SRV_UPDATE, ACCP_REM_DELETE), Values.of(SOCKET_BINDING, HTTP))
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

        crudOperations.create(acceptorRemoteAddress(SRV_UPDATE, ACCP_REM_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, ACCP_REM_CREATE);
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

        crudOperations.createWithErrorAndCancelDialog(table, ACCP_REM_CREATE, SOCKET_BINDING);
    }

    @Test
    void acceptorRemoteUpdate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_REMOTE_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorRemoteTable();
        FormFragment form = page.getAcceptorRemoteForm();
        table.bind(form);
        table.select(ACCP_REM_UPDATE);
        crudOperations.update(acceptorRemoteAddress(SRV_UPDATE, ACCP_REM_UPDATE), form, SOCKET_BINDING, HTTPS);
    }

    @Test
    void acceptorRemoteTryUpdate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_REMOTE_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorRemoteTable();
        FormFragment form = page.getAcceptorRemoteForm();
        table.bind(form);
        table.select(ACCP_REM_TRY_UPDATE);
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

        crudOperations.delete(acceptorRemoteAddress(SRV_UPDATE, ACCP_REM_DELETE), table, ACCP_REM_DELETE);
    }

}
