package org.coldis.library.helper;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;

/**
 * Extended validator.
 */
public class ExtendedValidator implements Validator {

	/**
	 * Validator.
	 */
	private final Validator validator;

	/**
	 * Default constructor.
	 *
	 * @param validator Validator.
	 */
	public ExtendedValidator(final Validator validator) {
		super();
		this.validator = validator;
	}

	/**
	 * @see jakarta.validation.Validator#validate(java.lang.Object, java.lang.Class[])
	 */
	@Override
	public <ObjectType> Set<ConstraintViolation<ObjectType>> validate(
			final ObjectType object,
			final Class<?>... groups) {
		return this.validator.validate(object, groups);
	}

	/**
	 * @see jakarta.validation.Validator#validateProperty(java.lang.Object,
	 *      java.lang.String, java.lang.Class[])
	 */
	@Override
	public <ObjectType> Set<ConstraintViolation<ObjectType>> validateProperty(
			final ObjectType object,
			final String propertyName,
			final Class<?>... groups) {
		return this.validator.validateProperty(object, propertyName, groups);
	}

	/**
	 * @see jakarta.validation.Validator#validateValue(java.lang.Class,
	 *      java.lang.String, java.lang.Object, java.lang.Class[])
	 */
	@Override
	public <ObjectType> Set<ConstraintViolation<ObjectType>> validateValue(
			final Class<ObjectType> beanType,
			final String propertyName,
			final Object value,
			final Class<?>... groups) {
		return this.validator.validateValue(beanType, propertyName, value, groups);
	}

	/**
	 * @see jakarta.validation.Validator#getConstraintsForClass(java.lang.Class)
	 */
	@Override
	public BeanDescriptor getConstraintsForClass(
			final Class<?> clazz) {
		return this.validator.getConstraintsForClass(clazz);
	}

	/**
	 * @see jakarta.validation.Validator#unwrap(java.lang.Class)
	 */
	@Override
	public <ObjectType> ObjectType unwrap(
			final Class<ObjectType> type) {
		return this.validator.unwrap(type);
	}

	/**
	 * @see jakarta.validation.Validator#forExecutables()
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
	public <ObjectType> void validateAndThrowViolations(
			final ObjectType object,
			final Class<?>... groups) {
		// Validates the object.
		final Set<ConstraintViolation<ObjectType>> violations = this.validate(object, groups);
		// Violations message.
		final StringBuffer violationsMessage = new StringBuffer();
		// For each violation.
		for (final ConstraintViolation<ObjectType> violation : violations) {
			// Adds the violation to the composed message.
			violationsMessage.append(violation.getPropertyPath() + ": " + violation.getMessage() + "\n");
		}
		// If there are constraint violations.
		if ((violations != null) && !violations.isEmpty()) {
			// Throws a constraint violation exception with the constraint violations.
			throw new ConstraintViolationException(violationsMessage.toString(), violations);
		}
	}

}
