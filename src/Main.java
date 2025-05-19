import DAL.DbConnect;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        //System.out.println("Hoàn thành nhận dạng văn bản tiếng Nhật từ ảnh.");
        DbConnect db = new DbConnect();
        try {
            // Kết nối đến cơ sở dữ liệu
            Connection connection = db.getConnection();
            System.out.println("Kết nối thành công!");

            // Thực hiện truy vấn
            String sql = "SELECT * FROM Exams";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Xử lý kết quả
            while (resultSet.next()) {
                System.out.println("Dữ liệu: " + resultSet.getString("Title"));
            }

            // Đóng kết nối
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}