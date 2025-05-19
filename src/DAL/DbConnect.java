package DAL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//"jdbc:mysql://127.0.0.1:3306/JLPT", "root", "16082005"
public class DbConnect {
    private static final String CONNECTION_STRING = "jdbc:mysql://127.0.0.1:3306/JLPT";
    private static final String USER = "root";
    private static final String PASSWORD = "16082005"; // sửa theo mật khẩu MySQL của bạn

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC Driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found.", e);
        }
    }

    // Truy vấn SELECT, trả về List các dòng
    public List<List<Object>> getData(String sql, List<SqlParameter> parameters) throws SQLException {
        List<List<Object>> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParameters(stmt, parameters);

            try (ResultSet rs = stmt.executeQuery()) {
                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    List<Object> row = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getObject(i));
                    }
                    result.add(row);
                }
            }
        }
        return result;
    }

    // INSERT/UPDATE/DELETE
    public int executeNonQuery(String sql, List<SqlParameter> parameters) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParameters(stmt, parameters);
            return stmt.executeUpdate();
        }
    }

    // Trả về giá trị đầu tiên
    public Object executeScalar(String sql, List<SqlParameter> parameters) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParameters(stmt, parameters);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getObject(1) : null;
            }
        }
    }

    // Gọi stored procedure và trả về 1 giá trị
    public Object executeSpScalar(String procedureName, List<SqlParameter> parameters) throws SQLException {
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall("{ call " + procedureName + "(" + buildPlaceholders(parameters.size()) + ") }")) {

            setParameters(stmt, parameters);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getObject(1) : null;
            }
        }
    }

    // Gọi stored procedure và trả về danh sách kết quả
    public List<List<Object>> executeStoredProcedure(String procedureName, List<SqlParameter> parameters) throws SQLException {
        List<List<Object>> result = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall("{ call " + procedureName + "(" + buildPlaceholders(parameters.size()) + ") }")) {

            setParameters(stmt, parameters);
            try (ResultSet rs = stmt.executeQuery()) {
                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    List<Object> row = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getObject(i));
                    }
                    result.add(row);
                }
            }
        }
        return result;
    }

    // Gọi stored procedure và trả về số dòng bị ảnh hưởng
    public int executeSpNonQuery(String procedureName, List<SqlParameter> parameters) throws SQLException {
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall("{ call " + procedureName + "(" + buildPlaceholders(parameters.size()) + ") }")) {

            setParameters(stmt, parameters);
            return stmt.executeUpdate();
        }
    }

    // Giao dịch
    public void executeTransaction(SqlTransactionAction action) throws SQLException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                action.execute(conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new SQLException("Transaction failed and rolled back.", e);
            }
        }
    }

    // Thiết lập tham số
    private void setParameters(PreparedStatement stmt, List<SqlParameter> parameters) throws SQLException {
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i).getValue());
            }
        }
    }

    // Sinh chuỗi "?, ?, ?, ..." cho stored procedure
    private String buildPlaceholders(int count) {
        return String.join(", ", java.util.Collections.nCopies(count, "?"));
    }

    // Trả về kết nối thô
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
    }

    // Tham số SQL
    public static class SqlParameter {
        private final Object value;

        public SqlParameter(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }

    // Giao diện giao dịch
    @FunctionalInterface
    public interface SqlTransactionAction {
        void execute(Connection conn) throws SQLException;
    }
}
