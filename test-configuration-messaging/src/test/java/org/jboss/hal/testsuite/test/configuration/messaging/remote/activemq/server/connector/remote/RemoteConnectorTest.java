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
package org.jboss.hal.testsuite.test.configuration.messaging.remote.activemq.server.connector.remote;

import java.io.IOException;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddLocalSocketBinding;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.model.ModelNodeGenerator;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.page.configuration.MessagingRemoteActiveMQPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.RemoteActiveMQServer;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class RemoteConnectorTest {

    private static final String REMOTE_CONNECTOR_CREATE = "remote-connector-to-create-" + Random.name();
    private static final String REMOTE_CONNECTOR_UPDATE = "remote-connector-to-update-" + Random.name();
    private static final String REMOTE_CONNECTOR_DELETE = "remote-connector-to-delete-" + Random.name();

    private static final String LOCAL_SOCKET_BINDING = "local-socket-binding-" + Random.name();
    private static final String LOCAL_SOCKET_BINDING_UPDATE = "local-socket-binding-update-" + Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, FULL_HA);
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        client.apply(new AddLocalSocketBinding(LOCAL_SOCKET_BINDING));
        client.apply(new AddLocalSocketBinding(LOCAL_SOCKET_BINDING_UPDATE));
        createRemoteConnector(REMOTE_CONNECTOR_DELETE);
        createRemoteConnector(REMOTE_CONNECTOR_UPDATE);
    }

    private static void createRemoteConnector(String name) throws IOException {
        operations.add(RemoteActiveMQServer.remoteConnectorAddress(name), Values.of("socket-binding",
                RemoteConnectorTest.LOCAL_SOCKET_BINDING))
                .assertSuccess();
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page MessagingRemoteActiveMQPage page;

    @BeforeEach
    void navigate() {
        page.navigate();
        console.verticalNavigation()
                .selectSecondary("msg-remote-connector-group-item", "msg-remote-remote-connector-item");
    }

    @Test
    void create() throws Exception {
        crudOperations.create(RemoteActiveMQServer.remoteConnectorAddress(REMOTE_CONNECTOR_CREATE),
                page.getRemoteConnectorTable(),
                formFragment -> {
                    formFragment.text(ModelDescriptionConstants.NAME, REMOTE_CONNECTOR_CREATE);
                    formFragment.text("socket-binding", LOCAL_SOCKET_BINDING);
                }, ResourceVerifier::verifyExists);
    }

    @Test
    void remove() throws Exception {
        crudOperations.delete(RemoteActiveMQServer.remoteConnectorAddress(REMOTE_CONNECTOR_DELETE),
                page.getRemoteConnectorTable(), REMOTE_CONNECTOR_DELETE);
    }

    @Test
    void editParams() throws Exception {
        page.getRemoteConnectorTable().select(REMOTE_CONNECTOR_UPDATE);
        crudOperations.update(RemoteActiveMQServer.remoteConnectorAddress(REMOTE_CONNECTOR_UPDATE),
                page.getRemoteConnectorForm(), "params",
                new ModelNodeGenerator.ModelNodePropertiesBuilder().addProperty(Random.name(), Random.name()).build());
    }

    @Test
    void editSocketBinding() throws Exception {
        page.getRemoteConnectorTable().select(REMOTE_CONNECTOR_UPDATE);
        crudOperations.update(RemoteActiveMQServer.remoteConnectorAddress(REMOTE_CONNECTOR_UPDATE),
                page.getRemoteConnectorForm(), "socket-binding", LOCAL_SOCKET_BINDING_UPDATE);
    }
}
