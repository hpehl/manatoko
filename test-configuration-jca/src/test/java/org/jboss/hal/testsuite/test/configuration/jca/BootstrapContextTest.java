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
package org.jboss.hal.testsuite.test.configuration.jca;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
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
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.WORKMANAGER;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.BC_CREATE;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.BC_DELETE;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.BC_READ;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.bootstrapContextAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class BootstrapContextTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(bootstrapContextAddress(BC_READ), Values.of(NAME, BC_READ).and(WORKMANAGER, DEFAULT));
        operations.add(bootstrapContextAddress(BC_DELETE), Values.of(NAME, BC_DELETE).and(WORKMANAGER, DEFAULT));
        Administration administration = new Administration(client);
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page JcaPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.JCA_BOOTSTRAP_CONTEXT_ITEM);

        form = page.getBootstrapContextForm();
        table = page.getBootstrapContextTable();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(bootstrapContextAddress(BC_CREATE), table, form -> {
            form.text(NAME, BC_CREATE);
            form.text(WORKMANAGER, DEFAULT);
        });
    }

    @Test
    void read() {
        table.select(BC_READ);
        assertEquals(BC_READ, form.value(NAME));
        assertEquals(DEFAULT, form.value(WORKMANAGER));
    }

    @Test
    void delete() throws Exception {
        crud.delete(bootstrapContextAddress(BC_DELETE), table, BC_DELETE);
    }
}
