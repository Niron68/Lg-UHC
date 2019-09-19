package com.niron.lgUhc;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class LgListeners implements Listener {

	private LgUHC lg;
	
	public LgListeners(LgUHC lg) {
		this.lg = lg;
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		LgPlayer lgPlayer = getPlayer(player);
		
		if(lg.start) {
			if(block.getType() == Material.DIAMOND_ORE) {
				if(lg.getConfig().getBoolean("diamondLimit")) {
					if(lgPlayer.diamondLimit == lg.getConfig().getInt("diamondLimitSize")) {
						player.sendMessage("§cVous ne pouvez pas miner plus de " + lg.getConfig().getInt("diamondLimitSize") + " diamants");
						event.setCancelled(true);
					}else {
						lgPlayer.diamondLimit++;
					}
				}
			}
		}else {
			event.setCancelled(true);
			player.sendMessage("§cVous ne pouvez pas casser de block tant que la partie n'a pas commencÃ©");
		}
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		World overworld = Bukkit.getWorlds().get(0);
		Location spawn = new Location(overworld, overworld.getSpawnLocation().getX(), overworld.getSpawnLocation().getY() + 10, overworld.getSpawnLocation().getZ());
		Player player = event.getPlayer();
		for(PotionEffect potion : player.getActivePotionEffects()) {
			player.removePotionEffect(potion.getType());
		}
		lg.playerList.add(new LgPlayer(player));
		player.sendMessage("§aBienvenue !");
		if(!lg.start) {
			player.teleport(spawn);
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			player.getInventory().setItem(8, createItem(Material.COMPASS, "§5Menu"));
			player.updateInventory();
		}
	}
	
	@EventHandler
	public void onLeft(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		LgPlayer lgPlayer = getPlayer(player);
		if(lgPlayer.isReady) lg.nbPret--;
		lg.playerList.remove(lgPlayer);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		if(item == null) return;
		
		if(item.getType() == Material.COMPASS && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§5Menu")) {
			Inventory inv = Bukkit.createInventory(null, 27, "§8Menu");
			
			inv.setItem(11, createItem(Material.EMERALD_BLOCK, "§aPret"));
			inv.setItem(15, createItem(Material.REDSTONE_BLOCK, "§cAnnuler"));
			
			player.openInventory(inv);
		}
		
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		Player player = (Player) event.getWhoClicked();
		Inventory inv = event.getInventory();
		ItemStack current = event.getCurrentItem();
		
		LgPlayer lgPlayer = getPlayer(player);
		
		if(current == null) return;
		
		if(!lg.start) {
			event.setCancelled(true);
			
			if(inv.getName().equalsIgnoreCase("§8Menu")) {
				if(current.getType() == Material.EMERALD_BLOCK) {
					player.closeInventory();
					if(!lgPlayer.isReady) lg.nbPret++;
					lgPlayer.isReady = true;
					Bukkit.broadcastMessage("§a" + player.getName() + " est prêt ! (" + lg.nbPret + "/" + lg.getConfig().getInt("nbPlayer") + ")");
				}else if(current.getType() == Material.REDSTONE_BLOCK) {
					player.closeInventory();
					if(lgPlayer.isReady) lg.nbPret--;
					lgPlayer.isReady = false;
					Bukkit.broadcastMessage("§c" + player.getName() + " n'est plus prêt ! (" + lg.nbPret + "/" + lg.getConfig().getInt("nbPlayer") + ")");
				}
			}
		}
		
		
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Player killer = player.getKiller();
		lg.game.lastKiller = getPlayer(killer);
	}
	
	public LgPlayer getPlayer(Player player) {
		LgPlayer res = new LgPlayer(player);
		for(LgPlayer lgPlayer : lg.playerList) {
			if(player.getName().equals(lgPlayer.player.getName())) {
				res = lgPlayer;
			}
		}
		return res;
	}
	
	public ItemStack createItem(Material item, String name) {
		ItemStack res = new ItemStack(item, 1);
		ItemMeta resM = res.getItemMeta();
		if(name != null) resM.setDisplayName(name);
		res.setItemMeta(resM);
		return res;
	}
	
}
