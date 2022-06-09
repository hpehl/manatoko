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
import static org.jboss.hal.dmr.ModelDescriptionConstants.PROPERTIES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.JGROUPS_TCP;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.PROTOCOL_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.PROTOCOL_DELETE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.PROTOCOL_UPDATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.STACK_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.TRANSPORT_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.protocolAddress;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.stackAddress;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.transportAddress;

@Manatoko
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
public class StackProtocolTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        Batch stackCreate = new Batch();
        stackCreate.add(stackAddress(STACK_CREATE));
        stackCreate.add(transportAddress(STACK_CREATE, TRANSPORT_CREATE), Values.of(SOCKET_BINDING, JGROUPS_TCP));
        operations.batch(stackCreate);
        operations.add(protocolAddress(STACK_CREATE, PROTOCOL_UPDATE));
        operations.add(protocolAddress(STACK_CREATE, PROTOCOL_DELETE));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page JGroupsPage page;
    FormFragment protocolForm;
    TableFragment stackTable;
    TableFragment protocolTable;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectPrimary("jgroups-stack-item");

        stackTable = page.getStackTable();
        protocolTable = page.getProtocolTable();
        protocolForm = page.getProtocolForm();
        protocolTable.bind(protocolForm);
    }

    // ----- order of tests are important

    @Test
    void create() throws Exception {
        stackTable.action(STACK_CREATE, Names.PROTOCOL);
        waitGui().until().element(protocolTable.getRoot()).is().visible();
        crud.create(protocolAddress(STACK_CREATE, PROTOCOL_CREATE), protocolTable, PROTOCOL_CREATE);
    }

    @Test()
    void update() throws Exception {
        stackTable.action(STACK_CREATE, Names.PROTOCOL);
        waitGui().until().element(protocolTable.getRoot()).is().visible();
        protocolTable.select(PROTOCOL_UPDATE);
        crud.update(protocolAddress(STACK_CREATE, PROTOCOL_UPDATE), protocolForm, PROPERTIES, Random.properties());
    }

    @Test
    void zzzDelete() throws Exception {
        stackTable.action(STACK_CREATE, Names.PROTOCOL);
        waitGui().until().element(protocolTable.getRoot()).is().visible();
        crud.delete(protocolAddress(STACK_CREATE, PROTOCOL_DELETE), protocolTable, PROTOCOL_DELETE);
    }
}
