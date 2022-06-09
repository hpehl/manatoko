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
package org.jboss.hal.testsuite.test.configuration.jgroups;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Names;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.JGroupsPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CHANNEL;
import static org.jboss.hal.dmr.ModelDescriptionConstants.EE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.RELAY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STACK;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.HA;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.CHANNEL_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.JGROUPS_TCP;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.REMOTESITE_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.REMOTESITE_DELETE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.REMOTESITE_UPDATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.SITE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.STACK_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.TCP;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.TRANSPORT_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.channelAddress;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.relayAddress;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.relayRemoteSiteAddress;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.stackAddress;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.transportAddress;

@Manatoko
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
class StackRelayRemoteSiteTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        Batch stackCreate = new Batch();
        stackCreate.add(stackAddress(STACK_CREATE));
        stackCreate.add(transportAddress(STACK_CREATE, TRANSPORT_CREATE), Values.of(SOCKET_BINDING, JGROUPS_TCP));
        operations.batch(stackCreate);
        operations.add(channelAddress(CHANNEL_CREATE), Values.of(STACK, TCP));
        operations.add(relayAddress(STACK_CREATE), Values.of(SITE, Random.name()));
        operations.add(relayRemoteSiteAddress(STACK_CREATE, REMOTESITE_UPDATE), Values.of(CHANNEL, EE));
        operations.add(relayRemoteSiteAddress(STACK_CREATE, REMOTESITE_DELETE), Values.of(CHANNEL, EE));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page JGroupsPage page;
    TableFragment stackTable;
    TableFragment relayTable;
    TableFragment remoteSiteTable;
    FormFragment remoteSiteForm;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectPrimary("jgroups-stack-item");

        stackTable = page.getStackTable();
        relayTable = page.getRelayTable();
        remoteSiteTable = page.getRelayRemoteSiteTable();
        remoteSiteForm = page.getRelayRemoteSiteForm();
        remoteSiteTable.bind(remoteSiteForm);
    }

    @Test
    void create() throws Exception {
        stackTable.action(STACK_CREATE, Names.RELAY);
        waitGui().until().element(relayTable.getRoot()).is().visible();

        relayTable.select(RELAY.toUpperCase());
        relayTable.action(RELAY.toUpperCase(), Names.REMOTE_SITE);
        waitGui().until().element(remoteSiteTable.getRoot()).is().visible();

        crud.create(relayRemoteSiteAddress(STACK_CREATE, REMOTESITE_CREATE), remoteSiteTable,
                form -> {
                    form.text(NAME, REMOTESITE_CREATE);
                    form.text(CHANNEL, EE);
                },
                resourceVerifier -> resourceVerifier.verifyAttribute(CHANNEL, EE));
    }

    @Test()
    void update() throws Exception {
        stackTable.action(STACK_CREATE, Names.RELAY);
        waitGui().until().element(relayTable.getRoot()).is().visible();

        relayTable.select(RELAY.toUpperCase());
        relayTable.action(RELAY.toUpperCase(), Names.REMOTE_SITE);
        waitGui().until().element(remoteSiteTable.getRoot()).is().visible();

        remoteSiteTable.select(REMOTESITE_UPDATE);
        crud.update(relayRemoteSiteAddress(STACK_CREATE, REMOTESITE_UPDATE), remoteSiteForm, CHANNEL, CHANNEL_CREATE);
    }

    @Test()
    void updateEmptyEE() {
        stackTable.action(STACK_CREATE, Names.RELAY);
        waitGui().until().element(relayTable.getRoot()).is().visible();

        relayTable.select(RELAY.toUpperCase());
        relayTable.action(RELAY.toUpperCase(), Names.REMOTE_SITE);
        waitGui().until().element(remoteSiteTable.getRoot()).is().visible();

        remoteSiteTable.select(REMOTESITE_UPDATE);
        crud.updateWithError(remoteSiteForm, form -> form.clear(CHANNEL), CHANNEL);
    }

    @Test
    void remove() throws Exception {
        stackTable.action(STACK_CREATE, Names.RELAY);
        waitGui().until().element(relayTable.getRoot()).is().visible();

        relayTable.select(RELAY.toUpperCase());
        relayTable.action(RELAY.toUpperCase(), Names.REMOTE_SITE);
        waitGui().until().element(remoteSiteTable.getRoot()).is().visible();

        crud.delete(relayRemoteSiteAddress(STACK_CREATE, REMOTESITE_DELETE), remoteSiteTable, REMOTESITE_DELETE);
    }
}
