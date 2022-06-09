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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.backup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.ScatteredCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.ENABLED;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.backupAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

@Manatoko
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
class BackupsTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE = "scattered-cache-" + Random.name();
    private static final String BACKUP_CREATE = "scattered-cache-with-backup-to-be-created-" + Random.name();
    private static final String BACKUP_DELETE = "scattered-cache-with-backup-to-be-deleted-" + Random.name();
    private static final String BACKUP_EDIT = "scattered-cache-with-backup-to-be-edited-" + Random.name();

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(_26_1, FULL_HA);
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and("transport", "jgroups"));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE));
        operations.add(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_DELETE));
        operations.add(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_EDIT));
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page ScatteredCachePage page;
    TableFragment table;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate(CACHE_CONTAINER, SCATTERED_CACHE);
        console.verticalNavigation().selectPrimary("scattered-cache-backup-item");
        table = page.getBackupsTable();
        form = page.getBackupsForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crudOperations.create(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_CREATE), table, BACKUP_CREATE);
    }

    @Test
    void delete() throws Exception {
        crudOperations.delete(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_DELETE), table, BACKUP_DELETE);
    }

    @Test
    void editAfterFailures() throws Exception {
        table.select(BACKUP_EDIT);
        crudOperations.update(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_EDIT), form, "after-failures",
                Random.number());
    }

    @Test
    void toggleEnabled() throws Exception {
        console.waitNoNotification();
        table.select(BACKUP_EDIT);
        boolean enabled = operations.readAttribute(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_EDIT), "enabled")
                .booleanValue();
        crudOperations.update(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_EDIT), form, ENABLED, !enabled);
    }

    @Test
    void editFailurePolicy() throws Exception {
        String currentFailurePolicy = operations
                .readAttribute(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_EDIT), "failure-policy")
                .stringValue();
        List<String> failurePolicies = new ArrayList<>(Arrays.asList("IGNORE", "FAIL", "WARN", "CUSTOM"));
        failurePolicies.remove(currentFailurePolicy);
        String failurePolicy = failurePolicies.get(Random.number(0, failurePolicies.size()));
        table.select(BACKUP_EDIT);
        crudOperations.update(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_EDIT), form,
                formFragment -> formFragment.select("failure-policy", failurePolicy),
                resourceVerifier -> resourceVerifier.verifyAttribute("failure-policy", failurePolicy));
    }

    @Test
    void editMinWait() throws Exception {
        table.select(BACKUP_EDIT);
        crudOperations.update(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_EDIT), form, "min-wait",
                (long) Random.number());
    }

    @Test
    void editStrategy() throws Exception {
        String currentStrategy = operations
                .readAttribute(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_EDIT), "strategy")
                .stringValue();
        List<String> strategies = new ArrayList<>(Arrays.asList("SYNC", "ASYNC"));
        strategies.remove(currentStrategy);
        String strategy = strategies.get(0);
        table.select(BACKUP_EDIT);
        console.waitNoNotification();
        crudOperations.update(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_EDIT), form,
                formFragment -> formFragment.select("strategy", strategy),
                resourceVerifier -> resourceVerifier.verifyAttribute("strategy", strategy));
    }

    @Test
    void editTimeout() throws Exception {
        console.waitNoNotification();
        table.select(BACKUP_EDIT);
        crudOperations.update(backupAddress(CACHE_CONTAINER, SCATTERED_CACHE, BACKUP_EDIT), form,
                "timeout", 123L);
    }
}
