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
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.HEADER_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.FILTER_CREATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.FILTER_DELETE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.FILTER_UPDATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.HEADER_VALUE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.responseHeaderAddress;

@Manatoko
@Testcontainers
public class ResponseHeaderTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @BeforeAll
    static void setupModel() throws Exception {
        Operations operations = new Operations(wildFly.managementClient());
        operations.add(responseHeaderAddress(FILTER_UPDATE),
                Values.of(HEADER_NAME, FILTER_UPDATE).and(HEADER_VALUE, Random.name()));
        operations.add(responseHeaderAddress(FILTER_DELETE),
                Values.of(HEADER_NAME, FILTER_DELETE).and(HEADER_VALUE, Random.name()));
    }

    @Inject Console console;
    @Page FilterPage page;
    @Inject CrudOperations crud;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.build("undertow-response-header", "item"));
        table = page.getResponseHeaderTable();
        form = page.getResponseHeaderForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(responseHeaderAddress(FILTER_CREATE), table, form -> {
            form.text(NAME, FILTER_CREATE);
            form.text(HEADER_NAME, Random.name());
            form.text(HEADER_VALUE, Random.name());
        });
    }

    @Test
    void update() throws Exception {
        String value = Random.name();

        table.select(FILTER_UPDATE);
        crud.update(responseHeaderAddress(FILTER_UPDATE), form, f -> f.text(HEADER_VALUE, value),
                resourceVerifier -> resourceVerifier.verifyAttribute(HEADER_VALUE, value));
    }

    @Test
    void delete() throws Exception {
        crud.delete(responseHeaderAddress(FILTER_DELETE), table, FILTER_DELETE);
    }
}
