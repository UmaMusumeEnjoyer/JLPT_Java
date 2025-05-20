package BussinessLogic;

import DataAccess.DAL.ExamQuestionsDAL;
import java.util.List;

public class ExamQuestionsBLL {
    private ExamQuestionsDAL examQuestionsDAL;

    public ExamQuestionsBLL() {
        examQuestionsDAL = new ExamQuestionsDAL();
    }

    // Lấy toàn bộ dữ liệu từ bảng ExamQuestions (ExamID, QuestionID)
    public List<List<Object>> getExamQuestions() throws Exception {
        return examQuestionsDAL.getExamQuestions();
    }
}
