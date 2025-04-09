import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;


public class SnakeGame extends JPanel implements ActionListener {

    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int BOX_SIZE = 40;
    private final int NUM_BOXES_X = WIDTH / BOX_SIZE;
    private final int NUM_BOXES_Y = HEIGHT / BOX_SIZE;
    private final int INIT_LENGTH = 3;
    private final int NUM_APPLES = 1;

    private LinkedList<Point> snake;
    private List<Point> apples;
    private char direction;
    private boolean gameOver;
    private boolean botEnabled;

    private Timer timer;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        snake = new LinkedList<>();
        for (int i = 0; i < INIT_LENGTH; i++) {
            snake.add(new Point(NUM_BOXES_X / 2 - i, NUM_BOXES_Y / 2));
        }

        direction = 'R';
        apples = new LinkedList<>();
        generateApples();

        timer = new Timer(150, this);
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_W && direction != 'D') {
                    direction = 'U';
                } else if (key == KeyEvent.VK_S && direction != 'U') {
                    direction = 'D';
                } else if (key == KeyEvent.VK_A && direction != 'R') {
                    direction = 'L';
                } else if (key == KeyEvent.VK_D && direction != 'L') {
                    direction = 'R';
                } else if (key == KeyEvent.VK_B) {
                    botEnabled = !botEnabled;
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            if (botEnabled) {
                botMove();
            }
            moveSnake();
            checkCollision();
            repaint();
        }
    }

    private void moveSnake() {
        Point head = snake.getFirst();
        Point newHead = null;

        switch (direction) {
            case 'U':
                newHead = new Point(head.x, head.y - 1);
                break;
            case 'D':
                newHead = new Point(head.x, head.y + 1);
                break;
            case 'L':
                newHead = new Point(head.x - 1, head.y);
                break;
            case 'R':
                newHead = new Point(head.x + 1, head.y);
                break;
        }

        snake.addFirst(newHead);

        if (apples.contains(newHead)) {
            apples.remove(newHead);
            if (apples.size() < NUM_APPLES) {
                generateApple();
            }
        } else {
            snake.removeLast();
        }
    }

    private void generateApples() {
        while (apples.size() < NUM_APPLES) {
            generateApple();
        }
    }

    private void generateApple() {
        Random rand = new Random();
        Point newApple;
        do {
            int appleX = rand.nextInt(NUM_BOXES_X);
            int appleY = rand.nextInt(NUM_BOXES_Y);
            newApple = new Point(appleX, appleY);
        } while (snake.contains(newApple) || apples.contains(newApple));
        apples.add(newApple);
    }

    private void checkCollision() {
        Point head = snake.getFirst();

        if (head.x < 0 || head.x >= NUM_BOXES_X || head.y < 0 || head.y >= NUM_BOXES_Y) {
            gameOver = true;
        }

        if (snake.indexOf(head) != snake.lastIndexOf(head)) {
            gameOver = true;
        }
    }

    private void botMove() {
    Point head = snake.getFirst();
    List<Point> possibleMoves = getPossibleMoves(head);
    System.out.println(possibleMoves);
    Point targetApple = findNearestApple(head);
    
    if (targetApple != null) {
        Point bestMove = null;
        double minDistance = Double.MAX_VALUE;

        for (Point move : possibleMoves) {
            double distance = move.distance(targetApple);
            if (distance < minDistance) {
                minDistance = distance;
                bestMove = move;
            }
        }

        if (bestMove != null) {
            direction = determineDirection(head, bestMove).charAt(0);
        }
    }
}

    private Point findNearestApple(Point head) {
        Point nearest = null;
        double minDistance = Double.MAX_VALUE;
    
        for (Point apple : apples) {
            double distance = head.distance(apple);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = apple;
            }
        }
        return nearest;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        setBackground(Color.decode("#90EE90"));
        
        for (int y = 0; y < NUM_BOXES_Y; y++) {
            for (int x = 0; x < NUM_BOXES_X; x++) {
                if ((x + y) % 2 == 0) {
                    g.setColor(Color.decode("#90EE90"));
                } else {
                    g.setColor(Color.decode("#76C76A"));
                }
                g.fillRect(x * BOX_SIZE, y * BOX_SIZE, BOX_SIZE, BOX_SIZE);
            }
        }
        
        g.setColor(Color.decode("#0000FF"));
        for (Point p : snake) {
            g.fillRect(p.x * BOX_SIZE, p.y * BOX_SIZE, BOX_SIZE, BOX_SIZE);
        }
    
        g.setColor(Color.decode("#FF0000"));
        for (Point apple : apples) {
            g.fillRect(apple.x * BOX_SIZE, apple.y * BOX_SIZE, BOX_SIZE, BOX_SIZE);
        }
    
        if (gameOver) {
            g.setColor(Color.decode("#FFFFFF"));
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String message = "Game Over! Press R to Restart";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(message, (WIDTH - fm.stringWidth(message)) / 2, HEIGHT / 2);
        }
    }
        
    private void restartGame() {
        snake.clear();
        for (int i = 0; i < INIT_LENGTH; i++) {
            snake.add(new Point(NUM_BOXES_X / 2 - i, NUM_BOXES_Y / 2));
        }
        direction = 'R';
        apples.clear();
        generateApples();
        gameOver = false;
    }
    
    private List<Point> getPossibleMoves(Point head) {
        List<ArrayList<Point>> moves = new ArrayList<>();
        List<Point> lastMoves = new ArrayList<>();
        List<Point> largestMoveList = new ArrayList<>();

        
        for (int i = 0; i < 10; i++) {
            
            Point up = new Point(head.x, head.y - i);
            Point down = new Point(head.x, head.y + i);
            Point left = new Point(head.x - i, head.y);
            Point right = new Point(head.x + i, head.y);
            
            if (isValidMove(up)) lastMoves.add(up);
            if (isValidMove(down)) lastMoves.add(down);
            if (isValidMove(left)) lastMoves.add(left);
            if (isValidMove(right)) lastMoves.add(right);
            
            moves.add(new ArrayList<>(lastMoves));
            System.out.println(moves);
            lastMoves.clear();
        }
        
        int maxSize = 0;
        
        for (ArrayList<Point> moveList : moves) {
            if (moveList.size() > maxSize) {
                largestMoveList = moveList;
                maxSize = moveList.size();
            }
        }
    
        return largestMoveList;
    }
    
    private boolean isValidMove(Point move) {
        return move.x >= 0 && move.x < NUM_BOXES_X && move.y >= 0 && move.y < NUM_BOXES_Y && !snake.contains(move);
    }
    
    private String determineDirection(Point head, Point move) {
        if (move.x > head.x) return "R";
        if (move.x < head.x) return "L";
        if (move.y > head.y) return "D";
        return "U";
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R && game.gameOver) {
                    game.restartGame();
                }
            }
        });
    }
    
    private void calculateNearestWayToApple() {
        
    }
}
