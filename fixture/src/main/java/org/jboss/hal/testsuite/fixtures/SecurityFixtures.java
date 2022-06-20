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

import static org.jboss.hal.dmr.ModelDescriptionConstants.AGGREGATE_EVIDENCE_DECODER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CERTIFICATE_AUTHORITY;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CLIENT_SSL_CONTEXT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.CREDENTIAL_STORE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.ELYTRON;
import static org.jboss.hal.dmr.ModelDescriptionConstants.KEY_MANAGER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.KEY_STORE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SECRET_KEY_CREDENTIAL_STORE;
import static org.jboss.hal.dmr.ModelDescriptionConstants.SERVER_SSL_CONTEXT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TOKEN_REALM;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TRUST_MANAGER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.X500_SUBJECT_EVIDENCE_DECODER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.X509_SUBJECT_ALT_NAME_EVIDENCE_DECODER;
import static org.jboss.hal.testsuite.model.CrudConstants.CREATE;
import static org.jboss.hal.testsuite.model.CrudConstants.DELETE;
import static org.jboss.hal.testsuite.model.CrudConstants.READ;
import static org.jboss.hal.testsuite.model.CrudConstants.UPDATE;

public final class SecurityFixtures {

    private static final String CLIENT_SSL_CONTEXT_PREFIX = "cli-ssl";
    private static final String CREDENTIAL_STORE_PREFIX = "cred-store";
    private static final String EXPRESSION_RESOLVER_PREFIX = "er";
    private static final String EVIDENCE_DECODER_PREFIX = "ed";
    private static final String KEY_MANAGER_PREFIX = "ks";
    private static final String KEY_STORE_PREFIX = "km";
    private static final String SECRET_KEY_CREDENTIAL_STORE_PREFIX = "skcs";
    private static final String SERVER_SSL_CONTEXT_PREFIX = "srv-ssl";
    private static final String TOKEN_REALM_PREFIX = "tr";
    private static final String TRUST_MANAGER_PREFIX = "tm";

    public static final String ALT_NAME_TYPE = "alt-name-type";
    public static final String ALT_NAME_TYPE_DIRECTORY_NAME = "directoryName";
    public static final String ALT_NAME_TYPE_RFC822_NAME = "rfc822Name";
    public static final String AUDIENCE = "audience";
    public static final String CERTIFICATE = "certificate";
    public static final String CERTIFICATE_AUTHORITY_URL = "https://acme.org";
    public static final String CIPHER_SUITE_NAMES = "cipher-suite-names";
    public static final String CLIENT_ID = "client-id";
    public static final String CLIENT_SECRET = "client-secret";
    public static final String DEFAULT_ALIAS = "default-alias";
    public static final String DEFAULT_RESOLVER = "default-resolver";
    public static final String EVIDENCE_DECODER_ITEM = "mappers-decoders-evidence-decoder-item";
    public static final String EVIDENCE_DECODERS = "evidence-decoders";
    public static final String INITIAL_PROVIDERS = "initial-providers";
    public static final String INTROSPECTION_URL = "introspection-url";
    public static final String INTROSPECTION_URL_VALUE = "https://acme.org";
    public static final String ISSUER = "issuer";
    public static final String JWT = "jwt";
    public static final String JWT_TAB = "elytron-token-realm-jwt-tab";
    public static final String OAUTH2_INTROSPECTION = "oauth2-introspection";
    public static final String OAUTH2_INTROSPECTION_TAB = "elytron-token-realm-oauth2-introspection-tab";
    public static final String OCSP = "ocsp";
    public static final String PRINCIPAL_CLAIM = "principal-claim";
    public static final String PUBLIC_KEY = "public-key";
    public static final String RESPONDER = "responder";
    public static final String SECRET_KEY = "secret-key";
    public static final String SEGMENT = "segment";
    public static final String SSL_CONTEXT_CIPHER_SUITE_NAMES = "TLS_AES_128_CCM_8_SHA256:TLS_AES_256_GCM_SHA384:TLS_CHACHA20_POLY1305_SHA256:TLS_AES_128_GCM_SHA256";

    public static final Address SUBSYSTEM_ADDRESS = Address.subsystem(ELYTRON);

    // ------------------------------------------------------ aggregate evidence decoder

    public static final String AGGREGATE_EVIDENCE_DECODER_CREATE = Ids.build(EVIDENCE_DECODER_PREFIX, "aggregate",
            CREATE, Random.name());
    public static final String AGGREGATE_EVIDENCE_DECODER_READ = Ids.build(EVIDENCE_DECODER_PREFIX, "aggregate",
            READ, Random.name());
    public static final String AGGREGATE_EVIDENCE_DECODER_UPDATE = Ids.build(EVIDENCE_DECODER_PREFIX, "aggregate",
            UPDATE, Random.name());
    public static final String AGGREGATE_EVIDENCE_DECODER_DELETE = Ids.build(EVIDENCE_DECODER_PREFIX, "aggregate",
            DELETE, Random.name());

    public static Address aggregateEvidenceDecoderAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(AGGREGATE_EVIDENCE_DECODER, name);
    }

    // ------------------------------------------------------ certificate authority

    public static final String CERTIFICATE_AUTHORITY_CREATE = Ids.build(CERTIFICATE_AUTHORITY, CREATE, Random.name());
    public static final String CERTIFICATE_AUTHORITY_READ = Ids.build(CERTIFICATE_AUTHORITY, READ, Random.name());
    public static final String CERTIFICATE_AUTHORITY_UPDATE = Ids.build(CERTIFICATE_AUTHORITY, UPDATE, Random.name());
    public static final String CERTIFICATE_AUTHORITY_DELETE = Ids.build(CERTIFICATE_AUTHORITY, DELETE, Random.name());

    public static Address certificateAuthorityAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(CERTIFICATE_AUTHORITY, name);
    }

    // ------------------------------------------------------ client-ssl-context

    public static final String CLIENT_SSL_CREATE = Ids.build(CLIENT_SSL_CONTEXT_PREFIX, CREATE, Random.name());
    public static final String CLIENT_SSL_READ = Ids.build(CLIENT_SSL_CONTEXT_PREFIX, READ, Random.name());
    public static final String CLIENT_SSL_UPDATE = Ids.build(CLIENT_SSL_CONTEXT_PREFIX, UPDATE, Random.name());
    public static final String CLIENT_SSL_DELETE = Ids.build(CLIENT_SSL_CONTEXT_PREFIX, DELETE, Random.name());

    public static Address clientSslContextAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(CLIENT_SSL_CONTEXT, name);
    }

    // ------------------------------------------------------ credential store

    public static final String CREDENTIAL_STORE_CREATE = Ids.build(CREDENTIAL_STORE_PREFIX, CREATE, Random.name());

    public static Address credentialStoreAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(CREDENTIAL_STORE, name);
    }

    // ------------------------------------------------------ expression encryption (resolver)

    public static final String EXPRESSION_RESOLVER_CREATE = Ids.build(EXPRESSION_RESOLVER_PREFIX, CREATE,
            Random.name());
    public static final String EXPRESSION_RESOLVER_READ = Ids.build(EXPRESSION_RESOLVER_PREFIX, READ,
            Random.name());
    public static final String EXPRESSION_RESOLVER_UPDATE = Ids.build(EXPRESSION_RESOLVER_PREFIX, UPDATE,
            Random.name());
    public static final String EXPRESSION_RESOLVER_DELETE = Ids.build(EXPRESSION_RESOLVER_PREFIX, DELETE,
            Random.name());

    public static Address expressionEncryptionAddress() {
        return SUBSYSTEM_ADDRESS.and("expression", "encryption");
    }

    // ------------------------------------------------------ key manager

    public static final String KEY_MANAGER_CREATE = Ids.build(KEY_MANAGER_PREFIX, CREATE, Random.name());

    public static Address keyManagerAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(KEY_MANAGER, name);
    }

    // ------------------------------------------------------ key store

    public static final String KEY_STORE_CREATE = Ids.build(KEY_STORE_PREFIX, CREATE, Random.name());
    public static final String KEY_STORE_READ = Ids.build(KEY_STORE_PREFIX, READ, Random.name());

    public static Address keyStoreAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(KEY_STORE, name);
    }

    // ------------------------------------------------------ secret key credential store

    public static final String SECRET_KEY_CREDENTIAL_STORE_CREATE = Ids.build(SECRET_KEY_CREDENTIAL_STORE_PREFIX, CREATE,
            Random.name());
    public static final String SECRET_KEY_CREDENTIAL_STORE_READ = Ids.build(SECRET_KEY_CREDENTIAL_STORE_PREFIX, READ,
            Random.name());
    public static final String SECRET_KEY_CREDENTIAL_STORE_UPDATE = Ids.build(SECRET_KEY_CREDENTIAL_STORE_PREFIX, UPDATE,
            Random.name());
    public static final String SECRET_KEY_CREDENTIAL_STORE_DELETE = Ids.build(SECRET_KEY_CREDENTIAL_STORE_PREFIX, DELETE,
            Random.name());

    public static Address secretKeyCredentialStoreAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(SECRET_KEY_CREDENTIAL_STORE, name);
    }

    // ------------------------------------------------------ server-ssl-context

    public static final String SERVER_SSL_CREATE = Ids.build(SERVER_SSL_CONTEXT_PREFIX, CREATE, Random.name());
    public static final String SERVER_SSL_UPDATE = Ids.build(SERVER_SSL_CONTEXT_PREFIX, UPDATE, Random.name());
    public static final String SERVER_SSL_DELETE = Ids.build(SERVER_SSL_CONTEXT_PREFIX, DELETE, Random.name());

    public static Address serverSslContextAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(SERVER_SSL_CONTEXT, name);
    }

    // ------------------------------------------------------ token realm

    public static final String TOKEN_REALM_CREATE = Ids.build(TOKEN_REALM_PREFIX, CREATE, Random.name());
    public static final String TOKEN_REALM_UPDATE = Ids.build(TOKEN_REALM_PREFIX, UPDATE, Random.name());
    public static final String TOKEN_REALM_DELETE = Ids.build(TOKEN_REALM_PREFIX, DELETE, Random.name());
    public static final String TOKEN_REALM_JWT_CREATE = Ids.build(TOKEN_REALM_PREFIX, JWT, CREATE, Random.name());
    public static final String TOKEN_REALM_JWT_UPDATE = Ids.build(TOKEN_REALM_PREFIX, JWT, UPDATE, Random.name());
    public static final String TOKEN_REALM_JWT_DELETE = Ids.build(TOKEN_REALM_PREFIX, JWT, DELETE, Random.name());
    public static final String TOKEN_REALM_OAUTH2_INTROSPECTION_CREATE = Ids.build(TOKEN_REALM_PREFIX, OAUTH2_INTROSPECTION,
            CREATE, Random.name());
    public static final String TOKEN_REALM_OAUTH2_INTROSPECTION_UPDATE = Ids.build(TOKEN_REALM_PREFIX, OAUTH2_INTROSPECTION,
            UPDATE, Random.name());
    public static final String TOKEN_REALM_OAUTH2_INTROSPECTION_DELETE = Ids.build(TOKEN_REALM_PREFIX, OAUTH2_INTROSPECTION,
            DELETE, Random.name());

    public static Address tokenRealmAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(TOKEN_REALM, name);
    }

    // ------------------------------------------------------ trust manager

    public static final String TRUST_MANAGER_CREATE = Ids.build(TRUST_MANAGER_PREFIX, CREATE, Random.name());
    public static final String TRUST_MANAGER_UPDATE = Ids.build(TRUST_MANAGER_PREFIX, UPDATE, Random.name());
    public static final String TRUST_MANAGER_DELETE = Ids.build(TRUST_MANAGER_PREFIX, DELETE, Random.name());

    public static Address trustManagerAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(TRUST_MANAGER, name);
    }

    // ------------------------------------------------------ x500 evidence decoder

    public static final String X500_EVIDENCE_DECODER_CREATE = Ids.build(EVIDENCE_DECODER_PREFIX, "x500", CREATE,
            Random.name());
    public static final String X500_EVIDENCE_DECODER_READ = Ids.build(EVIDENCE_DECODER_PREFIX, "x500", READ,
            Random.name());
    public static final String X500_EVIDENCE_DECODER_UPDATE = Ids.build(EVIDENCE_DECODER_PREFIX, "x500", UPDATE,
            Random.name());
    public static final String X500_EVIDENCE_DECODER_DELETE = Ids.build(EVIDENCE_DECODER_PREFIX, "x500", DELETE,
            Random.name());

    public static Address x500EvidenceDecoderAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(X500_SUBJECT_EVIDENCE_DECODER, name);
    }

    // ------------------------------------------------------ x509 evidence decoder

    public static final String X509_EVIDENCE_DECODER_CREATE = Ids.build(EVIDENCE_DECODER_PREFIX, "x509", CREATE,
            Random.name());
    public static final String X509_EVIDENCE_DECODER_READ = Ids.build(EVIDENCE_DECODER_PREFIX, "x509", READ,
            Random.name());
    public static final String X509_EVIDENCE_DECODER_UPDATE = Ids.build(EVIDENCE_DECODER_PREFIX, "x509", UPDATE,
            Random.name());
    public static final String X509_EVIDENCE_DECODER_DELETE = Ids.build(EVIDENCE_DECODER_PREFIX, "x509", DELETE,
            Random.name());

    public static Address x509EvidenceDecoderAddress(String name) {
        return SUBSYSTEM_ADDRESS.and(X509_SUBJECT_ALT_NAME_EVIDENCE_DECODER, name);
    }
}
