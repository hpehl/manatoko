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
package org.jboss.hal.testsuite.test.configuration.ee;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.EEPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.jboss.hal.testsuite.test.Manatoko;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.JNDI_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.SCHEDULED_EXECUTOR_CREATE;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.SCHEDULED_EXECUTOR_DELETE;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.SCHEDULED_EXECUTOR_READ;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.SCHEDULED_EXECUTOR_UPDATE;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.scheduledExecutorAddress;

@Manatoko
@Testcontainers
class ScheduledExecutorTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(scheduledExecutorAddress(SCHEDULED_EXECUTOR_READ), Values.of(JNDI_NAME, Random.jndiName()));
        operations.add(scheduledExecutorAddress(SCHEDULED_EXECUTOR_UPDATE), Values.of(JNDI_NAME, Random.jndiName()));
        operations.add(scheduledExecutorAddress(SCHEDULED_EXECUTOR_DELETE), Values.of(JNDI_NAME, Random.jndiName()));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page EEPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary(Ids.EE_SERVICES_ITEM, Ids.EE_MANAGED_EXECUTOR_SCHEDULED);

        table = page.getExecutorScheduledTable();
        form = page.getExecutorScheduledForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        String jndiName = Random.jndiName();
        crud.create(scheduledExecutorAddress(SCHEDULED_EXECUTOR_CREATE), table, form -> {
            form.text(NAME, SCHEDULED_EXECUTOR_CREATE);
            form.text(JNDI_NAME, jndiName);
        });
    }

    @Test
    void update() throws Exception {
        table.select(SCHEDULED_EXECUTOR_UPDATE);
        crud.update(scheduledExecutorAddress(SCHEDULED_EXECUTOR_UPDATE), form, JNDI_NAME, Random.jndiName());
    }

    @Test
    void reset() throws Exception {
        table.select(SCHEDULED_EXECUTOR_UPDATE);
        crud.reset(scheduledExecutorAddress(SCHEDULED_EXECUTOR_UPDATE), form);
    }

    @Test
    void delete() throws Exception {
        crud.delete(scheduledExecutorAddress(SCHEDULED_EXECUTOR_DELETE), table, SCHEDULED_EXECUTOR_DELETE);
    }
}
