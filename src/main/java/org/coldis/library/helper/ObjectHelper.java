package org.coldis.library.helper;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * String helper.
 */
public class ObjectHelper {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectHelper.class);

	/**
	 * Java packages regex.
	 */
	public static final String NON_JAVA_PACKAGES_REGEX = "^(?!(java\\.)).*";

	/**
	 * If it is a class for a complex object.
	 *
	 * @param  clazz                  Class.
	 * @param  complexClassesPackages Complex classes packages regular expressions.
	 * @return                        If it is a class for a complex object.
	 */
	public static Boolean isComplexClass(
			final Class<?> clazz,
			final Set<String> complexClassesPackages) {
		// By default the class is not complex.
		boolean complexClass = false;
		// If the class is not primitive.
		if (!clazz.isPrimitive() && !clazz.isAnnotation() && !clazz.isArray() && !clazz.isEnum()) {
			// If the class matches the complex classes packages.
			if (!CollectionUtils.isEmpty(complexClassesPackages)
					&& complexClassesPackages.stream().anyMatch(complexClassesPackage -> clazz.getPackageName().matches(complexClassesPackage))) {
				// The class is complex.
				complexClass = true;
			}
		}
		// Returns if the class is complex.
		return complexClass;
	}

	/**
	 * If it is a class for a complex object.
	 *
	 * @param  clazz Class.
	 * @return       If it is a class for a complex object.
	 */
	public static Boolean isComplexClass(
			final Class<?> clazz) {
		return ObjectHelper.isComplexClass(clazz, Set.of(ObjectHelper.NON_JAVA_PACKAGES_REGEX));
	}

	/**
	 * Returns if object is empty.
	 *
	 * @param  value Value.
	 * @return       If object is empty.
	 */
	public static <T> boolean isEmpty(
			final T value) {
		return (value == null) || ((value instanceof CharSequence) && StringUtils.isBlank((CharSequence) value))
				|| (((value instanceof Collection<?>) && CollectionUtils.isEmpty((Collection<?>) value))
						|| ((value instanceof Object[]) && ArrayUtils.isEmpty((Object[]) value))
						|| ((value instanceof char[]) && ArrayUtils.isEmpty((char[]) value)) || ((value instanceof int[]) && ArrayUtils.isEmpty((int[]) value))
						|| ((value instanceof long[]) && ArrayUtils.isEmpty((long[]) value))
						|| ((value instanceof double[]) && ArrayUtils.isEmpty((double[]) value))
						|| ((value instanceof float[]) && ArrayUtils.isEmpty((float[]) value))
						|| ((value instanceof short[]) && ArrayUtils.isEmpty((short[]) value))
						|| ((value instanceof boolean[]) && ArrayUtils.isEmpty((boolean[]) value))
						|| ((value instanceof byte[]) && ArrayUtils.isEmpty((byte[]) value)));
	}

	/**
	 * Attribute copy conditions predicate.
	 */
	@FunctionalInterface
	public interface AttributeCopyConditionsPredicate {

		/**
		 * Attribute copy conditions predicate.
		 *
		 * @param  getter      Getter.
		 * @param  sourceValue Source value.
		 * @param  targetValue Target value.
		 * @return             If the attribute should be copied.
		 */
		<A, B> boolean shouldCopy(
				Method getter,
				A sourceValue,
				B targetValue);

	}

	/**
	 * Copies properties from a source to a target object.
	 *
	 * @param  <SourceType>              Source type.
	 * @param  <TargetType>              Target type.
	 * @param  source                    Source.
	 * @param  target                    Target.
	 * @param  deepCopy                  If deep copy should be made (complex
	 *                                       objects attributes should be copied
	 *                                       individually).
	 * @param  initializeEmptyAttributes If empty attributes should be initialized
	 *                                       with empty constructors for complex
	 *                                       objects on deep copy (if target
	 *                                       attribute value is currently null).
	 * @param  ignoreAttributes          Attributes names to be ignored when copied.
	 * @param  sourceConditions          Conditions using the source getter and
	 *                                       value to copy the attribute.
	 * @param  targetConditions          Conditions using the target getter and
	 *                                       value to copy the attribute.
	 * @return                           The target object after the copy.
	 */
	public static <SourceType, TargetType> TargetType copyAttributes(
			final SourceType source,
			final TargetType target,
			final Boolean deepCopy,
			final Boolean initializeEmptyAttributes,
			final Set<String> ignoreAttributes,
			final AttributeCopyConditionsPredicate conditions) {
		// Only if both objects exist.
		if ((source != null) && (target != null)) {
			// For each setter in the target.
			for (final Method targetSetter : target.getClass().getMethods()) {
				if (ReflectionHelper.isSetter(targetSetter.getName())) {
					// If the setter has one parameter.
					if (targetSetter.getParameterTypes().length == 1) {
						// Gets the attribute name and type.
						final String attributeName = ReflectionHelper.getAttributeName(targetSetter.getName());
						final Class<?> attributeType = targetSetter.getParameterTypes()[0];
						// Source getter.
						Method sourceGetter = null;
						// Gets the getter for the me type.
						try {
							sourceGetter = source.getClass().getMethod(ReflectionHelper.getGetterName(attributeName));
						}
						// If the cannot cannot be found.
						catch (final Exception exception) {
							// Logs it.
							ObjectHelper.LOGGER.debug("Source getter not found for attribute '" + attributeName + "'.", exception);
						}
						// Target getter.
						Method targetGetter = null;
						// Gets the getter for the me type.
						try {
							targetGetter = target.getClass().getMethod(ReflectionHelper.getGetterName(attributeName));
						}
						// If the cannot cannot be found.
						catch (final Exception exception) {
							// Logs it.
							ObjectHelper.LOGGER.debug("Target getter not found for attribute '" + attributeName + "'.", exception);
						}

						// If there is a getter.
						if ((targetGetter != null) && targetGetter.getReturnType().equals(attributeType)) {
							// Tries to copy the properties.
							try {
								// Gets both the source and target attribute values.
								final Object sourceAttributeValue = sourceGetter.invoke(source);
								Object targetAttributeValue = targetGetter.invoke(target);
								// If the attribute should not be ignored.
								if (CollectionUtils.isEmpty(ignoreAttributes) || !ignoreAttributes.contains(attributeName)) {
									// If the attribute type is complex.
									if (deepCopy && ObjectHelper.isComplexClass(attributeType)) {
										// If the target attribute value is null, the source is not and it should be
										// created.
										if (initializeEmptyAttributes && (targetAttributeValue == null) && (sourceAttributeValue != null)) {
											// Tries creating a new object before copying the attributes.
											targetSetter.invoke(target, targetGetter.getReturnType().getConstructor().newInstance());
											targetAttributeValue = targetGetter.invoke(target);
										}
										// Gets the attributes to ignore from the current attribute.
										final String attributePrefix = attributeName + ".";
										final Set<String> attributeIgnoreAttributes = (CollectionUtils.isEmpty(ignoreAttributes) ? null
												: ignoreAttributes.stream().filter(ignoredAttribute -> ignoredAttribute.startsWith(attributePrefix))
														.map(ignoreAttribute -> ignoreAttribute.substring(attributePrefix.length()))
														.collect(Collectors.toSet()));
										// Copies the attributes recursively.
										ObjectHelper.copyAttributes(sourceAttributeValue, targetAttributeValue, deepCopy, initializeEmptyAttributes,
												attributeIgnoreAttributes, conditions);
									}
									// If it is a simple type or no deep copy is set.
									else {
										// If the source and target conditions are met.
										if (((conditions == null) || conditions.shouldCopy(sourceGetter, sourceAttributeValue, targetAttributeValue))) {
											// Copies the attribute.
											targetSetter.invoke(target, sourceAttributeValue);
										}
									}
								}
							}
							// If the attribute cannot be copied.
							catch (final Exception exception) {
								// Logs it.
								ObjectHelper.LOGGER.debug("Attribute '" + attributeName + "' could not be copied.", exception);
							}
						}
					}
				}
			}
		}
		// Returns the target object.
		return target;
	}

}
