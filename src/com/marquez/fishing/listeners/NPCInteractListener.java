package com.marquez.fishing.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.marquez.fishing.FishingPlugin;

import net.citizensnpcs.api.event.NPCClickEvent;

public class NPCInteractListener implements Listener{
	
	@EventHandler
	public void onInteract(NPCClickEvent e) {
		if(e.getNPC().getEntity().getUniqueId().toString().equals(FishingPlugin.npc)) {
			e.setCancelled(true);
			Player player = e.getClicker();
			String uuid = player.getUniqueId().toString();
			if(FishingPlugin.quests.containsKey(uuid) && FishingPlugin.quests.get(uuid) == 4) {
				new Thread() {
					public void run() {
						for(String cmd : FishingPlugin.endCommands) {
							if(cmd.startsWith("delay!")) {
								int delay = Integer.parseInt(cmd.replace("delay!", "").replace(" ", ""));
								try {
									Thread.sleep(delay*1000);
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
