package org.coldis.library.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.ObjectUtils;
import org.coldis.library.helper.DateTimeHelper;
import org.coldis.library.model.view.ModelView;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Retry backoff model.
 *
 * <p>
 * Serialization reads and writes only the stored state ({@code attempts},
 * {@code nextAttemptAt}, {@code nextAttemptScheduledFor}) and two read-only
 * computed values. The business getters, which reset the state once
 * {@link #shouldRetry()} is false, are never called by (de)serialization: a
 * serialization can run while the owner is not wired yet (for instance a
 * persistence snapshot right after loading, or a freshly deserialized backoff
 * whose predicate is still the no-args default), and would otherwise wipe the
 * state of a backoff that is still retrying.
 */
@JsonTypeName(value = RetryBackoff.TYPE_NAME)
public class RetryBackoff<Type> implements Typable {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = -4695307630532402133L;

	/**
	 * Type name.
	 */
	public static final String TYPE_NAME = "org.coldis.library.model.RetryBackoff";

	/** The backoff time. */
	public static final Duration DEFAULT_BACKOFF = Duration.ofMinutes(2);

	/** The backoff multiplier. */
	public static final Double DEFAULT_BACKOFF_MULTIPLIER = 1.5D;

	/** The max backoff time. */
	public static final Duration DEFAULT_MAX_BACKOFF = Duration.ofHours(2);

	/** The max retries. */
	public static final Integer DEFAULT_MAX_RETRIES = 21;

	/** Retry owner. */
	private transient Type retryOwner;

	/** Should retry predicate. */
	private transient Predicate<Type> shouldRetry;

	/** Base date function. */
	private transient Function<Type, LocalDateTime> baseDateFunction;

	/** Backoff minuutes. */
	private Duration backoff;

	/** Backoff multiplier. */
	private Double backoffMultiplier;

	/** Max backoff. */
	private Duration maxBackoff;

	/** Max retries. */
	private Integer maxRetries;

	/** Number of attempts since on backoff. */
	@JsonProperty("attempts")
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	private Integer attempts = 0;

	/** Next attempt. */
	@JsonProperty("nextAttemptAt")
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	private LocalDateTime nextAttemptAt;

	/** When the next attempt were scheduled for. */
	@JsonProperty("nextAttemptScheduledFor")
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	private LocalDateTime nextAttemptScheduledFor;

	/**
	 * Complete constructor.
	 *
	 * @param backoff           Backoff.
	 * @param backoffMultiplier Backoff multiplier.
	 * @param maxBackoff        Max backoff.
	 */
	public RetryBackoff(
			final Type retryOwner,
			final Predicate<Type> shouldRetry,
			final Function<Type, LocalDateTime> baseDateFunction,
			final Duration backoff,
			final Double backoffMultiplier,
			final Duration maxBackoff,
			final Integer maxRetries) {
		this.retryOwner = retryOwner;
		this.shouldRetry = shouldRetry;
		this.baseDateFunction = baseDateFunction;
		this.backoff = ObjectUtils.defaultIfNull(backoff, RetryBackoff.DEFAULT_BACKOFF);
		this.backoffMultiplier = ObjectUtils.defaultIfNull(backoffMultiplier, RetryBackoff.DEFAULT_BACKOFF_MULTIPLIER);
		this.maxBackoff = ObjectUtils.defaultIfNull(maxBackoff, RetryBackoff.DEFAULT_MAX_BACKOFF);
		this.maxRetries = ObjectUtils.defaultIfNull(maxRetries, RetryBackoff.DEFAULT_MAX_RETRIES);
	}

	/**
	 * Simpler constructor.
	 *
	 * @param retryOwner       The retry owner.
	 * @param shouldRetry      The predicate to determine if should retry.
	 * @param baseDateFunction The function to get the base date for the retry
	 *                             backoff.
	 */
	public RetryBackoff(final Type retryOwner, final Predicate<Type> shouldRetry, final Function<Type, LocalDateTime> baseDateFunction) {
		this(retryOwner, shouldRetry, baseDateFunction, RetryBackoff.DEFAULT_BACKOFF, RetryBackoff.DEFAULT_BACKOFF_MULTIPLIER, RetryBackoff.DEFAULT_MAX_BACKOFF,
				RetryBackoff.DEFAULT_MAX_RETRIES);
	}

	/** No-args constructor. */
	public RetryBackoff() {
		this(null, object -> false, object -> DateTimeHelper.getCurrentLocalDateTime(), RetryBackoff.DEFAULT_BACKOFF, RetryBackoff.DEFAULT_BACKOFF_MULTIPLIER,
				RetryBackoff.DEFAULT_MAX_BACKOFF, RetryBackoff.DEFAULT_MAX_RETRIES);
	}

	/**
	 * Gets the retryOwner.
	 *
	 * @return The retryOwner.
	 */
	@JsonIgnore
	public Type getRetryOwner() {
		return this.retryOwner;
	}

	/**
	 * Sets the retryOwner.
	 *
	 * @param retryOwner New retryOwner.
	 */
	public void setRetryOwner(
			final Type retryOwner) {
		this.retryOwner = retryOwner;
	}

	/**
	 * Gets the shouldRetry.
	 *
	 * @return The shouldRetry.
	 */
	@JsonIgnore
	public Predicate<Type> getShouldRetry() {
		return this.shouldRetry;
	}

	/**
	 * Sets the shouldRetry.
	 *
	 * @param shouldRetry New shouldRetry.
	 */
	public void setShouldRetry(
			final Predicate<Type> shouldRetry) {
		this.shouldRetry = shouldRetry;
	}

	/**
	 * Gets the baseDateFunction.
	 *
	 * @return The baseDateFunction.
	 */
	@JsonIgnore
	public Function<Type, LocalDateTime> getBaseDateFunction() {
		return this.baseDateFunction;
	}

	/**
	 * Sets the baseDateFunction.
	 *
	 * @param baseDateFunction New baseDateFunction.
	 */
	public void setBaseDateFunction(
			final Function<Type, LocalDateTime> baseDateFunction) {
		this.baseDateFunction = baseDateFunction;
	}

	/**
	 * Gets the backoff.
	 *
	 * @return The backoff.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public Duration getBackoff() {
		return this.backoff;
	}

	/**
	 * Sets the backoff.
	 *
	 * @param backoff New backoff.
	 */
	public void setBackoff(
			final Duration backoff) {
		this.backoff = backoff;
	}

	/**
	 * Gets the backoffMultiplier.
	 *
	 * @return The backoffMultiplier.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public Double getBackoffMultiplier() {
		return this.backoffMultiplier;
	}

	/**
	 * Sets the backoffMultiplier.
	 *
	 * @param backoffMultiplier New backoffMultiplier.
	 */
	public void setBackoffMultiplier(
			final Double backoffMultiplier) {
		this.backoffMultiplier = backoffMultiplier;
	}

	/**
	 * Gets the maxBackoff.
	 *
	 * @return The maxBackoff.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public Duration getMaxBackoff() {
		return this.maxBackoff;
	}

	/**
	 * Sets the maxBackoff.
	 *
	 * @param maxBackoff New maxBackoff.
	 */
	public void setMaxBackoff(
			final Duration maxBackoff) {
		this.maxBackoff = maxBackoff;
	}

	/**
	 * Gets the maxRetries.
	 *
	 * @return The maxRetries.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public Integer getMaxRetries() {
		return this.maxRetries;
	}

	/**
	 * Sets the maxRetries.
	 *
	 * @param maxRetries New maxRetries.
	 */
	public void setMaxRetries(
			final Integer maxRetries) {
		this.maxRetries = maxRetries;
	}

	/** If should clear attempts. */
	public boolean shouldRetry() {
		return this.shouldRetry.test(this.retryOwner);
	}

	/**
	 * Gets the base date for the retry backoff. This is used to calculate the next
	 * attempt based on the base date.
	 */
	@JsonIgnore
	private LocalDateTime getBaseDate() {
		return (this.baseDateFunction == null ? DateTimeHelper.getCurrentLocalDateTime() : this.baseDateFunction.apply(this.retryOwner));
	}

	/**
	 * Gets the attemptsSince.
	 *
	 * Resets the attempts once retrying is over. Not used by serialization.
	 *
	 * @return The attemptsSince.
	 */
	@JsonIgnore
	public Integer getAttempts() {
		this.attempts = (!this.shouldRetry() || (this.getNextAttemptScheduledFor() == null) ? null : this.attempts);
		this.attempts = ObjectUtils.defaultIfNull(this.attempts, 0);
		return this.attempts;
	}

	/**
	 * Sets the attemptsSince.
	 *
	 * @param attemptsSince New attemptsSince.
	 */
	public void setAttempts(
			final Integer attemptsSince) {
		this.attempts = attemptsSince;
	}

	/**
	 * Gets the next attempt for a number of attempts, from the base date. Does not
	 * change any state.
	 *
	 * @param  currentAttempts Number of attempts.
	 * @return                 The next attempt.
	 */
	private LocalDateTime getNextAttemptAtFor(
			final Integer currentAttempts) {
		Long backoffMillis = Double
				.valueOf((currentAttempts <= 1D ? 0D : Math.pow(this.backoffMultiplier, currentAttempts - 2) * this.backoff.toMillis())).longValue();
		backoffMillis = Math.max(backoffMillis, 0);
		backoffMillis = Math.min(backoffMillis, this.maxBackoff.toMillis());
		return this.getBaseDate().plus(backoffMillis, ChronoUnit.MILLIS);
	}

	/** Calculates the next attempt rules at. */
	private void calculateNextAttemptAt() {
		this.nextAttemptAt = this.getNextAttemptAtFor(this.getAttempts());
	}

	/**
	 * Gets the attempts as {@link #getAttempts()} would, without resetting them.
	 *
	 * @return The attempts.
	 */
	private Integer getCurrentAttempts() {
		return ((!this.shouldRetry() || (this.getCurrentNextAttemptScheduledFor() == null)) ? 0 : ObjectUtils.defaultIfNull(this.attempts, 0));
	}

	/**
	 * Gets the next attempt as {@link #getNextAttemptAt()} would, without storing
	 * or resetting it.
	 *
	 * @return The next attempt.
	 */
	private LocalDateTime getCurrentNextAttemptAt() {
		LocalDateTime currentNextAttemptAt = null;
		if (this.shouldRetry() && (this.getCurrentAttempts() <= this.maxRetries)) {
			currentNextAttemptAt = (this.nextAttemptAt == null ? this.getNextAttemptAtFor(this.getCurrentAttempts()) : this.nextAttemptAt);
		}
		return currentNextAttemptAt;
	}

	/**
	 * Gets the scheduled attempt as {@link #getNextAttemptScheduledFor()} would,
	 * without resetting it.
	 *
	 * @return The scheduled attempt.
	 */
	private LocalDateTime getCurrentNextAttemptScheduledFor() {
		return (this.shouldRetry() ? this.nextAttemptScheduledFor : null);
	}

	/**
	 * Gets the nextAttempt.
	 *
	 * Resets the next attempt once retrying is over. Not used by serialization.
	 *
	 * @return The nextAttempt.
	 */
	@JsonIgnore
	public LocalDateTime getNextAttemptAt() {
		if ((!this.shouldRetry() || (this.getAttempts() > this.maxRetries))) {
			this.nextAttemptAt = null;
		}
		else if (this.nextAttemptAt == null) {
			this.calculateNextAttemptAt();
		}
		return this.nextAttemptAt;
	}

	/**
	 * Gets the next attempt delay.
	 *
	 * @return The next attempt delay.
	 */
	@JsonIgnore
	public Duration getNextAttemptDelay() {
		return RetryBackoff.getDelayUntil(this.getNextAttemptAt());
	}

	/**
	 * Gets the delay until an attempt, never negative.
	 *
	 * @param  attemptAt The attempt.
	 * @return           The delay, or null without an attempt.
	 */
	private static Duration getDelayUntil(
			final LocalDateTime attemptAt) {
		Duration delay = null;
		if (attemptAt != null) {
			delay = Duration.ofSeconds(DateTimeHelper.getCurrentLocalDateTime().until(attemptAt, ChronoUnit.SECONDS));
			delay = delay.isNegative() ? Duration.ofSeconds(0) : delay;
		}
		return delay;
	}

	/**
	 * Gets the next attempt delay for serialization, without changing any state.
	 *
	 * @return The next attempt delay.
	 */
	@JsonProperty("nextAttemptDelay")
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	private Duration getSerializedNextAttemptDelay() {
		return RetryBackoff.getDelayUntil(this.getCurrentNextAttemptAt());
	}

	/**
	 * Sets the nextAttempt.
	 *
	 * @param nextAttemptAt New nextAttempt.
	 */
	public void setNextAttemptAt(
			final LocalDateTime nextAttemptAt) {
		this.nextAttemptAt = nextAttemptAt;
	}

	/**
	 * Gets the nextAttemptScheduledFor.
	 *
	 * Resets the scheduled attempt once retrying is over. Not used by
	 * serialization.
	 *
	 * @return The nextAttemptScheduledFor.
	 */
	@JsonIgnore
	public LocalDateTime getNextAttemptScheduledFor() {
		if (!this.shouldRetry()) {
			this.nextAttemptScheduledFor = null;
		}
		return this.nextAttemptScheduledFor;
	}

	/**
	 * Sets the nextAttemptScheduledFor.
	 *
	 * @param nextAttemptScheduledFor New nextAttemptScheduledFor.
	 */
	public void setNextAttemptScheduledFor(
			final LocalDateTime nextAttemptScheduledFor) {
		this.nextAttemptScheduledFor = nextAttemptScheduledFor;
	}

	/**
	 * Checks if the next attempt rules should be scheduled.
	 *
	 * Once retrying is over, resets the state ({@link #reset()}), so the next retry
	 * cycle starts from the first attempt and schedules again. Callers check it on
	 * the owner's updates, with the owner wired. Not used by serialization.
	 *
	 * @return If the next attempt rules should be scheduled.
	 */
	@JsonIgnore
	public Boolean getShouldScheduleNextAttempt() {
		final boolean isRetrying = this.shouldRetry();
		if (!isRetrying) {
			this.reset();
		}
		return (isRetrying && (this.getNextAttemptAt() != null)
				&& ((this.getNextAttemptScheduledFor() == null) || this.getNextAttemptAt().isAfter(this.getNextAttemptScheduledFor())));
	}

	/** Resets the retry state: no attempts, nothing scheduled. */
	public void reset() {
		this.attempts = 0;
		this.nextAttemptAt = null;
		this.nextAttemptScheduledFor = null;
	}

	/**
	 * Gets if the next attempt should be scheduled, for serialization, without
	 * changing any state.
	 *
	 * @return If the next attempt should be scheduled.
	 */
	@JsonProperty("shouldScheduleNextAttempt")
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	private Boolean getSerializedShouldScheduleNextAttempt() {
		final LocalDateTime currentNextAttemptAt = this.getCurrentNextAttemptAt();
		final LocalDateTime currentScheduledFor = this.getCurrentNextAttemptScheduledFor();
		return (this.shouldRetry() && (currentNextAttemptAt != null)
				&& ((currentScheduledFor == null) || currentNextAttemptAt.isAfter(currentScheduledFor)));
	}

	/** Schedule attempts. */
	public void setScheduled() {
		this.setNextAttemptScheduledFor(this.getNextAttemptAt());
	}

	/** Increments the attempts. */
	public void incrementRetries() {
		if (this.shouldRetry()) {
			if (this.getNextAttemptScheduledFor() == null) {
				this.setScheduled();
			}
			this.setAttempts(this.getAttempts() + 1);
			this.calculateNextAttemptAt();
		}
	}

	/**
	 * @see org.coldis.library.model.Typable#getTypeName()
	 */
	@Override
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getTypeName() {
		return RetryBackoff.TYPE_NAME;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.attempts, this.backoff, this.backoffMultiplier, this.maxBackoff, this.maxRetries, this.nextAttemptAt,
				this.nextAttemptScheduledFor);
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
		if ((obj == null) || (this.getClass() != obj.getClass())) {
			return false;
		}
		final RetryBackoff other = (RetryBackoff) obj;
		return Objects.equals(this.attempts, other.attempts) && Objects.equals(this.backoff, other.backoff)
				&& Objects.equals(this.backoffMultiplier, other.backoffMultiplier) && Objects.equals(this.maxBackoff, other.maxBackoff)
				&& Objects.equals(this.maxRetries, other.maxRetries) && Objects.equals(this.nextAttemptAt, other.nextAttemptAt)
				&& Objects.equals(this.nextAttemptScheduledFor, other.nextAttemptScheduledFor);
	}

}
