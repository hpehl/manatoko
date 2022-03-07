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
package org.jboss.hal.testsuite.test.configuration.messaging.server.connections.acceptor.generic;

import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddMessagingServer;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.test.configuration.messaging.server.connections.AbstractServerConnectionsTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.GROUP;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_ACCEPTOR;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_GEN_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_GEN_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_GEN_TRY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_GEN_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.FACTORY_CLASS;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.acceptorGenericAddress;

@Manatoko
@Testcontainers
class GenericAcceptorTest extends AbstractServerConnectionsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(acceptorGenericAddress(SRV_UPDATE, ACCP_GEN_UPDATE), Values.of(FACTORY_CLASS, Random.name()))
                .assertSuccess();
        operations.add(acceptorGenericAddress(SRV_UPDATE, ACCP_GEN_TRY_UPDATE), Values.of(FACTORY_CLASS, Random.name()))
                .assertSuccess();
        operations.add(acceptorGenericAddress(SRV_UPDATE, ACCP_GEN_DELETE), Values.of(FACTORY_CLASS, Random.name()))
                .assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void acceptorGenericCreate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM), Ids.build(MESSAGING_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorGenericTable();
        FormFragment form = page.getAcceptorGenericForm();
        table.bind(form);

        crudOperations.create(acceptorGenericAddress(SRV_UPDATE, ACCP_GEN_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, ACCP_GEN_CREATE);
                    formFragment.text(FACTORY_CLASS, Random.name());
                });
    }

    @Test
    void acceptorGenericTryCreate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM), Ids.build(MESSAGING_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorGenericTable();
        FormFragment form = page.getAcceptorGenericForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, ACCP_GEN_CREATE, FACTORY_CLASS);
    }

    @Test
    void acceptorGenericUpdate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM), Ids.build(MESSAGING_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorGenericTable();
        FormFragment form = page.getAcceptorGenericForm();
        table.bind(form);
        table.select(ACCP_GEN_UPDATE);
        crudOperations.update(acceptorGenericAddress(SRV_UPDATE, ACCP_GEN_UPDATE), form, FACTORY_CLASS);
    }

    @Test
    void acceptorGenericTryUpdate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM), Ids.build(MESSAGING_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorGenericTable();
        FormFragment form = page.getAcceptorGenericForm();
        table.bind(form);
        table.select(ACCP_GEN_TRY_UPDATE);
        crudOperations.updateWithError(form, f -> f.clear(FACTORY_CLASS), FACTORY_CLASS);
    }

    @Test
    void acceptorGenericRemove() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM), Ids.build(MESSAGING_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorGenericTable();
        FormFragment form = page.getAcceptorGenericForm();
        table.bind(form);

        crudOperations.delete(acceptorGenericAddress(SRV_UPDATE, ACCP_GEN_DELETE), table, ACCP_GEN_DELETE);
    }
}
