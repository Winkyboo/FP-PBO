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
import java.io.File;           
import java.io.IOException;    
import javax.imageio.ImageIO;  

public class GamePanel extends JPanel implements ActionListener {
    private static final int TILE_SIZE = 30;
    private static final int GRID_WIDTH = 32;
    private static final int GRID_HEIGHT = 24;
    private static final int SCREEN_WIDTH = TILE_SIZE * GRID_WIDTH;
    private static final int SCREEN_HEIGHT = TILE_SIZE * GRID_HEIGHT;
    
    private static final int MAX_SNAKE_LENGTH = GRID_WIDTH * GRID_HEIGHT;
    private static final int DELAY_SLOW = 200;
    private static final int DELAY_NORMAL = 150;
    private static final int DELAY_FAST = 80;

    private static final int STATE_MENU = 0;
    private static final int STATE_RUNNING = 1;
    private static final int STATE_GAMEOVER = 2;
    
    private int gameState = STATE_MENU;

    private final int[] x = new int[MAX_SNAKE_LENGTH];
    private final int[] y = new int[MAX_SNAKE_LENGTH];
    private int snakeLength = 5;
    private int foodX;
    private int foodY;
    private int score = 0;
    private char direction = 'R';
    private Timer timer;
    private Random random;
    
    private Image backgroundImage; 

    private final Color COLOR_BG = Color.decode("#212121"); 
    private final Color COLOR_HEAD = Color.decode("#00E676"); 
    private final Color COLOR_BODY = Color.decode("#69F0AE"); 
    private final Color COLOR_FOOD = Color.decode("#FF5252"); 
    private final Color COLOR_TEXT = Color.decode("#EEEEEE"); 
    private final Color COLOR_GRID = Color.decode("#303030");

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.random = new Random();
        try {
            backgroundImage = ImageIO.read(new File("background.jpg"));
        } catch (IOException e) {
            System.out.println("Foto tidak ditemukan.");
        }

        timer = new Timer(DELAY_NORMAL, this);
        timer.start();
    }

    public void startGame(int delay) {
        snakeLength = 5;
        score = 0;
        direction = 'R';
        gameState = STATE_RUNNING;
        timer.setDelay(delay);
        timer.restart();
        for (int i = 0; i < snakeLength; i++) {
            x[i] = (GRID_WIDTH / 2 - i) * TILE_SIZE;
            y[i] = (GRID_HEIGHT / 2) * TILE_SIZE;
        }
        spawnFood();
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

    public void checkCollision() {
        for (int i = snakeLength; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) gameState = STATE_GAMEOVER;
        }
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) gameState = STATE_GAMEOVER;
    }
    
    public void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            snakeLength++;
            score += 10;
            spawnFood();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == STATE_RUNNING) {
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

        if (gameState == STATE_MENU && backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
            
            g.setColor(new Color(0, 0, 0, 100)); 
            g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            
        } else {
            g.setColor(COLOR_BG); 
            g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            
            drawGrid(g); 
        }

        if (gameState == STATE_MENU) {
            drawMenu(g);
        } else if (gameState == STATE_RUNNING) {
            drawGame(g);
        } else if (gameState == STATE_GAMEOVER) {
            drawGame(g); 
            drawGameOverScreen(g);
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(COLOR_GRID);
        for (int i = 0; i <= SCREEN_WIDTH; i += TILE_SIZE) g.drawLine(i, 0, i, SCREEN_HEIGHT);
        for (int i = 0; i <= SCREEN_HEIGHT; i += TILE_SIZE) g.drawLine(0, i, SCREEN_WIDTH, i);
    }

    private void drawMenu(Graphics g) {
        g.setFont(new Font("Monospaced", Font.BOLD, 50));
        String title = "SNAKE GAME";
        FontMetrics metrics = getFontMetrics(g.getFont());
        int xPos = (SCREEN_WIDTH - metrics.stringWidth(title)) / 2;
        int yPos = SCREEN_HEIGHT / 3;
        
        g.setColor(Color.BLACK); 
        g.drawString(title, xPos + 4, yPos + 4); 
        g.setColor(COLOR_HEAD);
        g.drawString(title, xPos, yPos);
        
        g.setFont(new Font("SansSerif", Font.BOLD, 18)); 
        g.setColor(Color.WHITE);
        
        centerText(g, "Press '1' for SLOW", yPos + 60);
        centerText(g, "Press '2' for NORMAL", yPos + 90);
        centerText(g, "Press '3' for FAST", yPos + 120);
        
        g.setColor(Color.YELLOW);
        centerText(g, "[ Arrows to Move ]", yPos + 180);
    }

    private void drawGame(Graphics g) {
        g.setColor(COLOR_FOOD);
        g.fillOval(foodX + 2, foodY + 2, TILE_SIZE - 4, TILE_SIZE - 4); 

        for (int i = 0; i < snakeLength; i++) {
            if (i == 0) g.setColor(COLOR_HEAD); 
            else g.setColor(COLOR_BODY);
            g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE); 
            
            g.setColor(Color.BLACK);
            g.drawRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Score: " + score, 10, 20);
    }
    
    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180)); 
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        
        g.setColor(Color.RED);
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        centerText(g, "GAME OVER", SCREEN_HEIGHT / 2 - 20);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        centerText(g, "Final Score: " + score, SCREEN_HEIGHT / 2 + 20);
        
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        centerText(g, "Press SPACE to Return to Menu", SCREEN_HEIGHT / 2 + 60);
    }

    private void centerText(Graphics g, String text, int y) {
        FontMetrics metrics = getFontMetrics(g.getFont());
        int x = (SCREEN_WIDTH - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (gameState == STATE_MENU) {
                if (key == KeyEvent.VK_1) startGame(DELAY_SLOW);
                if (key == KeyEvent.VK_2) startGame(DELAY_NORMAL);
                if (key == KeyEvent.VK_3) startGame(DELAY_FAST);
            } else if (gameState == STATE_RUNNING) {
                switch (key) {
                    case KeyEvent.VK_LEFT:  if (direction != 'R') direction = 'L'; break;
                    case KeyEvent.VK_RIGHT: if (direction != 'L') direction = 'R'; break;
                    case KeyEvent.VK_UP:    if (direction != 'D') direction = 'U'; break;
                    case KeyEvent.VK_DOWN:  if (direction != 'U') direction = 'D'; break;
                }
            } else if (gameState == STATE_GAMEOVER) {
                if (key == KeyEvent.VK_SPACE) {
                    gameState = STATE_MENU;
                    repaint();
                }
            }
        }
    }
}