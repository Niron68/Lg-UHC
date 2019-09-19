package com.niron.lgUhc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Game {
	
	private static Random random = new Random();
	public boolean start;
	private LgUHC lg;
	public List<LgPlayer> playerList;
	private WorldCreator worldCreator = new WorldCreator("lg");
	public World world;
	public WorldBorder wb;
	private int mapNumber = -1;
	private List<Long> seedList;
	private List<Roles> roleList = new ArrayList<>();
	public LgPlayer lastKiller;
	
	public Game(LgUHC lg) {
		this.lg = lg;
		this.start = false;
		this.playerList = new ArrayList<>();
		System.out.println("Partie cr�er !");
		List<String> rolename = lg.getConfig().getStringList("role_priority");
		for(String role : rolename) {
			this.roleList.add(new Roles(role, lg));
		}
	}

	public void startGame() {
		this.start = true;
		this.lg.start = true;
		Bukkit.broadcastMessage("§4Début de la partie !!!");
		this.createWorld();
		for(LgPlayer lgPlayer : lg.playerList) {
			Location tp = new Location(this.world, 0, 0, 0);
			if(lgPlayer.isReady) {
				tp.setY(world.getHighestBlockYAt((int)tp.getX(), (int)tp.getZ()));
				lgPlayer.player.getInventory().clear();
				lgPlayer.player.setGameMode(GameMode.SURVIVAL);
				this.playerList.add(lgPlayer);
				lgPlayer.player.getInventory().clear();
				this.giveKit(lgPlayer, "base");
			}else{
				lgPlayer.player.setGameMode(GameMode.SPECTATOR);
			}
			lgPlayer.player.setHealth(20);
			lgPlayer.player.setFoodLevel(20);
			lgPlayer.player.teleport(tp);
		}
	}
	
	public LgPlayer getPlayer(Player player) {
		LgPlayer res = new LgPlayer(player);
		for(LgPlayer lgPlayer : this.playerList) {
			if(player.getName().equals(lgPlayer.player.getName())) {
				res = lgPlayer;
			}
		}
		return res;
	}
	
	private void giveKit(LgPlayer lgPlayer, String name) {
		if(lg.getConfig().contains("roles." + name + ".item")){
			ConfigurationSection item = lg.getConfig().getConfigurationSection("roles." + name + ".item");
			Set<String> section = item.getKeys(false);
			for(String part : section) {
				ItemStack it = new ItemStack(Material.valueOf(item.getString(part + ".name")), item.getInt(part + ".quantity"));
				ItemMeta mit = it.getItemMeta();
				List<String> enchantList = item.getStringList(part + ".enchant");
				List<Integer> enchantLvlList = item.getIntegerList(part + ".enchantlevel");
				for(int i = 0; i < enchantList.size(); i++) {
					mit.addEnchant(Enchantment.getByName(enchantList.get(i)), enchantLvlList.get(i), true);
				}
				it.setItemMeta(mit);
				lgPlayer.player.getInventory().addItem(it);
				lgPlayer.player.updateInventory();
			}
		}
	}
	
	public void createWorld() {
		this.world = lg.getServer().getWorld("lg");
		Bukkit.unloadWorld(world, false);
		//System.out.println(world == null);
		//System.out.println(world.getWorldFolder() == null);
		if(world != null && world.getWorldFolder() != null)
			System.out.println("Monde supprime : " + deleteFolder(world.getWorldFolder()));
		this.seedList = lg.getConfig().getLongList("map.seed");
		this.mapNumber = random.nextInt(this.seedList.size());
		worldCreator.seed(seedList.get(this.mapNumber));
		this.world = worldCreator.createWorld();
		this.wb = world.getWorldBorder();
		wb.setCenter(0,0);
		wb.setSize(1000);
	}
	
	public static boolean deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    boolean res = folder.delete();
	    return res;
	}
	
	public void setRole() {
		Bukkit.broadcastMessage("Distribution des roles");
		List<Roles> list = new ArrayList<>();
		for(Roles role : roleList) {
			list.add(role);
		}
		for(int i = list.size(); i > this.playerList.size(); i--) {
			list.remove(i-1);
		}
		Collections.shuffle(list);
		Bukkit.broadcastMessage("" + list.size());
		for(int i = 0; i < this.playerList.size(); i++) {
			this.playerList.get(i).setRole(list.get(i));
			this.giveKit(this.playerList.get(i), list.get(i).rolename);
			Bukkit.broadcastMessage(list.get(i).rolename);
		}
		Bukkit.broadcastMessage("Fin de la distribution");
	}
	
	public void updateEffect() {
		for(LgPlayer lgPlayer : playerList) {
			lgPlayer.setEffect();
		}
	}
	
}
