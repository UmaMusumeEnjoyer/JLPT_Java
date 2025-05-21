package BussinessLogic;
import DataAccess.DAL.ExamsDAL;

import java.util.List;

public class ExamsBLL {
    private ExamsDAL examsDAL;

    public ExamsBLL() {
        examsDAL = new ExamsDAL();
    }

    // Lấy toàn bộ dữ liệu từ bảng Exams
    public List<List<Object>> getExams() throws Exception {
        return examsDAL.getExams();
    }

    // Thêm một bài thi mới
    public int addExam(String title) throws Exception {
        return examsDAL.addExam(title);
    }

    // Overload: Thêm một bài thi mới với Connection (for transaction)
    public int addExam(java.sql.Connection conn, String title) throws Exception {
        return examsDAL.addExam(conn, title);
    }

    // Cập nhật thông tin bài thi
    public void updateExam(int examID, String title) throws Exception {
        examsDAL.updateExam(examID, title);
    }

    // Xóa một bài thi
    public void deleteExam(int examID) throws Exception {
        examsDAL.deleteExam(examID);
    }
}
