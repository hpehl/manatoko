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
package org.jboss.hal.testsuite.test.configuration.logging;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.LoggingConfigurationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.COLOR_MAP;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.COLOR_MAP_VALUE;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.PatternFormatter;

public abstract class AbstractPatternFormatterTest {

    @Inject CrudOperations crud;
    TableFragment table;
    FormFragment form;

    protected abstract LoggingConfigurationPage getPage();

    protected abstract Address patternFormatterAddress(String name);

    protected abstract void navigateToPage();

    @BeforeEach
    void prepare() {
        navigateToPage();
        table = getPage().getPatternFormatterTable();
        form = getPage().getPatternFormatterForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(patternFormatterAddress(
                PatternFormatter.PATTERN_FORMATTER_CREATE), table, PatternFormatter.PATTERN_FORMATTER_CREATE);
    }

    @Test
    void update() throws Exception {
        table.select(PatternFormatter.PATTERN_FORMATTER_UPDATE);
        crud.update(patternFormatterAddress(PatternFormatter.PATTERN_FORMATTER_UPDATE), form, COLOR_MAP,
                COLOR_MAP_VALUE);
    }

    @Test
    void reset() throws Exception {
        table.select(PatternFormatter.PATTERN_FORMATTER_UPDATE);
        crud.reset(patternFormatterAddress(PatternFormatter.PATTERN_FORMATTER_UPDATE), form);
    }

    @Test
    void delete() throws Exception {
        crud.delete(patternFormatterAddress(
                PatternFormatter.PATTERN_FORMATTER_DELETE), table, PatternFormatter.PATTERN_FORMATTER_DELETE);
    }
}
