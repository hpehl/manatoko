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
import static org.jboss.hal.resources.Ids.MESSAGING_JGROUPS_DISCOVERY_GROUP;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.BROADCAST_GROUP_ITEM;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_DG_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_DG_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_DG_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.REFRESH_TIMEOUT;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.jgroupsDiscoveryGroupAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class JgroupsDiscoveryGroupTest extends AbstractClusteringTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(jgroupsDiscoveryGroupAddress(SRV_UPDATE, JGROUPS_DG_UPDATE)).assertSuccess();
        operations.add(jgroupsDiscoveryGroupAddress(SRV_UPDATE, JGROUPS_DG_DELETE)).assertSuccess();
    }

    @BeforeEach
    void setUp() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void create() throws Exception {
        console.verticalNavigation().selectSecondary(BROADCAST_GROUP_ITEM, Ids.build(MESSAGING_JGROUPS_DISCOVERY_GROUP, ITEM));
        TableFragment table = page.getJgroupsDiscoveryGroupTable();
        FormFragment form = page.getJgroupsDiscoveryGroupForm();
        table.bind(form);

        crudOperations.create(jgroupsDiscoveryGroupAddress(SRV_UPDATE, JGROUPS_DG_CREATE), table, JGROUPS_DG_CREATE);
    }

    @Test
    void update() throws Exception {
        console.verticalNavigation().selectSecondary(BROADCAST_GROUP_ITEM, Ids.build(MESSAGING_JGROUPS_DISCOVERY_GROUP, ITEM));
        TableFragment table = page.getJgroupsDiscoveryGroupTable();
        FormFragment form = page.getJgroupsDiscoveryGroupForm();
        table.bind(form);
        table.select(JGROUPS_DG_UPDATE);
        crudOperations.update(jgroupsDiscoveryGroupAddress(SRV_UPDATE, JGROUPS_DG_UPDATE), form, REFRESH_TIMEOUT, 123L);
    }

    @Test
    void delete() throws Exception {
        console.verticalNavigation().selectSecondary(BROADCAST_GROUP_ITEM, Ids.build(MESSAGING_JGROUPS_DISCOVERY_GROUP, ITEM));
        TableFragment table = page.getJgroupsDiscoveryGroupTable();
        FormFragment form = page.getJgroupsDiscoveryGroupForm();
        table.bind(form);

        crudOperations.delete(jgroupsDiscoveryGroupAddress(SRV_UPDATE, JGROUPS_DG_DELETE), table, JGROUPS_DG_DELETE);
    }

}
