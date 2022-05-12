package org.coldis.library.model.verification;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.coldis.library.model.Typable;
import org.coldis.library.model.view.ModelView;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Verification question.
 */
@JsonTypeName(value = VerificationQuestion.TYPE_NAME)
public class VerificationQuestion extends VerificationItem implements Typable {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 7777545529572957437L;

	/**
	 * Type name.
	 */
	protected static final String TYPE_NAME = "br.com.supersim.service.party.model.verification.VerificationQuestion";

	/**
	 * Question.
	 */
	private String question;

	/**
	 * Answers options.
	 */
	private List<Map<String, String>> options;

	/**
	 * Possible correct answers.
	 */
	private List<String> correctAnswer;

	/**
	 * Submitted answer.
	 */
	private String answer;

	/**
	 * Data sources.
	 */
	private String dataSource;

	/**
	 * Gets the question.
	 *
	 * @return The question.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getQuestion() {
		return this.question;
	}

	/**
	 * Sets the question.
	 *
	 * @param question New question.
	 */
	public void setQuestion(
			final String question) {
		this.question = question;
	}

	/**
	 * @see org.coldis.library.model.verification.VerificationItem#getDescription()
	 */
	@Override
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getDescription() {
		return this.getQuestion();
	}

	/**
	 * @see org.coldis.library.model.verification.VerificationItem#setDescription(java.lang.Object)
	 */
	@Override
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public void setDescription(
			final Object description) {
		this.setQuestion(this.getDescriptionFromSimpleMessage(description));
	}

	/**
	 * Gets the options.
	 *
	 * @return The options.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public List<Map<String, String>> getOptions() {
		return this.options;
	}

	/**
	 * Sets the options.
	 *
	 * @param options New options.
	 */
	public void setOptions(
			final List<Map<String, String>> options) {
		this.options = options;
	}

	/**
	 * Gets the correctAnswer.
	 *
	 * @return The correctAnswer.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public List<String> getCorrectAnswer() {
		return this.correctAnswer;
	}

	/**
	 * Sets the correctAnswer.
	 *
	 * @param correctAnswer New correctAnswer.
	 */
	public void setCorrectAnswer(
			final List<String> correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	/**
	 * Gets the answer.
	 *
	 * @return The answer.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getAnswer() {
		return this.answer;
	}

	/**
	 * Sets the answer.
	 *
	 * @param answer New answer.
	 */
	public void setAnswer(
			final String answer) {
		this.answer = answer;
	}

	/**
	 * Gets the dataSource.
	 *
	 * @return The dataSource.
	 */
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getDataSource() {
		return this.dataSource;
	}

	/**
	 * Sets the dataSource.
	 *
	 * @param dataSource New dataSource.
	 */
	public void setDataSource(
			final String dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @see org.coldis.library.model.Typable#getTypeName()
	 */
	@Override
	@JsonView({ ModelView.Persistent.class, ModelView.Public.class })
	public String getTypeName() {
		return VerificationQuestion.TYPE_NAME;
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
		if (!super.equals(obj) || !(obj instanceof VerificationQuestion)) {
			return false;
		}
		final VerificationQuestion other = (VerificationQuestion) obj;
		return Objects.equals(this.answer, other.answer) && Objects.equals(this.correctAnswer, other.correctAnswer)
				&& Objects.equals(this.dataSource, other.dataSource) && Objects.equals(this.options, other.options)
				&& Objects.equals(this.question, other.question);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + Objects.hash(this.answer, this.correctAnswer, this.dataSource, this.options, this.question);
		return result;
	}
}
