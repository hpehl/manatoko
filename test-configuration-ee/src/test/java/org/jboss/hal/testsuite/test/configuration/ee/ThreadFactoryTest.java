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
package org.jboss.hal.testsuite.test.configuration.ee;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.EEPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.JNDI_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.THREAD_FACTORY_CREATE;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.THREAD_FACTORY_DELETE;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.THREAD_FACTORY_READ;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.THREAD_FACTORY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.threadFactoryAddress;

@Manatoko
@Testcontainers
class ThreadFactoryTest {

    @Container static Browser browser = new Browser();

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(threadFactoryAddress(THREAD_FACTORY_READ), Values.of(JNDI_NAME, Random.jndiName()));
        operations.add(threadFactoryAddress(THREAD_FACTORY_UPDATE), Values.of(JNDI_NAME, Random.jndiName()));
        operations.add(threadFactoryAddress(THREAD_FACTORY_DELETE), Values.of(JNDI_NAME, Random.jndiName()));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page EEPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary(Ids.EE_SERVICES_ITEM, Ids.EE_MANAGED_THREAD_FACTORY);

        table = page.getThreadFactoryTable();
        form = page.getThreadFactoryForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(threadFactoryAddress(THREAD_FACTORY_CREATE), table, form -> {
            form.text(NAME, THREAD_FACTORY_CREATE);
            form.text(JNDI_NAME, Random.jndiName());
        });
    }

    @Test
    void update() throws Exception {
        table.select(THREAD_FACTORY_UPDATE);
        crud.update(threadFactoryAddress(THREAD_FACTORY_UPDATE), form, JNDI_NAME, Random.jndiName());
    }

    @Test
    void reset() throws Exception {
        table.select(THREAD_FACTORY_UPDATE);
        crud.reset(threadFactoryAddress(THREAD_FACTORY_UPDATE), form);
    }

    @Test
    void delete() throws Exception {
        crud.delete(threadFactoryAddress(THREAD_FACTORY_DELETE), table, THREAD_FACTORY_DELETE);
    }
}
