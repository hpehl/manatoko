package org.jboss.hal.testsuite.environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment {

    private enum Type {
        LOCAL, REMOTE
    }


    private static final Type DEFAULT_TYPE = Type.REMOTE;
    private static final String PROPERTY = "test.environment";
    private static final Logger logger = LoggerFactory.getLogger(Environment.class);
    private static Environment instance;

    public static Environment instance() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    private final Type type;

    private Environment() {
        Type type = DEFAULT_TYPE;
        String property = System.getProperty(PROPERTY);
        if (property != null) {
            String value = property.toUpperCase();
            try {
                type = Type.valueOf(value);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to parse '{}' as test environment. Fallback to {} environment",
                        value, type);
            }
        }
        this.type = type;
        logger.info("Use {} test environment", type.name().toLowerCase());
    }

    public boolean local() {
        return type == Type.LOCAL;
    }

    public boolean remote() {
        return type == Type.REMOTE;
    }
}
