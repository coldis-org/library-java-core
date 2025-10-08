package org.coldis.library.model.verification;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.coldis.library.helper.ReflectionHelper;

/**
 * Object that can be verified.
 */
public interface Verifiable {

	/**
	 * Returns the verification.
	 *
	 * @return The verification.
	 */
	Verification getVerification();

	/**
	 * Returns the verification status for an attribute.
	 *
	 * @param  verifiable    The verifiable object.
	 * @param  attributeName Attribute name.
	 * @return               The verification status for an attribute.
	 */
	static VerificationStatus getVerificationStatus(
			final Verification verification,
			final String attributeName) {
		// Attribute status is not verified by default.
		VerificationStatus status = VerificationStatus.NOT_VERIFIED;
		// If the object and attribute are given.
		if ((verification != null) && (verification.getItems() != null) && (attributeName != null)) {
			// For each verification on the object.
			for (final VerificationItem verificationItem : verification.getItems()) {
				// If the verification is for the attribute.
				if (verificationItem.getAttributes().contains(attributeName)) {
					// If the verification is not expired.
					if (!verificationItem.getExpired()) {
						// Depending on the current attribute status.
						switch (status) {
							// If the current attribute status is not verified or override.
							case NOT_VERIFIED:
							case OVERRIDE:
								// If the current verification status is not override.
								if (!VerificationStatus.OVERRIDE.equals(verificationItem.getStatus())) {
									// Replaces the current attribute status.
									status = verificationItem.getStatus();
								}
								break;
							// If the current attribute status is valid.
							case VALID:
								// If the current verification status is not verified or override.
								if (!Set.of(VerificationStatus.NOT_VERIFIED, VerificationStatus.OVERRIDE).contains(verificationItem.getStatus())) {
									// If the current verification status is invalid.
									if (VerificationStatus.INVALID.equals(verificationItem.getStatus())) {
										// The status is set to dubious.
										status = VerificationStatus.DUBIOUS;
									}
									// For every other verification status.
									else {
										// Replaces the current attribute status.
										status = verificationItem.getStatus();
									}
								}
								break;
							// If the current attribute status is dubious.
							case DUBIOUS:
								// If the current verification status is invalid.
								if (VerificationStatus.INVALID.equals(verificationItem.getStatus())) {
									// Replaces the current attribute status.
									status = verificationItem.getStatus();
								}
								break;
							// If the current attribute status is invalid.
							case INVALID:
								// If the current verification status is valid.
								if (VerificationStatus.VALID.equals(verificationItem.getStatus())) {
									// The status is set to dubious.
									status = VerificationStatus.DUBIOUS;
								}
								break;
						}
					}
				}
			}
		}
		// Returns the verification status.
		return status;
	}

	/**
	 * Gets the attributes that should be verified for the object to be verified.
	 *
	 * @param  verifiable The verifiable object.
	 * @return            The attributes that should be verified for the object to
	 *                    be verified.
	 */
	static Set<String> getVerifiableAttributes(
			final Verifiable verifiable) {
		// Set of verifiable attributes.
		final Set<String> verifiableAttributes = new HashSet<>();
		// If the verifiable object is given.
		if (verifiable != null) {
			// For each method.
			for (final Method currentMethod : verifiable.getClass().getMethods()) {
				// If it is a getter.
				if (ReflectionHelper.isGetter(currentMethod)) {
					// Gets the attribute name.
					final String attributeName = ReflectionHelper.getAttributeName(currentMethod.getName());
					// Gets the verifiable attribute annotation.
					final VerifiableAttribute verifiableAttributeMetadata = currentMethod.getAnnotation(VerifiableAttribute.class);
					// If the attribute should be verified.
					if (verifiableAttributeMetadata != null) {
						// Adds the attribute to the verifiable set.
						verifiableAttributes.add(attributeName);
					}
				}
			}
		}
		// Returns the verifiable attributes.
		return verifiableAttributes;
	}

	/**
	 * Returns the verification status for an attribute.
	 *
	 * @param  verifiable    The verifiable object.
	 * @param  attributeName Attribute name.
	 * @return               The verification status for an attribute.
	 */
	static VerificationStatus getVerificationStatus(
			final Verifiable verifiable,
			final String attributeName) {
		return Verifiable.getVerificationStatus(verifiable != null ? verifiable.getVerification() : null, attributeName);
	}

	/**
	 * Returns the verification status for an attribute.
	 *
	 * @param  verifiable    The verifiable object.
	 * @param  attributeName Attribute name.
	 * @return               The verification status for an attribute.
	 */
	default VerificationStatus getVerificationStatus(
			final String attributeName) {
		return Verifiable.getVerificationStatus(this, attributeName);
	}

	/**
	 * Returns the verification status for an object.
	 *
	 * @param  verifiable The verifiable object.
	 * @return            The verification status for an object.
	 */
	static VerificationStatus getVerificationStatus(
			final Verifiable verifiable) {
		// Object status is valid by default.
		VerificationStatus status = VerificationStatus.VALID;
		// If there are verifiable attributes.
		if (Verifiable.getVerifiableAttributes(verifiable) != null) {
			// For each verifiable.
			for (final String attributeName : Verifiable.getVerifiableAttributes(verifiable)) {
				// Gets the verification for the attribute.
				final VerificationStatus attributeStatus = Verifiable.getVerificationStatus(verifiable, attributeName);
				// Depending on the current object status.
				switch (status) {
					// If the current object status is valid.
					case VALID:
						// Replaces the current object status.
						status = attributeStatus;
						break;
					// If the current object status is dubious.
					case DUBIOUS:
						// If the current attribute status is not valid.
						if (!VerificationStatus.VALID.equals(attributeStatus)) {
							// Replaces the current object status.
							status = attributeStatus;
						}
						break;
					// If the current object status is not verified or override.
					case NOT_VERIFIED:
					case OVERRIDE:
						// If the current attribute status is invalid.
						if (VerificationStatus.INVALID.equals(attributeStatus)) {
							// Replaces the current object status.
							status = attributeStatus;
						}
						break;
					// If the current object status is invalid.
					case INVALID:
						// Nothing to do.
						break;
				}
			}
		}
		// Returns the object status.
		return status;
	}

	/**
	 * Returns the verification status for an attribute.
	 *
	 * @param  verifiable    The verifiable object.
	 * @param  attributeName Attribute name.
	 * @return               The verification status for an attribute.
	 */
	default VerificationStatus getVerificationStatus() {
		return Verifiable.getVerificationStatus(this);
	}

}
