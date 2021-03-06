package com.edgeburnmedia.horsehighway;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class HorseHighwayListeners implements Listener {
    HorseHighway plugin;

    public HorseHighwayListeners(HorseHighway plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Entity playerVehicle = event.getPlayer().getVehicle();
        Material playerStandingOn = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType();

        if (playerVehicle != null) { // first we want to check that the player's vehicle isn't null, and if it is we
                                     // just want to ignore and do nothing further
            if (playerVehicle.getType() == EntityType.HORSE) { // now that we know it is not null, we can check if it's
                                                               // a horse
                plugin.getHorseManagers().get((Horse) playerVehicle).updateSpeed(playerStandingOn); // tell the horse
                                                                                                    // manager to update
                                                                                                    // the speed with
                                                                                                    // the block the
                                                                                                    // player is
                                                                                                    // currently
                                                                                                    // standing on
                Speedometer.displaySpeedometer(event, plugin);
            }
        }
    }

    @EventHandler
    public void onMount(EntityMountEvent event) {
        Entity mount = event.getMount();
        Player rider = (Player) event.getEntity();
        if (mount.getType() == EntityType.HORSE) {
            plugin.registerHorse((Horse) event.getMount(), rider);
        }
    }

}
