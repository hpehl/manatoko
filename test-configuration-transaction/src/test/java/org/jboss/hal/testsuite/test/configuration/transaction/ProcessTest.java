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
package org.jboss.hal.testsuite.test.configuration.transaction;

import java.io.IOException;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.command.AddLocalSocketBinding;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.TransactionFixtures;
import org.jboss.hal.testsuite.page.configuration.TransactionPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.junit.jupiter.api.Assertions.fail;

@Manatoko
@Testcontainers
class ProcessTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, STANDALONE);
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        operations = new Operations(wildFly.managementClient());
        wildFly.managementClient().apply(new AddLocalSocketBinding(TransactionFixtures.PROCESS_SOCKET_BINDING_CREATE));
        wildFly.managementClient()
                .apply(new AddLocalSocketBinding(TransactionFixtures.PROCESS_SOCKET_BINDING_WITH_PROCESS_ID_UUID));
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page TransactionPage page;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation()
                .selectPrimary(Ids.build("tx", "process", "item"));
    }

    @Test
    void toggleProcessIDUUID() throws Exception {
        boolean processIDUUID = operations
                .readAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS, TransactionFixtures.PROCESS_ID_UUID)
                .booleanValue();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getProcessForm(), formFragment -> {
            formFragment.flip(TransactionFixtures.PROCESS_ID_UUID, !processIDUUID);
            if (processIDUUID) {
                formFragment.text(TransactionFixtures.PROCESS_ID_SOCKET_BINDING,
                        TransactionFixtures.PROCESS_SOCKET_BINDING_WITH_PROCESS_ID_UUID);
            } else {
                formFragment.text(TransactionFixtures.PROCESS_ID_SOCKET_BINDING, "");
            }
        },
                resourceVerifier -> resourceVerifier.verifyAttribute(TransactionFixtures.PROCESS_ID_UUID,
                        !processIDUUID));
    }

    @Test
    void editProcessIdSocketBinding() throws Exception {
        boolean processIDUUID = operations
                .readAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS, TransactionFixtures.PROCESS_ID_UUID)
                .booleanValue();
        crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getProcessForm(), formFragment -> {
            if (processIDUUID) {
                formFragment.flip(TransactionFixtures.PROCESS_ID_UUID, false);
            }
            formFragment.text(TransactionFixtures.PROCESS_ID_SOCKET_BINDING,
                    TransactionFixtures.PROCESS_SOCKET_BINDING_CREATE);
        },
                resourceVerifier -> resourceVerifier.verifyAttribute(TransactionFixtures.PROCESS_ID_SOCKET_BINDING,
                        TransactionFixtures.PROCESS_SOCKET_BINDING_CREATE));
    }

    @Test
    void editProcessIDSocketMaxPorts() throws Exception {
        prepareProcessID();
        try {
            crudOperations.update(TransactionFixtures.TRANSACTIONS_ADDRESS, page.getProcessForm(),
                    TransactionFixtures.PROCESS_ID_SOCKET_MAX_PORTS, Random.number());
        } catch (TimeoutException e) {
            fail("HAL-1454");
        }
    }

    private void prepareProcessID() throws IOException {
        Batch batch = new Batch().undefineAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS,
                TransactionFixtures.PROCESS_ID_UUID)
                .writeAttribute(TransactionFixtures.TRANSACTIONS_ADDRESS, TransactionFixtures.PROCESS_ID_SOCKET_BINDING,
                        TransactionFixtures.PROCESS_SOCKET_BINDING_CREATE);
        operations.batch(batch);
    }
}
