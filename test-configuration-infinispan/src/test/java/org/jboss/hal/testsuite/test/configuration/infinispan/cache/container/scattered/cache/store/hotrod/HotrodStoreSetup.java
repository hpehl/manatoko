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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.store.hotrod;

import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddRemoteSocketBinding;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.model.AvailablePortFinder;
import org.jboss.hal.testsuite.model.ModelNodeGenerator;
import org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.store.StoreSetup;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.JGROUPS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TRANSPORT;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SOCKET_BINDINGS;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.hotrodStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.remoteCacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

interface HotrodStoreSetup {

    String REMOTE_SOCKET_BINDING = "remote-socket-binding-" + Random.name();
    String REMOTE_CLUSTER = "remote-cluster-" + Random.name();
    String REMOTE_CACHE_CONTAINER = "remote-cache-container-" + Random.name();
    String REMOTE_SOCKET_BINDING_EDIT = "remote-socket-binding-edit-" + Random.name();
    String REMOTE_CLUSTER_EDIT = "remote-cluster-edit-" + Random.name();
    String REMOTE_CACHE_CONTAINER_EDIT = "remote-cache-container-edit-" + Random.name();

    static void setup(WildFlyContainer wildFly, StoreSetup setup) throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);

        client.apply(new AddRemoteSocketBinding(REMOTE_SOCKET_BINDING, "localhost",
                AvailablePortFinder.getNextAvailableTCPPort()));
        operations.batch(new Batch().add(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER),
                Values.of("default-remote-cluster", REMOTE_CLUSTER))
                .add(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER).and("remote-cluster", REMOTE_CLUSTER),
                        Values.of(SOCKET_BINDINGS,
                                new ModelNodeGenerator.ModelNodeListBuilder().addAll(REMOTE_SOCKET_BINDING).build())));
        client.apply(new AddRemoteSocketBinding(REMOTE_SOCKET_BINDING_EDIT, "localhost",
                AvailablePortFinder.getNextAvailableTCPPort()));
        operations.batch(new Batch().add(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_EDIT),
                Values.of("default-remote-cluster", REMOTE_CLUSTER_EDIT))
                .add(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_EDIT).and("remote-cluster",
                        REMOTE_CLUSTER_EDIT),
                        Values.of(SOCKET_BINDINGS,
                                new ModelNodeGenerator.ModelNodeListBuilder().addAll(REMOTE_SOCKET_BINDING).build())));

        operations.add(cacheContainerAddress(CC_CREATE));
        operations.add(cacheContainerAddress(CC_CREATE).and(TRANSPORT, JGROUPS));
        operations.add(scatteredCacheAddress(CC_CREATE, SC_CREATE));
        operations.headers(Values.of("allow-resource-service-restart", true))
                .add(hotrodStoreAddress(CC_CREATE, SC_CREATE),
                        Values.of("remote-cache-container", REMOTE_CACHE_CONTAINER));
        setup.accept(operations);
    }
}
