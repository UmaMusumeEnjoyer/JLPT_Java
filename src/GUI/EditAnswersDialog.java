package GUI;

import DataAccess.DTO.Answers;
import BussinessLogic.QuestionsBLL;
import BussinessLogic.AnswersBLL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class EditAnswersDialog extends JDialog {
    private List<Answers> answers;
    private List<JTextField> answerFields = new ArrayList<>();
    private List<JRadioButton> correctButtons = new ArrayList<>();
    private JButton btnSave;
    private ButtonGroup group;
    private int questionId;

    public EditAnswersDialog(Frame parent, int questionId, List<Answers> answers) {
        super(parent, "選択肢編集", true);
        this.answers = new ArrayList<>(answers);
        this.questionId = questionId;
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(answers.size(), 3, 5, 5));
        group = new ButtonGroup();
        for (int i = 0; i < answers.size(); i++) {
            JTextField field = new JTextField(answers.get(i).getContent());
            answerFields.add(field);
            JRadioButton radio = new JRadioButton();
            correctButtons.add(radio);
            group.add(radio);
            if (answers.get(i).isCorrect()) radio.setSelected(true);
            mainPanel.add(new JLabel("選択肢" + (char)('A'+i) + ":"));
            mainPanel.add(field);
            mainPanel.add(radio);
        }
        add(mainPanel, BorderLayout.CENTER);

        btnSave = new JButton("保存");
        btnSave.addActionListener(this::onSave);
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnSave);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void onSave(ActionEvent e) {
        try {
            AnswersBLL answersBLL = new AnswersBLL();
            QuestionsBLL bll = new QuestionsBLL();
            int correctIdx = -1;
            for (int i = 0; i < correctButtons.size(); i++) {
                if (correctButtons.get(i).isSelected()) {
                    correctIdx = i;
                    break;
                }
            }
            for (int i = 0; i < answers.size(); i++) {
                Answers ans = answers.get(i);
                String newContent = answerFields.get(i).getText().trim();
                boolean isCorrect = (i == correctIdx);
                answersBLL.updateAnswer(ans.getAnswersID(), newContent, isCorrect);
            }
            JOptionPane.showMessageDialog(this, "保存が完了しました！");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "保存エラー: " + ex.getMessage());
        }
    }
}
