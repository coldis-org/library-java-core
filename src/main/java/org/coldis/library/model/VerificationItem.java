package org.coldis.library.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.coldis.library.helper.DateTimeHelper;
import org.coldis.library.model.view.ModelView;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * VerificationItem information.
 */
@JsonTypeName(value = VerificationItem.TYPE_NAME)
public class VerificationItem extends AbstractExpirable
implements Typable, Expirable, Comparable<VerificationItem> {

	/**
	 * Generated serial.
	 */
	private static final long serialVersionUID = 8706231067242837985L;

	/**
	 * Type name.
	 */
	public static final String TYPE_NAME = "org.coldis.library.model.VerificationItem";

	/**
	 * VerificationItem status.
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
	private LocalDateTime verifiedAt;

	/**
	 * VerificationItem description.
	 */
	private SimpleMessage description;

	/**
	 * No arguments constructor.
	 */
	public VerificationItem() {
	}

	/**
	 * Default constructor.
	 *
	 * @param status      VerificationItem status.
	 * @param attributes  Attributes that have been verified or not.
	 * @param verifiedBy  Object verification claimer.
	 * @param expiredAt   Object verification expiration.
	 * @param description VerificationItem description.
	 */
	public VerificationItem(final VerificationStatus status, final Set<String> attributes, final String verifiedBy,
			final LocalDateTime expiredAt, final SimpleMessage description) {
		super();
		this.status = status;
		this.attributes = attributes;
		this.verifiedBy = verifiedBy;
		this.setExpiredAt(expiredAt);
		this.description = description;
	}

	/**
	 * Gets the status.
	 *
	 * @return The status.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public VerificationStatus getStatus() {
		return this.status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status New status.
	 */
	public void setStatus(final VerificationStatus status) {
		this.status = status;
	}

	/**
	 * Gets the attributes.
	 *
	 * @return The attributes.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public Set<String> getAttributes() {
		// Makes sure the set is initialized.
		this.attributes = this.attributes == null ? new HashSet<>() : this.attributes;
		// Returns the verified attributes.
		return this.attributes;
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
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getVerifiedBy() {
		return this.verifiedBy;
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
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public LocalDateTime getVerifiedAt() {
		// Makes sure the date is initialized.
		this.verifiedAt = this.verifiedAt == null ? DateTimeHelper.getCurrentLocalDateTime() : this.verifiedAt;
		// Returns the date.
		return this.verifiedAt;
	}

	/**
	 * Sets the verification date.
	 *
	 * @param verificationDate New verification date.
	 */
	public void setVerifiedAt(final LocalDateTime verificationDate) {
		this.verifiedAt = verificationDate;
	}

	/**
	 * Gets the description.
	 *
	 * @return The description.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public SimpleMessage getDescription() {
		// Makes sure the object is initialized.
		this.description = this.description == null ? new SimpleMessage() : this.description;
		// Returns the object.
		return this.description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description New description.
	 */
	public void setDescription(final SimpleMessage description) {
		this.description = description;
	}

	/**
	 * @see org.coldis.library.model.AbstractExpirable#getExpiredByDefault()
	 */
	@Override
	protected Boolean getExpiredByDefault() {
		return false;
	}

	/**
	 * @see org.coldis.library.model.Typable#getTypeName()
	 */
	@Override
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getTypeName() {
		return VerificationItem.TYPE_NAME;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final VerificationItem verificationInfo) {
		return this.equals(verificationInfo) ? 0 : -(this.getVerifiedAt().compareTo(verificationInfo.getVerifiedAt()));
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.attributes, this.description, this.verifiedBy);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VerificationItem)) {
			return false;
		}
		final VerificationItem other = (VerificationItem) obj;
		return Objects.equals(this.attributes, other.attributes) && Objects.equals(this.description, other.description)
				&& Objects.equals(this.verifiedBy, other.verifiedBy);
	}

}
