package com.DragonSoulSong.Listeners; //Tells Java what package this class belongs to.

//Necessary imports.
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.DragonSoulSong.Lib.*;
import com.DragonSoulSong.Wl.WeaponsOfLegend;

//All bukkit listener classes must implement the Listener interface.
public class WlEntityDamageListener implements Listener {

	//Create an instance of the main plugin.
	private final WeaponsOfLegend plugin;
		
	public WlEntityDamageListener(WeaponsOfLegend plugin) {
			
		this.plugin = plugin;
	}
	
	//Defines the onEntityDamageByEntity method. Tells the plugin what to do when an EntityDamageByEntityEvent is triggered (one entity damages another).
	@EventHandler//This decorator tells the server that this function is meant to handle the event type specified as it's argument.
	@SuppressWarnings("deprecation")
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		//Get the damger and damagee of the event.
		Entity damager = event.getDamager();
		Entity damagee = event.getEntity();
		
		//If the damager was a lightning strike, check to see if the damagee was recently thor targetted.
		if (damager instanceof LightningStrike) {
			
			//If the damagee was recently targetted by thor's hammer, set the event's damage.
			if (damagee == mainLib.recentlyTargeted) {
				
				//Set the event's damage to thorDamage.
				event.setDamage(plugin.thorDamage);
				//Clean up the global recentlyTargetted variable.
				mainLib.recentlyTargeted = null;
			}
		}
		
		//Or if the damager was a fireball, check if the shooter was a player.
		//else if (damager instanceof Fireball) {
		else if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
			
			//Create a fireball instance of the damager.
			Fireball meteor = (Fireball) damager;
			//Get the shooter of the fireball.
			Fireball shooter = (Fireball) meteor.getShooter();
			
			//If the shooter of the fireball was a player, check if they have permission to
			//use the meteor staff.
			if (shooter instanceof Player) {
				
				//Create a player instance of the fireball's shooter.
				Player weilder = (Player) shooter;
				
				//If the shooter has permission to use the meteor staff, check if they are in legendPlayers.
				if (weilder.hasPermission("wol.use.meteor")) {
					
					//If the shooter is in legendPlayers, modify the event.
					if (mainLib.legendPlayers.containsKey(weilder.getName())) {
					
						//If the plugin is configured for the meteor fireballs to set the target on fire,
						//set the target entity on fire for meteorBurnLength seconds, converted to ticks.
						if (plugin.meteorBurn) {
				
							//If either a chance check succeeds for whether or not to ignite the target, or the shooter has permission
							//to bypass limits, ignite the target.
							if (mainLib.percentChance(plugin.meteorBurnChance) || weilder.hasPermission("wol.limits.bypass")) {
							
								damagee.setFireTicks((int) Math.ceil(plugin.meteorBurnLen * mainLib.ticksPerSecond));
							}
						}
				
						//Set the damage the fireball does to the target to meteorDamage.
						event.setDamage(plugin.meteorDamage);
					}
				}
			}
		}
		
		//Or if the damager is a player, check to see if they are holding the Demon Scythe item (golden hoe by default).
		else if (damager instanceof Player) {
			
			//Create a player instance of the damager.
			Player weilder = (Player) damager;
			
			//If the player is holding the Demon Scythe item, check if theyhave permission to
			//use the demon scythe.
			if (weilder.getItemInHand().getTypeId() == plugin.demonItem) {
			
				if (weilder.hasPermission("wol.use.demon")) {
				
					if (mainLib.legendPlayers.containsKey(weilder.getName())) {
						
						event.setDamage(plugin.demonDamage);
						//Play a mobspawner flames effect in the player's current world, at the victim's location.
						weilder.getWorld().playEffect(damagee.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
					
						//If either a chance check succeeds for whether or not to poison the target, or the player has permission
						//to bypass limits, poison the target. 
						if (mainLib.percentChance(plugin.demonPoisonChance) || weilder.hasPermission("wol.limits.bypass")) {
						
							//If the damagee is a living entity (a mob/player), poison it.
							if (damagee instanceof LivingEntity) {
							
								//Get a LivingEntity instance of the damagee.
								LivingEntity target = (LivingEntity) damagee;
							
								//Create a new potion effect for the demon poison effect.
								PotionEffect demonPoison;
							
								//If the target is undead (eg. zombie, skeleton, or zombie pigman), set the demon poison to a regeneration effect.
								if (target instanceof Zombie || target instanceof Skeleton || target instanceof PigZombie) {
								
									//Set demonPoison to a regeneration effect of duration demonPoisonLength and potion strength demonPoisonStrength.
									demonPoison = new PotionEffect(PotionEffectType.REGENERATION, (int) (plugin.demonPoisonLen * mainLib.ticksPerSecond), plugin.demonPoisonStrength);
								}
							
								//Otherwise, set demonPoison to a poison effect.
								else {
								
									demonPoison = new PotionEffect(PotionEffectType.POISON, (int) (plugin.demonPoisonLen * mainLib.ticksPerSecond), plugin.demonPoisonStrength);
								}
						
								//Apply the demon poison effect to the target.
								target.addPotionEffect(demonPoison);
							}
						}
					}
				}
			}
		}
		
		else {
			
		}
	}
}
