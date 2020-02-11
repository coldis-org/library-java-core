package org.coldis.library.model;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Verifiable attribute metadata.
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface VerifiableAttribute {

}
