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
package org.jboss.hal.testsuite.test.configuration.modcluster;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.ModclusterPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTPS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.LISTENER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.EXCLUDED_CONTEXTS;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.NODE_TIMEOUT;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.PROXY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.PROXY_URL;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.STICKY_SESSION;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.proxyAddress;

@Manatoko
@Testcontainers
class ModclusterConfigurationTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(proxyAddress(PROXY_UPDATE), Values.of(LISTENER, DEFAULT));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page ModclusterPage page;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate(NAME, PROXY_UPDATE);
        console.verticalNavigation().selectPrimary("proxy-item");
    }

    @Test
    void updateAdvertising() throws Exception {
        page.getTabs().select("advertising-tab");
        form = page.getAdvertisingForm();
        crud.update(proxyAddress(PROXY_UPDATE), form, LISTENER, HTTPS);
    }

    @Test
    void updateSessions() throws Exception {
        page.getTabs().select("sessions-tab");
        form = page.getSessionsForm();
        crud.update(proxyAddress(PROXY_UPDATE), form, STICKY_SESSION, false);
    }

    @Test
    void updateWebContexts() throws Exception {
        page.getTabs().select("web-contexts-tab");
        form = page.getWebContextsForm();
        crud.update(proxyAddress(PROXY_UPDATE), form, EXCLUDED_CONTEXTS);
    }

    @Test
    void updateProxies() throws Exception {
        page.getTabs().select("proxies-tab");
        form = page.getProxiesForm();
        crud.update(proxyAddress(PROXY_UPDATE), form, PROXY_URL);
    }

    @Test
    void updateNetworking() throws Exception {
        page.getTabs().select("networking-tab");
        form = page.getNetworkingForm();
        crud.update(proxyAddress(PROXY_UPDATE), form, NODE_TIMEOUT, 123);
    }
}
