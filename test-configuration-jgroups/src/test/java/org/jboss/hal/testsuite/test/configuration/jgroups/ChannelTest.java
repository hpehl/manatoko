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

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STACK;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.CHANNEL_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.CHANNEL_DELETE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.CHANNEL_UPDATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.CLUSTER;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.TCP;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.channelAddress;

@Manatoko
@Testcontainers
class ChannelTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(channelAddress(CHANNEL_UPDATE), Values.of(STACK, TCP));
        operations.add(channelAddress(CHANNEL_DELETE), Values.of(STACK, TCP));
    }

    @Page JGroupsPage page;
    @Inject CrudOperations crud;
    @Inject Console console;
    FormFragment form;
    TableFragment table;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectPrimary("jgroups-channel-item");

        table = page.getChannelTable();
        form = page.getChannelForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(channelAddress(CHANNEL_CREATE), table, form -> {
            form.text(NAME, CHANNEL_CREATE);
            form.text(STACK, TCP);
        });
    }

    @Test
    void update() throws Exception {
        table.select(CHANNEL_UPDATE);
        crud.update(channelAddress(CHANNEL_UPDATE), form, CLUSTER, Random.name());
    }

    @Test
    void updateEmptyStack() {
        table.select(CHANNEL_UPDATE);
        crud.updateWithError(form, f -> f.clear(STACK), STACK);
    }

    @Test
    void reset() throws Exception {
        table.select(CHANNEL_UPDATE);
        crud.reset(channelAddress(CHANNEL_UPDATE), form);
    }

    @Test
    void delete() throws Exception {
        crud.delete(channelAddress(CHANNEL_DELETE), table, CHANNEL_DELETE);
    }
}
