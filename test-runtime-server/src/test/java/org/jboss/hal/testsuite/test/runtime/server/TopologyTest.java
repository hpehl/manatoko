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
package org.jboss.hal.testsuite.test.runtime.server;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.finder.FinderPath;
import org.jboss.hal.testsuite.preview.runtime.TopologyPreview;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jboss.hal.resources.Ids.DOMAIN_BROWSE_BY;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class TopologyTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.domain();

    @Inject Console console;
    TopologyPreview preview;

    @BeforeEach
    void prepare() {
        preview = console.finder(NameTokens.RUNTIME,
                new FinderPath().append(DOMAIN_BROWSE_BY, "topology")).preview(TopologyPreview.class);
    }

    @Test
    void verifyServerOneDisabled() {
        preview.getServerOneContainer().click();
        assertEquals("DISABLED", preview.getServerOneStatus());
    }
}
