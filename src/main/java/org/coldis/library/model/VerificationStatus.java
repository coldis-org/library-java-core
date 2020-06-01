package org.coldis.library.model;

/**
 * VerificationItem status.
 */
public enum VerificationStatus implements Identifiable {

	/**
	 * Valid information.
	 */
	VALID(1L),

	/**
	 * Not verified information.
	 */
	NOT_VERIFIED(2L),

	/**
	 * Invalid information.
	 */
	INVALID(3L),

	/**
	 * Dubious information (both verified and not).
	 */
	DUBIOUS(4L),

	/**
	 * VerificationItem override. When original verification is not considered anymore.
	 */
	OVERRIDE(5L);

	/**
	 * Constructor.
	 * 
	 * @param identifier The new identifier.
	 */
	private VerificationStatus(Long identifier) {
		this.identifier = identifier;
	}

	/**
	 * Identifier.
	 */
	private Long identifier;

	/**
	 * Gets the identifier.
	 * 
	 * @return The identifier.
	 */
	public Long getId() {
		return identifier;
	}

}
