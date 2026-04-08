package common.message;

import common.model.questions.Question;
import common.model.questions.MCQQuestion;
import common.model.questions.ShortAnswerQuestion;
import common.model.questions.TrueFalseQuestion;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class QuestionMessage extends Message {

    private Question question;

    public QuestionMessage(int messageId, LocalDateTime timestamp, int senderId, Question question) {

        super(messageId, timestamp, senderId);

        this.question = question;

    }

    public Question getQuestion() {

        return question;

    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override

    public String getMessageType() {

        return "QUESTION";

    }

    @Override

    public String serialize() {

        List<String> choices = Collections.emptyList();

        if (question instanceof MCQQuestion) {

            choices = ((MCQQuestion) question).getChoices();

        }

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                timestamp.toString(),
                String.valueOf(senderId),
                question.getQuestionType(),
                String.valueOf(question.getQuestionId()),
                MessageCodec.encode(question.getPrompt()),
                MessageCodec.encode(question.getTopic()),
                String.valueOf(question.getPoints()),
                MessageCodec.encodeList(choices));

    }

    static QuestionMessage fromParts(String[] parts) {

        if (parts.length < 10) {

            throw new IllegalArgumentException("Invalid QUESTION message.");

        }

        String questionType = parts[4];
        int questionId = Integer.parseInt(parts[5]);
        String prompt = MessageCodec.decode(parts[6]);
        String topic = MessageCodec.decode(parts[7]);
        int points = Integer.parseInt(parts[8]);
        Question question;

        if ("MCQ".equals(questionType)) {

            question = new MCQQuestion(questionId, prompt, topic, points, MessageCodec.decodeList(parts[9]), "");

        } else if ("TRUE_FALSE".equals(questionType)) {

            question = new TrueFalseQuestion(questionId, prompt, topic, points, false);

        } else if ("SHORT_ANSWER".equals(questionType)) {

            question = new ShortAnswerQuestion(questionId, prompt, topic, points, "");

        } else {

            throw new IllegalArgumentException("Unsupported question type: " + questionType);

        }

        return new QuestionMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                question);

    }

}
