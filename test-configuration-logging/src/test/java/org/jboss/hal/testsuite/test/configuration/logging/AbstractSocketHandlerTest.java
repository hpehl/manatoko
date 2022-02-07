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
package org.jboss.hal.testsuite.test.configuration.logging;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.dmr.ModelDescriptionConstants;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.LoggingConfigurationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.NAMED_FORMATTER;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.SocketHandler.SOCKET_HANDLER_CREATE;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.SocketHandler.SOCKET_HANDLER_DELETE;
import static org.jboss.hal.testsuite.fixtures.LoggingFixtures.SocketHandler.SOCKET_HANDLER_UPDATE;

public abstract class AbstractSocketHandlerTest {

    static final String OUTBOUND_SOCKET_BINDING_REF = "outbound-socket-binding-ref-" + Random.name();
    static final String SSL_CONTEXT = "client-ssl-context-" + Random.name();
    protected static final String XML_FORMATTER = "xml-formatter-" + Random.name();
    protected static Operations ops;

    @Inject CrudOperations crud;
    TableFragment table;
    FormFragment form;

    protected abstract LoggingConfigurationPage getPage();

    protected abstract Address socketHandlerAddress(String name);

    protected abstract TableFragment getHandlerTable();

    protected abstract FormFragment getHandlerForm();

    protected abstract void navigateToPage();

    // there must be a separate pattern-formatter name for logging-profile
    // subclasses may override this method to provide a different name
    protected String getPatternFormatter() {
        return "PATTERN";
    }

    @BeforeEach
    void navigate() {
        navigateToPage();
        table = getPage().getSocketHandlerTable();
        form = getPage().getSocketHandlerForm();
        table.bind(form);
    }

    @Test
    void create() throws Exception {
        crud.create(socketHandlerAddress(SOCKET_HANDLER_CREATE), table, f -> {
            f.text(NAME, SOCKET_HANDLER_CREATE);
            f.text(NAMED_FORMATTER, getPatternFormatter());
            f.text(ModelDescriptionConstants.OUTBOUND_SOCKET_BINDING_REF, "mail-smtp");
        });
    }

    @Test
    void reset() throws Exception {
        table.select(SOCKET_HANDLER_UPDATE);
        crud.reset(socketHandlerAddress(SOCKET_HANDLER_UPDATE), form);
    }

    @Test
    void delete() throws Exception {
        crud.delete(socketHandlerAddress(SOCKET_HANDLER_DELETE), table, SOCKET_HANDLER_DELETE);
    }

    @Test
    void toggleAutoFlush() throws Exception {
        boolean autoflush = ops.readAttribute(socketHandlerAddress(SOCKET_HANDLER_UPDATE), "autoflush").booleanValue(true);
        table.select(SOCKET_HANDLER_UPDATE);
        crud.update(socketHandlerAddress(SOCKET_HANDLER_UPDATE), form, "autoflush", !autoflush);
    }

    @Test
    void toggleBlockOnReconnect() throws Exception {
        boolean blockOnReconnect = ops.readAttribute(socketHandlerAddress(SOCKET_HANDLER_UPDATE), "block-on-reconnect")
                .booleanValue(true);
        table.select(SOCKET_HANDLER_UPDATE);
        crud.update(socketHandlerAddress(SOCKET_HANDLER_UPDATE), form, "block-on-reconnect", !blockOnReconnect);
    }

    @Test
    void toggleEnabled() throws Exception {
        boolean enabled = ops.readAttribute(socketHandlerAddress(SOCKET_HANDLER_UPDATE), "enabled").booleanValue(true);
        table.select(SOCKET_HANDLER_UPDATE);
        crud.update(socketHandlerAddress(SOCKET_HANDLER_UPDATE), form, "enabled", !enabled);
    }

    @Test
    void editEncoding() throws Exception {
        table.select(SOCKET_HANDLER_UPDATE);
        crud.update(socketHandlerAddress(SOCKET_HANDLER_UPDATE), form, "encoding");
    }

    @Test
    void editFilterSpec() throws Exception {
        table.select(SOCKET_HANDLER_UPDATE);
        crud.update(socketHandlerAddress(SOCKET_HANDLER_UPDATE), form, "filter-spec", "not(match(\"JBAS.*\"))");
    }

    @Test
    void editLevel() throws Exception {
        String[] levels = { "ALL", "FINEST", "FINER", "TRACE", "DEBUG", "FINE", "CONFIG", "INFO", "WARN", "WARNING", "ERROR",
                "SEVERE",
                "FATAL", "OFF" };
        String level = levels[Random.number(1, levels.length)];
        table.select(SOCKET_HANDLER_UPDATE);
        crud.update(socketHandlerAddress(SOCKET_HANDLER_UPDATE), form,
                formFragment -> formFragment.select("level", level),
                resourceVerifier -> resourceVerifier.verifyAttribute("level", level));
    }

    @Test
    void editNamedFormatter() throws Exception {
        table.select(SOCKET_HANDLER_UPDATE);
        crud.update(socketHandlerAddress(SOCKET_HANDLER_UPDATE), form, "named-formatter", XML_FORMATTER);
    }

    @Test
    void editOutboundSocketBindingRef() throws Exception {
        table.select(SOCKET_HANDLER_UPDATE);
        crud.update(socketHandlerAddress(SOCKET_HANDLER_UPDATE), form, "outbound-socket-binding-ref",
                OUTBOUND_SOCKET_BINDING_REF);
    }

    @Test
    void editProtocol() throws Exception {
        String[] protocols = { "TCP", "UDP", "SSL_TCP" };
        String protocol = protocols[Random.number(1, protocols.length)];
        table.select(SOCKET_HANDLER_UPDATE);
        crud.update(socketHandlerAddress(SOCKET_HANDLER_UPDATE), form,
                formFragment -> formFragment.select("protocol", protocol),
                resourceVerifier -> resourceVerifier.verifyAttribute("protocol", protocol));
    }

    @Test
    void editSSLContext() throws Exception {
        table.select(SOCKET_HANDLER_UPDATE);
        crud.update(socketHandlerAddress(SOCKET_HANDLER_UPDATE), form, "ssl-context", SSL_CONTEXT);
    }
}
