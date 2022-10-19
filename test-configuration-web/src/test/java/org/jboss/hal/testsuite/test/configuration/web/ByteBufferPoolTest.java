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
package org.jboss.hal.testsuite.test.configuration.web;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.ByteBufferPoolPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.MAX_POOL_SIZE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.BUFFER_SIZE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.BYTE_BUFFER_POOL_CREATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.BYTE_BUFFER_POOL_DELETE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.BYTE_BUFFER_POOL_UPDATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.THREAD_LOCAL_CACHE_SIZE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.byteBufferPoolAddress;

@Manatoko
@Testcontainers
class ByteBufferPoolTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        Operations operations = new Operations(wildFly.managementClient());
        operations.add(byteBufferPoolAddress(BYTE_BUFFER_POOL_UPDATE));
        operations.add(byteBufferPoolAddress(BYTE_BUFFER_POOL_DELETE));
    }

    @Page ByteBufferPoolPage page;
    @Inject CrudOperations crud;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        table = page.getTable();
        form = page.getForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(byteBufferPoolAddress(BYTE_BUFFER_POOL_CREATE), table, form -> form.text(NAME, BYTE_BUFFER_POOL_CREATE));
    }

    @Test
    void update() throws Exception {
        table.select(BYTE_BUFFER_POOL_UPDATE);
        crud.update(byteBufferPoolAddress(BYTE_BUFFER_POOL_UPDATE), form, f -> {
            f.number(BUFFER_SIZE, 10);
            f.number(MAX_POOL_SIZE, 11);
            f.number(THREAD_LOCAL_CACHE_SIZE, 12);
        }, resourceVerifier -> {
            resourceVerifier.verifyAttribute(BUFFER_SIZE, 10);
            resourceVerifier.verifyAttribute(MAX_POOL_SIZE, 11);
            resourceVerifier.verifyAttribute(THREAD_LOCAL_CACHE_SIZE, 12);
        });
    }

    @Test
    void delete() throws Exception {
        crud.delete(byteBufferPoolAddress(BYTE_BUFFER_POOL_DELETE), table, BYTE_BUFFER_POOL_DELETE);
    }
}
