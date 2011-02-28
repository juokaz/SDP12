package main.gui;

import java.util.ArrayList;

public class DrawablesListener {
	
	protected Simulator simulator;

	/**
	 * Drawables listener
	 * 
	 * @param simulator
	 */
	public DrawablesListener(Simulator simulator)
	{
		this.simulator = simulator;
	}
	
	/**
	 * Set drawables
	 * 
	 * updates them on a simulator
	 * 
	 * @param drawables
	 */
	public void setDrawables(ArrayList<Drawable> drawables)
	{
		simulator.setDrawables(drawables);
	}
}
