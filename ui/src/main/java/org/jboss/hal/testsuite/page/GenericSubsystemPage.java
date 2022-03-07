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
package org.jboss.hal.testsuite.page;

import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TabsFragment;
import org.openqa.selenium.support.FindBy;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public abstract class GenericSubsystemPage extends AbstractPage {

    private static final String ADDRESS = "address";
    private static final String ROOT_SEPARATOR = "%255C0";
    private static final String SUBSYSTEM_SEPARATOR = "%255C2";

    @FindBy(id = Ids.MODEL_BROWSER + "-resource-tab-container") private TabsFragment resourceTabContainer;

    @FindBy(id = "model-browser-" + Ids.MODEL_BROWSER_ROOT + "-form") private FormFragment dataForm;

    @Override
    public void navigate() {
        browser.navigate().refresh();
        console.navigate(getPlaceRequest());
    }

    public PlaceRequest getPlaceRequest() {
        return new PlaceRequest.Builder().nameToken(NameTokens.GENERIC_SUBSYSTEM)
                .with(ADDRESS, placeToGenericSubsystemUrl()).build();
    }

    private String placeToGenericSubsystemUrl() {
        return ROOT_SEPARATOR
                + "subsystem"
                + SUBSYSTEM_SEPARATOR
                + assertPlace().value();
    }

    public FormFragment getDataForm() {
        resourceTabContainer.select(Ids.build(Ids.MODEL_BROWSER, "resource", "data", "tab"));
        return dataForm;
    }

}
