package DataAccess.DTO;

public class ExamQuestions {
    private int examID;
    private int questionID;
    private int order;
    private float score;


    public ExamQuestions(int examID, int questionID, int order, float score) {
        this.examID = examID;
        this.questionID = questionID;
        this.order = order;
        this.score = score;
    }

    public int getExamID() {
        return examID;
    }

    public void setExamID(int examID) {
        this.examID = examID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }


}
