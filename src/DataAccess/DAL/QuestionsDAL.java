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
        // Không có tham số truyền vào
        return dbConnect.getData(sql, new ArrayList<>());
    }
}
