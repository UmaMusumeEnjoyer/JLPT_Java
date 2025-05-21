package DataAccess.DAL;
import DataAccess.DbConnect;
import java.util.List;
import java.util.ArrayList;

public class AnswersDAL {
    private DbConnect dbConnect;

    public AnswersDAL() {
        dbConnect = new DbConnect();
    }

    // Lấy toàn bộ dữ liệu từ bảng Answers
    public List<List<Object>> getAnswers() throws Exception {
        String sql = "SELECT * FROM Answers";
        // Không có tham số truyền vào
        return dbConnect.getData(sql, new ArrayList<>());
    }

    // Cập nhật nội dung và đáp án đúng/sai
    public void updateAnswer(int answerId, String content, boolean isCorrect) throws Exception {
        String sql = "UPDATE Answers SET Content = ?, IsCorrect = ? WHERE AnswerID = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(content);
        params.add(isCorrect ? 1 : 0);
        params.add(answerId);
        dbConnect.executeNonQuery(sql, wrapParams(params));
    }

    public List<DataAccess.DTO.Answers> getAnswersByQuestionID(int questionID) throws Exception {
        String sql = "SELECT * FROM Answers WHERE QuestionID = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(questionID);
        List<List<Object>> rawData = dbConnect.getData(sql, wrapParams(params));

        List<DataAccess.DTO.Answers> answers = new ArrayList<>();
        for (List<Object> row : rawData) {
            DataAccess.DTO.Answers answer = new DataAccess.DTO.Answers();
            answer.setAnswersID((Integer) row.get(0));
            answer.setQuestionID((Integer) row.get(1));
            answer.setContent((String) row.get(2));
            answer.setCorrect((Boolean) row.get(3));
            answers.add(answer);
        }
        return answers;
    }

    private List<DbConnect.SqlParameter> wrapParams(List<Object> params) {
        List<DbConnect.SqlParameter> sqlParams = new ArrayList<>();
        for (Object obj : params) {
            sqlParams.add(new DbConnect.SqlParameter(obj));
        }
        return sqlParams;
    }
}
