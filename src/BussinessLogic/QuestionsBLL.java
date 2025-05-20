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

}
