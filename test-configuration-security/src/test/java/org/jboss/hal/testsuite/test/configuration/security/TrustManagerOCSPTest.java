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
import org.jboss.hal.testsuite.command.AddKeyStore;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.KEY_STORE;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.OCSP;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.RESPONDER;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.TRUST_MANAGER_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.TRUST_MANAGER_DELETE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.TRUST_MANAGER_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.trustManagerAddress;

@Manatoko
@Testcontainers
class TrustManagerOCSPTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        ModelNode ocspModel = new ModelNode();
        ocspModel.get(RESPONDER).set("responder");

        OnlineManagementClient client = wildFly.managementClient();
        String keyStore = Random.name();
        client.apply(new AddKeyStore(keyStore));

        Operations operations = new Operations(wildFly.managementClient());
        operations.add(trustManagerAddress(TRUST_MANAGER_CREATE), Values.of(KEY_STORE, keyStore));
        operations.add(trustManagerAddress(TRUST_MANAGER_UPDATE), Values.of(KEY_STORE, keyStore));
        operations.writeAttribute(trustManagerAddress(TRUST_MANAGER_UPDATE), OCSP, ocspModel);
        operations.add(trustManagerAddress(TRUST_MANAGER_DELETE), Values.of(KEY_STORE, keyStore));
        operations.writeAttribute(trustManagerAddress(TRUST_MANAGER_DELETE), OCSP, ocspModel);
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page ElytronOtherSettingsPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary(Ids.ELYTRON_SSL_ITEM, "elytron-trust-manager-item");
        table = page.getTrustManagerTable();
        form = page.getTrustManagerOCSPForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        table.select(TRUST_MANAGER_CREATE);
        page.getTrustManagerTab().select("elytron-trust-manager-ocsp-tab");
        crud.createSingleton(trustManagerAddress(TRUST_MANAGER_CREATE), form);
    }

    @Test
    void update() throws Exception {
        String responder = Random.name();
        table.select(TRUST_MANAGER_UPDATE);
        page.getTrustManagerTab().select("elytron-trust-manager-ocsp-tab");
        crud.update(trustManagerAddress(TRUST_MANAGER_UPDATE), form, f -> {
            f.text(RESPONDER, responder);
        }, resourceVerifier -> resourceVerifier.verifyAttribute(OCSP + "." + RESPONDER, responder));
    }

    @Test
    void delete() throws Exception {
        table.select(TRUST_MANAGER_DELETE);
        page.getTrustManagerTab().select("elytron-trust-manager-ocsp-tab");
        crud.deleteSingleton(trustManagerAddress(TRUST_MANAGER_DELETE), form,
                resourceVerifier -> resourceVerifier.verifyAttributeIsUndefined(OCSP));
    }
}
