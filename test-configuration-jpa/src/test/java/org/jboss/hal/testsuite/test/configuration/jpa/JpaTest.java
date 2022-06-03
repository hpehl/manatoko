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
package org.jboss.hal.testsuite.test.configuration.jpa;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.JpaPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.JpaFixtures.DEFAULT_EXTENDED_PERSISTENCE_INHERITANCE;
import static org.jboss.hal.testsuite.fixtures.JpaFixtures.SHALLOW;
import static org.jboss.hal.testsuite.fixtures.JpaFixtures.SUBSYSTEM_ADDRESS;

@Manatoko
@Testcontainers
class JpaTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, STANDALONE);

    @Inject CrudOperations crud;
    @Page JpaPage page;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate();
        form = page.getForm();
    }

    @Test
    void update() throws Exception {
        crud.update(SUBSYSTEM_ADDRESS, form,
                f -> f.select(DEFAULT_EXTENDED_PERSISTENCE_INHERITANCE, SHALLOW),
                resourceVerifier -> resourceVerifier.verifyAttribute(DEFAULT_EXTENDED_PERSISTENCE_INHERITANCE,
                        SHALLOW));
    }

    @Test
    void reset() throws Exception {
        crud.reset(SUBSYSTEM_ADDRESS, form);
    }
}
