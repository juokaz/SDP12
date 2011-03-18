package main;

import java.io.IOException;

import javax.swing.*;

import main.executor.Simulator;
import main.gui.DrawablesListener;
import main.gui.GuiListener;
import main.strategy.ProcessorListener;

public class Runner {
	
	/**
	 * Is debugging enabled 
	 */
	public static boolean DEBUG = true;

	/**
	 * Processor instance
	 */
	private Processor processor;
	
	/**
	 * Strategy instance
	 */
	private Strategy strategy;
	
	/**
	 * Executor instance
	 */
	private Executor executor;

	/**
	 * Is Runner running?
	 */
	private boolean running = false;

	/**
	 * Is Runner initialised?
	 */
	private boolean initialised = false;
	
	/**
	 * Processing worker instance used to run processing in a separate thread
	 */
	private ProcessingWorker worker;

	/**
	 * Window instance controlling UI
	 */
	private Window window;

	/**
	 * Available processors
	 */
	public final String[] processors = { "File", "Local process", "Simulator" };
	
	/**
	 * Available strategies
	 */
	public final String[] strategies = {"Main Strategy", "GoToBall", "PFS", "Basic", "GetBallFromWall", "TakePenalty", "Interception"};
	
	/**
	 * Available executors
	 */
	public final String[] executors = { "Simulator", "Bluetooth", "Dull" };

	/**
	 * Robot blue
	 */
	private final String ROBOT_BLUE = "Blue";
	
	/**
	 * Robot yellow
	 */
	private final String ROBOT_YELLOW = "Yellow";
	
	/**
	 * Available robots
	 */
	public final String[] robots = { ROBOT_BLUE, ROBOT_YELLOW };
	
	/**
	 * Left Goal
	 */
	private final String LEFT_GOAL = "Left Goal";
	
	/**
	 * Robot yellow
	 */
	private final String RIGHT_GOAL = "Right Goal";
	
	/**
	 * Available goals
	 */
	public final String[] goals = { LEFT_GOAL, RIGHT_GOAL };
	

	/**
	 * Start Runner statically
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// we need actual runner instance as this is not a static-only class actually
		new Runner();
	}

	/**
	 * Runner instance
	 */
	public Runner() {
		// create a window
		window = new Window(this);
	}

	/**
	 * Initialize runner using 3 sub-parts
	 * 
	 * Might throw exception if configuration is wrong or any of the sub-parts fail to start
	 * 
	 * @param processor
	 * @param strategy
	 * @throws Exception
	 */
	public void initialize(String processor, String executor)
			throws Exception {
		// Disable button from being clicked 
		window.setButton("Running...", false);
		
		// set initialised to true so other parts of code can know Runner state
		initialised = true;

		// initialise all sub-components
		setProcessor(processor);
		setExecutor(executor);

		// allow Runner to be stopped by settings button text
		window.setButton("Stop", true);
		// Inform window about running process
		// TODO handle this better
		if (!(this.processor instanceof Simulator) && window.getPitch() != null) {
			this.processor.addListener(new GuiListener(window.getPitch()));
		}
	}
	
	/**
	 * Start processing
	 */
	public void startRunner()
	{
		System.out.println("Processing starting");
		
		// start running processor
		this.processor.run();
	}

	/**
	 * Stop runner from being executed anymore
	 */
	public void stopRunner() {
		
		// first disable the button
		window.setButton("Stopping...", false);
		
		// cancel worker thread
		if (worker != null) {
			worker.cancel(true);
			worker = null;
		}

		// stop processing data and kill the executor
		if (processor != null) {
			processor.stop();
		}
		if (executor != null) {
			executor.exit();
		}

		// set initialised to false to notify later code that Runner has been
		// successfully stopped 
		initialised = false;
		running = false;
		
		// re-enable button for start Runner again
		window.setButton("Run", true);
		window.setButtonExecution("Start execution", false);
		System.out.println("Runner stopped");
	}

	/**
	 * Set processor by it's type 
	 * 
	 * @param type
	 * @throws IOException 
	 */
	private void setProcessor(String type) throws IOException {

		// TODO refactor this
		if (type.equals("Local process")) {
			processor = new main.processor.LocalVision("../../Vision/trunk/ObjectDetection/src/build/vision c RankedArea outputToConsole show");
		} else if (type.equals("Simulator")) {
			processor = new main.executor.Simulator(window.getPitch());
		} else {
			processor = new main.processor.File("data/Outputlocs.txt");
		}
	}

