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
package org.jboss.hal.testsuite.test.configuration.remoting;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.RemotingPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.commands.socketbindings.AddSocketBinding;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.CONNECTOR_REF;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.HTTP_CONNECTOR_CREATE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.HTTP_CONNECTOR_CREATE_LISTENER;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.HTTP_CONNECTOR_CREATE_SOCKET;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.HTTP_CONNECTOR_DELETE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.HTTP_CONNECTOR_DELETE_LISTENER;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.HTTP_CONNECTOR_DELETE_SOCKET;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.HTTP_CONNECTOR_READ;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.HTTP_CONNECTOR_READ_LISTENER;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.HTTP_CONNECTOR_READ_SOCKET;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.HTTP_CONNECTOR_UPDATE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.HTTP_CONNECTOR_UPDATE_LISTENER;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.HTTP_CONNECTOR_UPDATE_SOCKET;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.httpConnectorAddress;
import static org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures.DEFAULT_SERVER;
import static org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures.httpListenerAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class HttpConnectorTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        /*
         * HTTP connector needs valid HTTP listener with socket from EAP 7.3.4 1. Create socket, 2. Add listener to undertow
         * server and 3. Create connector.
         */
        client.apply(new AddSocketBinding.Builder(HTTP_CONNECTOR_CREATE_SOCKET).build());
        client.apply(new AddSocketBinding.Builder(HTTP_CONNECTOR_READ_SOCKET).build());
        client.apply(new AddSocketBinding.Builder(HTTP_CONNECTOR_UPDATE_SOCKET).build());
        client.apply(new AddSocketBinding.Builder(HTTP_CONNECTOR_DELETE_SOCKET).build());

        operations.add(httpListenerAddress(DEFAULT_SERVER, HTTP_CONNECTOR_CREATE_LISTENER),
                Values.of(SOCKET_BINDING, HTTP_CONNECTOR_CREATE_SOCKET)).assertSuccess();
        operations.add(httpListenerAddress(DEFAULT_SERVER, HTTP_CONNECTOR_READ_LISTENER),
                Values.of(SOCKET_BINDING, HTTP_CONNECTOR_READ_SOCKET)).assertSuccess();
        operations.add(httpListenerAddress(DEFAULT_SERVER, HTTP_CONNECTOR_UPDATE_LISTENER),
                Values.of(SOCKET_BINDING, HTTP_CONNECTOR_UPDATE_SOCKET)).assertSuccess();
        operations.add(httpListenerAddress(DEFAULT_SERVER, HTTP_CONNECTOR_DELETE_LISTENER),
                Values.of(SOCKET_BINDING, HTTP_CONNECTOR_DELETE_SOCKET)).assertSuccess();

        operations.add(httpConnectorAddress(HTTP_CONNECTOR_READ),
                Values.of(CONNECTOR_REF, HTTP_CONNECTOR_READ_LISTENER)).assertSuccess();
        operations.add(httpConnectorAddress(HTTP_CONNECTOR_UPDATE),
                Values.of(CONNECTOR_REF, HTTP_CONNECTOR_UPDATE_LISTENER)).assertSuccess();
        operations.add(httpConnectorAddress(HTTP_CONNECTOR_DELETE),
                Values.of(CONNECTOR_REF, HTTP_CONNECTOR_DELETE_LISTENER)).assertSuccess();
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page RemotingPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary("remoting-remote-connector-item",
                "remoting-http-connector-sub-item");
        table = page.getHttpConnectorTable();
        form = page.getHttpConnectorAttributesForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(httpConnectorAddress(HTTP_CONNECTOR_CREATE), table, form -> {
            form.text(NAME, HTTP_CONNECTOR_CREATE);
            form.text(CONNECTOR_REF, HTTP_CONNECTOR_CREATE_LISTENER);
        });
    }

    @Test
    void read() {
        table.select(HTTP_CONNECTOR_READ);
        page.getHttpConnectorTabs().select(Ids.REMOTING_HTTP_CONNECTOR_TAB);
        assertEquals(HTTP_CONNECTOR_READ_LISTENER, form.value(CONNECTOR_REF));
    }

    @Test
    void update() throws Exception {
        table.select(HTTP_CONNECTOR_UPDATE);
        page.getHttpConnectorTabs().select(Ids.REMOTING_HTTP_CONNECTOR_TAB);
        crud.update(httpConnectorAddress(HTTP_CONNECTOR_UPDATE), form, CONNECTOR_REF, Random.name());
    }

    @Test
    void reset() throws Exception {
        table.select(HTTP_CONNECTOR_UPDATE);
        page.getHttpConnectorTabs().select(Ids.REMOTING_HTTP_CONNECTOR_TAB);
        crud.reset(httpConnectorAddress(HTTP_CONNECTOR_UPDATE), form);
    }
}
