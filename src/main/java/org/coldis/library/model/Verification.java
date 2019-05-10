package org.coldis.library.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Verification information.
 */
@JsonTypeName(value = Verification.TYPE_NAME)
public class Verification implements TypedObject {

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
	private SortedSet<VerificationItem> items;

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
	public Verification(final SortedSet<VerificationItem> items) {
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
	public SortedSet<VerificationItem> getItems() {
		// Makes sure the set is initialized.
		this.items = this.items == null ? new TreeSet<>() : this.items;
		// Returns the set.
		return this.items;
	}

	/**
	 * Sets the verification items.
	 *
	 * @param items New verification items.
	 */
	public void setItems(final SortedSet<VerificationItem> items) {
		this.items = items;
	}

	/**
	 * @see org.coldis.library.model.TypedObject#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return Verification.TYPE_NAME;
	}

}
