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
package org.jboss.hal.testsuite.fixtures.microprofile;

import org.wildfly.extras.creaper.core.online.operations.Address;

public class MicroprofileMetricsFixtures {

    public static final Address MICROPROFILE_METRICS_ADDRESS = Address.subsystem("microprofile-metrics-smallrye");
    public static final String EXPOSE_ALL_SUBSYSTEMS = "expose-all-subsystems";
    public static final String EXPOSED_SUBSYSTEMS = "exposed-subsystems";
    public static final String SECURITY_ENABLED = "security-enabled";

    private MicroprofileMetricsFixtures() {
    }
}
