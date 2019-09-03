package com.niron.lgUhc;

import org.bukkit.entity.Player;

public class LgPlayer {
	
	public Player player;
	public int diamondLimit;
	public boolean isReady;
	public Roles role;
	
	public LgPlayer(Player player) {
		this.player = player;
		this.diamondLimit = 0;
		this.isReady = false;
		this.role = null;
	}
	
	public void setRole(Roles role) {
		this.role = role;
		this.setEffect();
	}
	
	public void setEffect() {
		this.role.setEffect(player);
	}
	
}
