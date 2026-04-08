package common.message;

import common.model.questions.MCQQuestion;
import common.model.questions.Question;
import common.model.questions.ShortAnswerQuestion;
import common.model.questions.TrueFalseQuestion;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class PostQuestionMessage extends Message {

    private String joinCode;

    private Question question;

    public PostQuestionMessage(int messageId, LocalDateTime timestamp, int senderId, String joinCode, Question question) {

        super(messageId, timestamp, senderId);

        this.joinCode = joinCode;
        this.question = question;

    }

    public String getJoinCode() { return joinCode; }

    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }

    @Override
    public String getMessageType() {

        return "POST_QUESTION";

    }

    @Override
    public String serialize() {

        List<String> choices = Collections.emptyList();
        String correctAnswer = "";

        if (question instanceof MCQQuestion) {

            MCQQuestion mcqQuestion = (MCQQuestion) question;
            choices = mcqQuestion.getChoices();
            correctAnswer = mcqQuestion.getCorrectChoice();

        } else if (question instanceof TrueFalseQuestion) {

            correctAnswer = String.valueOf(((TrueFalseQuestion) question).isCorrectValue());

        } else if (question instanceof ShortAnswerQuestion) {

            correctAnswer = ((ShortAnswerQuestion) question).getExpectedAnswer();

        }

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                timestamp.toString(),
                String.valueOf(senderId),
                MessageCodec.encode(joinCode),
                question.getQuestionType(),
                String.valueOf(question.getQuestionId()),
                MessageCodec.encode(question.getPrompt()),
                MessageCodec.encode(question.getTopic()),
                String.valueOf(question.getPoints()),
                MessageCodec.encodeList(choices),
                MessageCodec.encode(correctAnswer));

    }

    static PostQuestionMessage fromParts(String[] parts) {

        if (parts.length < 12) {

            throw new IllegalArgumentException("Invalid POST_QUESTION message.");

        }

        String joinCode = MessageCodec.decode(parts[4]);
        String questionType = parts[5];
        int questionId = Integer.parseInt(parts[6]);
        String prompt = MessageCodec.decode(parts[7]);
        String topic = MessageCodec.decode(parts[8]);
        int points = Integer.parseInt(parts[9]);
        List<String> choices = MessageCodec.decodeList(parts[10]);
        String correctAnswer = MessageCodec.decode(parts[11]);
        Question question;

        if ("MCQ".equals(questionType)) {

            question = new MCQQuestion(questionId, prompt, topic, points, choices, correctAnswer);

        } else if ("TRUE_FALSE".equals(questionType)) {

            question = new TrueFalseQuestion(questionId, prompt, topic, points, Boolean.parseBoolean(correctAnswer));

        } else if ("SHORT_ANSWER".equals(questionType)) {

            question = new ShortAnswerQuestion(questionId, prompt, topic, points, correctAnswer);

        } else {

            throw new IllegalArgumentException("Unsupported question type: " + questionType);

        }

        return new PostQuestionMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                joinCode,
                question);

    }

}
