package cc.isotopestudio.SubtleRPG.subtlerpg;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

class SubtleRPGEffect {

    static void applyEffectList(List<String> config, Player player) {
        for (String aConfig : config) {
            applyEffect(aConfig, player);
        }
    }

    static void removeEffectList(List<String> config, Player player) {
        for (String aConfig : config) {
            removeEffect(aConfig, player);
        }
    }

    private static void applyEffect(String config, Player player) {
        {
            try {
                String[] data = config.split(" ");
                int amplifier = Integer.parseInt(data[1]);
                String[] level = data[2].split("-");
                int minLevel, maxLevel = Integer.MAX_VALUE;
                minLevel = Integer.parseInt(level[0]);
                if (level.length > 1) {
                    maxLevel = Integer.parseInt(level[1]);
                }

                if (player.getLevel() >= minLevel && player.getLevel() <= maxLevel) {
                    player.addPotionEffect(
                            new PotionEffect(PotionEffectType.getByName(data[0]), Integer.MAX_VALUE, amplifier));
                    player.sendMessage("Apply " + data[0]);
                }
            } catch (Exception e) {
                player.sendMessage("SubtleRPGÅäÖÃÎÄ¼þ³ö´í");
            }
        }
    }

    private static void removeEffect(String config, Player player) {
        String type = config.split(" ")[0];
        player.removePotionEffect(PotionEffectType.getByName(type));
    }

}
