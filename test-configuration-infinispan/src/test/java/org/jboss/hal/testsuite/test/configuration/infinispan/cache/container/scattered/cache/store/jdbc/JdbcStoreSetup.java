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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.store.jdbc;

import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.DataSourceFixtures;
import org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.store.StoreSetup;
import org.wildfly.extras.creaper.commands.datasources.AddDataSource;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.JGROUPS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TRANSPORT;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.jdbcStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

interface JdbcStoreSetup {

    String DATA_SOURCE_CREATE = "data-source-to-be-created-" + Random.name();
    String DATA_SOURCE_EDIT = "data-source-to-be-edited-" + Random.name();

    static void setup(WildFlyContainer wildFly, StoreSetup setup) throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);

        client.apply(new AddDataSource.Builder<>(DATA_SOURCE_CREATE).driverName("h2").jndiName(Random.jndiName())
                .connectionUrl(DataSourceFixtures.h2ConnectionUrl(Random.name()))
                .build());
        client.apply(new AddDataSource.Builder<>(DATA_SOURCE_EDIT).driverName("h2").jndiName(Random.jndiName())
                .connectionUrl(DataSourceFixtures.h2ConnectionUrl(Random.name()))
                .build());

        operations.add(cacheContainerAddress(CC_CREATE));
        operations.add(cacheContainerAddress(CC_CREATE).and(TRANSPORT, JGROUPS));
        operations.add(scatteredCacheAddress(CC_CREATE, SC_CREATE));
        operations.headers(Values.of("allow-resource-service-restart", true))
                .add(jdbcStoreAddress(CC_CREATE, SC_CREATE),
                        Values.of("data-source", DATA_SOURCE_CREATE));
        setup.accept(operations);
    }
}