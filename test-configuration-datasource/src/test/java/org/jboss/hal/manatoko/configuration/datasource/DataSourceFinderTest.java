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
package org.jboss.hal.manatoko.configuration.datasource;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.manatoko.Console;
import org.jboss.hal.manatoko.Random;
import org.jboss.hal.manatoko.creaper.ResourceVerifier;
import org.jboss.hal.manatoko.fragment.finder.ColumnFragment;
import org.jboss.hal.manatoko.page.Places;
import org.jboss.hal.manatoko.test.WildFlyTest;
import org.jboss.hal.manatoko.util.Library;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.extras.creaper.commands.datasources.AddDataSource;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;

import static org.jboss.hal.dmr.ModelDescriptionConstants.DATASOURCES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ENABLED;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.DATA_SOURCE_DELETE;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.DATA_SOURCE_DISABLE;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.DATA_SOURCE_ENABLE;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.DATA_SOURCE_READ;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.DATA_SOURCE_TEST;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.dataSourceAddress;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.h2ConnectionUrl;
import static org.jboss.hal.manatoko.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.jboss.hal.resources.UIConstants.MEDIUM_TIMEOUT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataSourceFinderTest extends WildFlyTest {

    private static final String H2_DRIVER_NAME = "h2";
    private static final OnlineManagementClient client = wildFly.managementClient();

    @BeforeAll
    static void setupModel() throws Exception {
        client.apply(new AddDataSource.Builder<>(DATA_SOURCE_DELETE)
                .driverName(H2_DRIVER_NAME)
                .jndiName(Random.jndiName(DATA_SOURCE_DELETE))
                .connectionUrl(h2ConnectionUrl(DATA_SOURCE_DELETE))
                .build());
        client.apply(new AddDataSource.Builder<>(DATA_SOURCE_DISABLE)
                .driverName(H2_DRIVER_NAME)
                .jndiName(Random.jndiName(DATA_SOURCE_DISABLE))
                .connectionUrl(h2ConnectionUrl(DATA_SOURCE_DISABLE))
                .enableAfterCreate()
                .build());
        client.apply(new AddDataSource.Builder<>(DATA_SOURCE_ENABLE)
                .driverName(H2_DRIVER_NAME)
                .jndiName(Random.jndiName(DATA_SOURCE_ENABLE))
                .connectionUrl(h2ConnectionUrl(DATA_SOURCE_ENABLE))
                .build());
        client.apply(new AddDataSource.Builder<>(DATA_SOURCE_READ)
                .driverName(H2_DRIVER_NAME)
                .jndiName(Random.jndiName(DATA_SOURCE_READ))
                .connectionUrl(h2ConnectionUrl(DATA_SOURCE_READ))
                .build());
        client.apply(new AddDataSource.Builder<>(DATA_SOURCE_TEST)
                .driverName(H2_DRIVER_NAME)
                .jndiName(Random.jndiName(DATA_SOURCE_TEST))
                .connectionUrl(h2ConnectionUrl(DATA_SOURCE_TEST))
                .enableAfterCreate()
                .build());
    }

    @Inject Console console;
    ColumnFragment column;

    @BeforeEach
    void prepare() {
        column = console.finder(NameTokens.CONFIGURATION, configurationSubsystemPath(DATASOURCES)
                .append(Ids.DATA_SOURCE_DRIVER, Ids.asId(Names.DATASOURCES)))
                .column(Ids.DATA_SOURCE_CONFIGURATION);
    }

    @Test
    void read() {
        assertTrue(column.containsItem(Ids.dataSourceConfiguration(DATA_SOURCE_READ, false)));
    }

    @Test
    void select() {
        column.selectItem(Ids.dataSourceConfiguration(DATA_SOURCE_READ, false));
        PlaceRequest placeRequest = Places.finderPlace(NameTokens.CONFIGURATION,
                configurationSubsystemPath(DATASOURCES)
                        .append(Ids.DATA_SOURCE_DRIVER, Ids.asId(Names.DATASOURCES))
                        .append(Ids.DATA_SOURCE_CONFIGURATION, Ids.dataSourceConfiguration(DATA_SOURCE_READ, false)));
        console.verify(placeRequest);
    }

    @Test
    void view() {
        column.selectItem(Ids.dataSourceConfiguration(DATA_SOURCE_READ, false))
                .view();

        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.DATA_SOURCE_CONFIGURATION)
                .with(NAME, DATA_SOURCE_READ)
                .build();
        console.verify(placeRequest);
    }

    @Test
    void disable() throws Exception {
        column.selectItem(Ids.dataSourceConfiguration(DATA_SOURCE_DISABLE, false))
                .dropdown()
                .click("Disable");

        console.verifySuccess();
        new ResourceVerifier(dataSourceAddress(DATA_SOURCE_DISABLE), client)
                .verifyAttribute(ENABLED, false);
    }

    @Test
    void enable() throws Exception {
        column.selectItem(Ids.dataSourceConfiguration(DATA_SOURCE_ENABLE, false))
                .dropdown()
                .click("Enable");

        console.verifySuccess();
        new ResourceVerifier(dataSourceAddress(DATA_SOURCE_ENABLE), client)
                .verifyAttribute(ENABLED, true);
    }

    @Test
    void testConnection() {
        column.selectItem(Ids.dataSourceConfiguration(DATA_SOURCE_TEST, false))
                .dropdown()
                .click("Test Connection");
        console.verifySuccess();
    }

    @Test
    void delete() throws Exception {
        column.selectItem(Ids.dataSourceConfiguration(DATA_SOURCE_DELETE, false))
                .dropdown()
                .click("Remove");
        console.confirmationDialog().confirm();

        console.verifySuccess();
        Library.letsSleep(MEDIUM_TIMEOUT);
        assertFalse(column.containsItem(Ids.dataSourceConfiguration(DATA_SOURCE_DELETE, false)));
        new ResourceVerifier(dataSourceAddress(DATA_SOURCE_DELETE), client)
                .verifyDoesNotExist();
    }
}
