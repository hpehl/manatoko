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

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.model.ModelNodeGenerator;
import org.jboss.hal.testsuite.page.configuration.RemoteCacheContainerPage;
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
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SOCKET_BINDINGS;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.remoteCacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.remoteClusterAddress;
import static org.jboss.hal.testsuite.test.configuration.infinispan.remote.cache.container.RemoteCacheContainerCommons.createRemoteCacheContainer;
import static org.jboss.hal.testsuite.test.configuration.infinispan.remote.cache.container.RemoteCacheContainerCommons.createRemoteSocketBinding;

@Manatoko
@Testcontainers
class AttributesTest {

    private static final String REMOTE_CACHE_CONTAINER_TO_BE_TESTED = "remote-cache-container-to-be-tested-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING = "remote-socket-binding-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING_CLUSTER = "remote-socket-binding-cluster-" + Random.name();
    private static final String REMOTE_CLUSTER = "remote-cluster-" + Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);
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

    @Page RemoteCacheContainerPage page;
    @Inject Console console;
    @Inject CrudOperations crudOperations;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate("name", REMOTE_CACHE_CONTAINER_TO_BE_TESTED);
        console.verticalNavigation().selectPrimary("rcc-item");
        form = page.getConfigurationForm();
    }

    @Test
    void editDefaultRemoteCluster() throws Exception {
        crudOperations.update(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED), form,
                "default-remote-cluster", REMOTE_CLUSTER);
    }

    @Test
    void editModules() throws Exception {
        String module = Random.name();
        crudOperations.update(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED), form,
                formFragment -> formFragment.properties("modules").add(module),
                resourceVerifier -> resourceVerifier.verifyListAttributeContainsValue("modules", module));
    }

    @Test
    void toggleTCPKeepAlive() throws Exception {
        boolean tcpKeepAlive = operations.readAttribute(
                remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED), "tcp-keep-alive").booleanValue();
        crudOperations.update(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED), form, "tcp-keep-alive",
                !tcpKeepAlive);
    }

    @Test
    void toggleTCPNoDelay() throws Exception {
        boolean tcpNoDelay = operations.readAttribute(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED),
                "tcp-no-delay").booleanValue();
        crudOperations.update(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_TO_BE_TESTED), form, "tcp-no-delay",
                !tcpNoDelay);
    }
}
