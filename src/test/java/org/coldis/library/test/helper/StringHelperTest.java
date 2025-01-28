package org.coldis.library.test.helper;

import org.coldis.library.helper.StringHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * String helper test.
 */
public class StringHelperTest {

	/**
	 * Valid landline phones.
	 */
	private static final String[] VALID_BR_LANDLINE_PHONES = { "1122334455", "1233445566", "1344556677", "1455667788", "3422334455", "3533445566", "3744556677",
			"3855667788", "4122334455", "4233445566", "4344556677", "4455667788", "4522334455", "4633445566", "4744556677", "4855667788", "4955667788",
			"5122334455", "5344556677", "5455667788", "5522334455", "6122334455", "6233445566", "6344556677", "6455667788", "6522334455", "6633445566",
			"6744556677", "6855667788", "6955667788", "7122334455", "7344556677", "7455667788", "7522334455", "7744556677", "7955667788", "8122334455",
			"8233445566", "8344556677", "8455667788", "8522334455", "8633445566", "8744556677", "8855667788", "8955667788", "9122334455", "9233445566",
			"9344556677", "9455667788", "9522334455", "9633445566", "9744556677", "9855667788", "9955667788" };

	/**
	 * Valid mobile phones.
	 */
	private static final String[] VALID_BR_MOBILE_PHONES = { "15999881166", "16988776655", "17977665544", "18966554433", "19955443322", "21944332211",
			"22933221100", "24922110099", "27911009988", "27900998877", "2870112233", "3177889966", "3278990011", "3379887766" };

	/**
	 * Invalid phones.
	 */
	private static final String[] INVALID_BR_PHONES = { "219998811666", "2498877655", "119776644", "1296655984433", "23955443322", "25944332211", "29933221100",
			"36922110099", "5239911009988", "56900998877", "5770112233", "5877889966", "5978990011", "7279887766", "7622334455", "7833445566", "1371223344",
			"1472334455", "1573445566", "1674556677", "1775667788", "1876778899", "1022334455", "2033445566", "3044556677", "4055667788", "5022334455",
			"6033445566", "7044556677", "8055667788", "9022334455", };

	/**
	 * Tests Brazilian phone numbers.
	 */
	@Test
	public void testBrazilianPhoneNumbers() {
		// For each valid land-line phone number.
		for (final String validNumber : StringHelperTest.VALID_BR_LANDLINE_PHONES) {
			// Asserts that the numbers are valid.
			Assertions.assertTrue(validNumber.matches(StringHelper.BR_LANDLINE_PHONE_NUMBER_REGEX), validNumber);
			Assertions.assertTrue(validNumber.matches(StringHelper.BR_PHONE_NUMBER_REGEX), validNumber);
			Assertions.assertFalse(validNumber.matches(StringHelper.BR_MOBILE_PHONE_NUMBER_REGEX), validNumber);
		}
		// For each valid mobile phone number.
		for (final String validNumber : StringHelperTest.VALID_BR_MOBILE_PHONES) {
			// Asserts that the numbers are valid.
			Assertions.assertFalse(validNumber.matches(StringHelper.BR_LANDLINE_PHONE_NUMBER_REGEX), validNumber);
			Assertions.assertTrue(validNumber.matches(StringHelper.BR_PHONE_NUMBER_REGEX), validNumber);
			Assertions.assertTrue(validNumber.matches(StringHelper.BR_MOBILE_PHONE_NUMBER_REGEX), validNumber);
		}
		// For each invalid phone number.
		for (final String invalidNumber : StringHelperTest.INVALID_BR_PHONES) {
			// Asserts that the numbers are invalid.
			Assertions.assertFalse(invalidNumber.matches(StringHelper.BR_LANDLINE_PHONE_NUMBER_REGEX), invalidNumber);
			Assertions.assertFalse(invalidNumber.matches(StringHelper.BR_PHONE_NUMBER_REGEX), invalidNumber);
			Assertions.assertFalse(invalidNumber.matches(StringHelper.BR_MOBILE_PHONE_NUMBER_REGEX), invalidNumber);
		}

	}

	/**
	 * Tests the removal of accents.
	 */
	@Test
	public void testRemoveAccents() {
		// Given a text with accents.
		final String input = "Boleto 2 - Empréstimo 1,042,836";
		final String expected = "Boleto 2 - Emprestimo 1,042,836";

		// Validates the removal of accents.
		Assertions.assertEquals(expected, StringHelper.removeAccents(input));
		Assertions.assertEquals(expected, StringHelper.removeAccents(expected));
	}

	/**
	 * Tests the removal of non-alphanumeric characters.
	 */
	@Test
	public void testRemoveNonAlphanumericChars() {
		// Given a text with accents.
		final String input = "Boleto 2 - Emprestimo 1,042,836";
		final String expected = "Boleto 2  Emprestimo 1042836";

		// Validates the removal of accents.
		Assertions.assertEquals(expected, StringHelper.removeNonAlphaNumericCharacters(input));
		Assertions.assertEquals(expected, StringHelper.removeNonAlphaNumericCharacters(expected));
	}

	/**
	 * Tests the removal of non-alphanumeric characters.
	 */
	@Test
	public void testEmailValidation() {
		Assertions.assertTrue(StringHelper.hasValidEmailDomain("romulo@gmail.com"));
		Assertions.assertTrue(StringHelper.hasValidEmailDomain("romulo@gamil.com"));
		Assertions.assertFalse(StringHelper.hasValidEmailDomain("romulo@gmail.comn"));
		Assertions.assertFalse(StringHelper.hasValidEmailDomain("romulo@gmail.con"));
		Assertions.assertEquals("romulo@gmail.com", StringHelper.fixEmailDomainTypos("romulo@gmail.com"));
		Assertions.assertEquals("romulo@gmail.com", StringHelper.fixEmailDomainTypos("romulo@gmail.comn"));
		Assertions.assertEquals("romulo@gmail.com", StringHelper.fixEmailDomainTypos("romulo@gmail.con"));
		Assertions.assertEquals("romulo@gmail.com.br", StringHelper.fixEmailDomainTypos("romulo@gmail.combr", false));
		Assertions.assertEquals("romulo@gmail.com", StringHelper.fixEmailDomainTypos("romulo@gail.con", false));
		Assertions.assertEquals("romulo@gmail.com", StringHelper.fixEmailDomainTypos("romulo@gmil.con", false));
		Assertions.assertEquals("romulo@gmail.com", StringHelper.fixEmailDomainTypos("romulo@gmail.con", false));
		Assertions.assertEquals("romulo@gamil.com", StringHelper.fixEmailDomainTypos("romulo@gamil.com", false));
		Assertions.assertEquals("romulo@gmail.com", StringHelper.fixEmailDomainTypos("romulo@gamil.com", true));
	}

}
