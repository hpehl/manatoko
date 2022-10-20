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

import static org.jboss.hal.dmr.ModelDescriptionConstants.ENTRIES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_JMS_QUEUE;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMS_QUEUE_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMS_QUEUE_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMS_QUEUE_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.jmsQueueAddress;

@Manatoko
@Testcontainers
class JMSQueueTest extends AbstractServerDestinationsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(jmsQueueAddress(SRV_UPDATE, JMS_QUEUE_UPDATE), Values.ofList(ENTRIES, Random.name()))
                .assertSuccess();
        operations.add(jmsQueueAddress(SRV_UPDATE, JMS_QUEUE_DELETE), Values.ofList(ENTRIES, Random.name()))
                .assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void create() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_JMS_QUEUE + ID_DELIMITER + ITEM);
        TableFragment table = page.getJmsQueueTable();
        FormFragment form = page.getJmsQueueForm();
        table.bind(form);

        crudOperations.create(jmsQueueAddress(SRV_UPDATE, JMS_QUEUE_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, JMS_QUEUE_CREATE);
                    formFragment.properties(ENTRIES).add(Random.name());
                });
    }

    @Test
    void editEntries() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_JMS_QUEUE + ID_DELIMITER + ITEM);
        TableFragment table = page.getJmsQueueTable();
        FormFragment form = page.getJmsQueueForm();
        table.bind(form);
        String val = Random.name();

        table.select(JMS_QUEUE_UPDATE);
        crudOperations.update(jmsQueueAddress(SRV_UPDATE, JMS_QUEUE_UPDATE), form,
                formFragment -> formFragment.list(ENTRIES).add(val),
                resourceVerifier -> resourceVerifier.verifyListAttributeContainsValue(ENTRIES, val));
    }

    @Test
    void remove() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_JMS_QUEUE + ID_DELIMITER + ITEM);
        TableFragment table = page.getJmsQueueTable();
        FormFragment form = page.getJmsQueueForm();
        table.bind(form);

        crudOperations.delete(jmsQueueAddress(SRV_UPDATE, JMS_QUEUE_DELETE), table, JMS_QUEUE_DELETE);
    }
}
