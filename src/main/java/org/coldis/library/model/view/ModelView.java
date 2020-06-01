package org.coldis.library.model.view;

/**
 * Model views.
 */
public class ModelView {

	/**
	 * Persistent view.
	 */
	public interface Persistent {
	}

	/**
	 * Public view.
	 */
	public interface Public {
	}

	/**
	 * Lazy view.
	 */
	public interface Lazy {
	}

	/**
	 * Internal view.
	 */
	public interface Internal {
	}

	/**
	 * Public and lazy view.
	 */
	public interface PublicAndLazy extends Public, Lazy {
	}

	/**
	 * Sensitive view.
	 */
	public interface Sensitive {
	}
}
