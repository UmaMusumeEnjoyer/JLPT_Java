package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Welrucomucomu extends JFrame {

    public Welrucomucomu(String username) {

        username = "チャン・クアン・ズン";
        setTitle("クイズ管理システム"); // Hệ thống quản lý quiz
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center window

        // Tạo các nút
        JButton btnManageExams = new JButton("試験管理"); // Quản lý đề thi
        JButton btnManageQuestions = new JButton("質問管理"); // Quản lý câu hỏi
        JButton btnExit = new JButton("終了"); // Thoát chương trình

        // Xử lý sự kiện nút "終了"
        btnExit.addActionListener(e -> System.exit(0));
        // Xử lý sự kiện nút "試験管理"
        btnManageExams.addActionListener(e -> {
            // Mở cửa sổ quản lý đề thi
            ExamKanri shikenKanri = new ExamKanri();
            shikenKanri.setVisible(true);
        });
        // Xử lý sự kiện nút "質問管理"
        btnManageQuestions.addActionListener(e -> {
            // Mở cửa sổ quản lý câu hỏi
            ShitsumonKanri shitsumonKanri = new ShitsumonKanri();
            shitsumonKanri.setVisible(true);
        });

        // Tạo panel chính và đặt layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1, 10, 10));
        mainPanel.add(btnManageExams);
        mainPanel.add(btnManageQuestions);
        mainPanel.add(btnExit);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 10, 50));

        // Tạo watermark (label tên người dùng)
        JLabel watermark = new JLabel("ユーザー名: " + username, SwingConstants.CENTER);
        watermark.setFont(new Font("SansSerif", Font.ITALIC, 12));
        watermark.setForeground(Color.GRAY);

        // Thêm vào giao diện chính
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(watermark, BorderLayout.SOUTH);
    }


}

