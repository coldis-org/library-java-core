package org.coldis.library.model.view;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Sensitive attribute.
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface SensitiveAttribute {

	/**
	 * To be masked RegEx.
	 */
	public String toBeMaskedRegex() default SensitiveFieldSerializer.DEFAULT_TO_BE_MASKED_REGEX;

}
