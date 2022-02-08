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
package org.jboss.hal.testsuite.command;

import org.jboss.dmr.ModelNode;
import org.jboss.hal.testsuite.Random;
import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT_REALM;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ELYTRON;
import static org.jboss.hal.dmr.ModelDescriptionConstants.FILESYSTEM_REALM;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PATH;
import static org.jboss.hal.dmr.ModelDescriptionConstants.REALM;
import static org.jboss.hal.dmr.ModelDescriptionConstants.REALMS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SECURITY_DOMAIN;

public class AddSecurityDomain implements OnlineCommand {

    private final String name;

    public AddSecurityDomain(final String name) {
        this.name = name;
    }

    @Override
    public void apply(final OnlineCommandContext ctx) throws Exception {
        Operations operations = new Operations(ctx.client);
        Batch batch = new Batch();
        String realm = Random.name();
        ModelNode node = new ModelNode();
        node.get(REALM).set(realm);

        batch.add(Address.subsystem(ELYTRON).and(FILESYSTEM_REALM, realm), Values.of(PATH, Random.name()));
        batch.add(Address.subsystem(ELYTRON).and(SECURITY_DOMAIN, name),
                Values.of(DEFAULT_REALM, realm).andList(REALMS, node));
        operations.batch(batch);
    }
}
