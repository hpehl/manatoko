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
package org.jboss.hal.testsuite.test.configuration.messaging.server.destinations;

import java.io.IOException;

import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddMessagingServer;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.PATTERN;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ROLE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER;
import static org.jboss.hal.resources.Ids.ITEM;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONSUME;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.MESSAGING_SECURITY_SETTING_ROLE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.ROLE_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SECURITY_SETTINGS_CREATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SECURITY_SETTINGS_DELETE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SECURITY_SETTINGS_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.securitySettingAddress;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.securitySettingRoleAddress;

@Manatoko
@Testcontainers
class SecuritySettingTest extends AbstractServerDestinationsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddMessagingServer(SRV_UPDATE));
        Operations operations = new Operations(client);
        createSecuritySettingsWithRoles(operations, SECURITY_SETTINGS_DELETE);
        createSecuritySettingsWithRoles(operations, SECURITY_SETTINGS_UPDATE);
    }

    private static void createSecuritySettingsWithRoles(Operations operations, String securitySetting)
            throws IOException {
        Batch securitySettingBatch = new Batch();
        securitySettingBatch.add(
                securitySettingAddress(org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE, securitySetting));
        securitySettingBatch.add(
                securitySettingRoleAddress(org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE,
                        securitySetting, ROLE_CREATE));
        operations.batch(securitySettingBatch).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate(SERVER, SRV_UPDATE);
    }

    @Test
    void create() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_SECURITY_SETTING_ROLE + ID_DELIMITER + ITEM);
        TableFragment table = page.getSecuritySettingTable();
        FormFragment form = page.getSecuritySettingForm();
        table.bind(form);

        crudOperations.create(securitySettingAddress(SRV_UPDATE, SECURITY_SETTINGS_CREATE), table,
                formFragment -> {
                    formFragment.text(PATTERN, SECURITY_SETTINGS_CREATE);
                    formFragment.text(ROLE, Random.name());
                });
    }

    @Test
    void editConsume() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_SECURITY_SETTING_ROLE + ID_DELIMITER + ITEM);
        TableFragment table = page.getSecuritySettingTable();
        FormFragment form = page.getSecuritySettingForm();
        table.bind(form);

        table.select(SECURITY_SETTINGS_UPDATE);
        crudOperations.update(securitySettingRoleAddress(SRV_UPDATE, SECURITY_SETTINGS_UPDATE, ROLE_CREATE), form, CONSUME,
                true);
    }

    @Test
    void remove() throws Exception {
        console.verticalNavigation().selectPrimary(MESSAGING_SECURITY_SETTING_ROLE + ID_DELIMITER + ITEM);
        TableFragment table = page.getSecuritySettingTable();
        FormFragment form = page.getSecuritySettingForm();
        table.bind(form);

        crudOperations.delete(securitySettingAddress(SRV_UPDATE, SECURITY_SETTINGS_DELETE), table,
                SECURITY_SETTINGS_DELETE);
    }
}
