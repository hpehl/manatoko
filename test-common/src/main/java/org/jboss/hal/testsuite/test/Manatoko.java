package org.jboss.hal.testsuite.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * {@code @Manatoko} is a composite JUnit Jupiter extension to activate {@code @SystemSetupExtension} and {@code @ArquillianExtension}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({SystemSetupExtension.class, ArquillianExtension.class})
@Inherited
public @interface Manatoko {
}
