package GUI;

import ViewModels.ExamWithQuestionsVM;
import DataAccess.DTO.Questions;
import DataAccess.DTO.Exams;
import BussinessLogic.ExamsBLL;
import BussinessLogic.ExamQuestionsBLL;
import BussinessLogic.QuestionsBLL;
import BussinessLogic.AnswersBLL;
import DataAccess.DTO.Answers;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.io.font.constants.StandardFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class AddOrEditExamDialog extends JDialog {
    private JTextField titleField;
    private JList<Questions> questionJList;
    private DefaultListModel<Questions> questionListModel;
    private JButton btnSave, btnRandomize, btnPrintExam;
    private Exams editingExam;
    private List<Questions> allQuestions;
    private List<ExamWithQuestionsVM> allExams;
    // Thay đổi: Cho phép chọn từng câu hỏi, thêm vào danh sách đề thi, sau đó có nút random thứ tự các câu hỏi đã chọn
    private DefaultListModel<Questions> selectedQuestionsModel = new DefaultListModel<>();
    private JList<Questions> selectedQuestionsList = new JList<>(selectedQuestionsModel);

    public AddOrEditExamDialog(Frame parent, ExamWithQuestionsVM examVM, List<ExamWithQuestionsVM> allExams) {
        super(parent, examVM == null ? "試験追加" : "試験編集", true);
        this.allExams = allExams;
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        topPanel.add(new JLabel("試験名:"));
        titleField = new JTextField();
        topPanel.add(titleField);
        btnRandomize = new JButton("ランダム選択");
        topPanel.add(new JLabel(""));
        topPanel.add(btnRandomize);
        add(topPanel, BorderLayout.NORTH);

        questionListModel = new DefaultListModel<>();
        questionJList = new JList<>(questionListModel);
        questionJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Questions) {
                    Questions q = (Questions) value;
                    setText("[" + (q.getType() != null ? q.getType() : "") + "] " + q.getContent());
                }
                return this;
            }
        });
        questionJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(questionJList);
        add(scrollPane, BorderLayout.CENTER);

        btnSave = new JButton("保存");
        btnPrintExam = new JButton("IN ĐỀ");
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnSave);
        btnPanel.add(btnPrintExam);
        add(btnPanel, BorderLayout.SOUTH);

        // Thay đổi: Cho phép chọn từng câu hỏi, thêm vào danh sách đề thi, sau đó có nút random thứ tự các câu hỏi đã chọn
        JPanel selectPanel = new JPanel(new BorderLayout());
        selectPanel.setBorder(BorderFactory.createTitledBorder("全問題リスト"));
        questionJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JButton btnAddQuestion = new JButton("→ 追加");
        selectPanel.add(new JScrollPane(questionJList), BorderLayout.CENTER);
        selectPanel.add(btnAddQuestion, BorderLayout.SOUTH);

        JPanel selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.setBorder(BorderFactory.createTitledBorder("選択済み問題"));
        selectedQuestionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JButton btnRemoveQuestion = new JButton("← 削除");
        selectedPanel.add(new JScrollPane(selectedQuestionsList), BorderLayout.CENTER);
        selectedPanel.add(btnRemoveQuestion, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add(selectPanel);
        centerPanel.add(selectedPanel);
        add(centerPanel, BorderLayout.CENTER);

        btnAddQuestion.addActionListener(e -> {
            Questions q = questionJList.getSelectedValue();
            if (q != null && !selectedQuestionsModel.contains(q)) {
                selectedQuestionsModel.addElement(q);
            }
        });
        btnRemoveQuestion.addActionListener(e -> {
            Questions q = selectedQuestionsList.getSelectedValue();
            if (q != null) {
                selectedQuestionsModel.removeElement(q);
            }
        });

        btnRandomize.setText("選択済み問題をランダム並び替え");
        btnRandomize.addActionListener(e -> {
            java.util.List<Questions> temp = java.util.Collections.list(selectedQuestionsModel.elements());
            java.util.Collections.shuffle(temp);
            selectedQuestionsModel.clear();
            for (Questions q : temp) selectedQuestionsModel.addElement(q);
        });

        loadAllQuestions();
        if (examVM != null) {
            editingExam = examVM.getExam();
            titleField.setText(editingExam.getTitle());
            selectedQuestionsModel.clear();
            for (Questions q : examVM.getQuestions()) selectedQuestionsModel.addElement(q);
        }

        btnSave.addActionListener(this::onSave);
        btnRandomize.addActionListener(this::onRandomize);
        btnPrintExam.addActionListener(e -> onPrintExam());
    }

    private void loadAllQuestions() {
        try {
            QuestionsBLL bll = new QuestionsBLL();
            List<List<Object>> raw = bll.getQuestions();
            allQuestions = new ArrayList<>();
            for (List<Object> row : raw) {
                Questions q = new Questions();
                q.setQuestionID((Integer) row.get(0));
                q.setContent((String) row.get(1));
                if (row.size() > 2 && row.get(2) != null) q.setType(row.get(2).toString());
                if (row.size() > 3 && row.get(3) != null) q.setLevel(row.get(3).toString());
                allQuestions.add(q);
            }
            questionListModel.clear();
            for (Questions q : allQuestions) questionListModel.addElement(q);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "問題リストの取得エラー: " + ex.getMessage());
        }
    }

    private void onRandomize(ActionEvent e) {
        String input = JOptionPane.showInputDialog(this, "何問をランダムで選択しますか？", "ランダム選択", JOptionPane.QUESTION_MESSAGE);
        if (input == null) return;
        try {
            int n = Integer.parseInt(input.trim());
            if (n <= 0 || n > allQuestions.size()) throw new NumberFormatException();
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < allQuestions.size(); i++) indices.add(i);
            Collections.shuffle(indices);
            int[] selected = indices.subList(0, n).stream().mapToInt(i -> i).toArray();
            questionJList.setSelectedIndices(selected);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "有効な数値を入力してください。");
        }
    }

    private void onSave(ActionEvent e) {
        try {
            String title = titleField.getText().trim();
            java.util.List<Questions> selected = java.util.Collections.list(selectedQuestionsModel.elements());
            if (title.isEmpty() || selected.isEmpty()) {
                JOptionPane.showMessageDialog(this, "試験名と問題を選択してください。");
                return;
            }
            ExamsBLL examsBLL = new ExamsBLL();
            ExamQuestionsBLL eqBLL = new ExamQuestionsBLL();
            DataAccess.DbConnect db = new DataAccess.DbConnect();
            if (editingExam == null) {
                // Thêm mới: Transactional
                db.executeTransaction(conn -> {
                    try {
                        int examId = examsBLL.addExam(conn, title);
                        for (Questions q : selected) {
                            eqBLL.addExamQuestion(conn, examId, q.getQuestionID());
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
            } else {
                // Sửa: cập nhật tên và danh sách câu hỏi (transactional)
                db.executeTransaction(conn -> {
                    try {
                        examsBLL.updateExam(editingExam.getExamID(), title); // updateExam does not need conn
                        eqBLL.deleteExamQuestions(editingExam.getExamID()); // deleteExamQuestions does not need conn
                        for (Questions q : selected) {
                            eqBLL.addExamQuestion(conn, editingExam.getExamID(), q.getQuestionID());
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
            JOptionPane.showMessageDialog(this, "保存が完了しました！");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "保存エラー: " + ex.getMessage());
        }
    }

    private void onPrintExam() {
        String[] options = {"PDF", "DOCX"};
        int choice = JOptionPane.showOptionDialog(this, "Chọn định dạng xuất đề:", "In đề",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == -1) return;
        String format = options[choice];
        java.util.List<Questions> selected = java.util.Collections.list(selectedQuestionsModel.elements());
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một câu hỏi để in đề.");
            return;
        }
        if ("DOCX".equalsIgnoreCase(format)) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn thư mục lưu file DOCX");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection != JFileChooser.APPROVE_OPTION) return;
            String dir = fileChooser.getSelectedFile().getAbsolutePath();
            String examDocxFile = Paths.get(dir, titleField.getText().trim() + "_de.docx").toString();
            String answerDocxFile = Paths.get(dir, titleField.getText().trim() + "_dapan.docx").toString();
            try {
                exportExamAndAnswersToDocx(titleField.getText().trim(), selected, examDocxFile, answerDocxFile);
                JOptionPane.showMessageDialog(this, "Xuất file DOCX thành công!\nĐề: " + examDocxFile + "\nĐáp án: " + answerDocxFile);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file DOCX: " + ex.getMessage());
            }
        } else {
            try {
                exportExamAndAnswers(titleField.getText().trim(), selected, format);
                JOptionPane.showMessageDialog(this, "Xuất file PDF thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file PDF: " + ex.getMessage());
            }
        }
    }

    // Khung hàm xuất file đề và đáp án
    private void exportExamAndAnswers(String examTitle, java.util.List<Questions> questions, String format) throws Exception {
        if (!"PDF".equalsIgnoreCase(format)) {
            JOptionPane.showMessageDialog(this, "Chỉ hỗ trợ xuất PDF ở phiên bản này.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn thư mục lưu file đề thi");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;
        String dir = fileChooser.getSelectedFile().getAbsolutePath();
        PdfFont font;
        try {
            String notoFontPath = "lib/NotoSansCJKjp-Regular.otf";
            String dejaVuFontPath = "lib/DejaVuSans.ttf";
            if (new java.io.File(notoFontPath).exists()) {
                font = PdfFontFactory.createFont(notoFontPath, PdfEncodings.IDENTITY_H, EmbeddingStrategy.FORCE_EMBEDDED);
            } else if (new java.io.File(dejaVuFontPath).exists()) {
                font = PdfFontFactory.createFont(dejaVuFontPath, PdfEncodings.IDENTITY_H, EmbeddingStrategy.FORCE_EMBEDDED);
            } else {
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            }
        } catch (Exception e) {
            throw new Exception("Không thể load font PDF: " + e.getMessage());
        }
        // DEBUG: Show which font is actually used
        JOptionPane.showMessageDialog(this, "Font used for PDF: " + font.getFontProgram().toString());
        // Lấy toàn bộ đáp án
        AnswersBLL answersBLL = new AnswersBLL();
        List<List<Object>> allAnswersRaw = answersBLL.getAnswers();
        List<Answers> allAnswers = new java.util.ArrayList<>();
        for (List<Object> row : allAnswersRaw) {
            Answers a = new Answers();
            a.setAnswersID((Integer) row.get(0));
            a.setQuestionID((Integer) row.get(1));
            a.setContent((String) row.get(2));
            Object isCorrectObj = row.get(3);
            boolean isCorrect = false;
            if (isCorrectObj instanceof Boolean) {
                isCorrect = (Boolean) isCorrectObj;
            } else if (isCorrectObj instanceof Number) {
                isCorrect = ((Number) isCorrectObj).intValue() != 0;
            } else if (isCorrectObj != null) {
                isCorrect = Boolean.parseBoolean(isCorrectObj.toString());
            }
            a.setCorrect(isCorrect);
            allAnswers.add(a);
        }
        // Chia câu hỏi thành 2 phần: nghe và thường
        List<Questions> normalQuestions = new ArrayList<>();
        List<Questions> ngheQuestions = new ArrayList<>();
        for (Questions q : questions) {
            if (q.getType() != null && q.getType().toLowerCase().contains("nghe")) {
                ngheQuestions.add(q);
            } else {
                normalQuestions.add(q);
            }
        }
        // 1. Xuất file đề
        String examFile = Paths.get(dir, examTitle + "_de.pdf").toString();
        // DEBUG: Show output file path
        JOptionPane.showMessageDialog(this, "PDF will be saved to: " + examFile);
        try (PdfWriter writer = new PdfWriter(examFile);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf)) {
            doc.setFont(font);
            doc.add(new Paragraph("ĐỀ THI - " + examTitle));
            // Part 1: Câu hỏi bình thường
            doc.add(new Paragraph("\nPart 1: Câu hỏi bình thường").setFontSize(14).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLACK));
            int idx = 1;
            for (Questions q : normalQuestions) {
                String content = q.getContent() != null ? q.getContent() : "(Không có nội dung)";
                doc.add(new Paragraph(idx + ". " + content));
                List<Answers> answers = allAnswers.stream().filter(a -> a.getQuestionID() == q.getQuestionID()).toList();
                char ansChar = 'A';
                for (Answers ans : answers) {
                    doc.add(new Paragraph("    " + ansChar + ". " + ans.getContent()));
                    ansChar++;
                }
                idx++;
            }
            // Part 2: Câu hỏi nghe
            doc.add(new Paragraph("\nPart 2: Câu hỏi nghe").setFontSize(14).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLACK));
            idx = 1;
            for (Questions q : ngheQuestions) {
                String content = q.getContent() != null ? q.getContent() : "(Không có nội dung)";
                doc.add(new Paragraph(idx + ". " + content));
                // Nếu có audio, chỉ lấy tên file
                if (q.getSoundURL() != null && !q.getSoundURL().isEmpty()) {
                    String audioFileName = new java.io.File(q.getSoundURL()).getName();
                    doc.add(new Paragraph("[Audio: ./" + audioFileName + "]").setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLUE).setFontSize(12));
                }
                List<Answers> answers = allAnswers.stream().filter(a -> a.getQuestionID() == q.getQuestionID()).toList();
                char ansChar = 'A';
                for (Answers ans : answers) {
                    doc.add(new Paragraph("    " + ansChar + ". " + ans.getContent()));
                    ansChar++;
                }
                idx++;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi ghi file đề: " + ex.getMessage());
            throw ex;
        }
        // 2. Xuất file đáp án
        String answerFile = Paths.get(dir, examTitle + "_dapan.pdf").toString();
        try (PdfWriter writerAns = new PdfWriter(answerFile);
             PdfDocument pdfAns = new PdfDocument(writerAns);
             Document docAns = new Document(pdfAns)) {
            docAns.setFont(font);
            docAns.add(new Paragraph("ĐÁP ÁN - " + examTitle));
            // Part 1 đáp án
            docAns.add(new Paragraph("\nPart 1: Câu hỏi bình thường").setFontSize(14).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLACK));
            int idx = 1;
            for (Questions q : normalQuestions) {
                docAns.add(new Paragraph(idx + ". " + q.getContent()));
                List<Answers> answers = allAnswers.stream().filter(a -> a.getQuestionID() == q.getQuestionID()).toList();
                char ansChar = 'A';
                for (Answers ans : answers) {
                    if (ans.isCorrect()) {
                        docAns.add(new Paragraph("    " + ansChar + ". " + ans.getContent() + " (Đúng)"));
                    }
                    ansChar++;
                }
                idx++;
            }
            // Part 2 đáp án
            docAns.add(new Paragraph("\nPart 2: Câu hỏi nghe").setFontSize(14).setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLACK));
            idx = 1;
            for (Questions q : ngheQuestions) {
                docAns.add(new Paragraph(idx + ". " + q.getContent()));
                List<Answers> answers = allAnswers.stream().filter(a -> a.getQuestionID() == q.getQuestionID()).toList();
                char ansChar = 'A';
                for (Answers ans : answers) {
                    if (ans.isCorrect()) {
                        docAns.add(new Paragraph("    " + ansChar + ". " + ans.getContent() + " (Đúng)"));
                    }
                    ansChar++;
                }
                idx++;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi ghi file đáp án: " + ex.getMessage());
            throw ex;
        }
    }

    // Thêm phương thức xuất DOCX
    public void exportExamAndAnswersToDocx(String examTitle, List<Questions> questions, String examDocxFile, String answerDocxFile) throws Exception {
        // Lấy toàn bộ đáp án và chia câu hỏi thành 2 phần ở ngoài try-with-resources để dùng cho cả hai file
        AnswersBLL answersBLL = new AnswersBLL();
        List<List<Object>> allAnswersRaw = answersBLL.getAnswers();
        List<Answers> allAnswers = new java.util.ArrayList<>();
        for (List<Object> row : allAnswersRaw) {
            Answers a = new Answers();
            a.setAnswersID((Integer) row.get(0));
            a.setQuestionID((Integer) row.get(1));
            a.setContent((String) row.get(2));
            Object isCorrectObj = row.get(3);
            boolean isCorrect = false;
            if (isCorrectObj instanceof Boolean) {
                isCorrect = (Boolean) isCorrectObj;
            } else if (isCorrectObj instanceof Number) {
                isCorrect = ((Number) isCorrectObj).intValue() != 0;
            } else if (isCorrectObj != null) {
                isCorrect = Boolean.parseBoolean(isCorrectObj.toString());
            }
            a.setCorrect(isCorrect);
            allAnswers.add(a);
        }
        List<Questions> normalQuestions = new ArrayList<>();
        List<Questions> ngheQuestions = new ArrayList<>();
        for (Questions q : questions) {
            if (q.getType() != null && q.getType().toLowerCase().contains("nghe")) {
                ngheQuestions.add(q);
            } else {
                normalQuestions.add(q);
            }
        }
        // Sử dụng Apache POI để xuất DOCX
        try (org.apache.poi.xwpf.usermodel.XWPFDocument examDoc = new org.apache.poi.xwpf.usermodel.XWPFDocument()) {
            org.apache.poi.xwpf.usermodel.XWPFParagraph titlePara = examDoc.createParagraph();
            org.apache.poi.xwpf.usermodel.XWPFRun run = titlePara.createRun();
            run.setText("ĐỀ THI - " + examTitle);
            run.setBold(true);
            run.setFontSize(16);
            // Part 1: Câu hỏi bình thường
            org.apache.poi.xwpf.usermodel.XWPFParagraph part1 = examDoc.createParagraph();
            org.apache.poi.xwpf.usermodel.XWPFRun part1Run = part1.createRun();
            part1Run.setText("\nPart 1: Câu hỏi bình thường");
            part1Run.setBold(true);
            int idx = 1;
            for (Questions q : normalQuestions) {
                org.apache.poi.xwpf.usermodel.XWPFParagraph qPara = examDoc.createParagraph();
                org.apache.poi.xwpf.usermodel.XWPFRun qRun = qPara.createRun();
                String content = q.getContent() != null ? q.getContent() : "(Không có nội dung)";
                qRun.setText(idx + ". " + content);
                List<Answers> answers = allAnswers.stream().filter(a -> a.getQuestionID() == q.getQuestionID()).toList();
                char ansChar = 'A';
                for (Answers ans : answers) {
                    org.apache.poi.xwpf.usermodel.XWPFParagraph ansPara = examDoc.createParagraph();
                    org.apache.poi.xwpf.usermodel.XWPFRun ansRun = ansPara.createRun();
                    ansRun.setText("    " + ansChar + ". " + ans.getContent());
                    ansChar++;
                }
                idx++;
            }
            // Part 2: Câu hỏi nghe
            org.apache.poi.xwpf.usermodel.XWPFParagraph part2 = examDoc.createParagraph();
            org.apache.poi.xwpf.usermodel.XWPFRun part2Run = part2.createRun();
            part2Run.setText("\nPart 2: Câu hỏi nghe");
            part2Run.setBold(true);
            idx = 1;
            for (Questions q : ngheQuestions) {
                org.apache.poi.xwpf.usermodel.XWPFParagraph qPara = examDoc.createParagraph();
                org.apache.poi.xwpf.usermodel.XWPFRun qRun = qPara.createRun();
                String content = q.getContent() != null ? q.getContent() : "(Không có nội dung)";
                qRun.setText(idx + ". " + content);
                if (q.getSoundURL() != null && !q.getSoundURL().isEmpty()) {
                    org.apache.poi.xwpf.usermodel.XWPFParagraph audioPara = examDoc.createParagraph();
                    org.apache.poi.xwpf.usermodel.XWPFRun audioRun = audioPara.createRun();
                    String audioFileName = new java.io.File(q.getSoundURL()).getName();
                    audioRun.setText("[Audio: ./" + audioFileName + "]");
                    audioRun.setColor("0000FF");
                }
                List<Answers> answers = allAnswers.stream().filter(a -> a.getQuestionID() == q.getQuestionID()).toList();
                char ansChar = 'A';
                for (Answers ans : answers) {
                    org.apache.poi.xwpf.usermodel.XWPFParagraph ansPara = examDoc.createParagraph();
                    org.apache.poi.xwpf.usermodel.XWPFRun ansRun = ansPara.createRun();
                    ansRun.setText("    " + ansChar + ". " + ans.getContent());
                    ansChar++;
                }
                idx++;
            }
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(examDocxFile)) {
                examDoc.write(out);
            }
        }
        // Đáp án DOCX
        try (org.apache.poi.xwpf.usermodel.XWPFDocument ansDoc = new org.apache.poi.xwpf.usermodel.XWPFDocument()) {
            org.apache.poi.xwpf.usermodel.XWPFParagraph ansTitle = ansDoc.createParagraph();
            org.apache.poi.xwpf.usermodel.XWPFRun ansTitleRun = ansTitle.createRun();
            ansTitleRun.setText("ĐÁP ÁN - " + examTitle);
            ansTitleRun.setBold(true);
            ansTitleRun.setFontSize(16);
            // Part 1 đáp án
            org.apache.poi.xwpf.usermodel.XWPFParagraph ansPart1 = ansDoc.createParagraph();
            org.apache.poi.xwpf.usermodel.XWPFRun ansPart1Run = ansPart1.createRun();
            ansPart1Run.setText("\nPart 1: Câu hỏi bình thường");
            ansPart1Run.setBold(true);
            int idx = 1;
            for (Questions q : normalQuestions) {
                org.apache.poi.xwpf.usermodel.XWPFParagraph qPara = ansDoc.createParagraph();
                org.apache.poi.xwpf.usermodel.XWPFRun qRun = qPara.createRun();
                qRun.setText(idx + ". " + q.getContent());
                List<Answers> answers = allAnswers.stream().filter(a -> a.getQuestionID() == q.getQuestionID()).toList();
                char ansChar = 'A';
                for (Answers ans : answers) {
                    if (ans.isCorrect()) {
                        org.apache.poi.xwpf.usermodel.XWPFParagraph ansPara = ansDoc.createParagraph();
                        org.apache.poi.xwpf.usermodel.XWPFRun ansRun = ansPara.createRun();
                        ansRun.setText("    " + ansChar + ". " + ans.getContent() + " (Đúng)");
                    }
                    ansChar++;
                }
                idx++;
            }
            // Part 2 đáp án
            org.apache.poi.xwpf.usermodel.XWPFParagraph ansPart2 = ansDoc.createParagraph();
            org.apache.poi.xwpf.usermodel.XWPFRun ansPart2Run = ansPart2.createRun();
            ansPart2Run.setText("\nPart 2: Câu hỏi nghe");
            ansPart2Run.setBold(true);
            idx = 1;
            for (Questions q : ngheQuestions) {
                org.apache.poi.xwpf.usermodel.XWPFParagraph qPara = ansDoc.createParagraph();
                org.apache.poi.xwpf.usermodel.XWPFRun qRun = qPara.createRun();
                qRun.setText(idx + ". " + q.getContent());
                List<Answers> answers = allAnswers.stream().filter(a -> a.getQuestionID() == q.getQuestionID()).toList();
                char ansChar = 'A';
                for (Answers ans : answers) {
                    if (ans.isCorrect()) {
                        org.apache.poi.xwpf.usermodel.XWPFParagraph ansPara = ansDoc.createParagraph();
                        org.apache.poi.xwpf.usermodel.XWPFRun ansRun = ansPara.createRun();
                        ansRun.setText("    " + ansChar + ". " + ans.getContent() + " (Đúng)");
                    }
                    ansChar++;
                }
                idx++;
            }
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(answerDocxFile)) {
                ansDoc.write(out);
            }
        }
    }
}
