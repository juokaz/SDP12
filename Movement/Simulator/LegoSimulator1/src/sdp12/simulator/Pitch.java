package sdp12.simulator;

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
public class Pitch extends JComponent implements ActionListener, KeyListener {

	// List of robots
	RobotT robot1;
	RobotT robot2;
	Ball ball;
	
	ArrayList<Wall> walls;
	
	// Pitch background
	BufferedImage pitchBackground;
	BufferedImage dbImage;
	
	// Timer 
	Timer time;
	
	public Pitch() {
		
	}
	
	// Constructor for Pitch DEPRECATED
	public Pitch(RobotT robot1, RobotT robot2) {
		
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
		
		this.robot1.setOpponent(this.robot2);
		
		// Start timer
		time = new Timer(5, this);
		time.start();
		
	}
	
	public void initializeWalls() {
		
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
	
	public Pitch(RobotT robot1, RobotT robot2, Ball ball) {
		
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
		
		robot1.pitch = this;
		robot1.setWalls(walls);
		robot1.setOpponent(robot2);
		robot1.setBall(ball);
		
		robot2.pitch = this;
		robot2.setWalls(walls);
		robot2.setOpponent(robot1);
		robot2.setBall(ball);
		
		ball.setWalls(walls);
		
		MyMouseListener mouseListener = new MyMouseListener();
		addMouseListener(mouseListener);
		setFocusable(true);
		addKeyListener(this);
		
		// Start timer
		time = new Timer(15, this);
		time.start();
		
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
		
//		int x1 = (int) (robot1.getXPos() - Math.cos(robot1.getTheta()+Math.toRadians(90))*20);
//		int y1 = (int) (robot1.getYPos() + Math.sin(robot1.getTheta()+Math.toRadians(90))*20);
//		
//		int x2 = (int) (robot1.getXPos() - Math.cos(robot1.getTheta()-Math.toRadians(90))*20);
//		int y2 = (int) (robot1.getYPos() + Math.sin(robot1.getTheta()-Math.toRadians(90))*20);
//		
//		g2d.drawLine(x2, y2, x1, y1);
//		
//		g2d.setColor(Color.RED);
//		g2d.drawOval(x1, y1, 3, 3);
//		
//		x1 = (int) (robot1.getXPos() + Math.cos(robot1.getTheta())*20);
//		y1 = (int) (robot1.getYPos() - Math.sin(robot1.getTheta())*20);
//		
//		x2 = (int) robot1.getXPos();
//		y2 = (int) robot1.getYPos();
//		
//		g2d.drawLine(x1, y1, x2, y2);
		
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
		
//		for(Wall wall : walls) {
//			
//			g2d.setColor(Color.RED);
//			wall.draw(g2d);
//			
//		}
		
	}
	
	private class MyMouseListener extends MouseInputAdapter {
		
		public void mousePressed(MouseEvent e) {
			
			int x = e.getX();
			int y = e.getY();
			
			if (e.getButton() == MouseEvent.BUTTON1) {
				ball.setXPos(x);
				ball.setYPos(y);
			} else if(e.getButton() == MouseEvent.BUTTON3) {
				robot1.resetMovement();
				robot1.stop();
				robot1.setXPos(x);
				robot1.setYPos(y);
			}
		}
		
		public void mouseDragged(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			
			ball.setXPos(x);
			ball.setYPos(y);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent keyEvent) {
		if(keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
			robot1.toggleCommandReceiving();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) { }

	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	public int getPitchWidth() {
		
		return pitchBackground.getWidth();
		
	}

	public int getPitchHeight() {
		
		return pitchBackground.getHeight();
		
	}
	
	public BufferedImage getPitchImage() {
		
		return pitchBackground;
		
	}

}
