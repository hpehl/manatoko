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
import static org.jboss.hal.dmr.ModelDescriptionConstants.URL;
import static org.jboss.hal.resources.Ids.ELYTRON_OTHER_ITEM;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CERTIFICATE_AUTHORITY_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CERTIFICATE_AUTHORITY_DELETE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CERTIFICATE_AUTHORITY_READ;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CERTIFICATE_AUTHORITY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CERTIFICATE_AUTHORITY_URL;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.certificateAuthorityAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class CertificateAuthorityTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @BeforeAll
    static void setupModel() throws Exception {
        Operations operations = new Operations(wildFly.managementClient());
        operations.add(certificateAuthorityAddress(CERTIFICATE_AUTHORITY_READ),
                Values.of(NAME, CERTIFICATE_AUTHORITY_READ).and(URL, CERTIFICATE_AUTHORITY_URL));
        operations.add(certificateAuthorityAddress(CERTIFICATE_AUTHORITY_UPDATE),
                Values.of(NAME, CERTIFICATE_AUTHORITY_UPDATE).and(URL, CERTIFICATE_AUTHORITY_URL));
        operations.add(certificateAuthorityAddress(CERTIFICATE_AUTHORITY_DELETE),
                Values.of(NAME, CERTIFICATE_AUTHORITY_DELETE).and(URL, CERTIFICATE_AUTHORITY_URL));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page ElytronOtherSettingsPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary(ELYTRON_OTHER_ITEM, "elytron-certificate-authority-item");
        table = page.getCertificateAuthorityTable();
        form = page.getCertificateAuthorityForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(certificateAuthorityAddress(CERTIFICATE_AUTHORITY_CREATE), table, form -> {
            form.text(NAME, CERTIFICATE_AUTHORITY_CREATE);
            form.text(URL, CERTIFICATE_AUTHORITY_URL);
        });
    }

    @Test
    void read() {
        table.select(CERTIFICATE_AUTHORITY_READ);
        assertEquals(CERTIFICATE_AUTHORITY_URL, form.value(URL));
    }

    @Test
    void update() throws Exception {
        table.select(CERTIFICATE_AUTHORITY_UPDATE);
        crud.update(certificateAuthorityAddress(CERTIFICATE_AUTHORITY_UPDATE), form, URL, "https://redhat.com");
    }

    @Test
    void delete() throws Exception {
        crud.delete(certificateAuthorityAddress(CERTIFICATE_AUTHORITY_DELETE), table, CERTIFICATE_AUTHORITY_DELETE);
    }
}
