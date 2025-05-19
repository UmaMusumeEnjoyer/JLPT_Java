// ...existing imports...

public class Main {
    public static void main(String[] args) {
        try {
            // Xuất ra file PDF ngay trong thư mục project
            ExportQuestionsPDF.exportToPDF("questions.pdf");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Lỗi khi xuất PDF: " + ex.getMessage());
        }
    }
}
