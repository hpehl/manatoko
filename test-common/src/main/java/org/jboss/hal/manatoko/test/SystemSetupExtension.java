package org.jboss.hal.manatoko.test;

import org.jboss.hal.manatoko.container.Browser;
import org.jboss.hal.manatoko.container.HalContainer;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

/**
 * Custom Extension which executes code only once before all tests are started and after all tests finished.
 * This is temporary solution until https://github.com/junit-team/junit5/issues/456 will not be released
 */
public class SystemSetupExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static boolean systemReady = false;

    /**
     * Separate method with 'synchronized static' required for make sure procedure will be executed
     * only once across all simultaneously running threads
     */
    synchronized private static void systemSetup() {
        if (!systemReady) {
            systemReady = true;
            Browser.instance().start();
            HalContainer.instance().start();
        }
    }

    /**
     * Initial setup of system. Including configuring services,
     * adding calls to callrec, users to scorecard, call media files
     *
     * @param context junit context
     */
    @Override
    public void beforeAll(ExtensionContext context) {
        systemSetup();
        context.getRoot().getStore(GLOBAL).put(systemReady, this);
    }

    /**
     * CloseableResource implementation, adding value into GLOBAL context is required to  registers a callback hook
     * With such steps close() method will be executed only once in the end of test execution
     */
    @Override
    public void close() {
        if (HalContainer.instance() != null) {
            HalContainer.instance().stop();
        }
        if (Browser.instance() != null) {
            Browser.instance().stop();
        }
    }
}