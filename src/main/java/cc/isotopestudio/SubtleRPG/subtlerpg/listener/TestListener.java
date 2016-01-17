package cc.isotopestudio.SubtleRPG.subtlerpg.listener;

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
			player.sendMessage(player.getName() + "攻击了" + damagee.getType());
			event.setDamage(100.0);
		}
	}

}
