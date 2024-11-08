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

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.hal.testsuite.fragment.DropdownFragment;
import org.jboss.hal.testsuite.fragment.finder.FinderPreviewFragment;
import org.jboss.hal.testsuite.model.DomainMode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.jboss.arquillian.graphene.Graphene.createPageFragment;
import static org.jboss.hal.resources.CSS.dropdownMenu;
import static org.jboss.hal.testsuite.Selectors.contains;

public class TopologyPreview extends FinderPreviewFragment {

    public void refresh() {
        WebElement refresh = root.findElement(ByJQuery.selector(".clickable > span" + contains("Refresh")));
        if (refresh != null) {
            refresh.click();
        }
    }

    public void selectServer(String server) {
        WebElement element = root.findElement(By.cssSelector("[data-server=" + hostServerId(server) + "]"));
        Graphene.waitGui().until().element(element).is().visible();
        element.click();
    }

    public void serverAction(String server, String action) {
        By linkSelector = By.id(hostServerId(server));
        By dropdownSelector = By.cssSelector("#" + hostServerId(server) + " + ul." + dropdownMenu);
        WebElement link = root.findElement(linkSelector);
        link.click();
        Graphene.waitGui().until().element(dropdownSelector).is().visible();
        DropdownFragment dropdownFragment = createPageFragment(DropdownFragment.class, root.findElement(dropdownSelector));
        dropdownFragment.click(action);
    }

    public String getServerAttribute(String attribute) {
        WebElement element = getAttributeElementMap("Server").get(attribute);
        return element.getText();
    }

    private String hostServerId(String server) {
        return DomainMode.instance().defaultHost() + "-" + server;
    }
}