	/**
	 * Set strategy by it's type 
	 * 
	 * @param type
	 */
	private void setStrategy(String type) {
		
		// TODO refactor this
		if (type.equals("PFS")) {
			// wheel radius = 2.48
			// gear ratio = 10 / 3
			// second parameter is 2.48 * 10 / 3
			// TODO use config for settings
			strategy = new main.strategy.PFStrategy(15.2, 8.27);
		} else if (type.equals("Basic")) {
			strategy = new main.strategy.BasicStrategy();
		} else if (type.equals("GoToBall")){
			strategy = new main.strategy.GoToBall();
		} else if (type.equals("GetBallFromWall")){
			strategy = new main.strategy.GetBallFromWall();
		} else if (type.equals("TakePenalty")){
			strategy = new main.strategy.TakePenalty();
		} else if (type.equals("Main Strategy")) {
			strategy = new main.strategy.MainStrategy();
		} else if (type.equals("Interception")) {
			strategy = new main.strategy.Interception();
		}
	}

	/**
	 * Set executor by it's type
	 * 
	 * Might throw exception if executor cannot be created
	 * 
	 * @param type
	 * @throws Exception
	 */
	private void setExecutor(String type) throws Exception {

		// TODO refactor this to use automatic discovery
		if (type.equals("Bluetooth")) {
			// TODO move settings to config file
			executor = new main.executor.Bluetooth("Roboto", "00:16:53:0b:b5:a3");
		} else if (type.equals("Dull")) {
			executor = new main.executor.Dull();
		} else {
			if (!(this.processor instanceof Executor)) {
				throw new Exception ("Simulator needs Simulator processor");
			}
			executor = (Executor) this.processor;
		}
	}

	/**
	 * Toggle runner, so if it's not started yet - start it, if started - stop it
	 * 
	 * @param processor
	 * @param executor
	 * @param our_robot
	 * @return true for started, false for stopped
	 */
	public boolean toggle(String processor, String executor) {
		
		// is it running now?
		if (initialised) {
			if (Runner.DEBUG) {
				System.out.println("Stopping runner");
			}
			// if yes - stop it
			stopRunner();
			return false;
		}
		
		System.out.println("Initializing runner");

		try 
		{
			initialize(processor, executor);
			
			window.setButtonExecution("Start execution", true);
			
			System.out.println("Runner initialized");
		}
		catch (Exception e)
		{
			// runner cannot be started
			System.out.println("Runner cannot be initialized: " + e.getMessage());
			// print stack trace to help with debugging
			if (DEBUG) {
				e.printStackTrace();
			}
			// stop it, this is unneeded in most cases, but just makes sure all processes are reset
			stopRunner();
		}

		return true;
	}
	
	/**
	 * Instantiates worked and starts it's process
	 * 
	 * @param strategy_name
	 * @param our_robot
	 * @param left_goal
	 */
	public void toggleExecution(String strategy_name, String our_robot, String left_goal)
	{
		if (!initialised) {
			System.out.println("Runner is not initialised");
			return;
		}
		
		if (!running) {
			if (Runner.DEBUG) {
				System.out.println("Starting runner");
			}
			
			// which robot are we controlling
			processor.setOurRobot(our_robot.equals(ROBOT_BLUE));
			
			// which is left goal
			processor.setLeftGoal(left_goal.equals(LEFT_GOAL));
		
			// initialise the strategy
			setStrategy(strategy_name);

			if (Runner.DEBUG) {
				System.out.println("Strategy to use: " + strategy_name);
			}
			
			// connect strategy with executor, strategy will send commands to it
			strategy.setExecutor(executor);

			// connect processor with strategy, sends new locations to strategy
			processor.addListener(new ProcessorListener(strategy));
			
			// Strategy drawables listener
			strategy.setDrawablesListener(new DrawablesListener(window.getPitch()));
			
			// update button to make it clear what next click will do
			window.setButtonExecution("Stop execution", true);
			
			// update running state
			running = true;
	
			System.out.println(strategy.getClass().toString());
			
			// create runner if doesn't exist
			if (worker == null) 
			{
				worker = new ProcessingWorker();
				worker.execute();
			}
		} else {
			if (Runner.DEBUG) {
				System.out.println("Pausing runner");
			}
			
			// send stop command
			if (executor != null) {
				executor.stop();
			}
			
			// set to dull strategy so it doesn't do anything
			strategy = new main.strategy.Dull();

			// connect processor with strategy, sends new locations to strategy
			processor.addListener(new ProcessorListener(strategy));
			
			// allow restarting
			window.setButtonExecution("Start execution", true);
			
			// update status
			running = false;
		}
	}
	
	/**
	 * ProcessingWorker class containing logic to start worker in separate thread
	 * using SwingWorker class
	 */
	class ProcessingWorker extends SwingWorker<String, Object> {

		@Override
		public String doInBackground() {
			try {
				// try starting runner
				startRunner();
			} catch (Exception e) {
				// runner cannot be started
				System.out.println("Runner cannot be started: " + e.getMessage());
				e.printStackTrace();
				// stop it, this is unneeded in most cases, but just makes sure all processes are reset
				stopRunner();
			}

			// worker interface processes an Object and returns a String
			// hence a String here
			return "";
		}

		@Override
		protected void done() {

		}
	}
}
