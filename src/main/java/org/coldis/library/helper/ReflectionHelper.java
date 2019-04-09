package org.coldis.library.helper;

/**
 * Reflection helper.
 */
public class ReflectionHelper {

	/**
	 * If a method is a getter.
	 *
	 * @param  methodName Method name.
	 * @return            If a method is a getter.
	 */
	public static Boolean isGetter(final String methodName) {
		return (methodName.startsWith("get") || methodName.startsWith("is"));
	}

	/**
	 * If a method is a getter.
	 *
	 * @param  methodName Method name.
	 * @param  parameters Parameters (or parameters classes) list.
	 * @return            If a method is a getter.
	 */
	public static Boolean isGetter(final String methodName, final Object[] parameters) {
		return ((parameters == null) || (parameters.length == 0)) && ReflectionHelper.isGetter(methodName);
	}

	/**
	 * Gets the original attribute name from a getter.
	 *
	 * @param  getterName Getter name.
	 * @return            The original attribute name.
	 */
	public static String getAttributeName(final String getterName) {
		// If it is a boolean getter.
		final Boolean booleanGetter = getterName.startsWith("is");
		// Returns attribute name.
		return getterName.substring(booleanGetter ? 2 : 3, booleanGetter ? 3 : 4).toLowerCase()
				+ getterName.substring(booleanGetter ? 3 : 4);
	}

}
