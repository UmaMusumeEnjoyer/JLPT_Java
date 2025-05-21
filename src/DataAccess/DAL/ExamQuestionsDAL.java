package DataAccess.DAL;

import DataAccess.DbConnect;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ExamQuestionsDAL {
    private DbConnect dbConnect;

    public ExamQuestionsDAL() {
        dbConnect = new DbConnect();
    }

    // Lấy toàn bộ dữ liệu từ bảng ExamQuestions (ExamID, QuestionID)
    public List<List<Object>> getExamQuestions() throws Exception {
        String sql = "SELECT ExamID, QuestionID FROM ExamQuestions";
        return dbConnect.getData(sql, new ArrayList<>());
    }

    // Thêm câu hỏi vào đề thi
    public void addExamQuestion(int examId, int questionId) throws Exception {
        String sql = "INSERT INTO ExamQuestions (ExamID, QuestionID) VALUES (?, ?)";
        ArrayList<Object> params = new ArrayList<>();
        params.add(examId);
        params.add(questionId);
        dbConnect.executeNonQuery(sql, wrapParams(params));
    }

    // Overload: Thêm câu hỏi vào đề thi với Connection (for transaction)
    public void addExamQuestion(Connection conn, int examId, int questionId) throws Exception {
        String sql = "INSERT INTO ExamQuestions (ExamID, QuestionID) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            stmt.setInt(2, questionId);
            stmt.executeUpdate();
        }
    }

    // Xóa toàn bộ câu hỏi khỏi đề thi
    public void deleteExamQuestions(int examId) throws Exception {
        String sql = "DELETE FROM ExamQuestions WHERE ExamID = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(examId);
        dbConnect.executeNonQuery(sql, wrapParams(params));
    }

    private List<DbConnect.SqlParameter> wrapParams(List<Object> params) {
        List<DbConnect.SqlParameter> sqlParams = new ArrayList<>();
        for (Object obj : params) {
            sqlParams.add(new DbConnect.SqlParameter(obj));
        }
        return sqlParams;
    }
}
