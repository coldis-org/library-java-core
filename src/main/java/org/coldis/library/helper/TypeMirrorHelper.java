package org.coldis.library.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.coldis.library.exception.IntegrationException;
import org.coldis.library.model.SimpleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reflection helper.
 */
public class TypeMirrorHelper {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TypeMirrorHelper.class);

	/**
	 * Gets the class name from an annotation attribute (ignoring type mirror
	 * exceptions).
	 *
	 * @param  <AnnotationType> Annotation type.
	 * @param  annotation       Annotation.
	 * @param  attribute        Attribute.
	 * @return                  The class name from an annotation attribute
	 *                          (ignoring type mirror exceptions).
	 */
	public static <AnnotationType extends Annotation> String getAnnotationClassAttribute(
			final AnnotationType annotation,
			final String attribute) {
		// Class attribute.
		String className = null;
		// If the annotation is given.
		if (annotation != null) {
			// Tries to get the attribute value.
			try {
				className = ((Class<?>) MethodUtils.invokeExactMethod(annotation, attribute)).getName();
			}
			// If there is an error with mirrors.
			catch (final InvocationTargetException exception) {
				if (exception.getTargetException() instanceof MirroredTypeException) {
					// Tries to get the error from the name.
					className = (((MirroredTypeException) exception.getTargetException()).getTypeMirror() instanceof DeclaredType
							? ((TypeElement) ((DeclaredType) ((MirroredTypeException) exception.getTargetException()).getTypeMirror()).asElement())
									.getQualifiedName().toString()
							: "void");
				}
				// For any other exception.
				else {
					// Re-throws it.
					throw new IntegrationException(new SimpleMessage("method.notfound"), exception);
				}
			}
			// For any other error.
			catch (NoSuchMethodException | IllegalAccessException exception) {
				throw new IntegrationException(new SimpleMessage("method.notfound"), exception);
			}
		}
		// Returns the attribute value.
		return className;
	}

	/**
	 * Gets the classes names from an annotation attribute (ignoring type mirror
	 * exceptions).
	 *
	 * @param  <AnnotationType> Annotation type.
	 * @param  annotation       Annotation.
	 * @param  attribute        Attribute.
	 * @return                  The classes names from an annotation attribute
	 *                          (ignoring type mirror exceptions).
	 */
	public static <AnnotationType extends Annotation> List<String> getAnnotationClassesAttribute(
			final AnnotationType annotation,
			final String attribute) {
		// Classes attribute.
		List<String> classesNames = null;
		// If the annotation is given.
		if (annotation != null) {
			// Tries to get the attribute value.
			try {
				classesNames = Arrays.stream((Class<?>[]) MethodUtils.invokeExactMethod(annotation, attribute)).map(item -> item.getName().toString())
						.collect(Collectors.toList());
			}
			// If there is an error with mirrors.
			catch (final InvocationTargetException exception) {
				if (exception.getTargetException() instanceof MirroredTypesException) {
					// Tries to get the error from the name.
					classesNames = ((MirroredTypesException) exception.getTargetException()).getTypeMirrors().stream().map(type -> {
						return (type instanceof DeclaredType ? ((TypeElement) ((DeclaredType) type).asElement()).getQualifiedName().toString() : "void");
					}).collect(Collectors.toList());
				}
				// For any other exception.
				else {
					// Re-throws it.
					throw new IntegrationException(new SimpleMessage("method.notfound"), exception);
				}
			}
			// For any other error.
			catch (NoSuchMethodException | IllegalAccessException exception) {
				throw new IntegrationException(new SimpleMessage("method.notfound"), exception);
			}
		}
		// Returns the attribute value.
		return classesNames;

	}

}
