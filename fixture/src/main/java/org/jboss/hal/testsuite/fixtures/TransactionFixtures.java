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
package org.jboss.hal.testsuite.fixtures;

import org.apache.commons.lang3.RandomStringUtils;
import org.wildfly.extras.creaper.core.online.operations.Address;

public interface TransactionFixtures {

    String DEFAULT_TIMEOUT = "default-timeout";
    String ENABLE_TSM_STATUS = "enable-tsm-status";
    String JOURNAL_STORE_ENABLE_ASYNC_IO = "journal-store-enable-async-io";
    String JTS = "jts";
    String USE_JOURNAL_STORE = "use-journal-store";
    String STATISTICS_ENABLED = "statistics-enabled";
    String JDBC_ACTION_STORE_DROP_TABLE = "jdbc-action-store-drop-table";
    String JDBC_ACTION_STORE_TABLE_PREFIX = "jdbc-action-store-table-prefix";
    String JDBC_COMMUNICATION_STORE_DROP_TABLE = "jdbc-communication-store-drop-table";
    String JDBC_COMMUNICATION_STORE_TABLE_PREFIX = "jdbc-communication-store-table-prefix";
    String JDBC_STATE_STORE_DROP_TABLE = "jdbc-state-store-drop-table";
    String JDBC_STATE_STORE_TABLE_PREFIX = "jdbc-state-store-table-prefix";
    String JDBC_STORE_DATASOURCE = "jdbc-store-datasource";
    String SOCKET_BINDING = "socket-binding";
    String STATUS_SOCKET_BINDING = "status-socket-binding";
    String RECOVERY_LISTENER = "recovery-listener";
    String PROCESS_ID_SOCKET_BINDING = "process-id-socket-binding";
    String PROCESS_ID_UUID = "process-id-uuid";
    String PROCESS_ID_SOCKET_MAX_PORTS = "process-id-socket-max-ports";
    String OBJECT_STORE_RELATIVE_TO = "object-store-relative-to";

    String PATH_EDIT = "path-to-be-edited-" + RandomStringUtils.randomAlphanumeric(7);
    String USE_JDBC_STORE = "use-jdbc-store";
    String JDBC_DATASOURCE = "jdbc-datasource-to-be-created-" + RandomStringUtils.randomAlphanumeric(7);
    String PROCESS_SOCKET_BINDING_CREATE = "socket-binding-to-be-created-"
            + RandomStringUtils.randomAlphanumeric(7);
    String PROCESS_SOCKET_BINDING_WITH_PROCESS_ID_UUID = "socket-binding-with-process-id-uuid-"
            + RandomStringUtils.randomAlphanumeric(7);
    String RECOVERY_SOCKET_BINDING_CREATE = "socket-binding-to-be-created-"
            + RandomStringUtils.randomAlphanumeric(7);
    String RECOVERY_STATUS_SOCKET_BINDING = "status-socket-binding-to-be-edited"
            + RandomStringUtils.randomAlphanumeric(7);

    Address TRANSACTIONS_ADDRESS = Address.subsystem("transactions");
}
