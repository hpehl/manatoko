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
package org.jboss.hal.testsuite.test;

import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.HalContainer;
import org.jboss.hal.testsuite.environment.Environment;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

/**
 * Custom Extension which executes code only once before all tests are started and after all tests finished. This is temporary
 * solution until <a href="https://github.com/junit-team/junit5/issues/456">https://github.com/junit-team/junit5/issues/456</a>
 * is resolved.
 */
public class SystemSetupExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static boolean systemReady = false;

    /**
     * Separate method with 'synchronized static' required for make sure procedure will be executed only once across all
     * simultaneously running threads
     */
    private static synchronized void systemSetup() {
        if (!systemReady) {
            systemReady = true;
            HalContainer.instance().start();
        }
    }

    /**
     * Initial setup of system. Including configuring services, adding calls to callrec, users to scorecard, call media files
     *
     * @param context junit context
     */
    @Override
    public void beforeAll(ExtensionContext context) {
        systemSetup();
        context.getRoot().getStore(GLOBAL).put(systemReady, this);
    }

    /**
     * CloseableResource implementation, adding value into GLOBAL context is required to registers a callback hook With such
     * steps close() method will be executed only once in the end of test execution
     */
    @Override
    public void close() {
        if (HalContainer.instance() != null) {
            HalContainer.instance().stop();
        }
        if (Environment.instance().remote() && Browser.instance() != null) {
            Browser.instance().stop();
        }
    }
}
