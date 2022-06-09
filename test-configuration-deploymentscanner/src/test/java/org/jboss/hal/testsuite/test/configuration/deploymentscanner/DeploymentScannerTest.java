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
package org.jboss.hal.testsuite.test.configuration.deploymentscanner;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.DeploymentScannerPage;
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
import static org.jboss.hal.dmr.ModelDescriptionConstants.PATH;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.DS_CREATE;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.DS_DELETE;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.DS_READ;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.DS_UPDATE;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.DS_UPDATE_RESET;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.deploymentScannerAddress;
import static org.jboss.hal.testsuite.fixtures.DeploymentScannerFixtures.path;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class DeploymentScannerTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(deploymentScannerAddress(DS_READ), Values.of(PATH, path(DS_READ)));
        operations.add(deploymentScannerAddress(DS_UPDATE), Values.of(PATH, path(DS_UPDATE)));
        operations.add(deploymentScannerAddress(DS_UPDATE_RESET), Values.of(PATH, path(DS_UPDATE_RESET)));
        operations.add(deploymentScannerAddress(DS_DELETE), Values.of(PATH, path(DS_DELETE)));
    }

    @Page DeploymentScannerPage page;
    @Inject CrudOperations crud;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        form = page.getForm();
        table = page.getTable();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(deploymentScannerAddress(DS_CREATE), table, form -> {
            form.text(NAME, DS_CREATE);
            form.text(PATH, path(DS_CREATE));
        });
    }

    @Test
    void read() {
        table.select(DS_READ);
        assertEquals(path(DS_READ), form.value(PATH));
    }

    @Test
    void update() throws Exception {
        table.select(DS_UPDATE);
        crud.update(deploymentScannerAddress(DS_UPDATE), form, PATH, Random.name() + "/" + Random.name());
    }

    @Test
    void reset() throws Exception {
        table.select(DS_UPDATE_RESET);
        crud.reset(deploymentScannerAddress(DS_UPDATE), form);
    }

    @Test
    void delete() throws Exception {
        crud.delete(deploymentScannerAddress(DS_DELETE), table, DS_DELETE);
    }
}
