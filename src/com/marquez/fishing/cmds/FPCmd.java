package com.marquez.fishing.cmds;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.marquez.fishing.FishingPlugin;
import com.marquez.fishing.enums.MessageEnum;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.npc.NPCSelector;

public class FPCmd implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			helpCommand(sender);
		}else {
			switch(args[0]) {
			case "start":
				if(!sender.hasPermission("fishing.user")) {
					sender.sendMessage(MessageEnum.info_NoPermission.getMessage());
					break;
				}
				if(args.length < 2) {
					sender.sendMessage(MessageEnum.command_Fishing_Help_Start.getMessage());
					break;
				}
				Player target = Bukkit.getPlayer(args[1]);
				if(target == null) {
					sender.sendMessage(MessageEnum.command_Fishing_Start_InvalidPlayer.getMessage());
					break;
				}
				if(FishingPlugin.quests.containsKey(target.getUniqueId().toString())) {
					sender.sendMessage(MessageEnum.command_Fishing_Start_AlreadyStarted.getMessage());
					break;
				}
				FishingPlugin.quests.put(target.getUniqueId().toString(), -1);
				FishingPlugin.instance.addCount(target);
				target.getInventory().addItem(FishingPlugin.item);
				new Thread() {
					public void run() {
						for(String cmd : FishingPlugin.startCommands) {
							if(cmd.startsWith("delay!")) {
								int delay = Integer.parseInt(cmd.replace("delay!", "").replace(" ", ""));
								try {
									Thread.sleep(delay*1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("[playername]", target.getName()));
						}
					}
				}.start();
				sender.sendMessage(MessageEnum.command_Fishing_Start_Start.getMessage());
				target.sendMessage(MessageEnum.command_Fishing_Start_StartToTarget.getMessage());
				break;
			case "npcset":
				if(!(sender instanceof Player)) {
					sender.sendMessage(MessageEnum.info_NoConsole.getMessage());
					break;
				}
				Player player = (Player)sender;
				if(!player.hasPermission("fishing.admin")) {
					player.sendMessage(MessageEnum.info_NoPermission.getMessage());
					break;
				}
				Citizens citizens = (Citizens)CitizensAPI.getPlugin();
				NPCSelector selector = citizens.getNPCSelector();
				if(selector.getSelected(sender) == null) {
					player.sendMessage(MessageEnum.command_Fishing_SetNPC_NotSelect.getMessage());
					break;
				}
				FishingPlugin.instance.setNPC(selector.getSelected(sender).getEntity());
				player.sendMessage(MessageEnum.command_Fishing_SetNPC_Set.getMessage());
				break;
			default:
				helpCommand(sender);
			}
		}
		return true;
	}
	
	public void helpCommand(CommandSender sender) {
		if(sender.hasPermission("fishing.user")) sender.sendMessage(MessageEnum.command_Fishing_Help_Start.getMessage());
		if(sender.hasPermission("fishing.admin")) sender.sendMessage(MessageEnum.command_Fishing_Help_SetNPC.getMessage());
	}

}
