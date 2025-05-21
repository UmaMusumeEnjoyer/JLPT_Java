package GUI;

import BussinessLogic.ExamsBLL;
import BussinessLogic.AnswersBLL;
import ViewModels.ExamQuestionsViewModel;
import ViewModels.ExamWithQuestionsVM;
import DataAccess.DTO.Exams;
import DataAccess.DTO.Questions;
import DataAccess.DTO.Answers;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExamKanri extends JFrame {
    private DefaultListModel<ExamWithQuestionsVM> examListModel;
    private JList<ExamWithQuestionsVM> examJList;
    private JTextArea examDetailArea;
    private List<ExamWithQuestionsVM> originalExams;
    private List<ExamWithQuestionsVM> filteredExams = new ArrayList<>();
    private javax.swing.table.DefaultTableModel tableModel;

    public ExamKanri() {
        setTitle("試験管理");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        originalExams = loadExams();

        JPanel leftPanel = new JPanel(new BorderLayout());
        JButton btnAdd = new JButton("追加"); // Thêm
        JButton btnEdit = new JButton("編集"); // Sửa
        JButton btnDelete = new JButton("削除"); // Xóa
        JButton btnPrint = new JButton("印刷"); // In đề
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnPrint);

        String[] columnNames = {"試験名", "問題数"};
        tableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable examTable = new JTable(tableModel);
        examTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(examTable);

        filteredExams = new ArrayList<>(originalExams);
        updateExamTable(filteredExams, tableModel);

        examTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < filteredExams.size()) {
                ExamWithQuestionsVM selected = filteredExams.get(selectedRow);
                StringBuilder sb = new StringBuilder();
                sb.append("ID: ").append(selected.getExam().getExamID()).append("\n");
                sb.append("試験名: ").append(selected.getExam().getTitle()).append("\n");
                sb.append("問題数: ").append(selected.getQuestions().size()).append("\n");
                sb.append("【問題リスト】\n");
                for (Questions q : selected.getQuestions()) {
                    sb.append("- ").append(q.getContent()).append("\n");
                }
                examDetailArea.setText(sb.toString());
            } else {
                examDetailArea.setText("");
            }
        });

        btnAdd.addActionListener(e -> {
            AddOrEditExamDialog dialog = new AddOrEditExamDialog(this, null, originalExams);
            dialog.setVisible(true);
            // Sau khi thêm, reload lại danh sách
            originalExams = loadExams();
            filteredExams = new ArrayList<>(originalExams);
            updateExamTable(filteredExams, tableModel);
        });
        btnEdit.addActionListener(e -> {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < filteredExams.size()) {
                ExamWithQuestionsVM selected = filteredExams.get(selectedRow);
                AddOrEditExamDialog dialog = new AddOrEditExamDialog(this, selected, originalExams);
                dialog.setVisible(true);
                // Sau khi sửa, reload lại danh sách
                originalExams = loadExams();
                filteredExams = new ArrayList<>(originalExams);
                updateExamTable(filteredExams, tableModel);
            } else {
                JOptionPane.showMessageDialog(this, "編集する試験を選択してください。");
            }
        });
        btnDelete.addActionListener(e -> {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < filteredExams.size()) {
                ExamWithQuestionsVM selected = filteredExams.get(selectedRow);
                int confirm = JOptionPane.showConfirmDialog(this, "本当にこの試験を削除しますか？", "削除確認", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        ExamsBLL bll = new ExamsBLL();
                        bll.deleteExam(selected.getExam().getExamID());
                        originalExams = loadExams();
                        filteredExams = new ArrayList<>(originalExams);
                        updateExamTable(filteredExams, tableModel);
                        examDetailArea.setText("");
                        JOptionPane.showMessageDialog(this, "削除が完了しました！");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "削除エラー: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "削除する試験を選択してください。");
            }
        });
        btnPrint.addActionListener(e -> {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < filteredExams.size()) {
                ExamWithQuestionsVM selected = filteredExams.get(selectedRow);
                String[] options = {"PDF", "DOCX"};
                int choice = JOptionPane.showOptionDialog(this, "出力形式を選択してください:", "印刷",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice == -1) return;
                String format = options[choice];
                if ("DOCX".equalsIgnoreCase(format)) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Chọn thư mục lưu file DOCX");
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int userSelection = fileChooser.showSaveDialog(this);
                    if (userSelection != JFileChooser.APPROVE_OPTION) return;
                    String dir = fileChooser.getSelectedFile().getAbsolutePath();
                    String examDocxFile = Paths.get(dir, selected.getExam().getTitle().trim() + "_de.docx").toString();
                    String answerDocxFile = Paths.get(dir, selected.getExam().getTitle().trim() + "_dapan.docx").toString();
                    try {
                        // Gọi dialog ẩn để dùng static export (hoặc refactor sang static nếu cần)
                        new AddOrEditExamDialog(this, null, null)
                            .exportExamAndAnswersToDocx(selected.getExam().getTitle().trim(), selected.getQuestions(), examDocxFile, answerDocxFile);
                        JOptionPane.showMessageDialog(this, "Xuất file DOCX thành công!\nĐề: " + examDocxFile + "\nĐáp án: " + answerDocxFile);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi xuất file DOCX: " + ex.getMessage());
                    }
                } else {
                    try {
                        exportExamAndAnswers(selected.getExam().getTitle(), selected.getQuestions(), format);
                        JOptionPane.showMessageDialog(this, "ファイルの出力が完了しました！");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "ファイル出力エラー: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "印刷する試験を選択してください。");
            }
        });

        leftPanel.add(buttonPanel, BorderLayout.NORTH);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        examDetailArea = new JTextArea();
        examDetailArea.setEditable(false);
        JScrollPane detailScrollPane = new JScrollPane(examDetailArea);
        detailScrollPane.setBorder(BorderFactory.createTitledBorder("試験詳細"));
        rightPanel.add(detailScrollPane, BorderLayout.CENTER);

        getContentPane().setLayout(new GridLayout(1, 2));
        getContentPane().add(leftPanel);
        getContentPane().add(rightPanel);
    }

    private List<ExamWithQuestionsVM> loadExams() {
        try {
            ExamQuestionsViewModel vm = new ExamQuestionsViewModel();
            vm.loadData();
            return vm.getExamWithQuestionsList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    private void updateExamTable(List<ExamWithQuestionsVM> exams, javax.swing.table.DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        for (ExamWithQuestionsVM e : exams) {
            Object[] row = {
                e.getExam().getTitle(),
                e.getQuestions().size()
            };
            tableModel.addRow(row);
        }
    }

    // Khung hàm xuất file đề và đáp án
    private void exportExamAndAnswers(String examTitle, java.util.List<Questions> questions, String format) throws Exception {
        if (!"PDF".equalsIgnoreCase(format)) {
            JOptionPane.showMessageDialog(this, "現在はPDFのみ対応しています。");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存先フォルダを選択してください");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;
        String dir = fileChooser.getSelectedFile().getAbsolutePath();
        PdfFont font;
        try {
            font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        } catch (Exception e) {
            throw new Exception("PDFフォントのロードに失敗しました: " + e.getMessage());
        }
        // 1. 問題PDF
        String examFile = Paths.get(dir, examTitle + "_問題.pdf").toString();
        PdfWriter writer = new PdfWriter(examFile);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);
        doc.setFont(font);
        doc.add(new Paragraph("【試験名】" + examTitle));
        int idx = 1;
        for (Questions q : questions) {
            doc.add(new Paragraph(idx + ". " + q.getContent()));
            idx++;
        }
        doc.close();
        // 2. 解答PDF
        AnswersBLL answersBLL = new AnswersBLL();
        java.util.List<List<Object>> allAnswersRaw = answersBLL.getAnswers();
        java.util.List<Answers> allAnswers = new java.util.ArrayList<>();
        for (java.util.List<Object> row : allAnswersRaw) {
            Answers a = new Answers();
            a.setAnswersID((Integer) row.get(0));
            a.setQuestionID((Integer) row.get(1));
            a.setContent((String) row.get(2));
            a.setCorrect((Boolean) row.get(3));
            allAnswers.add(a);
        }
        String answerFile = Paths.get(dir, examTitle + "_解答.pdf").toString();
        PdfWriter writerAns = new PdfWriter(answerFile);
        PdfDocument pdfAns = new PdfDocument(writerAns);
        Document docAns = new Document(pdfAns);
        docAns.setFont(font);
        docAns.add(new Paragraph("【解答】" + examTitle));
        idx = 1;
        for (Questions q : questions) {
            java.util.List<Answers> answers = allAnswers.stream().filter(a -> a.getQuestionID() == q.getQuestionID()).collect(Collectors.toList());
            Answers correct = answers.stream().filter(Answers::isCorrect).findFirst().orElse(null);
            String ansText = correct != null ? correct.getContent() : "(正解なし)";
            docAns.add(new Paragraph(idx + ". " + ansText));
            idx++;
        }
        docAns.close();
    }
}
