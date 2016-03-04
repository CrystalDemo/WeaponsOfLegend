package com.DragonSoulSong.Lib; //Lets Java know what package this class belongs to.

//Necessary imports.
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.Math;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

//Define the main class.
public class mainLib {
	
	//Define class-wide global variables that are accessible from the entirety of the plugin.
	public static Entity nearestEnt;
	public static int ticksPerSecond = 20;
	public static int defaultDist = 600;
	public static HashMap<String, HashMap<String, Long>> legendPlayers = new HashMap<String, HashMap<String, Long>>();
	public static String legendDb = "legendPlayers.sodb";
	public static Entity recentlyTargeted;
	public static int taskCount;
	
	/*Returns a double that represents the mathematical square of the parameter "num".*/
	public static double square(double num) {
		
		return  Math.pow(num, 2);
	}
	
	/*Checks if the block at blockLoc, in the world that player is in, is of the material MaskMaterial, and if it is, sets that block to placeMaterial.*/
	public static void replaceMat(Block block, Material maskMaterial, Material placeMaterial) {
		
		//If the block at blockLoc is maskMaterial, set it to placeMaterial.
		if (block.getType() == maskMaterial) {
			
			block.setType(placeMaterial);
		}
	}
	
	/*Draws a sphere of the specified drawMaterial, replacing blocks of the specified replaceMaterial, of the specified radius, centered on
	 * the location "center". Whether the drawn sphere is solid or hollow is specified using the boolean value "fill", true for solid.*/
	public static void drawSphere(Location center, int radius, Material drawMaterial, Material replaceMaterial, boolean fill) {
		
		radius += 0.5;
		//Get the square of the radius.
		final double radSquare = square(radius);
		
		//Get the mathematical ceiling of radius.
		final int radCeil = (int) Math.ceil(radius);
		
		//Get the center's coords.
		final double centerX = center.getX();
		final double centerY = center.getY();
		final double centerZ = center.getZ();
		
		//Loop through all points inside of a cube of side length "radius * 2", centered on "center".
		for(double x = centerX - radCeil; x <= centerX + radCeil; x++) {
			
			for(double y = centerY - radCeil; y <= centerY + radCeil; y++) {

				for(double z = centerZ - radCeil; z <= centerZ + radCeil; z++) {
					
					//Get the square of the distance between the sphere center and the current point.
					double distSquare = square(x - centerX) + square(y - centerY) + square(z - centerZ);
					
					//If the square of the distance to the current point is greater than the square of the radius, skip forward to the next point.
					if (distSquare > radSquare) {
						
						continue;
					}
					
					//Create a new Location of the current point coordinates.
					Location currPoint = new Location(center.getWorld(), x, y, z);
					//Get the block at the current location.
					Block block = currPoint.getWorld().getBlockAt(currPoint);
					
					//If "not fill", only act on points that are at the desired radius, and not within.
					if (!fill) {
						
						//If the square of the distance of the current point from the center is exactly equal to the
						//square of the radius, replace it with the drawMaterial if it is of the maskMaterial.
						if (distSquare == radSquare) {
							
							replaceMat(block, replaceMaterial, drawMaterial);
						}
					}
					
					//Otherwise, call replaceMat() on the current point.
					else {
						
						replaceMat(block, replaceMaterial, drawMaterial);
					}
				}
			}
		}
	}
	
	/*Returns the nearest entity in the LOS of player that lies between player and targetBlock or returns null if there is none.*/
	public static Entity getNENILOS(Player player, Location targetBlock) {

		//Get the location and eye location of player.
		Location playerLoc = player.getLocation();
		Location playerEye = player.getEyeLocation();
		
		//Create a new iterator of blocks along the player's LOS.
		BlockIterator iterator = new BlockIterator(player.getWorld(), playerLoc.toVector(), playerEye.getDirection(), 0, (int) getDist(playerEye, targetBlock) + 1);

		//While there is another block in iterator, keep looping.
		while (iterator.hasNext()) first:{

			//Get the next block in iterator.
			Block item = iterator.next();
			
			//Loop through all entities in player's view that are between player and targetBlock.
			for (Entity entity : player.getNearbyEntities(Math.abs(playerEye.getX() - targetBlock.getX()), Math.abs(playerLoc.getY() - targetBlock.getY()) + 1, Math.abs(playerEye.getZ() - targetBlock.getZ()))) {
			
				//Give a margin for error of 1 block while looking for entities.
				int acc = 1;
				
				//Loop through all points relative to the current one that are within the above tolerance.
				for (int x = -acc ; x < acc ; x++) {
				
					for (int z = -acc ; z < acc ; z++) {
					
						for (int y = -acc ; y < acc ; y++) {
						
							//If the current entity resides within the margin of error of the current block, set that as the nearest entity.
							if (entity.getLocation().getBlock().getRelative(x, y, z).equals(item)) {
							
								//This works because the first entity reached in this loop that meets the condition of the above "if" will ALWAYS be the one
								//nearest to the player along their LOS.
								nearestEnt = entity;
								//Break outta all of this insane looping!
								break first;
							}
						}
					}
				}
			}
		}
		
		//Set the return value to the nearest entity in the player's LOS.
		Entity retVal = nearestEnt;
		//Clean up that global variable.
		nearestEnt = null;
		
		//Return the nearest entity.
		return retVal;
	}

