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
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.BasePage;
import org.jboss.hal.testsuite.page.Place;
import org.openqa.selenium.support.FindBy;

@Place(NameTokens.UNDERTOW_FILTER)
public class FilterPage extends BasePage {

    @FindBy(id = "undertow-rewrite-table_wrapper") private TableFragment rewriteTable;

    @FindBy(id = "undertow-rewrite-form") private FormFragment rewriteForm;

    @FindBy(id = "undertow-response-header-table_wrapper") private TableFragment responseHeaderTable;

    @FindBy(id = "undertow-response-header-form") private FormFragment responseHeaderForm;

    @FindBy(id = "undertow-request-limit-table_wrapper") private TableFragment requestLimitTable;

    @FindBy(id = "undertow-request-limit-form") private FormFragment requestLimitForm;

    @FindBy(id = "undertow-mod-cluster-table_wrapper") private TableFragment modClusterFilterTable;

    @FindBy(id = "undertow-mod-cluster-form") private FormFragment modClusterFilterForm;

    @FindBy(id = "undertow-gzip-table_wrapper") private TableFragment gzipTable;

    @FindBy(id = "undertow-expression-filter-table_wrapper") private TableFragment expressionFilterTable;

    @FindBy(id = "undertow-expression-filter-form") private FormFragment expressionFilterForm;

    @FindBy(id = "undertow-error-page-table_wrapper") private TableFragment errorPageTable;

    @FindBy(id = "undertow-error-page-form") private FormFragment errorPageForm;

    @FindBy(id = "undertow-custom-filter-table_wrapper") private TableFragment customFilterTable;

    @FindBy(id = "undertow-custom-filter-form") private FormFragment customFilterForm;

    public TableFragment getRewriteTable() {
        return rewriteTable;
    }

    public FormFragment getRewriteForm() {
        return rewriteForm;
    }

    public TableFragment getResponseHeaderTable() {
        return responseHeaderTable;
    }

    public FormFragment getResponseHeaderForm() {
        return responseHeaderForm;
    }

    public TableFragment getRequestLimitTable() {
        return requestLimitTable;
    }

    public FormFragment getRequestLimitForm() {
        return requestLimitForm;
    }

    public TableFragment getModClusterFilterTable() {
        return modClusterFilterTable;
    }

    public FormFragment getModClusterFilterForm() {
        return modClusterFilterForm;
    }

    public TableFragment getGzipTable() {
        return gzipTable;
    }

    public TableFragment getExpressionFilterTable() {
        return expressionFilterTable;
    }

    public FormFragment getExpressionFilterForm() {
        return expressionFilterForm;
    }

    public TableFragment getErrorPageTable() {
        return errorPageTable;
    }

    public FormFragment getErrorPageForm() {
        return errorPageForm;
    }

    public TableFragment getCustomFilterTable() {
        return customFilterTable;
    }

    public FormFragment getCustomFilterForm() {
        return customFilterForm;
    }
}
