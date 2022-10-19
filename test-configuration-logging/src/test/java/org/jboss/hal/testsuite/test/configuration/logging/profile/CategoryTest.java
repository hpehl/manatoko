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
package org.jboss.hal.testsuite.test.configuration.logging.profile;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.LoggingFixtures;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.LoggingProfileConfigurationPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.test.configuration.logging.AbstractCategoryTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.NAME;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.Category.CATEGORY_DELETE;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.Category.CATEGORY_READ;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.Category.CATEGORY_UPDATE;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class CategoryTest extends AbstractCategoryTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @Container static Browser browser = new Browser();
    static final String LOGGING_PROFILE = "logging-profile-" + Random.name();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations ops = new Operations(client);
        ops.add(LoggingFixtures.LoggingProfile.loggingProfileAddress(LOGGING_PROFILE)).assertSuccess();
        ops.add(LoggingFixtures.LoggingProfile.categoryAddress(LOGGING_PROFILE, CATEGORY_READ)).assertSuccess();
        ops.add(LoggingFixtures.LoggingProfile.categoryAddress(LOGGING_PROFILE, CATEGORY_UPDATE)).assertSuccess();
        ops.add(LoggingFixtures.LoggingProfile.categoryAddress(LOGGING_PROFILE, CATEGORY_DELETE)).assertSuccess();
    }

    @Inject Console console;
    @Page LoggingProfileConfigurationPage page;

    @Override
    protected Address categoryAddress(String name) {
        return LoggingFixtures.LoggingProfile.categoryAddress(LOGGING_PROFILE, name);
    }

    @Override
    protected void navigateToPage() {
        page.navigate(NAME, LOGGING_PROFILE);
        console.verticalNavigation().selectPrimary("logging-profile-category-item");
    }

    @Override
    protected TableFragment getCategoryTable() {
        return page.getCategoryTable();
    }

    @Override
    protected FormFragment getCategoryForm() {
        return page.getCategoryForm();
    }
}
