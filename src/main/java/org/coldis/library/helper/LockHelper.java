package org.coldis.library.helper;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Lock helper.
 */
public class LockHelper {

	/** Lock map. */
	private static final ConcurrentHashMap<String, Object> LOCK_MAP = new ConcurrentHashMap<>();

	/**
	 * Gets the lock key for the kind.
	 */
	public static Object getStringLockKey(
			final String key) {
		return LockHelper.LOCK_MAP.computeIfAbsent(key, k -> new Object());
	}

}
