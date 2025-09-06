import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;

public class GamblingApp {

    public static void main(String[] args) {
        // Create and show the login/registration page
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
        });
    }
}

class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Map<String, String> users; // To store user data in memory
    private static final String USER_FILE = "users.txt"; // File to store user data

    public LoginPage() {
        // Load existing users from the file
        users = loadUsers();

        // Set up the frame
        setTitle("Login/Registration");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Load the background image from URL
        try {
            URL imageUrl = new URL("https://ichef.bbci.co.uk/news/976/cpsprodpb/8288/production/_117761433_hi051049438.jpg");
            Image backgroundImage = ImageIO.read(imageUrl);
            ImageIcon backgroundIcon = new ImageIcon(backgroundImage.getScaledInstance(800, 600, Image.SCALE_SMOOTH));
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setLayout(new GridBagLayout());

            // Create a panel for the login/registration form
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new GridBagLayout());
            formPanel.setOpaque(false); // Make the panel transparent

            // Set up constraints for GridBagLayout
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10); // Add padding

            // Add components to the form panel
            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setFont(new Font("Serif", Font.BOLD, 18));
            usernameLabel.setForeground(Color.WHITE); // Set text color
            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(usernameLabel, gbc);

            usernameField = new JTextField(20);
            usernameField.setFont(new Font("Serif", Font.PLAIN, 18));
            gbc.gridx = 1;
            gbc.gridy = 0;
            formPanel.add(usernameField, gbc);

            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setFont(new Font("Serif", Font.BOLD, 18));
            passwordLabel.setForeground(Color.WHITE); // Set text color
            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(passwordLabel, gbc);

            passwordField = new JPasswordField(20);
            passwordField.setFont(new Font("Serif", Font.PLAIN, 18));
            gbc.gridx = 1;
            gbc.gridy = 1;
            formPanel.add(passwordField, gbc);

            JButton loginButton = new JButton("Login");
            loginButton.setFont(new Font("Serif", Font.BOLD, 18));
            loginButton.setBackground(new Color(0, 153, 76)); // Green color
            loginButton.setForeground(Color.WHITE); // White text
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    login();
                }
            });
            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(loginButton, gbc);

            JButton registerButton = new JButton("Register");
            registerButton.setFont(new Font("Serif", Font.BOLD, 18));
            registerButton.setBackground(new Color(0, 102, 204)); // Blue color
            registerButton.setForeground(Color.WHITE); // White text
            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    register();
                }
            });
            gbc.gridx = 1;
            gbc.gridy = 2;
            formPanel.add(registerButton, gbc);

            // Add the form panel to the background label
            backgroundLabel.add(formPanel);

            // Add the background label to the frame
            add(backgroundLabel);

            // Make the frame visible
            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load background image!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to handle login
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if the user exists and the password matches
        if (users.containsKey(username) && users.get(username).equals(password)) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            openMainPage(username);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to handle registration
    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if the user already exists
        if (users.containsKey(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add the new user to the map and save to file
        users.put(username, password);
        saveUsers();

        // Create a new user details file with default wallet balance
        createUserDetailsFile(username);

        JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        openMainPage(username);
    }

    // Method to open the main page
    private void openMainPage(String username) {
        // Close the login/registration page
        this.dispose();

        // Open the main page
        MainPage mainPage = new MainPage(username);
        mainPage.setVisible(true);
    }

    // Method to load users from the file
    private Map<String, String> loadUsers() {
        Map<String, String> userMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    userMap.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            // If the file doesn't exist, it will be created when a user registers
            System.out.println("No user file found. A new one will be created.");
        }
        return userMap;
    }

    // Method to save users to the file
    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to create a user details file with default wallet balance
    private void createUserDetailsFile(String username) {
        String userDetailsFile = username + "_details.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userDetailsFile))) {
            writer.write("WalletBalance:1000");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class MainPage extends JFrame {
    private String username;
    private int walletBalance;
    private JLabel balanceLabel;

    public MainPage(String username) {
        this.username = username;
        loadUserDetails();

        // Set up the frame
        setTitle("TRIPLE P CASINO - Main Page");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Load the background image from URL
        try {
            URL imageUrl = new URL("https://imgix.ranker.com/list_img_v2/8563/1988563/original/gambling-apps-u1?w=817&h=427&fm=jpg&q=50&fit=crop?fm=pjpg&q=80");
            Image backgroundImage = ImageIO.read(imageUrl);
            ImageIcon backgroundIcon = new ImageIcon(backgroundImage.getScaledInstance(1200, 800, Image.SCALE_SMOOTH));
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setLayout(new BorderLayout());

            // Create a panel for the main content
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.setOpaque(false); // Make the panel transparent

            // Add header section
            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
            headerPanel.setOpaque(false);

            JLabel casinoName = new JLabel("TRIPLE P CASINO");
            casinoName.setFont(new Font("Serif", Font.BOLD, 36));
            casinoName.setForeground(Color.YELLOW);
            headerPanel.add(casinoName);

            JLabel userLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
            userLabel.setFont(new Font("Serif", Font.BOLD, 24));
            userLabel.setForeground(new Color(255, 215, 0)); // Gold color
            headerPanel.add(userLabel);

            balanceLabel = new JLabel("Wallet Balance: " + walletBalance + " points", SwingConstants.CENTER);
            balanceLabel.setFont(new Font("Serif", Font.BOLD, 24));
            balanceLabel.setForeground(new Color(0, 255, 0)); // Green color
            headerPanel.add(balanceLabel);

            // Add logout button
            JButton logoutButton = new JButton("Logout");
            logoutButton.setFont(new Font("Serif", Font.BOLD, 14));
            logoutButton.setBackground(Color.RED);
            logoutButton.setForeground(Color.WHITE);
            logoutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    logout();
                }
            });
            headerPanel.add(logoutButton);

            mainPanel.add(headerPanel, BorderLayout.NORTH);

            // Add game categories section
            JPanel gameCategoriesPanel = new JPanel();
            gameCategoriesPanel.setLayout(new GridLayout(2, 2, 10, 10));
            gameCategoriesPanel.setOpaque(false);

            addGameButton(gameCategoriesPanel, "Dice Game", "https://tse2.mm.bing.net/th?id=OIP.1LWMErr1TEX4ZkP4sKT43AHaGX&pid=Api&P=0&h=180");
            addGameButton(gameCategoriesPanel, "Slot Machine", "https://png.pngtree.com/png-clipart/20211116/original/pngtree-slot-logo-rhombus-background-png-image_6944963.png");
            addGameButton(gameCategoriesPanel, "Tic-Tac-Toe", "https://cdn-icons-png.flaticon.com/512/10199/10199746.png");
            addGameButton(gameCategoriesPanel, "Number Guess", "https://tse4.mm.bing.net/th?id=OIP.3LW_9JzguTfZcGxcK3PbIAAAAA&pid=Api&P=0&h=180");

            mainPanel.add(gameCategoriesPanel, BorderLayout.CENTER);

            // Add the main panel to the background label
            backgroundLabel.add(mainPanel, BorderLayout.CENTER);

            // Add the background label to the frame
            add(backgroundLabel);

            // Make the frame visible
            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load background image!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to add a game button with a logo
    private void addGameButton(JPanel panel, String gameName, String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            ImageIcon gameIcon = new ImageIcon(ImageIO.read(url));
            Image scaledImage = gameIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Resize image
            gameIcon = new ImageIcon(scaledImage);
            JButton gameButton = new JButton(gameName, gameIcon);
            gameButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            gameButton.setHorizontalTextPosition(SwingConstants.CENTER);
            gameButton.setFont(new Font("Serif", Font.BOLD, 18));
            gameButton.setForeground(Color.WHITE);
            gameButton.setBackground(new Color(0, 102, 204)); // Blue color
            gameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    askForPoints(gameName);
                }
            });
            panel.add(gameButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to ask the user for points before playing the game
    private void askForPoints(String gameName) {
        String input = JOptionPane.showInputDialog(this, "Enter points to play " + gameName + ":");
        try {
            int points = Integer.parseInt(input);
            if (points > walletBalance) {
                JOptionPane.showMessageDialog(this, "Not enough points in your wallet!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean won = playGame(gameName);
            if (won) {
                walletBalance += points; // Double the points if won
                JOptionPane.showMessageDialog(this, "You won! Your points have been doubled.", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
            } else {
                walletBalance -= points; // Deduct points if lost
                JOptionPane.showMessageDialog(this, "You lost! Points deducted from your wallet.", "Sorry", JOptionPane.INFORMATION_MESSAGE);
            }
            balanceLabel.setText("Wallet Balance: " + walletBalance + " points");
            saveUserDetails();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to play the selected game
    private boolean playGame(String gameName) {
        switch (gameName) {
            case "Dice Game":
                return new DiceGame().play();
            case "Slot Machine":
                return new SlotMachineGame().play();
            case "Tic-Tac-Toe":
                return new TicTacToeGame().play();
            case "Number Guess":
                return new NumberGuessGame().play();
            default:
                return false;
        }
    }

    // Method to load user details from file
    private void loadUserDetails() {
        String userDetailsFile = username + "_details.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(userDetailsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("WalletBalance:")) {
                    walletBalance = Integer.parseInt(line.split(":")[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to save user details to file
    private void saveUserDetails() {
        String userDetailsFile = username + "_details.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userDetailsFile))) {
            writer.write("WalletBalance:" + walletBalance);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to logout and return to login/registration page
    private void logout() {
        this.dispose(); // Close the main page
        new LoginPage().setVisible(true); // Open the login page
    }
}

class DiceGame extends JFrame {
    public boolean play() {
        setTitle("Dice Game");
        setSize(400, 300); // Smaller window size
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load background image
        try {
            URL imageUrl = new URL("https://ichef.bbci.co.uk/news/976/cpsprodpb/8288/production/_117761433_hi051049438.jpg");
            Image backgroundImage = ImageIO.read(imageUrl);
            ImageIcon backgroundIcon = new ImageIcon(backgroundImage.getScaledInstance(400, 300, Image.SCALE_SMOOTH));
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setLayout(new GridBagLayout());

            // Add game components
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new GridBagLayout());

            JLabel label = new JLabel("Guess a number between 1 and 6:");
            label.setFont(new Font("Serif", Font.BOLD, 16));
            label.setForeground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 10);
            panel.add(label, gbc);

            JTextField guessField = new JTextField(10);
            guessField.setFont(new Font("Serif", Font.PLAIN, 16));
            gbc.gridy = 1;
            panel.add(guessField, gbc);

            JButton submitButton = new JButton("Submit");
            submitButton.setFont(new Font("Serif", Font.BOLD, 16));
            submitButton.setBackground(new Color(0, 102, 204));
            submitButton.setForeground(Color.WHITE);
            gbc.gridy = 2;
            panel.add(submitButton, gbc);

            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int userGuess = Integer.parseInt(guessField.getText());
                    int diceResult = new Random().nextInt(6) + 1;
                    if (userGuess == diceResult) {
                        JOptionPane.showMessageDialog(DiceGame.this, "You won! The dice rolled " + diceResult, "Result", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(DiceGame.this, "You lost! The dice rolled " + diceResult, "Result", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    }
                }
            });

            backgroundLabel.add(panel);
            add(backgroundLabel);
            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false; // Assume the user didn't win for simplicity
    }
}

class SlotMachineGame extends JFrame {
    public boolean play() {
        setTitle("Slot Machine");
        setSize(400, 300); // Smaller window size
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load background image
        try {
            URL imageUrl = new URL("https://ichef.bbci.co.uk/news/976/cpsprodpb/8288/production/_117761433_hi051049438.jpg");
            Image backgroundImage = ImageIO.read(imageUrl);
            ImageIcon backgroundIcon = new ImageIcon(backgroundImage.getScaledInstance(400, 300, Image.SCALE_SMOOTH));
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setLayout(new GridBagLayout());

            // Add game components
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new GridBagLayout());

            JLabel label = new JLabel("Spin the slot machine:");
            label.setFont(new Font("Serif", Font.BOLD, 16));
            label.setForeground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 10);
            panel.add(label, gbc);

            JButton spinButton = new JButton("Spin");
            spinButton.setFont(new Font("Serif", Font.BOLD, 16));
            spinButton.setBackground(new Color(0, 102, 204));
            spinButton.setForeground(Color.WHITE);
            gbc.gridy = 1;
            panel.add(spinButton, gbc);

            spinButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int result1 = new Random().nextInt(3) + 1;
                    int result2 = new Random().nextInt(3) + 1;
                    int result3 = new Random().nextInt(3) + 1;
                    JOptionPane.showMessageDialog(SlotMachineGame.this, "Results: " + result1 + " | " + result2 + " | " + result3, "Slot Result", JOptionPane.INFORMATION_MESSAGE);
                    if (result1 == result2 && result2 == result3) {
                        dispose();
                    } else {
                        dispose();
                    }
                }
            });

            backgroundLabel.add(panel);
            add(backgroundLabel);
            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false; // Assume the user didn't win for simplicity
    }
}

class NumberGuessGame extends JFrame {
    public boolean play() {
        setTitle("Number Guess");
        setSize(400, 300); // Smaller window size
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load background image
        try {
            URL imageUrl = new URL("https://ichef.bbci.co.uk/news/976/cpsprodpb/8288/production/_117761433_hi051049438.jpg");
            Image backgroundImage = ImageIO.read(imageUrl);
            ImageIcon backgroundIcon = new ImageIcon(backgroundImage.getScaledInstance(400, 300, Image.SCALE_SMOOTH));
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setLayout(new GridBagLayout());

            // Add game components
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new GridBagLayout());

            JLabel label = new JLabel("Guess a number between 1 and 10:");
            label.setFont(new Font("Serif", Font.BOLD, 16));
            label.setForeground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 10);
            panel.add(label, gbc);

            JTextField guessField = new JTextField(10);
            guessField.setFont(new Font("Serif", Font.PLAIN, 16));
            gbc.gridy = 1;
            panel.add(guessField, gbc);

            JButton submitButton = new JButton("Submit");
            submitButton.setFont(new Font("Serif", Font.BOLD, 16));
            submitButton.setBackground(new Color(0, 102, 204));
            submitButton.setForeground(Color.WHITE);
            gbc.gridy = 2;
            panel.add(submitButton, gbc);

            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int userGuess = Integer.parseInt(guessField.getText());
                    int targetNumber = new Random().nextInt(10) + 1;
                    if (userGuess == targetNumber) {
                        JOptionPane.showMessageDialog(NumberGuessGame.this, "You won! The number was " + targetNumber, "Result", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(NumberGuessGame.this, "You lost! The number was " + targetNumber, "Result", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    }
                }
            });

            backgroundLabel.add(panel);
            add(backgroundLabel);
            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false; // Assume the user didn't win for simplicity
    }
}

class TicTacToeGame extends JFrame {
    private JButton[] buttons = new JButton[9];
    private boolean xTurn = true;

    public boolean play() {
        setTitle("Tic-Tac-Toe");
        setSize(400, 400); // Smaller window size
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load background image
        try {
            URL imageUrl = new URL("https://ichef.bbci.co.uk/news/976/cpsprodpb/8288/production/_117761433_hi051049438.jpg");
            Image backgroundImage = ImageIO.read(imageUrl);
            ImageIcon backgroundIcon = new ImageIcon(backgroundImage.getScaledInstance(400, 400, Image.SCALE_SMOOTH));
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setLayout(new GridBagLayout());

            // Add game components
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new GridLayout(3, 3));

            for (int i = 0; i < 9; i++) {
                buttons[i] = new JButton();
                buttons[i].setFont(new Font("Serif", Font.BOLD, 48));
                buttons[i].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton button = (JButton) e.getSource();
                        if (xTurn) {
                            button.setText("X");
                        } else {
                            button.setText("O");
                        }
                        xTurn = !xTurn;
                        button.setEnabled(false);
                        checkForWinner();
                    }
                });
                panel.add(buttons[i]);
            }

            backgroundLabel.add(panel);
            add(backgroundLabel);
            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false; // Assume the user didn't win for simplicity
    }

    private void checkForWinner() {
        // Check rows
        for (int i = 0; i < 9; i += 3) {
            if (buttons[i].getText().equals(buttons[i + 1].getText()) && buttons[i].getText().equals(buttons[i + 2].getText()) && !buttons[i].isEnabled()) {
                JOptionPane.showMessageDialog(this, buttons[i].getText() + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                resetGame();
                return;
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (buttons[i].getText().equals(buttons[i + 3].getText()) && buttons[i].getText().equals(buttons[i + 6].getText()) && !buttons[i].isEnabled()) {
                JOptionPane.showMessageDialog(this, buttons[i].getText() + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                resetGame();
                return;
            }
        }

        // Check diagonals
        if (buttons[0].getText().equals(buttons[4].getText()) && buttons[0].getText().equals(buttons[8].getText()) && !buttons[0].isEnabled()) {
            JOptionPane.showMessageDialog(this, buttons[0].getText() + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
            return;
        }
        if (buttons[2].getText().equals(buttons[4].getText()) && buttons[2].getText().equals(buttons[6].getText()) && !buttons[2].isEnabled()) {
            JOptionPane.showMessageDialog(this, buttons[2].getText() + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
            return;
        }

        // Check for draw
        boolean draw = true;
        for (int i = 0; i < 9; i++) {
            if (buttons[i].isEnabled()) {
                draw = false;
                break;
            }
        }
        if (draw) {
            JOptionPane.showMessageDialog(this, "It's a draw!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
        }
    }

    private void resetGame() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setText("");
            buttons[i].setEnabled(true);
        }
        xTurn = true;
    }
}