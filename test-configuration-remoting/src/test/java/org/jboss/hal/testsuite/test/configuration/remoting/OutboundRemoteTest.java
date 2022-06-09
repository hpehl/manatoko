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
package org.jboss.hal.testsuite.test.configuration.remoting;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.dmr.ModelNode;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddRemoteSocketBinding;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.page.configuration.RemotingPage;
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
import static org.jboss.hal.dmr.ModelDescriptionConstants.OUTBOUND_SOCKET_BINDING_REF;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PROPERTY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.VALUE;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.BACKLOG;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.REMOTE_OUTBOUND_CREATE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.REMOTE_OUTBOUND_DELETE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.REMOTE_OUTBOUND_READ;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.REMOTE_OUTBOUND_UPDATE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.outboundRemoteAddress;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.LOCALHOST;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.OUTBOUND_REMOTE_PORT;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.OUTBOUND_REMOTE_READ;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class OutboundRemoteTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, STANDALONE);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        client.apply(new AddRemoteSocketBinding(OUTBOUND_REMOTE_READ, LOCALHOST, OUTBOUND_REMOTE_PORT));

        operations.add(outboundRemoteAddress(REMOTE_OUTBOUND_READ),
                Values.of(OUTBOUND_SOCKET_BINDING_REF, OUTBOUND_REMOTE_READ));
        operations.add(outboundRemoteAddress(REMOTE_OUTBOUND_UPDATE),
                Values.of(OUTBOUND_SOCKET_BINDING_REF, OUTBOUND_REMOTE_READ));
        operations.add(outboundRemoteAddress(REMOTE_OUTBOUND_DELETE),
                Values.of(OUTBOUND_SOCKET_BINDING_REF, OUTBOUND_REMOTE_READ));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page RemotingPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary("remoting-outbound-connection-item",
                "remoting-remote-outbound-sub-item");
        table = page.getRemoteOutboundTable();
        form = page.getRemoteOutboundForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(outboundRemoteAddress(REMOTE_OUTBOUND_CREATE), table, form -> {
            form.text(NAME, REMOTE_OUTBOUND_CREATE);
            form.text(OUTBOUND_SOCKET_BINDING_REF, OUTBOUND_REMOTE_READ);
        });
    }

    @Test
    void read() {
        table.select(REMOTE_OUTBOUND_READ);
        form.showSensitive(OUTBOUND_SOCKET_BINDING_REF);
        assertEquals(OUTBOUND_REMOTE_READ, form.value(OUTBOUND_SOCKET_BINDING_REF));
    }

    @Test
    void update() throws Exception {
        ModelNode properties = Random.properties(BACKLOG, "34");

        table.select(REMOTE_OUTBOUND_UPDATE);
        crud.update(outboundRemoteAddress(REMOTE_OUTBOUND_UPDATE), form,
                f -> f.properties(PROPERTY).add(properties),
                resourceVerifier -> {
                    // properties are nested resources!
                    ResourceVerifier propertyVerifier = new ResourceVerifier(
                            outboundRemoteAddress(REMOTE_OUTBOUND_UPDATE).and(PROPERTY, BACKLOG), client);
                    propertyVerifier.verifyAttribute(VALUE, "34");
                });
    }

    @Test
    void delete() throws Exception {
        crud.delete(outboundRemoteAddress(REMOTE_OUTBOUND_DELETE), table, REMOTE_OUTBOUND_DELETE);
    }
}
