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

import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddMessagingServer;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
import org.jboss.hal.testsuite.fragment.finder.FinderPath;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.model.ServerEnvironmentUtils;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.ENTRIES;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MESSAGING_ACTIVEMQ;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PAUSED;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_JMS_TOPIC;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMS_TOPIC_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMS_TOPIC_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMS_TOPIC_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.jmsTopicAddress;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.runtimeSubsystemPath;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class JMSTopicTest extends AbstractServerDestinationsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();
    static OnlineManagementClient client;
    static ServerEnvironmentUtils serverEnvironmentUtils;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        serverEnvironmentUtils = new ServerEnvironmentUtils(client);
        Operations operations = new Operations(client);
        operations.add(jmsTopicAddress(SRV_UPDATE, JMS_TOPIC_UPDATE), Values.ofList(ENTRIES, Random.name()))
                .assertSuccess();
        operations.add(jmsTopicAddress(SRV_UPDATE, JMS_TOPIC_DELETE), Values.ofList(ENTRIES, Random.name()))
                .assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void create() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_JMS_TOPIC + ID_DELIMITER + ITEM);
        TableFragment table = page.getJmsTopicTable();
        FormFragment form = page.getJmsTopicForm();
        table.bind(form);

        crudOperations.create(jmsTopicAddress(SRV_UPDATE, JMS_TOPIC_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, JMS_TOPIC_CREATE);
                    formFragment.properties(ENTRIES).add(Random.name());
                });
    }

    @Test
    void editEntries() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_JMS_TOPIC + ID_DELIMITER + ITEM);
        TableFragment table = page.getJmsTopicTable();
        FormFragment form = page.getJmsTopicForm();
        table.bind(form);
        String val = Random.name();

        table.select(JMS_TOPIC_UPDATE);
        crudOperations.update(jmsTopicAddress(SRV_UPDATE, JMS_TOPIC_UPDATE), form,
                formFragment -> formFragment.list(ENTRIES).add(val),
                resourceVerifier -> resourceVerifier.verifyListAttributeContainsValue(ENTRIES, val));
    }

    @Test
    void remove() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_JMS_TOPIC + ID_DELIMITER + ITEM);
        TableFragment table = page.getJmsTopicTable();
        FormFragment form = page.getJmsTopicForm();
        table.bind(form);

        crudOperations.delete(jmsTopicAddress(SRV_UPDATE, JMS_TOPIC_DELETE), table, JMS_TOPIC_DELETE);
    }

    @Test
    void pause() throws Exception {
        FinderPath path = runtimeSubsystemPath(serverEnvironmentUtils.getServerHostName(), MESSAGING_ACTIVEMQ)
                .append(Ids.MESSAGING_SERVER_RUNTIME, Ids.messagingServer(SRV_UPDATE));
        String itemId = Ids.destination(null, null, SRV_UPDATE, "jms-topic",
                JMS_TOPIC_UPDATE);

        ColumnFragment column = console.finder(NameTokens.RUNTIME, path)
                .column(Ids.MESSAGING_SERVER_DESTINATION_RUNTIME);
        column.selectItem(itemId)
                .dropdown()
                .click("Pause");

        new ResourceVerifier(jmsTopicAddress(SRV_UPDATE, JMS_TOPIC_UPDATE), client)
                .verifyAttribute(PAUSED, true);
    }
}
