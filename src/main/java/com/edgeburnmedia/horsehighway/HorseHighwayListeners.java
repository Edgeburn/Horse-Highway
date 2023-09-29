/*
 * Copyright (c) 2023 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighway;

import org.bukkit.Material;
import org.bukkit.block.Block;
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
import java.util.List;

public class HorseHighwayListeners implements Listener {

	/**
	 * Materials that should use the block below them for calculating their speed,
	 * such as rails, snow layers, and torches, since these do not have an entity hitbox (or their
	 * hitbox is small enough that one would not want these to effect their speed)
	 */
	private static final List<Material> IGNORED_MATERIALS = List.of(Material.RAIL,
		Material.POWERED_RAIL,
		Material.ACTIVATOR_RAIL,
		Material.DETECTOR_RAIL,
		Material.TORCH,
		Material.WALL_TORCH,
		Material.REDSTONE_TORCH,
		Material.REDSTONE_WALL_TORCH,
		Material.SOUL_TORCH,
		Material.SOUL_WALL_TORCH,
		Material.SNOW,
		Material.STONE_PRESSURE_PLATE // specifically used for TrainCarts intersections
		);

	HorseHighway plugin;

	public HorseHighwayListeners(HorseHighway plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Entity playerVehicle = event.getPlayer().getVehicle();
		Block playerStandingOn = event
			.getPlayer()
			.getLocation()
			.getBlock()
			.getRelative(BlockFace.DOWN);
		Block feet = event.getPlayer().getLocation().getBlock();
		Block speedBlock;

		if (!feet.getType().isAir()) {
			speedBlock = feet;
		} else {
			speedBlock = playerStandingOn;
		}

		if (IGNORED_MATERIALS.contains(speedBlock.getType())) {
			speedBlock = speedBlock.getRelative(0, -1, 0);
		}

		if (playerVehicle instanceof AbstractHorse vehicle) { // first we want to check that the player's vehicle isn't null, and if it is we just want to ignore and do nothing further
			if (plugin.getHorseManagers().containsKey(vehicle)) {
				plugin.getHorseManagers().get(vehicle).updateSpeed(
					speedBlock); // tell the horse manager to update the speed with the block the player is currently standing on
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
