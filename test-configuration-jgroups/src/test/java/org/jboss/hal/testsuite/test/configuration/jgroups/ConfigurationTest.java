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
package org.jboss.hal.testsuite.test.configuration.jgroups;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.JGroupsPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.EE;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.DEFAULT_CHANNEL;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.SUBSYSTEM_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.JGroupsFixtures.TCP;

@Manatoko
@Testcontainers
class ConfigurationTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.writeAttribute(SUBSYSTEM_ADDRESS, DEFAULT_CHANNEL, EE);
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page JGroupsPage page;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.JGROUPS_ITEM);
        form = page.getConfigurationForm();
    }

    @Test
    void updateDefaultChannel() throws Exception {
        crud.update(SUBSYSTEM_ADDRESS, form, DEFAULT_CHANNEL, TCP);
    }
}
