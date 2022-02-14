package com.edgeburnmedia.horsehighway;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

public class Speedometer {
    private static final double TIME_MAGIC_NUMBER = 0.013890542896511;
    // TIME_MAGIC_NUMBER calculated using a separate client-side speedometer mod's
    // speed reading, and the distance given and solving for time using the formula
    // speed = distance / time
    // Probably a better way to do this, but this works good enough

    public static void displaySpeedometer(PlayerMoveEvent playerMoveEvent, HorseHighway plugin) {
        if (plugin.getHorseHighwayConfig().showSpeedometer()) {
            Location firstLocation = playerMoveEvent.getFrom();
            Location secondLocation = playerMoveEvent.getTo();
            String message;
            double distanceMoved;
            double speed;

            distanceMoved = firstLocation.distance(secondLocation);
            speed = distanceMoved / TIME_MAGIC_NUMBER;

            message = "§6§lSpeed: " + SpeedConversionUtil.round(speed, 1) + " km/h";
            playerMoveEvent.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(message));

        }
    }
}
