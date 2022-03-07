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
package org.jboss.hal.testsuite.test.configuration.messaging;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.MessagingPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.GLOBAL_MAX_SIZE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SUBSYSTEM_ADDRESS;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class GlobalSettingsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @Page MessagingPage page;
    @Inject CrudOperations crudOperations;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        form = page.getConfigurationForm();
    }

    @Test
    public void update() throws Exception {
        crudOperations.update(SUBSYSTEM_ADDRESS, form, GLOBAL_MAX_SIZE, 123);
    }
}
