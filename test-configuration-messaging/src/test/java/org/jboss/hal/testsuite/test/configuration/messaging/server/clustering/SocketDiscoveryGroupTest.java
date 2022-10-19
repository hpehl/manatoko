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
package org.jboss.hal.testsuite.test.configuration.messaging.server.clustering;

import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddMessagingServer;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.test.Manatoko;
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
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.DISCOVERY_GROUP_ITEM;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.REFRESH_TIMEOUT;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SOCKET_DG_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SOCKET_DG_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SOCKET_DG_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SOCKET_DISCOVERY_GROUP_ITEM;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.socketDiscoveryGroupAddress;

@Manatoko
@Testcontainers
class SocketDiscoveryGroupTest extends AbstractClusteringTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));

        Operations operations = new Operations(client);
        operations.add(socketDiscoveryGroupAddress(SRV_UPDATE, SOCKET_DG_UPDATE),
                Values.of(SOCKET_BINDING, Random.name()));
        operations.add(socketDiscoveryGroupAddress(SRV_UPDATE, SOCKET_DG_DELETE),
                Values.of(SOCKET_BINDING, Random.name()));
    }

    TableFragment table;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectSecondary(DISCOVERY_GROUP_ITEM, SOCKET_DISCOVERY_GROUP_ITEM);
        table = page.getSocketDiscoveryGroupTable();
        form = page.getSocketDiscoveryGroupForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crudOperations.create(socketDiscoveryGroupAddress(SRV_UPDATE, SOCKET_DG_CREATE), table, f -> {
            f.text(NAME, SOCKET_DG_CREATE);
            f.text(SOCKET_BINDING, Random.name());
        }, ResourceVerifier::verifyExists);
    }

    @Test
    void update() throws Exception {
        table.select(SOCKET_DG_UPDATE);
        crudOperations.update(socketDiscoveryGroupAddress(SRV_UPDATE, SOCKET_DG_UPDATE), form, REFRESH_TIMEOUT, 123L);
    }

    @Test
    void delete() throws Exception {
        crudOperations.delete(socketDiscoveryGroupAddress(SRV_UPDATE, SOCKET_DG_DELETE), table, SOCKET_DG_DELETE);
    }
}
