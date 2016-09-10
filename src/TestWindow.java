import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestWindow extends JPanel implements KeyListener {

	private int dotX = 400, dotY = 400;
	private NetworkTrainer trainer;

	private int ticksUntilReset = 10000;

	private int childNumber = 0;

	private int generationNum = 0;

	private Robot robot = null;

	private int score;

	private boolean[][] keysPressed;

	public static void main(String[] args) {
		new TestWindow();
	}

	private TestWindow() {

		JFrame frame = new JFrame("Test");

		frame.add(this);

		this.setPreferredSize(new Dimension(1000, 600));

		frame.pack();

		frame.setVisible(true);

		keysPressed = new boolean[4][1];

		frame.addKeyListener(this);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		NeuralNetwork network = new NeuralNetwork(NeuralNetwork.NetworkType.NORMAL, 1);
		network.registerOutputs(new String[] { "Up", "Down", "Left", "Right" });
		network.setupInputs(4);
		network.setupFriendlyInputNames(new String[] { "W", "A", "S", "D" });

		trainer = new NetworkTrainer(network);

		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

		new Timer(true).schedule(new TimerTask() {

			@Override
			public void run() {
				repaint();
			}
		}, 2500, 5);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {

		switch (arg0.getKeyChar()) {

		case 'w':
			keysPressed[0][0] = true;
			break;
		case 'a':
			keysPressed[1][0] = true;
			break;
		case 's':
			keysPressed[2][0] = true;
			break;
		case 'd':
			keysPressed[3][0] = true;
			break;

		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

		switch (arg0.getKeyChar()) {

		case 'w':
			keysPressed[0][0] = false;
			break;
		case 'a':
			keysPressed[1][0] = false;
			break;
		case 's':
			keysPressed[2][0] = false;
			break;
		case 'd':
			keysPressed[3][0] = false;
			break;

		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

		if (arg0.getKeyChar() == 'n') {

			try {
				trainer.getParentState().saveToFile("C:/Users/Nicholas/Desktop/NetworkSave.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (arg0.getKeyChar() == 'm') {

			try {
				trainer = new NetworkTrainer(new NeuralNetwork(new NetworkState("C:/Users/Nicholas/Desktop/NetworkSave.txt")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		NeuralNetwork network = trainer.getNetwork();

		if (ticksUntilReset > 7500) {
			robot.keyRelease(KeyEvent.VK_D);
			robot.keyPress(KeyEvent.VK_W);

		} else if (ticksUntilReset > 5000) {
			robot.keyRelease(KeyEvent.VK_W);
			robot.keyPress(KeyEvent.VK_A);

		} else if (ticksUntilReset > 2500) {
			robot.keyRelease(KeyEvent.VK_A);
			robot.keyPress(KeyEvent.VK_S);

		} else if (ticksUntilReset > 0) {
			robot.keyRelease(KeyEvent.VK_S);
			robot.keyPress(KeyEvent.VK_D);

		} else {
			ticksUntilReset = 10000;
			trainer.endAttempt(score);
			childNumber++;

			dotX = 400;
			dotY = 400;

			System.out.println("This network's score was: " + score);

			score = 0;

			if (childNumber == 8) {
				childNumber = 0;
				trainer.evolveNetwork();
				generationNum++;
			}
		}

		trainer.trainNetwork(keysPressed);

		if (network.getOutputValue("Up")) {
			dotY--;

			if (ticksUntilReset < 10000 && ticksUntilReset > 7500) {
				score++;
			} else {
				score--;
			}
		}

		if (network.getOutputValue("Down")) {
			dotY++;

			if (ticksUntilReset < 5000 && ticksUntilReset > 2500) {
				score++;
			} else {
				score--;
			}
		}

		if (network.getOutputValue("Left")) {
			dotX--;

			if (ticksUntilReset < 7500 && ticksUntilReset > 5000) {
				score++;
			} else {
				score--;
			}
		}

		if (network.getOutputValue("Right")) {
			dotX++;

			if (ticksUntilReset < 2500 && ticksUntilReset > 0) {
				score++;
			} else {
				score--;
			}
		}

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.RED);
		g.fillOval(dotX - 25, dotY - 25, 50, 50);

		for (int i = 0; i < network.getNetworkInputSize(); i++) {

			if (network.getNetworkInputs().get(i).getValue()[0]) {
				g.setColor(Color.GREEN);
			} else {
				g.setColor(Color.RED);
			}

			g.drawString(network.getNetworkInputs().get(i).getName(), 50, 100 + 15 * i);
		}

		for (int i = 0; i < network.getNumNetworkOutputs(); i++) {

			if (network.getNetworkOutputs().get(i).getOutput()) {
				g.setColor(Color.GREEN);
			} else {
				g.setColor(Color.RED);
			}

			g.drawString(network.getNetworkOutputs().get(i).getName(), 150, 100 + 15 * i);
		}

		for (Neuron n : network.getNeurons()) {

			int startY = 0, endY = 0;

			NetworkInput input = n.getInput();
			NetworkOutput output = n.getOutput();

			for (int i = 0; i < network.getNetworkInputSize(); i++) {
				if (input.equals(network.getNetworkInputs().get(i))) {
					startY = 100 + 15 * i;
					break;
				}
			}

			for (int i = 0; i < network.getNumNetworkOutputs(); i++) {
				if (output.equals(network.getNetworkOutputs().get(i))) {
					endY = 100 + 15 * i;
					break;
				}
			}

			g.setColor(Color.ORANGE);

			if (network.getNeurons().get(network.getNeurons().size() - 1).equals(n)) {
				g.setColor(Color.CYAN);
			}

			g.drawLine(65, startY - 5, 150, endY - 5);

		}

		g.setColor(Color.BLACK);

		g.drawString("Score: " + score, 50, 25);
		g.drawString("Ticks Left: " + ticksUntilReset, 50, 50);
		g.drawString("Generation Num: " + generationNum + " Child Number: " + childNumber, 50, 75);

		ticksUntilReset -= 100;
	}
}
