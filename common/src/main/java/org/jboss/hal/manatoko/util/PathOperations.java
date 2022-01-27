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
package org.jboss.hal.manatoko.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

public class PathOperations {

    private static final Logger log = LoggerFactory.getLogger(PathOperations.class);

    private final Operations ops;

    public PathOperations(final OnlineManagementClient managementClient) {
        this.ops = new Operations(managementClient);
    }

    /**
     * Resolve full filesystem path of path defined by /path=&lt;pathName&gt;. Path is resolved recursively until relative-to
     * attribute is specified.
     *
     * @param pathName name of path in configuration
     * @return full path
     * @throws IOException when some IO error occurs during reading of attribute
     */
    public String resolveFullPathForStandaloneServer(final String pathName) throws IOException {
        return resolveFullPathForResource(pathName, Address.root());
    }

    /**
     * Resolve full filesystem path of path defined by /path=&lt;pathName&gt;. Path is resolved recursively until relative-to
     * attribute is specified.
     *
     * @param pathName name of path in configuration
     * @param resourceAddress address of resource used for resolving
     * @return full path
     * @throws IOException when some IO error occurs during reading of attribute
     */
    public String resolveFullPathForResource(final String pathName, final Address resourceAddress) throws IOException {
        log.debug("Resolving full path of '" + pathName + "'");
        Address pathAddress = resourceAddress.and("path", pathName);
        String path = ops.readAttribute(pathAddress, "path").stringValue();
        log.debug("Resolved path: " + path);
        ModelNodeResult relativeTo = ops.readAttribute(pathAddress, "relative-to");

        Path resolved;
        if (relativeTo.isFailed() || !relativeTo.hasDefinedValue()) {
            resolved = Paths.get(path);
        } else {
            resolved = Paths.get(resolveFullPathForResource(relativeTo.stringValue(), resourceAddress), path);
        }

        log.debug("Resolved FULL path: " + path);
        return resolved.toString();
    }
}
