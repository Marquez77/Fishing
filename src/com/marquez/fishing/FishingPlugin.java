package com.marquez.fishing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.marquez.fishing.cmds.FPCmd;
import com.marquez.fishing.enums.MessageEnum;
import com.marquez.fishing.events.VehicleControlEvent;
import com.marquez.fishing.listeners.DropListener;
import com.marquez.fishing.listeners.FishingListener;
import com.marquez.fishing.util.ItemAPI;
import com.marquez.fishing.util.SoundEffect;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.PacketPlayInSteerVehicle;

public class FishingPlugin extends JavaPlugin{
	
	public static ItemStack item;
	public static ItemStack[] items = new ItemStack[4];
	public static double[] timeouts = new double[4];
	public static int[] counts = new int[4];
	public static List<String> startCommands;
	public static List<String> endCommands;
//	public static List<String> successCommands;
	public static SoundEffect FishingSnake;
	public static SoundEffect FishingSuccess;
	public static SoundEffect FishingFailed;
	public static SoundEffect CorrectControl;
	public static SoundEffect IncorrectControl;
	
	public static String npc;
	public static HashMap<String, Integer> quests = new HashMap<String, Integer>();
	
	public static FishingPlugin instance;
	
	public void addCount(Player player) {
		String uuid = player.getUniqueId().toString();
		File file = new File(getDataFolder(), "players/" + uuid);
		try {
			if(!quests.containsKey(uuid)) {
				quests.put(uuid, 0);
			}
			if(!file.exists()) {
				file.createNewFile();
			}
			int count = quests.get(uuid)+1;
			quests.put(uuid, count);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.append(count+"");
			writer.flush();
			writer.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEnable() {
		instance = this;
		loadConfig();
		getCommand("fishing").setExecutor(new FPCmd());
		File folder = new File(getDataFolder(), "players/");
		if(!folder.exists()) folder.mkdirs();
		for(File file : folder.listFiles()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				int count = Integer.parseInt(reader.readLine());
				quests.put(file.getName(), count);
				reader.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		getServer().getPluginManager().registerEvents(new DropListener(), this);
		getServer().getPluginManager().registerEvents(new FishingListener(), this);
//		getServer().getPluginManager().registerEvents(new NPCInteractListener(), this);
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketListener packetListener = new PacketAdapter(this, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Client.STEER_VEHICLE }) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				if(event.getPacketType().equals(PacketType.Play.Client.STEER_VEHICLE)) {
					PacketContainer container = event.getPacket();
					PacketPlayInSteerVehicle packet = (PacketPlayInSteerVehicle)container.getHandle();
					VehicleControlEvent customEvent = new VehicleControlEvent(event.getPlayer(), packet);
					Bukkit.getPluginManager().callEvent(customEvent);
				}
			}
		};
		protocolManager.addPacketListener(packetListener);
		
	}
	
	@Override
	public void onDisable() {
		
	}
	

	public Object getValue(FileConfiguration config, String key, Object default_value) {
		if(!config.isSet(key)) {
			config.set(key, default_value);
		}
		return config.get(key);
	}
	
	public List<?> getValueList(FileConfiguration config, String key, List<?> default_value) {
		if(!config.isSet(key)) {
			config.set(key, default_value);
		}
		return config.getList(key);
	}
	
