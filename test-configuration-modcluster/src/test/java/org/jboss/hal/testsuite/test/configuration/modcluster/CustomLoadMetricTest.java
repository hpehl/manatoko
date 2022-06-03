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
package org.jboss.hal.testsuite.test.configuration.modcluster;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.ModclusterPage;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.CLASS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CONNECTOR;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.CLASS_NAME;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.CUSTOM_LOAD_METRIC_CREATE;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.CUSTOM_LOAD_METRIC_DELETE;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.CUSTOM_LOAD_METRIC_UPDATE;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.PROXY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.WEIGHT;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.customLoadMetricAddress;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.loadProviderDynamicAddress;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.proxyAddress;

@Manatoko
@Testcontainers
class CustomLoadMetricTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        Batch proxyAdd = new Batch();
        proxyAdd.add(proxyAddress(PROXY_UPDATE), Values.of(CONNECTOR, DEFAULT));
        proxyAdd.add(loadProviderDynamicAddress(PROXY_UPDATE));
        operations.batch(proxyAdd);
        operations.add(customLoadMetricAddress(PROXY_UPDATE, CUSTOM_LOAD_METRIC_DELETE), Values.of(CLASS, CLASS_NAME));
        operations.add(customLoadMetricAddress(PROXY_UPDATE, CUSTOM_LOAD_METRIC_UPDATE), Values.of(CLASS, CLASS_NAME));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page ModclusterPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate(NAME, PROXY_UPDATE);
        console.verticalNavigation().selectPrimary("custom-load-metrics-item");
        table = page.getCustomLoadMetricTable();
        form = page.getCustomLoadMetricForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(customLoadMetricAddress(PROXY_UPDATE, CUSTOM_LOAD_METRIC_CREATE), table, f -> {
            f.text(NAME, CUSTOM_LOAD_METRIC_CREATE);
            f.text(CLASS, CLASS_NAME);
        },
                ver -> ver.verifyAttribute(CLASS, CLASS_NAME));
    }

    @Test
    void reset() throws Exception {
        table.select(CUSTOM_LOAD_METRIC_UPDATE);
        crud.reset(customLoadMetricAddress(PROXY_UPDATE, CUSTOM_LOAD_METRIC_UPDATE), form);
    }

    @Test
    void update() throws Exception {
        table.select(CUSTOM_LOAD_METRIC_UPDATE);
        crud.update(customLoadMetricAddress(PROXY_UPDATE, CUSTOM_LOAD_METRIC_UPDATE), form, WEIGHT, Random.number());
    }

    @Test
    void updateCapacity() throws Exception {
        // update an attribute of type DOUBLE
        table.select(CUSTOM_LOAD_METRIC_UPDATE);
        crud.update(customLoadMetricAddress(PROXY_UPDATE, CUSTOM_LOAD_METRIC_UPDATE), form, "capacity",
                Random.numberDouble());
    }

    @Test
    void delete() throws Exception {
        crud.delete(customLoadMetricAddress(PROXY_UPDATE, CUSTOM_LOAD_METRIC_DELETE), table, CUSTOM_LOAD_METRIC_DELETE);
    }
}
