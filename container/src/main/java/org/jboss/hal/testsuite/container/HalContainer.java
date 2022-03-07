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

    private boolean started;

    private HalContainer() {
        super(DockerImageName.parse(IMAGE));
        withNetwork(Network.INSTANCE)
                .withNetworkAliases(Network.HAL)
                .withExposedPorts(PORT)
                .waitingFor(Wait.forListeningPort());
        started = false;
    }

    @Override
    public String toString() {
        return "HalContainer{url='" + url() + '\'' + '}';
    }

    @Override
    public void start() {
        super.start();
        started = true;
        logger.info("HAL started: {}", this);
    }

    @Override
    public void stop() {
        super.stop();
        started = false;
        logger.info("HAL stopped: {}", this);
    }

    public String url() {
        if (Environment.instance().local() && started) {
            return "http://" + getHost() + ":" + getMappedPort(PORT);
        } else {
            return "http://" + Network.HAL + ":" + PORT;
        }
    }
}
