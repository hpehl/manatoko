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
package org.jboss.hal.testsuite.noop;

import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Manatoko
@Testcontainers
class NoopWildFlyTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);
    private static final Logger logger = LoggerFactory.getLogger(NoopWildFlyTest.class);

    @Test
    void noop() {
        assertNotNull(wildFly.managementClient());
        logger.info("All systems up and running. Management client: {}", wildFly.managementClient());
    }
}
