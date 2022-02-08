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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.store;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddRemoteSocketBinding;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.DataSourceFixtures;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.model.AvailablePortFinder;
import org.jboss.hal.testsuite.model.ModelNodeGenerator;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.page.configuration.ScatteredCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.commands.datasources.AddDataSource;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.JGROUPS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TRANSPORT;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SOCKET_BINDINGS;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.binaryJDBCStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.customStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.fileStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.hotrodStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.jdbcStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.remoteCacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class StoreTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE_CUSTOM_STORE = "scattered-cache-with-custom-store-to-be-created-"
            + Random.name();
    private static final String SCATTERED_CACHE_FILE_STORE = "scattered-cache-with-file-store-to-be-created-" + Random.name();
    private static final String SCATTERED_CACHE_JDBC_STORE = "scattered-cache-with-jdbc-store-to-be-created-" + Random.name();
    private static final String SCATTERED_CACHE_BINARY_JDBC_STORE = "scattered-cache-with-binary-jdbc-store-to-be-created-"
            + Random.name();
    private static final String SCATTERED_CACHE_HOTROD_STORE = "scattered-cache-with-hotrod-store-to-be-created-"
            + Random.name();
    private static final String DATA_SOURCE_JDBC = "data-source-for-jdbc-store-" + Random.name();
    private static final String DATA_SOURCE_BINARY_JDBC = "data-source-for-binary-jdbc-store-" + Random.name();
    private static final String DATA_SOURCE_MIXED_JDBC = "data-source-for-mixed-jdbc-store-" + Random.name();
    private static final String REMOTE_CACHE_CONTAINER_HOTROD = "remote-cache-container-for-hotrod-store-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING_HOTROD = "remote-socket-binding-for-hotrod-store-" + Random.name();
    private static final String REMOTE_CLUSTER_HOTROD = Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);
    private static OnlineManagementClient client;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and(TRANSPORT, JGROUPS));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE_FILE_STORE));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE_CUSTOM_STORE));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE_JDBC_STORE));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE_BINARY_JDBC_STORE));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE_HOTROD_STORE));
        client.apply(
                new AddDataSource.Builder<>(DATA_SOURCE_JDBC).driverName("h2").jndiName(Random.jndiName())
                        .connectionUrl(
                                DataSourceFixtures.h2ConnectionUrl(Random.name()))
                        .build());
        client.apply(
                new AddDataSource.Builder<>(DATA_SOURCE_BINARY_JDBC).driverName("h2")
                        .jndiName(Random.jndiName())
                        .connectionUrl(
                                DataSourceFixtures.h2ConnectionUrl(Random.name()))
                        .build());
        client.apply(
                new AddDataSource.Builder<>(DATA_SOURCE_MIXED_JDBC).driverName("h2")
                        .jndiName(Random.jndiName())
                        .connectionUrl(
                                DataSourceFixtures.h2ConnectionUrl(Random.name()))
                        .build());
        client.apply(new AddRemoteSocketBinding(REMOTE_SOCKET_BINDING_HOTROD, "localhost",
                AvailablePortFinder.getNextAvailableTCPPort()));
        operations.batch(new Batch().add(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_HOTROD),
                Values.of("default-remote-cluster", REMOTE_CLUSTER_HOTROD))
                .add(remoteCacheContainerAddress(REMOTE_CACHE_CONTAINER_HOTROD).and("remote-cluster",
                        REMOTE_CLUSTER_HOTROD),
                        Values.of(SOCKET_BINDINGS,
                                new ModelNodeGenerator.ModelNodeListBuilder().addAll(REMOTE_SOCKET_BINDING_HOTROD)
                                        .build())));
    }

    @Inject Console console;
    @Page ScatteredCachePage page;

    void navigate(String scatteredCache) {
        page.navigate(StoreTest.CACHE_CONTAINER, scatteredCache);
        console.verticalNavigation().selectPrimary("scattered-cache-store-item");
    }

    @Test
    void addCustomStore() throws Exception {
        navigate(SCATTERED_CACHE_CUSTOM_STORE);
        String customClass = Random.name();
        page.getSelectStoreDropdown().selectExact("Custom", "custom");
        page.getEmptyStoreForm().mainAction();
        AddResourceDialogFragment addResourceDialog = console.addResourceDialog();
        addResourceDialog.getForm().text("class", customClass);
        addResourceDialog.add();
        console.verifySuccess();
        new ResourceVerifier(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE_CUSTOM_STORE), client).verifyExists();
    }

    @Test
    void addFileStore() throws Exception {
        navigate(SCATTERED_CACHE_FILE_STORE);
        page.getSelectStoreDropdown().selectExact("File", "file");
        page.getEmptyStoreForm().mainAction();
        console.verifySuccess();
        new ResourceVerifier(fileStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE_FILE_STORE), client).verifyExists();
    }

    @Test
    void addJDBCStore() throws Exception {
        navigate(SCATTERED_CACHE_JDBC_STORE);
        page.getSelectStoreDropdown().selectExact("JDBC", "jdbc");
        page.getEmptyStoreForm().mainAction();
        AddResourceDialogFragment addResourceDialogFragment = console.addResourceDialog();
        addResourceDialogFragment.getForm().text("data-source", DATA_SOURCE_JDBC);
        addResourceDialogFragment.add();
        console.verifySuccess();
        new ResourceVerifier(jdbcStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE_JDBC_STORE),
                client).verifyExists();
    }

    @Test
    void addBinaryJDBCStore() throws Exception {
        navigate(SCATTERED_CACHE_BINARY_JDBC_STORE);
        page.getSelectStoreDropdown().selectExact("Binary JDBC", "binary-jdbc");
        page.getEmptyStoreForm().mainAction();
        AddResourceDialogFragment addResourceDialogFragment = console.addResourceDialog();
        addResourceDialogFragment.getForm().text("data-source", DATA_SOURCE_BINARY_JDBC);
        addResourceDialogFragment.add();
        console.verifySuccess();
        new ResourceVerifier(binaryJDBCStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE_BINARY_JDBC_STORE),
                client).verifyExists();
    }

    @Test
    void addHotrodStore() throws Exception {
        navigate(SCATTERED_CACHE_HOTROD_STORE);
        page.getSelectStoreDropdown().selectExact("Hot Rod", "hotrod");
        page.getEmptyStoreForm().mainAction();
        AddResourceDialogFragment addResourceDialogFragment = console.addResourceDialog();
        addResourceDialogFragment.getForm().text("remote-cache-container", REMOTE_CACHE_CONTAINER_HOTROD);
        addResourceDialogFragment.add();
        console.verifySuccess();
        new ResourceVerifier(hotrodStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE_HOTROD_STORE), client).verifyExists();
    }
}
