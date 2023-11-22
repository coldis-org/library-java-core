package org.coldis.library.model.verification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.coldis.library.model.Typable;
import org.coldis.library.model.view.ModelView;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.validation.Valid;

/**
 * Verification information.
 */
@JsonTypeName(value = Verification.TYPE_NAME)
public class Verification implements Typable {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = -5564404243971176102L;

	/**
	 * Type name.
	 */
	public static final String TYPE_NAME = "org.coldis.library.model.Verification";

	/**
	 * Verification items.
	 */
	private List<VerificationItem> items;

	/**
	 * No arguments constructor.
	 */
	public Verification() {
		super();
	}

	/**
	 * Default constructor.
	 *
	 * @param items Verification items.
	 */
	public Verification(final List<VerificationItem> items) {
		super();
		this.items = items;
	}

	/**
	 * Returns The verification items.
	 *
	 * @return The verification items.
	 */
	@Valid
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public List<VerificationItem> getItems() {
		// Makes sure the set is initialized.
		this.items = this.items == null ? new ArrayList<>() : this.items;
		// Returns the set.
		return this.items;
	}

	/**
	 * Sets the verification items.
	 *
	 * @param items New verification items.
	 */
	public void setItems(
			final List<VerificationItem> items) {
		this.items = items;
	}

	/**
	 * @see org.coldis.library.model.Typable#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return Verification.TYPE_NAME;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.items);
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
		if (!(obj instanceof Verification)) {
			return false;
		}
		final Verification other = (Verification) obj;
		return Objects.equals(this.items, other.items);
	}

}
