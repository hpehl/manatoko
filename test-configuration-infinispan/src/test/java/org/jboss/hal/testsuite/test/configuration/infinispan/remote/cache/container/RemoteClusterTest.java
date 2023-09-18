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

import java.io.IOException;
import java.util.Collections;

import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.model.ModelNodeGenerator;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SOCKET_BINDINGS;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.remoteClusterAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Enable once https://issues.redhat.com/browse/HAL-1904 has been fixed
class RemoteClusterTest extends AbstractRemoteCacheContainerTest {

    private static final String REMOTE_CACHE_CONTAINER_TO_BE_TESTED = "remote-cache-container-to-be-tested-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING = "remote-socket-binding-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING_CLUSTER_CREATE = "remote-socket-binding-cluster-create" + Random.name();
    private static final String REMOTE_SOCKET_BINDING_CLUSTER_EDIT = "remote-socket-binding-cluster-edit-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING_CLUSTER_DELETE = "remote-socket-binding-cluster-delete-" + Random.name();

    private static final String REMOTE_CLUSTER = "remote-cluster-" + Random.name();
    private static final String REMOTE_CLUSTER_CREATE = "remote-cluster-to-be-created-" + Random.name();
    private static final String REMOTE_CLUSTER_DELETE = "remote-cluster-to-be-deleted-" + Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING_CLUSTER_CREATE);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING_CLUSTER_EDIT);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING_CLUSTER_DELETE);
        createRemoteCacheContainer(operations, REMOTE_CACHE_CONTAINER_TO_BE_TESTED,
                REMOTE_SOCKET_BINDING_CLUSTER_CREATE);
        createRemoteCluster(operations, REMOTE_CLUSTER, REMOTE_SOCKET_BINDING);
        createRemoteCluster(operations, REMOTE_CLUSTER_DELETE,
                REMOTE_SOCKET_BINDING_CLUSTER_DELETE);
        new Administration(client).reloadIfRequired();
    }

    private static void createRemoteCluster(Operations operations, String remoteClusterName, String socketBinding)
            throws IOException {
        operations.add(remoteClusterAddress(RemoteClusterTest.REMOTE_CACHE_CONTAINER_TO_BE_TESTED, remoteClusterName),
                Values.of("socket-bindings",
                        new ModelNodeGenerator.ModelNodeListBuilder().addAll(socketBinding).build()));
    }

    @BeforeEach
    void prepare() {
        page.navigate("name", REMOTE_CACHE_CONTAINER_TO_BE_TESTED);
        console.verticalNavigation().selectPrimary("rc-item");
    }

    @Test
    void create() throws Exception {
        crudOperations.create(remoteClusterAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED, REMOTE_CLUSTER_CREATE),
                page.getRemoteClusterTable(), formFragment -> {
                    formFragment.text("name", REMOTE_CLUSTER_CREATE);
                    formFragment.list(SOCKET_BINDINGS).add(REMOTE_SOCKET_BINDING_CLUSTER_CREATE);
                }, resourceVerifier -> {
                    resourceVerifier.verifyExists();
                    resourceVerifier.verifyListAttributeContainsValue(SOCKET_BINDINGS,
                            REMOTE_SOCKET_BINDING_CLUSTER_CREATE);
                });
    }

    @Test
    void delete() throws Exception {
        crudOperations.delete(remoteClusterAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED, REMOTE_CLUSTER_DELETE),
                page.getRemoteClusterTable(), REMOTE_CLUSTER_DELETE);
    }

    @Test
    void edit() throws Exception {
        page.getRemoteClusterTable().select(REMOTE_CLUSTER);
        crudOperations.update(remoteClusterAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED, REMOTE_CLUSTER),
                page.getRemoteClusterForm(), SOCKET_BINDINGS,
                Collections.singletonList(REMOTE_SOCKET_BINDING_CLUSTER_EDIT));
    }
}
