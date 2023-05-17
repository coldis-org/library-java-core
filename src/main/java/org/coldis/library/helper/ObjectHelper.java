package org.coldis.library.helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
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
	public interface AttributeCopyConditionsPredicate<A, B> {

		/**
		 * Attribute copy conditions predicate.
		 *
		 * @param  getter      Getter.
		 * @param  sourceValue Source value.
		 * @param  targetValue Target value.
		 * @return             If the attribute should be copied.
		 */
		boolean shouldCopy(
				Method getter,
				A sourceValue,
				B targetValue);

	}

	/**
	 * TODO Javadoc
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
	 * @param  conditions                Conditions to copy the attribute.
	 * @param  targetAttributeSetter
	 * @param  attributeType
	 * @param  attributeName
	 * @param  sourceAttributeGetter
	 * @param  targetAttributeGetter
	 * @param  sourceAttributeValue
	 * @param  targetAttributeValue
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException    Javadoc
	 */
	private static <SourceType, TargetType> void copyAttribute(
			final SourceType source,
			final TargetType target,
			final Boolean deepCopy,
			final Boolean initializeEmptyAttributes,
			final Set<String> ignoreAttributes,
			final AttributeCopyConditionsPredicate<Object, Object> conditions,
			final Method targetAttributeSetter,
			final String attributeName) {
		// Only if both objects exist.
		if ((source != null) && (target != null)) {
			// Tries to copy the properties.
			try {

				// Checks if the type is a map.
				final boolean isSourceMap = Map.class.isAssignableFrom(source.getClass());
				final boolean isTargetMap = Map.class.isAssignableFrom(target.getClass());

				// Gets the source and target attribute values and types.
				final Method sourceAttributeGetter = (isSourceMap ? MethodUtils.getMatchingMethod(Map.class, "get", Object.class)
						: ReflectionHelper.getGetter(source, attributeName));
				final Method targetAttributeGetter = (isTargetMap ? MethodUtils.getMatchingMethod(Map.class, "get", Object.class)
						: ReflectionHelper.getGetter(target, attributeName));

				if ((targetAttributeGetter != null) && (targetAttributeSetter != null)) {

					final Object sourceAttributeValue = (isSourceMap ? sourceAttributeGetter.invoke(source, attributeName)
							: sourceAttributeGetter.invoke(source));
					Object targetAttributeValue = (isTargetMap ? targetAttributeGetter.invoke(target, attributeName) : targetAttributeGetter.invoke(target));
					final Class<?> sourceAttributeType = (sourceAttributeValue == null ? sourceAttributeGetter.getReturnType()
							: sourceAttributeValue.getClass());
					final Class<?> targetAttributeType = (targetAttributeValue == null ? targetAttributeGetter.getReturnType()
							: targetAttributeValue.getClass());

					// If the attribute should not be ignored.
					if (CollectionUtils.isEmpty(ignoreAttributes) || !ignoreAttributes.contains(attributeName)) {

						// Gets the attributes to ignore from the current attribute.
						final String attributePrefix = attributeName + ".";
						final Set<String> attributeIgnoreAttributes = (CollectionUtils.isEmpty(ignoreAttributes) ? null
								: ignoreAttributes.stream().filter(ignoredAttribute -> ignoredAttribute.startsWith(attributePrefix))
										.map(ignoreAttribute -> ignoreAttribute.substring(attributePrefix.length())).collect(Collectors.toSet()));

						// If the attribute type is complex.
						if (deepCopy && (sourceAttributeValue != null)
								&& (ObjectHelper.isComplexClass(targetAttributeType) || (Map.class.isAssignableFrom(sourceAttributeValue.getClass())
										&& ((Map<?, ?>) sourceAttributeValue).keySet().stream().allMatch(key -> key instanceof String)))) {
							if (targetAttributeValue == null) {
								final Constructor<?> targetAttributeConstructor = Arrays.stream(sourceAttributeValue.getClass().getConstructors())
										.filter(constructor -> constructor.getParameterCount() == 0).findAny().orElse(null);
								if (initializeEmptyAttributes && (targetAttributeValue == null) && (targetAttributeConstructor != null)
										&& targetAttributeConstructor.canAccess(null) && (targetAttributeConstructor.getParameterCount() == 0)) {
									// Tries creating a new object before copying the attributes.
									if (isTargetMap) {
										targetAttributeSetter.invoke(target, attributeName, targetAttributeConstructor.newInstance());
										targetAttributeValue = targetAttributeGetter.invoke(target, attributeName);
									}
									else {
										targetAttributeSetter.invoke(target, targetAttributeConstructor.newInstance());
										targetAttributeValue = targetAttributeGetter.invoke(target);
									}
								}
								else if (SortedMap.class.isAssignableFrom(targetAttributeType)) {
									// Tries creating a new object before copying the attributes.
									if (isTargetMap) {
										targetAttributeSetter.invoke(target, attributeName, new TreeMap<>());
										targetAttributeValue = targetAttributeGetter.invoke(target, attributeName);
									}
									else {
										targetAttributeSetter.invoke(target, new TreeMap<>());
										targetAttributeValue = targetAttributeGetter.invoke(target);
									}
								}
								else if (Map.class.isAssignableFrom(targetAttributeType)) {
									// Tries creating a new object before copying the attributes.
									if (isTargetMap) {
										targetAttributeSetter.invoke(target, attributeName, new HashMap<>());
										targetAttributeValue = targetAttributeGetter.invoke(target, attributeName);
									}
									else {
										targetAttributeSetter.invoke(target, new HashMap<>());
										targetAttributeValue = targetAttributeGetter.invoke(target);
									}
								}
							}
							// Copies the attributes recursively.
							if (targetAttributeValue != null) {
								ObjectHelper.copyAttributes(sourceAttributeValue, targetAttributeValue, deepCopy, initializeEmptyAttributes,
										attributeIgnoreAttributes, conditions);
							}
						}
						// If it is a simple type or no deep copy is set.
						else {
							// If the source and target conditions are met.
							if (((conditions == null) || conditions.shouldCopy(targetAttributeGetter, sourceAttributeValue, targetAttributeValue))) {
								// Copies the attribute.
								if (isTargetMap) {
									targetAttributeSetter.invoke(target, attributeName, sourceAttributeValue);
								}
								else {
									targetAttributeSetter.invoke(target, sourceAttributeValue);
								}
							}
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
	 * @param  conditions                Conditions to copy the attribute.
	 * @return                           The target object after the copy.
	 */
	@SuppressWarnings("unchecked")
	public static <SourceType, TargetType> TargetType copyAttributes(
			final SourceType source,
			final TargetType target,
			final Boolean deepCopy,
			final Boolean initializeEmptyAttributes,
			final Set<String> ignoreAttributes,
			final AttributeCopyConditionsPredicate<Object, Object> conditions) {
		// Only if both objects exist.
		if ((source != null) && (target != null)) {
			// If it is a map.
			if (Map.class.isAssignableFrom(target.getClass())) {
				// For each entry in the map.
				for (final Map.Entry<?, ?> entry : ((Map<?, ?>) source).entrySet()) {
					// Gets the attribute name and type.
					if (entry.getKey() instanceof String) {
						ObjectHelper.copyAttribute(source, target, deepCopy, initializeEmptyAttributes, ignoreAttributes, conditions,
								MethodUtils.getMatchingMethod(Map.class, "put", Object.class, Object.class), (String) entry.getKey());
					}
				}
			}
			else {
				// For each setter in the target.
				for (final Method targetSetter : target.getClass().getMethods()) {
					if (ReflectionHelper.isSetter(targetSetter)) {
						// Gets the attribute name and type.
						final String attributeName = ReflectionHelper.getAttributeName(targetSetter.getName());
						ObjectHelper.copyAttribute(source, target, deepCopy, initializeEmptyAttributes, ignoreAttributes, conditions, targetSetter,
								attributeName);
					}
				}
			}
		}
		// Returns the target object.
		return target;
	}

}
