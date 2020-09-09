package com.marquez.fishing.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import net.minecraft.server.v1_12_R1.PacketPlayInSteerVehicle;

public class VehicleControlEvent extends PlayerEvent{
	
	public static final HandlerList handlers;
	private PacketPlayInSteerVehicle packet;
	
	static {
		handlers = new HandlerList();
	}

	public VehicleControlEvent(Player who, PacketPlayInSteerVehicle packet) {
		super(who);
	}

	@Override
	public HandlerList getHandlers() {
		return VehicleControlEvent.handlers;
	}
	
	public HandlerList getHandlerList() {
		return VehicleControlEvent.handlers;
	}
	
	public float getSideways() {
		return this.packet.a();
	}
	
	public float getForwards() {
		return this.packet.b();
	}
	
	public boolean isJump() {
		return this.packet.c();
	}
	
	public boolean isDismount() {
		return this.packet.d();
	}
	
	public String getKey() {
		if(getForwards() > 0) {
			return "W";
		}else if(getForwards() < 0) {
			return "S";
		}else if(getSideways() > 0) {
			return "A";
		}else if(getSideways() < 0) {
			return "D";
		}
		return "";
	}

}
