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
	 * Personal and personal view.
	 */
	public interface PersonalAndPersonal extends Personal {}

	/**
	 * Personal and sensitive view.
	 */
	public interface PersonalAndSensitive extends Personal, Sensitive {}

	/**
	 * Public and lazy view.
	 */
	public interface PublicAndLazy extends Public, Lazy {}

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

}
