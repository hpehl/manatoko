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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.HeaderBreadcrumbFragment;
import org.jboss.hal.testsuite.page.configuration.CacheContainerPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_UPDATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class CacheContainerConfigurationTest {

    static final String ALIASES = "aliases";
    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(cacheContainerAddress(CC_UPDATE));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page CacheContainerPage page;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate(NAME, CC_UPDATE);
        console.verticalNavigation().selectPrimary(Ids.CACHE_CONTAINER_ITEM);
        form = page.getConfigurationForm();
    }

    @Test
    void view() {
        assertEquals(HeaderBreadcrumbFragment.abbreviate(CC_UPDATE), console.header().breadcrumb().lastValue());
    }

    @Test
    void update() throws Exception {
        String aliases = Random.name();
        crud.update(cacheContainerAddress(CC_UPDATE), form,
                f -> f.list(ALIASES).add(aliases),
                resourceVerifier -> resourceVerifier.verifyListAttributeContainsValue(ALIASES, aliases));
    }

    @Test
    void reset() throws Exception {
        crud.reset(cacheContainerAddress(CC_UPDATE), form);
    }
}
