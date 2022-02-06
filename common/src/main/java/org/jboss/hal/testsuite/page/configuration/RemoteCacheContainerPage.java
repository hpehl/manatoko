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
package org.jboss.hal.testsuite.page.configuration;

import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.fragment.TabsFragment;
import org.jboss.hal.testsuite.page.BasePage;
import org.jboss.hal.testsuite.page.Place;
import org.openqa.selenium.support.FindBy;

@Place("remote-cache-container")
public class RemoteCacheContainerPage extends BasePage {

    @FindBy(id = "rcc-tabs") private TabsFragment configurationTab;

    @FindBy(id = "rcc-configuration-form") private FormFragment configurationForm;

    @FindBy(id = "near-cache-invalidation-form") private FormFragment nearCacheForm;

    @FindBy(id = "rc-table_wrapper") private TableFragment remoteClusterTable;

    @FindBy(id = "rc-form") private FormFragment remoteClusterForm;

    @FindBy(id = "connection-pool-form") private FormFragment connectionPoolForm;

    @FindBy(id = "thread-pool-form") private FormFragment threadPoolForm;

    @FindBy(id = "security-form") private FormFragment securityForm;

    public TabsFragment getConfigurationTab() {
        return configurationTab;
    }

    public FormFragment getConfigurationForm() {
        configurationTab.select("rcc-configuration-tab");
        return configurationForm;
    }

    public FormFragment getNearCacheForm() {
        configurationTab.select("rcc-near-cache-tab");
        return nearCacheForm;
    }

    public TableFragment getRemoteClusterTable() {
        return remoteClusterTable;
    }

    public FormFragment getRemoteClusterForm() {
        return remoteClusterForm;
    }

    public FormFragment getConnectionPoolForm() {
        return connectionPoolForm;
    }

    public FormFragment getThreadPoolForm() {
        return threadPoolForm;
    }

    public FormFragment getSecurityForm() {
        return securityForm;
    }
}
