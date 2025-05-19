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
}
