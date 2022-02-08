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
package org.jboss.hal.testsuite.test.configuration.messaging.remote.activemq.server.discovery.group;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.creaper.command.AddLocalSocketBinding;
import org.jboss.hal.testsuite.fixtures.JGroupsFixtures;
import org.jboss.hal.testsuite.page.configuration.MessagingRemoteActiveMQPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.RemoteActiveMQServer;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class DiscoveryGroupTest {

    private static final String DISCOVERY_GROUP_CREATE = "discovery-group-to-create-" + Random.name();
    private static final String DISCOVERY_GROUP_UPDATE = "discovery-group-to-update-" + Random.name();
    private static final String DISCOVERY_GROUP_DELETE = "discovery-group-to-delete-" + Random.name();
    private static final String JGROUPS_CHANNEL_UPDATE = "jgroups-channel-" + Random.name();
    private static final String LOCAL_SOCKET_BINDING = "local-socket-binding-" + Random.name();
    private static final String JGROUPS_CHANNEL = "jgroups-channel";
    private static final String JGROUPS_CLUSTER = "jgroups-cluster";
    private static final String SOCKET_BINDING = "socket-binding";

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_UPDATE)).assertSuccess();
        operations.add(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_DELETE)).assertSuccess();
        AddLocalSocketBinding addLocalSocketBinding = new AddLocalSocketBinding(LOCAL_SOCKET_BINDING);
        client.apply(addLocalSocketBinding);
        operations.add(JGroupsFixtures.channelAddress(JGROUPS_CHANNEL_UPDATE), Values.of("stack", "tcp"))
                .assertSuccess();
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page MessagingRemoteActiveMQPage page;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary("msg-remote-discovery-group-item");
    }

    @Test
    void create() throws Exception {
        crudOperations.create(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_CREATE),
                page.getDiscoveryGroupTable(), DISCOVERY_GROUP_CREATE);
    }

    @Test
    void remove() throws Exception {
        crudOperations.delete(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_DELETE),
                page.getDiscoveryGroupTable(), DISCOVERY_GROUP_DELETE);
    }

    @Test
    void editInitialWaitTimeout() throws Exception {
        page.getDiscoveryGroupTable().select(DISCOVERY_GROUP_UPDATE);
        crudOperations.update(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_UPDATE),
                page.getDicoveryGroupForm(), "initial-wait-timeout", Long.valueOf(Random.number()));
    }

    @Test
    void editJGroupsChannel() throws Exception {
        page.getDiscoveryGroupTable().select(DISCOVERY_GROUP_UPDATE);
        crudOperations.update(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_UPDATE),
                page.getDicoveryGroupForm(), formFragment -> {
                    formFragment.text(JGROUPS_CHANNEL, JGROUPS_CHANNEL_UPDATE);
                    formFragment.text(JGROUPS_CLUSTER, Random.name());
                    formFragment.clear(SOCKET_BINDING);
                }, resourceVerifier -> resourceVerifier.verifyAttribute(JGROUPS_CHANNEL, JGROUPS_CHANNEL_UPDATE));
    }

    @Test
    void editJGroupsCluster() throws Exception {
        String jgroupsCluster = Random.name();
        page.getDiscoveryGroupTable().select(DISCOVERY_GROUP_UPDATE);
        crudOperations.update(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_UPDATE),
                page.getDicoveryGroupForm(), formFragment -> {
                    formFragment.clear(JGROUPS_CHANNEL);
                    formFragment.text(JGROUPS_CLUSTER, jgroupsCluster);
                    formFragment.clear(SOCKET_BINDING);
                }, resourceVerifier -> resourceVerifier.verifyAttribute(JGROUPS_CLUSTER, jgroupsCluster));
    }

    @Test
    void editRefreshTimeout() throws Exception {
        page.getDiscoveryGroupTable().select(DISCOVERY_GROUP_UPDATE);
        crudOperations.update(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_UPDATE),
                page.getDicoveryGroupForm(), "refresh-timeout", Long.valueOf(Random.number()));
    }

    @Test
    void editSocketBinding() throws Exception {
        page.getDiscoveryGroupTable().select(DISCOVERY_GROUP_UPDATE);
        crudOperations.update(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_UPDATE),
                page.getDicoveryGroupForm(), formFragment -> {
                    formFragment.clear(JGROUPS_CHANNEL);
                    formFragment.clear(JGROUPS_CLUSTER);
                    formFragment.text(SOCKET_BINDING, LOCAL_SOCKET_BINDING);
                }, resourceVerifier -> resourceVerifier.verifyAttribute(SOCKET_BINDING, LOCAL_SOCKET_BINDING));
    }
}
