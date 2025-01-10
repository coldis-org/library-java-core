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

	/* Content */

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

	/**
	 * Masked personal view.
	 */
	public interface MaskedPersonal extends Personal {}

	/**
	 * Masked sensitive view.
	 */
	public interface MaskedSensitive extends Sensitive, MaskedPersonal {}

	/* Combined */

	/**
	 * Public and lazy view.
	 */
	public interface PublicAndLazy extends Public, Lazy {}

	/**
	 * Public and lazy view.
	 */
	public interface PublicAndInternal extends Public, Internal {}

	/**
	 * Public and personal.
	 */
	public interface PublicAndPersonal extends Public, Personal {}

	/**
	 * Public and personal.
	 */
	public interface PublicAndSensitive extends Public, Sensitive {}

	/**
	 * Public and masked personal.
	 */
	public interface PublicAndMaskedPersonal extends Public, MaskedPersonal {}

	/**
	 * Public and masked sensitive.
	 */
	public interface PublicAndMaskedSensitive extends Public, MaskedSensitive {}

	/**
	 * Public and internal and personal.
	 */
	public interface PublicAndInternalAndPersonal extends Public, Internal, Personal {}

	/**
	 * Public and internal and sensitive.
	 */
	public interface PublicAndInternalAndSensitive extends Public, Internal, Sensitive {}

}
