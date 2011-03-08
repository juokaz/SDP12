package main.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

@SuppressWarnings("serial")
public class Pitch extends JComponent implements ActionListener {

	// List of robots
	private Robot robot1;
	private Robot robot2;
	private Ball ball;
	
	private ArrayList<Wall> walls;
	
	// Pitch background
	private BufferedImage pitchBackground;
	
	// Timer 
	private Timer time;

	private ArrayList<Drawable> drawables;
	
	public Pitch(Robot robot1, Robot robot2, final Ball ball) {
		
		// Try to load pitch image or throw exception
		try {
			
			pitchBackground = ImageIO.read(Pitch.class.getResource("images/bg.jpg"));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		// Place first robot near goal post
		//robot1 = new RobotT("ty.jpg", getPitchWidth()/10, getPitchHeight()/2, 0.0);
		this.robot1 = robot1;
		this.robot2 = robot2;
		this.ball = ball;
		
		walls = new ArrayList<Wall>();
		initializeWalls();
		
		robot1.setWalls(walls);
		robot1.setOpponent(robot2);
		robot1.setBall(ball);
		
		robot2.setWalls(walls);
		robot2.setOpponent(robot1);
		robot2.setBall(ball);
		
		ball.setWalls(walls);
		
		setFocusable(true);

		addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if(keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
					getRobot1().toggleCommandReceiving();
				}
				if(keyEvent.getKeyCode() == KeyEvent.VK_B) {
					getBall().getTimer().stop();
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {}

			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
		
		addMouseListener(new MouseInputAdapter() {
			
			public void mousePressed(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				
				if (e.getButton() == MouseEvent.BUTTON1) {
					getBall().setXPos(x);
					getBall().setYPos(y);
				} else if(e.getButton() == MouseEvent.BUTTON3) {
					getRobot1().resetMovement();
					getRobot1().stop();
					getRobot1().setXPos(x);
					getRobot1().setYPos(y);
					getRobot1().getKicker().updateLocation(getRobot1().getXPos(), getRobot1().getYPos(), getRobot1().getTheta());
				}
			}
			
			public void mouseDragged(MouseEvent e) {
				
				int x = e.getX();
				int y = e.getY();
				
				getBall().setXPos(x);
				getBall().setYPos(y);
			}
		});
		
		// Start timer
		time = new Timer(60, this);
		time.start();
	}
	
	protected void initializeWalls() {
		
		Wall top = new Wall(new Rectangle(30, 0, 705, 107), Wall.TOP_WALL);
		Wall bottom = new Wall(new Rectangle(30, 477, 705, 100), Wall.BOTTOM_WALL);
		Wall leftTop = new Wall(new Rectangle(0, 107, 30, 97), Wall.UPPER_LEFT_WALL);
		Wall rightTop = new Wall(new Rectangle(735, 107, 30, 92), Wall.UPPER_RIGHT_WALL);
		Wall leftBottom = new Wall(new Rectangle(0, 380, 30, 97), Wall.LOWER_LEFT_WALL);
		Wall rightBottom = new Wall(new Rectangle(735, 380, 30, 97), Wall.LOWER_RIGHT_WALL);
		
		walls.add(top);
		walls.add(bottom);
		walls.add(leftTop);
		walls.add(rightTop);
		walls.add(leftBottom);
		walls.add(rightBottom);
	}
	
	protected void paintComponent(Graphics graphics) {
		
		// Create a separate copy of graphics 
		Graphics2D g2d = (Graphics2D) graphics.create();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
		
		// Draw pitch and robots
		simulatorRender(g2d);
		
		g2d.dispose();
	}
	
	// Callback for timer
	public void actionPerformed(ActionEvent actionEvent) {
		repaint();
	}
	
	public void simulatorRender(Graphics2D g2d) {
		
		g2d.drawImage(getPitchImage(), 0, 0, null);
		
		ball.draw(g2d);
		robot2.draw(g2d);
		robot1.draw(g2d);
		
		// Draw drawables
		if (drawables != null) {
			for (Drawable drawable : drawables) {
				drawable.draw(g2d);
			}
		}
	}

	/*
	 * GETTERS AND SETTERS
	 */

	public void setDrawables(ArrayList<Drawable> drawables) {

		this.drawables = drawables;
	}
	
	public int getPitchWidth() {
		
		return pitchBackground.getWidth();
	}

	public int getPitchHeight() {
		
		return pitchBackground.getHeight();
	}

	public Robot getRobot1() {
		return robot1;
	}

	public Robot getRobot2() {
		return robot2;
	}

	public Ball getBall() {
		return ball;
	}
	
	public BufferedImage getPitchImage() {
		
		return pitchBackground;
	}
}
