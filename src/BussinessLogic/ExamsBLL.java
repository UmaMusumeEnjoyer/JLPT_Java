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
//    public void addExam(String examName, String examDate) throws Exception {
//        examsDAL.addExam(examName, examDate);
//    }
//
//    // Cập nhật thông tin bài thi
//    public void updateExam(int examID, String examName, String examDate) throws Exception {
//        examsDAL.updateExam(examID, examName, examDate);
//    }
//
//    // Xóa một bài thi
//    public void deleteExam(int examID) throws Exception {
//        examsDAL.deleteExam(examID);
//    }
}
