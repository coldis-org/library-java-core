package org.coldis.library.model;

import java.time.LocalDateTime;
import java.util.Objects;

import org.coldis.library.helper.DateTimeHelper;

/**
 * Distribution group.
 */
public abstract class AbstractDistributionGroup implements DistributionGroup {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = -5340111054555130647L;

	/**
	 * If is the primary group.
	 */
	private Boolean primary;

	/**
	 * Distribution size.
	 */
	private Integer distributionSize;

	/**
	 * Absolute limit.
	 */
	private Long absoluteLimit;

	/**
	 * Current size.
	 */
	private Long currentSize;

	/**
	 * When group expires.
	 */
	private LocalDateTime expiredAt;

	/**
	 * Gets the primary.
	 *
	 * @return The primary.
	 */
	@Override
	public Boolean getPrimary() {
		// Make sure the object is initialized.
		this.primary = (primary == null ? false : this.primary);
		// Returns the object.
		return this.primary;
	}

	/**
	 * Sets the primary.
	 *
	 * @param primary New primary.
	 */
	@Override
	public void setPrimary(final Boolean primary) {
		this.primary = primary;
	}

	/**
	 * Gets the distributionSize.
	 *
	 * @return The distributionSize.
	 */
	@Override
	public Integer getDistributionSize() {
		// Make sure the object is initialized.
		this.distributionSize = (distributionSize == null ? 0 : this.distributionSize);
		// Returns the object.
		return this.distributionSize;
	}

	/**
	 * Sets the distributionSize.
	 *
	 * @param distributionSize New distributionSize.
	 */
	@Override
	public void setDistributionSize(final Integer distributionSize) {
		this.distributionSize = distributionSize;
	}

	/**
	 * Gets the absoluteLimit.
	 *
	 * @return The absoluteLimit.
	 */
	@Override
	public Long getAbsoluteLimit() {
		// Make sure the object is initialized.
		this.absoluteLimit = (absoluteLimit == null ? 0L : this.absoluteLimit);
		// Returns the object.
		return this.absoluteLimit;
	}

	/**
	 * Sets the absoluteLimit.
	 *
	 * @param absoluteLimit New absoluteLimit.
	 */
	@Override
	public void setAbsoluteLimit(final Long absoluteLimit) {
		this.absoluteLimit = absoluteLimit;
	}

	/**
	 * Gets the currentSize.
	 *
	 * @return The currentSize.
	 */
	@Override
	public Long getCurrentSize() {
		// Make sure the object is initialized.
		this.currentSize = (currentSize == null ? 0L : this.currentSize);
		// Returns the object.
		return this.currentSize;
	}

	/**
	 * Sets the currentSize.
	 *
	 * @param currentSize New currentSize.
	 */
	@Override
	public void setCurrentSize(final Long currentSize) {
		this.currentSize = currentSize;
	}

	/**
	 * Gets the expiredAt.
	 *
	 * @return The expiredAt.
	 */
	@Override
	public LocalDateTime getExpiredAt() {
		return this.expiredAt;
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
	 * @see org.coldis.library.model.Expirable#getExpired()
	 */
	@Override
	public Boolean getExpired() {
		return
		// If the limit has been reached.
		(this.getCurrentSize().compareTo(this.getAbsoluteLimit()) >= 0)
				// Or if the expiration date has been reached.
				|| ((this.getExpiredAt() != null) && this.getExpiredAt().isBefore(DateTimeHelper.getCurrentLocalDateTime()));
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(absoluteLimit, currentSize, distributionSize, expiredAt, primary);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractDistributionGroup)) {
			return false;
		}
		AbstractDistributionGroup other = (AbstractDistributionGroup) obj;
		return Objects.equals(absoluteLimit, other.absoluteLimit) && Objects.equals(currentSize, other.currentSize)
				&& Objects.equals(distributionSize, other.distributionSize) && Objects.equals(expiredAt, other.expiredAt)
				&& Objects.equals(primary, other.primary);
	}

}
