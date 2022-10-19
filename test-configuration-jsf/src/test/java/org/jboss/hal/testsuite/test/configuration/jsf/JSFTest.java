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
package org.jboss.hal.testsuite.test.configuration.jsf;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.JSFFixtures;
import org.jboss.hal.testsuite.fragment.finder.FinderPath;
import org.jboss.hal.testsuite.page.configuration.JSFPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;

@Manatoko
@Testcontainers
class JSFTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @Container static Browser browser = new Browser();
    private static Operations operations;

    @BeforeAll
    static void setupModel() {
        operations = new Operations(wildFly.managementClient());
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page JSFPage page;

    @Test
    public void view() {
        console.finder(NameTokens.CONFIGURATION, new FinderPath().append(Ids.CONFIGURATION, Ids.asId(Names.SUBSYSTEMS)))
                .column(Ids.CONFIGURATION_SUBSYSTEM)
                .selectItem(JSFFixtures.JSF)
                .view();
        console.verify(page.getPlaceRequest());
    }

    @Test
    public void updateDefaultJSFImplSlot() throws Exception {
        page.navigate();
        crudOperations.update(JSFFixtures.JSF_ADDRESS, page.getDataForm(), JSFFixtures.DEFAULT_JSF_IMPL_SLOT);
    }

    @Test
    public void toggleDisallowDoctypeDecl() throws Exception {
        page.navigate();
        boolean disallowDoctypeDecl = operations.readAttribute(JSFFixtures.JSF_ADDRESS, JSFFixtures.DISALLOW_DOCTYPE_DECL)
                .booleanValue(false);
        crudOperations.update(JSFFixtures.JSF_ADDRESS, page.getDataForm(), JSFFixtures.DISALLOW_DOCTYPE_DECL,
                !disallowDoctypeDecl);
    }
}
