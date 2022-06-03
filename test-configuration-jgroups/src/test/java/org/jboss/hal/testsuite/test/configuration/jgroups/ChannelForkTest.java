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
import org.jboss.hal.testsuite.container.WildFlyContainer;
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
import static org.jboss.hal.dmr.ModelDescriptionConstants.STACK;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.CHANNEL_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.FORK_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.FORK_DELETE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.TCP;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.channelAddress;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.forkAddress;

@Manatoko
@Testcontainers
class ChannelForkTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(channelAddress(CHANNEL_CREATE), Values.of(STACK, TCP));
        operations.add(forkAddress(CHANNEL_CREATE, FORK_DELETE));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page JGroupsPage page;
    TableFragment channelTable;
    TableFragment forkTable;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectPrimary("jgroups-channel-item");

        channelTable = page.getChannelTable();
        forkTable = page.getChannelForkTable();
    }

    @Test
    void create() throws Exception {
        channelTable.action(CHANNEL_CREATE, Names.FORK);
        waitGui().until().element(forkTable.getRoot()).is().visible();

        crud.create(forkAddress(CHANNEL_CREATE, FORK_CREATE), forkTable, FORK_CREATE);
    }

    @Test
    void remove() throws Exception {
        channelTable.action(CHANNEL_CREATE, Names.FORK);
        waitGui().until().element(forkTable.getRoot()).is().visible();
        crud.delete(forkAddress(CHANNEL_CREATE, FORK_DELETE), forkTable, FORK_DELETE);
    }
}
