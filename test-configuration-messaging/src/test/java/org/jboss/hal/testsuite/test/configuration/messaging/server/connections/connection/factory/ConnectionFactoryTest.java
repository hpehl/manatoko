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
package org.jboss.hal.testsuite.test.configuration.messaging.server.connections.connection.factory;

import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddMessagingServer;
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
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CONNECTORS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DISCOVERY_GROUP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ENTRIES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_CONNECTION_FACTORY;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CALL_TIMEOUT;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_FAC_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_FAC_CREATE_ENTRY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_FAC_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_FAC_TRY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_FAC_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.connectionFactoryAddress;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.discoveryGroupAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class ConnectionFactoryTest extends AbstractServerConnectionsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        String discoveryGroup = Random.name();
        operations.add(discoveryGroupAddress(SRV_UPDATE, discoveryGroup)).assertSuccess();
        new Administration(client).reloadIfRequired();
        operations.add(connectionFactoryAddress(SRV_UPDATE, CONN_FAC_UPDATE),
                Values.ofList(ENTRIES, Random.name()).and(DISCOVERY_GROUP, discoveryGroup)).assertSuccess();
        operations.add(connectionFactoryAddress(SRV_UPDATE, CONN_FAC_TRY_UPDATE),
                Values.ofList(ENTRIES, Random.name()).and(DISCOVERY_GROUP, discoveryGroup)).assertSuccess();
        operations.add(connectionFactoryAddress(SRV_UPDATE, CONN_FAC_DELETE),
                Values.ofList(ENTRIES, Random.name()).and(DISCOVERY_GROUP, discoveryGroup)).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigateAgain(SERVER, SRV_UPDATE);
    }

    @Test
    void connectionFactoryCreate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CONNECTION_FACTORY, ITEM));
        TableFragment table = page.getConnectionFactoryTable();
        FormFragment form = page.getConnectionFactoryForm();
        table.bind(form);

        crudOperations.create(connectionFactoryAddress(SRV_UPDATE, CONN_FAC_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, CONN_FAC_CREATE);
                    formFragment.text(DISCOVERY_GROUP, Random.name());
                    formFragment.list(ENTRIES).add(CONN_FAC_CREATE_ENTRY);
                });
    }

    @Test
    void connectionFactoryTryCreate() {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CONNECTION_FACTORY, ITEM));
        TableFragment table = page.getConnectionFactoryTable();
        FormFragment form = page.getConnectionFactoryForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, CONN_FAC_CREATE, DISCOVERY_GROUP);
    }

    @Test
    void connectionFactoryUpdate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CONNECTION_FACTORY, ITEM));
        TableFragment table = page.getConnectionFactoryTable();
        FormFragment form = page.getConnectionFactoryForm();
        table.bind(form);
        table.select(CONN_FAC_UPDATE);
        crudOperations.update(connectionFactoryAddress(SRV_UPDATE, CONN_FAC_UPDATE), form,
                formFragment -> {
                    formFragment.number(CALL_TIMEOUT, 123L);
                    formFragment.flip("use-topology-for-load-balancing", false);
                },
                verifier -> {
                    verifier.verifyAttribute(CALL_TIMEOUT, 123L);
                    verifier.verifyAttribute("use-topology-for-load-balancing", false);
                });
    }

    @Test
    void connectionFactoryTryUpdate() {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CONNECTION_FACTORY, ITEM));
        TableFragment table = page.getConnectionFactoryTable();
        FormFragment form = page.getConnectionFactoryForm();
        table.bind(form);
        table.select(CONN_FAC_TRY_UPDATE);
        crudOperations.updateWithError(form, f -> f.list(CONNECTORS).add(Random.name()), DISCOVERY_GROUP);
    }

    @Test
    void connectionFactoryRemove() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CONNECTION_FACTORY, ITEM));
        TableFragment table = page.getConnectionFactoryTable();
        crudOperations.delete(connectionFactoryAddress(SRV_UPDATE, CONN_FAC_DELETE), table, CONN_FAC_DELETE);
    }
}
