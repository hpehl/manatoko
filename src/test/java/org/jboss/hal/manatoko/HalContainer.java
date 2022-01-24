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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class HalContainer extends GenericContainer<HalContainer> {

    private static final int PORT = 9090;
    private static final Logger LOGGER = LoggerFactory.getLogger(HalContainer.class);

    public static HalContainer instance() {
        return new HalContainer()
                .withNetwork(Network.INSTANCE)
                .withNetworkAliases(Network.HAL)
                .withExposedPorts(PORT)
                .waitingFor(Wait.forListeningPort());
    }

    private String managementEndpoint;

    private HalContainer() {
        super(DockerImageName.parse(Image.HAL));
    }

    public void connectTo(final WildFlyContainer wildFly) {
        this.managementEndpoint = wildFly.managementEndpoint();
    }

    public String url(final String path, final String fragment) {
        String slashPath = path.startsWith("/") ? path : "/" + path;
        String query = managementEndpoint != null ? "connect=" + managementEndpoint : null;
        try {
            return new URI("http", null, Network.HAL, PORT, slashPath, query, fragment).toString();
        } catch (URISyntaxException e) {
            LOGGER.error("Unable to build URL for path '{}': {}", path, e.getMessage(), e);
            return null;
        }
    }
}
