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
package org.jboss.hal.manatoko;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.OnlineOptions;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.jboss.hal.manatoko.WildFlyConfiguration.STANDALONE;

public class WildFlyContainer extends GenericContainer<WildFlyContainer> {

    private static final int PORT = 9990;
    private static final Logger LOGGER = LoggerFactory.getLogger(WildFlyContainer.class);

    public static WildFlyContainer version(WildFlyVersion version) {
        return version(version, STANDALONE);
    }

    public static WildFlyContainer version(WildFlyVersion version, WildFlyConfiguration configuration) {
        return new WildFlyContainer(version)
                .withNetwork(Network.INSTANCE)
                .withNetworkAliases(Network.WILDFLY)
                .withCommand("-c", configuration.configuration())
                .withExposedPorts(PORT)
                .waitingFor(Wait.forLogMessage(".*WildFly Full.*started in.*", 1))
                .withStartupTimeout(Duration.of(300, SECONDS));
    }

    private final WildFlyVersion version;

    private WildFlyContainer(WildFlyVersion version) {
        super(DockerImageName.parse(Image.WILDFLY + ":" + version.version()));
        this.version = version;
    }

    @Override
    public String toString() {
        return "WildFlyContainer{" +
                "version=" + version +
                '}';
    }

    public OnlineManagementClient managementClient() {
        return ManagementClient.onlineLazy(OnlineOptions.standalone().hostAndPort(getHost(), getMappedPort(PORT))
                .build());
    }

    public String managementEndpoint() {
        try {
            return new URI("http", null, Network.WILDFLY, PORT, null, null, null).toString();
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to build management endpoint for {}: {}", this, e.getMessage(), e);
            return null;
        }
    }

    public String url(String path) {
        String slashPath = path.startsWith("/") ? path : "/" + path;
        try {
            return new URI("http", null, Network.WILDFLY, PORT, slashPath, null, null).toString();
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to build URL for path '{}': {}", path, e.getMessage(), e);
            return null;
        }
    }
}
