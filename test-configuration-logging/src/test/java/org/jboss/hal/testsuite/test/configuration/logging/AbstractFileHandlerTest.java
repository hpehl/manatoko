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
import org.jboss.hal.testsuite.fragment.FileInputFragment;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.LoggingConfigurationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.jboss.arquillian.graphene.Graphene.createPageFragment;
import static org.jboss.hal.dmr.ModelDescriptionConstants.LEVEL;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.FileHandler;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.PATH_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractFileHandlerTest {

    @Inject CrudOperations crud;
    TableFragment table;
    FormFragment form;

    protected abstract LoggingConfigurationPage getPage();

    protected abstract Address fileHandlerAddress(String name);

    protected abstract TableFragment getHandlerTable();

    protected abstract FormFragment getHandlerForm();

    protected abstract void navigateToPage();

    @BeforeEach
    void prepare() {
        navigateToPage();
        table = getPage().getFileHandlerTable();
        form = getPage().getFileHandlerForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(fileHandlerAddress(FileHandler.FILE_HANDLER_CREATE), table, form -> {
            form.text(NAME, FileHandler.FILE_HANDLER_CREATE);
            FileInputFragment fileInput = createPageFragment(FileInputFragment.class,
                    getPage().getNewFileInputElement());
            fileInput.setPath(PATH_VALUE);
        });
    }

    @Test
    void read() {
        table.select(FileHandler.FILE_HANDLER_READ);
        FileInputFragment fileInput = createPageFragment(FileInputFragment.class, getPage().getReadFileInputElement());
        assertEquals(PATH_VALUE, fileInput.getPath());
    }

    @Test
    void update() throws Exception {
        table.select(FileHandler.FILE_HANDLER_UPDATE);
        crud.update(fileHandlerAddress(FileHandler.FILE_HANDLER_UPDATE), form,
                f -> f.select(LEVEL, "CONFIG"),
                resourceVerifier -> resourceVerifier.verifyAttribute(LEVEL, "CONFIG"));
    }

    @Test
    void reset() throws Exception {
        table.select(FileHandler.FILE_HANDLER_UPDATE);
        crud.reset(fileHandlerAddress(FileHandler.FILE_HANDLER_UPDATE), form);
    }

    @Test
    void delete() throws Exception {
        crud.delete(fileHandlerAddress(FileHandler.FILE_HANDLER_DELETE), table, FileHandler.FILE_HANDLER_DELETE);
    }
}
