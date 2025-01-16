package org.coldis.library.exception;

import org.apache.commons.lang3.StringUtils;
import org.coldis.library.model.SimpleMessage;

/**
 * Unchecked integration exception.
 */
public class IntegrationException extends RuntimeException {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = -6993989821432267915L;

	/**
	 * Default status code.
	 */
	private static final Integer DEFAULT_STATUS_CODE = 500;

	/**
	 * Message.
	 */
	private SimpleMessage internalMessage;

	/**
	 * Status code.
	 */
	private Integer statusCode;

	/**
	 * Message, status and cause constructor.
	 *
	 * @param message    Exception message.
	 * @param statusCode Exception status code.
	 * @param cause      The exception cause.
	 */
	public IntegrationException(final SimpleMessage message, final Integer statusCode, final Throwable cause) {
		super(cause);
		this.internalMessage = message;
		this.statusCode = statusCode;
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
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		String message = (this.getInternalMessage() != null ? this.getInternalMessage().toString() : null);
		message = (StringUtils.isBlank(message) ? ("Status: " + this.getStatusCode()) : message);
		return message;
	}

}
