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
package org.jboss.hal.manatoko.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.OnlineOptions;

import static org.jboss.hal.manatoko.container.WildFlyConfiguration.STANDALONE;

public class WildFlyContainer extends GenericContainer<WildFlyContainer> {

    private static final int PORT = 9990;
    private static final String IMAGE = "quay.io/halconsole/wildfly";
    private static final Logger logger = LoggerFactory.getLogger(WildFlyContainer.class);
    private static WildFlyContainer instance = null;

    public static WildFlyContainer version(WildFlyVersion version) {
        return version(version, STANDALONE);
    }

    public static WildFlyContainer version(WildFlyVersion version, WildFlyConfiguration configuration) {
        if (instance != null && instance.isRunning()) {
            instance.stop();
        }
        instance = new WildFlyContainer(version, configuration);
        return instance;
    }

    public static WildFlyContainer instance() {
        if (instance == null || !instance.isRunning()) {
            throw new IllegalStateException("WildFly container has not yet been started.");
        }
        return instance;
    }

    private final WildFlyVersion version;
    private final WildFlyConfiguration configuration;
    private final String managementEndpoint;

    private WildFlyContainer(WildFlyVersion version, WildFlyConfiguration configuration) {
        super(DockerImageName.parse(IMAGE + ":" + version.version()));
        withNetwork(Network.INSTANCE)
                .withNetworkAliases(Network.WILDFLY)
                .withCommand("-c", configuration.configuration())
                .withExposedPorts(PORT)
                .waitingFor(Wait.forLogMessage(".*WildFly Full.*started in.*", 1))
                .withStartupTimeout(Timeouts.WILDFLY_STARTUP_TIMEOUT);

        this.version = version;
        this.configuration = configuration;
        this.managementEndpoint = "http://" + Network.WILDFLY + ":" + PORT;
    }

    @Override
    public String toString() {
        return "WildFlyContainer{" +
                "version=" + version +
                ", configuration=" + configuration +
                ", managementEndpoint='" + managementEndpoint + '\'' +
                '}';
    }

    @Override
    public void start() {
        super.start();
        logger.info("WildFly started: {}", this);
    }

    @Override
    public void stop() {
        super.stop();
        logger.info("WildFly stopped: {}", this);
    }

    public OnlineManagementClient managementClient() {
        // The management client is used in the unit tests on the host machine.
        // That's why we need to use `getHost()` and `getMappedPort()`.
        return ManagementClient
                .onlineLazy(OnlineOptions.standalone().hostAndPort(getHost(), getMappedPort(PORT)).build());
    }

    public String managementEndpoint() {
        // The URL of management endpoint is used in the HAL container.
        // That's why we need to use the network name and original port.
        return managementEndpoint;
    }
}
