package org.coldis.library.helper;

import java.text.Collator;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * String helper.
 */
public class StringHelper {

	/**
	 * Brazilian phone number region prefix regular expression.
	 */
	public static final String BR_PHONE_NUMBER_REGION_PREFIX_REGEX = "((1[1-9])|(2[12478])|(3[1234578])|(4[1-9])|(5[1345])|(6[1-9])|(7[134579])|(8[1-9])|(9[1-9]))";

	/**
	 * Brazilian mobile phone number prefix regular expression.
	 */
	public static final String BR_MOBILE_PHONE_NUMBER_PREFIX_REGEX = "(9[0-9]{2})|(7[0789])";

	/**
	 * Brazilian land-line number prefix regular expression.
	 */
	public static final String BR_LANDLINE_PHONE_NUMBER_PREFIX_REGEX = "([2345][0-9])";

	/**
	 * Brazilian phone number suffix regular expression.
	 */
	public static final String BR_PHONE_NUMBER_SUFFIX_REGEX = "[0-9]{6}";

	/**
	 * Brazilian mobile phone number regular expression.
	 */
	public static final String BR_MOBILE_PHONE_NUMBER_REGEX = StringHelper.BR_PHONE_NUMBER_REGION_PREFIX_REGEX + "("
			+ StringHelper.BR_MOBILE_PHONE_NUMBER_PREFIX_REGEX + ")" + StringHelper.BR_PHONE_NUMBER_SUFFIX_REGEX;

	/**
	 * Brazilian land-line phone number regular expression.
	 */
	public static final String BR_LANDLINE_PHONE_NUMBER_REGEX = StringHelper.BR_PHONE_NUMBER_REGION_PREFIX_REGEX + "("
			+ StringHelper.BR_LANDLINE_PHONE_NUMBER_PREFIX_REGEX + ")" + StringHelper.BR_PHONE_NUMBER_SUFFIX_REGEX;

	/**
	 * Brazilian phone number regular expression.
	 */
	public static final String BR_PHONE_NUMBER_REGEX = StringHelper.BR_PHONE_NUMBER_REGION_PREFIX_REGEX + "("
			+ StringHelper.BR_LANDLINE_PHONE_NUMBER_PREFIX_REGEX + "|" + StringHelper.BR_MOBILE_PHONE_NUMBER_PREFIX_REGEX + ")"
			+ StringHelper.BR_PHONE_NUMBER_SUFFIX_REGEX;

	/**
	 * Compares if two strings are equal considering a given strength
	 * ({@link Collator}).
	 *
	 * @param  source   Source string.
	 * @param  target   Target string.
	 * @param  strength Strength.
	 * @return          If two strings are the equal considering a given strength
	 *                  ({@link Collator}).
	 */
	public static Boolean equals(
			final String source,
			final String target,
			final Integer strength) {
		// Creates a new collator.
		final Collator collator = Collator.getInstance();
		// Sets the collator strength.
		collator.setStrength(strength);
		// Returns if the strings are equal.
		return collator.compare(source, target) == 0;
	}

	/**
	 * Truncates an object (as string) for a given size limit.
	 *
	 * @param  original Original object.
	 * @param  limit    The size limit for a string.
	 * @param  append   Some string to be appended informing the original string was
	 *                      truncated.
	 * @return          The truncated string.
	 */
	public static String truncate(
			final Object original,
			final Integer limit,
			final String append) {
		// The truncated string is, initially, the original object (as string).
		String truncatedString = original == null ? null : original.toString();
		// Default append string is "" if none is given.
		final String actualAppend = append == null ? "" : append;
		// Actual size limit should include the append length.
		final Integer actualLimit = limit - actualAppend.length();
		// If the string size is greater than the size limit, truncates it and adds the
		// append.
		truncatedString = ((truncatedString != null) && (truncatedString.length() > actualLimit)) ? truncatedString.substring(0, actualLimit) + actualAppend
				: truncatedString;
		// Returns the truncated string.
		return truncatedString;
	}

	/**
	 * Removes multiple blank spaces.
	 *
	 * @param  stringValue The original string.
	 * @return             The updated string.
	 */
	public static final String removeMultipleBlankSpaces(
			final String stringValue) {
		return stringValue.trim().replaceAll("\\s\\s+", " ");
	}

	/**
	 * Verify if a URL is from a given domain.
	 *
	 * @param  url                   The URL to look for.
	 * @param  domainPattern         The domain pattern.
	 * @param  acceptOneMoreDomainCN If one more domain common name is accepted.
	 * @return                       If a URL is from a given domain.
	 */
	public static Boolean isFromDomain(
			final String url,
			final String domainPattern,
			final Boolean acceptOneMoreDomainCN) {
		// By default, the URL is not from the given domain.
		Boolean isFromDomain = false;
		// If both the URL and the domain pattern are given.
		if ((url != null) && (domainPattern != null)) {
			// If the URL matches the domain pattern.
			isFromDomain = url.matches("http[s]{0,1}://[^ /\\?]*" + domainPattern + (acceptOneMoreDomainCN ? ".[^ ./\\?]*" : "") + "($|[ /\\?].*)");
		}
		// Returns if the URL is from the given domain.
		return isFromDomain;
	}

	/**
	 * Removes accents from string.
	 *
	 * @param  stringValue The string value.
	 * @return             The string without accents.
	 */
	public static String removeAccents(
			final String stringValue) {
		final String normalizedString = Normalizer.normalize(stringValue, Normalizer.Form.NFD);
		final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(normalizedString).replaceAll("");
	}

	/**
	 * Removes non-alphanumeric characters from string.
	 *
	 * @param  stringValue The string value.
	 * @return             The string without non-alphanumeric characters.
	 */
	public static String removeNonAlphaNumericCharacters(
			final String stringValue) {
		final Pattern pattern = Pattern.compile("[^(\\w|\\s)]");
		return pattern.matcher(stringValue).replaceAll("");
	}

}
