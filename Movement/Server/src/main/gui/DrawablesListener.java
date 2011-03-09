package main.gui;

import java.util.ArrayList;

public class DrawablesListener {
	
	protected Pitch pitch;

	/**
	 * Drawables listener
	 * 
	 * @param pitch
	 */
	public DrawablesListener(Pitch pitch)
	{
		this.pitch = pitch;
	}
	
	/**
	 * Set drawables
	 * 
	 * updates them on a pitch
	 * 
	 * @param drawables
	 */
	@SuppressWarnings("unchecked")
	public void setDrawables(ArrayList<Drawable> drawables)
	{
		// dereference drawables
		pitch.setDrawables((ArrayList<Drawable>) drawables.clone());
		// clean them so new could be added
		drawables.clear();
	}
}
