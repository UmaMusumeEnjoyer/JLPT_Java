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

    // Thêm một câu hỏi mới
}
