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
package org.jboss.hal.testsuite.test.configuration.io;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.IOPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.IO_THREADS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.IOFixtures.WORKER_CREATE;
import static org.jboss.hal.testsuite.fixtures.IOFixtures.WORKER_DELETE;
import static org.jboss.hal.testsuite.fixtures.IOFixtures.WORKER_READ;
import static org.jboss.hal.testsuite.fixtures.IOFixtures.WORKER_UPDATE;
import static org.jboss.hal.testsuite.fixtures.IOFixtures.workerAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Manatoko
@Testcontainers
class WorkerTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(workerAddress(WORKER_READ), Values.empty().and(IO_THREADS, 11));
        operations.add(workerAddress(WORKER_UPDATE), Values.empty().and(IO_THREADS, 123));
        operations.add(workerAddress(WORKER_DELETE), Values.empty().and(IO_THREADS, 321));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page IOPage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary("io-worker-item");

        form = page.getWorkerForm();
        table = page.getWorkerTable();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(workerAddress(WORKER_CREATE), table,
                form -> {
                    form.text(NAME, WORKER_CREATE);
                    form.number(IO_THREADS, 12);
                    form.number("stack-size", 1024);
                    form.number("task-keepalive", 2233);
                    form.number("task-max-threads", 12345);
                });
    }

    @Test
    void read() {
        table.select(WORKER_READ);
        assertEquals(11, form.intValue(IO_THREADS));
    }

    @Test
    void update() throws Exception {
        table.select(WORKER_UPDATE);
        crud.update(workerAddress(WORKER_UPDATE), form, IO_THREADS, Random.number());
    }

    @Test
    void updateInvalidMaxThreads() {
        table.select(WORKER_UPDATE);
        crud.updateWithError(form, IO_THREADS, -1);
    }

    @Test
    void delete() throws Exception {
        crud.delete(workerAddress(WORKER_DELETE), table, WORKER_DELETE);
    }
}
