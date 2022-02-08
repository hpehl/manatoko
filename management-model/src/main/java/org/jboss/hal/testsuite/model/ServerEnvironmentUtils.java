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
package org.jboss.hal.testsuite.model;

import java.io.IOException;

import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

public class ServerEnvironmentUtils {

    private final Operations operations;
    private static final Address SERVER_ENVIRONMENT_ADDRESS = Address.coreService("server-environment");

    public ServerEnvironmentUtils(OnlineManagementClient client) {
        this.operations = new Operations(client);
    }

    public String getServerHostName() throws IOException {
        return operations.readAttribute(SERVER_ENVIRONMENT_ADDRESS, "host-name").stringValue();
    }
}
