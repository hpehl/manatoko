/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.fragment.TabsFragment;
import org.jboss.hal.testsuite.page.configuration.JGroupsPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static java.util.Arrays.asList;
import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MAX_THREADS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.JGROUPS_TCP;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.SITE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.STACK_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.TRANSPORT_CREATE;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.stackAddress;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.transportAddress;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.transportThreadPoolAddress;

@Manatoko
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled // TODO Fix failing tests
class StackTransportTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        Batch stackCreate = new Batch();
        stackCreate.add(stackAddress(STACK_CREATE));
        stackCreate.add(transportAddress(STACK_CREATE, TRANSPORT_CREATE), Values.of(SOCKET_BINDING, JGROUPS_TCP));
        operations.batch(stackCreate);
    }

    @Page JGroupsPage page;
    @Inject CrudOperations crud;
    @Inject Console console;
    TableFragment stackTable;
    TableFragment transportTable;
    FormFragment transportAttributesForm;
    FormFragment transportTPDefaultForm;
    TabsFragment threadPoolTab;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectPrimary("jgroups-stack-item");

        threadPoolTab = page.getTransportThreadPoolTab();
        stackTable = page.getStackTable();
        transportTable = page.getTransportTable();
        transportAttributesForm = page.getTransportAttributesForm();
        transportTPDefaultForm = page.getTransportThreadPoolDefaultForm();
        transportTable.bind(asList(transportAttributesForm, transportTPDefaultForm));
    }

    @Test()
    void updateAttributes() throws Exception {
        stackTable.action(STACK_CREATE, Names.TRANSPORT);
        waitGui().until().element(transportTable.getRoot()).is().visible();

        transportTable.select(TRANSPORT_CREATE);
        crud.update(transportAddress(STACK_CREATE, TRANSPORT_CREATE), transportAttributesForm, SITE, Random.name());
    }

    @Test()
    void threadPoolDefaultEdit() throws Exception {
        stackTable.action(STACK_CREATE, Names.TRANSPORT);
        waitGui().until().element(transportTable.getRoot()).is().visible();

        transportTable.select(TRANSPORT_CREATE);
        threadPoolTab.select(Ids.build(Ids.JGROUPS_TRANSPORT_THREADPOOL_DEFAULT_TAB));
        crud.update(transportThreadPoolAddress(STACK_CREATE, TRANSPORT_CREATE, DEFAULT), transportTPDefaultForm,
                MAX_THREADS, 123);
    }

    @Test()
    void threadPoolDefaultReset() throws Exception {
        stackTable.action(STACK_CREATE, Names.TRANSPORT);
        waitGui().until().element(transportTable.getRoot()).is().visible();

        transportTable.select(TRANSPORT_CREATE);
        threadPoolTab.select(Ids.build(Ids.JGROUPS_TRANSPORT_THREADPOOL_DEFAULT_TAB));
        crud.reset(transportThreadPoolAddress(STACK_CREATE, TRANSPORT_CREATE, DEFAULT), transportTPDefaultForm);
    }
}