	/*Returns a double that represents the distance between loc1 and loc2.*/
	public static double getDist(Location loc1, Location loc2) {
		
		//Get the difference in the x coordinates between the two points.
		double dx = loc2.getX() - loc1.getX();
		//Get the difference in the y coordinates between the two points.
		double dy = loc2.getY() - loc1.getY();
		//Get the difference in the z coordinates between the two points.
		double dz = loc2.getZ() - loc1.getZ();
		
		//Return the square-root of the sum of the mathematical squares of the difference in the coordinate values
		//between the two locations.
		return Math.sqrt(square(dx) + square(dy) + square(dz));
	}
	
	/*Gives the player "player" the amount "amount" of the item with id "id".*/

	@SuppressWarnings("deprecation")
	public static void giveItem(Player player, int id, int amount) {
		
		//Get the player's inventory and add an ItemStack of the specified amount of the specified item id.
		player.getInventory().addItem(new ItemStack(id, amount));
	}
	
	/*Like a Ghast, the player is. The boolean "isIncendiary" specifies if the fireball's explosion should create fires within it's
	 * blast radius, specified by the double parameter "blastRad".*/
	public static void launchFireball(Player player, boolean isIncendiary, double blastRad) {
		
		//Get the location of the player's eyes.
		Location playerEye = player.getEyeLocation();
		//Get the player's eye direction (the direction they're facing).
		final Vector eyeDirection = playerEye.getDirection().multiply(2);
		//Spawn a fireball in the player's world at the beginning of the player's eyeDirection vector, travelling away along their
		//current LOS.
		Fireball meteor = player.getWorld().spawn(playerEye.add(eyeDirection.getX(), eyeDirection.getY(), eyeDirection.getZ()), Fireball.class);
		//Set the shooter of the fireball to be the player. (Translation: when said fireball strikes a mob, the mob recognizes
		//the player as it's aggressor.)
		meteor.setShooter(player);
		//Set the yeild (aka blast radius) of the launched fireball to blastRad.
		meteor.setYield((float) blastRad);
	}
	
