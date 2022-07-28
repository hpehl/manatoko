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
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.FilterPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.FILTER_CREATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.FILTER_DELETE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.gzipAddress;

@Manatoko
@Testcontainers
public class GzipFilterTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @BeforeAll
    static void setupModel() throws Exception {
        Operations operations = new Operations(wildFly.managementClient());
        operations.add(gzipAddress(FILTER_DELETE));
    }

    @Inject Console console;
    @Page FilterPage page;
    @Inject CrudOperations crud;
    TableFragment table;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.build("undertow-gzip", "item"));
        table = page.getGzipTable();
    }

    @Test
    void create() throws Exception {
        crud.create(gzipAddress(FILTER_CREATE), table, form -> form.text(NAME, FILTER_CREATE));
    }

    @Test
    void delete() throws Exception {
        crud.delete(gzipAddress(FILTER_DELETE), table, FILTER_DELETE);
    }
}
