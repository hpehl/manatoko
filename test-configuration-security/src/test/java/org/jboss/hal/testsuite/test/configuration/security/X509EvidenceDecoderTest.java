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
import org.jboss.hal.testsuite.page.configuration.ElytronMappersDecodersPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.ALT_NAME_TYPE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.ALT_NAME_TYPE_DIRECTORY_NAME;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.ALT_NAME_TYPE_RFC822_NAME;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.EVIDENCE_DECODER_ITEM;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SEGMENT;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.X509_EVIDENCE_DECODER_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.X509_EVIDENCE_DECODER_DELETE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.X509_EVIDENCE_DECODER_READ;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.X509_EVIDENCE_DECODER_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.x509EvidenceDecoderAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class X509EvidenceDecoderTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        Operations operations = new Operations(wildFly.managementClient());
        operations.add(x509EvidenceDecoderAddress(X509_EVIDENCE_DECODER_READ),
                Values.of(NAME, X509_EVIDENCE_DECODER_READ).and(ALT_NAME_TYPE, ALT_NAME_TYPE_DIRECTORY_NAME));
        operations.add(x509EvidenceDecoderAddress(X509_EVIDENCE_DECODER_UPDATE),
                Values.of(NAME, X509_EVIDENCE_DECODER_UPDATE).and(ALT_NAME_TYPE, ALT_NAME_TYPE_DIRECTORY_NAME));
        operations.add(x509EvidenceDecoderAddress(X509_EVIDENCE_DECODER_DELETE),
                Values.of(NAME, X509_EVIDENCE_DECODER_DELETE).and(ALT_NAME_TYPE, ALT_NAME_TYPE_DIRECTORY_NAME));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page ElytronMappersDecodersPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary(EVIDENCE_DECODER_ITEM,
                "mappers-decoders-x509-subject-alt-name-evidence-decoder-item");
        table = page.getX509EvidenceDecoderTable();
        form = page.getX509EvidenceDecoderForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(x509EvidenceDecoderAddress(X509_EVIDENCE_DECODER_CREATE), table, form -> {
            form.text(NAME, X509_EVIDENCE_DECODER_CREATE);
            form.select(ALT_NAME_TYPE, ALT_NAME_TYPE_DIRECTORY_NAME);
        });
    }

    @Test
    void read() {
        table.select(X509_EVIDENCE_DECODER_READ);
        assertEquals(ALT_NAME_TYPE_DIRECTORY_NAME, form.value(ALT_NAME_TYPE));
    }

    @Test
    void update() throws Exception {
        table.select(X509_EVIDENCE_DECODER_UPDATE);
        crud.update(x509EvidenceDecoderAddress(X509_EVIDENCE_DECODER_UPDATE), form, f -> {
            f.select(ALT_NAME_TYPE, ALT_NAME_TYPE_RFC822_NAME);
            f.number(SEGMENT, 23);
        }, resourceVerifier -> {
            resourceVerifier.verifyAttribute(ALT_NAME_TYPE, ALT_NAME_TYPE_RFC822_NAME);
            resourceVerifier.verifyAttribute(SEGMENT, 23);
        });
    }

    @Test
    void delete() throws Exception {
        crud.delete(x509EvidenceDecoderAddress(X509_EVIDENCE_DECODER_DELETE), table, X509_EVIDENCE_DECODER_DELETE);
    }
}
