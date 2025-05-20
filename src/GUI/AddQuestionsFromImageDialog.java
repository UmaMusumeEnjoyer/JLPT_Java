package GUI;

import ai.JsonQuestionParser;
import ai.ExtractQuestionsFromImageByExe;
import BussinessLogic.QuestionsBLL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddQuestionsFromImageDialog extends JDialog {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnSelectImage, btnSave;
    private JLabel statusLabel;
    private List<JsonQuestionParser.Question> questions = new ArrayList<>();
    private final String[] LEVELS = {"N1", "N2", "N3", "N4", "N5"};
    private final String[] TYPES = {"文法", "語彙"};

    public AddQuestionsFromImageDialog(Frame parent) {
        super(parent, "画像から質問を追加", true);
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnSelectImage = new JButton("画像を選択");
        btnSave = new JButton("保存");
        btnSave.setEnabled(false);
        statusLabel = new JLabel("画像を選択してください");
        topPanel.add(btnSelectImage);
        topPanel.add(btnSave);
        topPanel.add(statusLabel);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"質問", "A", "B", "C", "D", "レベル", "種類"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Cho phép sửa cột レベル và 種類
                return col == 5 || col == 6;
            }
        };
        table = new JTable(tableModel);
        // Set combobox editor cho レベル và 種類
        table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JComboBox<>(LEVELS)));
        table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JComboBox<>(TYPES)));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        btnSelectImage.addActionListener(this::onSelectImage);
        btnSave.addActionListener(this::onSave);
    }

    private void onSelectImage(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("画像ファイルを選択");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            statusLabel.setText("AIが画像を解析中です。お待ちください...");
            btnSelectImage.setEnabled(false);
            btnSave.setEnabled(false);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    try {
                        String json = ExtractQuestionsFromImageByExe.extractQuestionsFromImageByExe(file.getAbsolutePath());
                        questions = JsonQuestionParser.parseQuestions(json);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(AddQuestionsFromImageDialog.this, "画像解析エラー: " + ex.getMessage());
                        questions = new ArrayList<>();
                    }
                    return null;
                }
                @Override
                protected void done() {
                    btnSelectImage.setEnabled(true);
                    tableModel.setRowCount(0);
                    for (JsonQuestionParser.Question q : questions) {
                        String[] row = new String[7];
                        row[0] = q.questionText;
                        for (int i = 0; i < 4 && i < q.options.size(); i++) {
                            row[i+1] = q.options.get(i).text;
                        }
                        row[5] = LEVELS[0];
                        row[6] = TYPES[0];
                        tableModel.addRow(row);
                    }
                    statusLabel.setText("AIの解析が完了しました。レベル・種類を選択して保存してください。");
                    btnSave.setEnabled(!questions.isEmpty());
                }
            };
            worker.execute();
        }
    }

    private void onSave(ActionEvent e) {
        try {
            QuestionsBLL bll = new QuestionsBLL();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String content = (String) tableModel.getValueAt(i, 0);
                String level = (String) tableModel.getValueAt(i, 5);
                String typeJp = (String) tableModel.getValueAt(i, 6);
                String type = typeJp; // Lưu đúng phân loại tiếng Nhật vào DB
                List<String> answers = new ArrayList<>();
                for (int j = 1; j <= 4; j++) {
                    answers.add((String) tableModel.getValueAt(i, j));
                }
                String suggested = (questions != null && i < questions.size()) ? questions.get(i).suggestedAnswer : null;
                bll.addQuestionWithAnswers(content, type, level, answers, suggested);
            }
            JOptionPane.showMessageDialog(this, "保存が完了しました！");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "保存エラー: " + ex.getMessage());
        }
    }

    private String getLabel(int idx) {
        switch (idx) {
            case 1: return "A";
            case 2: return "B";
            case 3: return "C";
            case 4: return "D";
        }
        return "";
    }
}
