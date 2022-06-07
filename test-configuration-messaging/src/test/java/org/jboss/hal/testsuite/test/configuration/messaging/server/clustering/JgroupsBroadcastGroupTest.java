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

import org.jboss.dmr.ModelNode;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddMessagingServer;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.CONNECTORS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.JGROUPS_CLUSTER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BROADCAST_GROUP_ITEM;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BROADCAST_PERIOD;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONN_INVM_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_BG_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_BG_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_BG_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_BROADCAST_GROUP_ITEM;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SERVER_ID;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.connectorInVMAddress;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.jgroupsBroadcastGroupAddress;

@Manatoko
@Testcontainers
class JgroupsBroadcastGroupTest extends AbstractClusteringTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));

        Operations operations = new Operations(client);
        operations.add(connectorInVMAddress(SRV_UPDATE, CONN_INVM_CREATE), Values.of(SERVER_ID, Random.number()));

        ModelNode connectors = new ModelNode();
        connectors.add(new ModelNode(CONN_INVM_CREATE));
        operations.add(jgroupsBroadcastGroupAddress(SRV_UPDATE, JGROUPS_BG_UPDATE),
                Values.of(JGROUPS_CLUSTER, Random.name()).and(CONNECTORS, connectors));
        operations.add(jgroupsBroadcastGroupAddress(SRV_UPDATE, JGROUPS_BG_DELETE),
                Values.of(JGROUPS_CLUSTER, Random.name()).and(CONNECTORS, connectors));
    }

    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectSecondary(BROADCAST_GROUP_ITEM, JGROUPS_BROADCAST_GROUP_ITEM);
        table = page.getJgroupsBroadcastGroupTable();
        form = page.getJgroupsBroadcastGroupForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crudOperations.create(jgroupsBroadcastGroupAddress(SRV_UPDATE, JGROUPS_BG_CREATE), table, f -> {
            f.text(NAME, JGROUPS_BG_CREATE);
            f.list(CONNECTORS).add(CONN_INVM_CREATE);
            f.text(JGROUPS_CLUSTER, Random.name());
        }, ResourceVerifier::verifyExists);
    }

    @Test
    void update() throws Exception {
        table.select(JGROUPS_BG_UPDATE);
        crudOperations.update(jgroupsBroadcastGroupAddress(SRV_UPDATE, JGROUPS_BG_UPDATE), form, BROADCAST_PERIOD, 123L);
    }

    @Test
    void delete() throws Exception {
        crudOperations.delete(jgroupsBroadcastGroupAddress(SRV_UPDATE, JGROUPS_BG_DELETE), table, JGROUPS_BG_DELETE);
    }
}
