package org.coldis.library.model;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

/**
 * Item that is optionally primary among others.
 */
public interface OptionallyPrimaryItem {

	/**
	 * Gets the primary.
	 *
	 * @return The primary.
	 */
	Boolean getPrimary();

	/**
	 * Sets the primary.
	 *
	 * @param primary New primary.
	 */
	void setPrimary(final Boolean primary);

	/**
	 * Gets the primary item of a collection.
	 *
	 * @param                <Item> Item type.
	 * @param itemCollection Item collection.
	 * @return The primary item of the collection.
	 */
	static <Item extends OptionallyPrimaryItem> Item getPrimary(final Collection<Item> itemCollection) {
		// Primary item.
		Item primaryItem = null;
		// If the collection is given.
		if (!CollectionUtils.isEmpty(itemCollection)) {
			// For each item in the collection.
			for (final Item currentItem : itemCollection) {
				// If the item is set as primary.
				if (currentItem != null && currentItem.getPrimary()) {
					// Sets the primary item and stops searching.
					primaryItem = currentItem;
					break;
				}
			}
		}
		// Returns the primary item.
		return primaryItem;
	}

	/**
	 * Only if there is no item set as primary in the collection, auto assign the
	 * primary flag to one of the items.
	 *
	 * @param                <Item> Item type.
	 * @param itemCollection Item collection.
	 */
	static <Item extends OptionallyPrimaryItem> void autoAssignPrimary(final Collection<Item> itemCollection) {
		// If the collection is given.
		if (!CollectionUtils.isEmpty(itemCollection)) {
			// Gets the primary item.
			Item primaryItem = getPrimary(itemCollection);
			// If there is no primary item.
			if (primaryItem == null) {
				// The first item is the primary one.
				itemCollection.stream().filter(item -> item != null).findAny().ifPresent(item -> item.setPrimary(true));
			}
		}
	}

	/**
	 * Sets a item as primary inside of a collection.
	 * 
	 * @param                <Item> Item type.
	 * @param itemCollection Item collection.
	 * @param primaryItem    The new primary item.
	 */
	static <Item extends OptionallyPrimaryItem> void setAsPrimary(final Collection<Item> itemCollection,
			final Item primaryItem) {
		// If the collection is given.
		if (!CollectionUtils.isEmpty(itemCollection)) {
			// If the primary item exists in the collection.
			if (itemCollection.contains(primaryItem)) {
				// Sets the item as primary and other items
				itemCollection.stream().forEach(item -> {
					// If the item is the given one (primary).
					if (item.equals(primaryItem)) {
						// Sets it as primary.
						item.setPrimary(true);
					}
					// If the item is not the given one.
					else {
						// Sets it as not primary.
						item.setPrimary(false);
					}
				});
			}
		}
	}

	/**
	 * Adds a new info to a list where one (and only one) record must be marked as
	 * primary.
	 *
	 * @param                <Item> Item type.
	 * @param itemCollection Item collection.
	 * @param newItem        New item to be added to the collection.
	 * @param overwrite      If existing item should be replaced. If not, item is
	 *                       discarded it already present.
	 */
	static <Item extends OptionallyPrimaryItem> void add(final Collection<Item> itemCollection, final Item newItem,
			Boolean overwrite) {
		// If the collection is given.
		if (itemCollection != null) {
			// If the collection does not contain the new item.
			if (!itemCollection.contains(newItem)) {
				// Adds the new item to the collection.
				itemCollection.add(newItem);
			}
			// If the collection contains the new item and it should be overwrite.
			else if (overwrite) {
				// Replaces the item in the collection.
				itemCollection.remove(newItem);
				itemCollection.add(newItem);
			}
			// If the new item is to be primary.
			if (newItem != null && newItem.getPrimary()) {
				// Makes sure the item is set as primary.
				setAsPrimary(itemCollection, newItem);
			}
			// If the item is not to be primary.
			else {
				// Makes sure that there is a primary item in the collection.
				OptionallyPrimaryItem.autoAssignPrimary(itemCollection);
			}
		}
	}

	/**
	 * Removes on item of the item collection, but assigning a new primary item (if
	 * the primary is removed).
	 *
	 * @param                <Item> Item type.
	 * @param itemCollection Item collection.
	 * @param itemToRemove   Item to be removed.
	 */
	static <Item extends OptionallyPrimaryItem> void remove(final Collection<Item> itemCollection,
			final Item itemToRemove) {
		// If the collection is given.
		if (!CollectionUtils.isEmpty(itemCollection)) {
			// Removes the item from the collection.
			itemCollection.remove(itemToRemove);
			// Makes sure that there is a primary item in the collection.
			OptionallyPrimaryItem.autoAssignPrimary(itemCollection);
		}
	}
}
