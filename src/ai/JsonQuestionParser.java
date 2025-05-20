package ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class JsonQuestionParser {
    public static class Answer {
        @JsonProperty("label")
        public String label;
        @JsonProperty("text")
        public String text;
        @Override
        public String toString() {
            return label + ": " + text;
        }
    }

    public static class Question {
        @JsonProperty("question_text")
        public String questionText;
        @JsonProperty("options")
        public List<Answer> options;
        @JsonProperty("suggested_answer")
        public String suggestedAnswer;
        @Override
        public String toString() {
            return "Q: " + questionText + "\n" +
                    "Options: " + options + "\n" +
                    "Suggested: " + suggestedAnswer;
        }
    }

    public static class OcrResult {
        @JsonProperty("success")
        public boolean success;
        @JsonProperty("questions")
        public List<Question> questions;
        @JsonProperty("error")
        public String error;
    }

    public static List<Question> parseQuestions(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        OcrResult result = mapper.readValue(json, OcrResult.class);
        if (!result.success) {
            throw new RuntimeException("OCR error: " + result.error);
        }
        return result.questions;
    }

    // Demo main
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java ai.JsonQuestionParser <jsonFile>");
            return;
        }
        String json = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(args[0])), java.nio.charset.StandardCharsets.UTF_8);
        List<Question> questions = parseQuestions(json);
        for (Question q : questions) {
            System.out.println(q);
        }
    }
}
