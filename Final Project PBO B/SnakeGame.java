/**
 * Kode ini dibuat oleh:
 * Fauzan Hafiz Amandani (5025241087)
 * Willy Marcelius (5025241096)
 * Adriel Mahira Dharma (5025241097)
 */
import javax.swing.JFrame;

public class SnakeGame {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
   
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack(); 
        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }
}