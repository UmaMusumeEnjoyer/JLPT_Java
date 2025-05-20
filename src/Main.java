package ai;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class Main {
    /**
     * Gọi file exe xử lý ảnh, trả về JSON kết quả OCR.
     * @param imagePath Đường dẫn file ảnh
     * @return Chuỗi JSON trả về từ exe
     */
    public static String extractQuestionsFromImageByExe(String imagePath) throws Exception {
        String exePath = "dist/japanese_ocr.exe"; // Đường dẫn đến file exe
        java.io.File imgFile = new java.io.File(imagePath);
        String absImagePath = imgFile.getAbsolutePath();

        System.out.println("[LocalOcrUtil] Đường dẫn exe: " + new java.io.File(exePath).getAbsolutePath());
        System.out.println("[LocalOcrUtil] Đường dẫn ảnh truyền vào: " + absImagePath);

        ProcessBuilder pb = new ProcessBuilder(exePath, absImagePath);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("[LocalOcrUtil] OCR exe failed with exit code: " + exitCode);
            System.err.println("[LocalOcrUtil] Output (stdout+stderr):\n" + output);
            throw new RuntimeException("OCR exe failed with exit code " + exitCode + ". Output: " + output);
        }

        return output.toString().trim();
    }

    public static void main(String[] args) {
        try {
            String imagePath = "C:\\Users\\Admin\\Downloads\\0a0faed19dec28b271fd.jpg"; // Đường dẫn đến ảnh
            String jsonResult = extractQuestionsFromImageByExe(imagePath);

            // Ghi kết quả vào file .txt
            String outputFilePath = "ocr_result.txt";
            try (FileWriter writer = new FileWriter(outputFilePath, false)) {
                writer.write(jsonResult);
            }

            System.out.println("✅ Kết quả OCR đã được ghi vào file: " + outputFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
