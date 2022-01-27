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
package org.jboss.hal.manatoko.page;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.hal.manatoko.Console;
import org.jboss.hal.resources.Ids;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public abstract class AbstractPage {

    @Drone protected WebDriver browser;
    @Inject protected Console console;
    @FindBy(id = Ids.ROOT_CONTAINER) private WebElement rootContainer;

    public WebElement getRootContainer() {
        return rootContainer;
    }

    protected Place assertPlace() {
        Place place = this.getClass().getAnnotation(Place.class);
        if (place == null) {
            throw new IllegalArgumentException(
                    String.format("The page object '%s' that you are navigating to is not annotated with @Place",
                            this.getClass().getSimpleName()));
        }
        return place;
    }

    /** Navigates to the name token specified in the {@code @Place} annotation. */
    public abstract void navigate();
}
