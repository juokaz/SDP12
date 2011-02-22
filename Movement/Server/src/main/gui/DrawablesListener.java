package main.gui;

import java.util.ArrayList;

public class DrawablesListener {
	
	protected Pitch pitch;

	public DrawablesListener(Pitch pitch)
	{
		this.pitch = pitch;
	}
	
	public void setDrawables(ArrayList<Drawable> drawables)
	{
		pitch.robot1.setDrawables(drawables);
	}
}
