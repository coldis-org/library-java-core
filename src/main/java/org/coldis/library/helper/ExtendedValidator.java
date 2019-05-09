package org.coldis.library.helper;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;

/**
 * Extended validator.
 */
public class ExtendedValidator implements Validator {

	/**
	 * Validator.
	 */
	private Validator validator;

	/**
	 * Default constructor.
	 *
	 * @param validator Validator.
	 */
	public ExtendedValidator(final Validator validator) {
		super();
	}

	/**
	 * @see javax.validation.Validator#validate(java.lang.Object, java.lang.Class[])
	 */
	@Override
	public <T> Set<ConstraintViolation<T>> validate(final T object, final Class<?>... groups) {
		return this.validator.validate(object, groups);
	}

	/**
	 * @see javax.validation.Validator#validateProperty(java.lang.Object,
	 *      java.lang.String, java.lang.Class[])
	 */
	@Override
	public <T> Set<ConstraintViolation<T>> validateProperty(final T object, final String propertyName,
			final Class<?>... groups) {
		return this.validator.validateProperty(object, propertyName, groups);
	}

	/**
	 * @see javax.validation.Validator#validateValue(java.lang.Class,
	 *      java.lang.String, java.lang.Object, java.lang.Class[])
	 */
	@Override
	public <T> Set<ConstraintViolation<T>> validateValue(final Class<T> beanType, final String propertyName,
			final Object value, final Class<?>... groups) {
		return this.validator.validateValue(beanType, propertyName, value, groups);
	}

	/**
	 * @see javax.validation.Validator#getConstraintsForClass(java.lang.Class)
	 */
	@Override
	public BeanDescriptor getConstraintsForClass(final Class<?> clazz) {
		return this.validator.getConstraintsForClass(clazz);
	}

	/**
	 * @see javax.validation.Validator#unwrap(java.lang.Class)
	 */
	@Override
	public <T> T unwrap(final Class<T> type) {
		return this.validator.unwrap(type);
	}

	/**
	 * @see javax.validation.Validator#forExecutables()
	 */
	@Override
	public ExecutableValidator forExecutables() {
		return this.validator.forExecutables();
	}

	/**
	 * Validates an object and throws its constraint violations.
	 *
	 * @param <ObjectType> Object type.
	 * @param object       Object to be validated.
	 * @param groups       Groups to be used in the validation.
	 */
	public <ObjectType> void validateAndThrowViolations(final ObjectType object, final Class<?>... groups) {
		// Validates the object.
		final Set<ConstraintViolation<ObjectType>> violations = this.validate(object, groups);
		// Violations message.
		final StringBuffer violationsMessage = new StringBuffer();
		// For each violation.
		for (final ConstraintViolation<ObjectType> violation : violations) {
			// Adds the violation to the composed message.
			violationsMessage.append(violation.getMessage() + "\n");
		}
		// If there are constraint violations.
		if ((violations != null) && !violations.isEmpty()) {
			// Throws a constraint violation exception with the constraint violations.
			throw new ConstraintViolationException(violationsMessage.toString(), violations);
		}
	}

}
