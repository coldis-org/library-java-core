package org.coldis.library.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.coldis.library.exception.IntegrationException;
import org.coldis.library.model.SimpleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reflection helper.
 */
public class ReflectionHelper {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionHelper.class);

	/**
	 * If a method is a getter.
	 *
	 * @param  methodName Method name.
	 * @return            If a method is a getter.
	 */
	public static Boolean isGetter(
			final String methodName) {
		return (methodName.startsWith("get") || methodName.startsWith("is"));
	}

	/**
	 * If a method is a getter.
	 *
	 * @param  methodName Method name.
	 * @param  parameters Parameters (or parameters classes) list.
	 * @return            If a method is a getter.
	 */
	public static Boolean isGetter(
			final Method method) {
		return (ReflectionHelper.isGetter(method.getName()) && ((method.getParameterTypes() == null) || (method.getParameterTypes().length == 0)));
	}

	/**
	 * Gets the attribute getter.
	 *
	 * @param  <Type>        Type.
	 * @param  object        Object.
	 * @param  attributeName Attribute name.
	 * @return               The attribute getter.
	 */
	public static <Type> Method getGetter(
			final Type object,
			final String attributeName) {
		Method attributeGetter = null;
		// Gets the getter for the type.
		try {
			attributeGetter = object.getClass().getMethod(ReflectionHelper.getGetterName(attributeName));
		}
		// If the cannot cannot be found.
		catch (final Exception exception) {
			// Logs it.
			ReflectionHelper.LOGGER.debug("Source getter not found for attribute '" + attributeName + "'.", exception);
		}
		return attributeGetter;
	}

	/**
	 * If a method is a setter.
	 *
	 * @param  methodName Method name.
	 * @return            If a method is a setter.
	 */
	public static Boolean isSetter(
			final String methodName) {
		return (methodName.startsWith("set"));
	}

	/**
	 * If a method is a setter.
	 *
	 * @param  methodName Method name.
	 * @return            If a method is a setter.
	 */
	public static Boolean isSetter(
			final Method method) {
		return (ReflectionHelper.isSetter(method.getName()) && ((method.getParameterTypes() != null) && (method.getParameterTypes().length == 1)));
	}

	/**
	 * Gets the attribute getter. FIXME Fix before making public
	 *
	 * @param  <Type>        Type.
	 * @param  object        Object.
	 * @param  attributeName Attribute name.
	 * @return               The attribute getter.
	 */
	public static <Type> Method getSetter(
			final Type object,
			final String attributeName) {
		Method attributeSetter = null;
		// Gets the setter for the type.
		try {
			attributeSetter = object.getClass().getMethod(ReflectionHelper.getSetterName(attributeName));
		}
		// If the cannot cannot be found.
		catch (final Exception exception) {
			// Logs it.
			ReflectionHelper.LOGGER.debug("Source setter not found for attribute '" + attributeName + "'.", exception);
		}
		return attributeSetter;
	}

	/**
	 * Gets the original attribute name from a getter.
	 *
	 * @param  getterName Getter name.
	 * @return            The original attribute name.
	 */
	public static String getAttributeName(
			final String getterName) {
		// If it is a boolean getter.
		final Boolean booleanGetter = getterName.startsWith("is");
		// Returns attribute name.
		return getterName.substring(booleanGetter ? 2 : 3, booleanGetter ? 3 : 4).toLowerCase() + getterName.substring(booleanGetter ? 3 : 4);
	}

	/**
	 * Gets the getter name from an attribute name.
	 *
	 * @param  attributeName Attribute name.
	 * @return               The getter name from an attribute name.
	 */
	public static String getGetterName(
			final String attributeName) {
		return "get" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
	}

	/**
	 * Gets the setter name from an attribute name.
	 *
	 * @param  attributeName Attribute name.
	 * @return               The setter name from an attribute name.
	 */
	public static String getSetterName(
			final String attributeName) {
		return "set" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
	}

	/**
	 * Gets an object attribute.
	 *
	 * @param  object            Object.
	 * @param  attributeNamePath Attribute name path.
	 * @return                   The object attribute value.
	 */
	public static Object getAttribute(
			final Object object,
			final Boolean fieldAccess,
			final String attributeNamePath) {
		// Attribute path value.
		Object attributePathValue = object;
		// If the object and attribute name are given.
		if ((object != null) && (StringUtils.isNotBlank(attributeNamePath))) {
			// Splits the attributes.
			final String[] attributePath = attributeNamePath.split("\\.");
			// For each attribute in the path.
			for (Integer attributePathPartIndex = 0; attributePathPartIndex < attributePath.length; attributePathPartIndex++) {
				// Gets the attribute path part.
				final String attributePathPart = attributePath[attributePathPartIndex];
				// If the attribute value path is still valid.
				if (attributePathValue != null) {
					// Tries to get the next path value.
					try {
						if (attributePathValue instanceof Map) {
							attributePathValue = ((Map<?, ?>) attributePathValue).get(attributePathPart);
						}
						else if ((attributePathValue instanceof Object[]) && NumberUtils.isDigits(attributePathPart)) {
							attributePathValue = ArrayUtils.get((Object[]) attributePathValue, Integer.parseInt(attributePathPart));
						}
						else if (fieldAccess) {
							attributePathValue = FieldUtils.getField(attributePathValue.getClass(), attributePathPart, true).get(attributePathValue);
						}
						else {
							attributePathValue = MethodUtils.invokeMethod(attributePathValue, ReflectionHelper.getGetterName(attributePathPart));
						}
					}
					// If the attribute path cannot be retrieved.
					catch (final Exception exception) {
						ReflectionHelper.LOGGER.debug("Attribute path part value cannot be retrieved.", exception);
						attributePathValue = null;
						break;
					}
				}
			}
		}
		// Returns the final attribute value.
		return attributePathValue;
	}

	/**
	 * Gets an object attribute.
	 *
	 * @param  object            Object.
	 * @param  attributeNamePath Attribute name path.
	 * @return                   The object attribute value.
	 */
	public static Object getAttribute(
			final Object object,
			final String attributeNamePath) {
		return ReflectionHelper.getAttribute(object, false, attributeNamePath);
	}

	/**
	 * Replace an object attribute.
	 *
	 * @param object            Object.
	 * @param attributeNamePath Attribute name path.
	 * @param newValue          New attribute value.
	 */
	@SuppressWarnings("unchecked")
	public static void setAttribute(
			final Object object,
			final Boolean fieldAccess,
			final String attributeNamePath,
			final Object newValue) {
		// If the object and attribute name are given.
		if ((object != null) && (attributeNamePath != null)) {
			// Splits the last attribute.
			final String setAttributePath = attributeNamePath.substring(attributeNamePath.lastIndexOf('.') + 1);
			final String getAttributePath = attributeNamePath.substring(0, attributeNamePath.length() - setAttributePath.length());
			if (setAttributePath != null) {
				// Gets the attribute path.
				final Object attributeObject = ReflectionHelper.getAttribute(object, fieldAccess, getAttributePath);
				// Tries to set the attribute value.
				try {
					if (attributeObject instanceof Map) {
						((Map<String, Object>) attributeObject).put(setAttributePath, newValue);
					}
					else if (fieldAccess) {
						FieldUtils.getField(attributeObject.getClass(), setAttributePath, true).set(attributeObject, newValue);
					}
					else {
						MethodUtils.invokeMethod(attributeObject, ReflectionHelper.getSetterName(setAttributePath), newValue);
					}
				}
				// If the method cannot be found, logs it.
				catch (final Exception exception) {
					ReflectionHelper.LOGGER.debug("Attribute value cannot be updated.", exception);
				}
			}
		}
	}

	/**
	 * Replace an object attribute.
	 *
	 * @param object            Object.
	 * @param attributeNamePath Attribute name path.
	 * @param newValue          New attribute value.
	 */
	public static void setAttribute(
			final Object object,
			final String attributeNamePath,
			final Object newValue) {
		ReflectionHelper.setAttribute(object, false, attributeNamePath, newValue);
	}

	/**
	 * Gets the class annotation value from an annotation (ignoring type mirror
	 * exceptions).
	 *
	 * @param  <AnnotationType> Annotation type.
	 * @param  annotation       Annotation.
	 * @param  attribute        Attribute.
	 * @return                  The class annotation value from an annotation
	 *                          (ignoring type mirror exceptions).
	 */
	public static <AnnotationType extends Annotation> Class<?> getAnnotationClassAttribute(
			final AnnotationType annotation,
			final String attribute) {
		// Class attribute.
		Class<?> classAttributeValue = null;
		// If the annotation is given.
		if (annotation != null) {
			// Tries to get the attribute value.
			try {
				classAttributeValue = (Class<?>) MethodUtils.invokeExactMethod(annotation, attribute);
			}
			// If there is an error with mirrors.
			catch (final MirroredTypeException exception) {
				// Tries to get the error from the name.
				try {
					classAttributeValue = ClassUtils
							.getClass(((TypeElement) ((DeclaredType) exception.getTypeMirror()).asElement()).getQualifiedName().toString());
				}
				catch (final ClassNotFoundException e) {
					throw new IntegrationException(new SimpleMessage("class.notfound"));
				}
			}
			// For any other error.
			catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
				throw new IntegrationException(new SimpleMessage("method.notfound"));
			}
		}
		// Returns the attribute value.
		return classAttributeValue;
	}

	/**
	 * Gets the classes annotation value from an annotation (ignoring type mirror
	 * exceptions).
	 *
	 * @param  <AnnotationType> Annotation type.
	 * @param  annotation       Annotation.
	 * @param  attribute        Attribute.
	 * @return                  The classes annotation value from an annotation
	 *                          (ignoring type mirror exceptions).
	 */
	public static <AnnotationType extends Annotation> Class<?>[] getAnnotationClassesAttribute(
			final AnnotationType annotation,
			final String attribute) {
		// Classes attribute.
		Class<?>[] classesAttributeValue = null;
		// If the annotation is given.
		if (annotation != null) {
			// Tries to get the attribute value.
			try {
				classesAttributeValue = (Class<?>[]) MethodUtils.invokeExactMethod(annotation, attribute);
			}
			// If there is an error with mirrors.
			catch (final MirroredTypesException exception) {
				// Tries to get the error from the name.
				classesAttributeValue = exception.getTypeMirrors().stream().map(type -> {
					try {
						return ClassUtils.getClass(((TypeElement) ((DeclaredType) type).asElement()).getQualifiedName().toString());
					}
					catch (final ClassNotFoundException exception2) {
						throw new IntegrationException(new SimpleMessage("class.notfound"));
					}
				}).collect(Collectors.toList()).toArray(new Class<?>[] {});
			}
			// For any other error.
			catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
				throw new IntegrationException(new SimpleMessage("method.notfound"));
			}
		}
		// Returns the attribute value.
		return classesAttributeValue;

	}

}
