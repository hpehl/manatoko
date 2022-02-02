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
package org.jboss.hal.manatoko.test;

import org.jboss.hal.manatoko.container.HalContainer;
import org.jboss.hal.manatoko.container.WildFlyConfiguration;
import org.jboss.hal.manatoko.container.WildFlyContainer;
import org.jboss.hal.manatoko.container.WildFlyVersion;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class WildFlyTest extends ManatokoTest {

    @Container protected static WildFlyContainer wildFly = WildFlyContainer
            .version(WildFlyVersion._26, WildFlyConfiguration.STANDALONE);

    @BeforeAll
    static void setupWildFly() {
        if (HalContainer.instance() != null) {
            HalContainer.instance().connectTo(wildFly);
        }
    }
}
