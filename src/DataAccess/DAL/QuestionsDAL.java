package DataAccess.DAL;
import DataAccess.DbConnect;
import java.util.List;
import java.util.ArrayList;

public class QuestionsDAL {
    private DbConnect dbConnect;

    public QuestionsDAL() {
        dbConnect = new DbConnect();
    }

    // Lấy toàn bộ dữ liệu từ bảng Questions
    public List<List<Object>> getQuestions() throws Exception {
        String sql = "SELECT * FROM Questions";
        return dbConnect.getData(sql, new ArrayList<>());
    }

    // Sửa nội dung câu hỏi
    public void updateQuestion(int questionID, String newContent) throws Exception {
        String sql = "UPDATE Questions SET Content = ? WHERE QuestionID = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(newContent);
        params.add(questionID);
        dbConnect.executeNonQuery(sql, wrapParams(params));
    }

    // Xoá câu hỏi
    public void deleteQuestion(int questionID) throws Exception {
        String sql = "DELETE FROM Questions WHERE QuestionID = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(questionID);
        dbConnect.executeNonQuery(sql, wrapParams(params));
    }

    // Xoá tất cả đáp án của một câu hỏi
    public void deleteAnswersByQuestionID(int questionID) throws Exception {
        String sql = "DELETE FROM Answers WHERE QuestionID = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(questionID);
        dbConnect.executeNonQuery(sql, wrapParams(params));
    }

    // Helper: chuyển ArrayList<Object> sang List<DbConnect.SqlParameter>
    private List<DbConnect.SqlParameter> wrapParams(List<Object> params) {
        List<DbConnect.SqlParameter> sqlParams = new ArrayList<>();
        for (Object obj : params) {
            sqlParams.add(new DbConnect.SqlParameter(obj));
        }
        return sqlParams;
    }
}
