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
    private static HalContainer instance = null;

    public static HalContainer instance() {
        if (instance == null) {
            instance = new HalContainer();
        }
        return instance;
    }

    private final String url;
    private String managementEndpoint;

    private HalContainer() {
        super(DockerImageName.parse(IMAGE));
        withNetwork(Network.INSTANCE)
                .withNetworkAliases(Network.HAL)
                .withExposedPorts(PORT)
                .waitingFor(Wait.forListeningPort());

        url = "http://" + Network.HAL + ":" + PORT;
    }

    @Override
    public String toString() {
        return "HalContainer{url='" + url + '\'' + '}';
    }

    @Override
    public void start() {
        super.start();
        logger.info("HAL started: {}", this);
    }

    @Override
    public void stop() {
        super.stop();
        logger.info("HAL stopped: {}", this);
    }

    /**
     * Tells the HAL standalone console to use the management endpoint of the specified WildFly instance.
     */
    public void connectTo(final WildFlyContainer wildFly) {
        this.managementEndpoint = wildFly.managementEndpoint();
        logger.info("{} connected to {}", this, wildFly);
    }

    public String consoleEndpoint() {
        if (managementEndpoint == null) {
            logger.warn("No management endpoint defined for {}", this);
            return url;
        }
        return url + "?connect=" + managementEndpoint;
    }
}
