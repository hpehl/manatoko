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
package org.jboss.hal.testsuite.test.configuration.logging.profile.handler;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.dmr.ModelNode;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.LoggingFixtures;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.LoggingConfigurationPage;
import org.jboss.hal.testsuite.page.configuration.LoggingProfileConfigurationPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.test.configuration.logging.AbstractSizeHandlerTest;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.FILE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PATH;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.LOGGING_PROFILE_HANDLER_ITEM;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.NAME;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.PATH_VALUE;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.SizeHandler.SIZE_HANDLER_DELETE;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.SizeHandler.SIZE_HANDLER_READ;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.SizeHandler.SIZE_HANDLER_UPDATE;

@Manatoko
@Testcontainers
class SizeHandlerTest extends AbstractSizeHandlerTest {

    static final String LOGGING_PROFILE = "logging-profile-" + Random.name();
    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations ops = new Operations(client);
        ops.add(LoggingFixtures.LoggingProfile.loggingProfileAddress(LOGGING_PROFILE)).assertSuccess();
        ModelNode file = new ModelNode();
        file.get(PATH).set(PATH_VALUE);
        ops.add(LoggingFixtures.LoggingProfile.sizeHandlerAddress(LOGGING_PROFILE, SIZE_HANDLER_READ),
                Values.of(FILE, file.clone())).assertSuccess();
        ops.add(LoggingFixtures.LoggingProfile.sizeHandlerAddress(LOGGING_PROFILE, SIZE_HANDLER_UPDATE),
                Values.of(FILE, file.clone())).assertSuccess();
        ops.add(LoggingFixtures.LoggingProfile.sizeHandlerAddress(LOGGING_PROFILE, SIZE_HANDLER_DELETE),
                Values.of(FILE, file.clone())).assertSuccess();
    }

    @Inject Console console;
    @Page LoggingProfileConfigurationPage page;

    @Override
    protected void navigateToPage() {
        page.navigate(NAME, LOGGING_PROFILE);
        console.verticalNavigation().selectSecondary(LOGGING_PROFILE_HANDLER_ITEM,
                "logging-profile-handler-size-rotating-file-item");
    }

    @Override
    protected LoggingConfigurationPage getPage() {
        return page;
    }

    @Override
    protected Address sizeHandlerAddress(String name) {
        return LoggingFixtures.LoggingProfile.sizeHandlerAddress(LOGGING_PROFILE, name);
    }

    @Override
    protected TableFragment getHandlerTable() {
        return page.getSizeHandlerTable();
    }

    @Override
    protected FormFragment getHandlerForm() {
        return page.getSizeHandlerForm();
    }
}
