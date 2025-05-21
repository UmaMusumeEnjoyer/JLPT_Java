package DataAccess.DAL;
import DataAccess.DbConnect;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

    // Thêm mới đề thi, trả về examID
    public int addExam(String title) throws Exception {
        return addExam(null, title);
    }

    // Overload: Thêm mới đề thi với Connection (for transaction)
    public int addExam(Connection conn, String title) throws Exception {
        String sql = "INSERT INTO Exams (Title) VALUES (?)";
        try {
            boolean localConn = false;
            if (conn == null) {
                conn = dbConnect.getConnection();
                localConn = true;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, title);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        if (localConn) conn.close();
                        return id;
                    } else {
                        throw new Exception("Failed to retrieve generated ExamID.");
                    }
                }
            }
        } catch (Exception ex) {
            throw new Exception("Error adding exam: " + ex.getMessage(), ex);
        }
    }

    // Cập nhật tên đề thi
    public void updateExam(int examID, String title) throws Exception {
        String sql = "UPDATE Exams SET Title = ? WHERE ExamID = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(title);
        params.add(examID);
        dbConnect.executeNonQuery(sql, wrapParams(params));
    }

    // Xóa đề thi
    public void deleteExam(int examID) throws Exception {
        String sql = "DELETE FROM Exams WHERE ExamID = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(examID);
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
