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
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.command.AddSecurityDomain;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SECURITY_DOMAIN;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SECURITY_DOMAIN_CREATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.APPLICATION_SECURITY_DOMAIN_CREATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.APPLICATION_SECURITY_DOMAIN_DELETE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.APPLICATION_SECURITY_DOMAIN_READ;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.applicationSecurityDomainAddress;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Manatoko
@Testcontainers
class ApplicationSecurityDomainFinderTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);
    static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        client.apply(new AddSecurityDomain(SECURITY_DOMAIN_CREATE));

        Operations operations = new Operations(client);
        operations.add(applicationSecurityDomainAddress(APPLICATION_SECURITY_DOMAIN_READ),
                Values.of(SECURITY_DOMAIN, SECURITY_DOMAIN_CREATE));
        operations.add(applicationSecurityDomainAddress(APPLICATION_SECURITY_DOMAIN_DELETE),
                Values.of(SECURITY_DOMAIN, SECURITY_DOMAIN_CREATE));
    }

    @Inject Console console;
    ColumnFragment column;

    @BeforeEach
    void prepare() {
        column = console.finder(NameTokens.CONFIGURATION, configurationSubsystemPath(NameTokens.UNDERTOW)
                .append(Ids.UNDERTOW_SETTINGS, "application-security-domain"))
                .column(Ids.UNDERTOW_APP_SECURITY_DOMAIN);
    }

    @Test
    void create() throws Exception {
        AddResourceDialogFragment dialog = column.add();
        FormFragment form = dialog.getForm();
        form.text(NAME, APPLICATION_SECURITY_DOMAIN_CREATE);
        form.text(SECURITY_DOMAIN, SECURITY_DOMAIN_CREATE);
        dialog.add();

        console.verifySuccess();
        assertTrue(column.containsItem(Ids.undertowApplicationSecurityDomain(APPLICATION_SECURITY_DOMAIN_CREATE)));
        new ResourceVerifier(applicationSecurityDomainAddress(APPLICATION_SECURITY_DOMAIN_CREATE), client).verifyExists();
    }

    @Test
    void read() throws Exception {
        assertTrue(column.containsItem(Ids.undertowApplicationSecurityDomain(APPLICATION_SECURITY_DOMAIN_READ)));
        new ResourceVerifier(applicationSecurityDomainAddress(APPLICATION_SECURITY_DOMAIN_READ), client).verifyExists();
    }

    @Test
    void view() {
        column.selectItem(Ids.undertowApplicationSecurityDomain(APPLICATION_SECURITY_DOMAIN_READ))
                .view();
        console.verify(new PlaceRequest.Builder().nameToken(NameTokens.UNDERTOW_APPLICATION_SECURITY_DOMAIN)
                .with("name", APPLICATION_SECURITY_DOMAIN_READ).build());
    }

    @Test
    void delete() throws Exception {
        column.selectItem(Ids.undertowApplicationSecurityDomain(APPLICATION_SECURITY_DOMAIN_DELETE))
                .dropdown().click("Remove");
        console.confirmationDialog().confirm();
        console.verifySuccess();
        new ResourceVerifier(applicationSecurityDomainAddress(APPLICATION_SECURITY_DOMAIN_DELETE), client).verifyDoesNotExist();
    }
}
