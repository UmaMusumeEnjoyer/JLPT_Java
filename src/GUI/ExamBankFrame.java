package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ExamBankFrame extends JFrame {
    private JFrame welcomeFrame;

    public ExamBankFrame(JFrame welcomeFrame) {
        this.welcomeFrame = welcomeFrame;
        setTitle("Ngân hàng đề thi");
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Đây là giao diện ngân hàng đề thi", SwingConstants.CENTER);
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
