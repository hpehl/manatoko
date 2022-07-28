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

import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.jboss.hal.dmr.ModelDescriptionConstants.AJP_LISTENER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.APPLICATION_SECURITY_DOMAIN;
import static org.jboss.hal.dmr.ModelDescriptionConstants.BUFFER_CACHE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CONFIGURATION;
import static org.jboss.hal.dmr.ModelDescriptionConstants.FILTER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HOST;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTPS_LISTENER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.HTTP_LISTENER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVLET_CONTAINER;
import static org.jboss.hal.testsuite.model.CrudConstants.CREATE;
import static org.jboss.hal.testsuite.model.CrudConstants.DELETE;
import static org.jboss.hal.testsuite.model.CrudConstants.READ;
import static org.jboss.hal.testsuite.model.CrudConstants.UPDATE;

public class WebFixtures {

    private static final String APPLICATION_SECURITY_DOMAIN_PREFIX = "asd";
    private static final String BUFFER_CACHE_PREFIX = "bc";
    private static final String BYTE_BUFFER_POOL_PREFIX = "bbp";
    private static final String FILTER_PREFIX = "flt";
    private static final String HOST_PREFIX = "hst";
    private static final String SERVER_PREFIX = "srv";
    private static final String SERVLET_CONTAINER_PREFIX = "slt_cnt";

    public static final Address UNDERTOW_ADDRESS = Address.subsystem("undertow");
    public static final Address FILTER_ADDRESS = UNDERTOW_ADDRESS.and(CONFIGURATION, FILTER);
    public static final String BUFFER_SIZE = "buffer-size";
    public static final String BUFFERS_PER_REGION = "buffers-per-region";
    public static final String CODE = "code";
    public static final String CUSTOM_FILTER = "custom-filter";
    public static final String DEFAULT_SERVER = "default-server";
    public static final String DEFAULT_SERVLET_CONTAINER = "default-servlet-container";
    public static final String DEFAULT_VIRTUAL_HOST = "default-virtual-host";
    public static final String ERROR_PAGE = "error-page";
    public static final String EXPRESSION_FILTER = "expression-filter";
    public static final String GZIP = "gzip";
    public static final String HEADER_VALUE = "header-value";
    public static final String MAX_CONCURRENT_REQUESTS = "max-concurrent-requests";
    public static final String MAX_REGIONS = "max-regions";
    public static final String MOD_CLUSTER = "mod-cluster";
    public static final String MANAGEMENT_SOCKET_BINDING = "management-socket-binding";
    public static final String PARAMETERS = "parameters";
    public static final String REDIRECT = "redirect";
    public static final String REQUEST_LIMIT = "request-limit";
    public static final String RESPONSE_HEADER = "response-header";
    public static final String REWRITE = "rewrite";
    public static final String THREAD_LOCAL_CACHE_SIZE = "thread-local-cache-size";

    // ------------------------------------------------------ application security domain

    public static final String APPLICATION_SECURITY_DOMAIN_CREATE = Ids.build(APPLICATION_SECURITY_DOMAIN_PREFIX, CREATE,
            Random.name());
    public static final String APPLICATION_SECURITY_DOMAIN_READ = Ids.build(APPLICATION_SECURITY_DOMAIN_PREFIX, READ,
            Random.name());
    public static final String APPLICATION_SECURITY_DOMAIN_DELETE = Ids.build(APPLICATION_SECURITY_DOMAIN_PREFIX, DELETE,
            Random.name());

    public static Address applicationSecurityDomainAddress(String name) {
        return UNDERTOW_ADDRESS.and(APPLICATION_SECURITY_DOMAIN, name);
    }

    // ------------------------------------------------------ buffer cache

    public static final String BUFFER_CACHE_CREATE = Ids.build(BUFFER_CACHE_PREFIX, CREATE, Random.name());
    public static final String BUFFER_CACHE_UPDATE = Ids.build(BUFFER_CACHE_PREFIX, UPDATE, Random.name());
    public static final String BUFFER_CACHE_DELETE = Ids.build(BUFFER_CACHE_PREFIX, DELETE, Random.name());

