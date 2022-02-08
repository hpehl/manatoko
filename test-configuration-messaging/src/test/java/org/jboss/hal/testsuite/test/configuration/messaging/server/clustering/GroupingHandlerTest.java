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
package org.jboss.hal.testsuite.test.configuration.messaging.server.clustering;

import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.REMOTE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TYPE;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.resources.Ids.MESSAGING_GROUPING_HANDLER;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.GH_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.GH_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.GH_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.GROUPING_HANDLER_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.groupingHandlerAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class GroupingHandlerTest extends AbstractClusteringTest {

    private static final Values GH_PARAMS = Values.of(GROUPING_HANDLER_ADDRESS, Random.name())
            .and(TYPE, REMOTE);

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        createServer(operations, SRV_UPDATE);
        operations.add(groupingHandlerAddress(SRV_UPDATE, GH_UPDATE), GH_PARAMS).assertSuccess();
        operations.add(groupingHandlerAddress(SRV_UPDATE, GH_DELETE), GH_PARAMS).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void groupingHandlerCreate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_GROUPING_HANDLER, ITEM));
        TableFragment table = page.getGroupingHandlerTable();
        FormFragment form = page.getGroupingHandlerForm();
        table.bind(form);

        crudOperations.create(groupingHandlerAddress(SRV_UPDATE, GH_CREATE), table, f -> {
            f.text(NAME, GH_CREATE);
            f.text(GROUPING_HANDLER_ADDRESS, Random.name());
            f.select(TYPE, REMOTE.toUpperCase());
        });
    }

    @Test
    void groupingHandlerTryCreate() {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_GROUPING_HANDLER, ITEM));
        TableFragment table = page.getGroupingHandlerTable();
        FormFragment form = page.getGroupingHandlerForm();
        table.bind(form);

        crudOperations.createWithErrorAndCancelDialog(table, f -> f.text(NAME, GH_CREATE), GROUPING_HANDLER_ADDRESS);
    }

    @Test
    void groupingHandlerUpdate() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_GROUPING_HANDLER, ITEM));
        TableFragment table = page.getGroupingHandlerTable();
        FormFragment form = page.getGroupingHandlerForm();
        table.bind(form);
        table.select(GH_UPDATE);
        crudOperations.update(groupingHandlerAddress(SRV_UPDATE, GH_UPDATE), form, GROUPING_HANDLER_ADDRESS,
                Random.name());
    }

    @Test
    void groupingHandlerRemove() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.build(MESSAGING_GROUPING_HANDLER, ITEM));
        TableFragment table = page.getGroupingHandlerTable();
        FormFragment form = page.getGroupingHandlerForm();
        table.bind(form);

        crudOperations.delete(groupingHandlerAddress(SRV_UPDATE, GH_DELETE), table, GH_DELETE);
    }

}
