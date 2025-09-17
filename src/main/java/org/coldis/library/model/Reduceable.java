package org.coldis.library.model;

/**
 * Reduceable object.
 */
public interface Reduceable<Type> {
	
	/**
	 * Reduces an instance of an object.
	 * @param toBeReduced Object to be reduced in this one.
	 */
	void reduce(Type toBeReduced);
		
}
