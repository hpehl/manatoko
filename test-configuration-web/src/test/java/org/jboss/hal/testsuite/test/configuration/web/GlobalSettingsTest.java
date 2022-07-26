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
package org.jboss.hal.testsuite.test.configuration.web;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures;
import org.jboss.hal.testsuite.page.configuration.UndertowPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT_HOST;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT_SECURITY_DOMAIN;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT_WEB_MODULE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.INSTANCE_ID;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.RESOLVE_EXPRESSIONS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.STATISTICS_ENABLED;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures.DEFAULT_SERVER;
import static org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures.DEFAULT_SERVER_CREATE;
import static org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures.DEFAULT_SERVLET_CONTAINER;
import static org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures.DEFAULT_SERVLET_CONTAINER_CREATE;
import static org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures.DEFAULT_VIRTUAL_HOST;
import static org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures.DEFAULT_VIRTUAL_HOST_CREATE;
import static org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures.UNDERTOW_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures.servletContainerAddress;
import static org.jboss.hal.testsuite.fixtures.undertow.UndertowFixtures.virtualHostAddress;

@Manatoko
@Testcontainers
class GlobalSettingsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);
    static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        operations = new Operations(wildFly.managementClient());
        Address serverAddress = UndertowFixtures.serverAddress(DEFAULT_SERVER_CREATE);
        operations.add(serverAddress);
        operations.add(servletContainerAddress(DEFAULT_SERVLET_CONTAINER_CREATE));
        operations.add(virtualHostAddress(DEFAULT_SERVER_CREATE, DEFAULT_VIRTUAL_HOST_CREATE),
                Values.of(DEFAULT_WEB_MODULE, "module_" + Random.name(7) + ".war"));
        operations.writeAttribute(serverAddress, DEFAULT_HOST, DEFAULT_VIRTUAL_HOST_CREATE);
    }

    @Inject CrudOperations crudOperations;
    @Page UndertowPage page;

    @BeforeEach
    void prepare() {
        page.navigate();
    }

    @Test
    public void updateDefaultSecurityDomain() throws Exception {
        crudOperations.update(UNDERTOW_ADDRESS, page.getConfigurationForm(), DEFAULT_SECURITY_DOMAIN);
    }

    @Test
    public void updateDefaultServer() throws Exception {
        crudOperations.update(UNDERTOW_ADDRESS, page.getConfigurationForm(), DEFAULT_SERVER, DEFAULT_SERVER_CREATE);
    }

    @Test
    public void updateDefaultServletContainer() throws Exception {
        crudOperations.update(UNDERTOW_ADDRESS, page.getConfigurationForm(), DEFAULT_SERVLET_CONTAINER,
                DEFAULT_SERVLET_CONTAINER_CREATE);
    }

    @Test
    public void updateDefaultVirtualHost() throws Exception {
        crudOperations.update(UNDERTOW_ADDRESS, page.getConfigurationForm(),
                consumer -> {
                    consumer.text(DEFAULT_SERVER, DEFAULT_SERVER_CREATE);
                    consumer.text(DEFAULT_VIRTUAL_HOST, DEFAULT_VIRTUAL_HOST_CREATE);
                }, resourceVerifier -> resourceVerifier.verifyAttribute(DEFAULT_VIRTUAL_HOST,
                        DEFAULT_VIRTUAL_HOST_CREATE));
    }

    @Test
    public void updateInstanceId() throws Exception {
        crudOperations.update(UNDERTOW_ADDRESS, page.getConfigurationForm(), INSTANCE_ID);
    }

    @Test
    public void toggleStatisticsEnabled() throws Exception {
        boolean statisticsEnabled = operations.invoke("read-attribute", UNDERTOW_ADDRESS,
                Values.of(NAME, STATISTICS_ENABLED).and(RESOLVE_EXPRESSIONS, true)).booleanValue();
        crudOperations.update(UNDERTOW_ADDRESS, page.getConfigurationForm(),
                form -> {
                    form.getRoot().findElement(ByJQuery.selector("button[title=\"Switch to normal mode\"]")).click();
                    form.flip(STATISTICS_ENABLED, !statisticsEnabled);
                }, resourceVerifier -> resourceVerifier.verifyAttribute(STATISTICS_ENABLED, !statisticsEnabled));
    }
}
