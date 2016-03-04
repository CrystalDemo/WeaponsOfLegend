package com.DragonSoulSong.Wl;//Lets Java know what package (a way of organizing Java classes) this class resides in.

//Necessary imports.

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.DragonSoulSong.Lib.mainLib;
import com.DragonSoulSong.Listeners.*;

//The main class of the plugin MUST extend the JavaPlugin class.
public class WeaponsOfLegend extends JavaPlugin {
	
	//Define configuration value variables for the plugin.
	public int thorItem;
	public int banItem;
	public int vulcanItem;
	public int meteorItem;
	public int demonItem;
	public int glacierItem;
	public int thorDamage;
	public int burnRad;
	public boolean meteorFire;
	public double meteorRad;
	public boolean meteorBurn;
	public double meteorBurnLen;
	public double meteorBurnChance;
	public int meteorDamage;
	public double demonCurseLen;
	public int demonCursePower;
	public double demonPoisonChance;
	public double demonPoisonLen;
	public int demonPoisonStrength;
	public int demonDamage;
	public int iceRad;
	public double glacierDuration;
	public int glacierDamage;
	public boolean glacierSnow;
	public double glacierSlowLength;
	public int glacierSlowStrength;
	public double thorChance;
	public double vulcanChance;
	public double meteorChance;
	public double demonChance;
	public double glacierChance;
	public double thorCooldown;
	public double vulcanCooldown;
	public double meteorCooldown;
	public double demonCooldown;
	public double glacierCooldown;
	public int maxAbilDist;
	public boolean usePercent;
	public boolean useCooldown;

	//Create logging variable for server log message printing. 
	public final Logger log = Logger.getLogger("Minecraft");

	//Create a player variable for storing command executor info.
	private Player cPlayer;
	//Plugin description variable for getting data for log messages in onEnable/Disable.
	private PluginDescriptionFile pluginFile;
	
