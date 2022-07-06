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
package org.jboss.hal.testsuite.page.configuration;

import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.testsuite.fragment.EmptyState;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.BasePage;
import org.jboss.hal.testsuite.page.Place;
import org.openqa.selenium.support.FindBy;

import static org.jboss.hal.resources.Ids.FORM;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_POLICY_EMPTY;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_REPLICATION_COLOCATED;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_REPLICATION_LIVE_ONLY;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_REPLICATION_PRIMARY;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_REPLICATION_SECONDARY;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_SHARED_STORE_COLOCATED;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_SHARED_STORE_PRIMARY;
import static org.jboss.hal.resources.Ids.MESSAGING_HA_SHARED_STORE_SECONDARY;

@Place(NameTokens.MESSAGING_SERVER_HA_POLICY)
public class MessagingServerHaPolicyPage extends BasePage {

    @FindBy(id = MESSAGING_HA_POLICY_EMPTY) private EmptyState emptyState;
    @FindBy(id = MESSAGING_HA_REPLICATION_LIVE_ONLY + "-" + FORM) private FormFragment replicationLiveOnlyForm;
    @FindBy(id = MESSAGING_HA_REPLICATION_PRIMARY + "-" + FORM) private FormFragment replicationPrimaryForm;
    @FindBy(id = MESSAGING_HA_REPLICATION_SECONDARY + "-" + FORM) private FormFragment replicationSecondaryForm;
    @FindBy(id = MESSAGING_HA_REPLICATION_COLOCATED + "-" + FORM) private FormFragment replicationColocatedForm;
    @FindBy(id = MESSAGING_HA_SHARED_STORE_PRIMARY + "-" + FORM) private FormFragment sharedStorePrimaryForm;
    @FindBy(id = MESSAGING_HA_SHARED_STORE_SECONDARY + "-" + FORM) private FormFragment sharedStoreSecondaryForm;
    @FindBy(id = MESSAGING_HA_SHARED_STORE_COLOCATED + "-" + FORM) private FormFragment sharedStoreColocatedForm;

    public EmptyState getEmptyState() {
        return emptyState;
    }

    public FormFragment getReplicationLiveOnlyForm() {
        return replicationLiveOnlyForm;
    }

    public FormFragment getReplicationPrimaryForm() {
        return replicationPrimaryForm;
    }

    public FormFragment getReplicationSecondaryForm() {
        return replicationSecondaryForm;
    }

    public FormFragment getReplicationColocatedForm() {
        return replicationColocatedForm;
    }

    public FormFragment getSharedStorePrimaryForm() {
        return sharedStorePrimaryForm;
    }

    public FormFragment getSharedStoreSecondaryForm() {
        return sharedStoreSecondaryForm;
    }

    public FormFragment getSharedStoreColocatedForm() {
        return sharedStoreColocatedForm;
    }
}
