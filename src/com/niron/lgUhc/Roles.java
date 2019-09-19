package com.niron.lgUhc;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Roles {

	public String name;
	public String rolename;
	private LgUHC lg;
	private List<PotionEffect> effectList;
	private List<PotionEffect> nighteffectList;
	private List<PotionEffect> dayeffectList;
	public boolean active;
	
	public Roles(String rolename, LgUHC lg) {
		this.lg = lg;
		this.effectList = new ArrayList<>();
		this.nighteffectList = new ArrayList<>();
		this.dayeffectList = new ArrayList<>();
		this.rolename = rolename;
		this.name = lg.getConfig().getString("roles." + rolename + ".name");
		if(lg.getConfig().contains("roles." + rolename + ".effect")) {
			List<String> effectName = lg.getConfig().getStringList("roles." + rolename + ".effect");
			List<Integer> effectLevel = lg.getConfig().getIntegerList("roles." + rolename + ".effectlevel");
			for(int i = 0; i < effectName.size(); i++) {
				this.effectList.add(PotionEffectType.getByName(effectName.get(i)).createEffect(Integer.MAX_VALUE, effectLevel.get(i)));
			}
		}
		if(lg.getConfig().contains("roles." + rolename + ".night_effect")) {
			List<String> nighteffectName = lg.getConfig().getStringList("roles." + rolename + ".night_effect");
			List<Integer> nighteffectLevel = lg.getConfig().getIntegerList("roles." + rolename + ".night_effectlevel");
			System.out.println(rolename);
			for(int i = 0; i < nighteffectName.size(); i++) {
				System.out.println(nighteffectName.get(i));
				System.out.println(nighteffectLevel.get(i));
				this.nighteffectList.add(PotionEffectType.getByName(nighteffectName.get(i)).createEffect(Integer.MAX_VALUE, nighteffectLevel.get(i)));
			}
		}
		if(lg.getConfig().contains("roles." + rolename + ".day_effect")) {
			List<String> dayeffectName = lg.getConfig().getStringList("roles." + rolename + ".day_effect");
			List<Integer> dayeffectLevel = lg.getConfig().getIntegerList("roles." + rolename + ".day_effectlevel");
			for(int i = 0; i < dayeffectName.size(); i++) {
				this.dayeffectList.add(PotionEffectType.getByName(dayeffectName.get(i)).createEffect(Integer.MAX_VALUE, dayeffectLevel.get(i)));
			}
		}
	}
	
	public void setEffect(Player player) {
		for(PotionEffect effect : effectList) {
			player.addPotionEffect(effect);
		}
		if(lg.game.world.getTime() > 12575 && lg.game.world.getTime() < 23459) {
			for(PotionEffect nighteffect : nighteffectList) {
				player.addPotionEffect(nighteffect);
			}
		}else {
			for(PotionEffect nighteffect : nighteffectList) {
				player.removePotionEffect(nighteffect.getType());
			}
		}
	}
	
	public void setBonusEffect(Player player) {
		List<String> loup = new ArrayList<>();
		loup.add("werewolf");
		loup.add("werewolfB");
		loup.add("werewolfF");
		loup.add("werewolfA");
		if(loup.contains(this.rolename)) {
			if(lg.game.lastKiller != null && loup.contains(lg.game.lastKiller.role.rolename)) {
				player.addPotionEffect(PotionEffectType.getByName("SPEED").createEffect(120, 1));
				player.addPotionEffect(PotionEffectType.getByName("ABSORPTION").createEffect(120, 6));
			}
		}
	}
	
}
