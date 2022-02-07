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
package org.jboss.hal.testsuite.test.configuration.jca;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.fragment.TabsFragment;
import org.jboss.hal.testsuite.page.configuration.JcaPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static java.util.Arrays.asList;
import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MAX_THREADS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.QUEUE_LENGTH;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.ALLOW_CORE_TIMEOUT;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.LONG_RUNNING;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.SHORT_RUNNING;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.WM_THREAD_POOL_CREATE;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.WM_THREAD_POOL_DELETE;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.WM_THREAD_POOL_READ;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.WM_THREAD_POOL_UPDATE;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.longRunningAddress;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.shortRunningAddress;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.workmanagerAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class ThreadPoolTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(workmanagerAddress(WM_THREAD_POOL_CREATE), Values.of(NAME, WM_THREAD_POOL_CREATE));
        addWorkmanagerWithThreadPools(operations, WM_THREAD_POOL_READ);
        addWorkmanagerWithThreadPools(operations, WM_THREAD_POOL_UPDATE);
        addWorkmanagerWithThreadPools(operations, WM_THREAD_POOL_DELETE);
    }

    private static void addWorkmanagerWithThreadPools(Operations operations, String workmanager) throws Exception {
        operations.add(workmanagerAddress(workmanager), Values.of(NAME, workmanager));
        operations.add(longRunningAddress(workmanager), Values.of(MAX_THREADS, 10).and(QUEUE_LENGTH, 5));
        operations.add(shortRunningAddress(workmanager), Values.of(MAX_THREADS, 10).and(QUEUE_LENGTH, 5));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page JcaPage page;
    TableFragment wmTable;
    TableFragment tpTable;
    TabsFragment tpTabs;
    FormFragment tpAttributesForm;
    FormFragment tpSizingForm;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.JCA_WORKMANAGER_ITEM);
        wmTable = page.getWmTable();
        tpTable = page.getWmThreadPoolTable();
        tpTabs = page.getWmThreadPoolTabs();
        tpAttributesForm = page.getWmThreadPoolAttributesForm();
        tpSizingForm = page.getWmThreadPoolSizingForm();
        tpTable.bind(asList(tpAttributesForm, tpSizingForm));
    }

    @Test
    void create() throws Exception {
        wmTable.action(WM_THREAD_POOL_CREATE, Names.THREAD_POOLS);
        waitGui().until().element(tpTable.getRoot()).is().visible();

        crud.create(longRunningAddress(WM_THREAD_POOL_CREATE), tpTable,
                form -> {
                    form.number(MAX_THREADS, 10);
                    form.number(QUEUE_LENGTH, 5);
                });

        crud.create(shortRunningAddress(WM_THREAD_POOL_CREATE), tpTable,
                form -> {
                    form.number(MAX_THREADS, 10);
                    form.number(QUEUE_LENGTH, 5);
                });
    }

    @Test
    void read() {
        wmTable.action(WM_THREAD_POOL_READ, Names.THREAD_POOLS);
        waitGui().until().element(tpTable.getRoot()).is().visible();

        tpTable.select(LONG_RUNNING);
        tpTabs.select(Ids.build(Ids.JCA_WORKMANAGER, Ids.JCA_THREAD_POOL_ATTRIBUTES_TAB));
        assertEquals(WM_THREAD_POOL_READ, tpAttributesForm.value(NAME));

        tpTable.select(SHORT_RUNNING);
        tpTabs.select(Ids.build(Ids.JCA_WORKMANAGER, Ids.JCA_THREAD_POOL_ATTRIBUTES_TAB));
        assertEquals(WM_THREAD_POOL_READ, tpAttributesForm.value(NAME));
    }

    @Test
    void update() throws Exception {
        wmTable.action(WM_THREAD_POOL_UPDATE, Names.THREAD_POOLS);
        waitGui().until().element(tpTable.getRoot()).is().visible();

        tpTable.select(LONG_RUNNING);
        tpTabs.select(Ids.build(Ids.JCA_WORKMANAGER, Ids.JCA_THREAD_POOL_ATTRIBUTES_TAB));
        crud.update(longRunningAddress(WM_THREAD_POOL_UPDATE), tpAttributesForm, ALLOW_CORE_TIMEOUT, true);

        tpTable.select(SHORT_RUNNING);
        tpTabs.select(Ids.build(Ids.JCA_WORKMANAGER, Ids.JCA_THREAD_POOL_SIZING_TAB));
        crud.update(shortRunningAddress(WM_THREAD_POOL_UPDATE), tpSizingForm, MAX_THREADS, 111);
    }

    @Test
    void delete() throws Exception {
        wmTable.action(WM_THREAD_POOL_DELETE, Names.THREAD_POOLS);
        waitGui().until().element(tpTable.getRoot()).is().visible();

        crud.delete(longRunningAddress(WM_THREAD_POOL_DELETE), tpTable, WM_THREAD_POOL_DELETE);
        crud.delete(shortRunningAddress(WM_THREAD_POOL_DELETE), tpTable, WM_THREAD_POOL_DELETE);
    }
}