	//Override of JavaPlugin's onEnable method: called when the plugin is enabled.
	@SuppressWarnings("unchecked")//Tell Java not to gripe about unchecked Object to HashMap<> casts that come later on in onEnable(). In other words, "STFU Java! I know what the heck I'm doing!"
	@Override
	public void onEnable() {
		
		//Get config values from the plugin's config file. If the config file doesn't exist, it's created.
		//If any of the values are missing from the config, or don't have a value, the default value and config structure is copied from
		//the plugin's internal config.yml default config template.
		burnRad = getConfig().getInt("Vulcan.Vulcan Burn Radius"/*5*/);
		thorDamage = getConfig().getInt("Thor.Thor Lightning Damage");
		meteorFire = getConfig().getBoolean("Meteor.Meteor Explosion Sets Fire"/*true*/);
		meteorRad = getConfig().getDouble("Meteor.Meteor Explosion Radius"/*3*/);
		meteorBurn = getConfig().getBoolean("Meteor.Meteor Burns Target"/*true*/);
		meteorBurnLen = getConfig().getDouble("Meteor.Meteor Target Burn Time (seconds)"/*2*/);
		meteorBurnChance = getConfig().getDouble("Meteor.Meteor Target Burn Chance (percent)"/*60.0*/);
		meteorDamage = getConfig().getInt("Meteor.Meteor Health Damage Amount"/*6*/);
		demonCurseLen = getConfig().getDouble("Demon.Demon Curse Length (seconds)"/*5*/);
		demonCursePower = getConfig().getInt("Demon.Demon Curse Power"/*2*/);
		demonDamage = getConfig().getInt("Demon.Melee.Scythe Damage"/*6*/);
		demonPoisonChance = getConfig().getDouble("Demon.Melee.Poison Chance (percent)"/*10.0*/);
		demonPoisonLen = getConfig().getDouble("Demon.Melee.Poison Length (seconds)"/*2*/);
		demonPoisonStrength = getConfig().getInt("Demon.Melee.Poison Strength"/*1*/);
		iceRad = getConfig().getInt("Glacier.Glacier Freeze Radius"/*5*/);
		glacierDuration = getConfig().getDouble("Glacier.Glacier Ice Duration (seconds)"/*10*/);
		glacierDamage = getConfig().getInt("Glacier.Glacier Freeze Damage"/*10*/);
		glacierSnow = getConfig().getBoolean("Glacier.Glacier Effect Lays Snow"/*false*/);
		glacierSlowLength = getConfig().getDouble("Glacier.Glacier Slowness Length (seconds)"/*10*/);
		glacierSlowStrength = getConfig().getInt("Glacier.Glacier Slowness Strength"/*3*/);
		thorItem = getConfig().getInt("Items.Thor Hammer Item"/*286*/);
		banItem = getConfig().getInt("Items.Banhammer Item"/*257*/);
		vulcanItem = getConfig().getInt("Items.Vulcan Hammer Item"/*274*/);
		meteorItem = getConfig().getInt("Items.Meteor Staff Item"/*369*/);
		demonItem = getConfig().getInt("Items.Demon Scythe Item"/*294*/);
		glacierItem = getConfig().getInt("Items.Glacier Staff Item"/*352*/);
		thorChance = getConfig().getDouble("Thor.Limits.Thor Chance (percent)"/*60.0*/);
		vulcanChance = getConfig().getDouble("Vulcan.Limits.Vulcan Chance (percent)"/*60.0*/);
		meteorChance = getConfig().getDouble("Meteor.Limits.Meteor Chance (percent)"/*60.0*/);
		demonChance = getConfig().getDouble("Demon.Limits.Demon Chance (percent)"/*60.0*/);
		glacierChance = getConfig().getDouble("Glacier.Limits.Glacier Chance (percent)"/*60.0*/);
		thorCooldown = getConfig().getDouble("Thor.Limits.Thor Cooldown (seconds)"/*10*/);
		vulcanCooldown = getConfig().getDouble("Vulcan.Limits.Vulcan Cooldown (seconds)"/*10*/);
		meteorCooldown = getConfig().getDouble("Meteor.Limits.Meteor Cooldown (seconds)"/*10*/);
		demonCooldown = getConfig().getDouble("Demon.Limits.Demon Cooldown (seconds)"/*10*/);
		glacierCooldown = getConfig().getDouble("Glacier.Limits.Glacier Cooldown (seconds)"/*10*/);
		maxAbilDist = getConfig().getInt("Limits.Max Ability Effect Distance"/*600*/);
		usePercent = getConfig().getBoolean("Limits.Use Percent Limits"/*false*/);
		useCooldown = getConfig().getBoolean("Limits.Use Cooldown Limits"/*true*/);
		
		//Set the config option "copyDefaults" to true.
		//This tells the plugin to properly replace missing keys, upon the config being saved, without overwriting any existing config data.
		getConfig().options().copyDefaults(true);
		//Save the config.
		saveConfig();
		
		//Prints a message to the server log to inform the user that we are looking for the legends data.
		log.info("[WoL] Searching for legends data file...");
		
		//If the desired data file exists, in this case legendPlayers.sodb, load it into the corresponding internal HashMap<> variable.
		if (mainLib.fileExists(mainLib.legendDb)) {
			
			//Inform the user that the data file was found and is being loaded.
			log.info("[WoL] Legends data file found. Loading...");
			//Load the data file into the legendPlayers HashMap<>, casting the returned object to the desired map type.
			//Java will normally complain about this, not error, just gripe.
			mainLib.legendPlayers = (HashMap<String, HashMap<String, Long>>) mainLib.loadHash(mainLib.legendDb);
			//Inform the user that the data file was successfully loaded into the plugin.
			log.info("[WoL] Legends data file successfully loaded.");
		}
		
		//If the data file doesn't exist, create a new, blank one.
		else {
			
			//Inform the user that the data file could be found, and that a new one is being created.
			log.info("[WoL] Could not find legends data file. Creating new...");
			//Save the legendPlayers HashMap<> to a new file "legendPlayers.sodb".
			mainLib.saveHash(mainLib.legendPlayers, mainLib.legendDb);
			//Inform the user that the new data file has been successfully created.
			log.info("[WoL] Legend data file " + mainLib.legendDb + " created.");
		}
		
		//Create a new plugin manager and register the plugin's event listeners.
        PluginManager pm = getServer().getPluginManager();
        //Register the player listener.
        pm.registerEvents(new WlPlayerListener(this), this);
        //register the entity damage listener.
        pm.registerEvents(new WlEntityDamageListener(this), this);
		
        //Get the plugin's description from plugin.yml.
		pluginFile = getDescription();
		
		//Print a "successfully enabled" massage to the server log.
		log.info("[" + pluginFile.getName() + " v" + pluginFile.getVersion() + "]" + " SUCCESSFULLY ENABLED");
	}

