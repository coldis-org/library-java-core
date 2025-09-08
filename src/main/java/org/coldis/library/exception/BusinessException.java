package org.coldis.library.exception;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.coldis.library.model.RetriableIn;
import org.coldis.library.model.SimpleMessage;

/**
 * Checked business exception.
 */
public class BusinessException extends Exception implements RetriableIn {

	/**
	 * Generated serial.
	 */
	private static final long serialVersionUID = 5822486739411214238L;

	/**
	 * Default status code.
	 */
	private static final Integer DEFAULT_STATUS_CODE = 400;

	/** Default retry in. */
	public static final Duration DEFAULT_RETRY_IN = Duration.ofSeconds(30L);

	/**
	 * Messages.
	 */
	private Collection<SimpleMessage> messages;

	/**
	 * Status code.
	 */
	private Integer statusCode;

	/** Retry in. */
	private Duration retryIn;

	/**
	 * Messages, status and cause constructor.
	 *
	 * @param messages   Exception messages.
	 * @param statusCode Exception status code.
	 * @param cause      Exception cause.
	 */
	public BusinessException(final Collection<SimpleMessage> messages, final Integer statusCode, final Duration retryIn, final Throwable cause) {
		super(cause);
		this.messages = messages;
		this.statusCode = statusCode;
		this.retryIn = retryIn;
	}

	/**
	 * Messages, status and cause constructor.
	 *
	 * @param messages   Exception messages.
	 * @param statusCode Exception status code.
	 * @param cause      Exception cause.
	 */
	public BusinessException(final Collection<SimpleMessage> messages, final Integer statusCode, final Throwable cause) {
		this(messages, statusCode, (Set.of(BusinessException.DEFAULT_STATUS_CODE).contains(statusCode) ? null : BusinessException.DEFAULT_RETRY_IN), cause);
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
	public void setMessages(
			final Collection<SimpleMessage> messages) {
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
	 * Sets the retryIn.
	 *
	 * @param retryIn New retryIn.
	 */
	@Override
	public void setRetryIn(
			final Duration retryIn) {
		this.retryIn = retryIn;
	}

	/**
	 * Gets the first message code, or null if there is no code for the message.
	 *
	 * @return The first message code, or null if there is no code for the message.
	 */
	public String getCode() {
		return (this.getMessages() != null) && !this.getMessages().isEmpty() ? this.getMessages().iterator().next().getCode() : null;
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		String message = (CollectionUtils.isNotEmpty(this.getMessages()) ? StringUtils.join(this.getMessages(), " ") : null);
		message = (StringUtils.isBlank(message) ? ("Status: " + this.getStatusCode()) : message);
		return message;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.messages, this.retryIn, this.statusCode);
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
		final BusinessException other = (BusinessException) obj;
		return Objects.equals(this.messages, other.messages) && Objects.equals(this.retryIn, other.retryIn)
				&& Objects.equals(this.statusCode, other.statusCode);
	}

}
