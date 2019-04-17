package org.coldis.library.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.coldis.library.model.SimpleMessage;

/**
 * Checked business exception.
 */
public class BusinessException extends Exception {

	/**
	 * Generated serial.
	 */
	private static final long serialVersionUID = 5822486739411214238L;

	/**
	 * Default status code.
	 */
	private static final Integer DEFAULT_STATUS_CODE = 400;

	/**
	 * Messages.
	 */
	private Collection<SimpleMessage> messages;

	/**
	 * Status code.
	 */
	private Integer statusCode;

	/**
	 * Messages, status and cause constructor.
	 *
	 * @param messages   Exception messages.
	 * @param statusCode Exception status code.
	 * @param cause      Exception cause.
	 */
	public BusinessException(final Collection<SimpleMessage> messages, final Integer statusCode,
			final Throwable cause) {
		super(cause);
		this.messages = messages;
		this.statusCode = statusCode;
	}

	/**
	 * Messages and cause constructor.
	 *
	 * @param messages Exception messages.
	 * @param cause    Exception cause.
	 */
	public BusinessException(final Collection<SimpleMessage> messages, final Throwable cause) {
		this(messages, BusinessException.DEFAULT_STATUS_CODE, cause);
	}

	/**
	 * Messages and status constructor.
	 *
	 * @param messages   Exception messages.
	 * @param statusCode Exception status code.
	 */
	public BusinessException(final Collection<SimpleMessage> messages, final Integer statusCode) {
		this(messages, statusCode, null);
	}

	/**
	 * Messages constructor.
	 *
	 * @param messages Exception messages.
	 */
	public BusinessException(final Collection<SimpleMessage> messages) {
		this(messages, BusinessException.DEFAULT_STATUS_CODE, null);
	}

	/**
	 * Message, status and cause constructor.
	 *
	 * @param message    Exception message.
	 * @param statusCode Exception status code.
	 * @param cause      Exception cause.
	 */
	public BusinessException(final SimpleMessage message, final Integer statusCode, final Throwable cause) {
		this(Arrays.asList(new SimpleMessage[] { message }), statusCode, cause);
	}

	/**
	 * Message and cause constructor.
	 *
	 * @param message Exception message.
	 * @param cause   Exception cause.
	 */
	public BusinessException(final SimpleMessage message, final Throwable cause) {
		this(message, BusinessException.DEFAULT_STATUS_CODE, cause);
	}

	/**
	 * Message, status and cause constructor.
	 *
	 * @param message    Exception message.
	 * @param statusCode Exception status code.
	 */
	public BusinessException(final SimpleMessage message, final Integer statusCode) {
		this(message, statusCode, null);
	}

	/**
	 * Message constructor.
	 *
	 * @param message Exception message.
	 */
	public BusinessException(final SimpleMessage message) {
		this(message, BusinessException.DEFAULT_STATUS_CODE, null);
	}

	/**
	 * No arguments constructor.
	 */
	public BusinessException() {
		this((Collection<SimpleMessage>) null, BusinessException.DEFAULT_STATUS_CODE, null);
	}

	/**
	 * Gets the exception messages.
	 *
	 * @return The exception messages.
	 */
	public Collection<SimpleMessage> getMessages() {
		// If messages list has not been initialized.
		if (this.messages == null) {
			// Initializes it with an empty list.
			this.messages = new ArrayList<>();
		}
		// Returns the list.
		return this.messages;
	}

	/**
	 * Sets the exception messages.
	 *
	 * @param messages New exception messages.
	 */
	public void setMessages(final Collection<SimpleMessage> messages) {
		this.messages = messages;
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
			this.statusCode = BusinessException.DEFAULT_STATUS_CODE;
		}
		// Returns the status code.
		return this.statusCode;
	}

	/**
	 * Sets the statusCode.
	 *
	 * @param statusCode New statusCode.
	 */
	public void setStatusCode(final Integer statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the first message code, or null if there is no code for the message.
	 *
	 * @return The first message code, or null if there is no code for the message.
	 */
	public String getCode() {
		return (this.getMessages() != null) && !this.getMessages().isEmpty()
				? this.getMessages().iterator().next().getCode()
						: null;
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return (this.getMessages() != null) && !this.getMessages().isEmpty()
				? this.getMessages().iterator().next().getContent()
						: null;
	}

}
