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
package org.jboss.hal.testsuite.test.configuration.remoting;

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
import org.jboss.hal.testsuite.page.configuration.RemotingPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.commands.socketbindings.AddSocketBinding;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.AUTHENTICATION_PROVIDER;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.CONNECTOR_CREATE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.CONNECTOR_DELETE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.CONNECTOR_READ;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.CONNECTOR_UPDATE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.connectorAddress;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.INBOUND_READ;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class ConnectorTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        client.apply(new AddSocketBinding.Builder(INBOUND_READ).build());
        operations.add(connectorAddress(CONNECTOR_READ), Values.of(SOCKET_BINDING, INBOUND_READ));
        operations.add(connectorAddress(CONNECTOR_UPDATE), Values.of(SOCKET_BINDING, INBOUND_READ));
        operations.add(connectorAddress(CONNECTOR_DELETE), Values.of(SOCKET_BINDING, INBOUND_READ));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page RemotingPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectSecondary("remoting-remote-connector-item",
                "remoting-connector-sub-item");
        table = page.getConnectorTable();
        form = page.getConnectorAttributesForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(connectorAddress(CONNECTOR_CREATE), table, form -> {
            form.text(NAME, CONNECTOR_CREATE);
            form.text(SOCKET_BINDING, INBOUND_READ);
        });
    }

    @Test
    void read() {
        table.select(CONNECTOR_READ);
        page.getConnectorTabs().select(Ids.REMOTING_CONNECTOR_TAB);
        form.showSensitive(SOCKET_BINDING);
        assertEquals(INBOUND_READ, form.value(SOCKET_BINDING));
    }

    @Test
    void update() throws Exception {
        table.select(CONNECTOR_UPDATE);
        page.getConnectorTabs().select(Ids.REMOTING_CONNECTOR_TAB);
        crud.update(connectorAddress(CONNECTOR_UPDATE), form, AUTHENTICATION_PROVIDER, Random.name());
    }

    @Test
    void reset() throws Exception {
        table.select(CONNECTOR_UPDATE);
        page.getConnectorTabs().select(Ids.REMOTING_CONNECTOR_TAB);
        crud.reset(connectorAddress(CONNECTOR_UPDATE), form);
    }
}
