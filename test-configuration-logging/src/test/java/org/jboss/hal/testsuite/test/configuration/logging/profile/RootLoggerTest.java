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
import org.jboss.hal.testsuite.command.AddRemoteSocketBinding;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.LoggingFixtures;
import org.jboss.hal.testsuite.model.AvailablePortFinder;
import org.jboss.hal.testsuite.model.ConfigUtils;
import org.jboss.hal.testsuite.page.configuration.LoggingConfigurationPage;
import org.jboss.hal.testsuite.page.configuration.LoggingProfileConfigurationPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.test.configuration.logging.AbstractRootLoggerTest;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.OUTBOUND_SOCKET_BINDING_REF;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.NAME;

@Manatoko
@Testcontainers
class RootLoggerTest extends AbstractRootLoggerTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);
    static final String LOGGING_PROFILE = "logger-profile-" + Random.name();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations ops = new Operations(client);

        AddRemoteSocketBinding addRemoteSocketBinding = new AddRemoteSocketBinding(OUTBOUND_SOCKET_BINDING_REF,
                ConfigUtils.getDefaultHost(), AvailablePortFinder.getNextAvailableTCPPort());
        client.apply(addRemoteSocketBinding);
        ops.add(LoggingFixtures.LoggingProfile.loggingProfileAddress(LOGGING_PROFILE)).assertSuccess();
        ops.add(LoggingFixtures.LoggingProfile.rootLoggerAddress(LOGGING_PROFILE)).assertSuccess();
    }

    @Inject Console console;
    @Page LoggingProfileConfigurationPage page;

    @Override
    protected Address rootLoggerAddress() {
        return LoggingFixtures.LoggingProfile.rootLoggerAddress(LOGGING_PROFILE);
    }

    @Override
    protected void navigateToPage() {
        page.navigate(NAME, LOGGING_PROFILE);
        console.verticalNavigation().selectPrimary("logging-profile-root-logger-item");
    }

    @Override
    protected LoggingConfigurationPage getPage() {
        return page;
    }
}
