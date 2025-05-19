package DataAccess.DAL;
import DataAccess.DbConnect;
import java.util.List;
import java.util.ArrayList;

public class ExamsDAL {
    private DbConnect dbConnect;
    public ExamsDAL() {
        dbConnect = new DbConnect();
    }

    // Lấy toàn bộ dữ liệu từ bảng Exams
    public List<List<Object>> getExams() throws Exception {
        String sql = "SELECT * FROM Exams";
        // Không có tham số truyền vào
        return dbConnect.getData(sql, new ArrayList<>());
    }
}
