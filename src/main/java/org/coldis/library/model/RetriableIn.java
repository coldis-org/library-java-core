package org.coldis.library.model;

import java.time.Duration;

/**
 * Retriable in interface.
 */
public interface RetriableIn {
	
	/**
	 * Gets the retry in.
	 *
	 * @return The retry in.
	 */
	Duration getRetryIn();
	
	/**
	 * Sets the retry in.
	 *
	 * @param retryIn The retry in.
	 */
	void setRetryIn(Duration retryIn);

}
