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
package org.jboss.hal.testsuite.test.configuration.web;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddKeyManager;
import org.jboss.hal.testsuite.command.AddSocketBinding;
import org.jboss.hal.testsuite.command.BindPublicInterface;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.FilterPage;
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

import static org.jboss.hal.dmr.ModelDescriptionConstants.IO_THREADS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.KEY_MANAGER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.DEFAULT;
import static org.jboss.hal.testsuite.fixtures.IOFixtures.WORKER_CREATE;
import static org.jboss.hal.testsuite.fixtures.IOFixtures.workerAddress;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.KEY_MANAGER_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.SERVER_SSL_CREATE;
import static org.jboss.hal.testsuite.fixtures.SecurityFixtures.serverSslContextAddress;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.FILTER_CREATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.FILTER_DELETE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.FILTER_UPDATE;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.MANAGEMENT_SOCKET_BINDING;
import static org.jboss.hal.testsuite.fixtures.WebFixtures.modClusterAddress;

@Manatoko
@Testcontainers
public class ModClusterFilterTest {

    static final String SOCKET_BINDING_NAME = Random.name();
    static final String SOCKET_BINDING_UPDATE = Random.name();
    static final String ADVERTISE_SOCKET_BINDING_NAME = Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(DEFAULT);
    static OnlineManagementClient client;
    static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        client = wildFly.managementClient();
        operations = new Operations(client);

        // by default the public interface is bound to "0.0.0.0"
        // which leads to an error when creating mod-cluster filters
        client.apply(new BindPublicInterface("127.0.0.1"));
        new Administration(client).reload();

        client.apply(new AddSocketBinding(SOCKET_BINDING_NAME));
        client.apply(new AddSocketBinding(SOCKET_BINDING_UPDATE));
        client.apply(new AddSocketBinding(ADVERTISE_SOCKET_BINDING_NAME));
        client.apply(new AddKeyManager(KEY_MANAGER_CREATE));
        operations.add(workerAddress(WORKER_CREATE), Values.empty().and(IO_THREADS, 11));
        operations.add(serverSslContextAddress(SERVER_SSL_CREATE), Values.of(KEY_MANAGER, KEY_MANAGER_CREATE));
        operations.add(modClusterAddress(FILTER_UPDATE), Values.of(MANAGEMENT_SOCKET_BINDING, SOCKET_BINDING_NAME));
        operations.add(modClusterAddress(FILTER_DELETE), Values.of(MANAGEMENT_SOCKET_BINDING, SOCKET_BINDING_NAME));
    }

    @Inject Console console;
    @Page FilterPage page;
    @Inject CrudOperations crud;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.build("undertow-mod-cluster", "item"));
        table = page.getModClusterFilterTable();
        form = page.getModClusterFilterForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(modClusterAddress(FILTER_CREATE), table, f -> {
            f.text(NAME, FILTER_CREATE);
            f.text(MANAGEMENT_SOCKET_BINDING, SOCKET_BINDING_NAME);
        });
    }

    @Test
    public void editAdvertiseFrequency() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "advertise-frequency", Random.number());
    }

    @Test
    public void editAdvertisePath() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "advertise-path");
    }

    @Test
    public void editAdvertiseProtocol() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "advertise-protocol");
    }

    @Test
    public void editAdvertiseSocketBinding() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "advertise-socket-binding", ADVERTISE_SOCKET_BINDING_NAME);
    }

    @Test
    public void editBrokenNodeTimeout() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "broken-node-timeout", Random.number());
    }

    @Test
    public void editCachedConnectionsPerThread() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "cached-connections-per-thread", Random.number());
    }

    @Test
    public void editConnectionIdleTimeout() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "connection-idle-timeout", Random.number());
    }

    @Test
    public void editConnectionsPerThread() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "connections-per-thread", Random.number());
    }

    @Test
    public void toggleEnableHttp2() throws Exception {
        boolean enableHttp2 = operations.readAttribute(modClusterAddress(FILTER_UPDATE), "enable-http2").booleanValue();
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "enable-http2", !enableHttp2);
    }

    @Test
    public void editHealthCheckInterval() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "health-check-interval", Random.number());
    }

    @Test
    public void toggleHttp2EnablePush() throws Exception {
        boolean http2EnablePush = operations.readAttribute(modClusterAddress(FILTER_UPDATE), "http2-enable-push")
                .booleanValue();
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "http2-enable-push", !http2EnablePush);
    }

    @Test
    public void editHttp2HeaderTableSize() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "http2-header-table-size", Random.number());
    }

    @Test
    public void editHttp2InitialWindowSize() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "http2-initial-window-size", Random.number());
    }

    @Test
    public void editHttp2MaxConcurrentStreams() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "http2-max-concurrent-streams", Random.number());
    }

    @Test
    public void editHttp2MaxFrameSize() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "http2-max-frame-size", Random.number(1, Integer.MAX_VALUE));
    }

    @Test
    public void editHttp2MaxHeaderListSize() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "http2-max-header-list-size", Random.number());
    }

    @Test
    public void editManagementAccessPredicate() throws Exception {
        String predicate = "secure";
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "management-access-predicate", predicate);
    }

    @Test
    public void editManagementSocketBinding() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, MANAGEMENT_SOCKET_BINDING, SOCKET_BINDING_UPDATE);
    }

    @Test
    public void editMaxAjpPacketSize() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "max-ajp-packet-size", Random.number());
    }

    @Test
    public void editMaxRequestTime() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "max-request-time", Random.number());
    }

    @Test
    public void editMaxRetries() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "max-retries", Random.number());
    }

    @Test
    public void editRequestQueueSize() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "request-queue-size", Random.number());
    }

    @Test
    public void editSecurityKey() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "security-key");
    }

    @Test
    public void editSSLContext() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "ssl-context", SERVER_SSL_CREATE);
    }

    @Test
    public void toggleUseAlias() throws Exception {
        boolean useAlias = operations.readAttribute(modClusterAddress(FILTER_UPDATE),
                "use-alias").booleanValue();
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "use-alias", !useAlias);
    }

    @Test
    public void editWorker() throws Exception {
        table.select(FILTER_UPDATE);
        crud.update(modClusterAddress(FILTER_UPDATE), form, "worker", WORKER_CREATE);
    }

    @Test
    void delete() throws Exception {
        table.select(FILTER_UPDATE);
        crud.delete(modClusterAddress(FILTER_DELETE), table, FILTER_DELETE);
    }
}
