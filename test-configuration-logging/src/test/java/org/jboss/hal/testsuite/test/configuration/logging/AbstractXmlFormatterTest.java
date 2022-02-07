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
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.LoggingConfigurationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.NAMESPACE_URI;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.XmlFormatter;

public abstract class AbstractXmlFormatterTest {

    @Inject CrudOperations crud;
    TableFragment table;
    FormFragment form;

    protected abstract LoggingConfigurationPage getPage();

    protected abstract Address xmlFormatterAddress(String name);

    protected abstract void navigateToPage();

    @BeforeEach
    void prepare() {
        navigateToPage();
        table = getPage().getXmlFormatterTable();
        form = getPage().getXmlFormatterForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(xmlFormatterAddress(XmlFormatter.XML_FORMATTER_CREATE), table, XmlFormatter.XML_FORMATTER_CREATE);
    }

    @Test
    void update() throws Exception {
        table.select(XmlFormatter.XML_FORMATTER_UPDATE);
        crud.update(xmlFormatterAddress(XmlFormatter.XML_FORMATTER_UPDATE), form, NAMESPACE_URI, Random.name());
    }

    @Test
    void reset() throws Exception {
        table.select(XmlFormatter.XML_FORMATTER_RESET);
        crud.reset(xmlFormatterAddress(XmlFormatter.XML_FORMATTER_RESET), form);
    }

    @Test
    void delete() throws Exception {
        crud.delete(xmlFormatterAddress(XmlFormatter.XML_FORMATTER_DELETE), table, XmlFormatter.XML_FORMATTER_DELETE);
    }
}
