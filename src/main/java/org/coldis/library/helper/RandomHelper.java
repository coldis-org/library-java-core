package org.coldis.library.helper;

import java.util.Random;

/**
 * Random content helper.
 */
public class RandomHelper {

	/**
	 * Distinguishable alpha numeric characters.
	 */
	public static final String CLEAR_APLHANUMERIC_CHARS = "234679ACDEFGHJKMNPQRTVWXYZ";

	/**
	 * Numbers.
	 */
	public static final String NUMBERS = "0123456789";

	/**
	 * Characters.
	 */
	public static final String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890";

	/**
	 * Random.
	 */
	private static final Random RANDOM = new Random();

	/**
	 * Gets some random string.
	 *
	 * @param  numberOfCharacters Number of characters to be generated.
	 * @param  possibleCharacters Possible characters to be generated.
	 * @return                    Some random string.
	 */
	public static String getRandomString(final Integer numberOfCharacters, final String possibleCharacters) {
		// Random string.
		final StringBuffer randomCode = new StringBuffer();
		// For the desired number of characters.
		for (Integer currentChar = 0; currentChar < numberOfCharacters; currentChar++) {
			// Appends a random character to the string.
			randomCode.append(possibleCharacters.charAt(RandomHelper.RANDOM.nextInt(possibleCharacters.length())));
		}
		// Returns the random string.
		return randomCode.toString();
	}

	/**
	 * Gets some random number.
	 *
	 * @param  numberOfNumbers Number of numbers to be generated.
	 * @return                 Some random number.
	 */
	public static String getRandomNumber(final Integer numberOfNumbers) {
		return RandomHelper.getRandomString(numberOfNumbers, RandomHelper.NUMBERS);
	}

}
