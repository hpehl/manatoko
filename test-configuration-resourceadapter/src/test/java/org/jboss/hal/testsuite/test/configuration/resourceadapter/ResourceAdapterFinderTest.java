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
package org.jboss.hal.testsuite.test.configuration.resourceadapter;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.ARCHIVE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.RESOURCE_ADAPTERS;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.ResourceAdapterFixtures.RESOURCE_ADAPTER_CREATE;
import static org.jboss.hal.testsuite.fixtures.ResourceAdapterFixtures.RESOURCE_ADAPTER_DELETE;
import static org.jboss.hal.testsuite.fixtures.ResourceAdapterFixtures.RESOURCE_ADAPTER_READ;
import static org.jboss.hal.testsuite.fixtures.ResourceAdapterFixtures.resourceAdapterAddress;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Manatoko
@Testcontainers
class ResourceAdapterFinderTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, STANDALONE);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(resourceAdapterAddress(RESOURCE_ADAPTER_READ), Values.of(ARCHIVE, Random.name()));
        operations.add(resourceAdapterAddress(RESOURCE_ADAPTER_DELETE), Values.of(ARCHIVE, Random.name()));
    }

    @Inject Console console;
    ColumnFragment column;

    @BeforeEach
    void setUp() {
        column = console.finder(NameTokens.CONFIGURATION, configurationSubsystemPath(RESOURCE_ADAPTERS))
                .column(Ids.RESOURCE_ADAPTER);
    }

    @Test
    void create() throws Exception {
        AddResourceDialogFragment dialog = column.add();
        dialog.getForm().text(NAME, RESOURCE_ADAPTER_CREATE);
        dialog.getForm().text(ARCHIVE, Random.name());
        dialog.add();

        console.verifySuccess();
        new ResourceVerifier(resourceAdapterAddress(RESOURCE_ADAPTER_CREATE), client)
                .verifyExists();
    }

    @Test
    void select() {
        column.selectItem(Ids.resourceAdapter(RESOURCE_ADAPTER_READ));
        PlaceRequest place = Places.finderPlace(NameTokens.CONFIGURATION, configurationSubsystemPath(RESOURCE_ADAPTERS)
                .append(Ids.RESOURCE_ADAPTER, Ids.resourceAdapter(RESOURCE_ADAPTER_READ)));
        console.verify(place);
    }

    @Test
    void view() {
        column.selectItem(Ids.resourceAdapter(RESOURCE_ADAPTER_READ)).view();
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.RESOURCE_ADAPTER)
                .with(NAME, RESOURCE_ADAPTER_READ)
                .build();
        console.verify(placeRequest);
    }

    @Test
    void delete() throws Exception {
        column.selectItem(Ids.resourceAdapter(RESOURCE_ADAPTER_DELETE))
                .dropdown()
                .click("Remove");
        console.confirmationDialog().confirm();

        console.verifySuccess();
        assertFalse(column.containsItem(Ids.resourceAdapter(RESOURCE_ADAPTER_DELETE)));
        new ResourceVerifier(resourceAdapterAddress(RESOURCE_ADAPTER_DELETE), client)
                .verifyDoesNotExist();
    }
}
