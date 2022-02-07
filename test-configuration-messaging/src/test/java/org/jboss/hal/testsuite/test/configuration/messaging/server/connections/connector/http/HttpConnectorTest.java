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
package org.jboss.hal.testsuite.test.configuration.messaging.server.connections.connector.http;

import org.jboss.hal.resources.Ids;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ENDPOINT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.GROUP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTP_ACCEPTOR;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_CONNECTOR;
import static org.jboss.hal.resources.Ids.MESSAGING_HTTP_CONNECTOR;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_HTTP_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_HTTP_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_HTTP_TRY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_HTTP_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SERVER_NAME;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.connectorHttpAddress;

@Manatoko
@Testcontainers
class HttpConnectorTest extends AbstractServerConnectionsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        createServer(operations, SRV_UPDATE);
        operations.add(connectorHttpAddress(SRV_UPDATE, CONN_HTTP_UPDATE),
                Values.of(ENDPOINT, HTTP_ACCEPTOR).and(SOCKET_BINDING, HTTP)).assertSuccess();
        operations.add(connectorHttpAddress(SRV_UPDATE, CONN_HTTP_TRY_UPDATE),
                Values.of(ENDPOINT, HTTP_ACCEPTOR).and(SOCKET_BINDING, HTTP)).assertSuccess();
        operations.add(connectorHttpAddress(SRV_UPDATE, CONN_HTTP_DELETE),
                Values.of(ENDPOINT, DEFAULT).and(SOCKET_BINDING, HTTP)).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void connectorHttpCreate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_HTTP_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorHttpTable();
        FormFragment form = page.getConnectorHttpForm();
        table.bind(form);

        crudOperations.create(connectorHttpAddress(SRV_UPDATE, CONN_HTTP_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, CONN_HTTP_CREATE);
                    formFragment.text(ENDPOINT, HTTP_ACCEPTOR);
                    formFragment.text(SOCKET_BINDING, HTTP);
                });
    }

    @Test
    void connectorHttpTryCreate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_HTTP_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorHttpTable();
        FormFragment form = page.getConnectorHttpForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, CONN_HTTP_CREATE, ENDPOINT);
    }

    @Test
    void connectorHttpUpdate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_HTTP_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorHttpTable();
        FormFragment form = page.getConnectorHttpForm();
        table.bind(form);
        table.select(CONN_HTTP_UPDATE);
        crudOperations.update(connectorHttpAddress(SRV_UPDATE, CONN_HTTP_UPDATE), form, SERVER_NAME);
    }

    @Test
    void connectorHttpTryUpdate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_HTTP_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorHttpTable();
        FormFragment form = page.getConnectorHttpForm();
        table.bind(form);
        table.select(CONN_HTTP_TRY_UPDATE);
        crudOperations.updateWithError(form, f -> f.clear(ENDPOINT), ENDPOINT);
    }

    @Test
    void connectorHttpRemove() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_HTTP_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorHttpTable();
        FormFragment form = page.getConnectorHttpForm();
        table.bind(form);

        crudOperations.delete(connectorHttpAddress(SRV_UPDATE, CONN_HTTP_DELETE), table, CONN_HTTP_DELETE);
    }

}
