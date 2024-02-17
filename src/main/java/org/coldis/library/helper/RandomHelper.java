package org.coldis.library.helper;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Random content helper.
 */
public class RandomHelper {

	/**
	 * Distinguishable alpha numeric characters.
	 */
	public static final String CLEAR_APLHANUMERIC_CHARS = "234679ACDEFGHJKMNPQRTVWXYZ";

	/**
	 * Characters.
	 */
	public static final String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890";

	/**
	 * Random.
	 */
	public static final Random RANDOM = new SecureRandom();

	/**
	 * Gets some random string.
	 *
	 * @param  numberOfCharacters Number of characters to be generated.
	 * @param  possibleCharacters Possible characters to be generated.
	 * @return                    Some random string.
	 */
	public static String getRandomString(
			final Integer numberOfCharacters,
			final String possibleCharacters) {
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
	 * Gets a positive random number (long).
	 *
	 * @param  maximum Maximum value.
	 * @return         a positive random number (long).
	 */
	public static Long getPositiveRandomLong(
			final Long maximum) {
		return Math.abs(maximum == null ? RandomHelper.RANDOM.nextLong() : RandomHelper.RANDOM.nextLong(maximum));
	}

	/**
	 * Gets a positive random number (long) as a string.
	 *
	 * @param  maximum Maximum value.
	 * @return         a positive random number (long) as a string.
	 */
	public static String getPositiveRandomLongAsString(
			final Long maximum) {
		return StringUtils.leftPad(RandomHelper.getPositiveRandomLong(maximum).toString(), maximum.toString().length(), '0');
	}

	/**
	 * Gets a random item from a collection.
	 *
	 * @param  <Type> The type of the items.
	 * @param  items  The collection of items.
	 * @return        A random item from the collection.C
	 */
	public static <Type> Type getRandomItem(
			final Collection<Type> items) {
		return (CollectionUtils.isEmpty(items) ? null : (Type) items.stream().skip(RandomHelper.RANDOM.nextInt(items.size())).findFirst().get());
	}

}
