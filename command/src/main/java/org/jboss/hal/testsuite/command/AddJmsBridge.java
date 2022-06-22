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
import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.MODULE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.AT_MOST_ONCE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.CONNECTION_FACTORY_VALUE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.DESTINATION_QUEUE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.QUALITY_OF_SERVICE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.REMOTE_CONNECTION_FACTORY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SOURCE_CONNECTION_FACTORY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SOURCE_DESTINATION;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.TARGET_CONNECTION_FACTORY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.TARGET_CONTEXT;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.TARGET_DESTINATION;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.jmsBridgeAddress;

public class AddJmsBridge implements OnlineCommand {

    private final String name;

    public AddJmsBridge(final String name) {
        this.name = name;
    }

    @Override
    public void apply(final OnlineCommandContext context) throws Exception {
        ModelNode targetContext = new ModelNode();
        targetContext.get("java.naming.factory.initial")
                .set("org.jboss.naming.remote.client.InitialContextFactory");
        targetContext.get("java.naming.provider.url").set("http-remoting://localhost:8180");

        Operations operations = new Operations(context.client);
        operations.add(jmsBridgeAddress(name), Values.of(QUALITY_OF_SERVICE, AT_MOST_ONCE)
                .and(MODULE, "org.wildfly.extension.messaging-activemq")
                .and(TARGET_CONTEXT, targetContext)
                .and(SOURCE_CONNECTION_FACTORY, CONNECTION_FACTORY_VALUE)
                .and(SOURCE_DESTINATION, DESTINATION_QUEUE)
                .and(TARGET_CONNECTION_FACTORY, REMOTE_CONNECTION_FACTORY)
                .and(TARGET_DESTINATION, DESTINATION_QUEUE));
    }
}
