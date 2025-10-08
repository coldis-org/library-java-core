package org.coldis.library.test.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.coldis.library.helper.DateTimeHelper;
import org.coldis.library.model.verification.Verifiable;
import org.coldis.library.model.verification.Verification;
import org.coldis.library.model.verification.VerificationItem;
import org.coldis.library.model.verification.VerificationQuestion;
import org.coldis.library.model.verification.VerificationScore;
import org.coldis.library.model.verification.VerificationStatus;
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
			new TestVerifiableObject(new Verification(List.of(
					new VerificationItem(VerificationStatus.VALID, Set.of("attribute1"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new VerificationItem(VerificationStatus.VALID, Set.of("attribute2"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new VerificationItem(VerificationStatus.VALID, Set.of("attribute3"), "me", null, "desc"),
					new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute1", "attribute2", "attribute3"), "me",
							DateTimeHelper.getCurrentLocalDateTime().minusWeeks(1), "desc")))),
			new TestVerifiableObject(new Verification(List.of(
					new VerificationItem(VerificationStatus.VALID, Set.of("attribute1", "attribute2", "attribute3"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new VerificationItem(VerificationStatus.INVALID, Set.of("attribute1", "attribute2"), "me",
							DateTimeHelper.getCurrentLocalDateTime().minusWeeks(1), "desc")))) };

	/**
	 * Invalid objects.
	 */
	private static final TestVerifiableObject[] INVALID_DATA = {
			new TestVerifiableObject(new Verification(List.of(
					new VerificationItem(VerificationStatus.INVALID, Set.of("attribute1"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new VerificationItem(VerificationStatus.INVALID, Set.of("attribute2"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new VerificationItem(VerificationStatus.INVALID, Set.of("attribute3"), "me", null, "desc"),
					new VerificationItem(VerificationStatus.INVALID, Set.of("attribute1", "attribute2", "attribute3"), "me",
							DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc")))),
			new TestVerifiableObject(new Verification(List.of(
					new VerificationItem(VerificationStatus.INVALID, Set.of("attribute1"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new VerificationItem(VerificationStatus.NOT_VERIFIED, Set.of("attribute2"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(2),
							"desc"),
					new VerificationItem(VerificationStatus.OVERRIDE, Set.of("attribute3"), "me", null, "desc"), new VerificationItem(VerificationStatus.VALID,
							Set.of("attribute1", "attribute2", "attribute3"), "me", DateTimeHelper.getCurrentLocalDateTime().minusDays(1), "desc")))) };

	/**
	 * Not verified objects.
	 */
	private static final TestVerifiableObject[] NOT_VERIFIED_DATA = { new TestVerifiableObject(new Verification(List
			.of(new VerificationItem(VerificationStatus.NOT_VERIFIED, Set.of("attribute1"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new VerificationItem(VerificationStatus.NOT_VERIFIED, Set.of("attribute2"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(2),
							"desc"),
					new VerificationItem(VerificationStatus.OVERRIDE, Set.of("attribute3"), "me", null, "desc")))),
			new TestVerifiableObject(new Verification(List.of(
					new VerificationItem(VerificationStatus.VALID, Set.of("attribute1"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new VerificationItem(VerificationStatus.VALID, Set.of("attribute2"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new VerificationItem(VerificationStatus.VALID, Set.of("attribute3"), "me", DateTimeHelper.getCurrentLocalDateTime().minusDays(1),
							"desc")))),
			new TestVerifiableObject(new Verification(List.of(
					new VerificationItem(VerificationStatus.VALID, Set.of("attribute1"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new VerificationItem(VerificationStatus.VALID, Set.of("attribute2"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new VerificationItem(VerificationStatus.INVALID, Set.of("attribute3"), "me", DateTimeHelper.getCurrentLocalDateTime().minusMinutes(1),
							"desc")))),
			new TestVerifiableObject(new Verification(List.of(
					new VerificationItem(VerificationStatus.VALID, Set.of("attribute1"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute2"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new VerificationItem(VerificationStatus.INVALID, Set.of("attribute3"), "me", DateTimeHelper.getCurrentLocalDateTime().minusHours(1),
							"desc")))),
			new TestVerifiableObject(new Verification(List.of())), new TestVerifiableObject(
					new Verification(List.of(new VerificationItem(VerificationStatus.VALID, Set.of("attribute1", "attribute2"), "me", null, "desc")))) };

	/**
	 * Dubious objects.
	 */
	private static final TestVerifiableObject[] DUBIOUS_DATA = {
			new TestVerifiableObject(new Verification(List.of(
					new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute1"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute2"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute3"), "me", null, "desc")))),
			new TestVerifiableObject(new Verification(List.of(
					new VerificationItem(VerificationStatus.VALID, Set.of("attribute1"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"),
					new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute2"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(2), "desc"),
					new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute3"), "me", null, "desc")))),
			new TestVerifiableObject(new Verification(
					List.of(new VerificationItem(VerificationStatus.DUBIOUS, Set.of("attribute1", "attribute2", "attribute3"), "me", null, "desc")))) };

	/**
	 * Tests valid objects.
	 */
	@Test
	public void test00ValidObject() {
		// For each test object.
		for (final TestVerifiableObject testObject : VerificationTest.VALID_DATA) {
			// Gets the verifiable attributes statuses.
			final VerificationStatus attribute1Status = Verifiable.getVerificationStatus(testObject, "attribute1");
			final VerificationStatus attribute2Status = Verifiable.getVerificationStatus(testObject, "attribute2");
			final VerificationStatus attribute3Status = Verifiable.getVerificationStatus(testObject, "attribute3");
			// Makes sure the attributes are valid.
			Assertions.assertEquals(VerificationStatus.VALID, attribute1Status);
			Assertions.assertEquals(VerificationStatus.VALID, attribute2Status);
			Assertions.assertEquals(VerificationStatus.VALID, attribute3Status);
			// Makes sure none of the attributes are dubious.
			Assertions.assertFalse(List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.DUBIOUS));
			// Makes sure none of the attributes are not verified.
			Assertions.assertFalse(List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.NOT_VERIFIED));
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
			final VerificationStatus attribute1Status = Verifiable.getVerificationStatus(testObject, "attribute1");
			final VerificationStatus attribute2Status = Verifiable.getVerificationStatus(testObject, "attribute2");
			final VerificationStatus attribute3Status = Verifiable.getVerificationStatus(testObject, "attribute3");
			// Makes sure at least one attribute is invalid.
			Assertions.assertTrue(List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.INVALID));
			// Makes sure none of the attributes are valid.
			Assertions.assertFalse(List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.VALID));
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
			final VerificationStatus attribute1Status = Verifiable.getVerificationStatus(testObject, "attribute1");
			final VerificationStatus attribute2Status = Verifiable.getVerificationStatus(testObject, "attribute2");
			final VerificationStatus attribute3Status = Verifiable.getVerificationStatus(testObject, "attribute3");
			// Makes sure none of the attributes is invalid.
			Assertions.assertFalse(List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.INVALID));
			// Makes sure not all of the attributes are valid.
			Assertions.assertFalse((VerificationStatus.VALID == attribute1Status) && (VerificationStatus.VALID == attribute2Status)
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
			final VerificationStatus attribute1Status = Verifiable.getVerificationStatus(testObject, "attribute1");
			final VerificationStatus attribute2Status = Verifiable.getVerificationStatus(testObject, "attribute2");
			final VerificationStatus attribute3Status = Verifiable.getVerificationStatus(testObject, "attribute3");
			// Makes sure none of the attributes is invalid.
			Assertions.assertFalse(List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.INVALID));
			// Makes sure not all of the attributes are valid.
			Assertions.assertFalse((VerificationStatus.VALID == attribute1Status) && (VerificationStatus.VALID == attribute2Status)
					&& (VerificationStatus.VALID == attribute3Status));
			// Makes sure at least one of the attributes is dubious.
			Assertions.assertTrue(List.of(attribute1Status, attribute2Status, attribute3Status).contains(VerificationStatus.DUBIOUS));
			// Makes sure the object is not verified.
			Assertions.assertEquals(VerificationStatus.DUBIOUS, testObject.getVerificationStatus());
		}
	}

	/**
	 * Tests replacing similar items.
	 */
	@Test
	public void test04ReplaceSimilarItems() {
		// Adds a verification item replacing a similar one.
		final Verification verification = new Verification();
		verification.addItem(
				new VerificationItem(VerificationStatus.VALID, Set.of("attribute1"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"));
		Assertions.assertEquals(1, verification.getItems().size());
		verification.addItem(
				new VerificationItem(VerificationStatus.INVALID, Set.of("attribute1"), "me", DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc"));
		// Makes sure the item was replaced and it is the last one added.
		Assertions.assertEquals(1, verification.getItems().size());
		Assertions.assertEquals(VerificationStatus.INVALID, verification.getItems().get(0).getStatus());

		// Adds a verification question replacing a similar one.
		verification.addItem(new VerificationQuestion(VerificationStatus.INVALID, Set.of("attribute1"), "Qual seu nome?", List.of("João", "José"), "João",
				"José", "data", DateTimeHelper.getCurrentLocalDateTime().plusDays(1)));
		Assertions.assertEquals(2, verification.getItems().size());
		verification.addItem(new VerificationQuestion(VerificationStatus.VALID, Set.of("attribute1"), "Qual seu nome?", List.of("João", "José"), "Antonia",
				"Maria", "data", DateTimeHelper.getCurrentLocalDateTime().plusDays(1)));
		// Makes sure the item was replaced and it is the last one added.
		Assertions.assertEquals(2, verification.getItems().size());
		Assertions.assertEquals(VerificationStatus.VALID, verification.getItems().get(1).getStatus());

		// Adds a verification score replacing a similar one.
		verification.addItem(new VerificationScore(VerificationStatus.INVALID, Set.of("attribute1"), "data",
				DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc", "alg1", new BigDecimal("0.5")));
		verification.addItem(new VerificationScore(VerificationStatus.VALID, Set.of("attribute1"), "data", DateTimeHelper.getCurrentLocalDateTime().plusDays(1),
				"desc", "alg1", new BigDecimal("0.1")));
		// Makes sure the item was replaced and it is the last one added.
		Assertions.assertEquals(3, verification.getItems().size());
		Assertions.assertEquals(VerificationStatus.VALID, verification.getItems().get(2).getStatus());

		// Adds a different algorithm verification score.
		verification.addItem(new VerificationScore(VerificationStatus.INVALID, Set.of("attribute1"), "data",
				DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc", "alg2", new BigDecimal("0.5")));
		// Makes sure the item was added.
		Assertions.assertEquals(4, verification.getItems().size());

		// Adds a different data source score.
		verification.addItem(new VerificationScore(VerificationStatus.INVALID, Set.of("attribute1"), "data2",
				DateTimeHelper.getCurrentLocalDateTime().plusDays(1), "desc", "alg2", new BigDecimal("0.5")));
		// Makes sure the item was added.
		Assertions.assertEquals(5, verification.getItems().size());

	}

}
