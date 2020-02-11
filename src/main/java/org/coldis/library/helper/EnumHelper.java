package org.coldis.library.helper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import org.coldis.library.exception.IntegrationException;
import org.coldis.library.model.SimpleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enum helper.
 */
public class EnumHelper {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EnumHelper.class);

	/**
	 * Gets a enum value by a given attribute value (between a range of two
	 * properties).
	 *
	 * @param  <EnumType>         Enum type.
	 * @param  <AttributeType>    Attribute type.
	 * @param  enumType           Enum type.
	 * @param  floorAttributeName Floor enum attribute name.
	 * @param  ceilAttributeName  Ceiling enum attribute name.
	 * @param  attributeValue     Attribute value.
	 * @return                    The enum value by a given attribute value.
	 */
	@SuppressWarnings("unchecked")
	public static <EnumType extends Enum<EnumType>, AttributeType> EnumType getByRange(final Class<EnumType> enumType,
			final String floorAttributeName, final String ceilAttributeName, final AttributeType attributeValue) {
		// Return enum value is null by default.
		EnumType enumValue = null;
		// Tries to get the value for the given attributeValue.
		try {
			// Gets the getter for the floor attribute.
			final Method floorGetter = enumType.getMethod(ReflectionHelper.getGetterName(floorAttributeName));
			// Gets the getter for the ceiling attribute.
			final Method ceilGetter = enumType.getMethod(ReflectionHelper.getGetterName(ceilAttributeName));
			// Gets the enum for the floor and ceil attributes.
			enumValue = Arrays.stream(enumType.getEnumConstants()).filter(enumConstant -> {
				// Tries to return if the attribute has the given value.
				try {
					return (((Comparable<AttributeType>) floorGetter.invoke(enumConstant))
							.compareTo(attributeValue) <= 0)
							&& (((Comparable<AttributeType>) ceilGetter.invoke(enumConstant))
									.compareTo(attributeValue) > 0);
				}
				// If there is a problem getting the attribute value.
				catch (final Exception exception) {
					// The constant is considered not to match the constraints.
					return false;
				}
			}).findFirst().orElse(null);
		}
		// If there is a problem getting the attribute getter.
		catch (final Exception exception) {
			// Ignores the error.
			EnumHelper.LOGGER.error("Enum '" + enumType + "' could not be gathered for range '" + floorAttributeName
					+ "/" + ceilAttributeName + "' and value '" + attributeValue + "': "
					+ exception.getLocalizedMessage());
			EnumHelper.LOGGER.debug("Enum '" + enumType + "' could not be gathered for range '" + floorAttributeName
					+ "/" + ceilAttributeName + "' and value '" + attributeValue + "': ", exception);
		}
		// Returns the enum value for the given attribute.
		return enumValue;

	}

	/**
	 * Gets a enum value by a given attribute value.
	 *
	 * @param  <EnumType>     Enum type.
	 * @param  enumType       Enum type.
	 * @param  attributeName  Attribute name.
	 * @param  attributeValue Attribute value.
	 * @return                The enum value by a given attribute value.
	 */
	public static <EnumType extends Enum<EnumType>> EnumType getByAttribute(final Class<EnumType> enumType,
			final String attributeName, final Object attributeValue) {
		// Return enum value is null by default.
		EnumType enumValue = null;
		try {
			// Gets the getter for the attribute.
			final Method getter = enumType.getMethod(ReflectionHelper.getGetterName(attributeName));
			// Gets the enum for the attribute value.
			enumValue = Arrays.stream(enumType.getEnumConstants()).filter(enumConstant -> {
				// Tries to return if the attribute has the given value.
				try {
					return Objects.equals(attributeValue, getter.invoke(enumConstant));
				}
				// If there is a problem getting the attribute value.
				catch (final Exception exception) {
					// The constant is considered not to match the constraints.
					return false;
				}
			}).findFirst().orElse(null);
		}
		// If there is a problem getting the attribute getter.
		catch (final Exception exception) {
			// Ignores the error.
			EnumHelper.LOGGER.error("Enum '" + enumType + "' could not be gathered for name '" + attributeName
					+ "' and value '" + attributeValue + "': " + exception.getLocalizedMessage());
			EnumHelper.LOGGER.debug("Enum '" + enumType + "' could not be gathered for name '" + attributeName
					+ "' and value '" + attributeValue + "'.", exception);
		}
		// Returns the enum value for the given attribute.
		return enumValue;

	}

	/**
	 * Gets a enum value by id (id attribute).
	 *
	 * @param  <EnumType>     Enum type.
	 * @param  enumType       Enum type.
	 * @param  attributeValue Attribute value.
	 * @return                The enum value by id (id attribute).
	 */
	public static <EnumType extends Enum<EnumType>> EnumType getById(final Class<EnumType> enumType,
			final Object attributeValue) {
		return EnumHelper.getByAttribute(enumType, "id", attributeValue);
	}

	/**
	 * Converts an enum to string using the following format
	 * "class.name.value.name".
	 *
	 * @param  enumValue  Enum value.
	 * @param  <EnumType> Enum type.
	 *
	 * @return            The enum as string.
	 */
	public static <EnumType extends Enum<EnumType>> String toString(final EnumType enumValue) {
		return (enumValue.getClass().getSimpleName().replaceAll("([^^])([A-Z])", "$1.$2") + "."
				+ enumValue.name().replace("_", ".")).toLowerCase();
	}

	/**
	 * Gets the value of a enum by name.
	 *
	 * @param  <EnumType>     The enum type.
	 * @param  enumType       The enum type.
	 * @param  name           The enum name.
	 * @param  resumeOnErrors If errors should be silently ignored.
	 * @return                The enum valu for the type and name.
	 */
	public static <EnumType extends Enum<EnumType>> EnumType valueOf(final Class<EnumType> enumType, final String name,
			final Boolean resumeOnErrors) {
		// Value of enum string.
		EnumType enumValue = null;
		// Tries to get the value of the enum string.
		try {
			enumValue = Enum.valueOf(enumType, name);
		}
		// If there is no value for the enum.
		catch (final Exception exception) {
			// If errors should be silently ignored.
			if (resumeOnErrors) {
				// Logs and returns null.
				EnumHelper.LOGGER.debug(
						"Supressed: invalid enum value for type: '" + enumType + "' and name '" + name + "'.",
						exception);
			}
			// If errors should not be silently ignored.
			else {
				// Throws an invalid name exception.
				throw new IntegrationException(new SimpleMessage("enum.name.invalid"), exception);
			}
		}
		// Returns the enum value.
		return enumValue;
	}

}
