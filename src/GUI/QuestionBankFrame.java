package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class QuestionBankFrame extends JFrame {
    private JFrame welcomeFrame;

    public QuestionBankFrame(JFrame welcomeFrame) {
        this.welcomeFrame = welcomeFrame;
        setTitle("Ngân hàng câu hỏi");
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Đây là giao diện ngân hàng câu hỏi", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        add(label, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (welcomeFrame != null) {
                    welcomeFrame.setVisible(true);
                }
            }
        });
    }
}
