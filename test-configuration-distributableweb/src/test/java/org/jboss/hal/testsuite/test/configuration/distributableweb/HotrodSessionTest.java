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
package org.jboss.hal.testsuite.test.configuration.distributableweb;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.DistributableWebPage;
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
import static org.jboss.hal.dmr.ModelDescriptionConstants.REMOTE_CACHE_CONTAINER;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.ATTRIBUTE;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.GRANULARITY;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.HOTROD_SESSION_CREATE;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.HOTROD_SESSION_DELETE;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.HOTROD_SESSION_UPDATE;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.REMOTE_SOCKET_BINDING;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.SESSION;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.hotrodSessionAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.REMOTE_CC_CREATE;
import static org.jboss.hal.testsuite.test.configuration.distributableweb.DistributableWebOperations.addRemoteCacheContainer;
import static org.jboss.hal.testsuite.test.configuration.distributableweb.DistributableWebOperations.addRemoteSocketBinding;

@Manatoko
@Testcontainers
class HotrodSessionTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        addRemoteSocketBinding(client, operations, REMOTE_SOCKET_BINDING);
        addRemoteCacheContainer(client, operations, REMOTE_CC_CREATE, REMOTE_SOCKET_BINDING);
        Values values = Values.of(REMOTE_CACHE_CONTAINER, REMOTE_CC_CREATE).and(GRANULARITY, SESSION);
        operations.add(hotrodSessionAddress(HOTROD_SESSION_UPDATE), values);
        operations.add(hotrodSessionAddress(HOTROD_SESSION_DELETE), values);
    }

    @Page DistributableWebPage page;
    @Inject CrudOperations crud;
    @Inject Console console;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectPrimary("dw-hotrod-session-management-item");
        table = page.getHotrodSessionManagementTable();
        form = page.getHotrodSessionManagementForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(hotrodSessionAddress(HOTROD_SESSION_CREATE), table, f -> {
            f.text(NAME, HOTROD_SESSION_CREATE);
            f.text(REMOTE_CACHE_CONTAINER, REMOTE_CC_CREATE);
        });
    }

    @Test
    void update() throws Exception {
        table.select(HOTROD_SESSION_UPDATE);
        crud.update(hotrodSessionAddress(HOTROD_SESSION_UPDATE), form,
                f -> f.select(GRANULARITY, ATTRIBUTE),
                verifier -> verifier.verifyAttribute(GRANULARITY, ATTRIBUTE));
    }

    @Test
    void reset() throws Exception {
        table.select(HOTROD_SESSION_UPDATE);
        crud.reset(hotrodSessionAddress(HOTROD_SESSION_UPDATE), form);
    }

    @Test
    void delete() throws Exception {
        table.select(HOTROD_SESSION_DELETE);
        crud.delete(hotrodSessionAddress(HOTROD_SESSION_DELETE), table, HOTROD_SESSION_DELETE);
    }
}
