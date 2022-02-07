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
package org.jboss.hal.testsuite.test.configuration.logging.profile.handler;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.creaper.command.AddRemoteSocketBinding;
import org.jboss.hal.testsuite.fixtures.LoggingFixtures;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.LoggingConfigurationPage;
import org.jboss.hal.testsuite.page.configuration.LoggingProfileConfigurationPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.test.configuration.logging.AbstractSocketHandlerTest;
import org.jboss.hal.testsuite.util.AvailablePortFinder;
import org.jboss.hal.testsuite.util.ConfigUtils;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.OUTBOUND_SOCKET_BINDING_REF;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PATTERN_FORMATTER;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.LOGGING_PROFILE_HANDLER_ITEM;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.NAMED_FORMATTER;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.PatternFormatter;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.SocketHandler;

@Manatoko
@Testcontainers
class SocketHandlerTest extends AbstractSocketHandlerTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);
    static final String LOGGING_PROFILE = "logging-profile-" + Random.name();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        ops = new Operations(client);

        AddRemoteSocketBinding addRemoteSocketBinding = new AddRemoteSocketBinding(OUTBOUND_SOCKET_BINDING_REF,
                ConfigUtils.getDefaultHost(), AvailablePortFinder.getNextAvailableTCPPort());
        client.apply(addRemoteSocketBinding);
        ops.add(LoggingFixtures.LoggingProfile.loggingProfileAddress(LOGGING_PROFILE)).assertSuccess();
        ops.add(LoggingFixtures.LoggingProfile.loggingProfileAddress(LOGGING_PROFILE)
                .and(PATTERN_FORMATTER, PatternFormatter.PATTERN_FORMATTER_CREATE)).assertSuccess();
        Values params = Values.of(NAMED_FORMATTER, PatternFormatter.PATTERN_FORMATTER_CREATE)
                .and(OUTBOUND_SOCKET_BINDING_REF, "mail-smtp");
        ops.add(LoggingFixtures.LoggingProfile.socketHandlerAddress(LOGGING_PROFILE,
                SocketHandler.SOCKET_HANDLER_UPDATE), params).assertSuccess();
        ops.add(LoggingFixtures.LoggingProfile.socketHandlerAddress(LOGGING_PROFILE,
                SocketHandler.SOCKET_HANDLER_DELETE), params).assertSuccess();
        ops.add(LoggingFixtures.LoggingProfile.xmlFormatterAddress(LOGGING_PROFILE, XML_FORMATTER)).assertSuccess();
    }

    @Inject Console console;
    @Page LoggingProfileConfigurationPage page;

    @Override
    protected String getPatternFormatter() {
        return PatternFormatter.PATTERN_FORMATTER_CREATE;
    }

    @Override
    protected void navigateToPage() {
        page.navigate(NAME, LOGGING_PROFILE);
        console.verticalNavigation().selectSecondary(LOGGING_PROFILE_HANDLER_ITEM,
                "logging-profile-handler-socket-item");
    }

    @Override
    protected LoggingConfigurationPage getPage() {
        return page;
    }

    @Override
    protected Address socketHandlerAddress(String name) {
        return LoggingFixtures.LoggingProfile.socketHandlerAddress(LOGGING_PROFILE, name);
    }

    @Override
    protected TableFragment getHandlerTable() {
        return page.getSocketHandlerTable();
    }

    @Override
    protected FormFragment getHandlerForm() {
        return page.getSocketHandlerForm();
    }
}
