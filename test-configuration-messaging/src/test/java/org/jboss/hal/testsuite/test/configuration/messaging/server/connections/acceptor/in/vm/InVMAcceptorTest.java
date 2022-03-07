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
package org.jboss.hal.testsuite.test.configuration.messaging.server.connections.acceptor.in.vm;

import org.jboss.hal.resources.Ids;
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
import static org.jboss.hal.resources.Ids.MESSAGING_IN_VM_ACCEPTOR;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_INVM_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_INVM_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_INVM_TRY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ACCP_INVM_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SERVER_ID;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.acceptorInVMAddress;

@Manatoko
@Testcontainers
class InVMAcceptorTest extends AbstractServerConnectionsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        operations.add(acceptorInVMAddress(SRV_UPDATE, ACCP_INVM_UPDATE), Values.of(SERVER_ID, 11)).assertSuccess();
        operations.add(acceptorInVMAddress(SRV_UPDATE, ACCP_INVM_TRY_UPDATE), Values.of(SERVER_ID, 12)).assertSuccess();
        operations.add(acceptorInVMAddress(SRV_UPDATE, ACCP_INVM_DELETE), Values.of(SERVER_ID, 22)).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void acceptorInVMCreate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_IN_VM_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorInVMTable();
        FormFragment form = page.getAcceptorInVMForm();
        table.bind(form);

        crudOperations.create(acceptorInVMAddress(SRV_UPDATE, ACCP_INVM_CREATE), table,
                formFragment -> {
                    formFragment.text(NAME, ACCP_INVM_CREATE);
                    formFragment.number(SERVER_ID, 123);
                });
    }

    @Test
    void acceptorInVMTryCreate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_IN_VM_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorInVMTable();
        FormFragment form = page.getAcceptorInVMForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, ACCP_INVM_CREATE, SERVER_ID);
    }

    @Test
    void acceptorInVMUpdate() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_IN_VM_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorInVMTable();
        FormFragment form = page.getAcceptorInVMForm();
        table.bind(form);
        table.select(ACCP_INVM_UPDATE);
        crudOperations.update(acceptorInVMAddress(SRV_UPDATE, ACCP_INVM_UPDATE), form, SERVER_ID, 89);
    }

    @Test
    void acceptorInVMTryUpdate() {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_IN_VM_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorInVMTable();
        FormFragment form = page.getAcceptorInVMForm();
        table.bind(form);
        table.select(ACCP_INVM_TRY_UPDATE);
        crudOperations.updateWithError(form, f -> f.clear(SERVER_ID), SERVER_ID);
    }

    @Test
    void acceptorInVMRemove() throws Exception {
        console.verticalNavigation()
                .selectSecondary(Ids.build(MESSAGING_ACCEPTOR, GROUP, ITEM),
                        Ids.build(MESSAGING_IN_VM_ACCEPTOR, ITEM));
        TableFragment table = page.getAcceptorInVMTable();
        FormFragment form = page.getAcceptorInVMForm();
        table.bind(form);

        crudOperations.delete(acceptorInVMAddress(SRV_UPDATE, ACCP_INVM_DELETE), table, ACCP_INVM_DELETE);
    }

}
