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
package org.jboss.hal.manatoko.noop;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.hal.manatoko.test.WildFlyTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ArquillianExtension.class)
class NoopWildFlyTest extends WildFlyTest {

    private static final Logger logger = LoggerFactory.getLogger(NoopWildFlyTest.class);

    @Test
    void noop() {
        assertNotNull(wildFly.managementClient());
        logger.info("All systems up and running. Management client: {}", wildFly.managementClient());
    }
}
