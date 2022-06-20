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
package org.jboss.hal.testsuite.fragment;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.fragment.Root;
import org.jboss.hal.testsuite.Console;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.jboss.hal.resources.CSS.blankSlatePfMainAction;
import static org.jboss.hal.resources.CSS.btnPrimary;

/** Fragment for an empty state element. */
public class EmptyState {

    @Inject private Console console;
    @Root private WebElement root;
    @FindBy(css = "." + blankSlatePfMainAction + " button." + btnPrimary) private WebElement primaryButton;

    /** Clicks on the main action */
    public void mainAction() {
        console.scrollIntoView(primaryButton);
        primaryButton.click();
    }

    public WebElement getRoot() {
        return root;
    }
}
