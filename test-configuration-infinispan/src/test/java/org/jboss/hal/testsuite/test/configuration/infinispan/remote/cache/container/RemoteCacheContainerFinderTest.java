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

import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.AddResourceDialogFragment;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
import org.jboss.hal.testsuite.model.ResourceVerifier;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

import static org.jboss.hal.dmr.ModelDescriptionConstants.INFINISPAN;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.REMOTE_CC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.REMOTE_CC_DELETE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.REMOTE_CC_READ;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.remoteCacheContainerAddress;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class RemoteCacheContainerFinderTest extends AbstractRemoteCacheContainerTest {

    private static final String REMOTE_SOCKET_BINDING_CREATE = "remote-socket-binding-create-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING_READ = "remote-socket-binding-read-" + Random.name();
    private static final String REMOTE_SOCKET_BINDING_DELETE = "remote-socket-binding-delete-" + Random.name();
    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, FULL_HA);
    private static OnlineManagementClient client;
    private static Administration administration;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        Operations operations = new Operations(client);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING_CREATE);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING_READ);
        createRemoteSocketBinding(client, REMOTE_SOCKET_BINDING_DELETE);
        createRemoteCacheContainer(operations, REMOTE_CC_READ, REMOTE_SOCKET_BINDING_READ);
        createRemoteCacheContainer(operations, REMOTE_CC_DELETE, REMOTE_SOCKET_BINDING_DELETE);
        administration = new Administration(client);
        administration.reloadIfRequired();
    }

    ColumnFragment column;

    @BeforeEach
    void setUp() {
        browser.navigate().refresh();
        column = console.finder(NameTokens.CONFIGURATION, configurationSubsystemPath(INFINISPAN))
                .column(Ids.CACHE_CONTAINER);
    }

    @Test
    void create() throws Exception {
        String defaultRemoteCluster = Random.name();
        column.dropdownAction("cc-add-actions", "rcc-add");
        AddResourceDialogFragment dialog = console.addResourceDialog();
        FormFragment formFragment = dialog.getForm();
        formFragment.text(NAME, REMOTE_CC_CREATE);
        formFragment.text("default-remote-cluster", defaultRemoteCluster);
        formFragment.list("socket-bindings").add(REMOTE_SOCKET_BINDING_CREATE);
        dialog.add();
        console.verifySuccess();
        assertTrue(column.containsItem(remoteCacheContainerId(REMOTE_CC_CREATE)));
        new ResourceVerifier(remoteCacheContainerAddress(REMOTE_CC_CREATE), client).verifyExists();
        administration.reloadIfRequired();
    }

    private String remoteCacheContainerId(String name) {
        return Ids.build("rcc", name);
    }

    @Test
    void read() {
        assertTrue(column.containsItem(remoteCacheContainerId(REMOTE_CC_READ)));
    }

    @Test
    void view() {
        column.selectItem(remoteCacheContainerId(REMOTE_CC_READ)).view();
        PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken("remote-cache-container").with("name", REMOTE_CC_READ)
                .build();
        console.verify(placeRequest);
    }

    @Test
    void delete() throws Exception {
        column.selectItem(remoteCacheContainerId(REMOTE_CC_DELETE)).dropdown().click("Remove");
        console.confirmationDialog().confirm();
        console.verifySuccess();
        assertFalse(column.containsItem(remoteCacheContainerId(REMOTE_CC_DELETE)));
        new ResourceVerifier(remoteCacheContainerAddress(REMOTE_CC_DELETE), client).verifyDoesNotExist();
        administration.reloadIfRequired();
    }
}
