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
import org.jboss.hal.testsuite.container.WildFlyContainer;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.GLOBAL_MODULES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.GLOBAL_MODULES_CREATE;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.GLOBAL_MODULES_DELETE;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.SUBSYSTEM_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.globalModule;

@Manatoko
@Testcontainers
class GlobalModulesTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.writeListAttribute(SUBSYSTEM_ADDRESS, GLOBAL_MODULES,
                globalModule(GLOBAL_MODULES_DELETE));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page EEPage page;
    TableFragment table;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.EE_GLOBAL_MODULES_ITEM);
        table = page.getGlobalModulesTable();
    }

    @Test
    void create() throws Exception {
        crud.create(SUBSYSTEM_ADDRESS, table,
                form -> form.text(NAME, GLOBAL_MODULES_CREATE),
                resourceVerifier -> resourceVerifier.verifyListAttributeContainsValue(GLOBAL_MODULES,
                        globalModule(GLOBAL_MODULES_CREATE)));
    }

    @Test
    void delete() throws Exception {
        crud.delete(SUBSYSTEM_ADDRESS, table, GLOBAL_MODULES_DELETE,
                resourceVerifier -> resourceVerifier.verifyListAttributeDoesNotContainValue(GLOBAL_MODULES,
                        globalModule(GLOBAL_MODULES_DELETE)));
    }
}
