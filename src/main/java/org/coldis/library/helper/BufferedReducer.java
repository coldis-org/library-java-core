package org.coldis.library.helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.coldis.library.model.Reduceable;

/**
 * Incremental reducer.
 */
public class BufferedReducer<TypeKey, Type extends Reduceable<Type>> {

	/** Buffer. */
	private Map<TypeKey, Type> localBuffer;

	/**
	 * Gets the not ready to flush buffer.
	 *
	 * @return The not ready to flush buffer.
	 */
	private synchronized Map<TypeKey, Type> getLocalBuffer() {
		this.localBuffer = (this.localBuffer == null ? new ConcurrentHashMap<>() : this.localBuffer);
		return this.localBuffer;
	}

	/** Flushes ready to flush buffer. */
	public void flushLocalBuffer(
			final Consumer<Type> flushReducedItemFunction) {
		final Map<TypeKey, Type> bufferToFlush = this.getLocalBuffer();
		this.localBuffer = null;
		for (final Type logSummary : bufferToFlush.values()) {
			flushReducedItemFunction.accept(logSummary);
		}
	}

	/**
	 * Reduces an object in a local buffer.
	 */

	public static <TypeKey, Type extends Reduceable<Type>> void reduce(
			final Map<TypeKey, Type> localBuffer,
			final TypeKey objectKey,
			final Type objectToBeReduced) {
		if (localBuffer.putIfAbsent(objectKey, objectToBeReduced) != null) {
			localBuffer.get(objectKey).reduce(objectToBeReduced);
		}
	}

	/**
	 * Reduces an object in a local buffer.
	 */
	public void reduce(
			final TypeKey objectKey,
			final Type objectToBeReduced) {
		BufferedReducer.reduce(this.getLocalBuffer(), objectKey, objectToBeReduced);
	}

}
