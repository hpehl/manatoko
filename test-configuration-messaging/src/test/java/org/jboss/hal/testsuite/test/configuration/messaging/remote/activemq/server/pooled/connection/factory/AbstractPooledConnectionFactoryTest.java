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
package org.jboss.hal.testsuite.test.configuration.messaging.remote.activemq.server.pooled.connection.factory;

import java.io.IOException;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.dmr.ModelNodeGenerator;
import org.jboss.hal.testsuite.fixtures.MessagingFixtures;
import org.jboss.hal.testsuite.page.configuration.MessagingRemoteActiveMQPage;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

abstract class AbstractPooledConnectionFactoryTest {

    protected static void createDiscoveryGroup(Operations operations, String name, String jgroupsChannel)
            throws IOException {
        Batch batch = new Batch();
        batch.add(MessagingFixtures.RemoteActiveMQServer.discoveryGroupAddress(name));
        batch.writeAttribute(MessagingFixtures.RemoteActiveMQServer.discoveryGroupAddress(name), "jgroups-channel",
                jgroupsChannel);
        batch.writeAttribute(
                MessagingFixtures.RemoteActiveMQServer.discoveryGroupAddress(name), "jgroups-cluster", Random.name());
        operations.batch(batch).assertSuccess();
    }

    protected static void createPooledConnectionFactory(Operations operations, String name, String discoveryGroup)
            throws IOException {
        operations.add(MessagingFixtures.RemoteActiveMQServer.pooledConnectionFactoryAddress(name),
                Values.of(ModelDescriptionConstants.DISCOVERY_GROUP, discoveryGroup)
                        .and("entries",
                                new ModelNodeGenerator.ModelNodeListBuilder()
                                        .addAll(Random.name(), Random.name(), Random.name())
                                        .build()))
                .assertSuccess();
    }

    protected static void createGenericConnector(Operations operations, String name) throws IOException {
        operations.add(
                MessagingFixtures.RemoteActiveMQServer.genericConnectorAddress(name),
                Values.of("factory-class", Random.name()))
                .assertSuccess();
    }

    @Inject protected Console console;
    @Inject protected CrudOperations crudOperations;
    @Page protected MessagingRemoteActiveMQPage page;
}
