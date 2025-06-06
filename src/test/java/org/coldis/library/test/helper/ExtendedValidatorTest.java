package org.coldis.library.test.helper;

import org.coldis.library.helper.ExtendedValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Test class for ExtendedValidator.
 */
public class ExtendedValidatorTest {

	/**
	 * Extended validator instance.
	 */
	private ExtendedValidator extendedValidator;

	/**
	 * Nested object for testing.
	 */
	public static class NestedObject {

		private String nestedField;

		@Email
		public String getNestedField() {
			return this.nestedField;
		}

		public void setNestedField(
				final String nestedField) {
			this.nestedField = nestedField;
		}
	}

	/**
	 * Test object for testing.
	 */
	public static class TestObject {

		private String field;

		private NestedObject nestedObject;

		@Size(
				min = 5,
				max = 10
		)
		public String getField() {
			return this.field;
		}

		public void setField(
				final String field) {
			this.field = field;
		}

		@Valid
		public NestedObject getNestedObject() {
			return this.nestedObject;
		}

		public void setNestedObject(
				final NestedObject nestedObject) {
			this.nestedObject = nestedObject;
		}
	}

	/**
	 * Sets up the test environment.
	 */
	@BeforeEach
	public void setUp() {
		final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		this.extendedValidator = new ExtendedValidator(validator);
	}

	/**
	 * Tests the validateAndThrowViolations method.
	 */
	@Test
	public void testValidateAndThrowViolations() {
		final TestObject testObject = new TestObject();
		testObject.setField("123"); // Invalid field
		testObject.setNestedObject(new NestedObject());
		testObject.getNestedObject().setNestedField("123"); // Invalid field
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			this.extendedValidator.validateAndThrowViolations(testObject);
		});

		testObject.setField("12345"); // Valid field
		testObject.setNestedObject(new NestedObject());
		testObject.getNestedObject().setNestedField("123"); // Invalid field
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			this.extendedValidator.validateAndThrowViolations(testObject);
		});

		testObject.setField("12345"); // Valid field
		testObject.setNestedObject(new NestedObject());
		testObject.getNestedObject().setNestedField("123@gmail.com"); // Valid field
		this.extendedValidator.validateAndThrowViolations(testObject);
	}

	/**
	 * Tests the clearInvalidFields method.
	 */
	@Test
	public void testClearInvalidFields() {
		final TestObject testObject = new TestObject();
		testObject.setField("123"); // Invalid field
		testObject.setNestedObject(new NestedObject());
		testObject.getNestedObject().setNestedField("123"); // Invalid field
		this.extendedValidator.clearInvalidFields(testObject);
		Assertions.assertNull(testObject.getField());
		Assertions.assertNull(testObject.getNestedObject().getNestedField());

		testObject.setField("12345"); // Valid field
		testObject.setNestedObject(new NestedObject());
		testObject.getNestedObject().setNestedField("123"); // Invalid field
		this.extendedValidator.clearInvalidFields(testObject);
		Assertions.assertNotNull(testObject.getField());
		Assertions.assertNull(testObject.getNestedObject().getNestedField());

		testObject.setField("12345"); // Valid field
		testObject.setNestedObject(new NestedObject());
		testObject.getNestedObject().setNestedField("123@gmail.com"); // Valid field
		this.extendedValidator.clearInvalidFields(testObject);
		Assertions.assertNotNull(testObject.getField());
		Assertions.assertNotNull(testObject.getNestedObject().getNestedField());

	}
}
