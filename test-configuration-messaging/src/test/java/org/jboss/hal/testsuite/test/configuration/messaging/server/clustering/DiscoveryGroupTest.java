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
package org.jboss.hal.testsuite.test.configuration.messaging.server.clustering;

import org.jboss.hal.resources.Ids;
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
import static org.jboss.hal.resources.Ids.MESSAGING_DISCOVERY_GROUP;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.DG_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.DG_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.DG_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.DG_UPDATE_ALTERNATIVES;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_CHANNEL;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JGROUPS_CLUSTER;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.REFRESH_TIMEOUT;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.discoveryGroupAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class DiscoveryGroupTest extends AbstractClusteringTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        createServer(operations, SRV_UPDATE);
        operations.add(discoveryGroupAddress(SRV_UPDATE, DG_UPDATE)).assertSuccess();
        operations.add(discoveryGroupAddress(SRV_UPDATE, DG_UPDATE_ALTERNATIVES)).assertSuccess();
        operations.add(discoveryGroupAddress(SRV_UPDATE, DG_DELETE)).assertSuccess();
    }

    @BeforeEach
    void setUp() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void discoveryGroupCreate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_DISCOVERY_GROUP, ITEM));
        TableFragment table = page.getDiscoveryGroupTable();
        FormFragment form = page.getDiscoveryGroupForm();
        table.bind(form);

        crudOperations.create(discoveryGroupAddress(SRV_UPDATE, DG_CREATE), table, DG_CREATE);
    }

    @Test
    void discoveryGroupUpdate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_DISCOVERY_GROUP, ITEM));
        TableFragment table = page.getDiscoveryGroupTable();
        FormFragment form = page.getDiscoveryGroupForm();
        table.bind(form);
        table.select(DG_UPDATE);
        crudOperations.update(discoveryGroupAddress(SRV_UPDATE, DG_UPDATE), form, REFRESH_TIMEOUT, 123L);
    }

    @Test
    void discoveryGroupTryUpdateAlternatives() {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_DISCOVERY_GROUP, ITEM));
        TableFragment table = page.getDiscoveryGroupTable();
        FormFragment form = page.getDiscoveryGroupForm();
        table.bind(form);
        table.select(DG_UPDATE_ALTERNATIVES);
        crudOperations.updateWithError(form, f -> {
            f.text(JGROUPS_CLUSTER, EE);
            f.text(JGROUPS_CHANNEL, EE);
            f.text(SOCKET_BINDING, HTTP);
        }, JGROUPS_CLUSTER, SOCKET_BINDING);
    }

    @Test
    void discoveryGroupRemove() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_DISCOVERY_GROUP, ITEM));
        TableFragment table = page.getDiscoveryGroupTable();
        FormFragment form = page.getDiscoveryGroupForm();
        table.bind(form);

        crudOperations.delete(discoveryGroupAddress(SRV_UPDATE, DG_DELETE), table, DG_DELETE);
    }

}
