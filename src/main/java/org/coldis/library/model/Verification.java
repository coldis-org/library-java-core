package org.coldis.library.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.coldis.library.helper.DateTimeHelper;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Verification information.
 */
@JsonTypeName(value = Verification.TYPE_NAME)
public class Verification implements TypedObject, ExpirableObject, Comparable<Verification> {

	/**
	 * Generated serial.
	 */
	private static final long serialVersionUID = 8706231067242837985L;

	/**
	 * Type name.
	 */
	public static final String TYPE_NAME = "org.coldis.library.model.Verification";

	/**
	 * Verification status.
	 */
	private VerificationStatus status;

	/**
	 * Attributes verified in this verification.
	 */
	private Set<String> attributes;

	/**
	 * Object verification claimer.
	 */
	private String verifiedBy;

	/**
	 * Object verification date.
	 */
	private LocalDateTime verifiedAt = DateTimeHelper.getCurrentLocalDateTime();

	/**
	 * Object verification expiration.
	 */
	private LocalDateTime expiredAt;

	/**
	 * Verification description.
	 */
	private String description;

	/**
	 * No arguments constructor.
	 */
	public Verification() {
	}

	/**
	 * Default constructor.
	 *
	 * @param status      Verification status.
	 * @param attributes  Attributes that have been verified or not.
	 * @param verifiedBy  Object verification claimer.
	 * @param expiredAt   Object verification expiration.
	 * @param description Verification description.
	 */
	public Verification(final VerificationStatus status, final Set<String> attributes, final String verifiedBy,
			final LocalDateTime expiredAt, final String description) {
		super();
		this.status = status;
		this.attributes = attributes;
		this.verifiedBy = verifiedBy;
		this.expiredAt = expiredAt;
		this.description = description;
	}

	/**
	 * Gets the status.
	 * 
	 * @return The status.
	 */
//	@NotNull(message = "verification.verified.required")
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public VerificationStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status New status.
	 */
	public void setStatus(VerificationStatus status) {
		this.status = status;
	}

	/**
	 * Gets the attributes.
	 *
	 * @return The attributes.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public Set<String> getAttributes() {
		// If there is no set for the information.
		if (attributes == null) {
			// Creates a new set for the attributes.
			attributes = new HashSet<>();
		}
		// Returns the verified attributes.
		return attributes;
	}

	/**
	 * Sets the attributes.
	 *
	 * @param attributes New attributes.
	 */
	public void setAttributes(final Set<String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Gets the verified by.
	 *
	 * @return The verified by.
	 */
//	@NotNull(message = "verification.verifiedby.required")
//	@NotEmpty(message = "verification.verifiedby.required")
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getVerifiedBy() {
		return verifiedBy;
	}

	/**
	 * Sets the verified by.
	 *
	 * @param verifiedBy New verified by.
	 */
	public void setVerifiedBy(final String verifiedBy) {
		this.verifiedBy = verifiedBy;
	}

	/**
	 * Gets the verification date.
	 *
	 * @return The verification date.
	 */
//	@NotNull(message = "verification.verifiedat.required")
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public LocalDateTime getVerifiedAt() {
		return verifiedAt;
	}

	/**
	 * Sets the verification date.
	 *
	 * @param verificationDate New verification date.
	 */
	protected void setVerifiedAt(final LocalDateTime verificationDate) {
		this.verifiedAt = verificationDate;
	}

	/**
	 * Gets the expiredAt.
	 *
	 * @return The expiredAt.
	 */
	@Override
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public LocalDateTime getExpiredAt() {
		return expiredAt;
	}

	/**
	 * Sets the expiredAt.
	 *
	 * @param expiredAt New expiredAt.
	 */
	@Override
	public void setExpiredAt(final LocalDateTime expiredAt) {
		this.expiredAt = expiredAt;
	}

	/**
	 * Gets the description.
	 *
	 * @return The description.
	 */
//	@NotNull(message = "verification.description.required")
//	@NotEmpty(message = "verification.description.required")
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description New description.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @see org.coldis.library.model.TypedObject#getTypeName()
	 */
	@Override
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getTypeName() {
		return TYPE_NAME;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Verification verificationInfo) {
		return equals(verificationInfo) ? 0 : -(getVerifiedAt().compareTo(verificationInfo.getVerifiedAt()));
	}

	/**
	 * @see org.coldis.library.model.ExpirableObject#getExpired()
	 */
	@Override
	@JsonView({ ModelView.Public.class })
	public Boolean getExpired() {
		return getExpiredAt() == null ? false : DateTimeHelper.getCurrentLocalDateTime().isAfter(getExpiredAt());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(attributes, description, verifiedBy);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Verification)) {
			return false;
		}
		Verification other = (Verification) obj;
		return Objects.equals(attributes, other.attributes) && Objects.equals(description, other.description)
				&& Objects.equals(verifiedBy, other.verifiedBy);
	}

}
