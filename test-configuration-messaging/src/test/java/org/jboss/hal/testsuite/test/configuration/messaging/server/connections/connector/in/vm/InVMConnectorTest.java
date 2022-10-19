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
package org.jboss.hal.testsuite.test.configuration.messaging.server.connections.connector.in.vm;

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
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_CONNECTOR;
import static org.jboss.hal.resources.Ids.MESSAGING_IN_VM_CONNECTOR;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONNECTOR_IN_VM_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONNECTOR_IN_VM_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONNECTOR_IN_VM_TRY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONNECTOR_IN_VM_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SERVER_ID;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.connectorInVMAddress;

@Manatoko
@Testcontainers
class InVMConnectorTest extends AbstractServerConnectionsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(connectorInVMAddress(SRV_UPDATE, CONNECTOR_IN_VM_UPDATE), Values.of(SERVER_ID, 11)).assertSuccess();
        operations.add(connectorInVMAddress(SRV_UPDATE, CONNECTOR_IN_VM_TRY_UPDATE), Values.of(SERVER_ID, 12))
                .assertSuccess();
        operations.add(connectorInVMAddress(SRV_UPDATE, CONNECTOR_IN_VM_DELETE), Values.of(SERVER_ID, 22)).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void connectorInVMCreate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_IN_VM_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorInVMTable();
        FormFragment form = page.getConnectorInVMForm();
        table.bind(form);

        crudOperations.create(connectorInVMAddress(SRV_UPDATE, CONNECTOR_IN_VM_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, CONNECTOR_IN_VM_CREATE);
                    formFragment.number(SERVER_ID, 123);
                });
    }

    @Test
    void connectorInVMTryCreate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_IN_VM_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorInVMTable();
        FormFragment form = page.getConnectorInVMForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, CONNECTOR_IN_VM_CREATE, SERVER_ID);
    }

    @Test
    void connectorInVMUpdate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_IN_VM_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorInVMTable();
        FormFragment form = page.getConnectorInVMForm();
        table.bind(form);
        table.select(CONNECTOR_IN_VM_UPDATE);
        crudOperations.update(connectorInVMAddress(SRV_UPDATE, CONNECTOR_IN_VM_UPDATE), form, SERVER_ID, 89);
    }

    @Test
    void connectorInVMTryUpdate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_IN_VM_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorInVMTable();
        FormFragment form = page.getConnectorInVMForm();
        table.bind(form);
        table.select(CONNECTOR_IN_VM_TRY_UPDATE);
        crudOperations.updateWithError(form, f -> f.clear(SERVER_ID), SERVER_ID);
    }

    @Test
    void connectorInVMRemove() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_IN_VM_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorInVMTable();
        FormFragment form = page.getConnectorInVMForm();
        table.bind(form);

        crudOperations.delete(connectorInVMAddress(SRV_UPDATE, CONNECTOR_IN_VM_DELETE), table, CONNECTOR_IN_VM_DELETE);
    }
}
