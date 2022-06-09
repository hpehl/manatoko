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
package org.jboss.hal.testsuite.test.configuration.messaging.server.ha.policy;

import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.testsuite.command.AddMessagingServer;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.MessagingFixtures;
import org.jboss.hal.testsuite.fragment.EmptyState;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;

@Manatoko
@Testcontainers
class ViewTest extends AbstractHaPolicyTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);
    private static OnlineManagementClient client;
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        operations = new Operations(client);
    }

    private final HAPolicyConsumer createPolicyInView = haPolicy -> {
        EmptyState emptyState = page.getEmptyState();
        console.waitNoNotification();
        emptyState.mainAction();
        wizard = console.wizard();
        wizard.getRoot().findElement(By.id(haPolicy.basicStrategy)).click();
        wizard.next(haPolicy.serverRole);
        wizard.getRoot().findElement(By.id(haPolicy.serverRole)).click();
        wizard.finish();
        console.verifySuccess();
        new ResourceVerifier(haPolicy.haPolicyAddress, client)
                .verifyExists();
        operations.removeIfExists(haPolicy.haPolicyAddress);
    };

    private final HAPolicyConsumer removePolicyInView = haPolicy -> {
        console.waitNoNotification();
        page.getRootContainer().findElement(ByJQuery.selector(".clickable:contains('Remove')")).click();
        console.confirmationDialog().confirm();
        new ResourceVerifier(haPolicy.haPolicyAddress, client).verifyDoesNotExist();
    };

    @Test
    void createReplicationLiveOnly() throws Exception {
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.LIVE_ONLY.create(createPolicyInView);
    }

    @Test
    void removeReplicationLiveOnly() throws Exception {
        operations.add(HAPolicy.LIVE_ONLY.haPolicyAddress);
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.LIVE_ONLY.remove(removePolicyInView);
    }

    @Test
    void createReplicationMaster() throws Exception {
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.REPLICATION_MASTER.create(createPolicyInView);
    }

    @Test
    void removeReplicationMaster() throws Exception {
        operations.add(HAPolicy.REPLICATION_MASTER.haPolicyAddress);
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.REPLICATION_MASTER.remove(removePolicyInView);
    }

    @Test
    void createReplicationSlave() throws Exception {
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.REPLICATION_SLAVE.create(createPolicyInView);
    }

    @Test
    void removeReplicationSlave() throws Exception {
        operations.add(HAPolicy.REPLICATION_SLAVE.haPolicyAddress);
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.REPLICATION_SLAVE.remove(removePolicyInView);
    }

    @Test
    void createReplicationColocated() throws Exception {
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.REPLICATION_COLOCATED.create(createPolicyInView);
    }

    @Test
    void removeReplicationColocated() throws Exception {
        operations.add(HAPolicy.REPLICATION_COLOCATED.haPolicyAddress);
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.REPLICATION_COLOCATED.remove(removePolicyInView);
    }

    @Test
    void createSharedStoreMaster() throws Exception {
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.SHARED_STORE_MASTER.create(createPolicyInView);
    }

    @Test
    void removeSharedStoreMaster() throws Exception {
        operations.add(HAPolicy.SHARED_STORE_MASTER.haPolicyAddress);
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.SHARED_STORE_MASTER.remove(removePolicyInView);
    }

    @Test
    void createSharedStoreSlave() throws Exception {
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.SHARED_STORE_SLAVE.create(createPolicyInView);
    }

    @Test
    void removeSharedStoreSlave() throws Exception {
        operations.add(HAPolicy.SHARED_STORE_SLAVE.haPolicyAddress);
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.SHARED_STORE_SLAVE.remove(removePolicyInView);
    }

    @Test
    void createSharedStoreColocated() throws Exception {
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.SHARED_STORE_COLOCATED.create(createPolicyInView);
    }

    @Test
    void removeSharedStoreColocated() throws Exception {
        operations.add(HAPolicy.SHARED_STORE_COLOCATED.haPolicyAddress);
        page.navigateAgain(ModelDescriptionConstants.SERVER, MessagingFixtures.SRV_UPDATE);
        HAPolicy.SHARED_STORE_COLOCATED.remove(removePolicyInView);
    }
}
