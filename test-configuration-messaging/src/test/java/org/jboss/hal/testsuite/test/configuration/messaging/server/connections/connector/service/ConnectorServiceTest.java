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
package org.jboss.hal.testsuite.test.configuration.messaging.server.connections.connector.service;

import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.command.AddMessagingServer;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_CONNECTOR_SERVICE;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONNECTOR_FACTORY_CLASS;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_SVC_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_SVC_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_SVC_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.FACTORY_CLASS;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.connectorServiceAddress;

@Manatoko
@Testcontainers
class ConnectorServiceTest extends AbstractServerConnectionsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(connectorServiceAddress(SRV_UPDATE, CONN_SVC_UPDATE),
                Values.of(FACTORY_CLASS, CONNECTOR_FACTORY_CLASS)).assertSuccess();
        operations.add(connectorServiceAddress(SRV_UPDATE, CONN_SVC_DELETE),
                Values.of(FACTORY_CLASS, CONNECTOR_FACTORY_CLASS)).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void connectorServiceCreate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CONNECTOR_SERVICE, ITEM));
        TableFragment table = page.getConnectorServiceTable();
        FormFragment form = page.getConnectorServiceForm();
        table.bind(form);

        crudOperations.create(connectorServiceAddress(SRV_UPDATE, CONN_SVC_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, CONN_SVC_CREATE);
                    formFragment.text(FACTORY_CLASS, CONNECTOR_FACTORY_CLASS);
                });
    }

    @Test
    void connectorServiceTryCreate() {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CONNECTOR_SERVICE, ITEM));
        TableFragment table = page.getConnectorServiceTable();
        FormFragment form = page.getConnectorServiceForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, CONN_SVC_CREATE, FACTORY_CLASS);
    }

    @Test
    void connectorServiceUpdate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CONNECTOR_SERVICE, ITEM));
        TableFragment table = page.getConnectorServiceTable();
        FormFragment form = page.getConnectorServiceForm();
        table.bind(form);
        table.select(CONN_SVC_UPDATE);
        crudOperations.update(connectorServiceAddress(SRV_UPDATE, CONN_SVC_UPDATE), form, FACTORY_CLASS,
                "org.apache.activemq.artemis.ArtemisConstants");
    }

    @Test
    void connectorServiceTryUpdate() {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CONNECTOR_SERVICE, ITEM));
        TableFragment table = page.getConnectorServiceTable();
        FormFragment form = page.getConnectorServiceForm();
        table.bind(form);
        table.select(CONN_SVC_UPDATE);
        crudOperations.updateWithError(form, f -> f.clear(FACTORY_CLASS), FACTORY_CLASS);
    }

    @Test
    void connectorServiceRemove() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CONNECTOR_SERVICE, ITEM));
        TableFragment table = page.getConnectorServiceTable();
        FormFragment form = page.getConnectorServiceForm();
        table.bind(form);

        crudOperations.delete(connectorServiceAddress(SRV_UPDATE, CONN_SVC_DELETE), table, CONN_SVC_DELETE);
    }

}
