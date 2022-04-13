package org.coldis.library.model.science;

import java.time.LocalDateTime;
import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;
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
		this.primary = (this.primary == null ? false : this.primary);
		// Returns the object.
		return this.primary;
	}

	/**
	 * Sets the primary.
	 *
	 * @param primary New primary.
	 */
	@Override
	public void setPrimary(
			final Boolean primary) {
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
		this.distributionSize = (this.distributionSize == null ? 0 : this.distributionSize);
		// Returns the object.
		return this.distributionSize;
	}

	/**
	 * Sets the distributionSize.
	 *
	 * @param distributionSize New distributionSize.
	 */
	@Override
	public void setDistributionSize(
			final Integer distributionSize) {
		this.distributionSize = distributionSize;
	}

	/**
	 * Gets the absoluteLimit.
	 *
	 * @return The absoluteLimit.
	 */
	@Override
	public Long getAbsoluteLimit() {
		return this.absoluteLimit;
	}

	/**
	 * Sets the absoluteLimit.
	 *
	 * @param absoluteLimit New absoluteLimit.
	 */
	@Override
	public void setAbsoluteLimit(
			final Long absoluteLimit) {
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
		this.currentSize = (this.currentSize == null ? 0L : this.currentSize);
		// Returns the object.
		return this.currentSize;
	}

	/**
	 * Sets the currentSize.
	 *
	 * @param currentSize New currentSize.
	 */
	@Override
	public void setCurrentSize(
			final Long currentSize) {
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
	public void setExpiredAt(
			final LocalDateTime expiredAt) {
		this.expiredAt = expiredAt;
	}

	/**
	 * Returns if the group is expired.
	 *
	 * @param  expiredAt     Expired at.
	 * @param  absoluteLimit Absolute limit.
	 * @param  currentSize   Current group size.
	 * @return               If the group is expired.
	 */
	public static Boolean getExpired(
			final LocalDateTime expiredAt,
			final Long absoluteLimit,
			final Long currentSize) {
		return
		// If the limit has been reached.
		((absoluteLimit != null) && (currentSize != null) && (currentSize.compareTo(absoluteLimit) >= 0))
				// Or if the expiration date has been reached.
				|| ((expiredAt != null) && expiredAt.isBefore(DateTimeHelper.getCurrentLocalDateTime()));
	}

	/**
	 * @see org.coldis.library.model.Expirable#getExpired()
	 */
	@Override
	public Boolean getExpired() {
		return AbstractDistributionGroup.getExpired(this.getExpiredAt(), this.getAbsoluteLimit(), this.getCurrentSize());
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(
			final DistributionGroup group) {
		return NumberUtils.compare(this.hashCode(), group.hashCode());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.absoluteLimit, this.currentSize, this.distributionSize, this.expiredAt, this.primary);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(
			final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractDistributionGroup)) {
			return false;
		}
		final AbstractDistributionGroup other = (AbstractDistributionGroup) obj;
		return Objects.equals(this.absoluteLimit, other.absoluteLimit) && Objects.equals(this.currentSize, other.currentSize)
				&& Objects.equals(this.distributionSize, other.distributionSize) && Objects.equals(this.expiredAt, other.expiredAt)
				&& Objects.equals(this.primary, other.primary);
	}

}