	//Override for JavaPlugin's onDisable method: called when the plugin is disabled.
	@Override
	public void onDisable() {
		
		log.info("[" + pluginFile.getName() + " v" + pluginFile.getVersion() + "]" + " SUCCESSFULLY DISABLED");
	}
	
	//Definition of the onCommand method: gives the server special instructions for what to do when a command is used in the chat.
	public boolean onCommand(CommandSender sender, Command cmd, String cmdString, String[] args) {
		
		//Declare a boolean control variable that will be used to decide whether to execute the command, or to
		//let the user know that they can't use the command that they tried to use.
		boolean canUse = true;
		
		//If the user of the command is a player, cast sender to a Player object and assign it to cPlayer.
		if (sender instanceof Player) {
			
			//Defines cPlayer as the variable sender changed from the type CommandSender to the type Player.
			cPlayer = (Player) sender;
		}
		
		//Checks to see if the command used in the chat is /lg, or its alias /legend, regardless of the case of letters.
		if (cmdString.equalsIgnoreCase("lg") || cmdString.equalsIgnoreCase("legend")) {
				
			//Returns false, and thus shows the command's usage message, if more than 2 arguments are given to /legend.
			if (args.length > 2) {
					
				return false;
			}
				
			//Otherwise, if the number of arguments passed to /legend is 0, toggles the command user's ability to use the plugin
			//item features by adding/removing them to/from the legendPlayers HashMap<>, along with a corresponding empty player
			//data map.
			else if (args.length == 0) {
				
				//If the command user was not a player, then it must have been the console. Let the user know that they
				//must be a player to use this particular command.
				if (cPlayer == null) {
					
					//Let the command user know that they have to be a player.
					sender.sendMessage("You have to be a player to execute this command!");
					//Return true, since nothing has gone wrong. (thus we exit from the command code as well)
					return true;
				}
				
				//If the user has permission to use "/lg", execute the command.
				if (cPlayer.hasPermission("wol.legend")) {
					
					//If the user is in the legendPlayers map, remove them.
					if (mainLib.legendPlayers.containsKey(cPlayer.getName())) {
						
						//Remove the user from legendPlayers.
						mainLib.legendPlayers.remove(cPlayer.getName());
						//Save the changes to legendPlayers to the legendPlayers.sodb.
						mainLib.saveHash(mainLib.legendPlayers, mainLib.legendDb);
						//Let the user know that they can no longer use weapon abilities.
						cPlayer.sendMessage(ChatColor.GREEN + "You are no longer a legend-wielder.");
					}
					
					//If they are not in legendPlayers, add them, along with a corresponding empty player data map.
					else {
						
						//Add the player and their new empty player data map.
						mainLib.legendPlayers.put(cPlayer.getName(), new HashMap<String, Long>());
						mainLib.saveHash(mainLib.legendPlayers, mainLib.legendDb);
						cPlayer.sendMessage(ChatColor.GREEN + "You are now a legend-wielder.");
					}
					
					//Print player command massage to the server log.
					log.info("[PLAYER_COMMAND] " + cPlayer.getName() + ": /" + cmdString);
				}
				
				//If the user does not have permission to use "/lg" set our boolean permission check "canUse" to false.
				else {
					
					canUse = false;
				}
			}
			
			//Otherwise, checks for certain arguments having been passed to /lg.
			else {
					
				//If the first argument passed to /lg is "give" or "g", check the second argument.
				if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("g")) {
					
					if (cPlayer == null) {
						
						sender.sendMessage("You have to be a player to execute this command!");
						return true;
					}
					
					//If the user has any of the give permissions, continue onward to check the second argument passed to /lg.
					if (cPlayer.hasPermission("wol.legend.give.thor") || cPlayer.hasPermission("wol.legend.give.banhammer") || cPlayer.hasPermission("wol.legend.give.vulcan") || cPlayer.hasPermission("wol.legend.give.meteor") || cPlayer.hasPermission("wol.legend.give.demon") || cPlayer.hasPermission("wol.legend.give.glacier")) {
						
						//If fewer than 2 arguments have been passed to /lg, return false.
						if (args.length < 2) {
							
							return false;
						}
						
						//If everything is right, check for certain arguments having been passed as the second argument to /lg.
						else {
							
							//If the second argument is "thor", give the player one of the thorItem (286 (golden axe) by default) (If they have permission).
							if (args[1].equalsIgnoreCase("thor")) {
					
								//If the player has permission, give them the thor item.
								if (cPlayer.hasPermission("wol.legend.give.thor")) {
									
									//Give the player one thorItem.
									mainLib.giveItem(cPlayer, thorItem, 1);
									//Inform them that they have been given the thorItem.
									cPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "You've been given Thor's Hammer!");
								}
								
								else {
									
									canUse = false;
								}
								
							}
		
							else if (args[1].equalsIgnoreCase("banhammer")) {
								
								if (cPlayer.hasPermission("wol.legend.give.banhammer")) {
								
									mainLib.giveItem(cPlayer, banItem, 1);
									cPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "You've been given the Banhammer!");
								}
								
								else {
									
									canUse = false;
								}
							}
		
							else if (args[1].equalsIgnoreCase("vulcan")) {
								
								if (cPlayer.hasPermission("wol.legend.give.vulcan")) {
								
									mainLib.giveItem(cPlayer, vulcanItem, 1);
									cPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "You've been given Vulcan's Hammer!");
								}
								
								else {
									
									canUse = false;
								}
							}
							
							else if (args[1].equalsIgnoreCase("meteor")) {
								
								if (cPlayer.hasPermission("wol.legend.give.meteor")) {
									
									mainLib.giveItem(cPlayer, meteorItem, 1);
									cPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "You've been given the Meteor Staff!");
								}
								
								else {
									
									canUse = false;
								}
							}
							
							else if (args[1].equalsIgnoreCase("demon")) {
								
								if (cPlayer.hasPermission("wol.legend.give.demon")) {
									
									mainLib.giveItem(cPlayer, demonItem, 1);
									cPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "You've been given the Demon Scythe!");
								}
								
								else {
									
									canUse = false;
								}
							}
							
