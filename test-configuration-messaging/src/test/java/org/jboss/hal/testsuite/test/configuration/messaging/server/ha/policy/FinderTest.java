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

import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.command.AddMessagingServer;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.WizardFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import static org.jboss.arquillian.graphene.Graphene.waitModel;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MESSAGING_ACTIVEMQ;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SHARED_STORE_COLOCATED;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SHARED_STORE_MASTER;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_REPLICATION;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_REPLICATION_LIVE_ONLY;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_REPLICATION_MASTER;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_SHARED_STORE;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_SHARED_STORE_COLOCATED;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_SHARED_STORE_MASTER;
import static org.jboss.hal.resources.Ids.MESSAGING_SERVER_CONFIGURATION;
import static org.jboss.hal.resources.Ids.MESSAGING_SERVER_HA_POLICY;
import static org.jboss.hal.resources.Ids.MESSAGING_SERVER_SETTINGS;
import static org.jboss.hal.resources.Ids.messagingServer;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.haPolicyAddress;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;

@Manatoko
@Testcontainers
class FinderTest extends AbstractHaPolicyTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);
    private static OnlineManagementClient client;
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        operations = new Operations(client);
        client.apply(new AddMessagingServer(SRV_UPDATE));
    }

    private final HAPolicyConsumer createPolicyInFinder = (haPolicy -> {
        column.selectItem(MESSAGING_SERVER_HA_POLICY).defaultAction();
        WizardFragment wizard = console.wizard();
        wizard.getRoot().findElement(By.id(haPolicy.basicStrategy)).click();
        wizard.next(haPolicy.serverRole);
        wizard.getRoot().findElement(By.id(haPolicy.serverRole)).click();
        wizard.finish();
        console.verifySuccess();
        new ResourceVerifier(haPolicy.haPolicyAddress, client)
                .verifyExists();
        operations.removeIfExists(haPolicy.haPolicyAddress);
    });

    private final HAPolicyConsumer removePolicyInFinder = (haPolicy -> {
        operations.add(haPolicy.haPolicyAddress);
        refreshConfigurationColumn();
        column.selectItem(MESSAGING_SERVER_HA_POLICY).dropdown().click("Remove");
        console.confirmationDialog().confirm();
        console.verifySuccess();
        new ResourceVerifier(haPolicy.haPolicyAddress, client)
                .verifyDoesNotExist();
        operations.removeIfExists(haPolicy.haPolicyAddress);
    });

    @BeforeEach
    void prepare() {
        column = console.finder(NameTokens.CONFIGURATION, configurationSubsystemPath(MESSAGING_ACTIVEMQ)
                .append(Ids.MESSAGING_CATEGORY, SERVER)
                .append(MESSAGING_SERVER_CONFIGURATION, messagingServer(SRV_UPDATE)))
                .column(MESSAGING_SERVER_SETTINGS);
    }

    void refreshConfigurationColumn() {
        // after the previous operations, it is necessary to refresh the "server" column
        console.finder(NameTokens.CONFIGURATION, configurationSubsystemPath(MESSAGING_ACTIVEMQ)
                .append(Ids.MESSAGING_CATEGORY, SERVER))
                .column(MESSAGING_SERVER_CONFIGURATION)
                .refresh();
        prepare();
    }

    @Test
    void createReplicationLiveOnly() throws Exception {
        HAPolicy.LIVE_ONLY.create(createPolicyInFinder);
    }

    @Test
    void removeReplicationLiveOnly() throws Exception {
        HAPolicy.LIVE_ONLY.remove(removePolicyInFinder);
    }

    @Test
    void createReplicationMaster() throws Exception {
        HAPolicy.REPLICATION_MASTER.create(createPolicyInFinder);
    }

    @Test
    void removeReplicationMaster() throws Exception {
        HAPolicy.REPLICATION_MASTER.remove(removePolicyInFinder);
    }

    @Test
    void createReplicationSlave() throws Exception {
        HAPolicy.REPLICATION_SLAVE.create(createPolicyInFinder);
    }

    @Test
    void removeReplicationSlave() throws Exception {
        HAPolicy.REPLICATION_SLAVE.remove(removePolicyInFinder);
    }

    @Test
    void createReplicationColocated() throws Exception {
        HAPolicy.REPLICATION_COLOCATED.create(createPolicyInFinder);
    }

    @Test
    void removeReplicationColocated() throws Exception {
        HAPolicy.REPLICATION_COLOCATED.remove(removePolicyInFinder);
    }

    @Test
    void createSharedStoreMaster() throws Exception {
        HAPolicy.SHARED_STORE_MASTER.create(createPolicyInFinder);
    }

    @Test
    void removeSharedStoreMaster() throws Exception {
        HAPolicy.SHARED_STORE_MASTER.remove(removePolicyInFinder);
    }

    @Test
    void createSharedStoreSlave() throws Exception {
        HAPolicy.SHARED_STORE_SLAVE.create(createPolicyInFinder);
    }

    @Test
    void removeSharedStoreSlave() throws Exception {
        HAPolicy.SHARED_STORE_SLAVE.remove(removePolicyInFinder);
    }

    @Test
    void createSharedStoreColocated() throws Exception {
        HAPolicy.SHARED_STORE_COLOCATED.create(createPolicyInFinder);
    }

    @Test
    void removeSharedStoreColocated() throws Exception {
        HAPolicy.SHARED_STORE_COLOCATED.remove(removePolicyInFinder);
    }

    // use default values, do not select any radio box.

    // test the back/next wizard workflow
    // first select replication/live-master, then goes back and select shared-store/colocated
    @Test
    void createSharedStoreColocatedBackForth() throws Exception {
        column.selectItem(MESSAGING_SERVER_HA_POLICY).defaultAction();
        wizard = console.wizard();
        // selects the "replication" radio item
        wizard.getRoot().findElement(By.id(MESSAGING_HA_REPLICATION)).click();
        // clicks the "next" button and waits for the dom id
        wizard.next(MESSAGING_HA_REPLICATION_LIVE_ONLY);
        // selects the "Live server (master)" radio item
        wizard.getRoot().findElement(By.id(MESSAGING_HA_REPLICATION_MASTER)).click();
        // clicks the "back" button and waits for the "shared-store" dom id
        wizard.back(By.id(MESSAGING_HA_SHARED_STORE));
        // clicks the "shared-store" radio item
        wizard.getRoot().findElement(By.id(MESSAGING_HA_SHARED_STORE)).click();
        // clicks "next"
        wizard.next(MESSAGING_HA_SHARED_STORE_MASTER);
        // clicks on the "Colocate live and backup server" radio box
        wizard.getRoot().findElement(By.id(MESSAGING_HA_SHARED_STORE_COLOCATED)).click();
        // finish the wizard
        wizard.next();

        console.verifySuccess();
        new ResourceVerifier(haPolicyAddress(SRV_UPDATE, SHARED_STORE_COLOCATED), client)
                .verifyExists();
        operations.removeIfExists(haPolicyAddress(SRV_UPDATE, SHARED_STORE_COLOCATED));
    }

    // test the back/next wizard workflow with default values
    // first select replication/live-master, then goes back and select shared-store, do not select any item and finish
    @Test
    void createSharedStoreLiveMasterBackForth() throws Exception {
        column.selectItem(MESSAGING_SERVER_HA_POLICY).defaultAction();
        wizard = console.wizard();
        // selects the "replication" radio item
        wizard.getRoot().findElement(By.id(MESSAGING_HA_REPLICATION)).click();
        // clicks the "next" button and waits for the dom id
        wizard.next(MESSAGING_HA_REPLICATION_LIVE_ONLY);
        // selects the "Live server (master)" radio item
        wizard.getRoot().findElement(By.id(MESSAGING_HA_REPLICATION_MASTER)).click();
        // clicks the "back" button and waits for the "shared-store" dom id
        wizard.back(By.id(MESSAGING_HA_SHARED_STORE));
        // clicks the "shared-store" radio item
        wizard.getRoot().findElement(By.id(MESSAGING_HA_SHARED_STORE)).click();
        // clicks "next"
        wizard.next(MESSAGING_HA_SHARED_STORE_MASTER);
        // finish the wizard with default "Live server (master)" radio selected
        wizard.next();

        console.verifySuccess();
        new ResourceVerifier(haPolicyAddress(SRV_UPDATE, SHARED_STORE_MASTER), client)
                .verifyExists();
        operations.removeIfExists(haPolicyAddress(SRV_UPDATE, SHARED_STORE_MASTER));
    }

    @Test
    void viewEmptyHaPolicy() {
        column.selectItem(MESSAGING_SERVER_HA_POLICY).dropdown().click("View");
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.MESSAGING_SERVER_HA_POLICY)
                .with(SERVER, SRV_UPDATE)
                .build();
        waitModel().until().element(By.id(Ids.FINDER)).is().not().present();
        console.verify(placeRequest);
    }
}
