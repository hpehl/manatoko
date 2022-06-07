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
package org.jboss.hal.testsuite.command;

import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.model.CredentialReference;
import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CREATE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CREDENTIAL_REFERENCE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PATH;
import static org.jboss.hal.dmr.ModelDescriptionConstants.RELATIVE_TO;
import static org.jboss.hal.testsuite.fixtures.PathsFixtures.JBOSS_SERVER_DATA_DIR;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.CREDENTIAL_STORE_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.credentialStoreAddress;

public class AddCredentialStore implements OnlineCommand {

    private final String name;

    public AddCredentialStore(final String name) {
        this.name = name;
    }

    @Override
    public void apply(final OnlineCommandContext context) throws Exception {
        Operations operations = new Operations(context.client);
        operations.add(credentialStoreAddress(CREDENTIAL_STORE_CREATE), Values
                .of(PATH, Random.name())
                .and(RELATIVE_TO, JBOSS_SERVER_DATA_DIR)
                .and(CREATE, true)
                .and(CREDENTIAL_REFERENCE, CredentialReference.clearText("secret")));
    }
}
