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
package org.jboss.hal.testsuite.command;

import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.FIXED_PORT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.INTERFACE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MULTICAST_ADDRESS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MULTICAST_PORT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PORT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING_GROUP;

public class AddSocketBinding extends SocketBindingCommand {

    private static final int DEFAULT_MULTICAST_PORT = 1;

    private final boolean fixedPort;

    private final String socketBindingInterface;

    private final String multicastAddress;

    private final int multicastPort;

    private final int port;

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        String socketBindingGroup = resolveSocketBindingGroup(ctx);
        Address socketBindingAddress = Address.of(SOCKET_BINDING_GROUP, socketBindingGroup)
                .and(SOCKET_BINDING, name);
        ops.add(socketBindingAddress,
                Values.empty().andOptional(FIXED_PORT, fixedPort).andOptional(INTERFACE, socketBindingInterface)
                        .andOptional(MULTICAST_ADDRESS, multicastAddress).andOptional(MULTICAST_PORT, multicastPort)
                        .andOptional(PORT, port));
    }

    public static class Builder {

        private String name;

        private String socketBindingGroup;

        private boolean fixedPort;

        private String socketBindingInterface;

        private String multicastAddress;

        private int multicastPort;

        private int port;

        public Builder name(String value) {
            this.name = value;
            return this;
        }

        public Builder socketBindingGroup(String value) {
            this.socketBindingGroup = value;
            return this;
        }

        public Builder fixedPort(boolean value) {
            this.fixedPort = value;
            return this;
        }

        public Builder socketBindingInterface(String value) {
            this.socketBindingInterface = value;
            return this;
        }

        public Builder multicastAddress(String value) {
            this.multicastAddress = value;
            return this;
        }

        public Builder multicastPort(int value) {
            this.multicastPort = value;
            return this;
        }

        public Builder port(int value) {
            this.port = value;
            return this;
        }

        public AddSocketBinding build() {
            return new AddSocketBinding(this);
        }
    }

    private AddSocketBinding(Builder builder) {
        this(builder.name, builder.socketBindingGroup, builder.fixedPort, builder.socketBindingInterface,
                builder.multicastAddress, builder.multicastPort, builder.port);
    }

    public AddSocketBinding(String name) {
        this(name, null, false, null, null, DEFAULT_MULTICAST_PORT, 0);
    }

    public AddSocketBinding(String name, String socketBindingGroup, boolean fixedPort, String socketBindingInterface,
            String multicastAddress, int multicastPort, int port) {
        super(name, socketBindingGroup);
        this.fixedPort = fixedPort;
        this.socketBindingInterface = socketBindingInterface;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.port = port;
    }
}
