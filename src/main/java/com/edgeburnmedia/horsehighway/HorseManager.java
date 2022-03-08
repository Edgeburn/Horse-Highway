package com.edgeburnmedia.horsehighway;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class HorseManager {
    private Horse horse;
    private HorseHighway plugin;
    private Player rider;
    private double speed;

    HorseManager(Horse horse, Player rider, HorseHighway plugin) {
        this.horse = horse;
        this.plugin = plugin;
        this.rider = rider;
    }

    public void updateSpeed(Material material) {
        if (rider.hasPermission("horsehighway.use")) {
            HorseSpeedChangeEvent speedChangeEvent = new HorseSpeedChangeEvent(this, material, speed);
            Bukkit.getPluginManager().callEvent(new HorseSpeedChangeEvent(this, material, speed));
            if (!speedChangeEvent.isCancelled()) {
                speed = speedChangeEvent.setHorseSpeed();
            }
        }
    }

    public Horse getHorse() {
        return horse;
    }

    public HorseHighway getPlugin() {
        return plugin;
    }

    public Player getRider() {
        return rider;
    }

    public double getDefaultSpeed() {
        return SpeedConversionUtil.calculateGenericMovementSpeedFromKph(getPlugin().getHorseHighwayConfig().getDefaultSpeedInKph());
    }
}
