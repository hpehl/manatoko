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

import org.jboss.hal.manatoko.fragment.TableFragment;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.openqa.selenium.support.FindBy;

@Place(NameTokens.SYSTEM_PROPERTIES)
public class SystemPropertyPage extends BasePage {

    @FindBy(id = Ids.SYSTEM_PROPERTY_TABLE)
    private TableFragment table;

    public TableFragment getTable() {
        return table;
    }
}
