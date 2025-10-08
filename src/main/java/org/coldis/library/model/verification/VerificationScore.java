package org.coldis.library.model.verification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import org.coldis.library.model.Typable;
import org.coldis.library.model.view.ModelView;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Verification score.
 */
@JsonTypeName(value = VerificationScore.TYPE_NAME)
public class VerificationScore extends VerificationItem implements Typable {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 7210006553355294336L;

	/**
	 * Type name.
	 */
	protected static final String TYPE_NAME = "org.coldis.library.model.verification.VerificationScore";

	/**
	 * Used algorithm.
	 */
	private String algorithm;

	/**
	 * Score.
	 */
	private BigDecimal score;

	/**
	 * No arguments constructor.
	 */
	public VerificationScore() {
		super();
	}

	/**
	 * Default constructor.
	 *
	 * @param status      Status.
	 * @param attributes  Attributes.
	 * @param verifiedBy  VerifiedBy.
	 * @param expiredAt   Expiration.
	 * @param description Description.
	 * @param algorithm   Algorithm.
	 * @param score       Score.
	 */
	public VerificationScore(
			final VerificationStatus status,
			final Set<String> attributes,
			final String verifiedBy,
			final LocalDateTime expiredAt,
			final String description,
			final String algorithm,
			final BigDecimal score) {
		super(status, attributes, verifiedBy, expiredAt, description);
		this.algorithm = algorithm;
		this.score = score;
	}

	/**
	 * Gets the algorithm.
	 *
	 * @return The algorithm.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getAlgorithm() {
		return this.algorithm;
	}

	/**
	 * Sets the algorithm.
	 *
	 * @param algorithm New algorithm.
	 */
	public void setAlgorithm(
			final String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Gets the score.
	 *
	 * @return The score.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public BigDecimal getScore() {
		return this.score;
	}

	/**
	 * Sets the score.
	 *
	 * @param score New score.
	 */
	public void setScore(
			final BigDecimal score) {
		this.score = score;
	}

	/**
	 * @see org.coldis.library.model.Typable#getTypeName()
	 */
	@Override
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getTypeName() {
		return VerificationScore.TYPE_NAME;
	}

	/**
	 * Checks if the given objects are similar.
	 *
	 * @param  this   Object 1.
	 * @param  object Object 2.
	 * @return        If the given object are similar.
	 */
	@Override
	public Boolean isSimilar(
			final VerificationItem object) {
		return (object != null) && Objects.equals(this.getClass(), object.getClass()) && object instanceof final VerificationScore verificationScore
				&& super.isSimilar(verificationScore) && Objects.equals(this.getAlgorithm(), verificationScore.getAlgorithm());
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
		if (!super.equals(obj) || !(obj instanceof VerificationScore)) {
			return false;
		}
		final VerificationScore other = (VerificationScore) obj;
		return Objects.equals(this.algorithm, other.algorithm) && Objects.equals(this.score, other.score);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + Objects.hash(this.algorithm, this.score);
		return result;
	}
}
