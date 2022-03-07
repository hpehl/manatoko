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
package org.jboss.hal.testsuite.test.configuration.logging.subsystem;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.page.configuration.LoggingSubsystemConfigurationPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.ADD_LOGGING_API_DEPENDENCIES;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.SUBSYSTEM_ADDRESS;

@Manatoko
@Testcontainers
class ConfigurationTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page LoggingSubsystemConfigurationPage page;

    @Test
    public void updateConfiguration() throws Exception {
        page.navigate();
        console.verticalNavigation().selectPrimary("logging-config-item");
        crud.update(SUBSYSTEM_ADDRESS, page.getConfigurationForm(), ADD_LOGGING_API_DEPENDENCIES, false);
    }

    @Test
    public void resetConfiguration() throws Exception {
        page.navigate();
        console.verticalNavigation().selectPrimary("logging-config-item");
        crud.reset(SUBSYSTEM_ADDRESS, page.getConfigurationForm());
    }
}