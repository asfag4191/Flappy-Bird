package no.uib;

import javax.swing.JFrame;

public class App {

    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);   // Adding the JPanel
        frame.pack();            // Ensures components are sized correctly
        flappyBird.requestFocus();  // Ensures key events are captured
        frame.setVisible(true);  // Makes the frame visible
    }
}
