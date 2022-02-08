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
package org.jboss.hal.testsuite.test.configuration.remoting;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.dmr.ModelNode;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
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
import static org.jboss.hal.dmr.ModelDescriptionConstants.PROPERTY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.VALUE;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.BACKLOG;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.OUTBOUND_CREATE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.OUTBOUND_DELETE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.OUTBOUND_READ;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.OUTBOUND_UPDATE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.URI;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.outboundAddress;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.uri;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class OutboundTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(outboundAddress(OUTBOUND_READ), Values.of(URI, uri(OUTBOUND_READ)));
        operations.add(outboundAddress(OUTBOUND_UPDATE), Values.of(URI, uri(OUTBOUND_UPDATE)));
        operations.add(outboundAddress(OUTBOUND_DELETE), Values.of(URI, uri(OUTBOUND_DELETE)));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page RemotingPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectSecondary("remoting-outbound-connection-item",
                "remoting-outbound-sub-item");
        table = page.getOutboundTable();
        form = page.getOutboundForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(outboundAddress(OUTBOUND_CREATE), table, form -> {
            form.text(NAME, OUTBOUND_CREATE);
            form.text(URI, uri(OUTBOUND_CREATE));
        });
    }

    @Test
    void read() {
        table.select(OUTBOUND_READ);
        assertEquals(uri(OUTBOUND_READ), form.value(URI));
    }

    @Test
    void update() throws Exception {
        ModelNode properties = Random.properties(BACKLOG, "15");

        table.select(OUTBOUND_UPDATE);
        crud.update(outboundAddress(OUTBOUND_UPDATE), form,
                f -> f.properties(PROPERTY).add(properties),
                resourceVerifier -> {
                    // properties are nested resources!
                    ResourceVerifier propertyVerifier = new ResourceVerifier(
                            outboundAddress(OUTBOUND_UPDATE).and(PROPERTY, BACKLOG), client);
                    propertyVerifier.verifyAttribute(VALUE, "15");
                });
    }

    @Test
    void delete() throws Exception {
        crud.delete(outboundAddress(OUTBOUND_DELETE), table, OUTBOUND_DELETE);
    }
}
