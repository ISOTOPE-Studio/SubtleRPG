package cc.isotopestudio.SubtleRPG.subtlerpg;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

class SubtleRPGListener implements Listener {
    private final SubtleRPG plugin;
    private final String key;

    SubtleRPGListener(SubtleRPG instance) {
        plugin = instance;
        key = plugin.getConfig().getString("lore") + ": ";
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
        List<String> effectList = plugin.getConfig().getStringList(temp + ".Effect");
        if (effectList.size() > 0)
            SubtleRPGEffect.applyEffectList(effectList, player);

        // subGroups
        int count = 0;
        while (temp != null) {
            count++;
            temp = plugin.getPlayersData().getString("Players." + player.getName() + ".subGroup" + count);
            if (temp != null) {
                permissionList = plugin.getConfig().getStringList(temp + ".Perrmission");
                if (permissionList.size() > 0)
                    per.playerAddPermission(event.getPlayer(), permissionList);
                effectList = plugin.getConfig().getStringList(temp + ".Effect");
                if (effectList.size() > 0)
                    SubtleRPGEffect.applyEffectList(effectList, player);
            }
        }

    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) { // Add Effect
        final Player player = event.getPlayer();
        String temp = plugin.getPlayersData().getString("Players." + player.getName() + ".group");
        if (temp == null)
            return;
        final List<String> effectList = plugin.getConfig().getStringList(temp + ".Effect");
        if (effectList.size() > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    SubtleRPGEffect.applyEffectList(effectList, player);
                }

            }.runTaskLater(this.plugin, 20);
        }
        // subGroups
        int count = 0;
        while (temp != null) {
            count++;
            temp = plugin.getPlayersData().getString("Players." + player.getName() + ".subGroup" + count);
            if (temp != null) {
                final List<String> subeffectList = plugin.getConfig().getStringList(temp + ".Effect");
                if (effectList.size() > 0)
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            SubtleRPGEffect.applyEffectList(subeffectList, player);
                        }

                    }.runTaskLater(this.plugin, 20);
            }
        }
    }

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event) { // Add Effect
        final Player player = event.getPlayer();
        if (event.getOldLevel() < event.getNewLevel()) {
            String temp = plugin.getPlayersData().getString("Players." + player.getName() + ".group");
            if (temp == null)
                return;
            final List<String> effectList = plugin.getConfig().getStringList(temp + ".Effect");
            if (effectList.size() > 0) {
                SubtleRPGEffect.removeEffectList(effectList, player);
                SubtleRPGEffect.applyEffectList(effectList, player);
            }
            // subGroups
            int count = 0;
            while (temp != null) {
                count++;
                temp = plugin.getPlayersData().getString("Players." + player.getName() + ".subGroup" + count);
                if (temp != null) {
                    final List<String> subeffectList = plugin.getConfig().getStringList(temp + ".Effect");
                    if (effectList.size() > 0) {
                        SubtleRPGEffect.removeEffectList(subeffectList, player);
                        SubtleRPGEffect.applyEffectList(subeffectList, player);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damagee = event.getEntity();

        // 玩家攻击玩家
        if (damager instanceof Player && damagee instanceof Player) {
            Player attacker = (Player) damager;
            Player victim = (Player) damagee;

            //
            double damage = returnDamage(attacker) + event.getDamage();
            double defence = returnDefence(victim);
            if (defence < 0) defence = 0;
            double newDamage = damage - defence;

            String msg = attacker.getName() + "攻击了" + victim.getName() + "原伤害：" + event.getDamage() + "加成伤害"
                    + returnDamage(attacker) + "对方防御值" + defence;
            attacker.sendMessage(msg);
            victim.sendMessage(msg);
            if (newDamage < 0) {
                newDamage = 0;
            }
            event.setDamage(newDamage);

        } else {

            // 玩家攻击
            if (damager instanceof Player && damagee instanceof LivingEntity) {
                Player player = (Player) damager;
                double newDamage = returnDamage(player) + event.getDamage();
                //player.sendMessage(player.getName() + "攻击了" + damagee.getType() + "原伤害：" + event.getDamage() + "加成伤害" + returnDamage(player));
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
                    if (damagee instanceof Player) {
                        Player victim = (Player) damagee;

                        double damage = returnDamage(player) + event.getDamage();
                        double defence = returnDefence(victim);
                        if (defence < 0) defence = 0;
                        newDamage = damage - defence;
                        /*
                        String msg = player.getName() + "射击了" + victim.getName() + "原伤害：" + event.getDamage() + "加成伤害"
                                + returnDamage(player) + "对方防御值" + defence;
                        player.sendMessage(msg);
                        victim.sendMessage(msg);
                        */
                        if (newDamage < 0) {
                            newDamage = 0;
                        }
                        event.setDamage(newDamage);
                        return;
                    } else
                        /*
                        player.sendMessage(player.getName() + "射击了" + damagee.getType() + "原伤害：" + event.getDamage()
                                + "加成伤害" + (int) (newDamage - event.getDamage()) + "力量：" + (int) (power * 100) + "%");
                    */
                        event.setDamage(newDamage);
                }
            }

            // 玩家防御
            if ((damager instanceof LivingEntity || damager instanceof Arrow) && damagee instanceof Player) {
                Player player = (Player) damagee;
                double defence = returnDefence(player);
                if (defence < 0) defence = 0;
                double newDamage = event.getDamage() - defence;
                //player.sendMessage(damager.getType() + "攻击了" + player.getName() + "原伤害：" + event.getDamage() + "防御" + defence);
                if (newDamage < 0) {
                    newDamage = 0;
                }
                event.setDamage(newDamage);
            }
        }

    }

    private int getDefefenceFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return 0;
        for (String lore : item.getItemMeta().getLore()) {
            int index = lore.indexOf(key);
            if (index == -1) continue;
            index += key.length();
            boolean add = true;
            if (lore.charAt(index) == '-') {
                add = false;
            }
            try {
                return Integer.parseInt(lore.substring(++index)) * (add ? 1 : -1);
            } catch (Exception ignored) {
            }
        }
        return 0;
    }

    private double returnDamage(Player player) {
        double addDamage;
        String group = plugin.getPlayersData().getString("Players." + player.getName() + ".group");

        if (group != null) {
            addDamage = plugin.getConfig().getDouble(group + ".Attack.default")
                    + plugin.getConfig().getDouble(group + ".Attack.increasePerPeriod")
                    * (int) (player.getLevel() / plugin.getConfig().getDouble(group + ".Attack.levPeriod"));
            return returnDamage(player, addDamage, 1);
        } else {
            addDamage = plugin.getConfig().getDouble("DefaultGroup.Attack.default")
                    + plugin.getConfig().getDouble("DefaultGroup.Attack.increasePerPeriod")
                    * (int) (player.getLevel() / plugin.getConfig().getDouble("DefaultGroup.Attack.levPeriod"));
            return addDamage;
        }
    }

    private double returnDamage(final Player player, final double addDamage, int subGroupNum) {
        String newGroup = plugin.getPlayersData().getString("Players." + player.getName() + ".subGroup" + subGroupNum);
        if (newGroup == null) {
            return addDamage;
        } else {
            double newDamage = plugin.getConfig().getDouble(newGroup + ".Attack.default")
                    + plugin.getConfig().getDouble(newGroup + ".Attack.increasePerPeriod")
                    * (int) (player.getLevel() / plugin.getConfig().getDouble(newGroup + ".Attack.levPeriod"));
            return returnDamage(player, newDamage + addDamage, subGroupNum + 1);
        }
    }

    private double returnDefence(Player player) {
        double Defence = 0;
        for (ItemStack item : player.getEquipment().getArmorContents()) {
            Defence += getDefefenceFromItem(item);
        }
        Defence += getDefefenceFromItem(player.getItemInHand());
        String group = plugin.getPlayersData().getString("Players." + player.getName() + ".group");

        if (group != null) { // Player has a group
            Defence += plugin.getConfig().getDouble(group + ".Defence.default")
                    + plugin.getConfig().getDouble(group + ".Defence.increasePerPeriod")
                    * (int) (player.getLevel() / plugin.getConfig().getDouble(group + ".Defence.levPeriod"));
            return returnDefence(player, Defence, 1);

        } else { // Player does not have a group
            Defence += plugin.getConfig().getDouble("DefaultGroup.Defence.default") + plugin.getConfig()
                    .getDouble("DefaultGroup.Defence.increasePerPeriod")
                    * (int) (player.getLevel() / plugin.getConfig().getDouble("DefaultGroup.Defence.levPeriod"));
            return Defence;
        }
    }

    private double returnDefence(final Player player, final double addDefence, int subGroupNum) {
        String newGroup = plugin.getPlayersData().getString("Players." + player.getName() + ".subGroup" + subGroupNum);
        if (newGroup == null) {
            return addDefence;
        } else {
            double newDefence = plugin.getConfig().getDouble(newGroup + ".Defence.default")
                    + plugin.getConfig().getDouble(newGroup + ".Defence.increasePerPeriod")
                    * (int) (player.getLevel() / plugin.getConfig().getDouble(newGroup + ".Defence.levPeriod"));
            return returnDefence(player, newDefence + addDefence, subGroupNum + 1);
        }
    }

}
