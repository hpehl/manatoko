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
package org.jboss.hal.testsuite.fixtures;

import org.wildfly.extras.creaper.core.online.operations.Address;

public class JSFFixtures {

    public static final Address JSF_ADDRESS = Address.subsystem("jsf");
    public static final String DEFAULT_JSF_IMPL_SLOT = "default-jsf-impl-slot";
    public static final String DISALLOW_DOCTYPE_DECL = "disallow-doctype-decl";
    public static final String JSF = "jsf";

    private JSFFixtures() {

    }
}
