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
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddHttpAuthenticationFactory;
import org.jboss.hal.testsuite.command.AddSecurityDomain;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.page.configuration.ApplicationSecurityDomainPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTP_AUTHENTICATION_FACTORY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SECURITY_DOMAIN;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.APPLICATION_SECURITY_DOMAIN_HAF;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.APPLICATION_SECURITY_DOMAIN_SD;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.APPLICATION_SECURITY_DOMAIN_UPDATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.applicationSecurityDomainAddress;

@Manatoko
@Testcontainers
class ApplicationSecurityDomainConflictTest {

    private static final String SECURITY_DOMAIN_NAME = Random.name();
    private static final String HTTP_AUTHENTICATION_FACTORY_NAME = Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);
    static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddSecurityDomain(SECURITY_DOMAIN_NAME));
        client.apply(new AddHttpAuthenticationFactory(HTTP_AUTHENTICATION_FACTORY_NAME));

        operations = new Operations(client);
        operations.add(applicationSecurityDomainAddress(APPLICATION_SECURITY_DOMAIN_UPDATE),
                Values.of(HTTP_AUTHENTICATION_FACTORY, HTTP_AUTHENTICATION_FACTORY_NAME));
        operations.add(applicationSecurityDomainAddress(APPLICATION_SECURITY_DOMAIN_HAF),
                Values.of(HTTP_AUTHENTICATION_FACTORY, HTTP_AUTHENTICATION_FACTORY_NAME));
        operations.add(applicationSecurityDomainAddress(APPLICATION_SECURITY_DOMAIN_SD),
                Values.of(SECURITY_DOMAIN, SECURITY_DOMAIN_NAME));
    }

    @Page ApplicationSecurityDomainPage page;
    @Inject CrudOperations crud;

    @Test
    void preventBothHttpAuthenticationFactoryAndSecurityDomain() {
        page.navigate(NAME, APPLICATION_SECURITY_DOMAIN_HAF);
        crud.updateWithError(page.getAttributesForm(), SECURITY_DOMAIN, SECURITY_DOMAIN_NAME);
    }

    @Test
    void preventBothSecurityDomainAndHttpAuthenticationFactory() {
        page.navigate(NAME, APPLICATION_SECURITY_DOMAIN_SD);
        crud.updateWithError(page.getAttributesForm(), HTTP_AUTHENTICATION_FACTORY, HTTP_AUTHENTICATION_FACTORY_NAME);
    }
}
