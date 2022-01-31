/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.hal.manatoko.configuration.batch;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.manatoko.Console;
import org.jboss.hal.manatoko.CrudOperations;
import org.jboss.hal.manatoko.fragment.FormFragment;
import org.jboss.hal.manatoko.page.BatchPage;
import org.jboss.hal.manatoko.test.WildFlyTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.RESTART_JOBS_ON_RESUME;
import static org.jboss.hal.manatoko.fixture.BatchFixtures.SUBSYSTEM_ADDRESS;

class BatchConfigurationTest extends WildFlyTest {

    private static boolean restart;

    @BeforeAll
    static void setupModel() throws Exception {
        Operations operations = new Operations(wildFly.managementClient());
        restart = operations.readAttribute(SUBSYSTEM_ADDRESS, RESTART_JOBS_ON_RESUME).booleanValue();
    }

    @Page BatchPage page;
    @Inject Console console;
    @Inject CrudOperations crud;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary("batch-configuration-item");
        form = page.getConfigurationForm();
    }

    @Test
    void update() throws Exception {
        crud.update(SUBSYSTEM_ADDRESS, form, RESTART_JOBS_ON_RESUME, !restart);
    }

    @Test
    void reset() throws Exception {
        crud.reset(SUBSYSTEM_ADDRESS, form);
    }
}
