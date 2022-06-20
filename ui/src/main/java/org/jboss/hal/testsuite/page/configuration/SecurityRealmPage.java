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
import org.jboss.hal.testsuite.fragment.TabsFragment;
import org.jboss.hal.testsuite.page.BasePage;
import org.jboss.hal.testsuite.page.Place;
import org.openqa.selenium.support.FindBy;

import static org.jboss.hal.testsuite.Selectors.WRAPPER;

@Place(NameTokens.ELYTRON_SECURITY_REALMS)
public class SecurityRealmPage extends BasePage {

    @FindBy(id = Ids.ELYTRON_TOKEN_REALM + "-" + Ids.TABLE + WRAPPER) private TableFragment tokenRealmTable;
    @FindBy(id = Ids.ELYTRON_TOKEN_REALM + "-" + Ids.TAB_CONTAINER) private TabsFragment tokenRealmTabs;
    @FindBy(id = Ids.ELYTRON_TOKEN_REALM + "-" + Ids.FORM) private FormFragment tokenRealmForm;
    @FindBy(id = Ids.ELYTRON_TOKEN_REALM + "-jwt-" + Ids.FORM) private FormFragment jwtForm;
    @FindBy(id = Ids.ELYTRON_TOKEN_REALM + "-oauth2-introspection-" + Ids.FORM) private FormFragment oauth2IntrospectionForm;

    public TableFragment getTokenRealmTable() {
        return tokenRealmTable;
    }

    public TabsFragment getTokenRealmTabs() {
        return tokenRealmTabs;
    }

    public FormFragment getTokenRealmForm() {
        return tokenRealmForm;
    }

    public FormFragment getJwtForm() {
        return jwtForm;
    }

    public FormFragment getOauth2IntrospectionForm() {
        return oauth2IntrospectionForm;
    }
}
