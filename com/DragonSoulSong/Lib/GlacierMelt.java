package com.DragonSoulSong.Lib; //Tells Java what package this class belongs to.

//Necessary imports.

import org.bukkit.Location;
import com.DragonSoulSong.Lib.mainLib;

//In order for a class to be runnable as a task, it must implement the Runnable interface.
public class GlacierMelt implements Runnable {

	//Create two class variable fields, a world instance and a max life for glacier blocks.
	private Location center;
	private int glacierRad;
	
	//Define the class constructor. It needs the world being operated in, the radius of glaciers, and the
	//max possible age for glaciers in order to properly clear ice spheres.
	public GlacierMelt(Location cent, int rad) {
		
		//Define the class fields as the values given to the constructor when called.
		center = cent;
		glacierRad = rad;
	
	
	//Classes that implement the Runnable interface must override the run() method, with no arguments.
	//This method is called when an instance of the class is used by a task thread.
  }
	@Override
	public void run() {
		//Melt the current glacier centered at "center" with the radius "glacierRad".
		mainLib.glacierMelt(center, glacierRad);
	}
}