							else if (args[1].equalsIgnoreCase("glacier")) {
								
								if (cPlayer.hasPermission("wol.legend.give.glacier")) {
									
									mainLib.giveItem(cPlayer, 352, 1);
									cPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "You've been given the Glacier Staff!");
								}
								
								else {
									
									canUse = false;
								}
							}
							
							//If the second argument isn't thor, banhammer, vulcan, meteor, demon, or glacier, return false.
							else {
								
								return false;
							}
							
							log.info("[PLAYER_COMMAND] " + cPlayer.getName() + ": /" + cmdString + " " + args[0] + " " + args[1]);
						}
					}
					
					else {
						
						canUse = false;
					}
				}
					
				//Otherwise, if the first argument passed to /lg is "version" or "v", print the current version of the plugin to the user of the command.
				else if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("v")) {
					
					if (sender.hasPermission("wol.legend.version")) {
					
						if (args.length > 1) {
							
							return false;
						}
						
						//Send the user plugin version info.
						sender.sendMessage(ChatColor.LIGHT_PURPLE + "[" + pluginFile.getName() + " v" + pluginFile.getVersion() + "]");
						
						//If the command user was a player, print a player command log message.
						if (cPlayer != null) {
							
							log.info("[PLAYER_COMMAND] " + cPlayer.getName() + ": /" + cmdString + " " + args[0]);
						}
					}
					
					else {
						
						canUse = false;
					}
				}
					
				//Or if the first argument to /lg is "reload" or "r", reload the plugin's config info.
				else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
					
					if (sender.hasPermission("wol.legend.reload")) {
						
						//Let the user know that the config is being reloaded.
						sender.sendMessage(ChatColor.LIGHT_PURPLE + "Reloading Weapons_of_Legend configuration...");
						//Reload the config values in the plugin from the plugin's config file.
						reloadConfig();
						//Let the user know that the config was successfully reloaded.
						sender.sendMessage(ChatColor.LIGHT_PURPLE + "WoL reload complete.");
					}
					
					else {
						
						canUse = false;
					}
					
					if (cPlayer != null) {
						
						log.info("[PLAYER_COMMAND] " + cPlayer.getName() + ": /" + cmdString + " " + args[0]);
					}
				}
				
				//Otherwise, if the first argument passed to /lg is the name of a player that has played on this server before, 
				//but may or may not be online, attempt to toggle the specified player's legend state.
				//else if (getServer().getOfflinePlayer(args[0]) != null)
				else if (getServer().getOfflinePlayers() != null) {
					
					if (sender.hasPermission("wol.legend.toggle")) {
						
						if (args.length < 1) {
							
							return false;
						}
						
						else {
							
							//Make new Player instance from the player name provided as the second argument to /legend.
							Player player = sender.getServer().getPlayer(args[0]);
							
							//If the player specified in the second argument is offline, let the user of the command know.
							if (player == null) {
								
								sender.sendMessage(ChatColor.RED + "Sorry, " + args[0] + " is not online.");
							}
							
							//If they are online, then go ahead and toggle their legend state.
							else {
								
								//Get the names of the player using the command, and the player the command is being used on.
								String cName = sender.getName();
								String pName = player.getName();
								
								//If the legends list contains the player specified, remove them from legendPlayers.
								if (mainLib.legendPlayers.containsKey(pName)) {
								
									mainLib.legendPlayers.remove(pName);
									mainLib.saveHash(mainLib.legendPlayers, mainLib.legendDb);
									
									sender.sendMessage(ChatColor.GREEN + pName + " is no longer a legend-wielder.");
									player.sendMessage(ChatColor.LIGHT_PURPLE + cName + " has revoked your legend-wielding powers.");
								}
								
								//Otherwise add the specified player to legendPlayers.
								else {
								
									mainLib.legendPlayers.put(pName, new HashMap<String, Long>());
									mainLib.saveHash(mainLib.legendPlayers, mainLib.legendDb);
									
									sender.sendMessage(ChatColor.GREEN +  pName + " is now a legend-wielder.");
									player.sendMessage(ChatColor.LIGHT_PURPLE + cName + " has made you a legend-wielder!");
								}
							}
						}
						
						if (cPlayer != null) {
							
							log.info("[PLAYER_COMMAND] " + cPlayer.getName() + ": /" + cmdString + " " + args[0]);
						}
					}
					
					else {
						
						canUse = false;
					}
				}
					
				//If any arguments other than the ones specified are passed to /legend, return false.
				else {

					return false;
				}
			}
			
			//If the user of the command doesn't have permission to use it, let them know that they can't use the command.
			if (!canUse) {
				
				cPlayer.sendMessage(ChatColor.RED + "You don't have permission.");
			}

			//Everything went right, so return true.
			return true;
		}
		
		return false;
	}
}