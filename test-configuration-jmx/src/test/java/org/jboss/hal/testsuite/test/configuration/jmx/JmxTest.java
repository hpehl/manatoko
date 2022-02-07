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
package org.jboss.hal.testsuite.test.configuration.jmx;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.JmxPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.ENABLED;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.JmxFixtures.AUDIT_LOG_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.JmxFixtures.NON_CORE_MBEAN_SENSITIVITY;
import static org.jboss.hal.testsuite.fixtures.JmxFixtures.REMOTING_CONNECTOR_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.JmxFixtures.SUBSYSTEM_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.JmxFixtures.USE_MANAGEMENT_ENDPOINT;

@Manatoko
@Testcontainers
class JmxTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);
    private static Operations operations;

    @BeforeAll
    static void setupModel() {
        operations = new Operations(wildFly.managementClient());
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page JmxPage page;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate();
    }

    // ------------------------------------------------------ configuration

    @Test
    void updateConfiguration() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.JMX_CONFIGURATION_ITEM);
        form = page.getConfigurationForm();
        crud.update(SUBSYSTEM_ADDRESS, form, NON_CORE_MBEAN_SENSITIVITY, true);
    }

    @Test
    void resetConfiguration() throws Exception {
        console.verticalNavigation().selectPrimary(Ids.JMX_CONFIGURATION_ITEM);
        form = page.getConfigurationForm();
        crud.reset(SUBSYSTEM_ADDRESS, form);
    }

    // ------------------------------------------------------ audit log

    @Test
    void createAuditLog() throws Exception {
        if (operations.removeIfExists(AUDIT_LOG_ADDRESS)) {
            console.reload();
        }

        console.verticalNavigation().selectPrimary(Ids.JMX_AUDIT_LOG_ITEM);
        form = page.getAuditForm();
        crud.createSingleton(AUDIT_LOG_ADDRESS, form);
    }

    @Test
    void updateAuditLog() throws Exception {
        if (!operations.exists(AUDIT_LOG_ADDRESS)) {
            operations.add(AUDIT_LOG_ADDRESS);
            console.reload();
        }
        console.verticalNavigation().selectPrimary(Ids.JMX_AUDIT_LOG_ITEM);
        form = page.getAuditForm();
        crud.update(AUDIT_LOG_ADDRESS, form, ENABLED, false);
    }

    @Test
    void resetAuditLog() throws Exception {
        if (!operations.exists(AUDIT_LOG_ADDRESS)) {
            operations.add(AUDIT_LOG_ADDRESS);
            console.reload();
        }
        console.verticalNavigation().selectPrimary(Ids.JMX_AUDIT_LOG_ITEM);
        form = page.getAuditForm();
        crud.reset(AUDIT_LOG_ADDRESS, form);
    }

    @Test
    void deleteAuditLog() throws Exception {
        if (!operations.exists(AUDIT_LOG_ADDRESS)) {
            operations.add(AUDIT_LOG_ADDRESS);
            console.reload();
        }
        console.verticalNavigation().selectPrimary(Ids.JMX_AUDIT_LOG_ITEM);
        form = page.getAuditForm();
        crud.deleteSingleton(AUDIT_LOG_ADDRESS, form);
    }

    // ------------------------------------------------------ remoting connector

    @Test
    void createRemotingConnector() throws Exception {
        if (operations.removeIfExists(REMOTING_CONNECTOR_ADDRESS)) {
            console.reload();
        }

        console.verticalNavigation().selectPrimary(Ids.JMX_REMOTING_CONNECTOR_ITEM);
        form = page.getRemotingConnectorForm();
        crud.createSingleton(REMOTING_CONNECTOR_ADDRESS, form);
    }

    @Test
    void updateRemotingConnector() throws Exception {
        if (!operations.exists(REMOTING_CONNECTOR_ADDRESS)) {
            operations.add(REMOTING_CONNECTOR_ADDRESS);
            console.reload();
        }
        console.verticalNavigation().selectPrimary(Ids.JMX_REMOTING_CONNECTOR_ITEM);
        form = page.getRemotingConnectorForm();
        crud.update(REMOTING_CONNECTOR_ADDRESS, form, USE_MANAGEMENT_ENDPOINT, false);
    }

    @Test
    void resetRemotingConnector() throws Exception {
        if (!operations.exists(REMOTING_CONNECTOR_ADDRESS)) {
            operations.add(REMOTING_CONNECTOR_ADDRESS);
            console.reload();
        }
        console.verticalNavigation().selectPrimary(Ids.JMX_REMOTING_CONNECTOR_ITEM);
        form = page.getRemotingConnectorForm();
        crud.reset(REMOTING_CONNECTOR_ADDRESS, form);
    }

    @Test
    void deleteRemotingConnector() throws Exception {
        if (!operations.exists(REMOTING_CONNECTOR_ADDRESS)) {
            operations.add(REMOTING_CONNECTOR_ADDRESS);
            console.reload();
        }
        console.verticalNavigation().selectPrimary(Ids.JMX_REMOTING_CONNECTOR_ITEM);
        form = page.getRemotingConnectorForm();
        crud.deleteSingleton(REMOTING_CONNECTOR_ADDRESS, form);
    }
}
