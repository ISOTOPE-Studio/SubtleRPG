package cc.isotopestudio.SubtleRPG.subtlerpg.listener;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import cc.isotopestudio.SubtleRPG.subtlerpg.SubtleRPG;

public class TestListener implements Listener {
	private final SubtleRPG plugin;

	public TestListener(SubtleRPG instance) {
		plugin = instance;
	}

	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(plugin.prefix + player.getName() + "登陆");
		double health = plugin.getConfig().getInt((new StringBuilder("Health.Scale")).toString());
		player.setHealthScale(health);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity damagee = event.getEntity();
		if (damager instanceof Player && damagee instanceof LivingEntity) {
			Player player = (Player) damager;
			List<String> list = plugin.getConfig().getStringList("Groups");
			double addDamage = 0, addDefence = 0;
			for (int i = 0; i < list.size(); i++) {
				String tempPermission = "subtleRPG." + list.get(i);
				if (player.hasPermission(tempPermission)) {
					
					player.sendMessage("You are in group " + plugin.groupsFile.getString(list.get(i) + ".name"));
					player.sendMessage("default: " + plugin.groupsFile.getDouble(list.get(i) + ".Attack.default"));
					player.sendMessage("increasePerPeriod: "
							+ plugin.groupsFile.getDouble(list.get(i) + ".Attack.increasePerPeriod"));
					player.sendMessage("level: " + player.getLevel());
					player.sendMessage("levPeriod: " + plugin.groupsFile.getDouble(list.get(i) + ".Attack.levPeriod"));

					addDamage = plugin.groupsFile.getDouble(list.get(i) + ".Attack.default")
							+ plugin.groupsFile.getDouble(list.get(i) + ".Attack.increasePerPeriod")
									* (int) (player.getLevel()
											/ plugin.groupsFile.getDouble(list.get(i) + ".Attack.levPeriod"));
					if (addDamage > plugin.groupsFile.getDouble(list.get(i) + ".Attack.max")) {
						addDamage = plugin.groupsFile.getDouble(list.get(i) + ".Attack.max");
					}
					addDefence = 1;
					break;
				}

				if (i >= list.size() - 1) {
					addDamage = plugin.getConfig().getDouble("DefaultGroup.Attack.default") + plugin.getConfig()
							.getDouble("DefaultGroup.Attack.increasePerPeriod")
							* (int) (player.getLevel() / plugin.getConfig().getDouble("DefaultGroup.Attack.levPeriod"));
					if (addDamage > plugin.getConfig().getDouble("DefaultGroup.Attack.max")) {
						addDamage = plugin.getConfig().getDouble("DefaultGroup.Attack.max");
					}
					addDefence = 0;

				}
			}
			double newDamage = addDamage + event.getDamage();
			player.sendMessage(
					player.getName() + "攻击了" + damagee.getType() + "原伤害：" + event.getDamage() + "加成伤害" + addDamage);
			event.setDamage(newDamage);
		}
	}

}
