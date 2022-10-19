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
package org.jboss.hal.testsuite.test.configuration.messaging.server.connections.connector.generic;

import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
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
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONNECTOR_GENERIC_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONNECTOR_GENERIC_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONNECTOR_GENERIC_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.FACTORY_CLASS;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.connectorGenericAddress;

@Manatoko
@Testcontainers
class GenericConnectorTest extends AbstractServerConnectionsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(connectorGenericAddress(SRV_UPDATE, CONNECTOR_GENERIC_UPDATE), Values.of(FACTORY_CLASS, Random.name()))
                .assertSuccess();
        operations.add(connectorGenericAddress(SRV_UPDATE, CONNECTOR_GENERIC_DELETE), Values.of(FACTORY_CLASS, Random.name()))
                .assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void connectorGenericCreate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM), Ids.build(MESSAGING_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorGenericTable();
        FormFragment form = page.getConnectorGenericForm();
        table.bind(form);

        crudOperations.create(connectorGenericAddress(SRV_UPDATE, CONNECTOR_GENERIC_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, CONNECTOR_GENERIC_CREATE);
                    formFragment.text(FACTORY_CLASS, Random.name());
                });
    }

    @Test
    void connectorGenericTryCreate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM), Ids.build(MESSAGING_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorGenericTable();
        FormFragment form = page.getConnectorGenericForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, CONNECTOR_GENERIC_CREATE, FACTORY_CLASS);
    }

    @Test
    void connectorGenericUpdate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM), Ids.build(MESSAGING_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorGenericTable();
        FormFragment form = page.getConnectorGenericForm();
        table.bind(form);
        table.select(CONNECTOR_GENERIC_UPDATE);
        crudOperations.update(connectorGenericAddress(SRV_UPDATE, CONNECTOR_GENERIC_UPDATE), form, FACTORY_CLASS);
    }

    @Test
    void connectorGenericTryUpdate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM), Ids.build(MESSAGING_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorGenericTable();
        FormFragment form = page.getConnectorGenericForm();
        table.bind(form);
        table.select(CONNECTOR_GENERIC_UPDATE);
        crudOperations.updateWithError(form, f -> f.clear(FACTORY_CLASS), FACTORY_CLASS);
    }

    @Test
    void connectorGenericRemove() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_CONNECTOR, GROUP, ITEM), Ids.build(MESSAGING_CONNECTOR, ITEM));
        TableFragment table = page.getConnectorGenericTable();
        FormFragment form = page.getConnectorGenericForm();
        table.bind(form);

        crudOperations.delete(connectorGenericAddress(SRV_UPDATE, CONNECTOR_GENERIC_DELETE), table,
                CONNECTOR_GENERIC_DELETE);
    }
}
