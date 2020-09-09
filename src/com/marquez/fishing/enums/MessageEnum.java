package com.marquez.fishing.enums;

import org.bukkit.ChatColor;

public enum MessageEnum {

	info_Prefix("[Fishing] "),
	info_NoPermission("&cYou don't have permission."),
	info_NoConsole("&cThis command can not use on console."),
	info_InvalidUsage("&cInvalid Usage"),
	command_Fishing_Help_Start("/fishing start [playername]"),
	command_Fishing_Help_SetNPC("/fishing npcset"),
	command_Fishing_Start_InvalidPlayer("Invalid player."),
	command_Fishing_Start_Start("Start fishing quest"),
	command_Fishing_Start_StartToTarget("Start fishing quest!"),
	command_Fishing_Start_AlreadyStarted("The user already started fishing quest."),
	command_Fishing_SetNPC_NotSelect("You are not selected the NPC"),
	command_Fishing_SetNPC_Set("Set the quest npc"),
	event_Fishing_Success("&f[&e!&f] 무언가를 낚았습니다!"),
	event_Fishing_Fail("&c[!] 이런! 물고기가 도망갔습니다."),
	event_Fishing_Snake("거대한 무언가가 낚였습니다!"),
	event_Fishing_Title("%words%"),
	event_Fishing_SubTitle("Please enter the keys in the time."),
	bossbar_Fishing("남은 시간: %Remain_Time%");

	private String[] message;

	private MessageEnum(String... message) {
		this.message = message;
	}

	public boolean isList() {
		return message.length > 1;
	}

	public String getMessage() {
		return this.message[0];
	}

	public String[] getMessages() {
		return this.message;
	}

	public void setMessage(String... message) {
		for(int i = 0; i < message.length; i++) {
			message[i] = ChatColor.translateAlternateColorCodes('&', message[i]);
		}
		this.message = message;
	}

}

