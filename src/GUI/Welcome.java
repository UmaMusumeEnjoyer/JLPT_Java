// File: src/Welcome.java
package GUI;

import javax.swing.*;
import java.awt.*;

public class Welcome extends JFrame {
    public JButton btnQuestionBank;
    public JButton btnExamBank;
    public JButton btnExit;
    public JLabel creditLabel;
    public JPanel mainPanel;

    public Welcome() {
        initComponents();
        // Sự kiện cho từng nút:
        // 1. btnQuestionBank: Khi nhấn sẽ mở giao diện quản lý/ngân hàng câu hỏi.
        btnQuestionBank.addActionListener(e -> {
            QuestionBankFrame qbf = new QuestionBankFrame(this);
            qbf.setVisible(true);
            this.setVisible(false);
        });

        // 2. btnExamBank: Khi nhấn sẽ mở giao diện quản lý/ngân hàng đề thi.
        btnExamBank.addActionListener(e -> {
            ExamBankFrame ebf = new ExamBankFrame(this);
            ebf.setVisible(true);
            this.setVisible(false);
        });

        // 3. btnExit: Khi nhấn sẽ thoát chương trình.
        btnExit.addActionListener(e -> {
            System.exit(0);
        });
    }

    private void initComponents() {
        mainPanel = new JPanel();
        btnQuestionBank = new JButton("Ngân hàng câu hỏi");
        btnExamBank = new JButton("Ngân hàng đề thi");
        btnExit = new JButton("Thoát chương trình");
        creditLabel = new JLabel("TranQuangDung-102230011", SwingConstants.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Main Menu");
        setSize(400, 300);
        setLocationRelativeTo(null);

        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;

        gbc.gridy = 0;
        mainPanel.add(btnQuestionBank, gbc);
        gbc.gridy = 1;
        mainPanel.add(btnExamBank, gbc);
        gbc.gridy = 2;
        mainPanel.add(btnExit, gbc);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(creditLabel, BorderLayout.SOUTH);
    }
}
