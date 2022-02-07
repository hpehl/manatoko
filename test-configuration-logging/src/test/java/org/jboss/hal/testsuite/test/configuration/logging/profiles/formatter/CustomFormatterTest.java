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
package org.jboss.hal.testsuite.test.configuration.logging.profiles.formatter;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.dmr.ModelNodeGenerator;
import org.jboss.hal.testsuite.fixtures.LoggingFixtures;
import org.jboss.hal.testsuite.page.configuration.LoggingConfigurationPage;
import org.jboss.hal.testsuite.page.configuration.LoggingProfileConfigurationPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.test.configuration.logging.AbstractCustomFormatterTest;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CLASS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MODULE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PROPERTIES;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.CUSTOM_FORMATTER_CLASS_1_VALUE;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.CUSTOM_FORMATTER_MODULE_VALUE;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.LOGGING_PROFILE_FORMATTER_ITEM;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.NAME;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.RECORD_DELIMITER_PROPERTY_NAME;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.CustomFormatter.CUSTOM_FORMATTER_DELETE;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.CustomFormatter.CUSTOM_FORMATTER_RESET;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.CustomFormatter.CUSTOM_FORMATTER_UPDATE;

@Manatoko
@Testcontainers
class CustomFormatterTest extends AbstractCustomFormatterTest {

    static final String LOGGING_PROFILE = "logging-profile-" + Random.name();
    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations ops = new Operations(client);
        ops.add(LoggingFixtures.LoggingProfile.loggingProfileAddress(LOGGING_PROFILE)).assertSuccess();
        ops.add(LoggingFixtures.LoggingProfile.customFormatterAddress(LOGGING_PROFILE, CUSTOM_FORMATTER_UPDATE),
                Values.of(MODULE, CUSTOM_FORMATTER_MODULE_VALUE)
                        .and(CLASS, CUSTOM_FORMATTER_CLASS_1_VALUE))
                .assertSuccess();
        ops.add(LoggingFixtures.LoggingProfile.customFormatterAddress(LOGGING_PROFILE, CUSTOM_FORMATTER_RESET),
                Values.of(MODULE, CUSTOM_FORMATTER_MODULE_VALUE)
                        .and(CLASS, CUSTOM_FORMATTER_CLASS_1_VALUE).and(PROPERTIES,
                                new ModelNodeGenerator.ModelNodePropertiesBuilder()
                                        .addProperty(RECORD_DELIMITER_PROPERTY_NAME, Random.name()).build()))
                .assertSuccess();
        ops.add(LoggingFixtures.LoggingProfile.customFormatterAddress(LOGGING_PROFILE, CUSTOM_FORMATTER_DELETE),
                Values.of(MODULE, CUSTOM_FORMATTER_MODULE_VALUE)
                        .and(CLASS, CUSTOM_FORMATTER_CLASS_1_VALUE))
                .assertSuccess();
    }

    @Inject Console console;
    @Page LoggingProfileConfigurationPage page;

    @Override
    protected void navigateToPage() {
        page.navigate(NAME, LOGGING_PROFILE);
        console.verticalNavigation().selectSecondary(LOGGING_PROFILE_FORMATTER_ITEM,
                "logging-profile-formatter-custom-item");
    }

    @Override
    protected LoggingConfigurationPage getPage() {
        return page;
    }

    @Override
    protected Address customFormatterAddress(String name) {
        return LoggingFixtures.LoggingProfile.customFormatterAddress(LOGGING_PROFILE, name);
    }
}
