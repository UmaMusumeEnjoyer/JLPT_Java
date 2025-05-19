package DataAccess.DTO;

public class Answers {
    private int answersID;
    private int questionID;
    private String content;
    private boolean isCorrect;
    private int order;

    public Answers(int answersID, int questionID, String content, boolean isCorrect, int order) {
        this.answersID = answersID;
        this.questionID = questionID;
        this.content = content;
        this.isCorrect = isCorrect;
        this.order = order;
    }
    public int getAnswersID() {
        return answersID;
    }
    public void setAnswersID(int answersID) {
        this.answersID = answersID;
    }
    public int getQuestionID() {
        return questionID;
    }
    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public boolean isCorrect() {
        return isCorrect;
    }
    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }

}
