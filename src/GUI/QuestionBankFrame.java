package GUI;

import ViewModels.QuestionAnswersViewModel;
import ViewModels.QuestionWithAnswersVM;
import DataAccess.DTO.Questions;
import DataAccess.DTO.Answers;
import BussinessLogic.QuestionsBLL;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuestionBankFrame extends JFrame {
    private JFrame welcomeFrame;
    private JList<String> questionList;
    private DefaultListModel<String> questionListModel;
    private JList<String> answerList;
    private DefaultListModel<String> answerListModel;
    private JButton btnAddQuestion;
    private JButton btnEditQuestion;
    private JButton btnDeleteQuestion;
    private List<QuestionWithAnswersVM> questionAnswerVMs;

    public QuestionBankFrame(JFrame welcomeFrame) {
        this.welcomeFrame = welcomeFrame;
        setTitle("Ngân hàng câu hỏi");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(350);

        // Left panel: List of questions
        questionListModel = new DefaultListModel<>();
        questionList = new JList<>(questionListModel);
        JScrollPane questionScroll = new JScrollPane(questionList);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Danh sách câu hỏi:"), BorderLayout.NORTH);
        leftPanel.add(questionScroll, BorderLayout.CENTER);

        // Right panel: Answers and buttons
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Top right: Add, Edit, Delete buttons
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAddQuestion = new JButton("Thêm câu hỏi");
        btnEditQuestion = new JButton("Sửa câu hỏi");
        btnDeleteQuestion = new JButton("Xoá câu hỏi");
        topRightPanel.add(btnAddQuestion);
        topRightPanel.add(btnEditQuestion);
        topRightPanel.add(btnDeleteQuestion);
        rightPanel.add(topRightPanel, BorderLayout.NORTH);

        // Center right: List of answers
        answerListModel = new DefaultListModel<>();
        answerList = new JList<>(answerListModel);
        JScrollPane answerScroll = new JScrollPane(answerList);
        JPanel answerPanel = new JPanel(new BorderLayout());
        answerPanel.add(new JLabel("Danh sách câu trả lời:"), BorderLayout.NORTH);
        answerPanel.add(answerScroll, BorderLayout.CENTER);
        rightPanel.add(answerPanel, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        loadQuestions();

        questionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = questionList.getSelectedIndex();
                showAnswers(idx);
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (welcomeFrame != null) {
                    welcomeFrame.setVisible(true);
                }
            }
        });

        btnAddQuestion.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng thêm câu hỏi chưa được cài đặt.");
        });

        btnEditQuestion.addActionListener(e -> {
            int idx = questionList.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn câu hỏi để sửa.");
                return;
            }
            Questions q = questionAnswerVMs.get(idx).getQuestion();
            String newContent = JOptionPane.showInputDialog(this, "Sửa nội dung câu hỏi:", q.getContent());
            if (newContent != null && !newContent.trim().isEmpty()) {
                try {
                    QuestionsBLL bll = new QuestionsBLL();
                    bll.updateQuestion(q.getQuestionID(), newContent);
                    loadQuestions();
                    questionList.setSelectedIndex(idx);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi sửa câu hỏi: " + ex.getMessage());
                }
            }
        });

        btnDeleteQuestion.addActionListener(e -> {
            int idx = questionList.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn câu hỏi để xoá.");
                return;
            }
            Questions q = questionAnswerVMs.get(idx).getQuestion();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xoá câu hỏi này? Tất cả đáp án của nó cũng sẽ bị xoá!", "Xác nhận xoá", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    QuestionsBLL bll = new QuestionsBLL();
                    bll.deleteQuestionAndAnswers(q.getQuestionID());
                    loadQuestions();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xoá câu hỏi: " + ex.getMessage());
                }
            }
        });
    }

    private void loadQuestions() {
        try {
            QuestionAnswersViewModel vm = new QuestionAnswersViewModel();
            vm.loadData();
            questionAnswerVMs = vm.getQuestionAnswerList();
            questionListModel.clear();
            for (QuestionWithAnswersVM qvm : questionAnswerVMs) {
                Questions q = qvm.getQuestion();
                questionListModel.addElement("[" + q.getQuestionID() + "] " + q.getContent());
            }
            if (!questionAnswerVMs.isEmpty()) {
                questionList.setSelectedIndex(0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu câu hỏi: " + ex.getMessage());
        }
    }

    private void showAnswers(int questionIdx) {
        answerListModel.clear();
        if (questionIdx >= 0 && questionIdx < questionAnswerVMs.size()) {
            List<Answers> answers = questionAnswerVMs.get(questionIdx).getAnswers();
            for (Answers a : answers) {
                String label = (a.isCorrect() ? "[Đúng] " : "") + a.getContent();
                answerListModel.addElement(label);
            }
        }
    }
}
