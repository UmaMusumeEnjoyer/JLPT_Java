package GUI;

import BussinessLogic.QuestionsBLL;
import ViewModels.QuestionAnswersViewModel;
import ViewModels.QuestionWithAnswersVM;
import DataAccess.DTO.Questions;
import DataAccess.DTO.Answers;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class ShitsumonKanri extends JFrame {
    private DefaultListModel<QuestionWithAnswersVM> questionListModel;
    private JList<QuestionWithAnswersVM> questionJList;
    private JComboBox<String> typeFilterCombo;
    private JComboBox<String> difficultyFilterCombo;
    private JTextArea questionDetailArea;
    private List<QuestionWithAnswersVM> originalQuestions;
    private List<QuestionWithAnswersVM> filteredQuestions = new ArrayList<>();
    private javax.swing.table.DefaultTableModel tableModel;

    public ShitsumonKanri() {
        setTitle("質問管理");
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Lấy danh sách ban đầu từ ViewModel
        originalQuestions = loadQuestions();

        // Panel trái: Danh sách và bộ lọc
        JPanel leftPanel = new JPanel(new BorderLayout());

        // Bộ lọc
        JPanel filterPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        // Sử dụng tiếng Nhật cho option
        typeFilterCombo = new JComboBox<>(new String[] {"すべて", "文法", "語彙"});
        difficultyFilterCombo = new JComboBox<>(new String[] {"すべて", "N1", "N2", "N3", "N4", "N5"});
        filterPanel.add(new JLabel("種類:"));
        filterPanel.add(typeFilterCombo);
        filterPanel.add(new JLabel("難易度:"));
        filterPanel.add(difficultyFilterCombo);

        // Nút filter và clear
        JButton btnFilter = new JButton("フィルター");
        JButton btnClear = new JButton("フィルターを解除");
        JPanel filterButtonPanel = new JPanel();
        filterButtonPanel.add(btnFilter);
        filterButtonPanel.add(btnClear);

        // Danh sách câu hỏi dưới dạng bảng
        String[] columnNames = {"質問", "レベル", "種類"};
        tableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable questionTable = new JTable(tableModel);
        questionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(questionTable);

        // Cập nhật bảng ban đầu
        filteredQuestions = new ArrayList<>(originalQuestions);
        updateQuestionTable(filteredQuestions, tableModel);

        // Sự kiện chọn dòng để hiển thị chi tiết
        questionTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = questionTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < filteredQuestions.size()) {
                QuestionWithAnswersVM selected = filteredQuestions.get(selectedRow);
                StringBuilder sb = new StringBuilder();
                sb.append("ID: ").append(selected.getQuestion().getQuestionID()).append("\n");
                sb.append("内容: ").append(selected.getQuestion().getContent()).append("\n");
                sb.append("種類: ").append(selected.getQuestion().getType()).append("\n");
                sb.append("難易度: ").append(selected.getQuestion().getLevel()).append("\n");
                sb.append("【選択肢】\n");
                if (selected.getAnswers() != null) {
                    for (DataAccess.DTO.Answers ans : selected.getAnswers()) {
                        sb.append("- ").append(ans.getContent());
                        if (ans.isCorrect()) sb.append("  (正解)");
                        sb.append("\n");
                    }
                }
                questionDetailArea.setText(sb.toString());
            } else {
                questionDetailArea.setText("");
            }
        });

        // Thêm sự kiện lọc
        btnFilter.addActionListener(e -> applyFilters());
        btnClear.addActionListener(e -> {
            typeFilterCombo.setSelectedIndex(0);
            difficultyFilterCombo.setSelectedIndex(0);
            filteredQuestions = new ArrayList<>(originalQuestions);
            updateQuestionTable(filteredQuestions, tableModel);
        });

        // Gộp panel trái
        JPanel filterWrapper = new JPanel(new BorderLayout());
        filterWrapper.add(filterPanel, BorderLayout.NORTH);
        filterWrapper.add(filterButtonPanel, BorderLayout.SOUTH);

        leftPanel.add(filterWrapper, BorderLayout.NORTH);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        // Panel phải: Chi tiết và nút chức năng
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Chi tiết câu hỏi
        questionDetailArea = new JTextArea();
        questionDetailArea.setEditable(false);
        JScrollPane detailScrollPane = new JScrollPane(questionDetailArea);
        detailScrollPane.setBorder(BorderFactory.createTitledBorder("質問詳細"));

        // Nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("追加");
        JButton btnEdit = new JButton("編集");
        JButton btnDelete = new JButton("削除");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        // Xử lý xóa câu hỏi
        btnDelete.addActionListener(e -> {
            int selectedRow = questionTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < filteredQuestions.size()) {
                QuestionWithAnswersVM selected = filteredQuestions.get(selectedRow);
                StringBuilder confirmMsg = new StringBuilder();
                confirmMsg.append("本当にこの質問を削除しますか？\n\n");
                confirmMsg.append("内容: ").append(selected.getQuestion().getContent()).append("\n");
                confirmMsg.append("種類: ").append(selected.getQuestion().getType()).append("\n");
                confirmMsg.append("レベル: ").append(selected.getQuestion().getLevel()).append("\n");
                confirmMsg.append("\n関連するすべての選択肢も削除されます！");
                int confirm = JOptionPane.showConfirmDialog(this, confirmMsg.toString(), "質問削除の確認", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        QuestionsBLL bll = new QuestionsBLL();
                        bll.deleteQuestionAndAnswers(selected.getQuestion().getQuestionID());
                        // Reload dữ liệu
                        originalQuestions = loadQuestions();
                        filteredQuestions = new ArrayList<>(originalQuestions);
                        updateQuestionTable(filteredQuestions, tableModel);
                        questionDetailArea.setText("");
                        JOptionPane.showMessageDialog(this, "削除が完了しました！");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "削除エラー: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "削除する質問を選択してください。");
            }
        });

        // Xử lý chỉnh sửa câu hỏi
        btnEdit.addActionListener(e -> {
            int selectedRow = questionTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < filteredQuestions.size()) {
                QuestionWithAnswersVM selected = filteredQuestions.get(selectedRow);
                Questions oldQ = selected.getQuestion();
                JPanel panel = new JPanel(new GridLayout(0, 1));
                JTextField contentField = new JTextField(oldQ.getContent());
                JComboBox<String> typeBox = new JComboBox<>(new String[]{"Grammar", "Vocabulary"});
                JComboBox<String> levelBox = new JComboBox<>(new String[]{"N1", "N2", "N3", "N4", "N5"});
                typeBox.setSelectedItem(oldQ.getType() != null ? oldQ.getType() : "Grammar");
                levelBox.setSelectedItem(oldQ.getLevel() != null ? oldQ.getLevel() : "N5");
                panel.add(new JLabel("Nội dung câu hỏi:"));
                panel.add(contentField);
                panel.add(new JLabel("Thể loại:"));
                panel.add(typeBox);
                panel.add(new JLabel("Cấp độ:"));
                panel.add(levelBox);
                int result = JOptionPane.showConfirmDialog(this, panel, "Chỉnh sửa câu hỏi", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String newContent = contentField.getText().trim();
                    String newType = (String) typeBox.getSelectedItem();
                    String newLevel = (String) levelBox.getSelectedItem();
                    if (!newContent.isEmpty()) {
                        try {
                            QuestionsBLL bll = new QuestionsBLL();
                            // Thêm hàm updateQuestionFull nếu chưa có
                            bll.updateQuestionFull(oldQ.getQuestionID(), newContent, newType, newLevel);
                            originalQuestions = loadQuestions();
                            filteredQuestions = new ArrayList<>(originalQuestions);
                            updateQuestionTable(filteredQuestions, tableModel);
                            JOptionPane.showMessageDialog(this, "Đã cập nhật thành công!");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một câu hỏi để chỉnh sửa.");
            }
        });

        rightPanel.add(detailScrollPane, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.NORTH);

        // Chia giao diện
        getContentPane().setLayout(new GridLayout(1, 2));
        getContentPane().add(leftPanel);
        getContentPane().add(rightPanel);
    }

    private List<QuestionWithAnswersVM> loadQuestions() {
        try {
            QuestionAnswersViewModel vm = new QuestionAnswersViewModel();
            vm.loadData();
            return vm.getQuestionAnswerList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    private void updateQuestionTable(List<QuestionWithAnswersVM> questions, javax.swing.table.DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        for (QuestionWithAnswersVM q : questions) {
            String level = q.getQuestion().getLevel();
            String type = q.getQuestion().getType();
            // Đảm bảo không null và chuẩn hóa hiển thị
            if (level == null || level.trim().isEmpty()) level = "(Không xác định)";
            if (type == null || type.trim().isEmpty()) type = "(Không xác định)";
            Object[] row = {
                q.getQuestion().getContent(),
                level,
                type
            };
            tableModel.addRow(row);
        }
    }

    private void applyFilters() {
        String type = (String) typeFilterCombo.getSelectedItem();
        String difficulty = (String) difficultyFilterCombo.getSelectedItem();
        // Map lại giá trị filter sang giá trị thực tế trong DB
        String typeValue;
        if (type.equals("すべて")) typeValue = null;
        else if (type.equals("文法")) typeValue = "Grammar";
        else if (type.equals("語彙")) typeValue = "Vocabulary";
        else typeValue = type;
        String difficultyValue = difficulty.equals("すべて") ? null : difficulty;
        filteredQuestions = originalQuestions.stream()
                .filter(q -> (typeValue == null || typeValue.equalsIgnoreCase(q.getQuestion().getType())) &&
                             (difficultyValue == null || difficultyValue.equalsIgnoreCase(q.getQuestion().getLevel())))
                .collect(Collectors.toList());
        updateQuestionTable(filteredQuestions, tableModel);
    }
}
