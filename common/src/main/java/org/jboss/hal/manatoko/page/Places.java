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

import org.jboss.hal.manatoko.fragment.finder.FinderPath;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class Places {

    private static final String PATH_PARAM = "path";

    public static PlaceRequest finderPlace(String token, FinderPath path) {
        PlaceRequest.Builder builder = new PlaceRequest.Builder().nameToken(token);
        if (path != null) {
            builder.with(PATH_PARAM, path.toString());
        }
        return builder.build();
    }

    private Places() {
    }
}
