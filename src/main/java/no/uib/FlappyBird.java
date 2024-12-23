package no.uib;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;
    Image backgroundImage;
    Image birdImage;
    Image topPipeImage;
    Image bottomPipeImage;
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;
    int velocityY = 0; // vertical velocity
    int gravity = 1; // gravity value
    int velocityX = -4; // negative value to move the pipes to the left

    public Bird bird;
    Timer gameLoop;
    Timer placePipeTimer;

    // Can dynamically add/remove pipes to this list.
    ArrayList<Pipe> pipes;
    Random random = new Random();

    int gameState = 0; // 0 = Start screen, 1 = Playing, 2 = Game Over
    double score = 0;

    JButton startButton;
    JButton restartButton;

    // Constructor
    public FlappyBird() {
        this.setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        loadImages();
        bird = new Bird(birdImage);
        pipes = new ArrayList<Pipe>();

        startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.PLAIN, 20));
        startButton.setBounds(boardWidth / 2 - 50, boardHeight / 2, 100, 40); // Center the button
        startButton.addActionListener(e -> startGame());
        this.setLayout(null);
        this.add(startButton);

        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.PLAIN, 20));
        restartButton.setBounds(boardWidth / 2 - 50, boardHeight / 2 + 50, 100, 40); // Center the button
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> restartGame());
        this.setLayout(null);
        this.add(restartButton);

        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipeTimer.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

    }

    private void loadImages() {
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/flappybirdbg.png"));
            birdImage = ImageIO.read(getClass().getResourceAsStream("/flappybird.png"));
            topPipeImage = ImageIO.read(getClass().getResourceAsStream("/toppipe.png"));
            bottomPipeImage = ImageIO.read(getClass().getResourceAsStream("/bottompipe.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading images");
        }
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openinSpace = boardHeight / 4;
        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = topPipe.y + pipeHeight + openinSpace;
        pipes.add(bottomPipe);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw the background
        drawBackground(g2);

        // Render based on the game state
        if (gameState == 0) {
            drawStartScreen(g2);
        } else if (gameState == 1) {
            drawBird(g2);
            drawPipes(g2);
            drawScore(g2);
        } else if (gameState == 2) {
            drawBird(g2);
            drawPipes(g2);
            drawScore(g2);
            drawGameOverScreen(g2);
        }
    }

    private void drawBackground(Graphics2D g2) {
        g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
    }

    private void drawBird(Graphics2D g2) {
        g2.drawImage(bird.image, bird.x, bird.y, bird.width, bird.height, null);
    }

    private void drawPipes(Graphics2D g2) {
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g2.drawImage(pipe.image, pipe.x, pipe.y, pipe.width, pipe.height, null);

        }
    }

    private void drawScore(Graphics2D g2) {
        g2.setColor(Color.PINK);
        g2.setFont(new Font("Arial", Font.PLAIN, 35));
        g2.drawString(String.valueOf((int) score), 10, 35);
    }

    private void drawStartScreen(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 50));

        // Center the text
        FontMetrics metrics = g2.getFontMetrics(g2.getFont());
        String text = "Flappy Bird";

        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() / 2) - (textHeight / 4) - 50;

        g2.drawString(text, x, y);
    }

    //pipes
    int pipex = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;

    int pipeHeight = 512;

    public class Pipe {

        int x = pipex;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image image;
        boolean passes = false;

        Pipe(Image image) {
            this.image = image;
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameState == 1 && e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // updates the game frame by frame.
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == 1) {
            move();
            repaint();
        }
    }

    private void move() {
        bird.y += velocityY; //vertival movement of the bird
        velocityY += gravity; // simulates falling effect.
        bird.y = Math.max(bird.y, 0);

        //moves the pipes to the left.
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            if (pipe.x + pipe.width < 0) {
                pipes.remove(i); // Remove the pipe if it's off-screen
                i--; // Adjust the index to avoid skipping the next pipe
            }

            if (!pipe.passes && bird.x > pipe.x + pipe.width) {
                pipe.passes = true;
                score += 0.5;
            }

            if (collision(bird, pipe)) {
                gameState = 2;
            }

        }
        if (bird.y > boardHeight) { // If the bird falls off the screen
            gameState = 2;
        }
    }

    public boolean collision(Bird bird, Pipe pipe) {
        return bird.x < pipe.x + pipe.width
                && bird.x + bird.width > pipe.x
                && bird.y < pipe.y + pipe.height
                && bird.y + bird.height > pipe.y;

    }

    public class Bird {

        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image image;

        Bird(Image image) {
            this.image = image;

        }
    }

    private void drawGameOverScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 50));
        g2.drawString("Game Over", getWidth() / 2 - 120, getHeight() / 2 - 50);

        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.drawString("Score: " + (int) score, getWidth() / 2 - 60, getHeight() / 2);

        restartButton.setVisible(true);
    }

    private void startGame() {
        gameState = 1;
        startButton.setVisible(false);
        gameLoop.start();
        placePipeTimer.start();
    }

    private void restartGame() {
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameState = 1;
        restartButton.setVisible(false);
        gameLoop.start();
        placePipeTimer.start();
        requestFocusInWindow();
    }
}
