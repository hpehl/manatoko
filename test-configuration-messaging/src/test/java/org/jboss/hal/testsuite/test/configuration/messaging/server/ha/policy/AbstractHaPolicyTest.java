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
package org.jboss.hal.testsuite.test.configuration.messaging.server.ha.policy;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.fragment.WizardFragment;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
import org.jboss.hal.testsuite.page.configuration.MessagingServerHaPolicyPage;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.jboss.hal.resources.Ids.MESSAGING_HA_REPLICATION;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_REPLICATION_COLOCATED;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_REPLICATION_LIVE_ONLY;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_REPLICATION_PRIMARY;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_REPLICATION_SECONDARY;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_SHARED_STORE;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_SHARED_STORE_COLOCATED;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_SHARED_STORE_PRIMARY;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_SHARED_STORE_SECONDARY;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.SRV_UPDATE;
import static org.jboss.hal.testsuite.fixtures.MessagingFixtures.haPolicyAddress;

abstract class AbstractHaPolicyTest {

    @Page protected MessagingServerHaPolicyPage page;
    @Inject protected Console console;
    @Inject protected CrudOperations crudOperations;
    protected ColumnFragment column;
    protected WizardFragment wizard;

    enum HAPolicy {
        LIVE_ONLY(MESSAGING_HA_REPLICATION, MESSAGING_HA_REPLICATION_LIVE_ONLY,
                haPolicyAddress(SRV_UPDATE, ModelDescriptionConstants.LIVE_ONLY)),

        REPLICATION_PRIMARY(MESSAGING_HA_REPLICATION, MESSAGING_HA_REPLICATION_PRIMARY,
                haPolicyAddress(SRV_UPDATE, ModelDescriptionConstants.REPLICATION_PRIMARY)),

        REPLICATION_SECONDARY(MESSAGING_HA_REPLICATION, MESSAGING_HA_REPLICATION_SECONDARY,
                haPolicyAddress(SRV_UPDATE, ModelDescriptionConstants.REPLICATION_SECONDARY)),

        REPLICATION_COLOCATED(MESSAGING_HA_REPLICATION, MESSAGING_HA_REPLICATION_COLOCATED,
                haPolicyAddress(SRV_UPDATE, ModelDescriptionConstants.REPLICATION_COLOCATED)),

        SHARED_STORE_PRIMARY(MESSAGING_HA_SHARED_STORE, MESSAGING_HA_SHARED_STORE_PRIMARY,
                haPolicyAddress(SRV_UPDATE, ModelDescriptionConstants.SHARED_STORE_PRIMARY)),

        SHARED_STORE_SECONDARY(MESSAGING_HA_SHARED_STORE, MESSAGING_HA_SHARED_STORE_SECONDARY,
                haPolicyAddress(SRV_UPDATE, ModelDescriptionConstants.SHARED_STORE_SECONDARY)),

        SHARED_STORE_COLOCATED(MESSAGING_HA_SHARED_STORE, MESSAGING_HA_SHARED_STORE_COLOCATED,
                haPolicyAddress(SRV_UPDATE, ModelDescriptionConstants.SHARED_STORE_COLOCATED));

        final String basicStrategy;
        final String serverRole;
        final Address haPolicyAddress;

        HAPolicy(String basicStrategy, String serverRole, Address haPolicyAddress) {
            this.basicStrategy = basicStrategy;
            this.serverRole = serverRole;
            this.haPolicyAddress = haPolicyAddress;
        }

        public void create(FinderTest.HAPolicyConsumer consumer) throws Exception {
            consumer.accept(this);
        }

        public void remove(FinderTest.HAPolicyConsumer consumer) throws Exception {
            consumer.accept(this);
        }
    }

    @FunctionalInterface
    interface HAPolicyConsumer {

        void accept(HAPolicy policy) throws Exception;
    }
}
