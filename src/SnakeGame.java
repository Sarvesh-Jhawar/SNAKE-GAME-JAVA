import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    // Inner class representing a tile on the game board
    private class Tile {
        int x; // x-coordinate of the tile
        int y; // y-coordinate of the tile

        // Constructor to initialize the tile's position
        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private int boardWidth; // Width of the game board
    private int boardHeight; // Height of the game board

    // Snake components
    Tile snakeHead; // The head of the snake
    ArrayList<Tile> snakeBody; // The body segments of the snake

    // Food component
    Tile food; // The food that the snake can eat

    // Random number generator for food placement
    Random random;

    // Game logic components
    Timer gameLoop; // Timer for the game loop
    int velocityX; // Horizontal movement velocity of the snake
    int velocityY; // Vertical movement velocity of the snake
    boolean gameOver = false; // Flag to indicate if the game is over
    int tileSize = 25; // Size of each tile in pixels

    // Constructor to initialize the game
    SnakeGame(int boardWidth, int boardHeight) {
        this.boardHeight = boardHeight; // Set the board height
        this.boardWidth = boardWidth; // Set the board width
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight)); // Set the preferred size of the panel
        setBackground(Color.black); // Set the background color to black

        // Add key listener to capture key presses
        addKeyListener(this);
        setFocusable(true); // Make the panel focusable to receive key events

        // Initialize the snake's head and body
        snakeHead = new Tile(5, 5); // Set initial position of the snake head
        snakeBody = new ArrayList<Tile>(); // Initialize the snake body as an empty list
        food = new Tile(10, 10); // Set initial position of the food
        random = new Random(); // Initialize the random number generator
        placeFood(); // Place the food at a random location

        // Set initial movement velocity
        velocityX = 0; // No horizontal movement
        velocityY = 0; // No vertical movement

        // Initialize the game loop timer
        gameLoop = new Timer(100, this); // Timer triggers every 100 milliseconds

        gameLoop.start(); // Start the game loop
        //        Every 100 milliseconds, the timer will trigger an action event,
        //        which will call the actionPerformed method of the SnakeGame class.
    }

    // Method to paint the component
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the superclass method to clear the panel
        draw(g); // Call the draw method to render the game elements
    }

    // Method to draw the game elements
    public void draw(Graphics g) {
        // Draw the grid (commented out)
        // for(int i = 0; i < boardWidth / tileSize; i++) {
        //     g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
        //     g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        // }

        // Draw the food
        g.setColor(Color.red); // Set color to red for the food
        // g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize); // Draw food as a rectangle
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true); // Draw food as a 3D rectangle
        //public void fill3DRect(int x, int y, int width, int height,boolean raised)
        // Draw the snake head
        g.setColor(Color.green); // Set color to green for the snake head
        // g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize); // Draw snake head as a rectangle
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true); // Draw snake head as a 3D rectangle

        // Draw the snake body
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i); // Get the current body segment
            // g.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize); // Draw body segment as a rectangle
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true); // Draw body segment as a 3D rectangle
        }

        // Draw the score
        g.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font for the score display
        if (gameOver) {
            g.setColor(Color.red); // Set color to red if the game is over
            g.drawString("Game Over : " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize); // Display game over message
        } else {
            g.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize); // Display current score
        }
    }

    // Method to place food at a random location on the board
    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize); // Random x position within board width
        food.y = random.nextInt(boardHeight / tileSize); // Random y position within board height
    }

    // Method to check for collision between two tiles
    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y; // Return true if both tiles occupy the same position
    }

    // Method to move the snake and handle game logic
    public void move() {
        // Check for collision with food
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y)); // Add a new body segment at the food's position
            placeFood(); // Place new food at a random location
        }

        // Move the snake body segments
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i); // Get the current body segment
            if (i == 0) {
                // Move the first body segment to the head's position
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                // Move each segment to the position of the segment in front of it
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Update the position of the snake head
        snakeHead.x += velocityX; // Update x position based on velocity
        snakeHead.y += velocityY; // Update y position based on velocity

        // Check for game over conditions
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i); // Get the current body segment
            // Check for collision with the snake body
            if (collision(snakeHead, snakePart)) {
                gameOver = true; // Set game over flag
            }
        }

        // Check for collision with walls
        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize > boardWidth
                || snakeHead.y * tileSize < 0 || snakeHead.y * tileSize > boardHeight) {
            gameOver = true; // Set game over flag if the head goes out of bounds
        }
    }

    // Method to handle key presses
    @Override
    public void keyPressed(KeyEvent e) {
        // Change direction based on key pressed
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0; // No horizontal movement
            velocityY = -1; // Move up
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0; // No horizontal movement
            velocityY = 1; // Move down
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1; // Move left
            velocityY = 0; // No vertical movement
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1; // Move right
            velocityY = 0; // No vertical movement
        }
    }

    // Unused method from KeyListener interface
    @Override
    public void keyTyped(KeyEvent e) {
        // No implementation needed
    }

    // Unused method from KeyListener interface
    @Override
    public void keyReleased(KeyEvent e) {
        // No implementation needed
    }

    // Method called by the timer to update the game state
    @Override
    public void actionPerformed(ActionEvent e) {
        move(); // Update the game state
        repaint(); // Call the draw method to render the updated game state
        if (gameOver) {
            gameLoop.stop(); // Stop the game loop if the game is over
        }
    }
}