    public static Address bufferCacheAddress(String name) {
        return UNDERTOW_ADDRESS.and(BUFFER_CACHE, name);
    }

    // ------------------------------------------------------ byte buffer

    public static final String BYTE_BUFFER_POOL_CREATE = Ids.build(BYTE_BUFFER_POOL_PREFIX, CREATE, Random.name());
    public static final String BYTE_BUFFER_POOL_UPDATE = Ids.build(BYTE_BUFFER_POOL_PREFIX, UPDATE, Random.name());
    public static final String BYTE_BUFFER_POOL_DELETE = Ids.build(BYTE_BUFFER_POOL_PREFIX, DELETE, Random.name());

    public static Address byteBufferPoolAddress(String byteBuffer) {
        return UNDERTOW_ADDRESS.and("byte-buffer-pool", byteBuffer);
    }

    // ------------------------------------------------------ filter

    public static final String FILTER_CREATE = Ids.build(FILTER_PREFIX, CREATE, Random.name());
    public static final String FILTER_UPDATE = Ids.build(FILTER_PREFIX, UPDATE, Random.name());
    public static final String FILTER_DELETE = Ids.build(FILTER_PREFIX, DELETE, Random.name());

    public static Address customFilterAddress(String name) {
        return FILTER_ADDRESS.and(CUSTOM_FILTER, name);
    }

    public static Address errorPageAddress(String name) {
        return FILTER_ADDRESS.and(ERROR_PAGE, name);
    }

    public static Address expressionFilterAddress(String name) {
        return FILTER_ADDRESS.and(EXPRESSION_FILTER, name);
    }

    public static Address gzipAddress(String name) {
        return FILTER_ADDRESS.and(GZIP, name);
    }

    public static Address modClusterAddress(String name) {
        return FILTER_ADDRESS.and(MOD_CLUSTER, name);
    }

    public static Address requestLimitAddress(String name) {
        return FILTER_ADDRESS.and(REQUEST_LIMIT, name);
    }

    public static Address responseHeaderAddress(String name) {
        return FILTER_ADDRESS.and(RESPONSE_HEADER, name);
    }

    public static Address rewriteAddress(String name) {
        return FILTER_ADDRESS.and(REWRITE, name);
    }

    // ------------------------------------------------------ listeners

    public static Address ajpListenerAddress(String serverName, String ajpListenerName) {
        return serverAddress(serverName).and(AJP_LISTENER, ajpListenerName);
    }

    public static Address httpListenerAddress(String serverName, String httpListenerName) {
        return serverAddress(serverName).and(HTTP_LISTENER, httpListenerName);
    }

    public static Address httpsListenerAddress(String serverName, String httpsListenerName) {
        return serverAddress(serverName).and(HTTPS_LISTENER, httpsListenerName);
    }

    // ------------------------------------------------------ server

    public static final String DEFAULT_SERVER_CREATE = Ids.build(SERVER_PREFIX, CREATE, Random.name());

    public static Address serverAddress(String serverName) {
        return UNDERTOW_ADDRESS.and("server", serverName);
    }

    // ------------------------------------------------------ servlet container

    public static final String DEFAULT_SERVLET_CONTAINER_CREATE = Ids.build(SERVLET_CONTAINER_PREFIX, CREATE,
            Random.name());

    public static Address servletContainerAddress(String name) {
        return UNDERTOW_ADDRESS.and(SERVLET_CONTAINER, name);
    }

    // ------------------------------------------------------ virtual host

    public static final String DEFAULT_VIRTUAL_HOST_CREATE = Ids.build(HOST_PREFIX, CREATE, Random.name());

    public static Address virtualHostAddress(String serverName, String name) {
        return serverAddress(serverName).and(HOST, name);
    }

    private WebFixtures() {
    }
}
