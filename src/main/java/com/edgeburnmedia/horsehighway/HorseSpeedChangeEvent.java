package com.edgeburnmedia.horsehighway;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HorseSpeedChangeEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	private final Horse horse;
	private boolean isCancelled;
	private double newSpeed;
	private final double previousSpeed;
	private final Material material;
	private final HorseManager horseManager;

	public HorseSpeedChangeEvent(HorseManager horseManager, Material material,
		double previousSpeed) {
		this.horseManager = horseManager;
		this.horse = horseManager.getHorse();
		this.material = material;
		this.previousSpeed = previousSpeed;
	}

	public Horse getHorse() {
		return horse;
	}

	public double getPreviousSpeed() {
		return previousSpeed;
	}

	public Material getMaterial() {
		return material;
	}

	public double getNewSpeed() {
		Double speed = horseManager.getPlugin().getSpeedMap().get(material);
		if (speed != null) {
			return speed;
		} else {
			return SpeedConversionUtil.calculateKphFromGenericMovementSpeed(
				horseManager.getDefaultSpeed());
		}
	}

	public Player getPlayer() {
		return horseManager.getRider();
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	/**
	 * Gets the cancellation state of this event. A cancelled event will not be executed in the
	 * server, but will still pass to other plugins
	 *
	 * @return true if this event is cancelled
	 */
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	/**
	 * Sets the cancellation state of this event. A cancelled event will not be executed in the
	 * server, but will still pass to other plugins.
	 *
	 * @param cancel true if you wish to cancel this event
	 */
	@Override
	public void setCancelled(boolean cancel) {
		this.isCancelled = cancel;
	}

	public double setHorseSpeed() {
		if (material.isAir()) { // we want to ignore the request to update the horse speed if the block is air,
			// otherwise whenever the horse jumps or goes up or down a slope the speed will
			// revert
			this.setCancelled(true);
			return getPreviousSpeed();
		}

		if (!isCancelled()) {
			if (horseManager.getPlugin().getSpeedMap().get(material) != null) {
				double speed = horseManager.getPlugin().getSpeedMap()
					.get(material); // look up the correct speed from
				// the speed table
				HorseSpeedSetterUtil.setHorseSpeedFromKph(horse, speed); // set the speed
				return speed;
			} else {
				// the table lookup must have returned null, so we should revert the horses'
				// speed to its default value
				HorseSpeedSetterUtil.setHorseSpeedFromGenericMovementSped(horse,
					horseManager.getDefaultSpeed());
				return horseManager.getDefaultSpeed();
			}
		}

		Bukkit.getLogger().log(Level.SEVERE, "Something went wrong setting the horse speed!");
		return getPreviousSpeed();

	}
}
