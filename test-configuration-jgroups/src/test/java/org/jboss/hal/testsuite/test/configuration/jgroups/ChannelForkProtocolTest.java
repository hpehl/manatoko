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
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PROPERTIES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STACK;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.HA;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.CHANNEL_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.FORK_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.FORK_PROTOCOL_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.FORK_PROTOCOL_DELETE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.FORK_PROTOCOL_UPDATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.TCP;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.channelAddress;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.forkAddress;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.forkProtocolAddress;

@Manatoko
@Testcontainers
class ChannelForkProtocolTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(channelAddress(CHANNEL_CREATE), Values.of(STACK, TCP));
        operations.add(forkAddress(CHANNEL_CREATE, FORK_CREATE));
        operations.add(forkProtocolAddress(CHANNEL_CREATE, FORK_CREATE, FORK_PROTOCOL_UPDATE));
        operations.add(forkProtocolAddress(CHANNEL_CREATE, FORK_CREATE, FORK_PROTOCOL_DELETE));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page JGroupsPage page;
    TableFragment channelTable;
    TableFragment forkTable;
    TableFragment forkProtocolTable;
    FormFragment forkProtocolForm;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectPrimary("jgroups-channel-item");

        channelTable = page.getChannelTable();
        forkTable = page.getChannelForkTable();
        forkProtocolTable = page.getForkProtocolTable();
        forkProtocolForm = page.getForkProtocolForm();
    }

    @Test
    void create() throws Exception {
        channelTable.action(CHANNEL_CREATE, Names.FORK);
        waitGui().until().element(forkTable.getRoot()).is().visible();

        forkTable.select(FORK_CREATE);
        forkTable.action(FORK_CREATE, Names.PROTOCOL);
        waitGui().until().element(forkProtocolTable.getRoot()).is().visible();

        crud.create(forkProtocolAddress(CHANNEL_CREATE, FORK_CREATE, FORK_PROTOCOL_CREATE), forkProtocolTable,
                FORK_PROTOCOL_CREATE);
    }

    @Test
    void update() throws Exception {
        channelTable.action(CHANNEL_CREATE, Names.FORK);
        waitGui().until().element(forkTable.getRoot()).is().visible();

        forkTable.select(FORK_CREATE);
        forkTable.action(FORK_CREATE, Names.PROTOCOL);
        waitGui().until().element(forkProtocolTable.getRoot()).is().visible();

        forkProtocolTable.select(FORK_PROTOCOL_UPDATE);
        crud.update(forkProtocolAddress(CHANNEL_CREATE, FORK_CREATE, FORK_PROTOCOL_UPDATE), forkProtocolForm,
                PROPERTIES, Random.properties());
    }

    @Test
    void remove() throws Exception {
        channelTable.action(CHANNEL_CREATE, Names.FORK);
        waitGui().until().element(forkTable.getRoot()).is().visible();

        forkTable.select(FORK_CREATE);
        forkTable.action(FORK_CREATE, Names.PROTOCOL);
        waitGui().until().element(forkProtocolTable.getRoot()).is().visible();

        crud.delete(forkProtocolAddress(CHANNEL_CREATE, FORK_CREATE, FORK_PROTOCOL_DELETE), forkProtocolTable,
                FORK_PROTOCOL_DELETE);
    }
}
