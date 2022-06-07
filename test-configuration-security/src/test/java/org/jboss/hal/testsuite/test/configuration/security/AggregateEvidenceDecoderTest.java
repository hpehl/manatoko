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
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.AGGREGATE_EVIDENCE_DECODER_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.AGGREGATE_EVIDENCE_DECODER_DELETE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.AGGREGATE_EVIDENCE_DECODER_READ;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.AGGREGATE_EVIDENCE_DECODER_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.ALT_NAME_TYPE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.ALT_NAME_TYPE_DIRECTORY_NAME;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.EVIDENCE_DECODERS;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.EVIDENCE_DECODER_ITEM;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.X500_EVIDENCE_DECODER_READ;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.X500_EVIDENCE_DECODER_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.X509_EVIDENCE_DECODER_READ;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.X509_EVIDENCE_DECODER_UPDATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.aggregateEvidenceDecoderAddress;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.x500EvidenceDecoderAddress;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.x509EvidenceDecoderAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class AggregateEvidenceDecoderTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        ModelNode evidenceDecoders = new ModelNode()
                .add(X500_EVIDENCE_DECODER_READ)
                .add(X509_EVIDENCE_DECODER_READ);
        Operations operations = new Operations(wildFly.managementClient());

        operations.add(x500EvidenceDecoderAddress(X500_EVIDENCE_DECODER_READ), Values.of(NAME, X500_EVIDENCE_DECODER_READ));
        operations.add(x500EvidenceDecoderAddress(X500_EVIDENCE_DECODER_UPDATE), Values.of(NAME, X500_EVIDENCE_DECODER_UPDATE));

        operations.add(x509EvidenceDecoderAddress(X509_EVIDENCE_DECODER_READ),
                Values.of(NAME, X509_EVIDENCE_DECODER_READ).and(ALT_NAME_TYPE, ALT_NAME_TYPE_DIRECTORY_NAME));
        operations.add(x509EvidenceDecoderAddress(X509_EVIDENCE_DECODER_UPDATE),
                Values.of(NAME, X509_EVIDENCE_DECODER_UPDATE).and(ALT_NAME_TYPE, ALT_NAME_TYPE_DIRECTORY_NAME));

        operations.add(aggregateEvidenceDecoderAddress(AGGREGATE_EVIDENCE_DECODER_READ),
                Values.of(EVIDENCE_DECODERS, evidenceDecoders));
        operations.add(aggregateEvidenceDecoderAddress(AGGREGATE_EVIDENCE_DECODER_UPDATE),
                Values.of(EVIDENCE_DECODERS, evidenceDecoders));
        operations.add(aggregateEvidenceDecoderAddress(AGGREGATE_EVIDENCE_DECODER_DELETE),
                Values.of(EVIDENCE_DECODERS, evidenceDecoders));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page ElytronMappersDecodersPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary(EVIDENCE_DECODER_ITEM, "mappers-decoders-aggregate-evidence-decoder-item");
        table = page.getAggregateEvidenceDecoderTable();
        form = page.getAggregateEvidenceDecoderForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(aggregateEvidenceDecoderAddress(AGGREGATE_EVIDENCE_DECODER_CREATE), table, form -> {
            form.text(NAME, AGGREGATE_EVIDENCE_DECODER_CREATE);
            form.list(EVIDENCE_DECODERS)
                    .add(X500_EVIDENCE_DECODER_READ)
                    .add(X509_EVIDENCE_DECODER_READ);
        });
    }

    @Test
    void read() {
        table.select(AGGREGATE_EVIDENCE_DECODER_READ);
        assertEquals(X500_EVIDENCE_DECODER_READ + ", " + X509_EVIDENCE_DECODER_READ, form.value(EVIDENCE_DECODERS));
    }

    @Test
    void update() throws Exception {
        ModelNode updatedEvidenceDecoders = new ModelNode()
                .add(X500_EVIDENCE_DECODER_UPDATE)
                .add(X509_EVIDENCE_DECODER_UPDATE);

        table.select(AGGREGATE_EVIDENCE_DECODER_UPDATE);
        crud.update(aggregateEvidenceDecoderAddress(AGGREGATE_EVIDENCE_DECODER_UPDATE), form, f -> {
            f.list(EVIDENCE_DECODERS).removeTags();
            f.list(EVIDENCE_DECODERS)
                    .add(X500_EVIDENCE_DECODER_UPDATE)
                    .add(X509_EVIDENCE_DECODER_UPDATE);
        }, resourceVerifier -> resourceVerifier.verifyAttribute(EVIDENCE_DECODERS, updatedEvidenceDecoders));
    }

    @Test
    void delete() throws Exception {
        crud.delete(aggregateEvidenceDecoderAddress(AGGREGATE_EVIDENCE_DECODER_DELETE), table,
                AGGREGATE_EVIDENCE_DECODER_DELETE);
    }
}
