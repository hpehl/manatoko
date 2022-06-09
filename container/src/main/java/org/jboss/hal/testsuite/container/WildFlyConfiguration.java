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

public enum WildFlyConfiguration {

    FULL("standalone-full"),

    FULL_HA("standalone-full-ha"),

    HA("standalone-ha"),

    LOAD_BALANCER("standalone-load-balancer"),

    MICROPROFILE("standalone-microprofile"),

    MICROPROFILE_HA("standalone-microprofile-ha"),

    DEFAULT("standalone");

    private final String name;

    WildFlyConfiguration(String name) {
        this.name = name;
    }

    public String configuration() {
        return name + "-insecure.xml";
    }
}
