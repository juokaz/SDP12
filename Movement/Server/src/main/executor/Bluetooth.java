package main.executor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import main.Executor;
import main.data.Robot;

public class Bluetooth implements Executor {
	/**
	 * Server instance speaking to a robot via bluetooth
	 */
	Server server = null;
	
	/**
	 * Has connection been closed?
	 */
	boolean closed=true;
	
	/**
	 * Exit command
	 */
	private static final String EXIT = "0";
	
	/**
	 * Kick command
	 */
	private static final String KICK = "1";
	
	/**
	 * Move command
	 */
	private static final String MOVE = "2";
	
	/**
	 * Rotate command
	 */
	private static final String ROTATE = "3";
	
	/**
	 * Stop command
	 */
	private static final String STOP = "4";
	
	/**
	 * Separator for commands
	 */
	private static final String SPACE = " ";
	
	/**
	 * Robot executor
	 * 
	 * @param robotName Robot name
	 * @param mac MAC address
	 * @throws Exception
	 */
	public Bluetooth(String robotName, String mac) throws Exception {
		server = new Server(robotName, mac);
		closed=false;
	}
	
	/**
	 * Instance destructor, close connection on this
	 */
	protected void finalize() throws Throwable {
		exit();
		server.close();
		super.finalize();
	} 

	@Override
	public void move(Robot robot, int X, int Y) {
		// TODO Auto-generated method stub
		// server.sendCommand(orders); ???
	}

	@Override
	public void rotateWheels(Robot robot, int A, int B) {
		if(!closed)
		server.sendCommand(MOVE + SPACE + Integer.toString(A)+ SPACE + Integer.toString(B));
	}

	@Override
	public void kick(Robot robot) {
		if(!closed)
		server.sendCommand(KICK);
	}

	@Override
	public void rotate(Robot robot, int T) {
		if(!closed)
		server.sendCommand(ROTATE + SPACE + Integer.toString(T));
	}
	
	@Override
	public void stop(Robot robot){
		if(!closed)
			server.sendCommand(STOP);
	}
	
	@Override
	public void exit() {
		if(!closed)
		server.sendCommand(EXIT);
		closed=true;
	}
	
	@Override
	public void exit(Robot robot) {
		if(!closed)
		server.sendCommand(EXIT);
		closed=true;
	}
	
	/**
	 * A class containing logic how to talk to a robot
	 */
	private class Server {
		/**
		 * Output stream from a bluetooth connection
		 */
		private DataOutputStream dos;
		
		/**
		 * Input stream from a bluetooth connection
		 */
		private DataInputStream dis;
		
		/**
		 * Connection instance 
		 */
		private NXTComm nxtComm;
		
		/**
		 * Server instance
		 * 
		 * @param name
		 * @param address
		 * @throws Exception
		 */
		public Server(String name, String address) throws Exception {
			// create connection instance, this is not really connecting to a robot yet
			try {
				nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			} catch (NXTCommException e) {
				throw new Exception("Failed to load Bluetooth driver");
			}

			// NXT info array
			NXTInfo[] nxtInfo = new NXTInfo[1];

			// Robot address info
			// TODO: find out what is protocol (1 here)
			nxtInfo[0] = new NXTInfo(1, name,address);

			System.out.println("Connecting to " + nxtInfo[0].name);

			boolean opened = false;

			// try connecting a robot using bluetooth
			try {
				opened = nxtComm.open(nxtInfo[0]); 
			} catch (NXTCommException e) {
				throw new Exception("Exception from open");
			}

			// connection couldn't be established
			if (!opened) {
				throw new Exception("Failed to open " + nxtInfo[0].name);
			}

			System.out.println("Connected to " + nxtInfo[0].name);

			//set data input/output streams
			InputStream is = nxtComm.getInputStream();
			OutputStream os = nxtComm.getOutputStream();

			dos = new DataOutputStream(os);
			dis = new DataInputStream(is);
		}
		
		/**
		 * Send command to a robot
		 * 
		 * @param orders
		 */
		public void sendCommand(String orders) {
			try {
				System.out.println("Sending: " + orders);
				
				// split string to array of ints as this is a communication protocl
				for (int i = 0; i<orders.split(SPACE).length;i++) {
					dos.writeInt(Integer.parseInt(orders.split(" ")[i]));
				}
				
				// commit sent messages
				dos.flush();
			}
			catch (IOException ioe) {
				System.out.println("IO Exception sending orders:");
				System.out.println(ioe.getMessage());
			}			
		}
		
		/**
		 * Close connection
		 */
		public void close() {
			try {
				// close input and output streams
				dos.close();
				dis.close();
				// close connection
				nxtComm.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	
}
