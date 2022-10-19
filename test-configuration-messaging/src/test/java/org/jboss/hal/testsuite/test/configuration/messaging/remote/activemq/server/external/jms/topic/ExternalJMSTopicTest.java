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
package org.jboss.hal.testsuite.test.configuration.messaging.remote.activemq.server.external.jms.topic;

import java.io.IOException;
import java.util.Arrays;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.model.ModelNodeGenerator;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.page.configuration.MessagingRemoteActiveMQPage;
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

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.RemoteActiveMQServer;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class ExternalJMSTopicTest {

    private static final String EXTERNAL_JMS_TOPIC_CREATE = "external-jms-topic-to-be-created-" + Random.name();
    private static final String EXTERNAL_JMS_TOPIC_UPDATE = "external-jms-topic-to-be-updated-" + Random.name();
    private static final String EXTERNAL_JMS_TOPIC_DELETE = "external-jms-topic-to-be-deleted-" + Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        createExternalJMSTopic(EXTERNAL_JMS_TOPIC_UPDATE);
        createExternalJMSTopic(EXTERNAL_JMS_TOPIC_DELETE);
    }

    private static void createExternalJMSTopic(String name) throws IOException {
        operations.add(RemoteActiveMQServer.externalJMSTopicAddress(name),
                Values.of("entries", new ModelNodeGenerator.ModelNodeListBuilder()
                        .addAll(Random.name(), Random.name(), Random.name()).build()))
                .assertSuccess();
    }

    @Inject Console console;
    @Page MessagingRemoteActiveMQPage page;
    @Inject CrudOperations crudOperations;

    @BeforeEach
    void before() {
        page.navigate();
        console.verticalNavigation().selectPrimary("msg-remote-external-topic-item");
    }

    @Test
    void create() throws Exception {
        crudOperations.create(RemoteActiveMQServer.externalJMSTopicAddress(EXTERNAL_JMS_TOPIC_CREATE),
                page.getExternalJMSTopicTable(), formFragment -> {
                    formFragment.text(ModelDescriptionConstants.NAME, EXTERNAL_JMS_TOPIC_CREATE);
                    formFragment.list("entries").add(Arrays.asList(Random.name(), Random.name(), Random.name()));
                }, ResourceVerifier::verifyExists);
    }

    @Test
    void remove() throws Exception {
        crudOperations.delete(RemoteActiveMQServer.externalJMSTopicAddress(EXTERNAL_JMS_TOPIC_DELETE),
                page.getExternalJMSTopicTable(), EXTERNAL_JMS_TOPIC_DELETE);
    }
}
