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

import java.io.File;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.hal.manatoko.container.Browser;
import org.jboss.hal.manatoko.container.HalContainer;
import org.jboss.hal.manatoko.container.WildFlyContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jboss.hal.manatoko.container.WildFlyVersion._26;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING;
import static org.testcontainers.containers.VncRecordingContainer.VncRecordingFormat.MP4;

// IMPORTANT!
// Please don't change the order of the annotations:
// The Arquillian extension depends on running containers
@Testcontainers
@ExtendWith(ArquillianExtension.class)
public abstract class ManatokoTest {

    @TempDir
    protected static File recordings;

    @Container
    protected static WildFlyContainer wildFly = WildFlyContainer.version(_26);

    @Container
    protected static HalContainer console = HalContainer.newInstance();

    @Container
    protected static Browser chrome = Browser.chrome()
            .withRecordingMode(RECORD_FAILING, recordings, MP4);

    @BeforeAll
    static void beforeAll() {
        console.connectTo(wildFly);
    }
}
