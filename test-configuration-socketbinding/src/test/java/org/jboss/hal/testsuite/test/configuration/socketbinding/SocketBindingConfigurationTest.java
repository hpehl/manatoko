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
package org.jboss.hal.testsuite.test.configuration.socketbinding;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.HeaderBreadcrumbFragment;
import org.jboss.hal.testsuite.page.configuration.SocketBindingPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT_INTERFACE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.PRIVATE;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.PUBLIC;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.SBG_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.socketBindingGroupAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
@Disabled // TODO Fix test issues
class SocketBindingConfigurationTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(socketBindingGroupAddress(SBG_UPDATE), Values.empty().and(DEFAULT_INTERFACE, PUBLIC));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page SocketBindingPage page;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate(NAME, SBG_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.SOCKET_BINDING_GROUP + "-" + Ids.CONFIGURATION + "-" + Ids.ITEM);
        form = page.getConfigurationForm();
    }

    @Test
    void view() {
        assertEquals(HeaderBreadcrumbFragment.abbreviate(SBG_UPDATE), console.header().breadcrumb().lastValue());
    }

    @Test
    void update() throws Exception {
        crud.update(socketBindingGroupAddress(SBG_UPDATE), form, DEFAULT_INTERFACE, PRIVATE);
    }
}
