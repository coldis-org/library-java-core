package org.coldis.library.helper;

import java.text.Collator;

/**
 * String helper.
 */
public class StringHelper {

	/**
	 * Compares if two strings are equal considering a given strength
	 * ({@link Collator}).
	 *
	 * @param source   Source string.
	 * @param target   Target string.
	 * @param strength Strength.
	 * @return If two strings are the equal considering a given strength
	 *         ({@link Collator}).
	 */
	public static Boolean equals(final String source, final String target, final Integer strength) {
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
	 * @param original Original object.
	 * @param limit    The size limit for a string.
	 * @param append   Some string to be appended informing the original string was
	 *                 truncated.
	 * @return The truncated string.
	 */
	public static String truncate(final Object original, final Integer limit, final String append) {
		// The truncated string is, initially, the original object (as string).
		String truncatedString = original == null ? null : original.toString();
		// Default append string is "" if none is given.
		final String actualAppend = append == null ? "" : append;
		// Actual size limit should include the append length.
		final Integer actualLimit = limit - actualAppend.length();
		// If the string size is greater than the size limit, truncates it and adds the
		// append.
		truncatedString = ((truncatedString != null) && (truncatedString.length() > actualLimit))
				? truncatedString.substring(0, actualLimit) + actualAppend
				: truncatedString;
		// Returns the truncated string.
		return truncatedString;
	}

	/**
	 * Removes multiple blank spaces.
	 *
	 * @param stringValue The original string.
	 * @return The updated string.
	 */
	public static final String removeMultipleBlankSpaces(final String stringValue) {
		return stringValue.trim().replaceAll("\\s\\s+", " ");
	}

	/**
	 * Verify if a URL is from a given domain.
	 *
	 * @param url                   The URL to look for.
	 * @param domainPattern         The domain pattern.
	 * @param acceptOneMoreDomainCN If one more domain common name is accepted.
	 * @return If a URL is from a given domain.
	 */
	public static Boolean isFromDomain(final String url, final String domainPattern,
			final Boolean acceptOneMoreDomainCN) {
		// By default, the URL is not from the given domain.
		Boolean isFromDomain = false;
		// If both the URL and the domain pattern are given.
		if ((url != null) && (domainPattern != null)) {
			// If the URL matches the domain pattern.
			isFromDomain = url.matches("http[s]{0,1}://[^ /\\?]*" + domainPattern
					+ (acceptOneMoreDomainCN ? ".[^ ./\\?]*" : "") + "($|[ /\\?].*)");
		}
		// Returns if the URL is from the given domain.
		return isFromDomain;
	}

}
