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

import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.command.AddMessagingServer;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_SOCKET_BROADCAST_GROUP;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BROADCAST_GROUP_ITEM;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BROADCAST_PERIOD;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SOCKET_BG_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SOCKET_BG_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SOCKET_BG_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.socketBroadcastGroupAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class SocketBroadcastGroupTest extends AbstractClusteringTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(socketBroadcastGroupAddress(SRV_UPDATE, SOCKET_BG_UPDATE)).assertSuccess();
        operations.add(socketBroadcastGroupAddress(SRV_UPDATE, SOCKET_BG_DELETE)).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void create() throws Exception {
        console.verticalNavigation().selectSecondary(BROADCAST_GROUP_ITEM, Ids.build(MESSAGING_SOCKET_BROADCAST_GROUP, ITEM));
        TableFragment table = page.getSocketBroadcastGroupTable();
        FormFragment form = page.getSocketBroadcastGroupForm();
        table.bind(form);

        crudOperations.create(socketBroadcastGroupAddress(SRV_UPDATE, SOCKET_BG_CREATE), table, SOCKET_BG_CREATE);
    }

    @Test
    void update() throws Exception {
        console.verticalNavigation().selectSecondary(BROADCAST_GROUP_ITEM, Ids.build(MESSAGING_SOCKET_BROADCAST_GROUP, ITEM));
        TableFragment table = page.getSocketBroadcastGroupTable();
        FormFragment form = page.getSocketBroadcastGroupForm();
        table.bind(form);
        table.scrollToTop();
        table.select(SOCKET_BG_UPDATE);
        crudOperations.update(socketBroadcastGroupAddress(SRV_UPDATE, SOCKET_BG_UPDATE), form, BROADCAST_PERIOD, 123L);
    }

    @Test
    void delete() throws Exception {
        console.verticalNavigation().selectSecondary(BROADCAST_GROUP_ITEM, Ids.build(MESSAGING_SOCKET_BROADCAST_GROUP, ITEM));
        TableFragment table = page.getSocketBroadcastGroupTable();
        FormFragment form = page.getSocketBroadcastGroupForm();
        table.bind(form);

        crudOperations.delete(socketBroadcastGroupAddress(SRV_UPDATE, SOCKET_BG_DELETE), table, SOCKET_BG_DELETE);
    }

}
