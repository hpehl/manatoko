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
package org.jboss.hal.testsuite.fixtures.undertow;

import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.model.CrudConstants;
import org.wildfly.extras.creaper.core.online.operations.Address;

public class UndertowFixtures {

    private static final String SERVER_PREFIX = "srv";
    private static final String SERVLET_CONTAINER_PREFIX = "slt_cnt";
    private static final String HOST_PREFIX = "hst";

    public static final Address UNDERTOW_ADDRESS = Address.subsystem("undertow");
    public static final String DEFAULT_SERVER = "default-server";
    public static final String DEFAULT_SERVLET_CONTAINER = "default-servlet-container";
    public static final String DEFAULT_VIRTUAL_HOST = "default-virtual-host";

    // ------------------------------------------------------ server

    public static final String DEFAULT_SERVER_CREATE = Ids.build(SERVER_PREFIX, CrudConstants.CREATE, Random.name());

    public static Address serverAddress(String serverName) {
        return UNDERTOW_ADDRESS.and("server", serverName);
    }

    // ------------------------------------------------------ servlet container

    public static final String DEFAULT_SERVLET_CONTAINER_CREATE = Ids.build(SERVLET_CONTAINER_PREFIX, CrudConstants.CREATE,
            Random.name());

    public static Address servletContainerAddress(String name) {
        return UNDERTOW_ADDRESS.and("servlet-container", name);
    }

    // ------------------------------------------------------ virtual host

    public static final String DEFAULT_VIRTUAL_HOST_CREATE = Ids.build(HOST_PREFIX, CrudConstants.CREATE, Random.name());

    public static Address virtualHostAddress(String serverName, String name) {
        return serverAddress(serverName).and("host", name);
    }

    // ------------------------------------------------------ listeners

    public static Address ajpListenerAddress(String serverName, String ajpListenerName) {
        return serverAddress(serverName).and("ajp-listener", ajpListenerName);
    }

    public static Address httpListenerAddress(String serverName, String httpListenerName) {
        return serverAddress(serverName).and("http-listener", httpListenerName);
    }

    public static Address httpsListenerAddress(String serverName, String httpsListenerName) {
        return serverAddress(serverName).and("https-listener", httpsListenerName);
    }

    // ------------------------------------------------------ misc

    public static Address bufferCacheAddress(String bufferCacheName) {
        return UNDERTOW_ADDRESS.and("buffer-cache", bufferCacheName);
    }

    public static Address byteBufferPoolAddress(String byteBuffer) {
        return UNDERTOW_ADDRESS.and("byte-buffer-pool", byteBuffer);
    }

    private UndertowFixtures() {
    }
}
