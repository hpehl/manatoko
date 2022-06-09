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
package org.jboss.hal.testsuite.test.configuration.socketbinding;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.command.AddLocalSocketBinding;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.SocketBindingPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.commands.socketbindings.AddSocketBinding;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SOCKET_BINDING_REF;
import static org.jboss.hal.testsuite.command.SocketBindingCommand.refName;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.OUTBOUND_LOCAL_CREATE;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.OUTBOUND_LOCAL_DELETE;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.OUTBOUND_LOCAL_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.SOURCE_PORT;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.STANDARD_SOCKETS;
import static org.jboss.hal.testsuite.fixtures.SocketBindingFixtures.outboundLocalAddress;

@Manatoko
@Testcontainers
class OutboundLocalTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(
                new AddSocketBinding.Builder(refName(OUTBOUND_LOCAL_CREATE)).build(),
                new AddLocalSocketBinding(OUTBOUND_LOCAL_UPDATE),
                new AddLocalSocketBinding(OUTBOUND_LOCAL_DELETE));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page SocketBindingPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate(NAME, STANDARD_SOCKETS);
        console.verticalNavigation().selectPrimary(Ids.SOCKET_BINDING_GROUP_OUTBOUND_LOCAL + "-" + Ids.ITEM);

        table = page.getOutboundLocalTable();
        form = page.getOutboundLocalForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(outboundLocalAddress(STANDARD_SOCKETS, OUTBOUND_LOCAL_CREATE), table, form -> {
            form.text(NAME, OUTBOUND_LOCAL_CREATE);
            form.text(SOCKET_BINDING_REF, refName(OUTBOUND_LOCAL_CREATE));
        });
    }

    @Test
    void update() throws Exception {
        table.select(OUTBOUND_LOCAL_UPDATE);
        crud.update(outboundLocalAddress(STANDARD_SOCKETS, OUTBOUND_LOCAL_UPDATE), form, SOURCE_PORT, 1234);
    }

    @Test
    void updateInvalidPort() {
        table.select(OUTBOUND_LOCAL_UPDATE);
        crud.updateWithError(form, SOURCE_PORT, -1);
    }

    @Test
    void reset() throws Exception {
        table.select(OUTBOUND_LOCAL_UPDATE);
        crud.reset(outboundLocalAddress(STANDARD_SOCKETS, OUTBOUND_LOCAL_UPDATE), form);
    }

    @Test
    void delete() throws Exception {
        crud.delete(outboundLocalAddress(STANDARD_SOCKETS, OUTBOUND_LOCAL_DELETE), table, OUTBOUND_LOCAL_DELETE);
    }
}
