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

    // Thêm câu hỏi vào đề thi
    public void addExamQuestion(int examId, int questionId) throws Exception {
        examQuestionsDAL.addExamQuestion(examId, questionId);
    }

    // Overload: Thêm câu hỏi vào đề thi với Connection (for transaction)
    public void addExamQuestion(java.sql.Connection conn, int examId, int questionId) throws Exception {
        examQuestionsDAL.addExamQuestion(conn, examId, questionId);
    }

    // Xóa toàn bộ câu hỏi khỏi đề thi
    public void deleteExamQuestions(int examId) throws Exception {
        examQuestionsDAL.deleteExamQuestions(examId);
    }
}
