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
package org.jboss.hal.testsuite.command;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.INET_ADDRESS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.INTERFACE;

public class BindPublicInterface implements OnlineCommand {

    private final String inetAddress;

    public BindPublicInterface(String inetAddress) {
        this.inetAddress = inetAddress;
    }

    @Override
    public void apply(OnlineCommandContext context) throws Exception {
        Operations operations = new Operations(context.client);
        operations.writeAttribute(Address.of(INTERFACE, "public"), INET_ADDRESS, inetAddress);
    }
}
