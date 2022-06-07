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
package org.jboss.hal.testsuite.fixtures;

import org.apache.commons.lang3.RandomStringUtils;
import org.wildfly.extras.creaper.core.online.operations.Address;

public class PathsFixtures {

    public final static String JBOSS_SERVER_BASE_DIR = "jboss.server.base.dir";
    public final static String JBOSS_SERVER_CONFIG_DIR = "jboss.server.config.dir";
    public final static String JBOSS_SERVER_DATA_DIR = "jboss.server.data.dir";
    public final static String JBOSS_SERVER_LOG_DIR = "jboss.server.log.dir";
    public final static String JBOSS_SERVER_TEMP_DIR = "jboss.server.temp.dir";

    private PathsFixtures() {

    }

    public static Address pathAddress(String path) {
        return Address.of("path", path);
    }

    public static final String PATH = "path";

    public static final String PATH_CREATE = "path-to-be-created-" + RandomStringUtils.randomAlphanumeric(7);
    public static final String PATH_DELETE = "path-to-be-removed-" + RandomStringUtils.randomAlphanumeric(7);
    public static final String PATH_EDIT = "path-to-be-edited-" + RandomStringUtils.randomAlphanumeric(7);
    public static final String RELATIVE_TO_PATH = "relative-to-path-" + RandomStringUtils.randomAlphanumeric(7);

}
