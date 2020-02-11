package org.coldis.library.test.model;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.coldis.library.helper.DateTimeHelper;
import org.coldis.library.model.SimpleMessage;
import org.coldis.library.model.VerifiableObject;
import org.coldis.library.model.Verification;
import org.coldis.library.model.VerificationItem;
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
					new TestVerifiableObject(new Verification(new TreeSet<>(Set.of(
							new VerificationItem(VerificationStatus.VALID, Set.of("attribute1"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(1), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.VALID, Set.of("attribute2"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(2), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.VALID, Set.of("attribute3"), "me", null,
									new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.DUBIOUS,
									Set.of("attribute1", "attribute2", "attribute3"), "me",
									DateTimeHelper.getCurrentLocalDateTime().minusWeeks(1),
									new SimpleMessage("desc")))))),
					new TestVerifiableObject(new Verification(new TreeSet<>(Set.of(
							new VerificationItem(VerificationStatus.VALID,
									Set.of("attribute1", "attribute2", "attribute3"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(1), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.INVALID, Set.of("attribute1", "attribute2"), "me",
									DateTimeHelper.getCurrentLocalDateTime().minusWeeks(1),
									new SimpleMessage("desc")))))) };

	/**
	 * Invalid objects.
	 */
	private static final TestVerifiableObject[] INVALID_DATA = {
					new TestVerifiableObject(new Verification(new TreeSet<>(Set.of(
							new VerificationItem(VerificationStatus.INVALID, Set.of("attribute1"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(1), new SimpleMessage("desc")),
							new VerificationItem(
									VerificationStatus.INVALID, Set.of("attribute2"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(2), new SimpleMessage("desc")),
							new VerificationItem(
									VerificationStatus.INVALID, Set.of("attribute3"), "me", null,
									new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.INVALID,
									Set.of("attribute1", "attribute2", "attribute3"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(1),
									new SimpleMessage("desc")))))),
					new TestVerifiableObject(new Verification(new TreeSet<>(Set.of(
							new VerificationItem(VerificationStatus.INVALID, Set.of("attribute1"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(1), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.NOT_VERIFIED, Set.of("attribute2"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(2), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.OVERRIDE, Set.of("attribute3"), "me", null,
									new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.VALID,
									Set.of("attribute1", "attribute2", "attribute3"), "me",
									DateTimeHelper.getCurrentLocalDateTime().minusDays(1),
									new SimpleMessage("desc")))))) };

	/**
	 * Not verified objects.
	 */
	private static final TestVerifiableObject[] NOT_VERIFIED_DATA = {
					new TestVerifiableObject(new Verification(new TreeSet<>(Set.of(
							new VerificationItem(VerificationStatus.NOT_VERIFIED, Set.of("attribute1"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(1), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.NOT_VERIFIED, Set.of("attribute2"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(2), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.OVERRIDE, Set.of("attribute3"), "me", null,
									new SimpleMessage("desc")))))),
					new TestVerifiableObject(new Verification(new TreeSet<>(Set.of(
							new VerificationItem(VerificationStatus.VALID, Set.of("attribute1"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(1), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.VALID, Set.of("attribute2"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(2), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.VALID, Set.of("attribute3"), "me",
									DateTimeHelper.getCurrentLocalDateTime().minusDays(1),
									new SimpleMessage("desc")))))),
					new TestVerifiableObject(new Verification(new TreeSet<>(Set.of(
							new VerificationItem(VerificationStatus.VALID, Set.of("attribute1"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(1), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.VALID, Set.of("attribute2"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(2), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.INVALID, Set.of("attribute3"), "me",
									DateTimeHelper.getCurrentLocalDateTime().minusMinutes(1),
									new SimpleMessage("desc")))))),
					new TestVerifiableObject(new Verification(new TreeSet<>(Set.of(
							new VerificationItem(VerificationStatus.VALID, Set.of("attribute1"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(1), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute2"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(2), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.INVALID, Set.of("attribute3"), "me",
									DateTimeHelper.getCurrentLocalDateTime().minusHours(1),
									new SimpleMessage("desc")))))),
					new TestVerifiableObject(new Verification(new TreeSet<>(Set.of()))),
					new TestVerifiableObject(
							new Verification(new TreeSet<>(Set.of(new VerificationItem(VerificationStatus.VALID,
									Set.of("attribute1", "attribute2"), "me", null, new SimpleMessage("desc")))))) };

	/**
	 * Dubious objects.
	 */
	private static final TestVerifiableObject[] DUBIOUS_DATA = {
					new TestVerifiableObject(new Verification(new TreeSet<>(Set.of(
							new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute1"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(1), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute2"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(2), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute3"), "me", null,
									new SimpleMessage("desc")))))),
					new TestVerifiableObject(new Verification(new TreeSet<>(Set.of(
							new VerificationItem(VerificationStatus.VALID, Set.of("attribute1"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(1), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute2"), "me",
									DateTimeHelper.getCurrentLocalDateTime().plusDays(2), new SimpleMessage("desc")),
							new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute3"), "me", null,
									new SimpleMessage("desc")))))),
					new TestVerifiableObject(
							new Verification(new TreeSet<>(Set.of(new VerificationItem(VerificationStatus.DUBIOUS,
									Set.of("attribute1", "attribute2", "attribute3"), "me", null,
									new SimpleMessage("desc")))))) };

	/**
	 * Tests valid objects.
	 */
	@Test
	public void test00ValidObject() {
		// For each test object.
		for (final TestVerifiableObject testObject : VerificationTest.VALID_DATA) {
			// Gets the verifiable attributes statuses.
			final VerificationStatus attribute1Status = VerifiableObject.getVerificationStatus(testObject,
					"attribute1");
			final VerificationStatus attribute2Status = VerifiableObject.getVerificationStatus(testObject,
					"attribute2");
			final VerificationStatus attribute3Status = VerifiableObject.getVerificationStatus(testObject,
					"attribute3");
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
			Assertions.assertEquals(VerificationStatus.VALID, testObject.getVerificationStatus());
		}
	}

	/**
	 * Tests invalid objects.
	 */
	@Test
	public void test01InvalidObject() {
		// For each test object.
		for (final TestVerifiableObject testObject : VerificationTest.INVALID_DATA) {
			// Gets the verifiable attributes statuses.
			final VerificationStatus attribute1Status = VerifiableObject.getVerificationStatus(testObject,
					"attribute1");
			final VerificationStatus attribute2Status = VerifiableObject.getVerificationStatus(testObject,
					"attribute2");
			final VerificationStatus attribute3Status = VerifiableObject.getVerificationStatus(testObject,
					"attribute3");
			// Makes sure at least one attribute is invalid.
			Assertions.assertTrue(
					List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.INVALID));
			// Makes sure none of the attributes are valid.
			Assertions.assertFalse(
					List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.VALID));
			// Makes sure the object is invalid.
			Assertions.assertEquals(VerificationStatus.INVALID, testObject.getVerificationStatus());
		}
	}

	/**
	 * Tests not verified objects.
	 */
	@Test
	public void test02NotVerifiedObject() {
		// For each test object.
		for (final TestVerifiableObject testObject : VerificationTest.NOT_VERIFIED_DATA) {
			// Gets the verifiable attributes statuses.
			final VerificationStatus attribute1Status = VerifiableObject.getVerificationStatus(testObject,
					"attribute1");
			final VerificationStatus attribute2Status = VerifiableObject.getVerificationStatus(testObject,
					"attribute2");
			final VerificationStatus attribute3Status = VerifiableObject.getVerificationStatus(testObject,
					"attribute3");
			// Makes sure none of the attributes is invalid.
			Assertions.assertFalse(
					List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.INVALID));
			// Makes sure not all of the attributes are valid.
			Assertions.assertFalse(
					(VerificationStatus.VALID == attribute1Status) && (VerificationStatus.VALID == attribute2Status)
					&& (VerificationStatus.VALID == attribute3Status));
			// Makes sure the object is not verified.
			Assertions.assertEquals(VerificationStatus.NOT_VERIFIED, testObject.getVerificationStatus());
		}
	}

	/**
	 * Tests dubious objects.
	 */
	@Test
	public void test03DubiousObject() {
		// For each test object.
		for (final TestVerifiableObject testObject : VerificationTest.DUBIOUS_DATA) {
			// Gets the verifiable attributes statuses.
			final VerificationStatus attribute1Status = VerifiableObject.getVerificationStatus(testObject,
					"attribute1");
			final VerificationStatus attribute2Status = VerifiableObject.getVerificationStatus(testObject,
					"attribute2");
			final VerificationStatus attribute3Status = VerifiableObject.getVerificationStatus(testObject,
					"attribute3");
			// Makes sure none of the attributes is invalid.
			Assertions.assertFalse(
					List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.INVALID));
			// Makes sure not all of the attributes are valid.
			Assertions.assertFalse(
					(VerificationStatus.VALID == attribute1Status) && (VerificationStatus.VALID == attribute2Status)
					&& (VerificationStatus.VALID == attribute3Status));
			// Makes sure at least one of the attributes is dubious.
			Assertions.assertTrue(
					List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.DUBIOUS));
			// Makes sure the object is not verified.
			Assertions.assertEquals(VerificationStatus.DUBIOUS, testObject.getVerificationStatus());
		}
	}

}
