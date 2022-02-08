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
package org.jboss.hal.testsuite.test.configuration.messaging.server.connections.acceptor.http;

import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.test.configuration.messaging.server.connections.AbstractServerConnectionsTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.GROUP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTP_LISTENER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_ACCEPTOR;
import static org.jboss.hal.resources.Ids.MESSAGING_HTTP_ACCEPTOR;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_HTTP_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_HTTP_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_HTTP_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.UPGRADE_LEGACY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.acceptorHttpAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class HttpAcceptorTest extends AbstractServerConnectionsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        createServer(operations, SRV_UPDATE);
        operations.add(acceptorHttpAddress(SRV_UPDATE, ACCP_HTTP_UPDATE), Values.of(HTTP_LISTENER, DEFAULT))
                .assertSuccess();
        operations.add(acceptorHttpAddress(SRV_UPDATE, ACCP_HTTP_DELETE), Values.of(HTTP_LISTENER, DEFAULT))
                .assertSuccess();
    }

    @BeforeEach
    void setUp() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void acceptorHttpCreate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_HTTP_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorHttpTable();
        FormFragment form = page.getAcceptorHttpForm();
        table.bind(form);

        crudOperations.create(acceptorHttpAddress(SRV_UPDATE, ACCP_HTTP_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, ACCP_HTTP_CREATE);
                    formFragment.text(HTTP_LISTENER, DEFAULT);
                });
    }

    @Test
    void acceptorHttpTryCreate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_HTTP_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorHttpTable();
        FormFragment form = page.getAcceptorHttpForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, ACCP_HTTP_CREATE, HTTP_LISTENER);
    }

    @Test
    void acceptorHttpUpdate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_HTTP_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorHttpTable();
        FormFragment form = page.getAcceptorHttpForm();
        table.bind(form);
        table.select(ACCP_HTTP_UPDATE);
        crudOperations.update(acceptorHttpAddress(SRV_UPDATE, ACCP_HTTP_UPDATE), form, UPGRADE_LEGACY, false);
    }

    @Test
    void acceptorHttpTryUpdate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_HTTP_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorHttpTable();
        FormFragment form = page.getAcceptorHttpForm();
        table.bind(form);
        table.select(ACCP_HTTP_UPDATE);
        crudOperations.updateWithError(form, f -> f.clear(HTTP_LISTENER), HTTP_LISTENER);
    }

    @Test
    void acceptorHttpRemove() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_HTTP_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorHttpTable();
        FormFragment form = page.getAcceptorHttpForm();
        table.bind(form);

        crudOperations.delete(acceptorHttpAddress(SRV_UPDATE, ACCP_HTTP_DELETE), table, ACCP_HTTP_DELETE);
    }

}
