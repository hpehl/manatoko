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

import java.util.List;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.dmr.ModelNode;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddKeyStore;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.page.configuration.SecurityRealmPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CLIENT_SSL_CONTEXT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.KEY_STORE;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.AUDIENCE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CERTIFICATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CLIENT_ID;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CLIENT_SECRET;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CLIENT_SSL_READ;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.INTROSPECTION_URL;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.INTROSPECTION_URL_VALUE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.ISSUER;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.JWT;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.JWT_TAB;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.KEY_STORE_READ;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.OAUTH2_INTROSPECTION;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.OAUTH2_INTROSPECTION_TAB;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.PRINCIPAL_CLAIM;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.PUBLIC_KEY;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.TOKEN_REALM_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.TOKEN_REALM_DELETE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.TOKEN_REALM_JWT_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.TOKEN_REALM_JWT_DELETE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.TOKEN_REALM_JWT_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.TOKEN_REALM_OAUTH2_INTROSPECTION_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.TOKEN_REALM_OAUTH2_INTROSPECTION_DELETE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.TOKEN_REALM_OAUTH2_INTROSPECTION_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.TOKEN_REALM_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.clientSslContextAddress;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.tokenRealmAddress;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Manatoko
@Testcontainers
class TokenRealmTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);

        client.apply(new AddKeyStore(KEY_STORE_READ));
        operations.add(clientSslContextAddress(CLIENT_SSL_READ));

        operations.add(tokenRealmAddress(TOKEN_REALM_UPDATE));
        operations.add(tokenRealmAddress(TOKEN_REALM_DELETE));

        ModelNode jwt = new ModelNode();
        jwt.get(AUDIENCE).add(Random.name());
        operations.add(tokenRealmAddress(TOKEN_REALM_JWT_CREATE));
        operations.add(tokenRealmAddress(TOKEN_REALM_JWT_UPDATE), Values.of(JWT, jwt));
        operations.add(tokenRealmAddress(TOKEN_REALM_JWT_DELETE), Values.of(JWT, jwt));

        ModelNode oauth2Introspection = new ModelNode();
        oauth2Introspection.get(CLIENT_ID).set(Random.name());
        oauth2Introspection.get(CLIENT_SECRET).set(Random.name());
        oauth2Introspection.get(CLIENT_SSL_CONTEXT).set(CLIENT_SSL_READ);
        oauth2Introspection.get(INTROSPECTION_URL).set(INTROSPECTION_URL_VALUE);
        operations.add(tokenRealmAddress(TOKEN_REALM_OAUTH2_INTROSPECTION_CREATE));
        operations.add(tokenRealmAddress(TOKEN_REALM_OAUTH2_INTROSPECTION_UPDATE),
                Values.of(OAUTH2_INTROSPECTION, oauth2Introspection));
        operations.add(tokenRealmAddress(TOKEN_REALM_OAUTH2_INTROSPECTION_DELETE),
                Values.of(OAUTH2_INTROSPECTION, oauth2Introspection));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page SecurityRealmPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary("security-realm-item", "elytron-token-realm-item");
        table = page.getTokenRealmTable();
        form = page.getTokenRealmForm();
        table.bind(form);
    }

    // ------------------------------------------------------ token realm

    @Test
    void create() throws Exception {
        crud.create(tokenRealmAddress(TOKEN_REALM_CREATE), table, TOKEN_REALM_CREATE);
    }

    @Test
    void update() throws Exception {
        table.select(TOKEN_REALM_UPDATE);
        crud.update(tokenRealmAddress(TOKEN_REALM_UPDATE), form, PRINCIPAL_CLAIM, Random.name());
    }

    @Test
    void delete() throws Exception {
        crud.delete(tokenRealmAddress(TOKEN_REALM_DELETE), table, TOKEN_REALM_DELETE);
    }

    // ------------------------------------------------------ jwt

    @Test
    void createJWT() throws Exception {
        table.select(TOKEN_REALM_JWT_CREATE);
        page.getTokenRealmTabs().select(JWT_TAB);
        crud.createSingleton(tokenRealmAddress(TOKEN_REALM_JWT_CREATE), page.getJwtForm(), null,
                resourceVerifier -> resourceVerifier.verifyAttribute(JWT, jwt -> assertTrue(jwt.isDefined())));
    }

    @Test
    void updateJWT() throws Exception {
        List<String> audience = List.of(Random.name(), Random.name());
        String certificate = Random.name();
        List<String> issuer = List.of(Random.name(), Random.name());

        table.select(TOKEN_REALM_JWT_UPDATE);
        page.getTokenRealmTabs().select(JWT_TAB);
        crud.update(tokenRealmAddress(TOKEN_REALM_JWT_UPDATE), page.getJwtForm(), f -> {
            f.list(AUDIENCE).removeTags();
            f.list(AUDIENCE).add(audience);
            f.text(CERTIFICATE, certificate);
            f.text(CLIENT_SSL_CONTEXT, CLIENT_SSL_READ);
            f.list(ISSUER).add(issuer);
            f.text(KEY_STORE, KEY_STORE_READ);
        }, resourceVerifier -> {
            ModelNode jwt = new ModelNode();
            jwt.get(AUDIENCE).add(audience.get(0));
            jwt.get(AUDIENCE).add(audience.get(1));
            jwt.get(CERTIFICATE).set(certificate);
            jwt.get(CLIENT_SSL_CONTEXT).set(CLIENT_SSL_READ);
            jwt.get(ISSUER).add(issuer.get(0));
            jwt.get(ISSUER).add(issuer.get(1));
            jwt.get(KEY_STORE).set(KEY_STORE_READ);
            jwt.get(PUBLIC_KEY).set(new ModelNode()); // needs to be undefined!
            resourceVerifier.verifyAttribute(JWT, jwt);
        });
    }

    @Test
    void deleteJWT() throws Exception {
        table.select(TOKEN_REALM_JWT_DELETE);
        page.getTokenRealmTabs().select(JWT_TAB);
        crud.deleteSingleton(tokenRealmAddress(TOKEN_REALM_JWT_DELETE), page.getJwtForm(),
                resourceVerifier -> resourceVerifier.verifyAttributeIsUndefined(JWT));
    }

    // ------------------------------------------------------ oauth2 introspection

    @Test
    void createOauth2Introspection() throws Exception {
        table.select(TOKEN_REALM_OAUTH2_INTROSPECTION_CREATE);
        page.getTokenRealmTabs().select(OAUTH2_INTROSPECTION_TAB);
        crud.createSingleton(tokenRealmAddress(TOKEN_REALM_OAUTH2_INTROSPECTION_CREATE), page.getOauth2IntrospectionForm(),
                f -> {
                    f.text(CLIENT_ID, Random.name());
                    f.text(CLIENT_SECRET, Random.name());
                    f.text(INTROSPECTION_URL, INTROSPECTION_URL_VALUE);
                }, ResourceVerifier::verifyExists);
    }

    @Test
    void updateOauth2Introspection() throws Exception {
        String clientId = Random.name();

        table.select(TOKEN_REALM_OAUTH2_INTROSPECTION_UPDATE);
        page.getTokenRealmTabs().select(OAUTH2_INTROSPECTION_TAB);
        crud.update(tokenRealmAddress(TOKEN_REALM_OAUTH2_INTROSPECTION_UPDATE), page.getOauth2IntrospectionForm(),
                f -> f.text(CLIENT_ID, clientId),
                resourceVerifier -> resourceVerifier.verifyAttribute(OAUTH2_INTROSPECTION + "." + CLIENT_ID, clientId));
    }

    @Test
    void deleteOauth2Introspection() throws Exception {
        table.select(TOKEN_REALM_OAUTH2_INTROSPECTION_DELETE);
        page.getTokenRealmTabs().select(OAUTH2_INTROSPECTION_TAB);
        crud.deleteSingleton(tokenRealmAddress(TOKEN_REALM_OAUTH2_INTROSPECTION_DELETE), page.getOauth2IntrospectionForm(),
                resourceVerifier -> resourceVerifier.verifyAttributeIsUndefined(OAUTH2_INTROSPECTION));
    }
}
