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
package org.jboss.hal.testsuite.test.configuration.jca;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.JcaPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jboss.hal.dmr.ModelDescriptionConstants.ENABLED;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.ARCHIVE_VALIDATION_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.BEAN_VALIDATION_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.CACHED_CONNECTION_MANAGER_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.JcaFixtures.DEBUG;

@Manatoko
@Testcontainers
class JcaConfigurationTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, STANDALONE);

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page JcaPage page;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.JCA_CONFIGURATION_ITEM);
    }

    @Test
    void updateCachedConnectionManager() throws Exception {
        page.getConfigurationTabs().select(Ids.JCA_CCM_TAB);
        form = page.getCachedConnectionManagerForm();
        crud.update(CACHED_CONNECTION_MANAGER_ADDRESS, form, DEBUG, true);
    }

    @Test
    void resetCachedConnectionManager() throws Exception {
        page.getConfigurationTabs().select(Ids.JCA_CCM_TAB);
        form = page.getCachedConnectionManagerForm();
        crud.reset(CACHED_CONNECTION_MANAGER_ADDRESS, form);
    }

    @Test
    void updateArchiveValidation() throws Exception {
        page.getConfigurationTabs().select(Ids.JCA_ARCHIVE_VALIDATION_TAB);
        form = page.getArchiveValidationForm();
        crud.update(ARCHIVE_VALIDATION_ADDRESS, form, ENABLED, false);
    }

    @Test
    void resetArchiveValidation() throws Exception {
        page.getConfigurationTabs().select(Ids.JCA_ARCHIVE_VALIDATION_TAB);
        form = page.getArchiveValidationForm();
        crud.reset(ARCHIVE_VALIDATION_ADDRESS, form);
    }

    @Test
    void updateBeanValidation() throws Exception {
        page.getConfigurationTabs().select(Ids.JCA_BEAN_VALIDATION_TAB);
        form = page.getBeanValidationForm();
        crud.update(BEAN_VALIDATION_ADDRESS, form, ENABLED, false);
    }

    @Test
    void resetBeanValidation() throws Exception {
        page.getConfigurationTabs().select(Ids.JCA_BEAN_VALIDATION_TAB);
        form = page.getBeanValidationForm();
        crud.reset(BEAN_VALIDATION_ADDRESS, form);
    }
}
