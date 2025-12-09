/**
 * Kode ini dibuat oleh:
 * Fauzan Hafiz Amandani (5025241087)
 * Willy Marcelius (5025241096)
 * Adriel Mahira Dharma (5025241097)
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private static final int TILE_SIZE = 25;
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final int SCREEN_WIDTH = TILE_SIZE * GRID_WIDTH;
    private static final int SCREEN_HEIGHT = TILE_SIZE * GRID_HEIGHT;
    
    private static final int MAX_SNAKE_LENGTH = GRID_WIDTH * GRID_HEIGHT;
    private static final int DELAY_SLOW = 200;
    private static final int DELAY_NORMAL = 150;
    private static final int DELAY_FAST = 100;

    private final int[] x = new int[MAX_SNAKE_LENGTH];
    private final int[] y = new int[MAX_SNAKE_LENGTH];

    private int snakeLength = 5;
    private int foodX;
    private int foodY;
    private int score = 0;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private Random random;

    private final Color COLOR_BG = Color.decode("#212121"); 
    private final Color COLOR_HEAD = Color.decode("#00E676"); 
    private final Color COLOR_BODY = Color.decode("#69F0AE");
    private final Color COLOR_FOOD = Color.decode("#FF5252"); 
    private final Color COLOR_TEXT = Color.decode("#EEEEEE");

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(COLOR_BG);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.random = new Random();
        
        showStartScreen();
    }

    private void showStartScreen() {
        String[] options = {"Slow", "Normal", "Fast"};
        int choice = JOptionPane.showOptionDialog(null, "Choose Speed:", "Snake Game",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);

        int speedDelay;
        switch (choice) {
            case 0:  speedDelay = DELAY_SLOW; break;
            case 1:  speedDelay = DELAY_NORMAL; break;
            case 2:  speedDelay = DELAY_FAST; break;
            default: speedDelay = DELAY_NORMAL; break;
        }

        startGame(speedDelay);
    }

    public void startGame(int delay) {
        snakeLength = 5;
        score = 0;
        direction = 'R';

        for (int i = 0; i < snakeLength; i++) {
            x[i] = (GRID_WIDTH / 2 - i) * TILE_SIZE;
            y[i] = (GRID_HEIGHT / 2) * TILE_SIZE;
        }

        spawnFood();
        running = true;

        if (timer != null) timer.stop();
        timer = new Timer(delay, this);
        timer.start();
        
        this.requestFocusInWindow(); 
    }

    public void spawnFood() {
        foodX = random.nextInt(GRID_WIDTH) * TILE_SIZE;
        foodY = random.nextInt(GRID_HEIGHT) * TILE_SIZE;
    }

    public void move() {
        for (int i = snakeLength; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U': y[0] -= TILE_SIZE; break;
            case 'D': y[0] += TILE_SIZE; break;
            case 'L': x[0] -= TILE_SIZE; break;
            case 'R': x[0] += TILE_SIZE; break;
        }
    }

    public void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            snakeLength++;
            score += 10;
            spawnFood();
        }
    }

    public void checkCollision() {
        for (int i = snakeLength; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (running) {
            g.setColor(COLOR_FOOD);
            g.fillOval(foodX + 2, foodY + 2, TILE_SIZE - 4, TILE_SIZE - 4); 

            for (int i = 0; i < snakeLength; i++) {
                if (i == 0) {
                    g.setColor(COLOR_HEAD); 
                } else {
                    g.setColor(COLOR_BODY); 
                }
                g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE); 
            }

            g.setColor(COLOR_TEXT);
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + score, (SCREEN_WIDTH - metrics.stringWidth("Score: " + score)) / 2, g.getFont().getSize());
        
        } else {
            gameOver(g);
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(COLOR_TEXT);
        g.setFont(new Font("SansSerif", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        String text = "Game Over";
        g.drawString(text, (SCREEN_WIDTH - metrics1.stringWidth(text)) / 2, SCREEN_HEIGHT / 2 - 20);

        g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        String scoreText = "Final Score: " + score;
        g.drawString(scoreText, (SCREEN_WIDTH - metrics2.stringWidth(scoreText)) / 2, SCREEN_HEIGHT / 2 + 20);
        
        String restartText = "Press SPACE to Restart";
        g.drawString(restartText, (SCREEN_WIDTH - metrics2.stringWidth(restartText)) / 2, SCREEN_HEIGHT / 2 + 60);
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
                case KeyEvent.VK_SPACE:
                    if (!running) {
                        showStartScreen(); 
                    }
                    break;
            }
        }
    }
}