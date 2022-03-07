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
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.BasePage;
import org.jboss.hal.testsuite.page.Place;
import org.openqa.selenium.support.FindBy;

@Place(NameTokens.WEBSERVICES)
public class WebServicesPage extends BasePage {

    @FindBy(id = Ids.WEBSERVICES_FORM) private FormFragment webServicesConfigurationForm;

    @FindBy(id = Ids.WEBSERVICES_CLIENT_CONFIG + "-table_wrapper") private TableFragment clientConfigurationTable;

    @FindBy(id = Ids.WEBSERVICES_CLIENT_CONFIG + "-form") private FormFragment clientConfigurationForm;

    @FindBy(id = Ids.WEBSERVICES_CLIENT_CONFIG
            + "-handler-chain-table_wrapper") private TableFragment clientConfigurationHandlerChainTable;

    @FindBy(id = Ids.WEBSERVICES_CLIENT_CONFIG
            + "-handler-chain-form") private FormFragment clientConfigurationHandlerChainForm;

    @FindBy(id = Ids.WEBSERVICES_CLIENT_CONFIG
            + "-handler-table_wrapper") private TableFragment clientConfigurationHandlerTable;

    @FindBy(id = Ids.WEBSERVICES_CLIENT_CONFIG + "-handler-form") private FormFragment clientConfigurationHandlerForm;

    @FindBy(id = Ids.WEBSERVICES_ENDPOINT_CONFIG + "-table_wrapper") private TableFragment endpointConfigurationTable;

    @FindBy(id = Ids.WEBSERVICES_ENDPOINT_CONFIG + "-form") private FormFragment endpointConfigurationForm;

    @FindBy(id = Ids.WEBSERVICES_ENDPOINT_CONFIG
            + "-handler-chain-table_wrapper") private TableFragment endpointConfigurationHandlerChainTable;

    @FindBy(id = Ids.WEBSERVICES_ENDPOINT_CONFIG
            + "-handler-chain-form") private FormFragment endpointConfigurationHandlerChainForm;

    @FindBy(id = Ids.WEBSERVICES_ENDPOINT_CONFIG
            + "-handler-table_wrapper") private TableFragment endpointConfigurationHandlerTable;

    @FindBy(id = Ids.WEBSERVICES_ENDPOINT_CONFIG + "-handler-form") private FormFragment endpointConfigurationHandlerForm;

    public FormFragment getWebServicesConfigurationForm() {
        return webServicesConfigurationForm;
    }

    public TableFragment getClientConfigurationTable() {
        return clientConfigurationTable;
    }

    public FormFragment getClientConfigurationForm() {
        return clientConfigurationForm;
    }

    public TableFragment getClientConfigurationHandlerChainTable() {
        return clientConfigurationHandlerChainTable;
    }

    public FormFragment getClientConfigurationHandlerChainForm() {
        return clientConfigurationHandlerChainForm;
    }

    public TableFragment getClientConfigurationHandlerTable() {
        return clientConfigurationHandlerTable;
    }

    public FormFragment getClientConfigurationHandlerForm() {
        return clientConfigurationHandlerForm;
    }

    public TableFragment getEndpointConfigurationTable() {
        return endpointConfigurationTable;
    }

    public FormFragment getEndpointConfigurationForm() {
        return endpointConfigurationForm;
    }

    public TableFragment getEndpointConfigurationHandlerChainTable() {
        return endpointConfigurationHandlerChainTable;
    }

    public FormFragment getEndpointConfigurationHandlerChainForm() {
        return endpointConfigurationHandlerChainForm;
    }

    public TableFragment getEndpointConfigurationHandlerTable() {
        return endpointConfigurationHandlerTable;
    }

    public FormFragment getEndpointConfigurationHandlerForm() {
        return endpointConfigurationHandlerForm;
    }
}