	/*Applies a demon curse effect of the specified potion strength "power" and the specified duration to the entity "entity".*/
	public static void demonCurse(Entity entity, double duration, int power) {
		
		//If the given entity is a living one (translation: a mob/player), apply the deomn curse.
		if (entity instanceof LivingEntity) {
			
			//Set the target to a LivingEntity cast of entity so that we can apply potion effects to it.
			LivingEntity target = (LivingEntity) entity;
			//Set the time to the duration (in seconds), multiplied by MC's ticks per second (20tps) to get the ticks
			//that the demon curse should last.
			int time = (int) duration * ticksPerSecond;
			
			//Create the potion effects for the demon curse: confusion, slow, slow dig, and weakness, all of the
			//specified duration and potion strength.
			PotionEffect confuse = new PotionEffect(PotionEffectType.CONFUSION, time, power);
			PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, time, power);
			PotionEffect slowDig = new PotionEffect(PotionEffectType.SLOW_DIGGING, time, power);
			PotionEffect weak = new PotionEffect(PotionEffectType.WEAKNESS, time, power);
			
			//Apply the demon curse potion effects to the target mob/player.
			target.addPotionEffect(confuse);
			target.addPotionEffect(slow);
			target.addPotionEffect(slowDig);
			target.addPotionEffect(weak);
		}
	}
	
	/*Applies a glacier freeze effect of the specified potion strength "power" and the specified duration to the entity "entity",
	 * as well as doing the specified initial amount of damage "damage".*/
	public static void glacierFreeze(Entity entity, double duration, int power, int damage) {

		if (entity instanceof LivingEntity) {
		
			LivingEntity target = (LivingEntity) entity;
			int time = (int) duration * ticksPerSecond;
		
			//Create the potion effects, slow and slow dig, for the glacier freeze effect with the specified power and duration.
			PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, time, power);
			PotionEffect slowDig = new PotionEffect(PotionEffectType.SLOW_DIGGING, time, power);
		
			//Apply the glacier freeze potion effects to the target.
			target.addPotionEffect(slow);
			target.addPotionEffect(slowDig);
			
			//Damage the target for the given amount of damage (2hp per heart eg. 2 hearts = damage of 4).
			target.damage(damage);
		}
	}
	
	/*Checks whether or not the current event succeed at a percent chance check of the double percentage "percent".
	 * Returns true if successful, false if not.*/
	public static boolean percentChance(double percent) {
		
		//For our chance test, generate a random number between 0.00 and 1.00.
		double chanceTest = Math.random();
		//Convert the specified percent to it's decimal equivalent.
		percent /= 100;
		
		//If the chance test generated is less than the specified percent, then the check succeeded, so
		//return true.
		if (chanceTest < percent) {
			
			return true;
		}
		
		//Otherwise, the check was unsuccessful, so return false.
		return false;
	}
	
	/*Checks whether the give player is past the given cooldown for the specified legend ability. Returns true if they are, 
	 * and false if not.*/
	public static boolean isPastCooldown(Player player, String ability, double cooldown) {
		
		//Get the player's name.
		String playerName = player.getName();
		
		//Get the player's data map.
		HashMap<String, Long> playerData = legendPlayers.get(playerName);
		
		//If the player's data map contains data for the specified ability, check if they are past cooldown.
		if (playerData.containsKey(ability)) {
			
			//If the difference between the current world time and the timestamp of when the player last used the specified
			//ability is greater than or equal to the cooldown converted to ticks, they are past cooldown, so return true.
			if (player.getWorld().getFullTime() - playerData.get(ability) >= cooldown * ticksPerSecond) {
				
				return true;
			}
		}
		
		//If the player's data map does not contain data for the specified ability, then they obviously haven't used it
		//since they last had their legend state toggled, and so clearly don't have to worry about the cooldown, so 
		//return true.
		else {
			
			return true;
		}
		
		//Otherwise, they are still not past the cooldown, so return false.
		return false;
	}
	
	/*Handles removal of the ice from Glacier Staff. Removes the ice sphere that is centered at the location
	 * "center" and with the integer radius glacierRad.*/
	public static void glacierMelt(Location center, int glacierRad) {
		
		drawSphere(center, glacierRad, Material.AIR, Material.ICE, true);
		drawSphere(center, glacierRad + 1, Material.AIR, Material.SNOW, true);
		drawSphere(center, glacierRad, Material.AIR, Material.WATER, true);
	}
	
	/*Saves the specified hash to the specified file of name "file".*/
	public static void saveHash(Object hash, String file) {
		
		//Monitor the following code for thrown exceptions.
		try {
		
			//Create a new ObjectOutputStream of the FileOutputStream of the specified file name.
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			//Write hash to the output stream.
			oos.writeObject(hash);
			//Flush the output stream to the desired file and save.
			oos.flush();
			//Close the output stream.
			oos.close();
		}
		
		//If the above code throws an IOException, print the error message to the console.
		catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/*Loads the hash contained within the file of the specified name "file". Returns an Object that represents said hash.*/
	public static Object loadHash(String file) {
		
		//Create an empty Object to represent the return value.
		Object retVal = new Object();
		
		try {
		
			//Create a new ObjectInputStream of the FileInputStream of the specified file.
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			//read the hash object contained within the specified file into retVal.
			retVal = ois.readObject();
			//Close the input stream.
			ois.close();
		}
		
		catch (IOException e){
			
			e.printStackTrace();
		}
		
		catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/*Checks if the file of the specified file name "file" exists. returns true if it does, false if not.*/
	public static boolean fileExists(String file) {
		
		//Try to create and close a new input stream of the specified file.
		try {
			
			FileInputStream fis = new FileInputStream(file);
			fis.close();
		}
		
		//If an IOException is thrown, the file most likely does not exist, so return false.
		catch (IOException e) {
			
			return false;
		}
		
		//Otherwise, the stream was created and closed successfully, so the specified file most likely exists,
		//thus, return true.
		return true;
	}
}