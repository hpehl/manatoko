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
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.WM_CREATE;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.WM_DELETE;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.WM_UPDATE;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.workmanagerAddress;

@Manatoko
@Testcontainers
class WorkmanagerTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(workmanagerAddress(WM_UPDATE), Values.of(NAME, WM_UPDATE));
        operations.add(workmanagerAddress(WM_DELETE), Values.of(NAME, WM_DELETE));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page JcaPage page;
    TableFragment table;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.JCA_WORKMANAGER_ITEM);

        table = page.getWmTable();
    }

    @Test
    void create() throws Exception {
        crud.create(workmanagerAddress(WM_CREATE), table, WM_CREATE);
    }

    @Test
    void delete() throws Exception {
        crud.delete(workmanagerAddress(WM_DELETE), table, WM_DELETE);
    }
}
