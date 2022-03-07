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

import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.REMOTE_DESTINATION_OUTBOUND_SOCKET_BINDING;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING_GROUP;

public class RemoveSocketBinding extends SocketBindingCommand {

    public RemoveSocketBinding(String name) {
        super(name);
    }

    public RemoveSocketBinding(String name, String socketBindingGroup) {
        super(name, socketBindingGroup);
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        String socketBindingGroup = resolveSocketBindingGroup(ctx);
        Address socketBindingRefAddress = Address.of(SOCKET_BINDING_GROUP, socketBindingGroup)
                .and(SOCKET_BINDING, name);
        ops.removeIfExists(socketBindingRefAddress);

        Address remoteSocketBindingRefAddress = Address.of(SOCKET_BINDING_GROUP, socketBindingGroup)
                .and(REMOTE_DESTINATION_OUTBOUND_SOCKET_BINDING, name);
        ops.removeIfExists(remoteSocketBindingRefAddress);
    }
}
