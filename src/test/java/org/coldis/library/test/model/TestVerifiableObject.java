package org.coldis.library.test.model;

import java.util.SortedSet;
import java.util.TreeSet;

import org.coldis.library.model.VerifiableAttribute;
import org.coldis.library.model.VerifiableObject;
import org.coldis.library.model.Verification;
import org.coldis.library.model.VerificationStatus;

/**
 * Test object.
 */
public class TestVerifiableObject implements VerifiableObject {

	/**
	 * Test attribute.
	 */
	private String attribute1;

	/**
	 * Test attribute.
	 */
	private Integer attribute2;

	/**
	 * Test attribute.
	 */
	private Long attribute3;

	/**
	 * Test attribute.
	 */
	private String attribute4;

	/**
	 * Verification information.
	 */
	private SortedSet<Verification> verification;

	/**
	 * Default constructor.
	 *
	 * @param verification Verification information.
	 */
	public TestVerifiableObject(final SortedSet<Verification> verification) {
		super();
		this.verification = verification;
	}

	/**
	 * Gets the attribute1.
	 *
	 * @return The attribute1.
	 */
	@VerifiableAttribute
	public String getAttribute1() {
		return this.attribute1;
	}

	/**
	 * Sets the attribute1.
	 *
	 * @param attribute1 New attribute1.
	 */
	public void setAttribute1(final String attribute1) {
		this.attribute1 = attribute1;
	}

	/**
	 * Gets the attribute2.
	 *
	 * @return The attribute2.
	 */
	@VerifiableAttribute
	public Integer getAttribute2() {
		return this.attribute2;
	}

	/**
	 * Sets the attribute2.
	 *
	 * @param attribute2 New attribute2.
	 */
	public void setAttribute2(final Integer attribute2) {
		this.attribute2 = attribute2;
	}

	/**
	 * Gets the attribute3.
	 *
	 * @return The attribute3.
	 */
	@VerifiableAttribute
	public Long getAttribute3() {
		return this.attribute3;
	}

	/**
	 * Sets the attribute3.
	 *
	 * @param attribute3 New attribute3.
	 */
	public void setAttribute3(final Long attribute3) {
		this.attribute3 = attribute3;
	}

	/**
	 * Gets the attribute4.
	 *
	 * @return The attribute4.
	 */
	public String getAttribute4() {
		return this.attribute4;
	}

	/**
	 * Sets the attribute4.
	 *
	 * @param attribute4 New attribute4.
	 */
	public void setAttribute4(final String attribute4) {
		this.attribute4 = attribute4;
	}

	/**
	 * Gets the verification.
	 *
	 * @return The verification.
	 */
	@Override
	public SortedSet<Verification> getVerification() {
		// If the sort has not been initialized.
		if (this.verification == null) {
			// Initializes the set.
			this.verification = new TreeSet<>();
		}
		// Returns the set.
		return this.verification;
	}

	/**
	 * Sets the verification.
	 *
	 * @param verification New verification.
	 */
	public void setVerification(final SortedSet<Verification> verification) {
		this.verification = verification;
	}

	/**
	 * @see org.coldis.library.model.VerifiableObject#getVerificationStatus()
	 */
	@Override
	public VerificationStatus getVerificationStatus() {
		return VerifiableObject.getVerificationStatus(this);
	}

}
