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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.ElementNotInteractableException;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.jboss.hal.dmr.ModelDescriptionConstants.LEVEL;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.CATEGORY;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.Category;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractCategoryTest {

    static final String HAL_1469_FAIL_MESSAGE = "Fails probably due to https://issues.jboss.org/browse/HAL-1469";

    @Inject CrudOperations crud;
    TableFragment table;
    FormFragment form;

    protected abstract Address categoryAddress(String name);

    protected abstract TableFragment getCategoryTable();

    protected abstract FormFragment getCategoryForm();

    protected abstract void navigateToPage();

    @BeforeEach
    void prepare() {
        navigateToPage();
        table = getCategoryTable();
        form = getCategoryForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(categoryAddress(Category.CATEGORY_CREATE), table, Category.CATEGORY_CREATE);
    }

    @Test
    void read() {
        table.select(Category.CATEGORY_READ);
        assertEquals(HAL_1469_FAIL_MESSAGE, Category.CATEGORY_READ, form.value(CATEGORY));
    }

    @Test
    void update() throws Exception {
        table.select(Category.CATEGORY_UPDATE);
        try {
            crud.update(categoryAddress(Category.CATEGORY_UPDATE), form,
                    f -> f.select(LEVEL, "CONFIG"),
                    resourceVerifier -> resourceVerifier.verifyAttribute(LEVEL, "CONFIG"));
        } catch (ElementNotInteractableException e) {
            fail(HAL_1469_FAIL_MESSAGE + e.getMessage());
        }
    }

    @Test
    void reset() throws Exception {
        table.select(Category.CATEGORY_UPDATE);
        try {
            crud.reset(categoryAddress(Category.CATEGORY_UPDATE), form);
        } catch (ElementNotInteractableException e) {
            fail(HAL_1469_FAIL_MESSAGE + e.getMessage());
        }
    }

    @Test
    void delete() throws Exception {
        crud.delete(categoryAddress(Category.CATEGORY_DELETE), table, Category.CATEGORY_DELETE);
    }
}
