package cc.isotopestudio.SubtleRPG.subtlerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

class SubtleRPGCommand implements CommandExecutor {
    private final SubtleRPG plugin;
    private final SubtleRPGPermission per;

    SubtleRPGCommand(SubtleRPG plugin) {
        this.plugin = plugin;
        per = new SubtleRPGPermission(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("SubtleRPG")) {
            if (args.length > 0 && !args[0].equals("help")) {
                if (args[0].equals("info")) {
                    if ((sender instanceof Player && (args.length == 1 || args.length == 2))
                            || (!(sender instanceof Player) && args.length == 2)) {
                        if (args.length == 2) {
                            Player player = (Bukkit.getServer().getPlayer(args[1]));
                            if (player == null) {
                                sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("���")
                                        .append(args[1]).append("������").toString());
                                return true;
                            } else {// Core
                                sendInfo(sender, player);
                                return true;
                            }
                        } else {// Core
                            sendInfo(sender, (Player) sender);
                            return true;
                        }

                    } else {
                        sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
                                .append("/subtleRPG info <�������>").toString());
                    }
                }

                if (args[0].equals("join")) {
                    if (sender.hasPermission("subtleRPG.join") || sender.isOp()) {
                        if (args.length == 3) {
                            Player player = (Bukkit.getServer().getPlayer(args[1]));
                            if (player == null) {
                                sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("���")
                                        .append(args[1]).append("������").toString());
                                return true;
                            } else { // Core
                                List<String> list = plugin.getConfig().getStringList("Groups");
                                String newJob = null, newJobName = null;
                                String oldJob = plugin.getPlayersData()
                                        .getString("Players." + player.getName() + ".group");
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).equals(args[2])) {
                                        newJob = list.get(i);
                                        newJobName = plugin.getConfig().getString(newJob + ".name");
                                        break;
                                    }
                                    if (i == list.size() - 1) {
                                        sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
                                                .append("ְҵ").append(args[2]).append("������").toString());
                                        return true;
                                    }
                                }
                                assert newJob != null;
                                if (newJob.equals(oldJob)) {
                                    sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
                                            .append("���Ѿ���").append(newJobName).append("��").toString());
                                    return true;
                                }
                                List<String> effectList = plugin.getConfig().getStringList(oldJob + ".Effect");
                                if (effectList.size() > 0)
                                    SubtleRPGEffect.removeEffectList(effectList, player);

                                // Delete Subgroups
                                String temp = "";
                                int count = 0;
                                while (temp != null) {
                                    count++;
                                    temp = plugin.getPlayersData()
                                            .getString("Players." + player.getName() + ".subGroup" + count);
                                    if (temp != null) {
                                        // Delete Permission
                                        List<String> permissionList = plugin.getConfig()
                                                .getStringList(temp + ".Perrmission");
                                        if (permissionList != null)
                                            per.playerRemovePermission(player, permissionList);

                                        effectList = plugin.getConfig().getStringList(temp + ".Effect");
                                        if (effectList.size() > 0)
                                            SubtleRPGEffect.removeEffectList(effectList, player);

                                        // Delete playersData
                                        plugin.getPlayersData().set("Players." + player.getName() + ".subGroup" + count,
                                                null);
                                    }
                                }
                                // Add Permission
                                List<String> permissionList = plugin.getConfig().getStringList(newJob + ".Perrmission");
                                if (permissionList.size() > 0)
                                    per.playerAddPermission(player, permissionList);
                                // Add effect
                                effectList = plugin.getConfig().getStringList(newJob + ".Effect");
                                if (effectList.size() > 0)
                                    SubtleRPGEffect.applyEffectList(effectList, player);

                                plugin.getPlayersData().set("Players." + player.getName() + ".group", newJob);
                                plugin.savePlayersData();

                                if (!player.getName().equals(sender.getName())) {
                                    sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA)
                                            .append("�������ְҵ").append(newJobName).append("��").toString());
                                }
                                sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA)
                                        .append(player.getName()).append("������").append(newJobName).append("��")
                                        .toString());
                                return true;
                            }
                        } else {
                            sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
                                    .append("/subtleRPG join <�������> <ְҵ>").toString());
                            return true;
                        }
                    } else {
                        sender.sendMessage(
                                (new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("��û��Ȩ��").toString());
                        return true;
                    }
                }

                if (args[0].equals("joinsub")) {
                    if (sender.hasPermission("subtleRPG.join") || sender.isOp()) {
                        if (args.length == 3) {
                            Player player = (Bukkit.getServer().getPlayer(args[1]));
                            if (player == null) {
                                sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("���")
                                        .append(args[1]).append("������").toString());
                                return true;
                            } else { // Core
                                int count = 0;
                                String group = plugin.getPlayersData()
                                        .getString("Players." + player.getName() + ".group");
                                String temp = "";
                                while (temp != null) {
                                    count++;
                                    temp = plugin.getPlayersData()
                                            .getString("Players." + player.getName() + ".subGroup" + count);
                                }
                                if (count != 1) {
                                    group = plugin.getPlayersData()
                                            .getString("Players." + player.getName() + ".subGroup" + (count - 1));
                                }
                                List<String> list = plugin.getConfig().getStringList(group + ".Children");
                                if (list.size() == 0) {
                                    sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
                                            .append(plugin.getConfig().getString(group + ".name")).append("û����ְҵ")
                                            .toString());
                                    return true;
                                }
                                String newSubGroup = null;
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).equals(args[2])) {
                                        newSubGroup = list.get(i);
                                        break;
                                    }
                                    if (i == list.size() - 1) {
                                        sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
                                                .append(plugin.getConfig().getString(group + ".name")).append("����ְҵ")
                                                .append(args[2]).append("������").toString());
                                        return true;
                                    }
                                }
                                String newJobName = plugin.getConfig().getString(newSubGroup + ".name");
                                // Add Permission
                                List<String> permissionList = plugin.getConfig()
                                        .getStringList(newSubGroup + ".Perrmission");
                                if (permissionList != null)
                                    per.playerAddPermission(player, permissionList);
                                // Add effect
                                List<String> effectList = plugin.getConfig().getStringList(newSubGroup + ".Effect");
                                if (effectList.size() > 0)
                                    SubtleRPGEffect.applyEffectList(effectList, player);

                                plugin.getPlayersData().set("Players." + player.getName() + ".subGroup" + count,
                                        newSubGroup);
                                plugin.savePlayersData();

                                if (!player.getName().equals(sender.getName())) {
                                    sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA)
                                            .append("���������ְҵ").append(newJobName).append("��").toString());
                                }

                                sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA)
                                        .append(player.getName()).append("��������ְҵ").append(newJobName).append("��")
                                        .toString());
                                return true;
                            }
                        } else {
                            sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
                                    .append("/subtleRPG joinsub <�������> <��ְҵ>").toString());
                            return true;
                        }
                    } else {
                        sender.sendMessage(
                                (new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("��û��Ȩ��").toString());
                        return true;
                    }
                }

                if (args[0].equals("list")) {
                    if (args.length == 1) {
                        List<String> groupList = plugin.getConfig().getStringList("Groups");
                        sender.sendMessage(
                                (new StringBuilder(plugin.prefix)).append(ChatColor.AQUA).append("ְҵ�б�").toString());
                        for (String temp : groupList) {
                            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append(" - ")
                                    .append(ChatColor.GREEN).append(plugin.getConfig().getString(temp + ".name"))
                                    .append(ChatColor.GRAY).append("(").append(temp).append(")").toString());
                        }
                        return true;
                    } else if (args.length == 2) {
                        List<String> groupList = plugin.getConfig().getStringList(args[1] + ".Children");
                        if (groupList.size() == 0) {
                            sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED).append(args[1]).append("û����ְҵ").toString());
                            return true;
                        }
                        sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA)
                                .append(plugin.getConfig().getString(args[1] + ".name")).append(ChatColor.GRAY).append("(").append(args[1]).append(")").append(ChatColor.AQUA).append("��ְҵ�б�").toString());
                        for (String temp : groupList) {
                            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append(" - ")
                                    .append(ChatColor.GREEN).append(plugin.getConfig().getString(temp + ".name"))
                                    .append(ChatColor.GRAY).append("(").append(temp).append(")").toString());
                        }
                        return true;
                    } else {
                        sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
                                .append("/subtleRPG list [ְҵ]").toString());
                        return true;
                    }
                }

                if (args[0].equals("reload")) {
                    if (sender instanceof Player && !sender.isOp()) {
                        sender.sendMessage(
                                (new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("��û��Ȩ��").toString());
                        return true;
                    }
                    plugin.onReload();
                    sender.sendMessage(
                            (new StringBuilder(plugin.prefix)).append(ChatColor.BLUE).append("���سɹ�").toString());
                    return true;
                }

                if (args[0].equals("about")) {
                    sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("---- ").append(plugin.prefix)
                            .append(ChatColor.RESET).append(ChatColor.DARK_GRAY).append(" " + SubtleRPG.version)
                            .append(ChatColor.GRAY).append(" ----").toString());
                    sender.sendMessage((new StringBuilder()).append(ChatColor.BLUE).append(ChatColor.ITALIC)
                            .append("Ϊ������������RPGְҵ���").toString());
                    sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append(ChatColor.BOLD)
                            .append("������ ").append(ChatColor.RESET).append(ChatColor.GREEN)
                            .append("Mars (ISOTOPE Studio)").toString());
                    sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append(ChatColor.BOLD)
                            .append("��ַ�� ").append(ChatColor.RESET).append(ChatColor.GREEN)
                            .append("http://isotopestudio.cc/minecraft.html").toString());
                    return true;
                }

                // Wrong args0
                else {
                    sender.sendMessage(
                            (new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("δ֪����").toString());
                    return true;
                }

            } else { // Help Menu
                sender.sendMessage(
                        (new StringBuilder(plugin.prefix)).append(ChatColor.AQUA).append("== �����˵� ==").toString());
                sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/subtleRPG info [�������]")
                        .append(ChatColor.GRAY).append(" - ").append(ChatColor.LIGHT_PURPLE).append("�鿴���ְҵ��Ϣ")
                        .toString());
                sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/subtleRPG join <�������> <ְҵ>")
                        .append(ChatColor.GRAY).append(" - ").append(ChatColor.LIGHT_PURPLE).append("����һ��ְҵ")
                        .toString());
                sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD)
                        .append("/subtleRPG joinsub <�������> <��ְҵ>").append(ChatColor.GRAY).append(" - ")
                        .append(ChatColor.LIGHT_PURPLE).append("����һ����ְҵ").toString());
                sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/subtleRPG list [ְҵ]")
                        .append(ChatColor.GRAY).append(" - ").append(ChatColor.LIGHT_PURPLE).append("�鿴���ӣ�ְҵ�б�")
                        .toString());
                sender.sendMessage(
                        (new StringBuilder()).append(ChatColor.GOLD).append("/subtleRPG reload").append(ChatColor.GRAY)
                                .append(" - ").append(ChatColor.LIGHT_PURPLE).append("���ز�������ļ�").toString());
                sender.sendMessage(
                        (new StringBuilder()).append(ChatColor.GOLD).append("/subtleRPG about").append(ChatColor.GRAY)
                                .append(" - ").append(ChatColor.LIGHT_PURPLE).append("�鿴�����Ϣ").toString());
                return true;
            }
        }
        return false;

    }

    private void sendInfo(CommandSender sender, Player player) {
        sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA).append("���")
                .append(player.getName()).append("��Ϣ").toString());

        boolean isOp = player.isOp();
        if (player.isOp()) {
            player.setOp(false);
        }
        double addDamage, Defence;

        String group = plugin.getPlayersData().getString("Players." + player.getName() + ".group");
        if (group == null) {
            sender.sendMessage((new StringBuilder()).append(ChatColor.DARK_GREEN).append("taû��ְҵ").toString());
            return;
        }
        addDamage = plugin.getConfig().getDouble(group + ".Attack.default")
                + plugin.getConfig().getDouble(group + ".Attack.increasePerPeriod")
                * (int) (player.getLevel() / plugin.getConfig().getDouble(group + ".Attack.levPeriod"));
        Defence = plugin.getConfig().getDouble(group + ".Defence.default")
                + plugin.getConfig().getDouble(group + ".Defence.increasePerPeriod")
                * (int) (player.getLevel() / plugin.getConfig().getDouble(group + ".Defence.levPeriod"));
        sender.sendMessage((new StringBuilder()).append(ChatColor.DARK_GREEN).append("ְҵ: ")
                .append(plugin.getConfig().getString(group + ".name")).toString());

        String temp = "";
        int count = 0;
        while (temp != null) {
            count++;
            temp = plugin.getPlayersData().getString("Players." + player.getName() + ".subGroup" + count);
            if (temp != null && count == 1) {
                sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("��ְҵ: ").toString());
            }
            if (temp != null) {
                addDamage += plugin.getConfig().getDouble(temp + ".Attack.default")
                        + plugin.getConfig().getDouble(temp + ".Attack.increasePerPeriod")
                        * (int) (player.getLevel() / plugin.getConfig().getDouble(temp + ".Attack.levPeriod"));
                Defence += plugin.getConfig().getDouble(temp + ".Defence.default")
                        + plugin.getConfig().getDouble(temp + ".Defence.increasePerPeriod")
                        * (int) (player.getLevel() / plugin.getConfig().getDouble(temp + ".Defence.levPeriod"));
                sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("- ")
                        .append(plugin.getConfig().getString(temp + ".name")).toString());
            }
        }
        sender.sendMessage((new StringBuilder()).append(ChatColor.DARK_RED).append("HP: ").append(player.getHealth())
                .append(" / ").append(player.getMaxHealth()).toString());
        sender.sendMessage(
                (new StringBuilder()).append(ChatColor.DARK_GREEN).append("�ȼ�: ").append(player.getLevel()).toString());
        sender.sendMessage(
                (new StringBuilder()).append(ChatColor.YELLOW).append("�����ӳ�: ").append(addDamage).toString());
        sender.sendMessage((new StringBuilder()).append(ChatColor.YELLOW).append("����: ").append(Defence).toString());

        player.setOp(isOp);

    }
}