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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.store.hotrod;

import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.ScatteredCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.store.AbstractBehindTest;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.BEHIND;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.SC_CREATE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.WRITE;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.hotrodStoreAddress;

@Manatoko
@Testcontainers
class BehindTest extends AbstractBehindTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);

    @BeforeAll
    static void setupModel() throws Exception {
        HotrodStoreSetup.setup(wildFly,
                operations -> operations.add(hotrodStoreAddress(CC_CREATE, SC_CREATE).and(WRITE, BEHIND)));
    }

    @Override
    protected void prepareStore(ScatteredCachePage page) {
        page.selectHotrodStoreWriteBehaviour();
    }

    @Override
    protected Address storeAddress() {
        return hotrodStoreAddress(CC_CREATE, SC_CREATE);
    }

    @Override
    protected FormFragment form(ScatteredCachePage page) {
        return page.getHotrodStoreWriteBehindForm();
    }
}
