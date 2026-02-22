import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizWindow extends JFrame {

    private String subject;
    private DashboardPage parentDashboard;
    private List<Question> questionList;
    private String[] userAnswers;
    private int currentQuestionIndex = 0;
    
    private Timer quizTimer;
    private int timeRemainingSeconds = 30 * 60;
    private boolean warningShown = false;

    private JLabel lblQuestionText, lblQuestionHeader, lblTimer;
    private JPanel optionsPanel, palettePanel;
    private ButtonGroup optionsGroup;
    private ModernButton btnNext, btnPrev, btnSubmit;
    private JButton[] paletteButtons;

    private final Color GRAD_TOP = new Color(20, 40, 80);    
    private final Color GRAD_BOT = new Color(40, 80, 140);   
    private final Color CARD_WHITE = new Color(255, 255, 255); 
    private final Color NEON_CYAN = new Color(0, 255, 255);
    private final Color SUCCESS_GREEN = new Color(40, 180, 100);
    private final Color TEXT_MAIN = new Color(30, 30, 30); // High contrast black

    public QuizWindow(String subject, DashboardPage dashboard) {
        this.subject = subject;
        this.parentDashboard = dashboard;

        setTitle("Premium Assessment - " + subject);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        setContentPane(new GradientBackground());
        setLayout(new BorderLayout());

        loadQuestionsForSubject(subject);
        userAnswers = new String[questionList.size()];

        add(createHeader(), BorderLayout.NORTH);
        add(createMainQuizArea(), BorderLayout.CENTER);
        add(createSidebarPalette(), BorderLayout.EAST);

        startTimer();
        loadQuestion(0);
        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 100));
        header.setBorder(new EmptyBorder(20, 40, 10, 40));

        JLabel title = new JLabel("<html><font color='white' size='7'>EDU</font><font color='#00ffff' size='7'>QUIZ PRO</font></html>");

        JPanel timerBox = new RoundedPanel(20, new Color(0, 0, 0, 150));
        timerBox.setPreferredSize(new Dimension(180, 60));
        timerBox.setLayout(new BorderLayout());
        
        lblTimer = new JLabel("30:00", SwingConstants.CENTER);
        lblTimer.setFont(new Font("Monospaced", Font.BOLD, 32));
        lblTimer.setForeground(NEON_CYAN);
        timerBox.add(lblTimer, BorderLayout.CENTER);

        header.add(title, BorderLayout.WEST);
        header.add(timerBox, BorderLayout.EAST);
        return header;
    }

    private JPanel createMainQuizArea() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel card = new RoundedPanel(30, CARD_WHITE);
        card.setPreferredSize(new Dimension(900, 580));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(50, 60, 50, 60));

        lblQuestionHeader = new JLabel("QUESTION 01");
        lblQuestionHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblQuestionHeader.setForeground(new Color(120, 120, 120));

        lblQuestionText = new JLabel();
        lblQuestionText.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblQuestionText.setForeground(TEXT_MAIN);

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(0, 1, 0, 20));
        optionsPanel.setOpaque(false);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        footer.setOpaque(false);
        
        btnPrev = new ModernButton("PREVIOUS", new Color(100, 110, 130));
        btnNext = new ModernButton("SAVE & NEXT", new Color(54, 55, 149));
        
        btnPrev.addActionListener(e -> navigate(-1));
        btnNext.addActionListener(e -> { saveAnswer(); navigate(1); });

        footer.add(btnPrev);
        footer.add(btnNext);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblQuestionHeader, BorderLayout.NORTH);
        top.add(lblQuestionText, BorderLayout.CENTER);

        card.add(top, BorderLayout.NORTH);
        card.add(optionsPanel, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        centerWrapper.add(card);
        return centerWrapper;
    }

    private JPanel createSidebarPalette() {
        JPanel sidebar = new RoundedPanel(0, new Color(0, 0, 0, 60));
        sidebar.setPreferredSize(new Dimension(340, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(new EmptyBorder(30, 25, 30, 25));

        JLabel palTitle = new JLabel("QUESTION PALETTE", SwingConstants.CENTER);
        palTitle.setForeground(Color.WHITE);
        palTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        palTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        sidebar.add(palTitle, BorderLayout.NORTH);

        palettePanel = new JPanel(new GridLayout(0, 4, 15, 15));
        palettePanel.setOpaque(false);

        paletteButtons = new JButton[questionList.size()];
        for (int i = 0; i < questionList.size(); i++) {
            final int index = i;
            JButton btn = new JButton(String.valueOf(i + 1));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setBorder(null);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> { saveAnswer(); loadQuestion(index); });
            
            paletteButtons[i] = btn;
            palettePanel.add(btn);
        }

        JScrollPane scroll = new JScrollPane(palettePanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        sidebar.add(scroll, BorderLayout.CENTER);

        btnSubmit = new ModernButton("SUBMIT EXAM", SUCCESS_GREEN);
        btnSubmit.setPreferredSize(new Dimension(0, 70));
        btnSubmit.addActionListener(e -> finishQuiz());
        sidebar.add(btnSubmit, BorderLayout.SOUTH);

        return sidebar;
    }

    private void loadQuestion(int index) {
        currentQuestionIndex = index;
        Question q = questionList.get(index);

        lblQuestionHeader.setText("QUESTION " + String.format("%02d", index + 1));
        lblQuestionText.setText("<html><body style='width: 650px'>" + q.getQuestionText() + "</body></html>");

        optionsPanel.removeAll();
        optionsGroup = new ButtonGroup();

        for (String opt : q.getOptions()) {
            JRadioButton rb = new JRadioButton(opt);
            rb.setFont(new Font("Segoe UI", Font.BOLD, 18));
            rb.setForeground(TEXT_MAIN); // Force visibility
            rb.setOpaque(false);
            rb.setActionCommand(opt);
            if (userAnswers[index] != null && userAnswers[index].equals(opt)) rb.setSelected(true);
            
            optionsGroup.add(rb);
            optionsPanel.add(rb);
        }

        btnPrev.setEnabled(index > 0);
        btnNext.setText(index == questionList.size() - 1 ? "FINISH EXAM" : "SAVE & NEXT");
        btnNext.setColor(index == questionList.size() - 1 ? SUCCESS_GREEN : new Color(54, 55, 149));

        updatePaletteUI();
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void saveAnswer() {
        if (optionsGroup.getSelection() != null) {
            userAnswers[currentQuestionIndex] = optionsGroup.getSelection().getActionCommand();
        }
    }

    private void navigate(int dir) {
        int target = currentQuestionIndex + dir;
        if (target >= 0 && target < questionList.size()) loadQuestion(target);
        else if (dir == 1) finishQuiz();
    }

    private void updatePaletteUI() {
        for (int i = 0; i < paletteButtons.length; i++) {
            if (i == currentQuestionIndex) {
                paletteButtons[i].setBackground(Color.WHITE);
                paletteButtons[i].setForeground(Color.BLACK);
                paletteButtons[i].setBorder(new LineBorder(NEON_CYAN, 3));
            } else if (userAnswers[i] != null) {
                paletteButtons[i].setBackground(SUCCESS_GREEN);
                paletteButtons[i].setForeground(Color.WHITE);
                paletteButtons[i].setBorder(null);
            } else {
                paletteButtons[i].setBackground(new Color(255, 255, 255, 200));
                paletteButtons[i].setForeground(Color.BLACK);
                paletteButtons[i].setBorder(null);
            }
        }
    }

    private void startTimer() {
        quizTimer = new Timer(1000, e -> {
            timeRemainingSeconds--;
            int m = timeRemainingSeconds / 60;
            int s = timeRemainingSeconds % 60;
            lblTimer.setText(String.format("%02d:%02d", m, s));

            if (timeRemainingSeconds <= 300 && !warningShown) {
                lblTimer.setForeground(Color.RED);
                JOptionPane.showMessageDialog(this, "⚠️ Warning: 5 Minutes Left!");
                warningShown = true;
            }
            if (timeRemainingSeconds <= 0) finishQuiz();
        });
        quizTimer.start();
    }

    private void finishQuiz() {
        quizTimer.stop();
        int score = 0;
        for (int i = 0; i < questionList.size(); i++) {
            if (userAnswers[i] != null && userAnswers[i].equals(questionList.get(i).getCorrectAnswer())) score++;
        }
        
        btnSubmit.setColor(new Color(255, 200, 0)); 
        btnSubmit.setText("SUBMITTED ✓");

        JOptionPane.showMessageDialog(this, "Exam Finished!\nFinal Score: " + score + "/" + questionList.size());
        parentDashboard.addResult(subject, score, questionList.size());
        dispose();
    }

    private void loadQuestionsForSubject(String subjectName) {
        questionList = new ArrayList<>();
        if (subjectName.contains("Java")) {
            addQ("1. What is the size of an int variable in Java?", new String[]{"8 bit", "16 bit", "32 bit", "64 bit"}, "32 bit");
            addQ("2. Which keyword is used for inheritance?", new String[]{"extends", "implements", "inherits", "using"}, "extends");
            addQ("3. What is the entry point of a Java Program?", new String[]{"main()", "start()", "run()", "init()"}, "main()");
            addQ("4. Which data type is used to store 'true' or 'false'?", new String[]{"int", "boolean", "String", "char"}, "boolean");
            addQ("5. What is the default value of an Object reference?", new String[]{"0", "null", "undefined", "false"}, "null");
            addQ("6. Method Overloading involves...", new String[]{"Same name, diff signature", "Diff name, same signature", "Same name, same signature", "None"}, "Same name, diff signature");
            addQ("7. Which keyword prevents a class from being inherited?", new String[]{"static", "final", "abstract", "private"}, "final");
            addQ("8. Can an abstract class be instantiated?", new String[]{"Yes", "No", "Only if static", "Sometimes"}, "No");
            addQ("9. Which concept allows an object to take many forms?", new String[]{"Encapsulation", "Polymorphism", "Inheritance", "Abstraction"}, "Polymorphism");
            addQ("10. Access modifier for 'visible only within the class'?", new String[]{"public", "protected", "default", "private"}, "private");
            addQ("11. Java code compiles into...", new String[]{"Source Code", "Machine Code", "Bytecode", "Binary"}, "Bytecode");
            addQ("12. Which component executes the Bytecode?", new String[]{"JDK", "JVM", "JRE", "JIT"}, "JVM");
            addQ("13. Which exception is Unchecked?", new String[]{"IOException", "SQLException", "RuntimeException", "ClassNotFoundException"}, "RuntimeException");
            addQ("14. Wrapper class for 'int' is...", new String[]{"Int", "Integer", "Number", "Float"}, "Integer");
            addQ("15. What is the return type of a Constructor?", new String[]{"int", "void", "Class Name", "No return type"}, "No return type");
            addQ("16. Strings in Java are...", new String[]{"Mutable", "Immutable", "Dynamic", "Static"}, "Immutable");
            addQ("17. Which class is used for mutable strings?", new String[]{"String", "StringBuilder", "CharBuffer", "StringArray"}, "StringBuilder");
            addQ("18. How do you find the size of an array?", new String[]{".size()", ".length", ".length()", ".getSize()"}, ".length");
            addQ("19. Method to compare two Strings for content equality?", new String[]{"==", ".equals()", ".compare()", "="}, ".equals()");
            addQ("20. Array index starts from...", new String[]{"1", "0", "-1", "null"}, "0");
            addQ("21. Keyword used to access static members?", new String[]{"Class Name", "Object", "this", "super"}, "Class Name");
            addQ("22. Keyword used to refer to the parent class?", new String[]{"this", "parent", "super", "base"}, "super");
            addQ("23. Which collection stores unique elements only?", new String[]{"List", "Map", "Set", "Vector"}, "Set");
            addQ("24. Which Map allows null keys?", new String[]{"TreeMap", "HashMap", "HashTable", "None"}, "HashMap");
            addQ("25. Which block always executes in exception handling?", new String[]{"try", "catch", "finally", "throws"}, "finally");
            addQ("26. Base class for all threads in Java?", new String[]{"Process", "Thread", "Runnable", "Worker"}, "Thread");
            addQ("27. Which symbol is used for Lambda Expressions?", new String[]{"->", "=>", "::", "--"}, "->");
            addQ("28. How to break out of a loop?", new String[]{"stop", "return", "break", "continue"}, "break");
            addQ("29. Correct way to declare a constant variable?", new String[]{"const int x", "final int x", "static int x", "var x"}, "final int x");
            addQ("30. Does Java support multiple inheritance with classes?", new String[]{"Yes", "No", "Maybe", "Only for Object"}, "No");
        } 
        
        else if (subjectName.contains("Python")) {
            addQ("1. Output of: print(2 ** 3)", new String[]{"6", "8", "9", "12"}, "8");
            addQ("2. Which keyword is used to define a function?", new String[]{"func", "def", "define", "lambda"}, "def");
            addQ("3. Which character is used for comments?", new String[]{"//", "#", "/*", "--"}, "#");
            addQ("4. Output of: print(10 // 3)", new String[]{"3.33", "3.0", "3", "30"}, "3");
            addQ("5. What is the file extension for Python files?", new String[]{".pt", ".pyt", ".py", ".java"}, ".py");
            addQ("6. List is mutable or immutable?", new String[]{"Mutable", "Immutable", "Both", "None"}, "Mutable");
            addQ("7. Which syntax creates a Tuple?", new String[]{"[]", "{}", "()", "<>"}, "()");
            addQ("8. Which syntax creates a Dictionary?", new String[]{"[]", "{}", "()", "set()"}, "{}");
            addQ("9. How do you add an element to a List?", new String[]{".add()", ".push()", ".append()", ".insert()"}, ".append()");
            addQ("10. Which creates an empty set?", new String[]{"set()", "{}", "[]", "()"}, "set()");
            addQ("11. Which keyword breaks out of a loop?", new String[]{"stop", "exit", "break", "return"}, "break");
            addQ("12. What is the output of: range(3)?", new String[]{"1, 2, 3", "0, 1, 2", "1, 2", "0, 1, 2, 3"}, "0, 1, 2");
            addQ("13. Boolean values in Python are...", new String[]{"true/false", "True/False", "1/0", "TRUE/FALSE"}, "True/False");
            addQ("14. Which operator is used for exponentiation?", new String[]{"^", "**", "exp()", "power"}, "**");
            addQ("15. Output of: print('Hello'[0])", new String[]{"H", "e", "Hello", "Error"}, "H");
            addQ("16. Which library is used for Data Analysis?", new String[]{"Pandas", "Requests", "Flask", "PyGame"}, "Pandas");
            addQ("17. Which library is used for Plotting graphs?", new String[]{"Matplotlib", "Numpy", "Json", "OS"}, "Matplotlib");
            addQ("18. How to import a library?", new String[]{"include", "import", "using", "require"}, "import");
            addQ("19. Return type of input() function?", new String[]{"int", "float", "str", "bool"}, "str");
            addQ("20. What is a lambda function?", new String[]{"A loop", "Anonymous function", "A library", "A variable"}, "Anonymous function");
            addQ("21. Method called when object is created?", new String[]{"__init__", "__start__", "__main__", "constructor"}, "__init__");
            addQ("22. Keyword to reference current instance?", new String[]{"this", "self", "me", "it"}, "self");
            addQ("23. How to handle exceptions?", new String[]{"try-catch", "try-except", "do-catch", "try-error"}, "try-except");
            addQ("24. Output of: print('a' * 3)", new String[]{"aaa", "a3", "Error", "3a"}, "aaa");
            addQ("25. Which is NOT a keyword?", new String[]{"eval", "assert", "nonlocal", "pass"}, "eval");
            addQ("26. What does 'pass' do?", new String[]{"Stops program", "Skips iteration", "Do nothing", "Prints pass"}, "Do nothing");
            addQ("27. Is Python compiled or interpreted?", new String[]{"Compiled", "Interpreted", "Both", "None"}, "Interpreted");
            addQ("28. Syntax for string slicing to reverse?", new String[]{"[::-1]", "[1::]", "[-1]", "[reverse]"}, "[::-1]");
            addQ("29. Correct way to create a variable?", new String[]{"int x = 5", "x = 5", "var x = 5", "dim x = 5"}, "x = 5");
            addQ("30. Which operator checks for identity?", new String[]{"==", "=", "is", "identity"}, "is");
        }
        
        else if (subjectName.contains("Web")) {
            addQ("1. Full form of HTML?", new String[]{"Hyper Text Markup Language", "Hyper Tool Multi Language", "Home Tool Markup Language", "None"}, "Hyper Text Markup Language");
            addQ("2. Which tag creates a line break?", new String[]{"<br>", "<lb>", "<break>", "<ln>"}, "<br>");
            addQ("3. Which is the largest heading tag?", new String[]{"<h6>", "<head>", "<h1>", "<heading>"}, "<h1>");
            addQ("4. Which tag is used to create a hyperlink?", new String[]{"<a>", "<link>", "<href>", "<url>"}, "<a>");
            addQ("5. Correct HTML5 doctype declaration?", new String[]{"<!DOCTYPE html>", "<doctype html>", "<html5>", "<!DOCTYPE HTML5>"}, "<!DOCTYPE html>");
            addQ("6. Which tag is used to display an image?", new String[]{"<picture>", "<image>", "<img>", "<src>"}, "<img>");
            addQ("7. Which attribute specifies the image URL?", new String[]{"link", "href", "src", "url"}, "src");
            addQ("8. How to create an unordered list?", new String[]{"<ul>", "<ol>", "<li>", "<list>"}, "<ul>");
            addQ("9. Which input type creates a checkbox?", new String[]{"type='check'", "type='checkbox'", "type='box'", "type='tick'"}, "type='checkbox'");
            addQ("10. Which is a semantic tag?", new String[]{"<div>", "<span>", "<article>", "<b>"}, "<article>");
            addQ("11. CSS stands for?", new String[]{"Cascading Style Sheets", "Creative Style System", "Color Style Sheet", "None"}, "Cascading Style Sheets");
            addQ("12. Which HTML tag is used to link an external CSS file?", new String[]{"<css>", "<script>", "<link>", "<style>"}, "<link>");
            addQ("13. How do you select an element with id 'header'?", new String[]{".header", "#header", "header", "*header"}, "#header");
            addQ("14. How do you select elements with class 'menu'?", new String[]{".menu", "#menu", "menu", "*menu"}, ".menu");
            addQ("15. Property to change text color?", new String[]{"text-color", "fgcolor", "color", "font-color"}, "color");
            addQ("16. Property to change background color?", new String[]{"bgcolor", "background-color", "color", "bg"}, "background-color");
            addQ("17. Which property controls text size?", new String[]{"font-style", "text-size", "font-size", "size"}, "font-size");
            addQ("18. How to make text bold in CSS?", new String[]{"font-weight: bold", "style: bold", "font: bold", "text: bold"}, "font-weight: bold");
            addQ("19. The space outside the border is called?", new String[]{"padding", "spacing", "margin", "border-out"}, "margin");
            addQ("20. Which property is used for Flexbox?", new String[]{"display: flex", "position: flex", "float: flex", "align: flex"}, "display: flex");
            addQ("21. Inside which HTML element do we put JavaScript?", new String[]{"<script>", "<js>", "<javascript>", "<scripting>"}, "<script>");
            addQ("22. How do you write 'Hello World' in an alert box?", new String[]{"msg('Hello World')", "alert('Hello World')", "msgBox('Hello World')", "alertBox('Hello World')"}, "alert('Hello World')");
            addQ("23. How do you create a function in JavaScript?", new String[]{"function myFunction()", "def myFunction()", "create myFunction()", "func myFunction()"}, "function myFunction()");
            addQ("24. How to call a function named 'myFunction'?", new String[]{"call myFunction()", "myFunction()", "run.myFunction()", "execute myFunction()"}, "myFunction()");
            addQ("25. Which operator is used to assign a value?", new String[]{"-", "*", "=", "=="}, "=");
            addQ("26. What is the correct way to write an IF statement?", new String[]{"if i = 5 then", "if (i == 5)", "if i == 5", "if i = 5"}, "if (i == 5)");
            addQ("27. How does a FOR loop start?", new String[]{"for (i = 0; i <= 5)", "for (i = 0; i <= 5; i++)", "for i = 1 to 5", "for (i <= 5; i++)"}, "for (i = 0; i <= 5; i++)");
            addQ("28. Which event occurs when the user clicks on an HTML element?", new String[]{"onchange", "onmouseover", "onmouseclick", "onclick"}, "onclick");
            addQ("29. How do you declare a JavaScript variable?", new String[]{"v carName;", "variable carName;", "var carName;", "dim carName;"}, "var carName;");
            addQ("30. Which operator returns true if both value and type are equal?", new String[]{"==", "=", "===", "!="}, "===");
        }
         
        else if (subjectName.contains("Database")) {
            addQ("1. SQL stands for?", new String[]{"Structured Query Language", "Simple Query Language", "System Query List", "None"}, "Structured Query Language");
            addQ("2. Which command is used to extract data from a database?", new String[]{"GET", "OPEN", "SELECT", "EXTRACT"}, "SELECT");
            addQ("3. Which SQL statement is used to update data in a database?", new String[]{"MODIFY", "UPDATE", "SAVE", "ALTER"}, "UPDATE");
            addQ("4. Which SQL statement is used to delete data from a database?", new String[]{"REMOVE", "COLLAPSE", "DELETE", "DROP"}, "DELETE");
            addQ("5. Which SQL statement is used to insert new data in a database?", new String[]{"ADD RECORD", "INSERT INTO", "ADD NEW", "INSERT NEW"}, "INSERT INTO");
            addQ("6. Which of the following is a DDL (Data Definition Language) command?", new String[]{"SELECT", "UPDATE", "CREATE", "INSERT"}, "CREATE");
            addQ("7. Which of the following is a DML (Data Manipulation Language) command?", new String[]{"ALTER", "INSERT", "DROP", "GRANT"}, "INSERT");
            addQ("8. Which command is used to remove a table from the database?", new String[]{"DELETE TABLE", "REMOVE TABLE", "DROP TABLE", "WIPE TABLE"}, "DROP TABLE");
            addQ("9. What does 'DCL' stand for in SQL?", new String[]{"Data Control Language", "Data Change Language", "Database Control List", "None"}, "Data Control Language");
            addQ("10. Which command is used to give user access privileges?", new String[]{"GIVE", "ALLOW", "GRANT", "REVOKE"}, "GRANT");
            addQ("11. Which clause is used to filter the records?", new String[]{"WHERE", "FILTER", "HAVING", "SORT"}, "WHERE");
            addQ("12. Which keyword is used to return only different values?", new String[]{"UNIQUE", "DISTINCT", "DIFFERENT", "ONLY"}, "DISTINCT");
            addQ("13. Which clause is used to sort the result-set?", new String[]{"SORT BY", "ORDER BY", "ALIGN BY", "GROUP BY"}, "ORDER BY");
            addQ("14. How do you select all columns from a table named 'Persons'?", new String[]{"SELECT Persons", "SELECT * FROM Persons", "SELECT [all] FROM Persons", "GET * FROM Persons"}, "SELECT * FROM Persons");
            addQ("15. Which clause is used with aggregate functions to group the result-set?", new String[]{"GROUP BY", "ORDER BY", "SORT", "COLLECT"}, "GROUP BY");
            addQ("16. Which operator is used to search for a specified pattern in a column?", new String[]{"GET", "LIKE", "SEARCH", "PATTERN"}, "LIKE");
            addQ("17. In SQL, what is the default sort order for ORDER BY?", new String[]{"Descending", "Ascending", "Random", "Numerical"}, "Ascending");
            addQ("18. Which function returns the number of rows in a table?", new String[]{"SUM()", "TOTAL()", "COUNT()", "ROWS()"}, "COUNT()");
            addQ("19. Which operator is used to select values within a range?", new String[]{"WITHIN", "BETWEEN", "RANGE", "IN"}, "BETWEEN");
            addQ("20. How do you find the highest value in a column named 'Price'?", new String[]{"MAX(Price)", "TOP(Price)", "HIGH(Price)", "SUM(Price)"}, "MAX(Price)");
            addQ("21. A Primary Key must be...", new String[]{"Unique & Not Null", "Unique & Null", "Duplicate", "Any value"}, "Unique & Not Null");
            addQ("22. Which key links two tables together?", new String[]{"Primary Key", "Secondary Key", "Foreign Key", "Composite Key"}, "Foreign Key");
            addQ("23. Which constraint ensures that all values in a column are different?", new String[]{"NOT NULL", "CHECK", "UNIQUE", "DEFAULT"}, "UNIQUE");
            addQ("24. What does ACID stand for in Database properties?", new String[]{"Atomicity, Consistency, Isolation, Durability", "Access, Control, Integrated, Direct", "Auto, Change, Input, Data", "None"}, "Atomicity, Consistency, Isolation, Durability");
            addQ("25. Which normal form deals with removing Partial Functional Dependency?", new String[]{"1NF", "2NF", "3NF", "BCNF"}, "2NF");
            addQ("26. What is a 'Join' used for?", new String[]{"Combine rows from tables", "Delete rows", "Create a table", "Sort data"}, "Combine rows from tables");
            addQ("27. Which Join returns all records when there is a match in either left or right table?", new String[]{"INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "FULL OUTER JOIN"}, "FULL OUTER JOIN");
            addQ("28. Which command is used to undo a transaction?", new String[]{"COMMIT", "SAVEPOINT", "ROLLBACK", "UNDO"}, "ROLLBACK");
            addQ("29. RDBMS stands for?", new String[]{"Relational Database Management System", "Relative Database Memory System", "Real Database Management System", "None"}, "Relational Database Management System");
            addQ("30. Which of these is NOT a popular SQL database?", new String[]{"MySQL", "Oracle", "Java", "PostgreSQL"}, "Java");
        }
        else if (subjectName.contains("Math")) {
            addQ("1. What is 15% of 200?", new String[]{"20", "30", "40", "25"}, "30");
            addQ("2. Value of 5 + 3 * 2?", new String[]{"16", "11", "13", "10"}, "11");
            addQ("3. What is the square root of 625?", new String[]{"15", "25", "35", "45"}, "25");
            addQ("4. Which is the smallest prime number?", new String[]{"0", "1", "2", "3"}, "2");
            addQ("5. What is the cube of 6?", new String[]{"36", "216", "116", "256"}, "216");
            addQ("6. Value of 1/2 + 1/4?", new String[]{"1/6", "2/6", "3/4", "2/4"}, "3/4");
            addQ("7. Solve: 100 / (5 * 4)?", new String[]{"20", "4", "5", "10"}, "5");
            addQ("8. How many sides does a Hexagon have?", new String[]{"5", "6", "7", "8"}, "6");
            addQ("9. What is 10 to the power of 3?", new String[]{"100", "1000", "10000", "30"}, "1000");
            addQ("10. Roman numeral for 50?", new String[]{"X", "V", "L", "C"}, "L");
            addQ("11. Solve for x: 2x + 5 = 15", new String[]{"10", "5", "7.5", "2.5"}, "5");
            addQ("12. Solve for y: 3y - 9 = 0", new String[]{"3", "9", "0", "-3"}, "3");
            addQ("13. What is (a + b)^2?", new String[]{"a^2 + b^2", "a^2 + 2ab + b^2", "a^2 - 2ab + b^2", "2a + 2b"}, "a^2 + 2ab + b^2");
            addQ("14. If x = 2 and y = 3, what is x * y + x?", new String[]{"6", "8", "9", "5"}, "8");
            addQ("15. Factorial of 4 (4!) is?", new String[]{"12", "16", "24", "4"}, "24");
            addQ("16. Sum of angles in a triangle is?", new String[]{"90°", "180°", "360°", "270°"}, "180°");
            addQ("17. Area of a circle formula?", new String[]{"2πr", "πr^2", "πd", "2πr^2"}, "πr^2");
            addQ("18. Perimeter of a square with side 5cm?", new String[]{"20cm", "25cm", "10cm", "15cm"}, "20cm");
            addQ("19. An angle less than 90 degrees is called?", new String[]{"Obtuse", "Right", "Acute", "Reflex"}, "Acute");
            addQ("20. How many degrees in a circle?", new String[]{"180°", "270°", "360°", "450°"}, "360°");
            addQ("21. Value of sin(90°)?", new String[]{"0", "1", "0.5", "undefined"}, "1");
            addQ("22. Value of cos(0°)?", new String[]{"0", "1", "0.5", "0.866"}, "1");
            addQ("23. In a right triangle, c^2 = a^2 + b^2 is called?", new String[]{"Newton's Law", "Euler's Law", "Pythagoras Theorem", "Pascal's Law"}, "Pythagoras Theorem");
            addQ("24. Probability of getting Heads on a coin flip?", new String[]{"1", "0", "0.5", "0.25"}, "0.5");
            addQ("25. Average of 10, 20, and 30?", new String[]{"15", "20", "25", "60"}, "20");
            addQ("26. What is the LCM of 4 and 6?", new String[]{"2", "12", "24", "10"}, "12");
            addQ("27. What is the HCF of 10 and 15?", new String[]{"2", "3", "5", "1"}, "5");
            addQ("28. Distance formula: Speed multiplied by ___?", new String[]{"Velocity", "Time", "Gravity", "Mass"}, "Time");
            addQ("29. A leap year has how many days?", new String[]{"365", "366", "364", "360"}, "366");
            addQ("30. Anything divided by 0 is?", new String[]{"0", "1", "Infinite/Undefined", "Itself"}, "Infinite/Undefined");
        }
        else {
            addQ("1. What is the chemical symbol for Water?", new String[]{"H2O", "O2", "CO2", "HO"}, "H2O");
            addQ("2. Which gas is most abundant in Earth's atmosphere?", new String[]{"Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen"}, "Nitrogen");
            addQ("3. What is the pH of pure water?", new String[]{"0", "5", "7", "14"}, "7");
            addQ("4. Which is the lightest element in the periodic table?", new String[]{"Helium", "Oxygen", "Hydrogen", "Lithium"}, "Hydrogen");
            addQ("5. Common name for Sodium Chloride?", new String[]{"Sugar", "Salt", "Baking Soda", "Vinegar"}, "Salt");
            addQ("6. Which metal is liquid at room temperature?", new String[]{"Iron", "Mercury", "Gold", "Aluminum"}, "Mercury");
            addQ("7. What is the hardest natural substance on Earth?", new String[]{"Gold", "Iron", "Diamond", "Quartz"}, "Diamond");
            addQ("8. Which gas do plants absorb during photosynthesis?", new String[]{"Oxygen", "Nitrogen", "Carbon Dioxide", "Helium"}, "Carbon Dioxide");
            addQ("9. Atomic number of Oxygen?", new String[]{"6", "8", "10", "12"}, "8");
            addQ("10. Which is a Noble Gas?", new String[]{"Helium", "Oxygen", "Nitrogen", "Chlorine"}, "Helium");
            addQ("11. Force = Mass multiplied by ___?", new String[]{"Acceleration", "Velocity", "Time", "Gravity"}, "Acceleration");
            addQ("12. What is the approximate speed of light?", new String[]{"3x10^8 m/s", "3x10^6 m/s", "300,000 m/s", "Infinite"}, "3x10^8 m/s");
            addQ("13. Who discovered the law of Gravity?", new String[]{"Einstein", "Newton", "Tesla", "Galileo"}, "Newton");
            addQ("14. Unit of electrical resistance?", new String[]{"Volt", "Ampere", "Ohm", "Watt"}, "Ohm");
            addQ("15. Sound travels fastest in which medium?", new String[]{"Air", "Water", "Steel (Solids)", "Vacuum"}, "Steel (Solids)");
            addQ("16. Which color has the longest wavelength?", new String[]{"Blue", "Green", "Violet", "Red"}, "Red");
            addQ("17. What type of energy is stored in a battery?", new String[]{"Kinetic", "Chemical", "Nuclear", "Solar"}, "Chemical");
            addQ("18. Process of a solid turning directly into gas?", new String[]{"Evaporation", "Melting", "Sublimation", "Condensation"}, "Sublimation");
            addQ("19. Part of the atom that has a negative charge?", new String[]{"Proton", "Neutron", "Electron", "Nucleus"}, "Electron");
            addQ("20. Instrument used to measure atmospheric pressure?", new String[]{"Thermometer", "Barometer", "Ammeter", "Speedometer"}, "Barometer");
            addQ("21. Which part is known as the powerhouse of the cell?", new String[]{"Mitochondria", "Nucleus", "Ribosome", "Cell Wall"}, "Mitochondria");
            addQ("22. How many bones are in the adult human body?", new String[]{"200", "206", "212", "190"}, "206");
            addQ("23. Which vitamin do we get from Sunlight?", new String[]{"Vitamin A", "Vitamin B12", "Vitamin C", "Vitamin D"}, "Vitamin D");
            addQ("24. Which organ filters blood in the human body?", new String[]{"Heart", "Lungs", "Kidneys", "Liver"}, "Kidneys");
            addQ("25. What is the primary pigment used in photosynthesis?", new String[]{"Hemoglobin", "Chlorophyll", "Melanin", "Carotene"}, "Chlorophyll");
            addQ("26. Which blood group is a universal donor?", new String[]{"A+", "B-", "AB+", "O-"}, "O-");
            addQ("27. Study of birds is called?", new String[]{"Zoology", "Ornithology", "Entomology", "Botany"}, "Ornithology");
            addQ("28. Largest organ of the human body?", new String[]{"Brain", "Liver", "Skin", "Heart"}, "Skin");
            addQ("29. Brain of the cell is called?", new String[]{"Nucleus", "Cytoplasm", "Vacuole", "Membrane"}, "Nucleus");
            addQ("30. Which gas do humans breathe out?", new String[]{"Oxygen", "Carbon Dioxide", "Hydrogen", "Argon"}, "Carbon Dioxide");
        }
    
    
    }

    private void addQ(String t, String[] o, String a) { questionList.add(new Question(t, o, a)); }

    class GradientBackground extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, GRAD_TOP, 0, getHeight(), GRAD_BOT));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    class RoundedPanel extends JPanel {
        private int r; Color c;
        RoundedPanel(int r, Color c) { this.r = r; this.c = c; setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
        }
    }

    class ModernButton extends JButton {
        private Color color;
        ModernButton(String text, Color c) {
            super(text); this.color = c;
            setOpaque(false); setContentAreaFilled(false); setBorderPainted(false);
            setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 16));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        public void setColor(Color c) { this.color = c; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            super.paintComponent(g2);
        }
    }

    class Question {
        private String text; private List<String> options; private String correctAns;
        public Question(String t, String[] o, String a) {
            this.text = t; this.correctAns = a;
            this.options = new ArrayList<>(Arrays.asList(o));
            Collections.shuffle(this.options);
        }
        public String getQuestionText() { return text; }
        public List<String> getOptions() { return options; }
        public String getCorrectAnswer() { return correctAns; }
    }
}