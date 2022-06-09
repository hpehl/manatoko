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
package org.jboss.hal.testsuite.test.configuration.security;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddCredentialStore;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.WizardFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.page.configuration.ElytronOtherSettingsPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CREDENTIAL_STORE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.resources.Ids.ELYTRON_OTHER_ITEM;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CREDENTIAL_STORE_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.DEFAULT_RESOLVER;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SECRET_KEY;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.expressionEncryptionAddress;

@Manatoko
@Testcontainers
class ExpressionEncryptionAddTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, STANDALONE);
    static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        client.apply(new AddCredentialStore(CREDENTIAL_STORE_CREATE));
    }

    @Inject Console console;
    @Page ElytronOtherSettingsPage page;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary(ELYTRON_OTHER_ITEM, Ids.ELYTRON_EXPRESSION);
    }

    @Test
    void add() throws Exception {
        page.getExpressionEmptyState().mainAction();
        WizardFragment wizard = console.wizard();
        FormFragment form = wizard.getForm(Ids.ELYTRON_EXPRESSION + "-add-form");
        form.text(DEFAULT_RESOLVER, Random.name());
        wizard.next();
        form = wizard.getForm(Ids.ELYTRON_EXPRESSION + "-resolvers-add-form");
        form.text(CREDENTIAL_STORE, CREDENTIAL_STORE_CREATE);
        form.text(NAME, Random.name());
        form.text(SECRET_KEY, Random.name());
        wizard.finish();
        new ResourceVerifier(expressionEncryptionAddress(), client).verifyExists();
    }
}
