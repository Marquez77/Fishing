package com.marquez.fishing.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.marquez.fishing.FishingPlugin;

import net.citizensnpcs.api.event.NPCRightClickEvent;

public class NPCInteractListener implements Listener{
	
	@EventHandler
	public void onInteract(NPCRightClickEvent e) {
		if(e.getNPC().getEntity().getUniqueId().toString().equals(FishingPlugin.npc)) {
			e.setCancelled(true);
			Player player = e.getClicker();
			String uuid = player.getUniqueId().toString();
			if(FishingPlugin.quests.containsKey(uuid) && FishingPlugin.quests.get(uuid) == 4) {
				FishingPlugin.instance.addCount(player);
				player.getInventory().removeItem(FishingPlugin.item);
				player.getInventory().removeItem(FishingPlugin.items);
				new Thread() {
					public void run() {
						for(String cmd : FishingPlugin.endCommands) {
							if(cmd.startsWith("delay!")) {
								double delay = Double.parseDouble(cmd.replace("delay!", "").replace(" ", ""));
								try {
									Thread.sleep((int)(delay*1000));
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("[playername]", player.getName()));
						}
					}
				}.start();
			}
		}
	}

}
