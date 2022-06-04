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
package org.jboss.hal.testsuite.container;

import org.jboss.hal.testsuite.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.OnlineOptions;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.hal.dmr.ModelDescriptionConstants.ALLOWED_ORIGINS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.LIST_ADD_OPERATION;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.VALUE;

public class WildFlyContainer extends GenericContainer<WildFlyContainer> {

    private static final int PORT = 9990;
    private static final String IMAGE = "quay.io/halconsole/wildfly";
    private static final Logger logger = LoggerFactory.getLogger(WildFlyContainer.class);
    private static WildFlyContainer instance = null;

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

    private boolean started;
    private final WildFlyVersion version;
    private final WildFlyConfiguration configuration;

    private WildFlyContainer(WildFlyVersion version, WildFlyConfiguration configuration) {
        super(DockerImageName.parse(IMAGE + ":" + version.version()));
        withNetwork(Network.INSTANCE)
                .withNetworkAliases(Network.WILDFLY)
                .withCommand("-c", configuration.configuration())
                .withExposedPorts(PORT)
                .waitingFor(Wait.forLogMessage(".*WildFly Full.*started in.*", 1))
                .withStartupTimeout(Timeouts.WILDFLY_STARTUP_TIMEOUT);

        this.started = false;
        this.version = version;
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        return "WildFlyContainer{" +
                "version=" + version +
                ", configuration=" + configuration +
                ", managementEndpoint='" + managementEndpoint() + '\'' +
                '}';
    }

    @Override
    public void start() {
        super.start();
        started = true;
        logger.info("WildFly started: {}", this);
        if (Environment.instance().local()) {
            String url = HalContainer.instance().url();
            OnlineManagementClient client = managementClient();
            Administration administration = new Administration(client);
            Operations operations = new Operations(client);
            Address address = Address.coreService("management").and("management-interface", "http-interface");
            try {
                operations.invoke(LIST_ADD_OPERATION, address, Values.of(NAME, ALLOWED_ORIGINS).and(VALUE, url));
                administration.reload();
                logger.info("Added {} as allowed origin to {}", url, this);
            } catch (Exception e) {
                logger.error("Unable to add {} as allowed origin to {}: {}", url, this, e.getMessage());
            }
        }
    }

    @Override
    public void stop() {
        String toString = toString(); // get the right URL before stopping the container
        super.stop();
        started = false;
        logger.info("WildFly stopped: {}", toString);
    }

    public OnlineManagementClient managementClient() {
        // The management client is used in the unit tests on the host machine.
        // That's why we always need to use `getHost()` and `getMappedPort()`.
        if (!started) {
            throw new IllegalStateException(
                    String.format("Unable to get management client for %s: Container has not been started yet!", this));
        }
        return ManagementClient
                .onlineLazy(OnlineOptions.standalone().hostAndPort(getHost(), getMappedPort(PORT)).build());
    }

    public String managementEndpoint() {
        if (Environment.instance().local() && started) {
            return "http://" + getHost() + ":" + getMappedPort(PORT);
        } else {
            return "http://" + Network.WILDFLY + ":" + PORT;
        }
    }
}
