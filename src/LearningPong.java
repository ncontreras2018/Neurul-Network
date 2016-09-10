import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LearningPong extends JPanel implements KeyListener {

	private double score = 0;

	private final int width = 600, height = 300;

	private int paddleX = width / 2;

	private double ballX = width / 2, ballY = height / 2;
	private double ballVX = 1, ballVY = 1;
	
	private int child = 0;
	private int generation = 0;

	private NetworkTrainer trainer;

	private final boolean DISPLAY = true;
	
	private boolean stopAll;
	
	private int ticksPerSecond;

	public static void main(String[] args) {
		new LearningPong();
	}

	private LearningPong() {

		if (DISPLAY) {

			JFrame frame = new JFrame("Learning Pong");

			frame.add(this);

			frame.setResizable(false);

			this.setPreferredSize(new Dimension(width, height));

			frame.pack();

			frame.setVisible(true);

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			frame.addKeyListener(this);
		}

		NeuralNetwork network = new NeuralNetwork(NeuralNetwork.NetworkType.NORMAL, 1);
		network.setupInputs(width * height - 100 * width);
		network.registerOutputs(new String[] { "Left", "Right" });

		trainer = new NetworkTrainer(network);

		Thread simThread = new Thread(() -> update());
		simThread.setPriority(Thread.MAX_PRIORITY);
		simThread.start();

		if (DISPLAY) {
			new Timer(true).schedule(new TimerTask() {

				@Override
				public void run() {
					repaint();
				}
			}, 0, 50);
		}
	}

	private void update() {

		while (true) {
			
			long startNano = System.nanoTime();
			
			while (stopAll) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			NeuralNetwork network = trainer.getNetwork();

			boolean[][] array = new boolean[width * height - 100 * width][1];

			for (int row = (int) ballY - 25; row < (int) ballY + 50; row++) {
				for (int col = (int) ballX - 25; col < (int) ballX + 50; col++) {
					int arrayPos = row * width + col;

					if (arrayPos >= 0 && arrayPos < array.length) {
						array[arrayPos][0] = true;
					}
				}
			}

			trainer.trainNetwork(array);

			if (network.getOutputValue("Left")) {
				paddleX += -2;
				
				if (paddleX < 0) {
					paddleX = 0;
				}
			}

			if (network.getOutputValue("Right")) {
				paddleX += 2;
				
				if (paddleX > width - width / 4) {
					paddleX = width - width / 4;
				}
			}

			if (ballY + ballVY >= height - 100) {

				if (ballX > paddleX - width / 8 && ballX < paddleX + width / 8) {
					ballVY = -ballVY;
					ballVY -= 0.5;
					double toAdd = Math.copySign(0.5, ballVX);
					ballVX += toAdd;
					score++;
				} else {
					
					score += Math.abs(ballX - paddleX) / width;
					
					ballX = width / 2;
					ballY = height / 2;
					ballVX = 1;
					ballVY = 1;

					paddleX = width / 2;

					trainer.endAttempt(score);

					score = 0;
					
					child++;
					
					if (child > 50) {
						child = 0;
						generation++;
						trainer.evolveNetwork();
					}
				}
			}

			if (ballY + ballVY < 0) {
				ballVY = -ballVY;
			}

			if (ballX + ballVX < 0 || ballX + ballVX > width) {
				ballVX = -ballVX;
			}

			ballX += ballVX;
			ballY += ballVY;
			
			long endNano = System.nanoTime();
			
			ticksPerSecond = (int) (1 / ((endNano - startNano) / 1000000000.0));
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		if (trainer == null) {
			return;
		}

		g.setColor(Color.WHITE);

		g.fillRect(0, 0, width, height);

		g.setColor(Color.BLACK);

		g.drawRect(paddleX - width / 8, height - 100, width / 4, 50);

		g.drawOval((int) (ballX - 10), (int) (ballY - 10), 20, 20);
		
		g.drawString("Generation: " + generation + " Child: " + child , 25, 15);

		g.drawString("Score: " + score, 25, 25);
		
		g.drawString("Ticks Per Second: " + ticksPerSecond, width - 150, 15);
		
		ArrayList<Neuron> neurons = trainer.getNetwork().getNeurons();

		for (Neuron n : neurons) {

			int x1 = 0, y1 = 0, x2 = 0;

			x1 = n.getInput().getId() % width;
			y1 = n.getInput().getId() / width;

			if (n.getOutput().getName().equals("Left")) {
				x2 = 10;
			} else {
				x2 = width - 10;
			}
			
			if (neurons.get(neurons.size() - 1).equals(n)) {
				g.setColor(Color.CYAN);
			} else {
				g.setColor(Color.ORANGE);
			}

			g.drawLine(x1, y1, x2, height - 10);

			g.drawRect(x1 - 25, y1 - 25, 50, 50);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

		if (arg0.getKeyChar() == 'n') {
			
			stopAll = true;

			try {
				trainer.getParentState().saveToFile("C:/Users/Nicholas/Desktop/PongNetworkSave.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			stopAll = false;

		}

		if (arg0.getKeyChar() == 'm') {
			
			stopAll = true;

			try {
				trainer = new NetworkTrainer(new NeuralNetwork(new NetworkState("C:/Users/Nicholas/Desktop/PongNetworkSave.txt")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			stopAll = false;
		}
	}
}
