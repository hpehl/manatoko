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
package org.jboss.hal.testsuite.test.runtime.messaging;

import java.io.IOException;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.command.AddJmsBridge;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.finder.FinderFragment;
import org.jboss.hal.testsuite.fragment.finder.FinderPath;
import org.jboss.hal.testsuite.fragment.finder.FinderPreviewFragment;
import org.jboss.hal.testsuite.model.ServerEnvironmentUtils;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;

import static org.jboss.hal.dmr.ModelDescriptionConstants.MESSAGING_ACTIVEMQ;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMS_BRIDGE_READ;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.runtimeSubsystemPath;

@Manatoko
@Testcontainers
public class JmsBridgeTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();
    static ServerEnvironmentUtils serverEnvironmentUtils;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddJmsBridge(JMS_BRIDGE_READ));
        serverEnvironmentUtils = new ServerEnvironmentUtils(client);
    }

    @Inject Console console;
    FinderFragment finder;

    @BeforeEach
    void setUp() throws IOException {
        FinderPath path = runtimeSubsystemPath(serverEnvironmentUtils.getServerHostName(), MESSAGING_ACTIVEMQ)
                .append(Ids.MESSAGING_CATEGORY_RUNTIME, Ids.JMS_BRIDGE_ITEM)
                .append(Ids.JMS_BRIDGE_RUNTIME, Ids.jmsBridge(JMS_BRIDGE_READ));
        finder = console.finder(NameTokens.RUNTIME, path);
    }

    @Test
    void messageCount() {
        FinderPreviewFragment preview = finder.preview();
        String messageCount = preview.getMainAttributes().get("Message Count");
        String abortedMessageCount = preview.getMainAttributes().get("Aborted Message Count");
        Assertions.assertEquals(0, Integer.parseInt(messageCount));
        Assertions.assertEquals(0, Integer.parseInt(abortedMessageCount));
    }
}
