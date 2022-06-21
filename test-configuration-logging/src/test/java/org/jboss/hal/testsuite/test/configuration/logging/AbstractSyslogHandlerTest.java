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
package org.jboss.hal.testsuite.test.configuration.logging;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.LoggingConfigurationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.jboss.hal.dmr.ModelDescriptionConstants.LEVEL;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.NAMED_FORMATTER;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.SyslogHandler;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.PatternFormatter.PATTERN_FORMATTER_REF;

public abstract class AbstractSyslogHandlerTest {

    @Inject Console console;
    @Inject CrudOperations crud;
    TableFragment table;
    FormFragment form;

    protected abstract LoggingConfigurationPage getPage();

    protected abstract Address syslogHandlerAddress(String name);

    protected abstract TableFragment getHandlerTable();

    protected abstract FormFragment getHandlerForm();

    protected abstract void navigateToPage();

    @BeforeEach
    void prepare() {
        navigateToPage();
        table = getPage().getSyslogHandlerTable();
        form = getPage().getSyslogHandlerForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(syslogHandlerAddress(SyslogHandler.SYSLOG_HANDLER_CREATE), table,
                SyslogHandler.SYSLOG_HANDLER_CREATE);
    }

    @Test
    void update() throws Exception {
        table.select(SyslogHandler.SYSLOG_HANDLER_UPDATE);
        crud.update(syslogHandlerAddress(SyslogHandler.SYSLOG_HANDLER_UPDATE), form,
                f -> f.select(LEVEL, "CONFIG"),
                resourceVerifier -> resourceVerifier.verifyAttribute(LEVEL, "CONFIG"));
    }

    @Test
    void updateNamedFormatter() throws Exception {
        table.select(SyslogHandler.SYSLOG_HANDLER_UPDATE);
        crud.update(syslogHandlerAddress(SyslogHandler.SYSLOG_HANDLER_UPDATE), form,
                f -> f.text(NAMED_FORMATTER, PATTERN_FORMATTER_REF),
                resourceVerifier -> resourceVerifier.verifyAttribute(NAMED_FORMATTER, PATTERN_FORMATTER_REF));
    }

    @Test
    void updateNonExistingNamedFormatter() throws Exception {
        table.select(SyslogHandler.SYSLOG_HANDLER_UPDATE);
        form.edit();
        form.text(NAMED_FORMATTER, "foo");
        form.save();
        console.verifyError();
    }

    @Test
    void reset() throws Exception {
        table.select(SyslogHandler.SYSLOG_HANDLER_UPDATE);
        crud.reset(syslogHandlerAddress(SyslogHandler.SYSLOG_HANDLER_UPDATE), form);
    }

    @Test
    void delete() throws Exception {
        crud.delete(syslogHandlerAddress(SyslogHandler.SYSLOG_HANDLER_DELETE), table,
                SyslogHandler.SYSLOG_HANDLER_DELETE);
    }
}
