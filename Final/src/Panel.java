import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Panel extends JPanel implements ActionListener {
    GameData gameData;
    int score;
    int height;
    int width;
    int size = 50;

    Character character;
    Coins coin;
    Enemy enemy;

    Random random;
    Timer timer;
    int x_velocity;
    int y_velocity;
    boolean dead = false;
    Image background;

    Panel(int height, int width) {
        this.height = height;
        this.width = width;
        setPreferredSize(new Dimension(this.width, this.height));
        setBackground(Color.black);
        addKeyListener(new movement());
        setFocusable(true);

        // Load images and entities
        try {
            background = new ImageIcon(getClass().getResource("/assets/Background.png")).getImage();
            character = new Character(6, 11, new ImageIcon(getClass().getResource("/assets/Character.png")).getImage());
            coin = new Coins(10, 10, new ImageIcon(getClass().getResource("/assets/coin.png")).getImage());
            enemy = new Enemy(2, 2, new ImageIcon(getClass().getResource("/assets/enemy.png")).getImage());
        } catch (Exception e) {
            System.out.println("Error loading images: " + e.getMessage());
        }

        random = new Random();
        gameData = new GameData();

        // Timer for game loop
        timer = new Timer(100, this);
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        create(g);
    }

    public void create(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        // Background
        g2D.drawImage(background, 0, 0, null);

        // Render entities
        coin.render(g, size);
        character.render(g, size);
        enemy.render(g, size);

        // Score display
        g2D.setFont(new Font("Times New Roman", Font.PLAIN, 25));
        Color gold = new Color(255, 215, 0);
        g.setColor(gold);
        g.drawString("Score: " + score, size - 45, size - 30);
    }

    public void coin_spawn() {
        coin.setX(random.nextInt(width / size));
        coin.setY(random.nextInt(height / size));
    }

    public boolean collision_detect(GameEntity entity1, GameEntity entity2) {
        return entity1.getX() == entity2.getX() && entity1.getY() == entity2.getY();
    }

    public void move() {
        // Collect coins
        if (collision_detect(character, coin)) {
            score++;
            gameData.saveGame(score, character.getX(), character.getY());
            coin_spawn();
        }

        // Enemy collision
        if (collision_detect(character, enemy)) {
            dead = true;
        }

        // Movement
        int x = character.getX() + x_velocity;
        int y = character.getY() + y_velocity;

        // Boundary checks
        if (x < 0) character.setX(0);
        else if (x >= width / size) character.setX(width / size - 1);
        else character.setX(x);

        if (y < 0) character.setY(0);
        else if (y >= height / size) character.setY(height / size - 1);
        else character.setY(y);

        // Enemy follows character
        int enemySpeed = 1;
        if (Math.abs(character.getX() - enemy.getX()) > enemySpeed) {
            enemy.setX(enemy.getX() + (character.getX() > enemy.getX() ? enemySpeed : -enemySpeed));
        }
        if (Math.abs(character.getY() - enemy.getY()) > enemySpeed) {
            enemy.setY(enemy.getY() + (character.getY() > enemy.getY() ? enemySpeed : -enemySpeed));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (dead) {
            timer.stop();
            showGameOverScreen();
        }
    }

    private void showGameOverScreen() {
        GameData gameData = new GameData();
        int highestScore = gameData.loadHighestScore();

        if (score > highestScore) {
            highestScore = score;
            gameData.saveHighestScore(highestScore); // Save the highest score
        }

        // Create a dialog for Game Over
        JDialog gameOverDialog = new JDialog();
        gameOverDialog.setTitle("Game Over");
        gameOverDialog.setSize(300, 300);
        gameOverDialog.setLayout(new BorderLayout());
        gameOverDialog.setLocationRelativeTo(this);

        // Game Over label
        JLabel gameOverLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gameOverDialog.add(gameOverLabel, BorderLayout.NORTH);

        // Score and high score panel
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(2, 1));
        JLabel scoreLabel = new JLabel("Your Score: " + score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel highScoreLabel = new JLabel("Highest Score: " + highestScore, SwingConstants.CENTER);
        highScoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        scorePanel.add(scoreLabel);
        scorePanel.add(highScoreLabel);
        gameOverDialog.add(scorePanel, BorderLayout.CENTER);

        // Button panel with aligned buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton restartButton = new JButton("Restart");
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartButton.addActionListener(e -> {
            gameOverDialog.dispose();
            restartGame();
        });

        JButton exitButton = new JButton("Exit");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons with spacing
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(restartButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        buttonPanel.add(exitButton);
        buttonPanel.add(Box.createVerticalGlue());

        gameOverDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Display dialog
        gameOverDialog.setModal(true);
        gameOverDialog.setVisible(true);
    }


    private void restartGame() {
        // Reset game variables
        score = 0;
        dead = false;
        character.x = 6;
        character.y = 11;
        x_velocity = 0;
        y_velocity = 0;
        coin_spawn();
        enemy.x = 2;
        enemy.y = 2;

        // Restart the timer
        timer.start();
        repaint();
    }


    private class movement extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W -> {
                    x_velocity = 0;
                    y_velocity = -1;
                }
                case KeyEvent.VK_A -> {
                    x_velocity = -1;
                    y_velocity = 0;
                }
                case KeyEvent.VK_S -> {
                    x_velocity = 0;
                    y_velocity = 1;
                }
                case KeyEvent.VK_D -> {
                    x_velocity = 1;
                    y_velocity = 0;
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            x_velocity = 0;
            y_velocity = 0;
        }
    }
}
