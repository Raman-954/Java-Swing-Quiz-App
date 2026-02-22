import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SignupPage extends JFrame {
    private final Color GRAD_START = new Color(46, 49, 146);
    private final Color GRAD_END = new Color(102, 45, 145);
    private final Color TEXT_DARK = new Color(40, 40, 80);

    public SignupPage() {
        setTitle("Sign Up - EduQuiz Pro");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);
        JPanel card = new RoundedPanel(30, Color.WHITE);
        card.setPreferredSize(new Dimension(400, 550));
        card.setLayout(null);
        JLabel lblTitle = new JLabel("Create Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBounds(0, 30, 400, 40);
        card.add(lblTitle);
        card.add(createLabel("Username", 90));
        JTextField txtUser = createTextField(115);
        card.add(txtUser);

        card.add(createLabel("Password", 170));
        JPasswordField txtPass = createPasswordField(195);
        card.add(txtPass);

        card.add(createLabel("Confirm Password", 250));
        JPasswordField txtConfirm = createPasswordField(275);
        card.add(txtConfirm);

        JLabel lblMsg = new JLabel("", SwingConstants.CENTER);
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMsg.setBounds(50, 330, 300, 20);
        card.add(lblMsg);
        JButton btnReg = createModernButton("SIGN UP");
        btnReg.setBounds(50, 370, 300, 45);
        card.add(btnReg);
        JButton btnLogin = new JButton("<html><u>Already have an account? Login</u></html>");
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLogin.setForeground(GRAD_START);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(50, 430, 300, 30);
        card.add(btnLogin);
        btnReg.addActionListener(e -> {
            String u = txtUser.getText();
            String p = new String(txtPass.getPassword());
            String c = new String(txtConfirm.getPassword());

            if(u.isEmpty() || p.isEmpty()) {
                lblMsg.setForeground(Color.RED);
                lblMsg.setText("All fields are required!");
            } else if (!p.equals(c)) {
                lblMsg.setForeground(Color.RED);
                lblMsg.setText("Passwords do not match!");
            } else {
                if(UserManager.register(u, p)) {
                    JOptionPane.showMessageDialog(this, "Account Created! Please Login.");
                    dispose();
                    new LoginPage().setVisible(true);
                } else {
                    lblMsg.setForeground(Color.RED);
                    lblMsg.setText("Username already taken!");
                }
            }
        });

        btnLogin.addActionListener(e -> {
            dispose();
            new LoginPage().setVisible(true);
        });

        mainPanel.add(card);
    }
    private JLabel createLabel(String text, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT_DARK);
        l.setBounds(50, y, 100, 20);
        return l;
    }

    private JTextField createTextField(int y) {
        JTextField t = new RoundTextField(15);
        t.setBounds(50, y, 300, 40);
        return t;
    }

    private JPasswordField createPasswordField(int y) {
        JPasswordField t = new RoundPasswordField(15);
        t.setBounds(50, y, 300, 40);
        return t;
    }

    private JButton createModernButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GRAD_START);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    class GradientPanel extends JPanel {
        protected void paintComponent(Graphics g) {
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