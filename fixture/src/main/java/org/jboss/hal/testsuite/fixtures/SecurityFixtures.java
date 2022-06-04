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
import static org.jboss.hal.dmr.ModelDescriptionConstants.X500_SUBJECT_EVIDENCE_DECODER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.X509_SUBJECT_ALT_NAME_EVIDENCE_DECODER;
import static org.jboss.hal.testsuite.model.CrudConstants.CREATE;
import static org.jboss.hal.testsuite.model.CrudConstants.DELETE;
import static org.jboss.hal.testsuite.model.CrudConstants.READ;
import static org.jboss.hal.testsuite.model.CrudConstants.UPDATE;

public final class SecurityFixtures {

    private static final String CLIENT_SSL_CONTEXT_PREFIX = "cli-ssl";
    private static final String CREDENTIAL_STORE_PREFIX = "cred-store";
    private static final String EVIDENCE_DECODER_PREFIX = "ed";

    public static final String ALT_NAME_TYPE = "alt-name-type";
    public static final String ALT_NAME_TYPE_DIRECTORY_NAME = "directoryName";
    public static final String ALT_NAME_TYPE_RFC822_NAME = "rfc822Name";
    public static final String CERTIFICATE_AUTHORITY_URL = "https://acme.org";
    public static final String EVIDENCE_DECODER_ITEM = "mappers-decoders-evidence-decoder-item";
    public static final String EVIDENCE_DECODERS = "evidence-decoders";
    public static final String INITIAL_PROVIDERS = "initial-providers";
    public static final String OTHER_ITEM = "other-item";
    public static final String SEGMENT = "segment";

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

    // -------------- client-ssl-context

    public static final String CLIENT_SSL_CREATE = Ids.build(CLIENT_SSL_CONTEXT_PREFIX, CREATE, Random.name());
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
