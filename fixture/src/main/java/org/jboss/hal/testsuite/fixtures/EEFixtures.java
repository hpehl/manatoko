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

import org.jboss.dmr.ModelNode;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.jboss.hal.dmr.ModelDescriptionConstants.CONTEXT_SERVICE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT_BINDINGS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.EE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.GLOBAL_DIRECTORY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MANAGED_EXECUTOR_SERVICE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MANAGED_SCHEDULED_EXECUTOR_SERVICE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.MANAGED_THREAD_FACTORY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVICE;
import static org.jboss.hal.testsuite.model.CrudConstants.CREATE;
import static org.jboss.hal.testsuite.model.CrudConstants.DELETE;
import static org.jboss.hal.testsuite.model.CrudConstants.READ;
import static org.jboss.hal.testsuite.model.CrudConstants.UPDATE;

public final class EEFixtures {

    private static final String CONTEXT_SERVICE_PREFIX = "cs";
    private static final String EXECUTOR_PREFIX = "exe";
    private static final String SCHEDULED_EXECUTOR_PREFIX = "se";
    private static final String THREAD_FACTORY_PREFIX = "tf";

    public static final String JBOSS_DATA_DIR = "jboss.server.data.dir";
    public static final Address SUBSYSTEM_ADDRESS = Address.subsystem(EE);
    public static final Address DEFAULT_BINDINGS_ADDRESS = SUBSYSTEM_ADDRESS.and(SERVICE, DEFAULT_BINDINGS);

    // ------------------------------------------------------ global directory

    public static final String GLOBAL_DIRECTORY_CREATE = Ids.build("gd", CREATE, Random.name());
    public static final String GLOBAL_DIRECTORY_READ = Ids.build("gd", READ, Random.name());
    public static final String GLOBAL_DIRECTORY_UPDATE = Ids.build("gd", UPDATE, Random.name());
    public static final String GLOBAL_DIRECTORY_DELETE = Ids.build("gd", DELETE, Random.name());

    public static Address globalDirectoryAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(GLOBAL_DIRECTORY, name);
    }

    // ------------------------------------------------------ global module

    public static final String GLOBAL_MODULES_CREATE = Ids.build("gm", CREATE, Random.name());
    public static final String GLOBAL_MODULES_DELETE = Ids.build("gm", DELETE, Random.name());

    public static ModelNode globalModule(String name) {
        ModelNode globalModule = new ModelNode();
        globalModule.get(NAME).set(name);
        globalModule.get("meta-inf").set(true);
        globalModule.get("slot");
        return globalModule;
    }

    // ------------------------------------------------------ context service

    public static final String CONTEXT_SERVICE_CREATE = Ids.build(CONTEXT_SERVICE_PREFIX, CREATE, Random.name());
    public static final String CONTEXT_SERVICE_READ = Ids.build(CONTEXT_SERVICE_PREFIX, READ, Random.name());
    public static final String CONTEXT_SERVICE_UPDATE = Ids.build(CONTEXT_SERVICE_PREFIX, UPDATE, Random.name());
    public static final String CONTEXT_SERVICE_DELETE = Ids.build(CONTEXT_SERVICE_PREFIX, DELETE, Random.name());

    public static Address contextServiceAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(CONTEXT_SERVICE, name);
    }

    // ------------------------------------------------------ executor

    public static final String EXECUTOR_CREATE = Ids.build(EXECUTOR_PREFIX, CREATE, Random.name());
    public static final String EXECUTOR_READ = Ids.build(EXECUTOR_PREFIX, READ, Random.name());
    public static final String EXECUTOR_UPDATE = Ids.build(EXECUTOR_PREFIX, UPDATE, Random.name());
    public static final String EXECUTOR_DELETE = Ids.build(EXECUTOR_PREFIX, DELETE, Random.name());

    public static Address executorAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(MANAGED_EXECUTOR_SERVICE, name);
    }

    // ------------------------------------------------------ scheduled executor

    public static final String SCHEDULED_EXECUTOR_CREATE = Ids.build(SCHEDULED_EXECUTOR_PREFIX, CREATE,
            Random.name());
    public static final String SCHEDULED_EXECUTOR_READ = Ids.build(SCHEDULED_EXECUTOR_PREFIX, READ,
            Random.name());
    public static final String SCHEDULED_EXECUTOR_UPDATE = Ids.build(SCHEDULED_EXECUTOR_PREFIX, UPDATE,
            Random.name());
    public static final String SCHEDULED_EXECUTOR_DELETE = Ids.build(SCHEDULED_EXECUTOR_PREFIX, DELETE,
            Random.name());

    public static Address scheduledExecutorAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(MANAGED_SCHEDULED_EXECUTOR_SERVICE, name);
    }

    // ------------------------------------------------------ thread factory

    public static final String THREAD_FACTORY_CREATE = Ids.build(THREAD_FACTORY_PREFIX, CREATE, Random.name());
    public static final String THREAD_FACTORY_READ = Ids.build(THREAD_FACTORY_PREFIX, READ, Random.name());
    public static final String THREAD_FACTORY_UPDATE = Ids.build(THREAD_FACTORY_PREFIX, UPDATE, Random.name());
    public static final String THREAD_FACTORY_DELETE = Ids.build(THREAD_FACTORY_PREFIX, DELETE, Random.name());

    public static Address threadFactoryAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(MANAGED_THREAD_FACTORY, name);
    }

    private EEFixtures() {
    }
}
