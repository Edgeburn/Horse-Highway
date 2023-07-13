/*
 * Copyright (c) 2023 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighway;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class HorseHighwayListeners implements Listener {

	HorseHighway plugin;

	public HorseHighwayListeners(HorseHighway plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Entity playerVehicle = event.getPlayer().getVehicle();
		Material playerStandingOn = event
			.getPlayer()
			.getLocation()
			.getBlock()
			.getRelative(BlockFace.DOWN)
			.getType();
		Material feet = event.getPlayer().getLocation().getBlock().getType();
		Material speedMaterial;

		if (!feet.isAir()) {
			speedMaterial = feet;
		} else {
			speedMaterial = playerStandingOn;
		}

		if (playerVehicle instanceof AbstractHorse vehicle) { // first we want to check that the player's vehicle isn't null, and if it is we just want to ignore and do nothing further
			if (plugin.getHorseManagers().containsKey(vehicle)) {
				plugin.getHorseManagers().get(vehicle).updateSpeed(
					speedMaterial); // tell the horse manager to update the speed with the block the player is currently standing on
				Speedometer.displaySpeedometer(event, plugin);
			} else {
				plugin.registerHorse(vehicle, event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onMount(EntityMountEvent event) {
		if (event.getEntity() instanceof Player rider
			&& event.getMount() instanceof AbstractHorse horse) {
			plugin.registerHorse(horse, rider);
		}
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event) {
		Entity playerVehicle = event.getPlayer().getVehicle();
		plugin.deregisterHorse(event.getPlayer(), playerVehicle);
	}

	@EventHandler
	public void onPlayerDismount(EntityDismountEvent event) {
		if (
			event.getEntity() instanceof Player rider &&
				event.getDismounted() instanceof AbstractHorse
		) {
			plugin.deregisterHorse(rider, event.getDismounted());
		}
	}
}
