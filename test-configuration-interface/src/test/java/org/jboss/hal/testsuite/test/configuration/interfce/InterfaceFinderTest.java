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
package org.jboss.hal.testsuite.test.configuration.interfce;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
import org.jboss.hal.testsuite.fragment.finder.FinderPath;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.page.Places;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.INET_ADDRESS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.InterfaceFixtures.CREATE;
import static org.jboss.hal.testsuite.fixtures.InterfaceFixtures.DELETE;
import static org.jboss.hal.testsuite.fixtures.InterfaceFixtures.LOCALHOST;
import static org.jboss.hal.testsuite.fixtures.InterfaceFixtures.READ;
import static org.jboss.hal.testsuite.fixtures.InterfaceFixtures.interfaceAddress;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Manatoko
@Testcontainers
class InterfaceFinderTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);
    static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(interfaceAddress(READ), Values.empty().and(INET_ADDRESS, LOCALHOST));
        operations.add(interfaceAddress(DELETE), Values.empty().and(INET_ADDRESS, LOCALHOST));
    }

    @Inject Console console;
    ColumnFragment column;

    @BeforeEach
    void prepare() {
        column = console.finder(NameTokens.CONFIGURATION,
                new FinderPath().append(Ids.CONFIGURATION, Ids.asId(Names.INTERFACES)))
                .column(Ids.INTERFACE);
    }

    @Test
    void create() throws Exception {
        AddResourceDialogFragment dialog = column.add();
        dialog.getForm().text(NAME, CREATE);
        dialog.getForm().text(INET_ADDRESS, LOCALHOST);
        dialog.add();

        console.verifySuccess();
        assertTrue(column.containsItem(CREATE));
        new ResourceVerifier(interfaceAddress(CREATE), client).verifyExists();
    }

    @Test
    void read() {
        assertTrue(column.containsItem(READ));
    }

    @Test
    void select() {
        column.selectItem(READ);
        PlaceRequest placeRequest = Places.finderPlace(NameTokens.CONFIGURATION, new FinderPath()
                .append(Ids.CONFIGURATION, Ids.build(Names.INTERFACES))
                .append(Ids.INTERFACE, READ));
        console.verify(placeRequest);
    }

    @Test
    void view() {
        column.selectItem(READ).view();

        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.INTERFACE)
                .with(NAME, READ)
                .build();
        console.verify(placeRequest);
    }

    @Test
    void delete() throws Exception {
        column.selectItem(DELETE).dropdown().click("Remove");
        console.confirmationDialog().confirm();

        console.verifySuccess();
        assertFalse(column.containsItem(DELETE));
        new ResourceVerifier(interfaceAddress(DELETE), client).verifyDoesNotExist();
    }
}
