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
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddMessagingServer;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CONNECTOR_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DISCOVERY_GROUP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTP_CONNECTOR;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STATIC_CONNECTORS;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_CLUSTER_CONNECTION;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CALL_TIMEOUT;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CC_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CC_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CC_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CC_UPDATE_ALTERNATIVES;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CLUSTER_CONNECTION_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.clusterConnectionAddress;

@Manatoko
@Testcontainers
class ClusterConnectionTest extends AbstractClusteringTest {

    private static final Values CC_PARAMS = Values.of(CLUSTER_CONNECTION_ADDRESS, Random.name())
            .and(CONNECTOR_NAME, HTTP_CONNECTOR)
            .and(DISCOVERY_GROUP, Random.name());

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(clusterConnectionAddress(SRV_UPDATE, CC_UPDATE), CC_PARAMS).assertSuccess();
        operations.add(clusterConnectionAddress(SRV_UPDATE, CC_UPDATE_ALTERNATIVES), CC_PARAMS).assertSuccess();
        operations.add(clusterConnectionAddress(SRV_UPDATE, CC_DELETE), CC_PARAMS).assertSuccess();
    }

    @BeforeEach
    void setUp() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void clusterConnectionCreate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CLUSTER_CONNECTION, ITEM));
        console.waitNoNotification();
        TableFragment table = page.getClusterConnectionTable();
        FormFragment form = page.getClusterConnectionForm();
        table.bind(form);
        crudOperations.create(clusterConnectionAddress(SRV_UPDATE, CC_CREATE), table, f -> {
            f.text(NAME, CC_CREATE);
            f.text(CLUSTER_CONNECTION_ADDRESS, Random.name());
            f.text(CONNECTOR_NAME, HTTP_CONNECTOR);
            f.text(DISCOVERY_GROUP, Random.name());
        });
    }

    @Test
    void clusterConnectionTryCreate() {
        page.navigateAgain(SERVER, SRV_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CLUSTER_CONNECTION, ITEM));
        TableFragment table = page.getClusterConnectionTable();
        FormFragment form = page.getClusterConnectionForm();
        table.bind(form);
        crudOperations.createWithErrorAndCancelDialog(table, f -> f.text(NAME, CC_CREATE), CLUSTER_CONNECTION_ADDRESS);
    }

    @Test
    void clusterConnectionUpdate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CLUSTER_CONNECTION, ITEM));
        TableFragment table = page.getClusterConnectionTable();
        FormFragment form = page.getClusterConnectionForm();
        table.bind(form);
        table.scrollToTop();
        table.select(CC_UPDATE);
        crudOperations.update(clusterConnectionAddress(SRV_UPDATE, CC_UPDATE), form, CALL_TIMEOUT, 123L);
    }

    @Test
    void clusterConnectionTryUpdateAlternatives() {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CLUSTER_CONNECTION, ITEM));
        TableFragment table = page.getClusterConnectionTable();
        FormFragment form = page.getClusterConnectionForm();
        table.bind(form);
        table.scrollToTop();
        table.select(CC_UPDATE_ALTERNATIVES);

        crudOperations.updateWithError(form, f -> {
            f.text(DISCOVERY_GROUP, Random.name());
            f.list(STATIC_CONNECTORS).add(HTTP);
        }, STATIC_CONNECTORS);
    }

    @Test
    void clusterConnectionRemove() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_CLUSTER_CONNECTION, ITEM));
        TableFragment table = page.getClusterConnectionTable();
        FormFragment form = page.getClusterConnectionForm();
        table.bind(form);

        crudOperations.delete(clusterConnectionAddress(SRV_UPDATE, CC_DELETE), table, CC_DELETE);
    }
}
