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

import org.wildfly.extras.creaper.core.online.operations.Address;

public class UndertowFiltersFixtures {

    private static final String CONFIGURATION = "configuration";

    private static final Address FILTERS_ADDRESS = UndertowFixtures.UNDERTOW_ADDRESS.and(CONFIGURATION, "filter");

    public static final String HEADER_VALUE = "header-value";

    public static Address rewriteAddress(String rewriteName) {
        return FILTERS_ADDRESS.and("rewrite", rewriteName);
    }

    public static Address responseHeaderAddress(String responseHeaderName) {
        return FILTERS_ADDRESS.and("response-header", responseHeaderName);
    }

    public static Address requestLimitAddress(String requestLimitName) {
        return FILTERS_ADDRESS.and("request-limit", requestLimitName);
    }

    public static Address modClusterFilterAdress(String modClusterFilterName) {
        return FILTERS_ADDRESS.and("mod-cluster", modClusterFilterName);
    }

    public static Address gzipFilterAddress(String gzipFilterName) {
        return FILTERS_ADDRESS.and("gzip", gzipFilterName);
    }

    public static Address expressionFilterAddress(String expressionFilterName) {
        return FILTERS_ADDRESS.and("expression-filter", expressionFilterName);
    }

    public static Address errorPageAddress(String errorPageName) {
        return FILTERS_ADDRESS.and("error-page", errorPageName);
    }

    public static Address customFilterAddress(String customFilterName) {
        return FILTERS_ADDRESS.and("custom-filter", customFilterName);
    }

    private UndertowFiltersFixtures() {

    }

}
