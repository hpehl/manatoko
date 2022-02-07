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
package org.jboss.hal.testsuite.fixtures.undertow;

import org.wildfly.extras.creaper.core.online.operations.Address;

public class UndertowFixtures {

    public static final Address UNDERTOW_ADDRESS = Address.subsystem("undertow");

    public static final String DEFAULT_SECURITY_DOMAIN = "default-security-domain";
    public static final String DEFAULT_SERVER = "default-server";
    public static final String DEFAULT_SERVLET_CONTAINER = "default-servlet-container";
    public static final String DEFAULT_VIRTUAL_HOST = "default-virtual-host";
    public static final String INSTANCE_ID = "instance-id";
    public static final String STATISTICS_ENABLED = "statistics-enabled";
    public static final String DEFAULT_HOST = "default-host";

    public static Address serverAddress(String serverName) {
        return UNDERTOW_ADDRESS.and("server", serverName);
    }

    public static Address ajpListenerAddress(String serverName, String ajpListenerName) {
        return serverAddress(serverName).and("ajp-listener", ajpListenerName);
    }

    public static Address httpListenerAddress(String serverName, String httpListenerName) {
        return serverAddress(serverName).and("http-listener", httpListenerName);
    }

    public static Address httpsListenerAddress(String serverName, String httpsListenerName) {
        return serverAddress(serverName).and("https-listener", httpsListenerName);
    }

    public static Address servletContainerAddress(String name) {
        return UNDERTOW_ADDRESS.and("servlet-container", name);
    }

    public static Address virtualHostAddress(String serverName, String name) {
        return serverAddress(serverName).and("host", name);
    }

    public static Address bufferCacheAddress(String bufferCacheName) {
        return UNDERTOW_ADDRESS.and("buffer-cache", bufferCacheName);
    }

    public static Address byteBufferPoolAddress(String byteBuffer) {
        return UNDERTOW_ADDRESS.and("byte-buffer-pool", byteBuffer);
    }

    private UndertowFixtures() {

    }
}
