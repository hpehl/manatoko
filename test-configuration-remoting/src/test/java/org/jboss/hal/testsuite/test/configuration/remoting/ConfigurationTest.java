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
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.RemotingPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.AUTH_REALM;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.BUFFER_REGION_SIZE;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.MAX_INBOUND_CHANNELS;
import static org.jboss.hal.testsuite.fixtures.RemotingFixtures.SUBSYSTEM_ADDRESS;

@Manatoko
@Testcontainers
class ConfigurationTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, STANDALONE);

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page RemotingPage page;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary("remoting-configuration-item");
    }

    @Test
    void updateAttributes() throws Exception {
        page.getConfigurationTabs().select("remoting-configuration-attributes-tab");
        FormFragment form = page.getConfigurationAttributesForm();
        crud.update(SUBSYSTEM_ADDRESS, form, BUFFER_REGION_SIZE, Random.number());
    }

    @Test
    void updateSecurity() throws Exception {
        page.getConfigurationTabs().select("remoting-configuration-security-tab");
        FormFragment form = page.getConfigurationSecurityForm();
        crud.update(SUBSYSTEM_ADDRESS, form, AUTH_REALM, Random.name());
    }

    @Test
    void updateChannels() throws Exception {
        page.getConfigurationTabs().select("remoting-configuration-channels-tab");
        FormFragment form = page.getConfigurationChannelsForm();
        crud.update(SUBSYSTEM_ADDRESS, form, MAX_INBOUND_CHANNELS, Random.number());
    }
}
