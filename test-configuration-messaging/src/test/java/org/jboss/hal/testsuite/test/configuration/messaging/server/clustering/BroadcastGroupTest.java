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

import static org.jboss.hal.dmr.ModelDescriptionConstants.EE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_BROADCAST_GROUP;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BG_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BG_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BG_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BROADCAST_PERIOD;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_CHANNEL;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_CLUSTER;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.broadcastGroupAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class BroadcastGroupTest extends AbstractClusteringTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(broadcastGroupAddress(SRV_UPDATE, BG_UPDATE)).assertSuccess();
        operations.add(broadcastGroupAddress(SRV_UPDATE, BG_DELETE)).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void broadcastGroupCreate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_BROADCAST_GROUP, ITEM));
        TableFragment table = page.getBroadcastGroupTable();
        FormFragment form = page.getBroadcastGroupForm();
        table.bind(form);

        crudOperations.create(broadcastGroupAddress(SRV_UPDATE, BG_CREATE), table, BG_CREATE);
    }

    @Test
    void broadcastGroupUpdate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_BROADCAST_GROUP, ITEM));
        TableFragment table = page.getBroadcastGroupTable();
        FormFragment form = page.getBroadcastGroupForm();
        table.bind(form);
        table.scrollToTop();
        table.select(BG_UPDATE);
        crudOperations.update(broadcastGroupAddress(SRV_UPDATE, BG_UPDATE), form, BROADCAST_PERIOD, 123L);
    }

    @Test
    void broadcastGroupTryUpdateAlternatives() {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_BROADCAST_GROUP, ITEM));
        TableFragment table = page.getBroadcastGroupTable();
        FormFragment form = page.getBroadcastGroupForm();
        table.bind(form);
        table.select(BG_UPDATE);
        crudOperations.updateWithError(form, f -> {
            f.text(JGROUPS_CLUSTER, EE);
            f.text(JGROUPS_CHANNEL, EE);
            f.text(SOCKET_BINDING, HTTP);
        }, JGROUPS_CLUSTER, SOCKET_BINDING);
    }

    @Test
    void broadcastGroupRemove() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_BROADCAST_GROUP, ITEM));
        TableFragment table = page.getBroadcastGroupTable();
        FormFragment form = page.getBroadcastGroupForm();
        table.bind(form);

        crudOperations.delete(broadcastGroupAddress(SRV_UPDATE, BG_DELETE), table, BG_DELETE);
    }

}
