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
package org.jboss.hal.testsuite.test.configuration.modcluster;

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

import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTPS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.LISTENER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MODCLUSTER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.PROXY_CREATE;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.PROXY_CREATE2;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.PROXY_DELETE;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.PROXY_READ;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.PROXY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.proxyAddress;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Manatoko
@Testcontainers
class ProxyFinderTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, HA);
    private static OnlineManagementClient client;
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        operations = new Operations(client);
        operations.add(proxyAddress(PROXY_READ), Values.of(LISTENER, HTTPS));
        operations.add(proxyAddress(PROXY_UPDATE), Values.of(LISTENER, HTTPS));
        operations.add(proxyAddress(PROXY_DELETE), Values.of(LISTENER, HTTPS));
    }

    @Inject Console console;
    ColumnFragment column;

    @BeforeEach
    void setUp() {
        column = console.finder(NameTokens.CONFIGURATION, configurationSubsystemPath(MODCLUSTER))
                .column(Ids.MODCLUSTER_PROXY);
    }

    @Test
    void create() throws Exception {
        AddResourceDialogFragment dialog = column.add();
        dialog.getForm().text(NAME, PROXY_CREATE);
        dialog.getForm().text(LISTENER, HTTPS);
        dialog.add();

        console.verifySuccess();
        assertTrue(column.containsItem(Ids.modclusterProxy(PROXY_CREATE)));
        new ResourceVerifier(proxyAddress(PROXY_CREATE), client).verifyExists();
    }

    @Test
    void read() {
        assertTrue(column.containsItem(Ids.modclusterProxy(PROXY_READ)));
    }

    @Test
    void refresh() throws Exception {
        operations.add(proxyAddress(PROXY_CREATE2), Values.of(LISTENER, HTTPS));
        console.waitNoNotification();
        column.refresh();
        assertTrue(column.containsItem(Ids.modclusterProxy(PROXY_CREATE2)));
    }

    @Test
    void select() {
        column.selectItem(Ids.modclusterProxy(PROXY_READ));
        PlaceRequest placeRequest = Places.finderPlace(NameTokens.CONFIGURATION, new FinderPath()
                .append(Ids.CONFIGURATION, Ids.asId(Names.SUBSYSTEMS))
                .append(Ids.CONFIGURATION_SUBSYSTEM, MODCLUSTER)
                .append(Ids.MODCLUSTER_PROXY, Ids.modclusterProxy(PROXY_READ)));
        console.verify(placeRequest);
    }

    @Test
    void view() {
        column.selectItem(Ids.modclusterProxy(PROXY_READ)).view();

        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.MODCLUSTER)
                .with(NAME, PROXY_READ)
                .build();
        console.verify(placeRequest);
    }

    @Test
    void delete() throws Exception {
        column.selectItem(Ids.modclusterProxy(PROXY_DELETE)).dropdown().click("Remove");
        console.confirmationDialog().confirm();

        console.verifySuccess();
        assertFalse(column.containsItem(Ids.modclusterProxy(PROXY_DELETE)));
        new ResourceVerifier(proxyAddress(PROXY_DELETE), client).verifyDoesNotExist();
    }
}
