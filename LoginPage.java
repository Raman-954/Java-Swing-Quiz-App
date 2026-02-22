import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {
    private final Color GRAD_START = new Color(46, 49, 146);
    private final Color GRAD_END = new Color(102, 45, 145);
    private final Color TEXT_DARK = new Color(40, 40, 80);
    public LoginPage() {
        setTitle("Login - EduQuiz Pro");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);
        RoundedPanel card = new RoundedPanel(30, Color.WHITE);
        card.setPreferredSize(new Dimension(400, 520));
        card.setLayout(null);

ImageIcon icon = new ImageIcon("login.png");
Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
icon = new ImageIcon(img);
        JLabel lblEmoji = new JLabel(icon, SwingConstants.CENTER);
        lblEmoji.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        lblEmoji.setBounds(0, 30, 400, 60);
        card.add(lblEmoji);

        JLabel lblTitle = new JLabel("Welcome Back", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBounds(0, 90, 400, 40);
        card.add(lblTitle);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(TEXT_DARK);
        lblUser.setBounds(50, 150, 100, 20);
        card.add(lblUser);

        RoundTextField txtUser = new RoundTextField(15);
        txtUser.setBounds(50, 175, 300, 40);
        card.add(txtUser);
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(TEXT_DARK);
        lblPass.setBounds(50, 230, 100, 20);
        card.add(lblPass);

        RoundPasswordField txtPass = new RoundPasswordField(15);
        txtPass.setBounds(50, 255, 300, 40);
        card.add(txtPass);

        JLabel lblMsg = new JLabel("", SwingConstants.CENTER);
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMsg.setBounds(50, 310, 300, 20);
        card.add(lblMsg);

        ModernButton btnLogin = new ModernButton("LOGIN", GRAD_START);
        btnLogin.setBounds(50, 340, 300, 45);
        card.add(btnLogin);
        JButton btnSignup = new JButton("<html>New User? <u>Create Account</u></html>");
        btnSignup.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSignup.setForeground(TEXT_DARK);
        btnSignup.setContentAreaFilled(false);
        btnSignup.setBorderPainted(false);
        btnSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSignup.setBounds(0, 400, 400, 30);
        btnSignup.addActionListener(e -> {
            dispose();
            new SignupPage().setVisible(true);
        });
        card.add(btnSignup);
        btnLogin.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());

            if (UserManager.validate(user, pass)) {
                dispose();
                new DashboardPage(user).setVisible(true);
            } else {
                lblMsg.setForeground(new Color(220, 53, 69));
                lblMsg.setText("Invalid username or password!");
            }
        });

        mainPanel.add(card);
    }
    class GradientPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, GRAD_START, 0, getHeight(), GRAD_END));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    class RoundedPanel extends JPanel {
        private int r; Color c;
        RoundedPanel(int radius, Color color) { this.r = radius; this.c = color; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
        }
    }

    class ModernButton extends JButton {
        private Color bgColor;
        ModernButton(String text, Color bg) {
            super(text); this.bgColor = bg;
            setOpaque(false); setContentAreaFilled(false); setBorderPainted(false);
            setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g2);
        }
    }

    class RoundTextField extends JTextField {
        private int r;
        RoundTextField(int radius) { this.r = radius; setOpaque(false); setBorder(new EmptyBorder(5, 10, 5, 10)); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(245, 245, 250));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
            super.paintComponent(g2);
        }
    }

    class RoundPasswordField extends JPasswordField {
        private int r;
        RoundPasswordField(int radius) { this.r = radius; setOpaque(false); setBorder(new EmptyBorder(5, 10, 5, 10)); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(245, 245, 250));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
            super.paintComponent(g2);
        }
    }
}