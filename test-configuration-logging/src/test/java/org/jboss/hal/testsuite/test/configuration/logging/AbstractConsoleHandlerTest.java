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
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.LoggingConfigurationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.jboss.hal.dmr.ModelDescriptionConstants.LEVEL;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.ConsoleHandler;

public abstract class AbstractConsoleHandlerTest {

    @Inject CrudOperations crud;
    TableFragment table;
    FormFragment form;

    protected abstract LoggingConfigurationPage getPage();

    protected abstract Address consoleHandlerAddress(String name);

    protected abstract TableFragment getHandlerTable();

    protected abstract FormFragment getHandlerForm();

    protected abstract void navigateToPage();

    @BeforeEach
    void prepare() {
        navigateToPage();
        table = getPage().getConsoleHandlerTable();
        form = getPage().getConsoleHandlerForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(consoleHandlerAddress(ConsoleHandler.CONSOLE_HANDLER_CREATE), table,
                ConsoleHandler.CONSOLE_HANDLER_CREATE);
    }

    @Test
    void update() throws Exception {
        table.select(ConsoleHandler.CONSOLE_HANDLER_UPDATE);
        crud.update(consoleHandlerAddress(ConsoleHandler.CONSOLE_HANDLER_UPDATE), form,
                f -> f.select(LEVEL, "CONFIG"),
                resourceVerifier -> resourceVerifier.verifyAttribute(LEVEL, "CONFIG"));
    }

    @Test
    void reset() throws Exception {
        table.select(ConsoleHandler.CONSOLE_HANDLER_UPDATE);
        crud.reset(consoleHandlerAddress(ConsoleHandler.CONSOLE_HANDLER_UPDATE), form);
    }

    @Test
    void delete() throws Exception {
        crud.delete(consoleHandlerAddress(ConsoleHandler.CONSOLE_HANDLER_DELETE), table,
                ConsoleHandler.CONSOLE_HANDLER_DELETE);
    }
}
