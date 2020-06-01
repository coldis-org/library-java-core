package org.coldis.library.test.helper;

import org.coldis.library.model.Identifiable;

/**
 * Test enum.
 */
public enum TestEnum implements Identifiable {

	/**
	 * Up to 6.
	 */
	ENUM1(1L, 0, 6),

	/**
	 * Between 6 and 12.
	 */
	ENUM2(2L, 6, 12),

	/**
	 * Between 12 and 24.
	 */
	ENUM3(3L, 12, 18),

	/**
	 * Between 24 and 60.
	 */
	ENUM4(4L, 18, 24),

	/**
	 * From 60.
	 */
	ENUM5(5L, 24, 12000);

	/**
	 * Id.
	 */
	private Long id;

	/**
	 * Floor.
	 */
	private Integer floor;

	/**
	 * Ceil.
	 */
	private Integer ceil;

	/**
	 * Default constructor.
	 *
	 * @param id    Range id.
	 * @param floor Range floor.
	 * @param ceil  Range ceil.
	 */
	private TestEnum(final Long id, final Integer floor, final Integer ceil) {
		this.id = id;
		this.floor = floor;
		this.ceil = ceil;
	}

	/**
	 * Gets the id.
	 *
	 * @return The id.
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * Gets the floor.
	 *
	 * @return The floor.
	 */
	public Integer getFloor() {
		return this.floor;
	}

	/**
	 * Gets the ceil.
	 *
	 * @return The ceil.
	 */
	public Integer getCeil() {
		return this.ceil;
	}

}
