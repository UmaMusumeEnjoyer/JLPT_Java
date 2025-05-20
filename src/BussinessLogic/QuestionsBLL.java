package BussinessLogic;
import DataAccess.DAL.QuestionsDAL;
import java.util.List;

public class QuestionsBLL {
    private QuestionsDAL questionsDAL;

    public QuestionsBLL() {
        questionsDAL = new QuestionsDAL();
    }

    // Lấy toàn bộ dữ liệu từ bảng Questions
    public List<List<Object>> getQuestions() throws Exception {
        return questionsDAL.getQuestions();
    }

    // Sửa nội dung câu hỏi
    public void updateQuestion(int questionID, String newContent) throws Exception {
        questionsDAL.updateQuestion(questionID, newContent);
    }

    // Xoá câu hỏi và các đáp án liên quan
    public void deleteQuestionAndAnswers(int questionID) throws Exception {
        questionsDAL.deleteAnswersByQuestionID(questionID); // Xoá đáp án trước
        questionsDAL.deleteQuestion(questionID);            // Xoá câu hỏi
    }

    // Lấy danh sách câu hỏi và đáp án theo Type và Level
    public List<List<Object>> getQuestionsWithAnswersByTypeAndLevel(String type, String level) throws Exception {
        return questionsDAL.getQuestionsWithAnswersByTypeAndLevel(type, level);
    }

    // Cập nhật toàn bộ nội dung, loại, cấp độ của câu hỏi
    public void updateQuestionFull(int questionID, String newContent, String newType, String newLevel) throws Exception {
        questionsDAL.updateQuestionFull(questionID, newContent, newType, newLevel);
    }

    // Thêm mới câu hỏi (content, type, level) => trả về questionID
    public int addQuestion(String content, String type, String level) throws Exception {
        return questionsDAL.addQuestion(content, type, level);
    }
    // Thêm đáp án cho câu hỏi
    public void addAnswer(String content, int questionId, boolean isCorrect) throws Exception {
        questionsDAL.addAnswer(content, questionId, isCorrect);
    }

    // Thêm mới câu hỏi và đáp án trong 1 transaction
    public void addQuestionWithAnswers(String content, String type, String level, List<String> answers, String suggestedLabel) throws Exception {
        questionsDAL.addQuestionWithAnswers(content, type, level, answers, suggestedLabel);
    }
}
