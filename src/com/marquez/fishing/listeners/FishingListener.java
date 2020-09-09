package com.marquez.fishing.listeners;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import com.marquez.fishing.FishingPlugin;
import com.marquez.fishing.enums.MessageEnum;
import com.marquez.fishing.events.VehicleControlEvent;

public class FishingListener implements Listener{

	public HashMap<Player, Game> process = new HashMap<Player, Game>();

	@EventHandler
	public void onFishing(PlayerFishEvent e) {
		Player player = e.getPlayer();
		if(!FishingPlugin.quests.containsKey(player.getUniqueId().toString()) || player.getPassengers() == null || !(player.getPassengers().get(0) instanceof ArmorStand)) {
			e.setCancelled(true);
			return;
		}
		if(e.getState() == State.CAUGHT_ENTITY || e.getState() == State.CAUGHT_FISH) {
			e.setCancelled(true);
			String uuid = player.getUniqueId().toString();
			int index = FishingPlugin.quests.get(uuid);
			long delay = 0L;
			if(index == 3) {
				player.sendTitle(MessageEnum.event_Fishing_Snake.getMessage(), "", 10, 20, 10);
				FishingPlugin.FishingSnake.playSound(player);
				delay = 10L;
			}
			Entity hook = e.getHook();
			ArmorStand armorstand = (ArmorStand)hook.getWorld().spawnEntity(hook.getLocation(), EntityType.ARMOR_STAND);
			armorstand.setHelmet(FishingPlugin.items[index]);
			armorstand.setVisible(false);
			armorstand.setGravity(false);
			hook.addPassenger(armorstand);
			Bukkit.getScheduler().scheduleSyncDelayedTask(FishingPlugin.instance, 
					new Runnable() {
				public void run() {
					int length = FishingPlugin.counts[index];
					Game game = new Game(length);
					process.put(player, game);
					Random random = new Random();
					String[] words = new String[] { "W", "A", "S", "D" };
					for(int i = 0; i < length; i++) {
						String word = words[random.nextInt(4)];
						game.word[i] = word;
					}
					new Thread() {
						public void run() {
							while(process.containsKey(player)) {
								StringBuilder sb = new StringBuilder();
								for(int i = 0; i < game.now; i++) {
									if(game.failed && i == game.now-1) {
										sb.append("§c").append(words[i]).append(" ");
									}else sb.append("§a").append(words[i]).append(" ");
								}
								for(int i = game.now; i < game.word.length; i++) {
									sb.append("§7").append(words[i]).append(" ");
								}
								sb.setLength(sb.length()-1);
								player.sendTitle(MessageEnum.event_Fishing_Title.getMessage().replace("%words%", sb.toString()), "", 0, 20, 0);
								if(game.now == game.word.length) {
									try {
										Thread.sleep(500);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									player.getInventory().addItem(FishingPlugin.items[index]);
									process.remove(player);
									FishingPlugin.instance.addCount(player);
									FishingPlugin.FishingSuccess.playSound(player);
									player.sendMessage(MessageEnum.event_Fishing_Success.getMessage());
								}else if(game.failed) {
									try {
										Thread.sleep(500);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									process.remove(player);
									FishingPlugin.FishingFailed.playSound(player);
									player.sendMessage(MessageEnum.event_Fishing_Fail.getMessage());
								}
								try {
									Thread.sleep(1);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							hook.remove();
							armorstand.remove();
						}
					}.start();
					new Thread() {
						public void run() {
							long start = System.currentTimeMillis();
							double now, max = FishingPlugin.timeouts[index]*1000;
							BossBar bar = Bukkit.createBossBar(MessageEnum.bossbar_Fishing.getMessage().replace("%Remain_Time%", max+""), BarColor.GREEN, BarStyle.SOLID);
							bar.setProgress(1);
							bar.addPlayer(player);
							bar.setVisible(true);
							while((now = System.currentTimeMillis() - start) <= max && process.containsKey(player)) {
								bar.setTitle(MessageEnum.bossbar_Fishing.getMessage().replace("%Remain_Time%", (max-now)+""));
								bar.setProgress((max-now)/max);
								try {
									Thread.sleep(1);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							process.remove(player);
						}
					}.start();
				}
			}, delay);
		}
	}

	@EventHandler
	public void onControl(VehicleControlEvent e) {
		Player player = e.getPlayer();
		if(process.containsKey(player)) {
			Game game = process.get(player);
			String key = e.getKey();
			game.now++;
			if(!game.word[game.now].equals(key)) {
				game.failed = true;
				FishingPlugin.IncorrectControl.playSound(player);
			}else {
				FishingPlugin.CorrectControl.playSound(player);
			}
		}
	}

	public class Game {
		String[] word;
		int now;
		boolean failed;

		public Game(int length) {
			word = new String[length];
			now = 0;
			failed = false;
		}
	}

}