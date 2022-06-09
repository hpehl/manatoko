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
import static org.jboss.hal.dmr.ModelDescriptionConstants.QUEUE_ADDRESS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_CORE_QUEUE;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.COREQUEUE_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.COREQUEUE_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.coreQueueAddress;

@Manatoko
@Testcontainers
class CoreQueueTest extends AbstractServerDestinationsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(coreQueueAddress(SRV_UPDATE, COREQUEUE_DELETE), Values.of(QUEUE_ADDRESS, Random.name()))
                .assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void create() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_CORE_QUEUE + ID_DELIMITER + ITEM);
        TableFragment table = page.getCoreQueueTable();
        FormFragment form = page.getCoreQueueForm();
        table.bind(form);

        crudOperations.create(coreQueueAddress(SRV_UPDATE, COREQUEUE_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, COREQUEUE_CREATE);
                    formFragment.text(QUEUE_ADDRESS, Random.name());
                });
    }

    @Test
    void remove() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_CORE_QUEUE + ID_DELIMITER + ITEM);
        TableFragment table = page.getCoreQueueTable();
        FormFragment form = page.getCoreQueueForm();
        table.bind(form);

        crudOperations.delete(coreQueueAddress(SRV_UPDATE, COREQUEUE_DELETE), table, COREQUEUE_DELETE);
    }
}
