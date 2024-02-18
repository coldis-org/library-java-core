package org.coldis.library.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread context holder.
 */
public class ThreadMapContextHolder {

	/**
	 * Thread local variable.
	 */
	private static final ThreadLocal<Object> THREAD_LOCAL_MAP_CONTEXT = new ThreadLocal<>();

	/**
	 * Gets the thread attributes.
	 *
	 * @return The thread attributes.
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> getAttributes() {
		final Map<String, Object> attributes = (ThreadMapContextHolder.THREAD_LOCAL_MAP_CONTEXT.get() == null ? new HashMap<>()
				: (Map<String, Object>) ThreadMapContextHolder.THREAD_LOCAL_MAP_CONTEXT.get());
		ThreadMapContextHolder.THREAD_LOCAL_MAP_CONTEXT.set(attributes);
		return attributes;
	}

	/**
	 * Gets a thread attribute.
	 *
	 * @param  key A thread attribute.
	 * @return     The attribute value.
	 */
	public static Object getAttribute(
			final String key) {
		return ThreadMapContextHolder.getAttributes().get(key);
	}

	/**
	 * Sets a thread attribute.
	 *
	 * @param key   The attribute key.
	 * @param value The attribute value.
	 */
	public static void setAttribute(
			final String key,
			final Object value) {
		ThreadMapContextHolder.getAttributes().put(key, value);
	}

	/**
	 * Clears a thread context.
	 */
	public static void clear() {
		ThreadMapContextHolder.THREAD_LOCAL_MAP_CONTEXT.remove();
	}
}
