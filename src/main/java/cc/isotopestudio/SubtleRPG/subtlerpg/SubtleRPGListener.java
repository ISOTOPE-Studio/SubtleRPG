﻿package cc.isotopestudio.SubtleRPG.subtlerpg;

import java.util.List;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import cc.isotopestudio.SubtleRPG.subtlerpg.SubtleRPG;

public class SubtleRPGListener implements Listener {
	private final SubtleRPG plugin;

	public SubtleRPGListener(SubtleRPG instance) {
		plugin = instance;
	}

	@EventHandler
	public void onLogin(PlayerJoinEvent event) { // Add Permission
		Player player = event.getPlayer();
		SubtleRPGPermission per = new SubtleRPGPermission(plugin);

		// Group
		String temp = plugin.getPlayersData().getString("Players." + player.getName() + ".group");
		if (temp == null)
			return;
		List<String> permissionList = plugin.getConfig().getStringList(temp + ".Perrmission");
		if (permissionList.size() > 0)
			per.playerAddPermission(event.getPlayer(), permissionList);

		// subGroups
		int count = 0;
		while (temp != null) {
			count++;
			temp = plugin.getPlayersData().getString("Players." + player.getName() + ".subGroup" + count);
			if (temp != null) {
				permissionList = plugin.getConfig().getStringList(temp + ".Perrmission");
				if (permissionList.size() > 0)
					per.playerAddPermission(event.getPlayer(), permissionList);
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onDamage(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity damagee = event.getEntity();

		// 玩家攻击
		if (damager instanceof Player && damagee instanceof LivingEntity) {
			Player player = (Player) damager;
			double newDamage = returnDamage(player) + event.getDamage();
			player.sendMessage(player.getName() + "攻击了" + damagee.getType() + "原伤害：" + event.getDamage() + "加成伤害"
					+ returnDamage(player));
			event.setDamage(newDamage);
		}

		// Arrow的 攻击
		if (damager instanceof Arrow) {
			Arrow arrow = (Arrow) damager;
			if (arrow.getShooter() instanceof Player) {
				Player player = (Player) arrow.getShooter();
				double power = event.getDamage() / 8.0;
				if (power > 10.0 / 8.0) {
					power = 10.0 / 8.0;
				}
				double newDamage = power * returnDamage(player) + event.getDamage();
				player.sendMessage(player.getName() + "射击了" + damagee.getType() + "原伤害：" + event.getDamage() + "加成伤害"
						+ (int) (newDamage - event.getDamage()) + "力量：" + (int) (power * 100) + "%");
				event.setDamage(newDamage);
			}
		}

		// 玩家防御
		if ((damager instanceof LivingEntity || damager instanceof Arrow) && damagee instanceof Player) {
			Player player = (Player) damagee;
			double newDamage = event.getDamage() - returnDefence(player);
			player.sendMessage(damager.getType() + "攻击了" + player.getName() + "原伤害：" + event.getDamage() + "防御"
					+ returnDefence(player));
			if (newDamage < 0) {
				newDamage = 0;
			}
			event.setDamage(newDamage);
		}
	}

	public double returnDamage(Player player) {
		double addDamage = 0;
		String group = plugin.getPlayersData().getString("Players." + player.getName() + ".group");

		if (group != null) {
			addDamage = plugin.getConfig().getDouble(group + ".Attack.default")
					+ plugin.getConfig().getDouble(group + ".Attack.increasePerPeriod")
							* (int) (player.getLevel() / plugin.getConfig().getDouble(group + ".Attack.levPeriod"));
			return returnDamage(player, group, addDamage, 1);
		} else {
			addDamage = plugin.getConfig().getDouble("DefaultGroup.Attack.default")
					+ plugin.getConfig().getDouble("DefaultGroup.Attack.increasePerPeriod")
							* (int) (player.getLevel() / plugin.getConfig().getDouble("DefaultGroup.Attack.levPeriod"));
			return addDamage;
		}
	}

	private double returnDamage(final Player player, final String group, final double addDamage, int subGroupNum) {
		String newGroup = plugin.getPlayersData().getString("Players." + player.getName() + ".subGroup" + subGroupNum);
		if (newGroup == null) {
			return addDamage;
		} else {
			double newDamage = plugin.getConfig().getDouble(newGroup + ".Attack.default")
					+ plugin.getConfig().getDouble(newGroup + ".Attack.increasePerPeriod")
							* (int) (player.getLevel() / plugin.getConfig().getDouble(newGroup + ".Attack.levPeriod"));
			return returnDamage(player, newGroup, newDamage + addDamage, subGroupNum + 1);
		}
	}

	public double returnDefence(Player player) {
		double Defence = 0;
		String group = plugin.getPlayersData().getString("Players." + player.getName() + ".group");

		if (group != null) { // Player has a group
			Defence = plugin.getConfig().getDouble(group + ".Defence.default")
					+ plugin.getConfig().getDouble(group + ".Defence.increasePerPeriod")
							* (int) (player.getLevel() / plugin.getConfig().getDouble(group + ".Defence.levPeriod"));
			return returnDefence(player, group, Defence, 1);

		} else { // Player does not have a group
			Defence = plugin.getConfig().getDouble("DefaultGroup.Defence.default") + plugin.getConfig()
					.getDouble("DefaultGroup.Defence.increasePerPeriod")
					* (int) (player.getLevel() / plugin.getConfig().getDouble("DefaultGroup.Defence.levPeriod"));
			return Defence;
		}
	}

	private double returnDefence(final Player player, final String group, final double addDefence, int subGroupNum) {
		String newGroup = plugin.getPlayersData().getString("Players." + player.getName() + ".subGroup" + subGroupNum);
		if (newGroup == null) {
			return addDefence;
		} else {
			double newDefence = plugin.getConfig().getDouble(newGroup + ".Defence.default")
					+ plugin.getConfig().getDouble(newGroup + ".Defence.increasePerPeriod")
							* (int) (player.getLevel() / plugin.getConfig().getDouble(newGroup + ".Defence.levPeriod"));
			return returnDefence(player, newGroup, newDefence + addDefence, subGroupNum + 1);
		}
	}

}
