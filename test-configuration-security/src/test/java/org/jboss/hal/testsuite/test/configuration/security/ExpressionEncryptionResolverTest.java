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
import org.jboss.dmr.ModelNode;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddCredentialStore;
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
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CREDENTIAL_STORE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.RESOLVERS;
import static org.jboss.hal.resources.Ids.ELYTRON_OTHER_ITEM;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CREDENTIAL_STORE_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.EXPRESSION_RESOLVER_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.EXPRESSION_RESOLVER_DELETE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.EXPRESSION_RESOLVER_READ;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.EXPRESSION_RESOLVER_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SECRET_KEY;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.expressionEncryptionAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class ExpressionEncryptionResolverTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        client.apply(new AddCredentialStore(CREDENTIAL_STORE_CREATE));

        ModelNode resolvers = new ModelNode();
        resolvers.add(resolver(EXPRESSION_RESOLVER_READ));
        resolvers.add(resolver(EXPRESSION_RESOLVER_UPDATE));
        resolvers.add(resolver(EXPRESSION_RESOLVER_DELETE));

        Operations operations = new Operations(client);
        operations.add(expressionEncryptionAddress(), Values.of(RESOLVERS, resolvers));
    }

    private static ModelNode resolver(String name) {
        ModelNode resolver = new ModelNode();
        resolver.get(NAME).set(name);
        resolver.get(CREDENTIAL_STORE).set(CREDENTIAL_STORE_CREATE);
        resolver.get(SECRET_KEY).set(Random.name());
        return resolver;
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page ElytronOtherSettingsPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary(ELYTRON_OTHER_ITEM, Ids.ELYTRON_EXPRESSION);
        table = page.getExpressionResolverTable();
        form = page.getExpressionResolverForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(expressionEncryptionAddress(), table, f -> {
            f.text(CREDENTIAL_STORE, CREDENTIAL_STORE_CREATE);
            f.text(NAME, EXPRESSION_RESOLVER_CREATE);
            f.text(SECRET_KEY, Random.name());
        }, resourceVerifier -> resourceVerifier.verifyListAttributeContainsSingleValue(RESOLVERS, NAME,
                EXPRESSION_RESOLVER_CREATE));
    }

    @Test
    void read() {
        table.select(EXPRESSION_RESOLVER_READ);
        assertEquals(EXPRESSION_RESOLVER_READ, form.value(NAME));
    }

    @Test
    void update() throws Exception {
        String secretKey = Random.name();
        table.select(EXPRESSION_RESOLVER_UPDATE);
        crud.update(expressionEncryptionAddress(), form, f -> f.text(SECRET_KEY, secretKey),
                resourceVerifier -> resourceVerifier.verifyListAttributeContainsSingleValue(RESOLVERS, SECRET_KEY, secretKey));
    }

    @Test
    void delete() throws Exception {
        crud.delete(expressionEncryptionAddress(), table, EXPRESSION_RESOLVER_DELETE, resourceVerifier -> resourceVerifier
                .verifyListAttributeDoesNotContainSingleValue(RESOLVERS, NAME, EXPRESSION_RESOLVER_DELETE));
    }
}
