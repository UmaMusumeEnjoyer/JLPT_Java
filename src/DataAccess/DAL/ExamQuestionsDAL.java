package DataAccess.DAL;

import DataAccess.DbConnect;
import java.util.List;
import java.util.ArrayList;

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
}
