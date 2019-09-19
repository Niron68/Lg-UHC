package com.niron.lgUhc;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;

import commands.CommandsLg;

public class LgUHC extends JavaPlugin {
	
	public boolean start;
	public boolean pvp = false;
	public boolean retrecir = false;
	public boolean role = false;
	public List<LgPlayer> playerList = new ArrayList<>();
	public int nbPret;
	public int count = 10;
	public int timer = 0;
	public Game game;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		saveDefaultConfig();
		this.game = new Game(this);
		this.start = this.game.start;
		getServer().getPluginManager().registerEvents(new LgListeners(this), this);
		getCommand("lg").setExecutor(new CommandsLg(this));
		
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for(Player player : players) {
			this.game.playerList.add(new LgPlayer(player));
		}
		
		Bukkit.getScheduler().runTaskTimer(this, new BukkitRunnable() {

			@Override
			public void run() {
				
				if(!start) {
					if(nbPret >= getConfig().getInt("nbPlayeumlr")) {
						Bukkit.broadcastMessage("§2La partie commence dans " + count);
						count--;
						if(count == 0) {
							game.startGame();
						}
					}else if(count != 10) {
						Bukkit.broadcastMessage("§cPartie annulé !");
						count = 10;
					}
				}else {
					timer++;
					//game.world.setTime(game.world.getTime() + 20);
					if(timer == 1*10) {
						//Attribution des roles
						game.setRole();
						role = true;
					}else if(timer == 100*60){
						//Début du retrecissement de la border
						retrecir = true;
					}
					
					if(role) {
						game.updateEffect();
					}
					
					if(retrecir) {
						if(game.wb.getSize() > 250) {
							game.wb.setSize(game.wb.getSize() - 1);
						}
					}
					
				}
			}
			
		}, 0, 20);
	}
	
	@Override
	public void onDisable() {
		
	}
}
