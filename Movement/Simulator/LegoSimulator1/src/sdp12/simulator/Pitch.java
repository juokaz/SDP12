package sdp12.simulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Timer;

public class Pitch extends JComponent implements ActionListener {

	// List of robots
	RobotT robot1;
	RobotT robot2;
	RobotT ball;
	
	// Pitch background
	BufferedImage pitchBackground;
	
	// Timer 
	Timer time;
	
	public Pitch() {
		
	}
	
	// Constructor for Pitch
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
		
		// Start timer
		time = new Timer(5, this);
		time.start();
		
	}
	
public Pitch(RobotT robot1, RobotT robot2, RobotT ball) {
		
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
		
		// Start timer
		time = new Timer(5, this);
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
		g2d.drawImage(getPitchImage(), 0, 0, null);
		
		if(robot1 != null) {
			
			drawObject(g2d, robot1);
		
		}
		
		if(robot2 != null) {
			
			drawObject(g2d, robot2);
		
		}
		
		if(ball != null) {
			
			drawObject(g2d, ball);
		
		}
		
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
	
	public void actionPerformed(ActionEvent actionEvent) {
		
		repaint();
		//writeCoordinates("images", "coordinates");
		
	}
	
	public void drawObject(Graphics2D g, RobotT obj) {
		
		AffineTransform xform = new AffineTransform();
		xform.rotate(obj.getTheta(), obj.getXPos() + obj.getWidth()/2,
							obj.getYPos() + obj.getHeight()/2);
		xform.translate(obj.getXPos(), obj.getYPos());
	
		g.drawImage(obj.getImage(), xform, null);
		
	}
	
	public void writeCoordinates(String destDir, String fileName) {
		
		 try {
			 
		    FileWriter fstream = new FileWriter(destDir + "/" + fileName + ".txt", true);
		    BufferedWriter out = new BufferedWriter(fstream);

		    String line = ""; 
		    line = line.concat(robot1.getXPos() + " " + robot1.getYPos()
		    		+ " " + robot1.getTheta() + " ");
		    line = line.concat(robot2.getXPos() + " " + robot2.getYPos()
		    		+ " " + robot2.getTheta() + " ");
		    
		    out.write(line + System.getProperty("line.separator"));
		    
		    out.close();
		    
		 } catch (Exception e) {
			 
		      System.err.println("Error: " + e.getMessage());
		      
		 }
		
	}
	
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
