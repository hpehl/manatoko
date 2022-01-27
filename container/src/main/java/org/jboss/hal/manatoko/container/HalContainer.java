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

public class HalContainer extends GenericContainer<HalContainer> {

    private static final int PORT = 9090;
    private static final String IMAGE = "quay.io/halconsole/hal";
    private static final Logger logger = LoggerFactory.getLogger(HalContainer.class);
    private static HalContainer currentInstance = null;

    public static HalContainer newInstance() {
        currentInstance = new HalContainer().withNetwork(Network.INSTANCE)
                .withNetworkAliases(Network.HAL).withExposedPorts(PORT)
                .waitingFor(Wait.forListeningPort());
        return currentInstance;
    }

    public static HalContainer currentInstance() {
        return currentInstance;
    }

    private String managementEndpoint;

    private HalContainer() {
        super(DockerImageName.parse(IMAGE));
    }

    /**
     * Tells the HAL standalone console to use the management endpoint of the specified WildFly instance.
     */
    public void connectTo(final WildFlyContainer wildFly) {
        this.managementEndpoint = wildFly.managementEndpoint();
        logger.info("{} connected to management endpoint {}", IMAGE, managementEndpoint);
    }

    public String consoleEndpoint() {
        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(Network.HAL).append(":").append(PORT);
        if (managementEndpoint != null) {
            builder.append("?connect=").append(managementEndpoint);
        } else {
            logger.warn("No management endpoint defined for {}", IMAGE);
        }
        return builder.toString();
    }
}
