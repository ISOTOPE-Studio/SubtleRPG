package cc.isotopestudio.SubtleRPG.subtlerpg;

import java.util.List;

import org.bukkit.entity.Arrow;
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
	public void onDamage(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity damagee = event.getEntity();

		// 玩家攻击
		if (damager instanceof Player && damagee instanceof LivingEntity) {
			Player player = (Player) damager;
			boolean isOp = player.isOp();
			if (player.isOp()) {
				player.setOp(false);
			}
			double newDamage = returnDamage(player) + event.getDamage();
			player.sendMessage(player.getName() + "攻击了" + damagee.getType() + "原伤害：" + event.getDamage() + "加成伤害"
					+ returnDamage(player));
			event.setDamage(newDamage);
			player.setOp(isOp);
		}

		// 玩家防御
		if ((damager instanceof LivingEntity || damager instanceof Arrow) && damagee instanceof Player) {
			Player player = (Player) damagee;
			boolean isOp = player.isOp();
			if (player.isOp()) {
				player.setOp(false);
			}
			double newDamage = event.getDamage() - returnDefence(player);
			player.sendMessage(
					damager.getType() + "攻击了" + player.getName() + "原伤害：" + event.getDamage() + "防御" + returnDefence(player));
			if (newDamage < 0) {
				newDamage = 0;
			}
			event.setDamage(newDamage);
			player.setOp(isOp);
		}
	}

	private double returnDamage(Player player) {
		List<String> list = plugin.getConfig().getStringList("Groups");
		double addDamage = 0;
		String group = null; // Player's group
		for (int i = 0; i < list.size(); i++) {
			String tempPermission = "subtleRPG." + list.get(i);
			if (player.hasPermission(tempPermission)) { // Need Revise
				group = list.get(i);
				addDamage = plugin.getConfig().getDouble(group + ".Attack.default")
						+ plugin.getConfig().getDouble(group + ".Attack.increasePerPeriod")
								* (int) (player.getLevel() / plugin.getConfig().getDouble(group + ".Attack.levPeriod"));
				break;
			}
			if (i >= list.size() - 1) {
				addDamage = plugin.getConfig().getDouble("DefaultGroup.Attack.default") + plugin.getConfig()
						.getDouble("DefaultGroup.Attack.increasePerPeriod")
						* (int) (player.getLevel() / plugin.getConfig().getDouble("DefaultGroup.Attack.levPeriod"));
			}
		}
		return returnDamage(player, group, addDamage);
	}

	private double returnDamage(final Player player, final String group, final double addDamage) {
		List<String> list = plugin.getConfig().getStringList(group + ".children");
		if (list == null) {
			return addDamage;
		} else {
			double newDamage = 0;
			String newGroup = null;
			for (int i = 0; i < list.size(); i++) {
				String tempPermission = "subtleRPG." + list.get(i);
				if (player.hasPermission(tempPermission)) { // Need Revise
					newGroup = list.get(i);
					newDamage = plugin.getConfig().getDouble(newGroup + ".Attack.default") + plugin.getConfig()
							.getDouble(newGroup + ".Attack.increasePerPeriod")
							* (int) (player.getLevel() / plugin.getConfig().getDouble(newGroup + ".Attack.levPeriod"));
					return returnDamage(player, newGroup, newDamage + addDamage);
				}
			}
			return addDamage;
		}
	}

	private double returnDefence(Player player) {
		List<String> list = plugin.getConfig().getStringList("Groups");
		double Defence = 0;
		String group = null; // Player's group
		for (int i = 0; i < list.size(); i++) {
			String tempPermission = "subtleRPG." + list.get(i);
			if (player.hasPermission(tempPermission)) { // Need Revise
				group = list.get(i);
				Defence = plugin.getConfig().getDouble(list.get(i) + ".Defence.default")
						+ plugin.getConfig()
								.getDouble(list.get(i)
										+ ".Defence.increasePerPeriod")
						* (int) (player.getLevel() / plugin.getConfig().getDouble(list.get(i) + ".Defence.levPeriod"));
				break;
			}
			if (i >= list.size() - 1) {
				Defence = plugin.getConfig().getDouble("DefaultGroup.Defence.default") + plugin.getConfig()
						.getDouble("DefaultGroup.Defence.increasePerPeriod")
						* (int) (player.getLevel() / plugin.getConfig().getDouble("DefaultGroup.Defence.levPeriod"));
			}
		}
		return returnDefence(player, group, Defence);
	}

	private double returnDefence(final Player player, final String group, final double addDefence) {
		List<String> list = plugin.getConfig().getStringList(group + ".children");
		if (list == null) {
			return addDefence;
		} else {
			double newDefence = 0;
			String newGroup = null;
			for (int i = 0; i < list.size(); i++) {
				String tempPermission = "subtleRPG." + list.get(i);
				if (player.hasPermission(tempPermission)) { // Need Revise

					newGroup = list.get(i);
					newDefence = plugin.getConfig().getDouble(newGroup + ".Defence.default") + plugin.getConfig()
							.getDouble(newGroup + ".Defence.increasePerPeriod")
							* (int) (player.getLevel() / plugin.getConfig().getDouble(newGroup + ".Defence.levPeriod"));
					return returnDefence(player, newGroup, newDefence + addDefence);
				}
			}
			return addDefence;
		}
	}

}
