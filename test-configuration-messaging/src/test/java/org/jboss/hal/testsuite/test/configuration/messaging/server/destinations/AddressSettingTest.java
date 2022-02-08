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
package org.jboss.hal.testsuite.test.configuration.messaging.server.destinations;

import org.jboss.hal.testsuite.command.AddMessagingServer;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.DEAD_LETTER_ADDRESS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_ADDRESS_SETTING;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.AS_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.AS_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.AS_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.addressSettingAddress;

@Manatoko
@Testcontainers
class AddressSettingTest extends AbstractServerDestinationsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(addressSettingAddress(SRV_UPDATE, AS_UPDATE)).assertSuccess();
        operations.add(addressSettingAddress(SRV_UPDATE, AS_DELETE)).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void create() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_ADDRESS_SETTING + ID_DELIMITER + ITEM);
        TableFragment table = page.getAddressSettingTable();
        FormFragment form = page.getAddressSettingForm();
        table.bind(form);

        crudOperations.create(addressSettingAddress(SRV_UPDATE, AS_CREATE), table, AS_CREATE);
    }

    @Test
    void editDeadLetterAddress() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_ADDRESS_SETTING + ID_DELIMITER + ITEM);
        TableFragment table = page.getAddressSettingTable();
        FormFragment form = page.getAddressSettingForm();
        table.bind(form);

        table.select(AS_UPDATE);
        crudOperations.update(addressSettingAddress(SRV_UPDATE, AS_UPDATE), form, DEAD_LETTER_ADDRESS);
    }

    @Test
    void remove() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_ADDRESS_SETTING + ID_DELIMITER + ITEM);
        TableFragment table = page.getAddressSettingTable();
        FormFragment form = page.getAddressSettingForm();
        table.bind(form);

        crudOperations.delete(addressSettingAddress(SRV_UPDATE, AS_DELETE), table, AS_DELETE);
    }
}
