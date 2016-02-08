package cc.isotopestudio.SubtleRPG.subtlerpg;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SubtleRPGEffect {

	public static void applyEffectList(List<String> config, Player player) {
		for (int i = 0; i < config.size(); i++) {
			applyEffect(config.get(i), player);
		}
	}

	public static void removeEffectList(List<String> config, Player player) {
		for (int i = 0; i < config.size(); i++) {
			removeEffect(config.get(i), player);
		}
	}

	private static void applyEffect(String config, Player player) {
		String[] data = config.split(" ");
		int amplifier = Integer.parseInt(data[1]);
		player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(data[0]), Integer.MAX_VALUE, amplifier));
		player.sendMessage("Apply " + data[0]);
	}

	private static void removeEffect(String config, Player player) {
		String type = config.split(" ")[0];
		player.removePotionEffect(PotionEffectType.getByName(type));
	}

}
