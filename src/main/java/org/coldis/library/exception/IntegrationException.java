package org.coldis.library.exception;

import java.time.Duration;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.coldis.library.model.RetriableIn;
import org.coldis.library.model.SimpleMessage;

/**
 * Unchecked integration exception.
 */
public class IntegrationException extends RuntimeException implements RetriableIn {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = -6993989821432267915L;

	/**
	 * Default status code.
	 */
	private static final Integer DEFAULT_STATUS_CODE = 500;

	/**
	 * Default retry in.
	 */
	public static final Duration DEFAULT_RETRY_IN = Duration.ofSeconds(30L);

	/**
	 * Message.
	 */
	private SimpleMessage internalMessage;

	/**
	 * Status code.
	 */
	private Integer statusCode;

	/**
	 * Retry at.
	 */
	private final Duration retryIn;

	/**
	 * Message, status and cause constructor.
	 *
	 * @param message    Exception message.
	 * @param statusCode Exception status code.
	 * @param cause      The exception cause.
	 */
	public IntegrationException(final SimpleMessage message, final Integer statusCode, final Duration retryIn, final Throwable cause) {
		super(cause);
		this.internalMessage = message;
		this.statusCode = statusCode;
		this.retryIn = retryIn;
	}

	/**
	 * Message, status and cause constructor.
	 *
	 * @param message    Exception message.
	 * @param statusCode Exception status code.
	 * @param cause      The exception cause.
	 */
	public IntegrationException(final SimpleMessage message, final Integer statusCode, final Throwable cause) {
		this(message, statusCode, IntegrationException.DEFAULT_RETRY_IN, cause);
	}

	/**
	 * Message and status constructor.
	 *
	 * @param message    Exception message.
	 * @param statusCode Exception status code.
	 */
	public IntegrationException(final SimpleMessage message, final Integer statusCode) {
		this(message, statusCode, null);
	}

	/**
	 * Message and cause constructor.
	 *
	 * @param message Exception message.
	 * @param cause   The exception cause.
	 */
	public IntegrationException(final SimpleMessage message, final Throwable cause) {
		this(message, IntegrationException.DEFAULT_STATUS_CODE, cause);
	}

	/**
	 * Message constructor.
	 *
	 * @param message Exception message.
	 */
	public IntegrationException(final SimpleMessage message) {
		this(message, (Throwable) null);
	}

	/**
	 * No arguments constructor.
	 */
	public IntegrationException() {
		this(null);
	}

	/**
	 * Gets the internalMessage.
	 *
	 * @return The internalMessage.
	 */
	public SimpleMessage getInternalMessage() {
		return this.internalMessage;
	}

	/**
	 * Sets the internalMessage.
	 *
	 * @param internalMessage New internalMessage.
	 */
	public void setInternalMessage(
			final SimpleMessage internalMessage) {
		this.internalMessage = internalMessage;
	}

	/**
	 * Gets the statusCode.
	 *
	 * @return The statusCode.
	 */
	public Integer getStatusCode() {
		// If the status code is null.
		if (this.statusCode == null) {
			// Uses the default status code.
			this.statusCode = IntegrationException.DEFAULT_STATUS_CODE;
		}
		// Returns the status code.
		return this.statusCode;
	}

	/**
	 * Sets the statusCode.
	 *
	 * @param statusCode New statusCode.
	 */
	public void setStatusCode(
			final Integer statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the retryIn.
	 *
	 * @return The retryIn.
	 */
	@Override
	public Duration getRetryIn() {
		return this.retryIn;
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		String message = (this.getInternalMessage() != null ? this.getInternalMessage().toString() : null);
		message = (StringUtils.isBlank(message) ? ("Status: " + this.getStatusCode()) : message);
		return message;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.internalMessage, this.retryIn, this.statusCode);
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
		if ((obj == null) || (this.getClass() != obj.getClass())) {
			return false;
		}
		final IntegrationException other = (IntegrationException) obj;
		return Objects.equals(this.internalMessage, other.internalMessage) && Objects.equals(this.retryIn, other.retryIn)
				&& Objects.equals(this.statusCode, other.statusCode);
	}

	/**
	 * @see org.coldis.library.model.RetriableIn#setRetryIn(java.time.Duration)
	 */
	@Override
	public void setRetryIn(
			final Duration retryIn) {
		// TODO Auto-generated method stub

	}

}
