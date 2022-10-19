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
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.ElytronOtherSettingsPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PATH;
import static org.jboss.hal.dmr.ModelDescriptionConstants.RELATIVE_TO;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.PathsFixtures.JBOSS_SERVER_DATA_DIR;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.DEFAULT_ALIAS;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SECRET_KEY_CREDENTIAL_STORE_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SECRET_KEY_CREDENTIAL_STORE_DELETE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SECRET_KEY_CREDENTIAL_STORE_READ;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SECRET_KEY_CREDENTIAL_STORE_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.secretKeyCredentialStoreAddress;

@Manatoko
@Testcontainers
class SecretKeyCredentialStoreTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        Operations operations = new Operations(wildFly.managementClient());
        operations.add(secretKeyCredentialStoreAddress(SECRET_KEY_CREDENTIAL_STORE_READ),
                Values.of(PATH, Random.name()).and(RELATIVE_TO, JBOSS_SERVER_DATA_DIR));
        operations.add(secretKeyCredentialStoreAddress(SECRET_KEY_CREDENTIAL_STORE_UPDATE),
                Values.of(PATH, Random.name()).and(RELATIVE_TO, JBOSS_SERVER_DATA_DIR));
        operations.add(secretKeyCredentialStoreAddress(SECRET_KEY_CREDENTIAL_STORE_DELETE),
                Values.of(PATH, Random.name()).and(RELATIVE_TO, JBOSS_SERVER_DATA_DIR));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page ElytronOtherSettingsPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary(Ids.ELYTRON_STORE_ITEM, "elytron-secret-key-credential-store-item");
        table = page.getSecretKeyCredentialStoreTable();
        form = page.getSecretKeyCredentialStoreForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(secretKeyCredentialStoreAddress(SECRET_KEY_CREDENTIAL_STORE_CREATE), table, form -> {
            form.text(NAME, SECRET_KEY_CREDENTIAL_STORE_CREATE);
            form.text(PATH, Random.name());
            form.text(RELATIVE_TO, JBOSS_SERVER_DATA_DIR);
        });
    }

    @Test
    void reset() throws Exception {
        table.select(SECRET_KEY_CREDENTIAL_STORE_READ);
        crud.reset(secretKeyCredentialStoreAddress(SECRET_KEY_CREDENTIAL_STORE_READ), form);
    }

    @Test
    void update() throws Exception {
        table.select(SECRET_KEY_CREDENTIAL_STORE_UPDATE);
        crud.update(secretKeyCredentialStoreAddress(SECRET_KEY_CREDENTIAL_STORE_UPDATE), form, DEFAULT_ALIAS, Random.name());
    }

    @Test
    void delete() throws Exception {
        crud.delete(secretKeyCredentialStoreAddress(SECRET_KEY_CREDENTIAL_STORE_DELETE), table,
                SECRET_KEY_CREDENTIAL_STORE_DELETE);
    }
}
