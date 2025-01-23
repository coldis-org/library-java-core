package org.coldis.library.model.verification.old;

import org.coldis.library.model.verification.VerificationScore;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Verification score.
 */
@JsonTypeName(value = "br.com.supersim.service.party.model.verification.VerificationScore")
public class VerificationScore1 extends VerificationScore {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 58010492439719591L;

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
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
		if (!super.equals(obj) || (this.getClass() != obj.getClass())) {
			return false;
		}
		return true;
	}

}
