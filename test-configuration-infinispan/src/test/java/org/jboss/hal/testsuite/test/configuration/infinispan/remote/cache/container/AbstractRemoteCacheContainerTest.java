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
package org.jboss.hal.testsuite.test.configuration.infinispan.remote.cache.container;

import java.io.IOException;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.creaper.command.AddRemoteSocketBinding;
import org.jboss.hal.testsuite.dmr.ModelNodeGenerator;
import org.jboss.hal.testsuite.page.configuration.RemoteCacheContainerPage;
import org.jboss.hal.testsuite.util.AvailablePortFinder;
import org.openqa.selenium.WebDriver;
import org.wildfly.extras.creaper.core.CommandFailedException;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SOCKET_BINDINGS;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.remoteCacheContainerAddress;

public abstract class AbstractRemoteCacheContainerTest {

    protected static void createRemoteCacheContainer(Operations operations, String name, String socketBinding)
            throws IOException {
        String remoteCluster = Random.name();
        operations.batch(
                new Batch().add(remoteCacheContainerAddress(name), Values.of("default-remote-cluster", remoteCluster))
                        .add(remoteCacheContainerAddress(name).and("remote-cluster", remoteCluster),
                                Values.of(SOCKET_BINDINGS,
                                        new ModelNodeGenerator.ModelNodeListBuilder().addAll(socketBinding).build())))
                .assertSuccess();
    }

    protected static void createRemoteSocketBinding(OnlineManagementClient client, String name)
            throws CommandFailedException {
        client.apply(new AddRemoteSocketBinding(name, "localhost", AvailablePortFinder.getNextAvailableTCPPort()));
    }

    @Drone protected WebDriver browser;
    @Page protected RemoteCacheContainerPage page;
    @Inject protected Console console;
    @Inject protected CrudOperations crudOperations;
}
