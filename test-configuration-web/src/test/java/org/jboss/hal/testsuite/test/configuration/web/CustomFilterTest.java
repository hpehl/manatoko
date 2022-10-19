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
package org.jboss.hal.testsuite.test.configuration.web;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.dmr.ModelNode;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.FilterPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CLASS_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MODULE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.FILTER_CREATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.FILTER_DELETE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.FILTER_UPDATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.PARAMETERS;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.customFilterAddress;

@Manatoko
@Testcontainers
public class CustomFilterTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        Operations operations = new Operations(wildFly.managementClient());
        operations.add(customFilterAddress(FILTER_UPDATE),
                Values.of(CLASS_NAME, Random.name()).and(MODULE, Random.name()));
        operations.add(customFilterAddress(FILTER_DELETE),
                Values.of(CLASS_NAME, Random.name()).and(MODULE, Random.name()));
    }

    @Inject Console console;
    @Page FilterPage page;
    @Inject CrudOperations crud;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.build("undertow-custom-filter", "item"));
        form = page.getCustomFilterForm();
        table = page.getCustomFilterTable();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(customFilterAddress(FILTER_CREATE), table, form -> {
            form.text(NAME, FILTER_CREATE);
            form.text(CLASS_NAME, Random.name());
            form.text(MODULE, Random.name());
        });
    }

    @Test
    void update() throws Exception {
        String module = Random.name();
        String key = Random.name();
        String value = Random.name();
        ModelNode parameters = new ModelNode();
        parameters.get(key).set(value);

        table.select(FILTER_UPDATE);
        crud.update(customFilterAddress(FILTER_UPDATE), form, f -> {
            f.text(MODULE, module);
            f.properties(PARAMETERS).add(key, value);
        }, resourceVerifier -> {
            resourceVerifier.verifyAttribute(MODULE, module);
            resourceVerifier.verifyAttribute(PARAMETERS, parameters);
        });
    }

    @Test
    void delete() throws Exception {
        crud.delete(customFilterAddress(FILTER_DELETE), table, FILTER_DELETE);
    }
}
