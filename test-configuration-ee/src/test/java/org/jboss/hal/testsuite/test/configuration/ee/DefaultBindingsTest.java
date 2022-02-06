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
package org.jboss.hal.testsuite.test.configuration.ee;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.EEPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.jboss.hal.testsuite.test.Manatoko;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CONTEXT_SERVICE;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.DEFAULT_BINDINGS_ADDRESS;

@Manatoko
@Testcontainers
public class DefaultBindingsTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page EEPage page;
    FormFragment form;

    @BeforeEach
    public void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.EE_DEFAULT_BINDINGS_ITEM);
        form = page.getDefaultBindingsForm();
    }

    @Test
    public void update() throws Exception {
        crud.update(DEFAULT_BINDINGS_ADDRESS, form, CONTEXT_SERVICE);
    }

    @Test
    public void reset() throws Exception {
        crud.reset(DEFAULT_BINDINGS_ADDRESS, form);
    }
}
