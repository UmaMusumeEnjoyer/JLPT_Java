package GUI;

import ViewModels.ExamWithQuestionsVM;
import DataAccess.DTO.Questions;
import DataAccess.DTO.Exams;
import BussinessLogic.ExamsBLL;
import BussinessLogic.ExamQuestionsBLL;
import BussinessLogic.QuestionsBLL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        try {
            exportExamAndAnswers(titleField.getText().trim(), selected, format);
            JOptionPane.showMessageDialog(this, "Xuất file thành công! (Chức năng mẫu, cần bổ sung xuất file thực tế)");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage());
        }
    }

    // Khung hàm xuất file đề và đáp án
    private void exportExamAndAnswers(String examTitle, java.util.List<Questions> questions, String format) throws Exception {
        // TODO: Thực hiện xuất file PDF/DOCX cho đề và PDF cho đáp án
        // Sử dụng iText (PDF) hoặc Apache POI (DOCX)
        // 1. Tạo file đề: chỉ in câu hỏi, không in đáp án đúng
        // 2. Tạo file đáp án: chỉ in số thứ tự và đáp án đúng
        // Gợi ý: Lặp qua questions, lấy nội dung và đáp án từ DB
    }
}
