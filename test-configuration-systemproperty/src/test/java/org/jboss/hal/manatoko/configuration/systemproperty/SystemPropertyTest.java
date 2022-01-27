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
package org.jboss.hal.manatoko.configuration.systemproperty;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.manatoko.CrudOperations;
import org.jboss.hal.manatoko.Random;
import org.jboss.hal.manatoko.fragment.FormFragment;
import org.jboss.hal.manatoko.fragment.TableFragment;
import org.jboss.hal.manatoko.page.SystemPropertyPage;
import org.jboss.hal.manatoko.test.ManatokoTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.VALUE;
import static org.jboss.hal.manatoko.fixture.SystemPropertyFixtures.CREATE_NAME;
import static org.jboss.hal.manatoko.fixture.SystemPropertyFixtures.CREATE_VALUE;
import static org.jboss.hal.manatoko.fixture.SystemPropertyFixtures.DELETE_NAME;
import static org.jboss.hal.manatoko.fixture.SystemPropertyFixtures.DELETE_VALUE;
import static org.jboss.hal.manatoko.fixture.SystemPropertyFixtures.READ_NAME;
import static org.jboss.hal.manatoko.fixture.SystemPropertyFixtures.READ_VALUE;
import static org.jboss.hal.manatoko.fixture.SystemPropertyFixtures.UPDATE_NAME;
import static org.jboss.hal.manatoko.fixture.SystemPropertyFixtures.UPDATE_VALUE;
import static org.jboss.hal.manatoko.fixture.SystemPropertyFixtures.systemPropertyAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemPropertyTest extends ManatokoTest {

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(systemPropertyAddress(READ_NAME), Values.empty().and(VALUE, READ_VALUE));
        operations.add(systemPropertyAddress(UPDATE_NAME), Values.empty().and(VALUE, UPDATE_VALUE));
        operations.add(systemPropertyAddress(DELETE_NAME), Values.empty().and(VALUE, DELETE_VALUE));
    }

    @AfterAll
    static void teardownModel() throws Exception {
        try (OnlineManagementClient client = wildFly.managementClient()) {
            Operations operations = new Operations(client);
            operations.removeIfExists(systemPropertyAddress(CREATE_NAME));
            operations.removeIfExists(systemPropertyAddress(READ_NAME));
            operations.removeIfExists(systemPropertyAddress(UPDATE_NAME));
            operations.removeIfExists(systemPropertyAddress(DELETE_NAME));
        }
    }

    @Inject
    CrudOperations crud;

    @Page
    SystemPropertyPage page;

    TableFragment table;
    FormFragment form;

    @BeforeEach
    void beforeEach() {
        page.navigate();
        form = page.getForm();
        table = page.getTable();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(systemPropertyAddress(CREATE_NAME), table, form -> {
            form.text(NAME, CREATE_NAME);
            form.text(VALUE, CREATE_VALUE);
        });
    }

    @Test
    void read() {
        table.select(READ_NAME);
        assertEquals(READ_VALUE, form.value(VALUE));
    }

    @Test
    void update() throws Exception {
        table.select(UPDATE_NAME);
        crud.update(systemPropertyAddress(UPDATE_NAME), form, VALUE, Random.name());
    }

    @Test
    void delete() throws Exception {
        crud.delete(systemPropertyAddress(DELETE_NAME), table, DELETE_NAME);
    }
}
