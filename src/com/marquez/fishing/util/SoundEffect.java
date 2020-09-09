package com.marquez.fishing.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundEffect {
	
	private Sound sound;
	private float volume;
	private float pitch;
	
	public SoundEffect(String sound, float volume, float pitch) {
		this.sound = Sound.valueOf(sound);
		this.volume = volume;
		this.pitch = pitch;
	}
	
	public SoundEffect(String data) {
		String[] splited = data.split(":");
		this.sound = Sound.valueOf(splited[0]);
		this.volume = Float.parseFloat(splited[1]);
		this.pitch = Float.parseFloat(splited[2]);
	}

	public Sound getSound() {
		return sound;
	}

	public void setSound(Sound sound) {
		this.sound = sound;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(sound.name()).append(":").append(volume).append(":").append(pitch);
		return sb.toString();
	}
	
	public void playSound(Player... players) {
		for(Player p : players) {
			p.playSound(p.getLocation(), this.sound, this.volume, this.pitch);
		}
	}
	
	public void playSoundLoc(Location location, Player... players) {
		for(Player p : players) {
			p.playSound(location, this.sound, this.volume, this.pitch);
		}
	}
	
	public void playSoundLoc(Location location) {
		location.getWorld().playSound(location, this.sound, this.volume, this.pitch);
	}
	
}
