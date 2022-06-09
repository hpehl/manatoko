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
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.securityAddress;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.clientSslContextAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class SecurityTest extends AbstractRemoteCacheContainerTest {

    private static final String REMOTE_CACHE_CONTAINER_TO_BE_TESTED = "remote-cache-container-to-be-tested-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING = "remote-socket-binding-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING_CLUSTER = "remote-socket-binding-cluster-" + Random.name();
    private static final String REMOTE_CLUSTER = "remote-cluster-" + Random.name();
    private static final String SSL_CONTEXT = "client-ssl-context-" + Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING_CLUSTER);
        createRemoteCacheContainer(operations, REMOTE_CACHE_CONTAINER_TO_BE_TESTED, REMOTE_SOCKET_BINDING);
        operations.add(remoteClusterAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED, REMOTE_CLUSTER),
                Values.of(SOCKET_BINDINGS,
                        new ModelNodeGenerator.ModelNodeListBuilder().addAll(REMOTE_SOCKET_BINDING_CLUSTER).build()));
        operations.add(clientSslContextAddress(SSL_CONTEXT));
        new Administration(client).reloadIfRequired();
    }

    @BeforeEach
    void initPage() {
        page.navigate("name", REMOTE_CACHE_CONTAINER_TO_BE_TESTED);
        console.verticalNavigation().selectPrimary("security-item");
    }

    @Test
    void editSSLContext() throws Exception {
        crudOperations.update(securityAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED), page.getSecurityForm(),
                "ssl-context",
                SSL_CONTEXT);
    }
}
