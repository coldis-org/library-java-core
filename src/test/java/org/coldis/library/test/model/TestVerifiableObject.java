package org.coldis.library.test.model;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.coldis.library.model.VerifiableObject;
import org.coldis.library.model.Verification;
import org.coldis.library.model.VerificationStatus;

/**
 * Test object.
 */
public class TestVerifiableObject implements VerifiableObject {

	/**
	 * Verifiable attributes.
	 */
	private static final Set<String> VERIFIABLE_ATTRIBUTES = Set.of("attribute1", "attribute2", "attribute3");

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
	public TestVerifiableObject(SortedSet<Verification> verification) {
		super();
		this.verification = verification;
	}

	/**
	 * Gets the attribute1.
	 * 
	 * @return The attribute1.
	 */
	public String getAttribute1() {
		return attribute1;
	}

	/**
	 * Sets the attribute1.
	 * 
	 * @param attribute1 New attribute1.
	 */
	public void setAttribute1(String attribute1) {
		this.attribute1 = attribute1;
	}

	/**
	 * Gets the attribute2.
	 * 
	 * @return The attribute2.
	 */
	public Integer getAttribute2() {
		return attribute2;
	}

	/**
	 * Sets the attribute2.
	 * 
	 * @param attribute2 New attribute2.
	 */
	public void setAttribute2(Integer attribute2) {
		this.attribute2 = attribute2;
	}

	/**
	 * Gets the attribute3.
	 * 
	 * @return The attribute3.
	 */
	public Long getAttribute3() {
		return attribute3;
	}

	/**
	 * Sets the attribute3.
	 * 
	 * @param attribute3 New attribute3.
	 */
	public void setAttribute3(Long attribute3) {
		this.attribute3 = attribute3;
	}

	/**
	 * Gets the attribute4.
	 * 
	 * @return The attribute4.
	 */
	public String getAttribute4() {
		return attribute4;
	}

	/**
	 * Sets the attribute4.
	 * 
	 * @param attribute4 New attribute4.
	 */
	public void setAttribute4(String attribute4) {
		this.attribute4 = attribute4;
	}

	/**
	 * Gets the verification.
	 * 
	 * @return The verification.
	 */
	public SortedSet<Verification> getVerification() {
		// If the sort has not been initialized.
		if (verification == null) {
			// Initializes the set.
			verification = new TreeSet<>();
		}
		// Returns the set.
		return verification;
	}

	/**
	 * Sets the verification.
	 * 
	 * @param verification New verification.
	 */
	public void setVerification(SortedSet<Verification> verification) {
		this.verification = verification;
	}

	/**
	 * @see org.coldis.library.model.VerifiableObject#getVerifiableAttributes()
	 */
	@Override
	public Set<String> getVerifiableAttributes() {
		return VERIFIABLE_ATTRIBUTES;
	}

	/**
	 * @see org.coldis.library.model.VerifiableObject#getStatus()
	 */
	@Override
	public VerificationStatus getStatus() {
		return VerifiableObject.getStatus(this);
	}

}
