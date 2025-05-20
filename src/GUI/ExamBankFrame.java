package GUI;

import ViewModels.ExamWithQuestionsVM;
import ViewModels.ExamQuestionsViewModel;
import DataAccess.DTO.Questions;
import DataAccess.DTO.Answers;
import DataAccess.DTO.Exams;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ExamBankFrame extends JFrame {
    private JFrame welcomeFrame;
    private DefaultListModel<String> examListModel;
    private JList<String> examList;
    private JPanel questionsPanel;
    private List<ExamWithQuestionsVM> examWithQuestionsVMs;

    // Thêm các nút chức năng
    private JButton btnAddExam;
    private JButton btnEditExam;
    private JButton btnDeleteExam;
    private JButton btnExportExam;

    public ExamBankFrame(JFrame welcomeFrame) {
        this.welcomeFrame = welcomeFrame;
        setTitle("Ngân hàng đề thi");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(350);

        // Left panel: List of exams
        examListModel = new DefaultListModel<>();
        examList = new JList<>(examListModel);
        JScrollPane examScroll = new JScrollPane(examList);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Danh sách đề thi:"), BorderLayout.NORTH);
        leftPanel.add(examScroll, BorderLayout.CENTER);

        // Right panel: Questions and answers of selected exam
        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        JScrollPane questionsScroll = new JScrollPane(questionsPanel);
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Góc trên bên phải: các nút chức năng
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAddExam = new JButton("Thêm đề thi");
        btnEditExam = new JButton("Sửa đề thi này");
        btnDeleteExam = new JButton("Xoá đề thi này");
        btnExportExam = new JButton("Xuất đề thi này");
        topRightPanel.add(btnAddExam);
        topRightPanel.add(btnEditExam);
        topRightPanel.add(btnDeleteExam);
        topRightPanel.add(btnExportExam);

        // Tiêu đề
        JLabel lblTitle = new JLabel("Chi tiết đề thi", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        // Thêm các thành phần vào rightPanel
        rightPanel.add(topRightPanel, BorderLayout.NORTH);
        rightPanel.add(lblTitle, BorderLayout.CENTER);
        rightPanel.add(questionsScroll, BorderLayout.SOUTH);

        // Sửa lại layout để tiêu đề nằm dưới các nút, trên danh sách câu hỏi
        JPanel rightTop = new JPanel(new BorderLayout());
        rightTop.add(topRightPanel, BorderLayout.NORTH);
        rightTop.add(lblTitle, BorderLayout.CENTER);
        rightPanel.add(rightTop, BorderLayout.NORTH);
        rightPanel.add(questionsScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        loadExams();

        examList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = examList.getSelectedIndex();
                showExamQuestions(idx);
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

        // Sự kiện mẫu cho các nút (bạn có thể thay đổi/hoàn thiện logic)
        btnAddExam.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng thêm đề thi chưa được cài đặt.");
        });
        btnEditExam.addActionListener(e -> {
            int idx = examList.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi để sửa.");
                return;
            }
            JOptionPane.showMessageDialog(this, "Chức năng sửa đề thi chưa được cài đặt.");
        });
        btnDeleteExam.addActionListener(e -> {
            int idx = examList.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi để xoá.");
                return;
            }
            JOptionPane.showMessageDialog(this, "Chức năng xoá đề thi chưa được cài đặt.");
        });
        btnExportExam.addActionListener(e -> {
            int idx = examList.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi để xuất.");
                return;
            }
            JOptionPane.showMessageDialog(this, "Chức năng xuất đề thi chưa được cài đặt.");
        });
    }

    private void loadExams() {
        try {
            ExamQuestionsViewModel vm = new ExamQuestionsViewModel();
            vm.loadData();
            examWithQuestionsVMs = vm.getExamWithQuestionsList();
            examListModel.clear();
            for (ExamWithQuestionsVM examVM : examWithQuestionsVMs) {
                Exams exam = examVM.getExam();
                examListModel.addElement("[" + exam.getExamID() + "] " + exam.getTitle());
            }
            if (!examWithQuestionsVMs.isEmpty()) {
                examList.setSelectedIndex(0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu đề thi: " + ex.getMessage());
        }
    }

    private void showExamQuestions(int examIdx) {
        questionsPanel.removeAll();
        if (examIdx >= 0 && examIdx < examWithQuestionsVMs.size()) {
            ExamWithQuestionsVM examVM = examWithQuestionsVMs.get(examIdx);
            List<Questions> questions = examVM.getQuestions();
            List<List<Answers>> answersList = examVM.getAnswersList();

            for (int i = 0; i < questions.size(); i++) {
                Questions q = questions.get(i);
                JPanel qPanel = new JPanel();
                qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
                qPanel.setBorder(BorderFactory.createTitledBorder("Câu hỏi " + (i + 1) + ": " + q.getContent()));

                // Hiển thị các đáp án cho câu hỏi này
                if (i < answersList.size()) {
                    List<Answers> answers = answersList.get(i);
                    for (Answers a : answers) {
                        JLabel ansLabel = new JLabel((a.isCorrect() ? "[Đúng] " : "") + a.getContent());
                        qPanel.add(ansLabel);
                    }
                }
                questionsPanel.add(qPanel);
                questionsPanel.add(Box.createVerticalStrut(10));
            }
        }
        questionsPanel.revalidate();
        questionsPanel.repaint();
    }
}
