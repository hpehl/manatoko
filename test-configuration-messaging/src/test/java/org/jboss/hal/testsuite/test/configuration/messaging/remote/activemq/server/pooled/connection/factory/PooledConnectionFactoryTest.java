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
package org.jboss.hal.testsuite.test.configuration.messaging.remote.activemq.server.pooled.connection.factory;

import java.util.Arrays;

import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.JGroupsFixtures;
import org.jboss.hal.testsuite.model.ResourceVerifier;
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
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.RemoteActiveMQServer;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class PooledConnectionFactoryTest extends AbstractPooledConnectionFactoryTest {

    private static final String POOLED_CONNECTION_FACTORY_CREATE = "pooled-connection-factory-to-create-" + Random.name();
    private static final String POOLED_CONNECTION_FACTORY_DELETE = "pooled-connection-factory-to-delete-" + Random.name();

    private static final String DISCOVERY_GROUP_CREATE = "discovery-group-create-" + Random.name();

    private static final String JGROUPS_CHANNEL = "jgroups-channel-" + Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(JGroupsFixtures.channelAddress(JGROUPS_CHANNEL),
                Values.of(ModelDescriptionConstants.STACK, "tcp"))
                .assertSuccess();
        createDiscoveryGroup(operations, DISCOVERY_GROUP_CREATE, JGROUPS_CHANNEL);
        new Administration(client).reloadIfRequired();
        createPooledConnectionFactory(operations, POOLED_CONNECTION_FACTORY_DELETE, DISCOVERY_GROUP_CREATE);
    }

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary("msg-remote-activemq-pooled-connection-factory-item");
    }

    @Test
    void create() throws Exception {
        crudOperations.create(RemoteActiveMQServer.pooledConnectionFactoryAddress(POOLED_CONNECTION_FACTORY_CREATE),
                page.getPooledConnectionFactoryTable(), formFragment -> {
                    formFragment.text(ModelDescriptionConstants.NAME, POOLED_CONNECTION_FACTORY_CREATE);
                    formFragment.list("entries").add(Arrays.asList(Random.name(), Random.name(), Random.name()));
                    formFragment.text(ModelDescriptionConstants.DISCOVERY_GROUP, DISCOVERY_GROUP_CREATE);
                }, ResourceVerifier::verifyExists);
    }

    @Test
    void remove() throws Exception {
        crudOperations.delete(RemoteActiveMQServer.pooledConnectionFactoryAddress(POOLED_CONNECTION_FACTORY_DELETE),
                page.getPooledConnectionFactoryTable(), POOLED_CONNECTION_FACTORY_DELETE);
    }
}
