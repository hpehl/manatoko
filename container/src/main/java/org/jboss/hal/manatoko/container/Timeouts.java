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

import java.time.Duration;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;

interface Timeouts {

    Duration BROWSER_STARTUP_TIMEOUT = Duration.of(66, SECONDS);
    Duration WEB_DRIVER_INIT_TIMEOUT = Duration.of(5555, MILLIS);
    Duration WILDFLY_STARTUP_TIMEOUT = Duration.of(333, SECONDS);

    Duration WEBDRIVER_PAGE_LOAD_TIMEOUT = Duration.of(33, SECONDS);
    Duration WEBDRIVER_SCRIPT_TIMEOUT = Duration.of(22, SECONDS);
    Duration WEBDRIVER_IMPLICIT_WAIT_TIMEOUT = Duration.of(11, SECONDS);
}
