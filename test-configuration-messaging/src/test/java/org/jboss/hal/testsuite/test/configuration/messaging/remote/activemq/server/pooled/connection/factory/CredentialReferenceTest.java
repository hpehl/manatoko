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
package org.jboss.hal.testsuite.test.configuration.messaging.remote.activemq.server.pooled.connection.factory;

import java.io.IOException;

import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.JGroupsFixtures;
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
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.RemoteActiveMQServer;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class CredentialReferenceTest extends AbstractPooledConnectionFactoryTest {

    private static final String POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_UPDATE = "pooled-connection-factory-with-credential-reference-to-update-"
            + Random.name();
    private static final String POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_CREATE = "pooled-connection-factory-with-credential-reference-to-create-"
            + Random.name();
    private static final String POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_DELETE = "pooled-connection-factory-with-credential-reference-to-delete-"
            + Random.name();
    private static final String DISCOVERY_GROUP = "discovery-group-" + Random.name();
    private static final String JGROUPS_CHANNEL = "jgroups-channel-" + Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(JGroupsFixtures.channelAddress(JGROUPS_CHANNEL),
                Values.of(ModelDescriptionConstants.STACK, "tcp"))
                .assertSuccess();
        createDiscoveryGroup(operations, DISCOVERY_GROUP, JGROUPS_CHANNEL);
        new Administration(client).reloadIfRequired();
        createPooledConnectionFactory(operations, POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_CREATE,
                DISCOVERY_GROUP);
        createPooledConnectionFactory(operations, POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_DELETE,
                DISCOVERY_GROUP);
        createPooledConnectionFactory(operations, POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_UPDATE,
                DISCOVERY_GROUP);
        addCredentialReferenceToPooledConnectionFactory(operations,
                POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_DELETE);
        addCredentialReferenceToPooledConnectionFactory(operations,
                POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_UPDATE);
    }

    private static void addCredentialReferenceToPooledConnectionFactory(Operations operations, String connectionFactory)
            throws IOException {
        operations.writeAttribute(RemoteActiveMQServer.pooledConnectionFactoryAddress(connectionFactory),
                "credential-reference.clear-text", Random.name()).assertSuccess();
    }

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary("msg-remote-activemq-pooled-connection-factory-item");
    }

    @Test
    void createCredentialReference() throws Exception {
        String clearText = Random.name();
        page.getPooledConnectionFactoryTable().select(POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_CREATE);
        crudOperations.createSingleton(
                RemoteActiveMQServer.pooledConnectionFactoryAddress(
                        POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_CREATE),
                page.getPooledConnectionFactoryCredentialReferenceForm(), formFragment -> {
                    formFragment.text(ModelDescriptionConstants.CLEAR_TEXT, clearText);
                }, resourceVerifier -> resourceVerifier.verifyAttribute("credential-reference.clear-text", clearText));
    }

    @Test
    void removeCredentialReference() throws Exception {
        page.getPooledConnectionFactoryTable().select(POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_DELETE);
        crudOperations.deleteSingleton(
                RemoteActiveMQServer.pooledConnectionFactoryAddress(
                        POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_DELETE),
                page.getPooledConnectionFactoryCredentialReferenceForm(),
                resourceVerifier -> resourceVerifier.verifyAttributeIsUndefined("credential-reference"));
    }

    @Test
    void editStore() throws Exception {
        String store = Random.name();
        page.getPooledConnectionFactoryTable().select(POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_UPDATE);
        crudOperations.update(
                RemoteActiveMQServer.pooledConnectionFactoryAddress(
                        POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_UPDATE),
                page.getPooledConnectionFactoryCredentialReferenceForm(), formFragment -> {
                    formFragment.text(ModelDescriptionConstants.STORE, store);
                    formFragment.text(ModelDescriptionConstants.ALIAS, Random.name());
                    formFragment.clear(ModelDescriptionConstants.CLEAR_TEXT);
                }, resourceVerifier -> resourceVerifier.verifyAttribute("credential-reference.store", store));
    }

    @Test
    void editAlias() throws Exception {
        String alias = Random.name();
        page.getPooledConnectionFactoryTable().select(POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_UPDATE);
        crudOperations.update(
                RemoteActiveMQServer.pooledConnectionFactoryAddress(
                        POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_UPDATE),
                page.getPooledConnectionFactoryCredentialReferenceForm(), formFragment -> {
                    formFragment.text(ModelDescriptionConstants.STORE, Random.name());
                    formFragment.text(ModelDescriptionConstants.ALIAS, alias);
                    formFragment.clear(ModelDescriptionConstants.CLEAR_TEXT);
                }, resourceVerifier -> resourceVerifier.verifyAttribute("credential-reference.alias", alias));
    }

    @Test
    void editClearText() throws Exception {
        String clearText = Random.name();
        page.getPooledConnectionFactoryTable().select(POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_UPDATE);
        crudOperations.update(
                RemoteActiveMQServer.pooledConnectionFactoryAddress(
                        POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_UPDATE),
                page.getPooledConnectionFactoryCredentialReferenceForm(), formFragment -> {
                    formFragment.clear(ModelDescriptionConstants.STORE);
                    formFragment.clear(ModelDescriptionConstants.ALIAS);
                    formFragment.text(ModelDescriptionConstants.CLEAR_TEXT, clearText);
                }, resourceVerifier -> resourceVerifier.verifyAttribute("credential-reference.clear-text", clearText));
    }

    @Test
    void editType() throws Exception {
        String type = Random.name();
        page.getPooledConnectionFactoryTable().select(POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_UPDATE);
        crudOperations.update(
                RemoteActiveMQServer.pooledConnectionFactoryAddress(
                        POOLED_CONNECTION_FACTORY_CREDENTIAL_REFERENCE_UPDATE),
                page.getPooledConnectionFactoryCredentialReferenceForm(), formFragment -> {
                    formFragment.clear(ModelDescriptionConstants.STORE);
                    formFragment.clear(ModelDescriptionConstants.ALIAS);
                    formFragment.text(ModelDescriptionConstants.CLEAR_TEXT, Random.name());
                    formFragment.text("type", type);
                }, resourceVerifier -> resourceVerifier.verifyAttribute("credential-reference.type", type));
    }
}
