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
import org.jboss.hal.testsuite.page.configuration.BufferCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.BUFFERS_PER_REGION;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.BUFFER_CACHE_CREATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.BUFFER_CACHE_DELETE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.BUFFER_CACHE_UPDATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.BUFFER_SIZE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.MAX_REGIONS;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.bufferCacheAddress;

@Manatoko
@Testcontainers
class BufferCacheTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        Operations operations = new Operations(wildFly.managementClient());
        operations.add(bufferCacheAddress(BUFFER_CACHE_UPDATE));
        operations.add(bufferCacheAddress(BUFFER_CACHE_DELETE));
    }

    @Page BufferCachePage page;
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
        crud.create(bufferCacheAddress(BUFFER_CACHE_CREATE), table, form -> form.text(NAME, BUFFER_CACHE_CREATE));
    }

    @Test
    void update() throws Exception {
        table.select(BUFFER_CACHE_UPDATE);
        crud.update(bufferCacheAddress(BUFFER_CACHE_UPDATE), form, f -> {
            f.number(BUFFER_SIZE, 10);
            f.number(BUFFERS_PER_REGION, 11);
            f.number(MAX_REGIONS, 12);
        }, resourceVerifier -> {
            resourceVerifier.verifyAttribute(BUFFER_SIZE, 10);
            resourceVerifier.verifyAttribute(BUFFERS_PER_REGION, 11);
            resourceVerifier.verifyAttribute(MAX_REGIONS, 12);
        });
    }

    @Test
    void delete() throws Exception {
        crud.delete(bufferCacheAddress(BUFFER_CACHE_DELETE), table, BUFFER_CACHE_DELETE);
    }
}
