package cc.isotopestudio.SubtleRPG.subtlerpg;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
	public void onPlayerLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(plugin.prefix + player.getName() + "登陆");
		// double health = plugin.getConfig().getInt((new
		// StringBuilder("Health.Scale")).toString());
		// player.setHealthScale(health);
		// BarAPI.setMessage(player, "Health", 100);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity damagee = event.getEntity();

		// 玩家攻击
		if (damager instanceof Player && damagee instanceof LivingEntity) {
			Player player = (Player) damager;
			List<String> list = plugin.getConfig().getStringList("Groups");
			double addDamage = 0;
			boolean isOp = player.isOp();
			if (player.isOp()) {
				player.setOp(false);
			}
			for (int i = 0; i < list.size(); i++) {
				String tempPermission = "subtleRPG." + list.get(i);
				if (player.hasPermission(tempPermission)) {

					player.sendMessage("You are in group " + plugin.getConfig().getString(list.get(i) + ".name"));
					player.sendMessage("default: " + plugin.getConfig().getDouble(list.get(i) + ".Attack.default"));
					player.sendMessage("increasePerPeriod: "
							+ plugin.getConfig().getDouble(list.get(i) + ".Attack.increasePerPeriod"));
					player.sendMessage("level: " + player.getLevel());
					player.sendMessage("levPeriod: " + plugin.getConfig().getDouble(list.get(i) + ".Attack.levPeriod"));

					addDamage = plugin.getConfig().getDouble(list.get(i) + ".Attack.default")
							+ plugin.getConfig().getDouble(list.get(i) + ".Attack.increasePerPeriod")
									* (int) (player.getLevel()
											/ plugin.getConfig().getDouble(list.get(i) + ".Attack.levPeriod"));
					if (addDamage > plugin.getConfig().getDouble(list.get(i) + ".Attack.max")) {
						addDamage = plugin.getConfig().getDouble(list.get(i) + ".Attack.max");
					}
					break;
				}

				if (i >= list.size() - 1) {
					addDamage = plugin.getConfig().getDouble("DefaultGroup.Attack.default") + plugin.getConfig()
							.getDouble("DefaultGroup.Attack.increasePerPeriod")
							* (int) (player.getLevel() / plugin.getConfig().getDouble("DefaultGroup.Attack.levPeriod"));
					if (addDamage > plugin.getConfig().getDouble("DefaultGroup.Attack.max")) {
						addDamage = plugin.getConfig().getDouble("DefaultGroup.Attack.max");
					}
				}
			}
			double newDamage = addDamage + event.getDamage();
			player.sendMessage(
					player.getName() + "攻击了" + damagee.getType() + "原伤害：" + event.getDamage() + "加成伤害" + addDamage);
			event.setDamage(newDamage);
			player.setOp(isOp);
		}

		// 玩家防御
		if (damager instanceof LivingEntity && damagee instanceof Player) {
			Player player = (Player) damagee;
			List<String> list = plugin.getConfig().getStringList("Groups");
			double Defence = 0;
			boolean isOp = player.isOp();
			if (player.isOp()) {
				player.setOp(false);
			}
			for (int i = 0; i < list.size(); i++) {
				String tempPermission = "subtleRPG." + list.get(i);
				if (player.hasPermission(tempPermission)) {

					player.sendMessage("You are in group " + plugin.getConfig().getString(list.get(i) + ".name"));
					player.sendMessage("default: " + plugin.getConfig().getDouble(list.get(i) + ".Defence.default"));
					player.sendMessage("increasePerPeriod: "
							+ plugin.getConfig().getDouble(list.get(i) + ".Defence.increasePerPeriod"));
					player.sendMessage("level: " + player.getLevel());
					player.sendMessage("levPeriod: " + plugin.getConfig().getDouble(list.get(i) + ".Defence.levPeriod"));

					Defence = plugin.getConfig().getDouble(list.get(i) + ".Defence.default")
							+ plugin.getConfig().getDouble(list.get(i) + ".Defence.increasePerPeriod")
									* (int) (player.getLevel()
											/ plugin.getConfig().getDouble(list.get(i) + ".Defence.levPeriod"));
					if (Defence > plugin.getConfig().getDouble(list.get(i) + ".Defence.max")) {
						Defence = plugin.getConfig().getDouble(list.get(i) + ".Defence.max");
					}
					break;
				}

				if (i >= list.size() - 1) {
					Defence = plugin.getConfig().getDouble("DefaultGroup.Defence.default")
							+ plugin.getConfig().getDouble("DefaultGroup.Defence.increasePerPeriod")
									* (int) (player.getLevel()
											/ plugin.getConfig().getDouble("DefaultGroup.Defence.levPeriod"));
					if (Defence > plugin.getConfig().getDouble("DefaultGroup.Defence.max")) {
						Defence = plugin.getConfig().getDouble("DefaultGroup.Defence.max");
					}
				}
			}
			double newDamage = event.getDamage() - Defence;
			player.sendMessage(
					damager.getType() + "攻击了" + player.getName() + "原伤害：" + event.getDamage() + "防御" + Defence);
			if (newDamage < 0) {
				newDamage = 0;
			}
			event.setDamage(newDamage);
			player.setOp(isOp);
		}
	}

}
