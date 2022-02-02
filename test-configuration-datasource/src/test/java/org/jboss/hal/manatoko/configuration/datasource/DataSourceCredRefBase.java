/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.hal.manatoko.configuration.datasource;

import java.util.List;

import org.jboss.arquillian.graphene.page.Page;
import org.jboss.dmr.ModelNode;
import org.jboss.hal.manatoko.Random;
import org.jboss.hal.manatoko.dmr.CredentialReference;
import org.jboss.hal.manatoko.fragment.FormFragment;
import org.jboss.hal.manatoko.page.DataSourcePage;
import org.jboss.hal.manatoko.test.WildFlyTest;
import org.jboss.hal.manatoko.util.Library;
import org.jboss.hal.resources.Ids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.wildfly.extras.creaper.commands.datasources.AddDataSource;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CREATE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CREDENTIAL_REFERENCE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PASSWORD;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PATH;
import static org.jboss.hal.dmr.ModelDescriptionConstants.RELATIVE_TO;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.DATA_SOURCE_UPDATE;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.H2_PASSWORD;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.H2_USER_NAME;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.dataSourceAddress;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.h2ConnectionUrl;
import static org.jboss.hal.manatoko.fixture.ElytronFixtures.CRED_ST_UPDATE;
import static org.jboss.hal.manatoko.fixture.ElytronFixtures.credentialStoreAddress;

abstract class DataSourceCredRefBase extends WildFlyTest {

    protected static final String ALIAS_VALUE = Random.name();
    protected static final OnlineManagementClient client = wildFly.managementClient();

    @BeforeAll
    static void setupModel() throws Exception {
        Administration administration = new Administration(client);
        Operations operations = new Operations(client);

        Values credParams = Values
                .of(PATH, CRED_ST_UPDATE)
                .and(RELATIVE_TO, "jboss.server.config.dir")
                .and(CREATE, true)
                .and(CREDENTIAL_REFERENCE, CredentialReference.clearText("secret"));
        operations.add(credentialStoreAddress(CRED_ST_UPDATE), credParams);
        client.apply(new AddDataSource.Builder<>(DATA_SOURCE_UPDATE)
                .driverName("h2")
                .jndiName(Random.jndiName(DATA_SOURCE_UPDATE))
                .connectionUrl(h2ConnectionUrl(DATA_SOURCE_UPDATE))
                .usernameAndPassword(H2_USER_NAME, H2_PASSWORD)
                .build());
        Batch credRef = new Batch();
        credRef.undefineAttribute(dataSourceAddress(DATA_SOURCE_UPDATE), PASSWORD);
        credRef.writeAttribute(dataSourceAddress(DATA_SOURCE_UPDATE), CREDENTIAL_REFERENCE,
                CredentialReference.storeAlias(CRED_ST_UPDATE, ALIAS_VALUE, H2_PASSWORD));
        operations.batch(credRef);
        administration.reloadIfRequired();
    }

    @Page DataSourcePage page;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate(NAME, DATA_SOURCE_UPDATE);
        page.getTabs().select(Ids.build(Ids.DATA_SOURCE_CONFIGURATION, CREDENTIAL_REFERENCE, Ids.TAB));
        form = page.getCredentialReferenceForm();
    }

    void reload() throws Exception {
        new Administration(client).reloadIfRequired();
        Library.letsSleep(3000);
    }

    protected boolean assertAlias(ModelNodeResult result, String expectedAlias) {
        if (result.isSuccess()) {
            List<ModelNode> aliases = result.value().asList();
            if (!aliases.isEmpty()) {
                return aliases.stream()
                        .map(ModelNode::asString)
                        .anyMatch(actualAlias -> actualAlias.equals(expectedAlias));
            }
        }
        return false;
    }
}
