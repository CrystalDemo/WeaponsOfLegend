package com.DragonSoulSong.Listeners; //Tells Java what package this class belongs to.

//Necessary imports.

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;

import com.DragonSoulSong.Lib.*;
import com.DragonSoulSong.Wl.WeaponsOfLegend;

//Any listener class must implement the Bukkit Listener interface.
public class WlPlayerListener implements Listener {
	
	//Create an instance of the main plugin so that we can use it's config variables.
	private final WeaponsOfLegend plugin;
	
	public WlPlayerListener(WeaponsOfLegend plugin) {
		
		this.plugin = plugin;
	}
	
	//Defines the onPlayerInteract method. Tells the plugin what to do when a PlayerInteractEvent is triggered (a player clicks with their mouse).
	@EventHandler//This decorator tells the server that this function is meant to handle the event type specified as it's argument.
	@SuppressWarnings("deprecation")
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		//If the player that clicked was holding an item, check if the action they did was a right click of some sort.
		if (event.hasItem()) {
			
			//If the player indeed right-clicked, see whether the player is in the legendPlayers.
			if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)) {
				
				//Get what player triggered the event.
				Player player = event.getPlayer();
				HashSet<Material> transparent = new HashSet<Material>();
				transparent.add(Material.AIR);
				Block block2 = player.getTargetBlock(transparent, 120);
				
				Location loc = null;
				//Define a boolean control variable that will allow us to check whether the player's attempted action is successful.
				boolean succeed = false;
				
				//If the configured maxAbilDist is less than the default distance, and the player doesn't have permission to bypass limits, get
				//the location of the block that the player's cursor was pointing at, but only within a max distance of maxAbilDist.
				if (plugin.maxAbilDist < mainLib.defaultDist && !player.hasPermission("wol.limits.bypass")) {
					
					//loc = player.getTargetBlock(null, plugin.maxAbilDist).getLocation();
					loc = player.getTargetBlock(transparent, mainLib.defaultDist).getLocation();
				}
				
				//Or, if the configured maxAbilDist is greater than the default distance, get the target block location within a max
				//distance of maxAbilDist.
				else if (plugin.maxAbilDist > mainLib.defaultDist) {
					
					//loc = player.getTargetBlock(null, plugin.maxAbilDist).getLocation();
					loc = player.getTargetBlock(transparent, mainLib.defaultDist).getLocation();
				}
				
				//Otherwise, get the target block location within the default distance.
				else {
				 
					//loc = player.getTargetBlock(null, mainLib.defaultDist).getLocation();
					//loc = (Location) player.getEyeLocation().getBlock();
					loc = player.getTargetBlock(transparent, mainLib.defaultDist).getLocation();
				}
				
				//If the player is in the legendPlayers, check what item they were holding.
				if (mainLib.legendPlayers.containsKey(player.getName())) {
					
					//Get the player's data map from legendPlayers.
					HashMap<String, Long> playerData = mainLib.legendPlayers.get(player.getName());
					
					//Get the world that the player is in.
					World playerWorld = player.getWorld();
					
					//If the player was holding the thor item (286 (golden axe) by default), check if they have permission to
					//use Thor's Hammer.
					if (event.getItem().getTypeId() == plugin.thorItem/*286*/) {
						
						//If the player has permission to use Thor's Hammer, continue onwards with operation as per the norm.
						if (player.hasPermission("wol.use.thor")) {
						
							//If the player has permission  to bypass limits, set the success control boolean to true without
							//checking for chance or cooldown success.
							if (player.hasPermission("wol.limits.bypass")) {
								
								succeed = true;
							}
							
							//Otherwise, check for chance success and whether they are past cooldown or not.
							else {
								
								//If the plugin is configured to use cooldown limits, check for cooldown and percent success.
								if (plugin.useCooldown) {
									
									//Set the succeed boolean control to the result of the cooldown check.
									succeed = mainLib.isPastCooldown(player, "thor", plugin.thorCooldown);
									
									//If the cooldown check succeeded, check if we should use a percent check as well.
									if (succeed) {
										
										//If the plugin is configured to use percent chance checks, perform a percent check.
										if (plugin.usePercent) {
										
											//Change succeed to the result of the percent chance check.
											succeed = mainLib.percentChance(plugin.thorChance);
										}
									}
									
									//If the cooldown check didn't succeed, let the player know and let them know how much time is left before they
									//can use this ability again.
									else {
										
										//Calculate how much time is left in their cooldown for this ability, in seconds.
										double timeLeft = ((plugin.thorCooldown * mainLib.ticksPerSecond) - (playerWorld.getFullTime() - playerData.get("thor"))/mainLib.ticksPerSecond) - 190;
										//Let the player know that they aren't past cooldown, and how much time they have left before they are.
										player.sendMessage(ChatColor.DARK_RED + "You don't have enough energy to use the thor ability (" + timeLeft + "s).");
									}
								}
								
								//If the plugin is not configured to use cooldwon limits, but is configured to use percent checks, then perform 
								//a chance check.
								else if (plugin.usePercent) {
									
									//Set succeed to the result of the percent chance check.
									succeed = mainLib.percentChance(plugin.thorChance);
								}
								
								//If the plugin isn't configured to use any limits, then set success to true.
								else {
									
									succeed = true;
								}
							}
							
							//If the player's attempted use of the ability was successful (succeed is true), then execute the ability. 
							if (succeed) {
								
								//Add the thor ability to the player's data map, along with the corresponding current time in ticks.
								playerData.put("thor", playerWorld.getFullTime());
								//Save changes to legendPlayers.
								mainLib.saveHash(mainLib.legendPlayers, mainLib.legendDb);
								
								//Get the player's location.
								Location playerLoc = player.getLocation();
								
								//Create a null target location.
								Location target = null;
								
								//Get the nearest entity in the player's LOS between their location and the target 
								//block (the nearest block in their LOS).
								Entity NENILOS = mainLib.getNENILOS(player, loc);
						
								//If there is an entity in the players LOS (Line Of Sight), get it's location.
								if (NENILOS != null) {
							
									Location NENILOSloc = NENILOS.getLocation();

									//If the entity in the player's LOS is closer than the nearest block on their LOS, set the target location
									//to the location of that entity. 
									if (mainLib.getDist(NENILOSloc, playerLoc) < mainLib.getDist(playerLoc, loc)) {
							
										target = NENILOSloc;
										//Let the rest of the plugin know that this entity was recently targeted.
										mainLib.recentlyTargeted = NENILOS;
									}
						
									//Otherwise, set the target location to the top of the nearest block (the block immediately above loc).
									else {
							
										loc.setY(loc.getY() + 1);
										target = loc;
									}
								}
						
								//Otherwise, set the target location to the top of the nearest block (the block immediately above loc).
								else {
							
									loc.setY(loc.getY() + 1);
									target = loc;
								}
								
								//Strike lightning in the player's current world at the target location.
								//playerWorld.strikeLightning(target);
								player.getWorld().strikeLightning(block2.getLocation(target));
							}
						}
					}
				
					//Or if the player was holding the Banhammer item (257 (iron pick) by default), check to see if they are op.
					else if (event.getItem().getTypeId() == plugin.banItem/*257*/) {
						
						//Ensure that the player is op before letting them use the Banhammer.
						if (player.isOp()) {
							
							Entity NENILOS = mainLib.getNENILOS(player, loc);
							
							//If there is an entity in the player's LOS, check to see if it is a player.
							if (NENILOS != null) {
								
								//If the entity in the player's LOS is another player, ban them.
								if (NENILOS instanceof Player) {
									
									//Create a new player instance from the target entity.
									Player target = (Player) NENILOS;
									//Ban the target player.
									target.setBanned(true);
									//Be sure to kick them from the server with an appropriate message!
									target.kickPlayer("The BanHammer has spoken!");
								}
							}
						}
					}
				
					//Or if the player was holding the Vulcan Hammer item (274 (stone pick) by default), check if they have permission to
					//use vulcan's hammer.
					else if (event.getItem().getTypeId() == plugin.vulcanItem/*274*/) {
						
						//If the player has permission to use Thor's Hammer, continue onwards with operation as per the norm.
						if (player.hasPermission("wol.use.vulcan")) {
							
							if (player.hasPermission("wol.limits.bypass")) {
								
								succeed = true;
							}
							
							else {
								
								if (plugin.useCooldown) {
									
									succeed = mainLib.isPastCooldown(player, "vulcan", plugin.vulcanCooldown);
									
									if (succeed) {
										
										if (plugin.usePercent) {
										
											succeed = mainLib.percentChance(plugin.vulcanChance);
										}
									}
									
									else {
										
										double timeLeft = ((plugin.vulcanCooldown * mainLib.ticksPerSecond) - (playerWorld.getFullTime() - playerData.get("vulcan"))/mainLib.ticksPerSecond) - 190;
										player.sendMessage(ChatColor.DARK_RED + "You don't have enough energy to use the vulcan ability (" + timeLeft + "s).");
									}
								}
								
								else if (plugin.usePercent) {
									
									succeed = mainLib.percentChance(plugin.vulcanChance);
								}
								
								else {
									
								}
							}
							
							if (succeed) {
								
								playerData.put("vulcan", playerWorld.getFullTime());
								mainLib.saveHash(mainLib.legendPlayers, mainLib.legendDb);
								
								Location playerLoc = player.getLocation();
								
								Location target = null;
						
								Entity NENILOS = mainLib.getNENILOS(player, loc);
								
								if (NENILOS != null) {
						
									Location NENILOSloc = NENILOS.getLocation();
						
									if (mainLib.getDist(NENILOSloc, playerLoc) < mainLib.getDist(playerLoc, loc)) {
								
										target = NENILOSloc;
									}
						
									else {
							
										target = loc;
									}
								}
						
								else {
							
									target = loc;
								}
								
								//Draw a "solid" sphere of fire, masked to air, of radius burnRad, centered on the target location.
								mainLib.drawSphere(target, plugin.burnRad, Material.FIRE, Material.AIR, true);
							}
						}
					}
					
					//Or if the player was holding the Meteor Staff item (369 (blaze rod) by default), then check if they have permission
					//to use the meteor staff.
					else if (event.getItem().getTypeId() == plugin.meteorItem/*369*/) {
						
						if (player.hasPermission("wol.use.meteor")) {
							
							if (player.hasPermission("wol.limits.bypass")) {
								
								succeed = true;
							}
							
							else {
								
								if (plugin.useCooldown) {
									
									succeed = mainLib.isPastCooldown(player, "meteor", plugin.meteorCooldown);
									
									if (succeed) {
										
										if (plugin.usePercent) {
										
											succeed = mainLib.percentChance(plugin.meteorChance);
										}
									}
									
									else {
										
										double timeLeft = ((plugin.meteorCooldown * mainLib.ticksPerSecond) - (playerWorld.getFullTime() - playerData.get("meteor"))/mainLib.ticksPerSecond) - 190;
										player.sendMessage(ChatColor.DARK_RED + "You don't have enough energy to use the meteor ability (" + timeLeft + "s).");
									}
								}
								
								else if (plugin.usePercent) {
									
									succeed = mainLib.percentChance(plugin.meteorChance);
								}
								
								else {
									
								}
							}
							
							if (succeed) {
								
								playerData.put("meteor", playerWorld.getFullTime());
								mainLib.saveHash(mainLib.legendPlayers, mainLib.legendDb);
							
								//Launch a fireball from the player. The boolean meteorFire determines whether or not the fireball's 
								//explosion sets fire to stuff inside of it's blast radius meteorRad.
								mainLib.launchFireball(player, plugin.meteorFire, plugin.meteorRad);
								//Play a blaze shooting audio effect in the player's current world, originating from the player's location.
								playerWorld.playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 1);
								//Play a mobspawner flames effect at the beginning of the fireball's path.
								playerWorld.playEffect(player.getLocation().add(player.getEyeLocation().getDirection()), Effect.MOBSPAWNER_FLAMES, 1);
							}
						}
					}
					
					//Or if the player was holding the Demon Scythe item (294 (golden hoe) by default), then check if they have permission
					//to use the demon scythe.
					else if (event.getItem().getTypeId() == plugin.demonItem/*294*/) {
						
						if (player.hasPermission("wol.use.demon")) {
							
							if (player.hasPermission("wol.limits.bypass")) {
								
								succeed = true;
							}
							
							else {
								
								if (plugin.useCooldown) {
									
									succeed = mainLib.isPastCooldown(player, "demon", plugin.demonCooldown);
									
									if (succeed) {
										
										if (plugin.usePercent) {
										
											succeed = mainLib.percentChance(plugin.demonChance);
										}
									}
									
									else {
										
										double timeLeft = ((plugin.demonCooldown * mainLib.ticksPerSecond) - (playerWorld.getFullTime() - playerData.get("demon"))/mainLib.ticksPerSecond) - 190;
										player.sendMessage(ChatColor.DARK_RED + "You don't have enough energy to use the demon ability (" + timeLeft + "s).");
									}
								}
								
								else if (plugin.usePercent) {
									
									succeed = mainLib.percentChance(plugin.demonChance);
								}
								
								else {
									
								}
							}
							
							if (succeed) {
							
								playerData.put("demon", playerWorld.getFullTime());
								mainLib.saveHash(mainLib.legendPlayers, mainLib.legendDb);
								
								Entity NENILOS = mainLib.getNENILOS(player, loc);
								
								if (NENILOS != null) {
								
									//Apply a demon curse effect of duration "demonCurseLength" and potion strength "demonCursePower" to
									//the target entity.
									mainLib.demonCurse(NENILOS, plugin.demonCurseLen, plugin.demonCursePower);
									playerWorld.playEffect(NENILOS.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
								}
							}
						}
					}
					
					//Or if the player was holding the Glacier Staff item (352 (bone) by default), check if they have permission
					//to use the glacier staff.
					else if (event.getItem().getTypeId() == plugin.glacierItem/*352*/) {
						
						if (player.hasPermission("wol.use.glacier")) {
							
							if (player.hasPermission("wol.limits.bypass")) {
								
								succeed = true;
							}
							
							else {
								
								if (plugin.useCooldown) {
									
									succeed = mainLib.isPastCooldown(player, "glacier", plugin.glacierCooldown);
									
									if (succeed) {
										
										if (plugin.usePercent) {
										
											succeed = mainLib.percentChance(plugin.glacierChance);
										}
									}
									
									else {
										
										double timeLeft = ((plugin.glacierCooldown * mainLib.ticksPerSecond) - (playerWorld.getFullTime() - playerData.get("glacier"))/mainLib.ticksPerSecond) - 190;
										player.sendMessage(ChatColor.DARK_RED + "You don't have enough energy to use the glacier ability (" + timeLeft + "s).");
									}
								}
								
								else if (plugin.usePercent) {
									
									succeed = mainLib.percentChance(plugin.glacierChance);
								}
								
								else {
									
								}
							}
							
							if (succeed) {
								
								playerData.put("glacier", playerWorld.getFullTime());
								mainLib.saveHash(mainLib.legendPlayers, mainLib.legendDb);
								
								//Get the list of blocks within the player's LOS within a max distance of 600 blocks.
								//List<Block> los = player.getLineOfSight(null, 600);
								List<Block> los = player.getLineOfSight((Set<Material>) null, 600);
								
								
								//Get the player's current location.
								Location playerLoc = player.getLocation();
								
								Location target = null;
								
								//Create a null target entity.
								Entity targetEntity = null;
								
								if (mainLib.getNENILOS(player, loc) != null) {
									
									Entity NENILOS = mainLib.getNENILOS(player, loc);
									
									Location NENILOSloc = NENILOS.getLocation();
									
									if (mainLib.getDist(NENILOSloc, playerLoc) < mainLib.getDist(playerLoc, loc)) {
										
										target = NENILOSloc;
										targetEntity = NENILOS;
									}
									
									else {
										
										target = loc;
									}
								}
								
								else {
									
									target = loc;
								}
								
								//Loop through all blocks in the list of blocks in the player's LOS.
								for (Block block : los) {
									
									//Get the location of the current block.
									Location blockLoc = block.getLocation();
									
									//If the current block's location is the same as the target location, break out of the loop.
									if (blockLoc == target) {
										
										break;
									}
									
									//Otherwise, play a blue (specified by the data byte "0") potion break effect at the current block location.
									//This is what creates the staff's ice jet animation/sound.
									else {
										
										playerWorld.playEffect(blockLoc, Effect.POTION_BREAK, 0);
									}
								}
								
								//Draw a solid sphere of ice, masked to air, of the radius iceRad, centered on the target location.
								mainLib.drawSphere(target, plugin.iceRad, Material.ICE, Material.AIR, true);
								
								//If the plugin is also configured to place snow down with this effect, draw a sphere of snow with
								//a radius 1 larger than iceRad.
								//Note that this snow is the thin, sheet snow, not snow blocks.
								if (plugin.glacierSnow) {
									
									mainLib.drawSphere(target, plugin.iceRad + 1, Material.SNOW, Material.AIR, true);
								}
								
								//If the target location is an entity, apply the glacier freeze effect to it.
								if (targetEntity != null) {
									
									//Apply a glacier freeze effect, and do an initial amount of contact damage glacierDamage, to the target entity.
									mainLib.glacierFreeze(targetEntity, plugin.glacierSlowLength, plugin.glacierSlowStrength, plugin.glacierDamage);
								}

								//Get the current server's task scheduler.
								BukkitScheduler bs = player.getServer().getScheduler();
								//Convert our glacierDuration from seconds to ticks so that we can use it.
								long glacierTicks = (long) (plugin.glacierDuration * mainLib.ticksPerSecond);
								//Schedule a new task to be run in a separate thread from the main server after a duration of glacierDuration converted to ticks.
								//The plugin that calls the task is specified in the first argument, the task itself is an instance of a runnable object, specified
								//in the second argument.
								//This is the task that will auto-remove the ice/snow sphere that we just created, after glacierDuration seconds.
								//bs.scheduleAsyncDelayedTask(plugin, new GlacierMelt(target, plugin.iceRad), glacierTicks);
								bs.runTaskTimer(plugin, new GlacierMelt(target, plugin.iceRad), glacierTicks, glacierTicks);
							}
						}
					}
				
					else {
					
					}
				}
			}
		}
	}	
}