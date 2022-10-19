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
package org.jboss.hal.testsuite.test.configuration.security;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.model.AvailablePortFinder;
import org.jboss.hal.testsuite.page.configuration.ElytronOtherSettingsPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PORT;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.HOST_NAME;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.LOCALHOST;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SERVER_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SYSLOG_AUDIT_LOG_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SYSLOG_AUDIT_LOG_DELETE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SYSLOG_AUDIT_LOG_RESET;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SYSLOG_AUDIT_LOG_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.syslogAuditLogAddress;

@Manatoko
@Testcontainers
class SyslogAuditTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        Values values = Values.of(HOST_NAME, Random.name())
                .and(PORT, AvailablePortFinder.getNextAvailableNonPrivilegedPort())
                .and("reconnect-attempts", 1) // TODO replace with MDC constant from 3.6.1.Final
                .and(SERVER_ADDRESS, LOCALHOST);
        Operations operations = new Operations(wildFly.managementClient());
        operations.add(syslogAuditLogAddress(SYSLOG_AUDIT_LOG_RESET), values);
        operations.add(syslogAuditLogAddress(SYSLOG_AUDIT_LOG_UPDATE), values);
        operations.add(syslogAuditLogAddress(SYSLOG_AUDIT_LOG_DELETE), values);
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page ElytronOtherSettingsPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary(Ids.ELYTRON_LOGS_ITEM, "elytron-syslog-audit-log-item");
        table = page.getSyslogAuditLogTable();
        form = page.getSyslogAuditLogForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(syslogAuditLogAddress(SYSLOG_AUDIT_LOG_CREATE), table, form -> {
            form.text(NAME, SYSLOG_AUDIT_LOG_CREATE);
            form.text(HOST_NAME, Random.name());
            form.number(PORT, AvailablePortFinder.getNextAvailableNonPrivilegedPort());
            form.text(SERVER_ADDRESS, LOCALHOST);
        });
    }

    @Test
    void reset() throws Exception {
        table.select(SYSLOG_AUDIT_LOG_RESET);
        crud.reset(syslogAuditLogAddress(SYSLOG_AUDIT_LOG_RESET), form,
                resourceVerifier -> resourceVerifier.verifyAttribute("reconnect-attempts", 0));
    }

    @Test
    void update() throws Exception {
        table.select(SYSLOG_AUDIT_LOG_UPDATE);
        crud.update(syslogAuditLogAddress(SYSLOG_AUDIT_LOG_UPDATE), form, HOST_NAME, Random.name());
    }

    @Test
    void delete() throws Exception {
        crud.delete(syslogAuditLogAddress(SYSLOG_AUDIT_LOG_DELETE), table, SYSLOG_AUDIT_LOG_DELETE);
    }
}
