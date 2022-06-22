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
package org.jboss.hal.testsuite.test.configuration.messaging.jms.bridge;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.dmr.ModelNode;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.command.AddJmsBridge;
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

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import static org.jboss.hal.dmr.ModelDescriptionConstants.MESSAGING_ACTIVEMQ;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MODULE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.resources.Ids.JMS_BRIDGE;
import static org.jboss.hal.resources.Ids.JMS_BRIDGE_ITEM;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONNECTION_FACTORY_VALUE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.DESTINATION_QUEUE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMS_BRIDGE_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMS_BRIDGE_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.JMS_BRIDGE_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.REMOTE_CONNECTION_FACTORY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SOURCE_CONNECTION_FACTORY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SOURCE_DESTINATION;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.TARGET_CONNECTION_FACTORY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.TARGET_CONTEXT;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.TARGET_DESTINATION;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.jmsBridgeAddress;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Manatoko
@Testcontainers
class FinderTest {

    private static final ModelNode TARGET_CONTEXT_MODEL;
    private static OnlineManagementClient client;

    static {
        TARGET_CONTEXT_MODEL = new ModelNode();
        TARGET_CONTEXT_MODEL.get("java.naming.factory.initial")
                .set("org.jboss.naming.remote.client.InitialContextFactory");
        TARGET_CONTEXT_MODEL.get("java.naming.provider.url").set("http-remoting://localhost:8180");
    }

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        client.apply(new AddJmsBridge(JMS_BRIDGE_UPDATE), new AddJmsBridge(JMS_BRIDGE_DELETE));
    }

    @Inject Console console;
    ColumnFragment column;

    @BeforeEach
    void setUp() {
        column = console.finder(NameTokens.CONFIGURATION, configurationSubsystemPath(MESSAGING_ACTIVEMQ)
                .append(Ids.MESSAGING_CATEGORY, JMS_BRIDGE_ITEM))
                .column(JMS_BRIDGE);
    }

    @Test
    void create() throws Exception {
        AddResourceDialogFragment dialog = column.add();
        dialog.getForm().text(NAME, JMS_BRIDGE_CREATE);
        dialog.getForm().properties(TARGET_CONTEXT).add(TARGET_CONTEXT_MODEL);
        dialog.getForm().text(SOURCE_CONNECTION_FACTORY, CONNECTION_FACTORY_VALUE);
        dialog.getForm().text(SOURCE_DESTINATION, DESTINATION_QUEUE);
        dialog.getForm().text(TARGET_CONNECTION_FACTORY, REMOTE_CONNECTION_FACTORY);
        dialog.getForm().text(TARGET_DESTINATION, DESTINATION_QUEUE);
        dialog.getForm().text(MODULE, "org.wildfly.extension.messaging-activemq");
        dialog.add();
        console.verifySuccess();
        assertTrue(column.containsItem(Ids.jmsBridge(JMS_BRIDGE_CREATE)));
        new ResourceVerifier(jmsBridgeAddress(JMS_BRIDGE_CREATE), client).verifyExists();
    }

    @Test
    void read() {
        assertTrue(column.containsItem(Ids.jmsBridge(JMS_BRIDGE_UPDATE)));
    }

    @Test
    void select() {
        column.selectItem(Ids.jmsBridge(JMS_BRIDGE_UPDATE));
        PlaceRequest placeRequest = Places.finderPlace(NameTokens.CONFIGURATION, new FinderPath()
                .append(Ids.CONFIGURATION, Ids.asId(Names.SUBSYSTEMS))
                .append(Ids.CONFIGURATION_SUBSYSTEM, MESSAGING_ACTIVEMQ)
                .append(Ids.MESSAGING_CATEGORY, JMS_BRIDGE_ITEM)
                .append(Ids.JMS_BRIDGE, Ids.jmsBridge(JMS_BRIDGE_UPDATE)));
        console.verify(placeRequest);
    }

    @Test
    void view() {
        column.selectItem(Ids.jmsBridge(JMS_BRIDGE_UPDATE)).view();

        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.JMS_BRIDGE)
                .with(NAME, JMS_BRIDGE_UPDATE)
                .build();
        console.verify(placeRequest);
    }

    @Test
    void delete() throws Exception {
        column.selectItem(Ids.jmsBridge(JMS_BRIDGE_DELETE)).dropdown().click("Remove");
        console.confirmationDialog().confirm();

        console.verifySuccess();
        assertFalse(column.containsItem(Ids.jmsBridge(JMS_BRIDGE_DELETE)));
        new ResourceVerifier(jmsBridgeAddress(JMS_BRIDGE_DELETE), client).verifyDoesNotExist();
    }
}
