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
import org.jboss.hal.testsuite.container.Browser;
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
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STATISTICS_ENABLED;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TRANSPORT;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.HA;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.JGROUPS_TCP;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.STACK_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.STACK_DELETE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.STACK_UPDATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.TRANSPORT_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.stackAddress;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.transportAddress;

@Manatoko
@Testcontainers
class StackTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(HA);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        Batch stackUpdate = new Batch();
        stackUpdate.add(stackAddress(STACK_UPDATE));
        stackUpdate.add(transportAddress(STACK_UPDATE, TRANSPORT_CREATE), Values.of(SOCKET_BINDING, JGROUPS_TCP));
        Batch stackDelete = new Batch();
        stackDelete.add(stackAddress(STACK_DELETE));
        stackDelete.add(transportAddress(STACK_DELETE, TRANSPORT_CREATE), Values.of(SOCKET_BINDING, JGROUPS_TCP));
        operations.batch(stackUpdate);
        operations.batch(stackDelete);
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page JGroupsPage page;
    FormFragment form;
    TableFragment table;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectPrimary("jgroups-stack-item");

        table = page.getStackTable();
        form = page.getStackForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(stackAddress(STACK_CREATE), table, form -> {
            form.text(NAME, STACK_CREATE);
            form.text(TRANSPORT, Random.name());
            form.text(SOCKET_BINDING, JGROUPS_TCP);
        });
    }

    @Test
    void update() throws Exception {
        table.select(STACK_UPDATE);
        crud.update(stackAddress(STACK_UPDATE), form, STATISTICS_ENABLED, true);
    }

    @Test
    void delete() throws Exception {
        crud.delete(stackAddress(STACK_DELETE), table, STACK_DELETE);
    }
}
