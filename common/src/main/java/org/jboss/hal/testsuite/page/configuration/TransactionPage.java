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

import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.BasePage;
import org.jboss.hal.testsuite.page.Place;
import org.openqa.selenium.support.FindBy;

@Place(NameTokens.TRANSACTIONS)
public class TransactionPage extends BasePage {

    @FindBy(id = "tx-attributes-form") private FormFragment configurationForm;

    @FindBy(id = "tx-process-form") private FormFragment processForm;

    @FindBy(id = "tx-recovery-form") private FormFragment recoveryForm;

    @FindBy(id = "tx-path-form") private FormFragment pathForm;

    @FindBy(id = "tx-jdbc-form") private FormFragment jdbcForm;

    public FormFragment getConfigurationForm() {
        return configurationForm;
    }

    public FormFragment getProcessForm() {
        return processForm;
    }

    public FormFragment getRecoveryForm() {
        return recoveryForm;
    }

    public FormFragment getPathForm() {
        return pathForm;
    }

    public FormFragment getJdbcForm() {
        return jdbcForm;
    }
}
