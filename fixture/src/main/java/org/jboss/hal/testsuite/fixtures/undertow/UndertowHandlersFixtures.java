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

public class UndertowHandlersFixtures {

    private static final Address HANDLERS_ADDRESS = UndertowFixtures.UNDERTOW_ADDRESS.and("configuration", "handler");

    public static Address fileHandlerAddress(String fileHandlerName) {
        return HANDLERS_ADDRESS.and("file", fileHandlerName);
    }

    public static Address reverseProxyAddress(String reverseProxyName) {
        return HANDLERS_ADDRESS.and("reverse-proxy", reverseProxyName);
    }

    private UndertowHandlersFixtures() {

    }

}
