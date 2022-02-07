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
package org.jboss.hal.testsuite.test.configuration.io;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.creaper.command.AddSocketBinding;
import org.jboss.hal.testsuite.fixtures.IOFixtures;
import org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures;
import org.jboss.hal.testsuite.fragment.finder.FinderFragment;
import org.jboss.hal.testsuite.fragment.finder.FinderPath;
import org.jboss.hal.testsuite.fragment.finder.IOWorkerPreviewFragment;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.util.AvailablePortFinder;
import org.jboss.hal.testsuite.util.ConfigUtils;
import org.jboss.hal.testsuite.util.ServerEnvironmentUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Manatoko
@Testcontainers
class IOWorkerTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);
    static final String SOCKET_BINDING = "socket-binding-" + Random.name();
    static final String HTTP_LISTENER = "http-listener-to-be-added-" + Random.name();
    static final int SOCKET_BINDING_PORT = AvailablePortFinder.getNextAvailableTCPPort();
    static Operations operations;
    static Administration administration;
    static ServerEnvironmentUtils serverEnvironmentUtils;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        administration = new Administration(client);
        serverEnvironmentUtils = new ServerEnvironmentUtils(client);
        client.apply(
                new AddSocketBinding.Builder().name(SOCKET_BINDING).port(SOCKET_BINDING_PORT).multicastPort(1).build());
    }

    @Inject Console console;
    @Drone WebDriver browser;

    @BeforeEach
    void prepare() {
        browser.navigate().refresh();
    }

    /**
     * Test is BusyTaskThreadCount is displayed. See https://issues.jboss.org/browse/HAL-1576
     */
    @Test
    void checkDisplayOfBusyTaskTreadCount() throws IOException {
        IOWorkerPreviewFragment ioWorkerPreviewFragment = getIOWorkerFragment().preview(IOWorkerPreviewFragment.class);
        try {
            IOWorkerPreviewFragment.ProgressItem busyTaskTreadCount = new IOWorkerPreviewFragment.ProgressItem(
                    ioWorkerPreviewFragment.getBusyTaskThreadCount());
            // noinspection ResultOfMethodCallIgnored
            busyTaskTreadCount.getCurrentValue();

        } catch (NoSuchElementException e) {
            fail("No number of busy threads is displayed. See HAL-1576.");
        }
    }

    @Test
    void verifyCorePoolSize() throws IOException, TimeoutException, InterruptedException {
        int taskMaxThreads = Random.number(1, 30);
        IOWorkerPreviewFragment ioWorkerPreviewFragment = getIOWorkerFragment().preview(IOWorkerPreviewFragment.class);
        operations.writeAttribute(IOFixtures.workerAddress(IOFixtures.DEFAULT_IO_WORKER), "task-max-threads",
                taskMaxThreads);
        administration.reloadIfRequired();
        ioWorkerPreviewFragment.refresh();
        IOWorkerPreviewFragment.ProgressItem corePoolSizeAttribute = new IOWorkerPreviewFragment.ProgressItem(
                ioWorkerPreviewFragment.getCorePoolSize());
        assertEquals(taskMaxThreads, corePoolSizeAttribute.getMaxValue(),
                "Newly updated task-max-threads's value should be present in the core pool size column");
    }

    @Test
    void verifyMaxPoolSize() throws IOException, TimeoutException, InterruptedException {
        int taskMaxThreads = Random.number(1, 30);
        IOWorkerPreviewFragment ioWorkerPreviewFragment = getIOWorkerFragment().preview(IOWorkerPreviewFragment.class);
        operations.writeAttribute(IOFixtures.workerAddress(IOFixtures.DEFAULT_IO_WORKER), "task-max-threads",
                taskMaxThreads);
        administration.reloadIfRequired();
        ioWorkerPreviewFragment.refresh();
        IOWorkerPreviewFragment.ProgressItem maxPoolSize = new IOWorkerPreviewFragment.ProgressItem(
                ioWorkerPreviewFragment.getMaxPoolSize());
        assertEquals(taskMaxThreads, maxPoolSize.getMaxValue(),
                "Newly updated task-max-threads's value should be present in the max pool size progress column");
    }

    @Test
    void verifyIOThreadCount() throws IOException, TimeoutException, InterruptedException {
        int ioThreads = Random.number(1, 8);
        IOWorkerPreviewFragment ioWorkerPreviewFragment = getIOWorkerFragment().preview(IOWorkerPreviewFragment.class);
        operations.writeAttribute(IOFixtures.workerAddress(IOFixtures.DEFAULT_IO_WORKER), "io-threads", ioThreads);
        administration.reloadIfRequired();
        ioWorkerPreviewFragment.refresh();
        IOWorkerPreviewFragment.ProgressItem ioThreadCount = new IOWorkerPreviewFragment.ProgressItem(
                ioWorkerPreviewFragment.getIoThreadCount());
        assertEquals(ioThreads, ioThreadCount.getMaxValue(),
                "Newly updated io-threads's value should be present in the IO thread count progress column");
    }

    @Test
    void verifyConnections() throws IOException, TimeoutException, InterruptedException {
        FinderFragment finderFragment = getIOWorkerFragment();
        IOWorkerPreviewFragment ioWorkerPreviewFragment = finderFragment.preview(IOWorkerPreviewFragment.class);
        operations.add(UndertowFixtures.httpListenerAddress("default-server", HTTP_LISTENER),
                Values.of("socket-binding", SOCKET_BINDING));
        administration.reloadIfRequired();
        ioWorkerPreviewFragment.refresh();
        List<String> newlyAddedConnections = ioWorkerPreviewFragment.getConnections()
                .stream()
                .map(attributeItem -> attributeItem.getKeyElement().getText())
                .filter(connectionUrl -> connectionUrl.contains("127.0.0.1:" + SOCKET_BINDING_PORT))
                .collect(Collectors.toList());
        assertEquals(1, newlyAddedConnections.size(), "Newly added connections should be present in the list");
    }

    private FinderFragment getIOWorkerFragment() throws IOException {
        FinderPath finderPath = new FinderPath();
        if (ConfigUtils.isDomain()) {
            finderPath
                    .append(Ids.DOMAIN_BROWSE_BY, "hosts")
                    .append(Ids.HOST, Ids.build("host", ConfigUtils.getDefaultHost()))
                    .append(Ids.SERVER, Ids.build(ConfigUtils.getDefaultHost(), ConfigUtils.getDefaultServer()))
                    .append(Ids.RUNTIME_SUBSYSTEM, "io");
        } else {
            finderPath = FinderFragment.runtimeSubsystemPath(serverEnvironmentUtils.getServerHostName(), "io");
        }
        finderPath.append("worker", IOFixtures.DEFAULT_IO_WORKER);
        return console.finder(NameTokens.RUNTIME, finderPath);
    }
}
