package org.coldis.library.model.view;

/**
 * Model views.
 */
public class ModelView {

	/* Technical */

	/**
	 * Persistent view.
	 */
	public interface Persistent {}

	/**
	 * Internal view.
	 */
	public interface Internal {}

	/**
	 * Lazy view.
	 */
	public interface Lazy {}

	/**
	 * Public view.
	 */
	public interface Public {}

	/**
	 * Personal view.
	 */
	public interface Personal {}

	/**
	 * Sensitive view.
	 */
	public interface Sensitive extends Personal {}

	/* Combined */

	/**
	 * Persistent and personal view.
	 */
	public interface PersistentAndPersonal extends Persistent {}

	/**
	 * Persistent and sensitive view.
	 */
	public interface PersistentAndSensitive extends Persistent, Sensitive {}

	/**
	 * Public and internal view.
	 */
	public interface PublicAndLazy extends Public, Lazy {}

	/**
	 * Public and personal.
	 */
	public interface PublicAndPersonal extends Public, Personal {}

	/**
	 * Public and lazy and personal.
	 */
	public interface PublicAndLazyAndPersonal extends Public, Lazy, Personal {}

	/**
	 * Public and personal.
	 */
	public interface PublicAndSensitive extends Public, Sensitive {}

	/**
	 * Public and lazy and personal.
	 */
	public interface PublicAndLazyAndSensitive extends Public, Lazy, Sensitive {}

}
