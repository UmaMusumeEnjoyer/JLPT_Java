package GUI;

import BussinessLogic.ExamsBLL;
import ViewModels.ExamQuestionsViewModel;
import ViewModels.ExamWithQuestionsVM;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import DataAccess.DTO.Questions;

public class ExamKanri extends JFrame {
    private List<ExamWithQuestionsVM> originalExams;
    private List<ExamWithQuestionsVM> filteredExams = new ArrayList<>();
    private javax.swing.table.DefaultTableModel tableModel;
    private JTextArea examDetailArea;

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
            AddOrEditExamDialog dialog = new AddOrEditExamDialog(this, null);
            dialog.setVisible(true);
            // Reload the list after adding
            originalExams = loadExams();
            filteredExams = new ArrayList<>(originalExams);
            updateExamTable(filteredExams, tableModel);
        });
        btnEdit.addActionListener(e -> {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < filteredExams.size()) {
                ExamWithQuestionsVM selected = filteredExams.get(selectedRow);
                AddOrEditExamDialog dialog = new AddOrEditExamDialog(this, selected);
                dialog.setVisible(true);
                // Reload the list after editing
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
                String[] options = {"PDF", "DOC"};
                int choice = JOptionPane.showOptionDialog(this, "出力形式を選択してください:", "印刷",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice == -1) return;
                try {
                    if ("PDF".equalsIgnoreCase(options[choice])) {
                        ExamExporter.exportToPDFWithAnswerKey(this, selected.getExam().getTitle(), selected.getQuestions());
                        JOptionPane.showMessageDialog(this, "ファイルの出力が完了しました！\n(問題と解答が別ファイルで保存されました)");
                    } else if ("DOC".equalsIgnoreCase(options[choice])) {
                        ExamExporter.exportToDOCWithAnswerKey(this, selected.getExam().getTitle(), selected.getQuestions());
                        JOptionPane.showMessageDialog(this, "ファイルの出力が完了しました！\n(問題と解答が別ファイルで保存されました)");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "ファイル出力エラー: " + ex.getMessage());
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
}
