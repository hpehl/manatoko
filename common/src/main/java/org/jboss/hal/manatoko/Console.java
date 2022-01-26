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
package org.jboss.hal.manatoko;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.hal.resources.Ids;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.mvp.shared.proxy.TokenFormatException;
import com.gwtplatform.mvp.shared.proxy.TokenFormatter;

import static org.jboss.arquillian.graphene.Graphene.waitModel;
import static org.junit.Assert.assertEquals;

public class Console {

    private static final String DOT = ".";

    @Drone
    private WebDriver browser;
    private final TokenFormatter tokenFormatter;

    public Console() {
        tokenFormatter = new HalTokenFormatter();
    }

    // ------------------------------------------------------ navigation

    /** Navigates to the place request and waits until the id {@link Ids#ROOT_CONTAINER} is present. */
    public void navigate(PlaceRequest request) {
        navigate(request, By.id(Ids.ROOT_CONTAINER));
    }

    /** Navigates to the place request and waits until the selector is present. */
    public void navigate(PlaceRequest request, By selector) {
        browser.navigate().to(HalContainer.currentInstance().url(request));
        waitModel().until().element(selector).is().present();
        browser.manage().window().maximize();
    }

    public void reload() {
        browser.navigate().refresh();
        waitModel().until().element(By.id(Ids.ROOT_CONTAINER)).is().present();
    }

    public void verify(PlaceRequest placeRequest) {
        String expected = tokenFormatter.toPlaceToken(placeRequest);
        String actual = StringUtils.substringAfter(browser.getCurrentUrl(), "#");
        assertEquals(expected, actual);
    }

    // ------------------------------------------------------ token formatter

    private static class HalTokenFormatter implements TokenFormatter {

        @Override
        public String toHistoryToken(List<PlaceRequest> placeRequestHierarchy) throws TokenFormatException {
            throw new UnsupportedOperationException();
        }

        @Override
        public PlaceRequest toPlaceRequest(String placeToken) throws TokenFormatException {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<PlaceRequest> toPlaceRequestHierarchy(String historyToken) throws TokenFormatException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toPlaceToken(PlaceRequest placeRequest) throws TokenFormatException {
            StringBuilder builder = new StringBuilder();
            builder.append(placeRequest.getNameToken());
            Set<String> params = placeRequest.getParameterNames();
            if (params != null) {
                for (String param : params) {
                    builder.append(";").append(param).append("=").append(placeRequest.getParameter(param, null));
                }
            }
            return builder.toString();
        }
    }
}
