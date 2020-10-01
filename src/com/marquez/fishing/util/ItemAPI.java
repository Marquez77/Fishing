package com.marquez.fishing.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class ItemAPI {
	
	public static ItemStack unbreakable(ItemStack item) {
		net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = nms.getTag();
		tag.setBoolean("Unbreakable", true);
		nms.setTag(tag);
		return CraftItemStack.asCraftMirror(nms);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack makeItem(int type, int amount, int data, int durability, String display, String... lores) {
		ItemStack item = new ItemStack(type, amount);
		item.getData().setData((byte)data);
		item.setDurability((short)durability);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(display);
		meta.setLore(Arrays.asList(lores));
		item.setItemMeta(meta);
		return item;
	}
	
	private static String isSimilar(String origin, String target, String placeHolder) {
//		System.out.println(origin + " isSimilar " + target + " " + placeHolder);
		if(placeHolder != null && !placeHolder.equals("") && origin.contains(placeHolder)) {
			int index = origin.indexOf(placeHolder);
			int lastIndex = index + placeHolder.length()+1;
//			System.out.println("isSimilar " + index + " " + lastIndex);
//			System.out.println(origin.length() + "==" + lastIndex);
			if(origin.length() == lastIndex) {
				origin = origin.substring(0, index);
				String result = target.substring(index, target.length()-1);
				target = target.substring(0, index);
//				System.out.println(origin + " " + target);
//				System.out.println(result);
				if(origin.equals(target)) return result;
			}else {
				for(int i = index ; i < target.length(); i++) {
					if(target.charAt(i) == origin.charAt(lastIndex)) {
						if(target.substring(i).equals(origin.substring(lastIndex))) {
							origin = origin.replace(placeHolder, "");
							String result = target.substring(index, i-1);
							target = target.replace(result, "");
//							System.out.println("TEST " + origin + " " + target);
//							System.out.println("Result " + result);
							if(target.equals(origin)) return result;
						}
					}
				}
			}
		}
		return origin.equals(target) ? "" : null;
	}
	
	@SuppressWarnings("deprecation")
	public static List<String> compareTo(ItemStack origin, ItemStack target, String... placeHolder) {
		if(origin.getType() == target.getType()) {
			if(origin.getData().getData() == target.getData().getData()) {
				if(target.getItemMeta().getDisplayName() == null && target.getItemMeta().getLore() == null) return new ArrayList<String>();
				if(placeHolder != null) {
					List<String> array = new ArrayList<String>();
					for(String s : placeHolder) {
//						System.out.println(s);
						if(target.getItemMeta().getDisplayName() != null) {
							String result = isSimilar(origin.getItemMeta().getDisplayName(), target.getItemMeta().getDisplayName(), s);
							if(result == null) return null;
							if(!result.equals("")) array.add(result);
						}
						if(target.getItemMeta().getLore() != null) {
							List<String> list = origin.getItemMeta().getLore();
							String[] lore = list.toArray(new String[list.size()]);
							List<String> targetList = target.getItemMeta().getLore();
							String[] targetLore = targetList.toArray(new String[targetList.size()]);
							String result = isSimilar(String.join("||", lore), String.join("||", targetLore), s);
							if(result == null) return null;
							if(!result.equals("")) array.add(result);
						}
					}
					return array;
				}else { 
					if(target.getItemMeta().getDisplayName() != null) {
						if(!origin.getItemMeta().getDisplayName().equals(target.getItemMeta().getDisplayName())) {
							return null;
						}
					}
					if(target.getItemMeta().getLore() != null) {
						if(!origin.getItemMeta().getLore().equals(target.getItemMeta().getLore())) {
							return null;
						}
					}
					return new ArrayList<String>();
				}
			}
		}
		return null;
	}

}
