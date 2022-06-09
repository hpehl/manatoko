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
package org.jboss.hal.testsuite.preview.runtime;

import org.jboss.hal.testsuite.fragment.DropdownFragment;
import org.jboss.hal.testsuite.fragment.finder.FinderPreviewFragment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.jboss.arquillian.graphene.Graphene.createPageFragment;

public class TopologyPreview extends FinderPreviewFragment {

    public String getServerOneStatus() {
        WebElement element = getAttributeElementMap("Server").get("Status");
        if (element != null) {
            return element.getText();
        }
        return null;
    }

    public WebElement getServerOneContainer() {
        return root.findElement(By.id("master-server-one-container"));
    }

    public DropdownFragment getServerOneDropdown() {
        return createPageFragment(DropdownFragment.class, root.findElement(By.id("master-server-one")));
    }
}
