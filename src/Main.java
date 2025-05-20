//package ai;

import ai.ExtractQuestionsFromImageByExe;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws Exception {
        ExtractQuestionsFromImageByExe extractor = new ExtractQuestionsFromImageByExe();
        String imagePath = "C:\\Users\\Admin\\Downloads\\0a0faed19dec28b271fd.jpg"; // Đường dẫn đến file ảnh
        String result = extractor.extractQuestionsFromImageByExe(imagePath);
        System.out.println("Kết quả OCR: " + result);
    }
}