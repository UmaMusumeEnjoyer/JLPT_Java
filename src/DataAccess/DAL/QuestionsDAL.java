package DataAccess.DAL;
import DataAccess.DbConnect;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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

    // Cập nhật toàn bộ nội dung, loại, cấp độ của câu hỏi
    public void updateQuestionFull(int questionID, String newContent, String newType, String newLevel) throws Exception {
        String sql = "UPDATE Questions SET Content = ?, Type = ?, Level = ? WHERE QuestionID = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(newContent);
        params.add(newType);
        params.add(newLevel);
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

    // Lấy danh sách câu hỏi và đáp án theo Type và Level (cho phép truyền % để lấy tất cả)
    public List<List<Object>> getQuestionsWithAnswersByTypeAndLevel(String type, String level) throws Exception {
        String sql = "SELECT q.QuestionID, q.Content, q.Type, q.Level, a.AnswerID, a.Content, a.IsCorrect " +
                     "FROM Questions q " +
                     "LEFT JOIN Answers a ON q.QuestionID = a.QuestionID " +
                     "WHERE q.Type LIKE ? AND q.Level LIKE ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(type);
        params.add(level);
        return dbConnect.getData(sql, wrapParams(params));
    }

    // Thêm mới câu hỏi, trả về questionID
    public int addQuestion(String content, String type, String level) throws Exception {
        String sql = "INSERT INTO Questions (Content, Type, Level) VALUES (?, ?, ?)";
        ArrayList<Object> params = new ArrayList<>();
        params.add(content);
        params.add(type);
        params.add(level);
        dbConnect.executeNonQuery(sql, wrapParams(params));
        // Lấy ID vừa insert (MySQL: LAST_INSERT_ID())
        List<List<Object>> result = dbConnect.getData("SELECT LAST_INSERT_ID()", new ArrayList<>());
        return ((Number)result.get(0).get(0)).intValue();
    }

    // Thêm đáp án cho câu hỏi
    public void addAnswer(String content, int questionId, boolean isCorrect) throws Exception {
        String sql = "INSERT INTO Answers (Content, QuestionID, IsCorrect) VALUES (?, ?, ?)";
        ArrayList<Object> params = new ArrayList<>();
        params.add(content);
        params.add(questionId);
        params.add(isCorrect ? 1 : 0);
        dbConnect.executeNonQuery(sql, wrapParams(params));
    }

    // Thêm mới câu hỏi và đáp án trong 1 transaction
    public void addQuestionWithAnswers(String content, String type, String level, List<String> answers, String suggestedLabel) throws Exception {
        dbConnect.executeTransaction(conn -> {
            String sqlQ = "INSERT INTO Questions (Content, Type, Level) VALUES (?, ?, ?)";
            try (java.sql.PreparedStatement stmtQ = conn.prepareStatement(sqlQ, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                stmtQ.setString(1, content);
                stmtQ.setString(2, type);
                stmtQ.setString(3, level);
                stmtQ.executeUpdate();
                try (java.sql.ResultSet rs = stmtQ.getGeneratedKeys()) {
                    if (rs.next()) {
                        int questionId = rs.getInt(1);
                        String sqlA = "INSERT INTO Answers (Content, QuestionID, IsCorrect) VALUES (?, ?, ?)";
                        for (int i = 0; i < answers.size(); i++) {
                            String ansText = answers.get(i);
                            if (ansText != null && !ansText.trim().isEmpty()) {
                                String label = getLabel(i+1);
                                boolean isCorrect = (suggestedLabel != null && label.equalsIgnoreCase(suggestedLabel));
                                try (java.sql.PreparedStatement stmtA = conn.prepareStatement(sqlA)) {
                                    stmtA.setString(1, ansText);
                                    stmtA.setInt(2, questionId);
                                    stmtA.setInt(3, isCorrect ? 1 : 0);
                                    stmtA.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private String getLabel(int idx) {
        switch (idx) {
            case 1: return "A";
            case 2: return "B";
            case 3: return "C";
            case 4: return "D";
        }
        return "";
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

