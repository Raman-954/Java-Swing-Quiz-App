import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;

public class DashboardPage extends JFrame {

    private String username;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private DefaultTableModel resultTableModel;
    private JLabel lblStatQuizzes;
    private JLabel lblStatAvg;
    private JLabel lblStatXP;
    private int quizzesTaken = 0;
    private int totalPercentageAccumulated = 0;
    private int currentXP = 0;
    public static final Color SIDEBAR_BLUE = new Color(24, 28, 55);
    public static final Color ACCENT_CYAN = new Color(0, 210, 255);
    public static final Color ACCENT_PURPLE = new Color(138, 43, 226);
    public static final Color TEXT_DARK = new Color(44, 62, 80);
    public static final Color BG_MAIN = new Color(240, 244, 247);

    public DashboardPage(String username) {
        this.username = username;

        setTitle("EduQuiz Pro | Dashboard");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(BG_MAIN);
        mainArea.add(createHeader(), BorderLayout.NORTH);
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.add(createHomeView(), "HOME");
        contentPanel.add(createResultView(), "RESULTS");

        mainArea.add(contentPanel, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BLUE);
        sidebar.setPreferredSize(new Dimension(260, 750));
        sidebar.setLayout(null);
        
ImageIcon logoIcon = new ImageIcon(getClass().getResource("header.png"));
Image img = logoIcon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
logoIcon = new ImageIcon(img);
        JLabel lblLogo = new JLabel("EDUQUIZ PRO", logoIcon,SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(ACCENT_CYAN);
        lblLogo.setBounds(20, 40, 220, 30);
        sidebar.add(lblLogo);
        RoundedPanel profCard = new RoundedPanel(20, new Color(255, 255, 255, 20));
        profCard.setBounds(25, 100, 210, 140);
        profCard.setLayout(new BorderLayout());
        
        JLabel lblUser = new JLabel(username, SwingConstants.CENTER);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblUser.setForeground(Color.WHITE);
        
        JLabel lblRole = new JLabel(" ", SwingConstants.CENTER);
        lblRole.setForeground(new Color(200, 200, 200));
        
        profCard.add(lblUser, BorderLayout.CENTER);
        profCard.add(lblRole, BorderLayout.SOUTH);
        sidebar.add(profCard);
        sidebar.add(createNavButton("Dashboard", "dashboard.png",280, "HOME"));
        sidebar.add(createNavButton("My Results", "results.png",340, "RESULTS"));
        sidebar.add(createNavButton(" Settings", "settings.png",400, "SETTINGS")); 

        ModernButton btnLogout = new ModernButton("Logout", new Color(255, 70, 70));
        btnLogout.setBounds(25, 630, 210, 45);
        btnLogout.addActionListener(e -> { dispose(); new LoginPage().setVisible(true); });
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(new EmptyBorder(0, 30, 0, 30));

        String greet = (LocalTime.now().getHour() < 12) ? "Morning" : "Evening";
        JLabel lblGreet = new JLabel("Good " + greet + ", " + username + "!");
        lblGreet.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblGreet.setForeground(TEXT_DARK);

        header.add(lblGreet, BorderLayout.WEST);
        return header;
    }

    private JScrollPane createHomeView() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BG_MAIN);
        container.setBorder(new EmptyBorder(20, 30, 20, 30));
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(1200, 100));
        lblStatQuizzes = new JLabel("0");
        lblStatAvg = new JLabel("0%");
        lblStatXP = new JLabel("0");
        stats.add(createStatCard("Quizzes Done", lblStatQuizzes, ACCENT_PURPLE));
        stats.add(createStatCard("Avg Score", lblStatAvg, ACCENT_CYAN));
        stats.add(createStatCard("Total XP", lblStatXP, new Color(255, 165, 0)));
        
        container.add(stats);
        container.add(Box.createVerticalStrut(30));
        JPanel grid = new JPanel(new GridLayout(0, 3, 25, 25));
        grid.setOpaque(false);
        String[][] subjects = {
            {"Java Core", "☕", "Advanced Concepts"},
            {"Python", "🐍", "Basics & Scripting"},
            {"Web Dev", "🌐", "HTML/CSS/JS"},
            {"Database", "🗄️", "SQL & Queries"},
            {"Maths", "📐", "Logic & Algebra"},
            {"Science", "⚛️", "Physics/Chem"}
        };

        for (String[] s : subjects) grid.add(createQuizCard(s[0], s[1], s[2]));
        container.add(grid);

        JScrollPane sp = new JScrollPane(container);
        sp.setBorder(null);
        return sp;
    }

    private JPanel createResultView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_MAIN);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        JLabel title = new JLabel("Assessment History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Subject", "Score", "Accuracy", "Status", "Date"};
        resultTableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(resultTableModel);
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(new Color(200, 200, 200)));
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }
    private void openSettingsDialog() {
        JDialog dialog = new JDialog(this, "Edit Profile", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Update Profile");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBounds(30, 20, 200, 30);
        dialog.add(lblTitle);

        JLabel lblUser = new JLabel("New Username:");
        lblUser.setBounds(30, 70, 150, 20);
        dialog.add(lblUser);

        JTextField txtNewUser = new JTextField(username);
        txtNewUser.setBounds(30, 95, 320, 35);
        txtNewUser.setForeground(Color.BLACK); 
        txtNewUser.setCaretColor(Color.BLACK);
        dialog.add(txtNewUser);

        JLabel lblPass = new JLabel("New Password:");
        lblPass.setBounds(30, 145, 150, 20);
        dialog.add(lblPass);

        JPasswordField txtNewPass = new JPasswordField();
        txtNewPass.setBounds(30, 170, 320, 35);
        txtNewPass.setForeground(Color.BLACK);
        txtNewPass.setCaretColor(Color.BLACK);
        dialog.add(txtNewPass);

        JButton btnSave = new JButton("SAVE & LOGOUT");
        btnSave.setBackground(ACCENT_PURPLE);
        btnSave.setForeground(Color.BLACK); 
        
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBounds(30, 230, 320, 40);
        btnSave.setFocusPainted(false);
        
        btnSave.addActionListener(e -> {
            String newUser = txtNewUser.getText();
            String newPass = new String(txtNewPass.getPassword());

            if (newUser.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Fields cannot be empty!");
            } else {
                UserManager.updateUser(username, newUser, newPass);
                JOptionPane.showMessageDialog(dialog, "Updated! Please Login again.");
                dialog.dispose();
                this.dispose();
                new LoginPage().setVisible(true);
            }
        });
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private JPanel createQuizCard(String title, String icon, String desc) {
        RoundedPanel card = new RoundedPanel(25, Color.WHITE);
        card.setLayout(null);
        card.setPreferredSize(new Dimension(220, 180));
        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setBounds(20, 20, 60, 60);
        card.add(lblIcon);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBounds(20, 85, 180, 25);
        card.add(lblTitle);
        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(Color.GRAY);
        lblDesc.setBounds(20, 110, 180, 20);
        card.add(lblDesc);
        ModernButton btn = new ModernButton("START", ACCENT_PURPLE);
        btn.setBounds(110, 25, 85, 30);
        btn.addActionListener(e -> new QuizWindow(title, this));
        card.add(btn);
        return card;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color c) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new GridLayout(2, 1));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel t = new JLabel(title); 
        t.setForeground(Color.GRAY);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); 
        valueLabel.setForeground(c);
        
        card.add(t); 
        card.add(valueLabel);
        return card;
    }

    private JButton createNavButton(String text, String iconPath, int y, String target) {
         ImageIcon icon = new ImageIcon(iconPath);
    Image img = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
    icon = new ImageIcon(img);
        JButton btn = new JButton(text,icon);
        btn.setBounds(20, y, 220, 45);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(10);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(180, 180, 220));
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(0, 15, 0, 0));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { btn.setForeground(new Color(180, 180, 220)); }
        });
        btn.addActionListener(e -> {
            if ("SETTINGS".equals(target)) openSettingsDialog();
            else if(target != null) cardLayout.show(contentPanel, target);
        });
        return btn;
    }

    public void addResult(String sub, int score, int total) {
        int accuracy = (score * 100) / total;
        String status = (accuracy >= 50) ? "Passed" : "Failed";
        
        resultTableModel.addRow(new Object[]{sub, score+"/"+total, accuracy+"%", status, "Today"});
        
        quizzesTaken++; 
        totalPercentageAccumulated += accuracy;
        currentXP += (score * 50); 
        int avg = totalPercentageAccumulated / quizzesTaken;

        lblStatQuizzes.setText(String.valueOf(quizzesTaken));
        lblStatAvg.setText(avg + "%");
        lblStatXP.setText(String.valueOf(currentXP));

        cardLayout.show(contentPanel, "RESULTS");
    }

    class ModernButton extends JButton {
        private Color color;
        public ModernButton(String text, Color c) { super(text); this.color = c; setOpaque(false); setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); 
        setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 13)); setCursor(new Cursor(Cursor.HAND_CURSOR)); }
        protected void paintComponent(Graphics g) { Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
        g2.setColor(color); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); super.paintComponent(g2); }
    }
    class RoundedPanel extends JPanel {
        private int r; private Color c;
        public RoundedPanel(int radius, Color color) { this.r = radius; this.c = color; setOpaque(false); }
        protected void paintComponent(Graphics g) { Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
        g2.setColor(c); g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r); }
    }
    class StatusRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col); l.setHorizontalAlignment(SwingConstants.CENTER); 
            if ("Passed".equals(value)) l.setForeground(new Color(0, 150, 0)); else l.setForeground(Color.RED); l.setFont(new Font("Segoe UI", Font.BOLD, 13)); return l;
        }
    }
}
