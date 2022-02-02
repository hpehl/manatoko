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

import org.jboss.hal.manatoko.creaper.ResourceVerifier;
import org.junit.jupiter.api.Test;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CLEAR_TEXT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.READ_ALIASES_OPERATION;
import static org.jboss.hal.manatoko.fixture.DataSourceFixtures.H2_PASSWORD;
import static org.jboss.hal.manatoko.fixture.ElytronFixtures.CRED_ST_UPDATE;
import static org.jboss.hal.manatoko.fixture.ElytronFixtures.credentialStoreAddress;

class DataSourceCredRefClearTextTest extends DataSourceCredRefBase {

    /**
     * Update the clear-text value of the credential reference. The number of aliases in the credential store must remain one.
     */
    @Test
    void updateClearText() throws Exception {
        form.edit();
        form.text(CLEAR_TEXT, H2_PASSWORD);
        form.save();

        reload();
        new ResourceVerifier(credentialStoreAddress(CRED_ST_UPDATE), client).verifyTrue(
                "Alias not found in credential store",
                () -> {
                    Operations operations = new Operations(client);
                    ModelNodeResult result = operations.invoke(READ_ALIASES_OPERATION,
                            credentialStoreAddress(CRED_ST_UPDATE));
                    return assertAlias(result, ALIAS_VALUE);
                });
    }
}
