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
package org.jboss.hal.testsuite.test.configuration.messaging.remote.activemq.server.connector.in.vm;

import java.io.IOException;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.creaper.ResourceVerifier;
import org.jboss.hal.testsuite.dmr.ModelNodeGenerator;
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
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.RemoteActiveMQServer;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class InVMConnectorTest {

    private static final String IN_VM_CONNECTOR_CREATE = "in-vm-connector-to-create-" + Random.name();
    private static final String IN_VM_CONNECTOR_UPDATE = "in-vm-connector-to-update-" + Random.name();
    private static final String IN_VM_CONNECTOR_DELETE = "in-vm-connector-to-delete-" + Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        createInVMConnector(IN_VM_CONNECTOR_DELETE);
        createInVMConnector(IN_VM_CONNECTOR_UPDATE);
    }

    static void createInVMConnector(String name) throws IOException {
        operations.add(RemoteActiveMQServer.inVMConnectorAddress(name), Values.of("server-id", Random.number()))
                .assertSuccess();
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page MessagingRemoteActiveMQPage page;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation()
                .selectSecondary("msg-remote-connector-group-item", "msg-remote-in-vm-connector-item");
    }

    @Test
    void create() throws Exception {
        crudOperations.create(RemoteActiveMQServer.inVMConnectorAddress(IN_VM_CONNECTOR_CREATE),
                page.getInVMConnectorTable(),
                formFragment -> {
                    formFragment.text(ModelDescriptionConstants.NAME, IN_VM_CONNECTOR_CREATE);
                    formFragment.number("server-id", Random.number());
                }, ResourceVerifier::verifyExists);
    }

    @Test
    void remove() throws Exception {
        crudOperations.delete(RemoteActiveMQServer.inVMConnectorAddress(IN_VM_CONNECTOR_DELETE),
                page.getInVMConnectorTable(), IN_VM_CONNECTOR_DELETE);
    }

    @Test
    void editParams() throws Exception {
        page.getInVMConnectorTable().select(IN_VM_CONNECTOR_UPDATE);
        crudOperations.update(RemoteActiveMQServer.inVMConnectorAddress(IN_VM_CONNECTOR_UPDATE),
                page.getInVMConnectorForm(), "params",
                new ModelNodeGenerator.ModelNodePropertiesBuilder().addProperty(Random.name(), Random.name()).build());
    }

    @Test
    void editServerId() throws Exception {
        page.getInVMConnectorTable().select(IN_VM_CONNECTOR_UPDATE);
        crudOperations.update(RemoteActiveMQServer.inVMConnectorAddress(IN_VM_CONNECTOR_UPDATE),
                page.getInVMConnectorForm(), "server-id", Random.number());
    }
}
