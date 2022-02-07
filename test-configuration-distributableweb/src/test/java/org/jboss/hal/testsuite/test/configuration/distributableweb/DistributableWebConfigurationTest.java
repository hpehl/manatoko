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
package org.jboss.hal.testsuite.test.configuration.distributableweb;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.DistributableWebPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CACHE_CONTAINER;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.DEFAULT_SESSION_MANAGEMENT;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.DEFAULT_SSO_MANAGEMENT;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.GRANULARITY;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.INFINISPAN_SESSION_REF;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.INFINISPAN_SSO_REF;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.SESSION;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.SUBSYSTEM_ADDRESS;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.infinispanSSOAddress;
import static org.jboss.hal.testsuite.fixtures.DistributableWebFixtures.infinispanSessionAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.CC_READ;
import static org.jboss.hal.testsuite.test.configuration.distributableweb.DistributableWebOperations.addCacheContainer;

@Manatoko
@Testcontainers
class DistributableWebConfigurationTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        addCacheContainer(client, operations, CC_READ);
        operations.add(infinispanSessionAddress(INFINISPAN_SESSION_REF), Values.of(CACHE_CONTAINER, CC_READ)
                .and(GRANULARITY, SESSION));
        operations.add(infinispanSSOAddress(INFINISPAN_SSO_REF), Values.of(CACHE_CONTAINER, CC_READ));
    }

    @Page DistributableWebPage page;
    @Inject CrudOperations crud;
    @Inject Console console;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary("dw-configuration-item");
        form = page.getConfigurationForm();
    }

    @Test
    void update() throws Exception {
        crud.update(SUBSYSTEM_ADDRESS, form,
                f -> {
                    f.text(DEFAULT_SESSION_MANAGEMENT, INFINISPAN_SESSION_REF);
                    f.text(DEFAULT_SSO_MANAGEMENT, INFINISPAN_SSO_REF);
                },
                verifier -> {
                    verifier.verifyAttribute(DEFAULT_SESSION_MANAGEMENT, INFINISPAN_SESSION_REF);
                    verifier.verifyAttribute(DEFAULT_SSO_MANAGEMENT, INFINISPAN_SSO_REF);
                });
    }
}
