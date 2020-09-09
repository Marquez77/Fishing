package com.marquez.fishing.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.marquez.fishing.FishingPlugin;

public class DropListener implements Listener{
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		ItemStack item = e.getItemDrop().getItemStack();
		if(item.equals(FishingPlugin.item)) {
			e.setCancelled(true);
			return;
		}
		for(ItemStack i : FishingPlugin.items) {
			if(item.equals(i)) {
				e.setCancelled(true);
				return;
			}
		}
	}

}
