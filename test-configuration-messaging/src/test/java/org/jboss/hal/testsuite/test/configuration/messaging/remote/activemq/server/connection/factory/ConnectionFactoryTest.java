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
package org.jboss.hal.testsuite.test.configuration.messaging.remote.activemq.server.connection.factory;

import java.util.Arrays;
import java.util.Collections;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.JGroupsFixtures;
import org.jboss.hal.testsuite.model.ModelNodeGenerator;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.page.configuration.MessagingRemoteActiveMQPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.RemoteActiveMQServer;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class ConnectionFactoryTest {

    private static final String CONNECTION_FACTORY_CREATE = "connection-factory-to-create-" + Random.name();
    private static final String CONNECTION_FACTORY_UPDATE = "connection-factory-to-update-" + Random.name();
    private static final String CONNECTION_FACTORY_DELETE = "connection-factory-to-delete-" + Random.name();

    private static final String DISCOVERY_GROUP_CREATE = "discovery-group-create-" + Random.name();
    private static final String DISCOVERY_GROUP_UPDATE = "discovery-group-update-" + Random.name();
    private static final String GENERIC_CONNECTOR_UPDATE = "generic-connection-update-" + Random.name();

    private static final String JGROUPS_CHANNEL = "jgroups-channel-" + Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, FULL_HA);
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        operations.add(JGroupsFixtures.channelAddress(JGROUPS_CHANNEL),
                Values.of(ModelDescriptionConstants.STACK, "tcp"))
                .assertSuccess();

        Batch batch = new Batch();
        batch.add(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_CREATE));
        batch.writeAttribute(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_CREATE), "jgroups-channel",
                JGROUPS_CHANNEL);
        batch.writeAttribute(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_CREATE), "jgroups-cluster",
                Random.name());
        operations.batch(batch).assertSuccess();

        batch = new Batch();
        batch.add(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_UPDATE));
        batch.writeAttribute(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_UPDATE), "jgroups-channel",
                JGROUPS_CHANNEL);
        batch.writeAttribute(RemoteActiveMQServer.discoveryGroupAddress(DISCOVERY_GROUP_UPDATE), "jgroups-cluster",
                Random.name());
        operations.batch(batch).assertSuccess();
        new Administration(client).reloadIfRequired();

        operations.add(RemoteActiveMQServer.connectionFactoryAddress(CONNECTION_FACTORY_UPDATE),
                Values.of(ModelDescriptionConstants.DISCOVERY_GROUP, DISCOVERY_GROUP_CREATE)
                        .and("entries",
                                new ModelNodeGenerator.ModelNodeListBuilder()
                                        .addAll(Random.name(), Random.name(), Random.name())
                                        .build()))
                .assertSuccess();
        operations.add(RemoteActiveMQServer.connectionFactoryAddress(CONNECTION_FACTORY_DELETE),
                Values.of(ModelDescriptionConstants.DISCOVERY_GROUP, DISCOVERY_GROUP_CREATE)
                        .and("entries",
                                new ModelNodeGenerator.ModelNodeListBuilder()
                                        .addAll(Random.name(), Random.name(), Random.name())
                                        .build()))
                .assertSuccess();
        operations.add(RemoteActiveMQServer.genericConnectorAddress(GENERIC_CONNECTOR_UPDATE),
                Values.of("factory-class", Random.name()))
                .assertSuccess();
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page MessagingRemoteActiveMQPage page;

    @BeforeEach
    void navigate() {
        page.navigate();
        console.verticalNavigation().selectPrimary("msg-remote-connection-factory-item");
    }

    @Test
    void create() throws Exception {
        crudOperations.create(RemoteActiveMQServer.connectionFactoryAddress(CONNECTION_FACTORY_CREATE),
                page.getConnectionFactoryTable(), formFragment -> {
                    formFragment.text(ModelDescriptionConstants.NAME, CONNECTION_FACTORY_CREATE);
                    formFragment.list("entries").add(Arrays.asList(Random.name(), Random.name(), Random.name()));
                    formFragment.text(ModelDescriptionConstants.DISCOVERY_GROUP, DISCOVERY_GROUP_CREATE);
                }, ResourceVerifier::verifyExists);
    }

    @Test
    void remove() throws Exception {
        crudOperations.delete(RemoteActiveMQServer.connectionFactoryAddress(CONNECTION_FACTORY_DELETE),
                page.getConnectionFactoryTable(), CONNECTION_FACTORY_DELETE);
    }

    @Test
    void editConnectors() throws Exception {
        page.getConnectionFactoryTable().select(CONNECTION_FACTORY_UPDATE);
        crudOperations.update(RemoteActiveMQServer.connectionFactoryAddress(CONNECTION_FACTORY_UPDATE),
                page.getConnectionFactoryForm(), formFragment -> {
                    formFragment.list("connectors").add(Collections.singletonList(GENERIC_CONNECTOR_UPDATE));
                    formFragment.clear(ModelDescriptionConstants.DISCOVERY_GROUP);
                },
                resourceVerifier -> resourceVerifier.verifyAttribute("connectors",
                        new ModelNodeGenerator.ModelNodeListBuilder().addAll(GENERIC_CONNECTOR_UPDATE).build()));
    }

    @Test
    void editDiscoveryGroup() throws Exception {
        page.getConnectionFactoryTable().select(CONNECTION_FACTORY_UPDATE);
        crudOperations.update(RemoteActiveMQServer.connectionFactoryAddress(CONNECTION_FACTORY_UPDATE),
                page.getConnectionFactoryForm(), formFragment -> {
                    formFragment.text(ModelDescriptionConstants.DISCOVERY_GROUP, DISCOVERY_GROUP_UPDATE);
                    formFragment.clear("connectors");
                }, resourceVerifier -> resourceVerifier.verifyAttribute(ModelDescriptionConstants.DISCOVERY_GROUP,
                        DISCOVERY_GROUP_UPDATE));
    }

    @Test
    void editEntries() throws Exception {
        page.getConnectionFactoryTable().select(CONNECTION_FACTORY_UPDATE);
        crudOperations.update(RemoteActiveMQServer.connectionFactoryAddress(CONNECTION_FACTORY_UPDATE),
                page.getConnectionFactoryForm(), "entries", Arrays.asList(Random.name(), Random.name()));
    }

    @Test
    void editFactoryType() throws Exception {
        String[] factoryTypes = { "GENERIC", "TOPIC", "QUEUE", "XA_GENERIC", "XA_QUEUE", "XA_TOPIC" };
        String factoryType = factoryTypes[Random.number(1, factoryTypes.length)];
        page.getConnectionFactoryTable().select(CONNECTION_FACTORY_UPDATE);
        crudOperations.update(RemoteActiveMQServer.connectionFactoryAddress(CONNECTION_FACTORY_UPDATE),
                page.getConnectionFactoryForm(), formFragment -> formFragment.select("factory-type", factoryType),
                resourceVerifier -> resourceVerifier.verifyAttribute("factory-type", factoryType));
    }

    @Test
    void toggleHa() throws Exception {
        boolean ha = operations.readAttribute(RemoteActiveMQServer.connectionFactoryAddress(CONNECTION_FACTORY_UPDATE),
                "ha")
                .booleanValue(false);
        page.getConnectionFactoryTable().select(CONNECTION_FACTORY_UPDATE);
        crudOperations.update(RemoteActiveMQServer.connectionFactoryAddress(CONNECTION_FACTORY_UPDATE),
                page.getConnectionFactoryForm(), "ha", !ha);
    }

    @Test
    void toggleAmq1Prefix() throws Exception {
        boolean amq1Prefix = operations
                .readAttribute(RemoteActiveMQServer.connectionFactoryAddress(CONNECTION_FACTORY_UPDATE),
                        "enable-amq1-prefix")
                .booleanValue(false);
        page.getConnectionFactoryTable().select(CONNECTION_FACTORY_UPDATE);
        crudOperations.update(RemoteActiveMQServer.connectionFactoryAddress(CONNECTION_FACTORY_UPDATE),
                page.getConnectionFactoryForm(), "enable-amq1-prefix", !amq1Prefix);
    }
}
