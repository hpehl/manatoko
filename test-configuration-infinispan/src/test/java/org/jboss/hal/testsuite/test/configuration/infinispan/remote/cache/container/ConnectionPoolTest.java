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
package org.jboss.hal.testsuite.test.configuration.infinispan.remote.cache.container;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.model.ModelNodeGenerator;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SOCKET_BINDINGS;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.connectionPoolAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.remoteClusterAddress;

@Manatoko
@Testcontainers
class ConnectionPoolTest extends AbstractRemoteCacheContainerTest {

    private static final String REMOTE_CACHE_CONTAINER_TO_BE_TESTED = "remote-cache-container-to-be-tested-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING = "remote-socket-binding-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING_CLUSTER = "remote-socket-binding-cluster-" + Random.name();
    private static final String REMOTE_CLUSTER = "remote-cluster-" + Random.name();
    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING_CLUSTER);
        createRemoteCacheContainer(operations, REMOTE_CACHE_CONTAINER_TO_BE_TESTED, REMOTE_SOCKET_BINDING);
        operations.add(remoteClusterAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED, REMOTE_CLUSTER),
                Values.of(SOCKET_BINDINGS,
                        new ModelNodeGenerator.ModelNodeListBuilder().addAll(REMOTE_SOCKET_BINDING_CLUSTER).build()));
        new Administration(client).reloadIfRequired();
    }

    @BeforeEach
    void prepare() {
        page.navigate("name", REMOTE_CACHE_CONTAINER_TO_BE_TESTED);
        console.verticalNavigation().selectPrimary("connection-pool-item");
    }

    @Test
    void editExhaustedAction() throws Exception {
        String previousExhaustedAction = operations
                .readAttribute(connectionPoolAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED), "exhausted-action")
                .stringValue();
        String[] allExhaustedActions = { "EXCEPTION", "WAIT", "CREATE_NEW" };
        List<String> availableExhaustedActions = Arrays.stream(allExhaustedActions)
                .filter(action -> !action.equals(previousExhaustedAction))
                .collect(Collectors.toList());
        String exhaustedAction = availableExhaustedActions.get(Random.number(0, availableExhaustedActions.size()));
        crudOperations.update(connectionPoolAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED), page.getConnectionPoolForm(),
                formFragment -> formFragment.select("exhausted-action", exhaustedAction),
                resourceVerifier -> resourceVerifier.verifyAttribute("exhausted-action", exhaustedAction));
    }

    @Test
    void editMaxActive() throws Exception {
        crudOperations.update(connectionPoolAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED), page.getConnectionPoolForm(),
                "max-active", Random.number());
    }

    @Test
    void editMaxWait() throws Exception {
        crudOperations.update(connectionPoolAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED), page.getConnectionPoolForm(),
                "max-wait", (long) Random.number());
    }

    @Test
    void editMinEvictableIdleTime() throws Exception {
        crudOperations.update(connectionPoolAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED), page.getConnectionPoolForm(),
                "min-evictable-idle-time", (long) Random.number());
    }

    @Test
    void editMinIdle() throws Exception {
        crudOperations.update(connectionPoolAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED), page.getConnectionPoolForm(),
                "min-idle", Random.number());
    }
}
