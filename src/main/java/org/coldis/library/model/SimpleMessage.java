package org.coldis.library.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Simple message.
 */
@JsonTypeName(value = SimpleMessage.TYPE_NAME)
public class SimpleMessage implements TypedObject {

	/**
	 * Generated serial.
	 */
	private static final long serialVersionUID = 691559440427608789L;

	/**
	 * Type name.
	 */
	public static final String TYPE_NAME = "org.coldis.library.model.SimpleMessage";

	/**
	 * Simple message code.
	 */
	private String code;

	/**
	 * Simple message content.
	 */
	private String content;

	/**
	 * Simple message parameters.
	 */
	private Object[] parameters;

	/**
	 * No arguments constructor.
	 */
	public SimpleMessage() {
	}

	/**
	 * Message code, content and parameters constructor.
	 *
	 * @param code       Code for the response message.
	 * @param content    Content for the response message.
	 * @param parameters Message parameters.
	 */
	public SimpleMessage(final String code, final String content, final Object[] parameters) {
		this.code = code;
		this.content = content;
		this.parameters = parameters;
	}

	/**
	 * Message code, content and parameters constructor.
	 *
	 * @param message    content for the response message.
	 * @param parameters Message parameters.
	 */
	public SimpleMessage(final String message, final Object[] parameters) {
		this(message, message, parameters);
	}

	/**
	 * Message code and content constructor.
	 *
	 * @param code    Code for the response message.
	 * @param message content for the response message.
	 */
	public SimpleMessage(final String code, final String message) {
		this(code, message, null);
	}

	/**
	 * Message content constructor.
	 *
	 * @param message content for the response message.
	 */
	public SimpleMessage(final String message) {
		this(message, message, null);
	}

	/**
	 * Gets the code.
	 *
	 * @return The code.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 *
	 * @param code New code.
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Gets the content.
	 *
	 * @return The content.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getContent() {
		return content;
	}

	/**
	 * Sets the message.
	 *
	 * @param content New content.
	 */
	public void setContent(final String content) {
		this.content = content;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return The parameters.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public Object[] getParameters() {
		return parameters;
	}

	/**
	 * Sets the parameters.
	 *
	 * @param parameters New parameters.
	 */
	public void setParameters(final Object[] parameters) {
		this.parameters = parameters;
	}

	/**
	 * @see org.coldis.library.model.TypedObject#getTypeName()
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getTypeName() {
		return SimpleMessage.TYPE_NAME;
	}

}
