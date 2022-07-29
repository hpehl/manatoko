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
import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.as.cli.Util.HTTP_SERVER_MECHANISM_FACTORY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SECURITY_DOMAIN;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.httpAuthenticationFactoryAddress;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.providerHttpServerMechanismFactoryAddress;

public class AddHttpAuthenticationFactory implements OnlineCommand {

    private final String name;

    public AddHttpAuthenticationFactory(String name) {
        this.name = name;
    }

    @Override
    public void apply(OnlineCommandContext context) throws Exception {
        String securityDomain = Random.name();
        String httpServerMechanismFactory = Random.name();

        OnlineManagementClient client = context.client;
        Operations operations = new Operations(client);

        client.apply(new AddSecurityDomain(securityDomain));
        operations.add(providerHttpServerMechanismFactoryAddress(httpServerMechanismFactory));
        operations.add(httpAuthenticationFactoryAddress(name), Values
                .of(HTTP_SERVER_MECHANISM_FACTORY, httpServerMechanismFactory)
                .and(SECURITY_DOMAIN, securityDomain));
    }
}
