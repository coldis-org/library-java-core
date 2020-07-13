package org.coldis.library.helper;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
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
	public static Boolean isComplexClass(final Class<?> clazz, final Set<String> complexClassesPackages) {
		// By default the class is not complex.
		Boolean complexClass = false;
		// If the class is not primitive.
		if (!clazz.isPrimitive()) {
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
	public static Boolean isComplexClass(final Class<?> clazz) {
		return ObjectHelper.isComplexClass(clazz, Set.of(ObjectHelper.NON_JAVA_PACKAGES_REGEX));
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
	public static <SourceType, TargetType> TargetType copyAttributes(final SourceType source, final TargetType target, final Boolean deepCopy,
			final Boolean initializeEmptyAttributes, final Set<String> ignoreAttributes, final BiPredicate<Method, Object> sourceConditions,
			final BiPredicate<Method, Object> targetConditions) {
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
												attributeIgnoreAttributes, sourceConditions, targetConditions);
									}
									// If it is a simple type or no deep copy is set.
									else {
										// If the source and target conditions are met.
										if (((sourceConditions == null) || sourceConditions.test(sourceGetter, sourceAttributeValue))
												&& ((targetConditions == null) || targetConditions.test(targetGetter, targetAttributeValue))) {
											// Copies the attribute.
											targetSetter.invoke(target, sourceAttributeValue);
										}
									}
								}
							}
							// If the attribute cannot be copied.
							catch (final Exception exception) {
								// Logs it.
								ObjectHelper.LOGGER.error("Attribute '" + attributeName + "' could not be copied: " + exception.getLocalizedMessage());
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
