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

import org.jboss.dmr.ModelNode;
import org.jboss.hal.testsuite.Random;
import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CLEAR_TEXT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CREDENTIAL_REFERENCE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PATH;
import static org.jboss.hal.dmr.ModelDescriptionConstants.RELATIVE_TO;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TYPE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.keyStoreAddress;

public class AddKeyStore implements OnlineCommand {

    private final String name;

    public AddKeyStore(final String name) {
        this.name = name;
    }

    @Override
    public void apply(final OnlineCommandContext context) throws Exception {
        Operations operations = new Operations(context.client);

        ModelNode credentialReference = new ModelNode();
        credentialReference.get(CLEAR_TEXT).set(Random.name());
        operations.add(keyStoreAddress(name), Values.of(TYPE, "JKS")
                .and(CREDENTIAL_REFERENCE, credentialReference)
                .and(PATH, Random.name())
                .and(RELATIVE_TO, "jboss.home.dir"));
    }
}
