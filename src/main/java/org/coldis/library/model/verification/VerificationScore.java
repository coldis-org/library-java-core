package org.coldis.library.model.verification;

import java.math.BigDecimal;
import java.util.Objects;

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
	protected static final String TYPE_NAME = "br.com.supersim.service.party.model.verification.VerificationScore";

	/**
	 * Score.
	 */
	private BigDecimal score;

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
		return Objects.equals(this.score, other.score);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + Objects.hash(this.score);
		return result;
	}
}
