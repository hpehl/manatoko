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
package org.jboss.hal.testsuite.test.runtime.server;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.finder.FinderPath;
import org.jboss.hal.testsuite.preview.runtime.TopologyPreview;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.DomainAdministration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SUSPEND_STATE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.UNDEFINED;
import static org.jboss.hal.resources.Ids.DOMAIN_BROWSE_BY;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class TopologyTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.domain();

    @Container static Browser browser = new Browser();
    static OnlineManagementClient client;
    static final Logger logger = LoggerFactory.getLogger(TopologyTest.class);

    @BeforeAll
    static void setupModel() throws IOException {
        client = wildFly.managementClient();
        DomainAdministration administration = new DomainAdministration(client);
        administration.stopServer("server-one");
    }

    @Inject Console console;
    TopologyPreview preview;

    @BeforeEach
    void prepare() {
        preview = console.finder(NameTokens.RUNTIME,
                new FinderPath().append(DOMAIN_BROWSE_BY, "topology")).preview(TopologyPreview.class);
    }

    @Test
    void startServerOneSuspended() {
        String server = "server-one";
        preview.serverAction(server, "Start in suspended mode");
        await().atMost(60, SECONDS).until(readServerState(server), equalTo("SUSPENDED"));

        console.waitNoNotification();
        preview.refresh();
        preview.selectServer(server);
        assertEquals("SUSPENDED", preview.getServerAttribute("Suspend State"));
    }

    private Callable<String> readServerState(String server) {
        return () -> {
            Address address = Address.host(client.options().defaultHost).and(SERVER, server);
            Operations operations = new Operations(wildFly.managementClient());
            try {
                ModelNodeResult result = operations.readAttribute(address, SUSPEND_STATE);
                if (result.isSuccess()) {
                    return result.stringValue();
                } else {
                    return UNDEFINED;
                }
            } catch (IOException e) {
                logger.warn("Unable to read suspend state for {}: {}", address, e.getMessage());
                return UNDEFINED;
            }
        };
    }
}