	@SuppressWarnings("unchecked")
	public void loadConfig() {
		this.reloadConfig();
		FileConfiguration config = this.getConfig();
		npc = (String)getValue(config, "NPC", "");
		startCommands = (List<String>)getValue(config, "Options.StartCommands", Arrays.asList("cmi", "delay! 5", "cmi"));
		endCommands = (List<String>)getValue(config, "Options.EndCommands", Arrays.asList("cmi", "delay! 5", "cmi"));
//		successCommands = (List<String>)getValue(config, "Item.4.Command", Arrays.asList("cmi", "delay! 5", "cmi"));
		String[] names = new String[] { "&f[ &b문어 &f]", "&f[ &e짚신 &f]", "&d[ &c상어 &d]", "&c[ &d이무기 &c]" };
		List<String>[] itemLores = new List[] { Arrays.asList("&f아니 민물에 문어가?!"), Arrays.asList("&f짚으로 만든", "&f평범한 짚신이다."), Arrays.asList("&f으악! 상어다.", "&f물리지 않도록 조심하자"), Arrays.asList("&f용이 되기 전의 뱀.", "&f차가운 물 속에서 1000년 동안 지내면 용으로 변한 뒤", "&f여의주와 굉음과 함께 폭풍우를 불러 하늘로 날아올라간다고 여겼다.") };
		double[] default_timeouts = new double[] { 4, 4, 4, 4 };
		int[] default_counts = new int[] { 4, 5, 6, 7 }; 
		for(int i = 1; i < 5; i++) {
			int type = (int)getValue(config, "Item." + i + ".Type", 293);
			int durability = (int)getValue(config, "Item." + i + ".Durability", (int)i);
			String name = ChatColor.translateAlternateColorCodes('&', (String)getValue(config, "Item." + i + ".Name", names[i-1]));
			List<String> loreArray = (List<String>)getValueList(config, "Item." + i + ".Lore", itemLores[i-1]);
			String[] lores = new String[loreArray.size()];
			for(int j = 0; j < loreArray.size(); j++) {
				lores[j] = ChatColor.translateAlternateColorCodes('&', loreArray.get(j));
			}
			items[i-1] = ItemAPI.unbreakable(ItemAPI.makeItem(type, 1, 0, durability, name, lores));
			timeouts[i-1] = (double)getValue(config, "Difficulty." + i + ".TimeOut", default_timeouts[i-1]);
			counts[i-1] = (int)getValue(config, "Difficulty." + i + ".Count", default_counts[i-1]);
		}
		int type = (int)getValue(config, "Item.Rod.Type", 346);
		String name = ChatColor.translateAlternateColorCodes('&', (String)getValue(config, "Item.Rod.Name", "&f[ &a낚싯대 &f]"));
		List<String> loreArray = (List<String>)getValueList(config, "Item.Rod.Lore", Arrays.asList("&f대나무로 만든 낚싯대이다.", "&f오늘 운수가 좋을 것만 같다!"));
		String[] lores = new String[loreArray.size()];
		for(int i = 0; i < loreArray.size(); i++) {
			lores[i] = ChatColor.translateAlternateColorCodes('&', loreArray.get(i));
		}
		item = ItemAPI.makeItem(type, 1, 0, (short)0, name, lores);
		FishingSnake = new SoundEffect((String)getValue(config, "Sound.Fishing.Snake_Start", "ENTITY_PLAYER_LEVELUP:3:1"));
		FishingSuccess = new SoundEffect((String)getValue(config, "Sound.Fishing.Success", "ENTITY_PLAYER_LEVELUP:3:1"));
		FishingFailed = new SoundEffect((String)getValue(config, "Sound.Fishing.Failed", "BLOCK_ANVIL_LAND:3:1"));
		CorrectControl = new SoundEffect((String)getValue(config, "Sound.Control.CorrectKey", "ENTITY_EXPERIENCE_ORB_PICKUP:3:1.2"));
		IncorrectControl = new SoundEffect((String)getValue(config, "Sound.Control.IncorrectKey", "ENTITY_EXPERIENCE_ORB_PICKUP:3:0.5"));
		for(MessageEnum msgEnum : MessageEnum.values()) {
			String key = "message." + msgEnum.name().replace("_", ".");
			if(!config.isSet(key)) {
				if(msgEnum.isList()) config.set(key, msgEnum.getMessages());
				else config.set(key, msgEnum.getMessage());
				msgEnum.setMessage(msgEnum.getMessages());
			}else {
				if(config.isList(key)) msgEnum.setMessage(config.getStringList(key).toArray(new String[0]));
				else msgEnum.setMessage(config.getString(key));
			}
		}
		this.saveConfig();
		for(MessageEnum msgEnum : MessageEnum.values()) {
			if(msgEnum != MessageEnum.info_Prefix && !msgEnum.name().contains("Holder")) {
				String[] msgs = msgEnum.getMessages();
//				for(int i = 0; i < msgs.length; i++) {
//					msgs[i] = prefix + msgs[i]; 
//				}
				msgEnum.setMessage(msgs);
			}
		}
	}
	
	public void setNPC(Entity entity) {
		npc = entity.getUniqueId().toString();
		this.reloadConfig();
		FileConfiguration config = this.getConfig();
		config.set("NPC", npc);
		this.saveConfig();
	}
	

}
