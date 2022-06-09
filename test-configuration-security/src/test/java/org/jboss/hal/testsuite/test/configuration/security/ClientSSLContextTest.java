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
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.PROVIDER_NAME;
import static org.jboss.hal.resources.Ids.ELYTRON_CLIENT_SSL_CONTEXT;
import static org.jboss.hal.resources.Ids.ELYTRON_SSL_ITEM;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CIPHER_SUITE_NAMES;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CLIENT_SSL_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CLIENT_SSL_DELETE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CLIENT_SSL_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SSL_CONTEXT_CIPHER_SUITE_NAMES;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.clientSslContextAddress;

@Manatoko
@Testcontainers
class ClientSSLContextTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @BeforeAll
    static void setupModel() throws Exception {
        Operations operations = new Operations(wildFly.managementClient());
        operations.add(clientSslContextAddress(CLIENT_SSL_UPDATE));
        operations.add(clientSslContextAddress(CLIENT_SSL_DELETE));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page ElytronOtherSettingsPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary(ELYTRON_SSL_ITEM, ELYTRON_CLIENT_SSL_CONTEXT + "-item");
        table = page.getClientSslContextTable();
        form = page.getClientSslContextForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(clientSslContextAddress(CLIENT_SSL_CREATE), table, CLIENT_SSL_CREATE);
    }

    @Test
    void update() throws Exception {
        table.select(CLIENT_SSL_UPDATE);
        crud.update(clientSslContextAddress(CLIENT_SSL_UPDATE), form, PROVIDER_NAME, Random.name());
    }

    @Test
    void updateCipherNames() throws Exception {
        table.select(CLIENT_SSL_UPDATE);
        crud.update(clientSslContextAddress(CLIENT_SSL_UPDATE), form, CIPHER_SUITE_NAMES,
                SSL_CONTEXT_CIPHER_SUITE_NAMES);
    }

    @Test
    void delete() throws Exception {
        crud.delete(clientSslContextAddress(CLIENT_SSL_DELETE), table, CLIENT_SSL_DELETE);
    }
}
