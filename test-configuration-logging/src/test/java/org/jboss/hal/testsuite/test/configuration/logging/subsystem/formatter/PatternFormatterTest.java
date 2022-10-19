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
package org.jboss.hal.testsuite.test.configuration.logging.subsystem.formatter;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.LoggingFixtures;
import org.jboss.hal.testsuite.page.configuration.LoggingConfigurationPage;
import org.jboss.hal.testsuite.page.configuration.LoggingSubsystemConfigurationPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.test.configuration.logging.AbstractPatternFormatterTest;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.LOGGING_FORMATTER_ITEM;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.PatternFormatter.PATTERN_FORMATTER_DELETE;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.PatternFormatter.PATTERN_FORMATTER_UPDATE;

@Manatoko
@Testcontainers
class PatternFormatterTest extends AbstractPatternFormatterTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations ops = new Operations(client);
        ops.add(LoggingFixtures.PatternFormatter.patternFormatterAddress(PATTERN_FORMATTER_UPDATE)).assertSuccess();
        ops.add(LoggingFixtures.PatternFormatter.patternFormatterAddress(PATTERN_FORMATTER_DELETE)).assertSuccess();
    }

    @Inject Console console;
    @Page LoggingSubsystemConfigurationPage page;

    @Override
    protected void navigateToPage() {
        page.navigate();
        console.verticalNavigation().selectSecondary(LOGGING_FORMATTER_ITEM,
                "logging-formatter-pattern-item");
    }

    @Override
    protected LoggingConfigurationPage getPage() {
        return page;
    }

    @Override
    protected Address patternFormatterAddress(String name) {
        return LoggingFixtures.PatternFormatter.patternFormatterAddress(name);
    }
}
