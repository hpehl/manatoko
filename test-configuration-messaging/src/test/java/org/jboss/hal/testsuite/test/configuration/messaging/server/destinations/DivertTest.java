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

import org.jboss.hal.testsuite.Random;
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
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_DIVERT;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.DIVERT_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.DIVERT_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.DIVERT_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.DIVERT_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.FORWARDING_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.divertAddress;

@Manatoko
@Testcontainers
class DivertTest extends AbstractServerDestinationsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(divertAddress(SRV_UPDATE, DIVERT_UPDATE),
                Values.of(DIVERT_ADDRESS, Random.name()).and(FORWARDING_ADDRESS, Random.name())).assertSuccess();
        operations.add(divertAddress(SRV_UPDATE, DIVERT_DELETE),
                Values.of(DIVERT_ADDRESS, Random.name()).and(FORWARDING_ADDRESS, Random.name())).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void create() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_DIVERT + ID_DELIMITER + ITEM);
        TableFragment table = page.getDivertTable();
        FormFragment form = page.getDivertForm();
        table.bind(form);

        crudOperations.create(divertAddress(SRV_UPDATE, DIVERT_CREATE), table, f -> {
            f.text(NAME, DIVERT_CREATE);
            f.text(DIVERT_ADDRESS, Random.name());
            f.text(FORWARDING_ADDRESS, Random.name());
        });
    }

    @Test
    void tryCreate() {
        console.verticalNavigation().selectPrimary(MESSAGING_DIVERT + ID_DELIMITER + ITEM);
        TableFragment table = page.getDivertTable();
        FormFragment form = page.getDivertForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, DIVERT_CREATE, DIVERT_ADDRESS);
    }

    @Test
    void editAddress() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_DIVERT + ID_DELIMITER + ITEM);
        TableFragment table = page.getDivertTable();
        FormFragment form = page.getDivertForm();
        table.bind(form);

        table.select(DIVERT_UPDATE);
        crudOperations.update(divertAddress(SRV_UPDATE, DIVERT_UPDATE), form, DIVERT_ADDRESS);
    }

    @Test
    void tryEditAddress() {
        console.verticalNavigation().selectPrimary(MESSAGING_DIVERT + ID_DELIMITER + ITEM);
        TableFragment table = page.getDivertTable();
        FormFragment form = page.getDivertForm();
        table.bind(form);

        table.select(DIVERT_UPDATE);
        crudOperations.updateWithError(form, f -> f.clear(DIVERT_ADDRESS), DIVERT_ADDRESS);
    }

    @Test
    void remove() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_DIVERT + ID_DELIMITER + ITEM);
        TableFragment table = page.getDivertTable();
        FormFragment form = page.getDivertForm();
        table.bind(form);

        crudOperations.delete(divertAddress(SRV_UPDATE, DIVERT_DELETE), table, DIVERT_DELETE);
    }
}
