package GUI;

import DataAccess.DTO.Questions;
import DataAccess.DTO.Answers;
import BussinessLogic.AnswersBLL;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import javax.swing.*;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.awt.Component;

public class ExamExporter {
    public static void exportToPDF(String examTitle, List<Questions> questions) throws Exception {
        File file = new File(examTitle + ".pdf");
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        PDPageContentStream content = new PDPageContentStream(doc, page);
        // Load Japanese font from system (MS Gothic)
        PDType0Font unicodeFont = PDType0Font.load(doc, new java.io.File("lib/NotoSansJP-VariableFont_wght.ttf"));
        content.setFont(unicodeFont, 12);
        content.beginText();
        content.setLeading(16f);
        content.newLineAtOffset(50, 750);
        content.showText("Exam Title: " + examTitle);
        content.newLine();
        content.newLine();
        AnswersBLL answersBLL = new AnswersBLL();
        int index = 1;
        for (Questions question : questions) {
            content.showText(index + ". " + question.getContent());
            content.newLine();
            List<Answers> answers = answersBLL.getAnswersByQuestionID(question.getQuestionID());
            char ansLabel = 'A';
            for (Answers answer : answers) {
                String ansText = "    " + ansLabel + ". " + answer.getContent() + (answer.isCorrect() ? " (Correct)" : "");
                content.showText(ansText);
                content.newLine();
                ansLabel++;
            }
            content.newLine();
            index++;
        }
        content.endText();
        content.close();
        doc.save(file);
        doc.close();
    }

    public static void exportToDOC(String examTitle, List<Questions> questions) throws Exception {
        File file = new File(examTitle + ".doc");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Exam Title: " + examTitle);
            writer.newLine();
            writer.newLine();
            AnswersBLL answersBLL = new AnswersBLL();
            int index = 1;
            for (Questions question : questions) {
                writer.write(index + ". " + question.getContent());
                writer.newLine();
                List<Answers> answers = answersBLL.getAnswersByQuestionID(question.getQuestionID());
                char ansLabel = 'A';
                for (Answers answer : answers) {
                    writer.write("    " + ansLabel + ". " + answer.getContent() + (answer.isCorrect() ? " (Correct)" : ""));
                    writer.newLine();
                    ansLabel++;
                }
                writer.newLine();
                index++;
            }
        }
    }

    public static void exportToDOCWithAnswerKey(Component parent, String examTitle, List<Questions> questions) throws Exception {
        // Chọn thư mục lưu file
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) return;
        File dir = chooser.getSelectedFile();
        if (!dir.exists()) dir.mkdirs();

        // File đề thi (DOC)
        File docFile = new File(dir, examTitle + ".doc");
        // File đáp án (TXT)
        File answerFile = new File(dir, examTitle + "_AnswerKey.txt");

        AnswersBLL answersBLL = new AnswersBLL();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(docFile));
             BufferedWriter answerWriter = new BufferedWriter(new FileWriter(answerFile))) {
            writer.write("Exam Title: " + examTitle);
            writer.newLine();
            writer.newLine();
            int index = 1;
            for (Questions question : questions) {
                writer.write(index + ". " + question.getContent());
                writer.newLine();
                List<Answers> answers = answersBLL.getAnswersByQuestionID(question.getQuestionID());
                char ansLabel = 'A';
                String correctLabel = "";
                for (Answers answer : answers) {
                    writer.write("    " + ansLabel + ". " + answer.getContent());
                    writer.newLine();
                    if (answer.isCorrect()) correctLabel = String.valueOf(ansLabel);
                    ansLabel++;
                }
                writer.newLine();
                // Ghi đáp án đúng ra file answer
                answerWriter.write(index + ". " + correctLabel);
                answerWriter.newLine();
                index++;
            }
        }
    }

    public static void exportToPDFWithAnswerKey(Component parent, String examTitle, List<Questions> questions) throws Exception {
        // Chọn thư mục lưu file
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) return;
        File dir = chooser.getSelectedFile();
        if (!dir.exists()) dir.mkdirs();

        // File đề thi (PDF)
        File pdfFile = new File(dir, examTitle + ".pdf");
        // File đáp án (TXT)
        File answerFile = new File(dir, examTitle + "_AnswerKey.txt");

        AnswersBLL answersBLL = new AnswersBLL();
        try (BufferedWriter answerWriter = new BufferedWriter(new FileWriter(answerFile))) {
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            // Load Japanese font from system (MS Gothic)
            PDType0Font unicodeFont = PDType0Font.load(doc, new java.io.File("lib/NotoSansJP-VariableFont_wght.ttf"));
            content.setFont(unicodeFont, 12);
            content.beginText();
            content.setLeading(16f);
            content.newLineAtOffset(50, 750);
            content.showText("Exam Title: " + examTitle);
            content.newLine();
            content.newLine();
            int index = 1;
            for (Questions question : questions) {
                content.showText(index + ". " + question.getContent());
                content.newLine();
                List<Answers> answers = answersBLL.getAnswersByQuestionID(question.getQuestionID());
                char ansLabel = 'A';
                String correctLabel = "";
                for (Answers answer : answers) {
                    content.showText("    " + ansLabel + ". " + answer.getContent());
                    content.newLine();
                    if (answer.isCorrect()) correctLabel = String.valueOf(ansLabel);
                    ansLabel++;
                }
                content.newLine();
                // Ghi đáp án đúng ra file answer
                answerWriter.write(index + ". " + correctLabel);
                answerWriter.newLine();
                index++;
            }
            content.endText();
            content.close();
            doc.save(pdfFile);
            doc.close();
        }
    }
}
