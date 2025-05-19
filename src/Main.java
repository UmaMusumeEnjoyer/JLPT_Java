import java.sql.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hoàn thành nhận dạng văn bản tiếng Nhật từ ảnh.");
        try{
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/JLPT", "root", "16082005");
            if (connection != null) {
                System.out.println("Kết nối thành công!");
            } else {
                System.out.println("Kết nối thất bại!");
            }
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Exams");
            while (rs.next()) {
                System.out.println(rs.getString("ExamID") + " " + rs.getString("Title"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




    }
}