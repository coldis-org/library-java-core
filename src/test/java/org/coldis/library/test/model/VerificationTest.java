package org.coldis.library.test.model;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.coldis.library.helper.DateTimeHelper;
import org.coldis.library.model.VerifiableObject;
import org.coldis.library.model.Verification;
import org.coldis.library.model.VerificationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Object verification test.
 */
public class VerificationTest {

	/**
	 * Valid objects.
	 */
	private static final TestVerifiableObject[] VALID_DATA = {
			new TestVerifiableObject(new TreeSet<>(Set.of(
					new Verification(VerificationStatus.VALID, Set.of("attribute1"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new Verification(VerificationStatus.VALID, Set.of("attribute2"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new Verification(VerificationStatus.VALID, Set.of("attribute3"), "me", null, "desc"),
					new Verification(VerificationStatus.DUBIOUS, Set.of("attribute1", "attribute2", "attribute3"), "me",
							DateTimeHelper.getCurrentLocalDateTime().minusWeeks(1), "desc")))),
			new TestVerifiableObject(new TreeSet<>(Set.of(
					new Verification(VerificationStatus.VALID, Set.of("attribute1", "attribute2", "attribute3"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new Verification(VerificationStatus.INVALID, Set.of("attribute1", "attribute2"), "me",
							DateTimeHelper.getCurrentLocalDateTime().minusWeeks(1), "desc")))) };

	/**
	 * Invalid objects.
	 */
	private static final TestVerifiableObject[] INVALID_DATA = {
			new TestVerifiableObject(new TreeSet<>(Set.of(
					new Verification(VerificationStatus.INVALID, Set.of("attribute1"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new Verification(VerificationStatus.INVALID, Set.of("attribute2"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new Verification(VerificationStatus.INVALID, Set.of("attribute3"), "me", null, "desc"),
					new Verification(VerificationStatus.INVALID, Set.of("attribute1", "attribute2", "attribute3"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc")))),
			new TestVerifiableObject(new TreeSet<>(Set.of(
					new Verification(VerificationStatus.INVALID, Set.of("attribute1"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new Verification(VerificationStatus.NOT_VERIFIED, Set.of("attribute2"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new Verification(VerificationStatus.OVERRIDE, Set.of("attribute3"), "me", null, "desc"),
					new Verification(VerificationStatus.VALID, Set.of("attribute1", "attribute2", "attribute3"), "me",
							DateTimeHelper.getCurrentLocalDateTime().minusDays(1), "desc")))) };

	/**
	 * Not verified objects.
	 */
	private static final TestVerifiableObject[] NOT_VERIFIED_DATA = {
			new TestVerifiableObject(new TreeSet<>(Set.of(
					new Verification(VerificationStatus.NOT_VERIFIED, Set.of("attribute1"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new Verification(VerificationStatus.NOT_VERIFIED, Set.of("attribute2"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new Verification(VerificationStatus.OVERRIDE, Set.of("attribute3"), "me", null, "desc")))),
			new TestVerifiableObject(new TreeSet<>(Set.of(
					new Verification(VerificationStatus.VALID, Set.of("attribute1"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new Verification(VerificationStatus.VALID, Set.of("attribute2"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new Verification(VerificationStatus.VALID, Set.of("attribute3"), "me",
							DateTimeHelper.getCurrentLocalDateTime().minusDays(1), "desc")))),
			new TestVerifiableObject(new TreeSet<>(Set.of(
					new Verification(VerificationStatus.VALID, Set.of("attribute1"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new Verification(VerificationStatus.VALID, Set.of("attribute2"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new Verification(VerificationStatus.INVALID, Set.of("attribute3"), "me",
							DateTimeHelper.getCurrentLocalDateTime().minusMinutes(1), "desc")))),
			new TestVerifiableObject(new TreeSet<>(Set.of(
					new Verification(VerificationStatus.VALID, Set.of("attribute1"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new Verification(VerificationStatus.DUBIOUS, Set.of("attribute2"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new Verification(VerificationStatus.INVALID, Set.of("attribute3"), "me",
							DateTimeHelper.getCurrentLocalDateTime().minusHours(1), "desc")))),
			new TestVerifiableObject(new TreeSet<>(Set.of())),
			new TestVerifiableObject(new TreeSet<>(Set.of(new Verification(VerificationStatus.VALID,
					Set.of("attribute1", "attribute2"), "me", null, "desc")))) };

	/**
	 * Dubious objects.
	 */
	private static final TestVerifiableObject[] DUBIOUS_DATA = {
			new TestVerifiableObject(new TreeSet<>(Set.of(
					new Verification(VerificationStatus.DUBIOUS, Set.of("attribute1"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new Verification(VerificationStatus.DUBIOUS, Set.of("attribute2"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new Verification(VerificationStatus.DUBIOUS, Set.of("attribute3"), "me", null, "desc")))),
			new TestVerifiableObject(new TreeSet<>(Set.of(
					new Verification(VerificationStatus.VALID, Set.of("attribute1"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new Verification(VerificationStatus.DUBIOUS, Set.of("attribute2"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new Verification(VerificationStatus.DUBIOUS, Set.of("attribute3"), "me", null, "desc")))),
			new TestVerifiableObject(new TreeSet<>(Set.of(new Verification(VerificationStatus.DUBIOUS,
					Set.of("attribute1", "attribute2", "attribute3"), "me", null, "desc")))) };

	/**
	 * Tests valid objects.
	 */
	@Test
	public void test00ValidObject() {
		// For each test object.
		for (TestVerifiableObject testObject : VALID_DATA) {
			// Gets the verifiable attributes statuses.
			VerificationStatus attribute1Status = VerifiableObject.getAttributeStatus(testObject, "attribute1");
			VerificationStatus attribute2Status = VerifiableObject.getAttributeStatus(testObject, "attribute2");
			VerificationStatus attribute3Status = VerifiableObject.getAttributeStatus(testObject, "attribute3");
			// Makes sure the attributes are valid.
			Assertions.assertEquals(VerificationStatus.VALID, attribute1Status);
			Assertions.assertEquals(VerificationStatus.VALID, attribute2Status);
			Assertions.assertEquals(VerificationStatus.VALID, attribute3Status);
			// Makes sure none of the attributes are dubious.
			Assertions.assertFalse(
					List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.DUBIOUS));
			// Makes sure none of the attributes are not verified.
			Assertions.assertFalse(List.of(attribute1Status, attribute2Status, attribute3Status)
					.contains(VerificationStatus.NOT_VERIFIED));
			// Makes sure the object is valid.
			Assertions.assertEquals(VerificationStatus.VALID, testObject.getStatus());
		}
	}

	/**
	 * Tests invalid objects.
	 */
	@Test
	public void test01InvalidObject() {
		// For each test object.
		for (TestVerifiableObject testObject : INVALID_DATA) {
			// Gets the verifiable attributes statuses.
			VerificationStatus attribute1Status = VerifiableObject.getAttributeStatus(testObject, "attribute1");
			VerificationStatus attribute2Status = VerifiableObject.getAttributeStatus(testObject, "attribute2");
			VerificationStatus attribute3Status = VerifiableObject.getAttributeStatus(testObject, "attribute3");
			// Makes sure at least one attribute is invalid.
			Assertions.assertTrue(
					List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.INVALID));
			// Makes sure none of the attributes are valid.
			Assertions.assertFalse(
					List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.VALID));
			// Makes sure the object is invalid.
			Assertions.assertEquals(VerificationStatus.INVALID, testObject.getStatus());
		}
	}

	/**
	 * Tests not verified objects.
	 */
	@Test
	public void test02NotVerifiedObject() {
		// For each test object.
		for (TestVerifiableObject testObject : NOT_VERIFIED_DATA) {
			// Gets the verifiable attributes statuses.
			VerificationStatus attribute1Status = VerifiableObject.getAttributeStatus(testObject, "attribute1");
			VerificationStatus attribute2Status = VerifiableObject.getAttributeStatus(testObject, "attribute2");
			VerificationStatus attribute3Status = VerifiableObject.getAttributeStatus(testObject, "attribute3");
			// Makes sure none of the attributes is invalid.
			Assertions.assertFalse(
					List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.INVALID));
			// Makes sure not all of the attributes are valid.
			Assertions.assertFalse(VerificationStatus.VALID == attribute1Status
					&& VerificationStatus.VALID == attribute2Status && VerificationStatus.VALID == attribute3Status);
			// Makes sure the object is not verified.
			Assertions.assertEquals(VerificationStatus.NOT_VERIFIED, testObject.getStatus());
		}
	}

	/**
	 * Tests dubious objects.
	 */
	@Test
	public void test03DubiousObject() {
		// For each test object.
		for (TestVerifiableObject testObject : DUBIOUS_DATA) {
			// Gets the verifiable attributes statuses.
			VerificationStatus attribute1Status = VerifiableObject.getAttributeStatus(testObject, "attribute1");
			VerificationStatus attribute2Status = VerifiableObject.getAttributeStatus(testObject, "attribute2");
			VerificationStatus attribute3Status = VerifiableObject.getAttributeStatus(testObject, "attribute3");
			// Makes sure none of the attributes is invalid.
			Assertions.assertFalse(
					List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.INVALID));
			// Makes sure not all of the attributes are valid.
			Assertions.assertFalse(VerificationStatus.VALID == attribute1Status
					&& VerificationStatus.VALID == attribute2Status && VerificationStatus.VALID == attribute3Status);
			// Makes sure at least one of the attributes is dubious.
			Assertions.assertTrue(
					List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.DUBIOUS));
			// Makes sure the object is not verified.
			Assertions.assertEquals(VerificationStatus.DUBIOUS, testObject.getStatus());
		}
	}

}
