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
package org.jboss.hal.testsuite.fixtures.microprofile;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.jboss.hal.dmr.ModelDescriptionConstants.MICROPROFILE_CONFIG_SMALLRYE;
import static org.jboss.hal.testsuite.CrudConstants.CREATE;
import static org.jboss.hal.testsuite.CrudConstants.DELETE;
import static org.jboss.hal.testsuite.CrudConstants.UPDATE;

public class MicroprofileConfigFixtures {

    private static final String CONFIG_SOURCE = "config-source",
            CONFIG_PROVIDER = "config-provider",
            PROPS = "props";

    private static final Address SUBYSTEM_ADDRESS = Address.subsystem(MICROPROFILE_CONFIG_SMALLRYE);

    public static final String ARCHIVE_NAME = "microprofile-config-custom.jar",
            ARCHIVE_FOR_UPDATE_NAME = "microprofile-config-custom-for-update.jar",
            CLASS = "class",
            NAME = "name",
            MODULE = "module",
            PROPERTIES = "properties",
            CLASS_NAME_LABEL = "Class / Class Name",
            MODULE_LABEL = "Class / Module",
            CONFIG_SOURCE_PROPS_CREATE = Ids.build(CONFIG_SOURCE, PROPS, CREATE, Random.name()),
            CONFIG_SOURCE_PROPS_UPDATE = Ids.build(CONFIG_SOURCE, PROPS, UPDATE, Random.name()),
            CONFIG_SOURCE_PROPS_DELETE = Ids.build(CONFIG_SOURCE, PROPS, DELETE, Random.name()),
            CONFIG_SOURCE_CLASS_CREATE = Ids.build(CONFIG_SOURCE, CLASS, CREATE, Random.name()),
            CONFIG_SOURCE_CLASS_UPDATE = Ids.build(CONFIG_SOURCE, CLASS, UPDATE, Random.name()),
            CONFIG_PROVIDER_CREATE = Ids.build(CONFIG_PROVIDER, CREATE, Random.name()),
            CONFIG_PROVIDER_UPDATE = Ids.build(CONFIG_PROVIDER, UPDATE, Random.name()),
            CONFIG_PROVIDER_DELETE = Ids.build(CONFIG_PROVIDER, DELETE, Random.name());

    public static Path CUSTOM_MODULE_PATH = Paths.get("test", "configuration", "microprofile", "config"),
            CUSTOM_MODULE_FOR_UPDATE_PATH = Paths.get("test", "configuration", "microprofile", "config-for-update");

    public static Address getConfigSourceAddress(String sourceName) {
        return SUBYSTEM_ADDRESS.and("config-source", sourceName);
    }

    public static Address getConfigProviderAddress(String providerName) {
        return SUBYSTEM_ADDRESS.and("config-source-provider", providerName);
    }

    private MicroprofileConfigFixtures() {
    }

}
