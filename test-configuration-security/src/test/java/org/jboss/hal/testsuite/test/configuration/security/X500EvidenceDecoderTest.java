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
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
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
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.EVIDENCE_DECODER_ITEM;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.X500_EVIDENCE_DECODER_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.X500_EVIDENCE_DECODER_DELETE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.x500EvidenceDecoderAddress;

@Manatoko
@Testcontainers
class X500EvidenceDecoderTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        Operations operations = new Operations(wildFly.managementClient());
        operations.add(x500EvidenceDecoderAddress(X500_EVIDENCE_DECODER_DELETE), Values.of(NAME, X500_EVIDENCE_DECODER_DELETE));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page ElytronMappersDecodersPage page;
    TableFragment table;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectSecondary(EVIDENCE_DECODER_ITEM,
                "mappers-decoders-x500-subject-evidence-decoder-item");
        table = page.getX500EvidenceDecoderTable();
    }

    @Test
    void create() throws Exception {
        crud.create(x500EvidenceDecoderAddress(X500_EVIDENCE_DECODER_CREATE), table,
                form -> form.text(NAME, X500_EVIDENCE_DECODER_CREATE));
    }

    @Test
    void delete() throws Exception {
        crud.delete(x500EvidenceDecoderAddress(X500_EVIDENCE_DECODER_DELETE), table, X500_EVIDENCE_DECODER_DELETE);
    }
}
