package cc.isotopestudio.SubtleRPG.subtlerpg;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubtleRPGCommand implements CommandExecutor {
	private final SubtleRPG plugin;

	public SubtleRPGCommand(SubtleRPG plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("SubtleRPG"))
			if (args.length > 0 && !args[0].equals("help")) {

				if (args[0].equals("info")) {
					if ((sender instanceof Player && (args.length == 1 || args.length == 2))
							|| (!(sender instanceof Player) && args.length == 2)) {
						if (args.length == 2) {
							Player player = (Bukkit.getServer().getPlayer(args[1]));
							if (player == null) {
								sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("玩家")
										.append(args[1]).append("不存在").toString());
								return true;
							} else {// Core
								sendInfo(sender, args, player);
								return true;
							}
						} else {// Core
							sendInfo(sender, args, (Player) sender);
							return true;
						}

					} else {
						sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
								.append("/subtleRPG info <玩家名字>").toString());
					}
				}

				if (args[0].equals("join")) {
					if (sender.hasPermission("subtleRPG.join")) {
						if (args.length == 3) {
							Player player = (Bukkit.getServer().getPlayer(args[2]));
							if (player == null) {
								sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("玩家")
										.append(args[2]).append("不存在").toString());
								return true;
							} else { // Core
								List<String> list = plugin.getConfig().getStringList("Groups");
								String newJob = null;
								for (int i = 0; i < list.size(); i++) {
									if (list.get(i).equals(args[1])) {
										newJob = list.get(i);
										break;
									}
									if (i == list.size() - 1) {
										sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
												.append("职业").append(args[1]).append("不存在").toString());

										return true;
									}
								}
								
								plugin.data.set(player.getName(), newJob);
								plugin.saveConfig();

								sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA)
										.append(args[2]).append("加入了").append(args[1]).append("！").toString());
								return true;
							}
						} else {
							sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
									.append("/subtleRPG join <职业名字> <玩家名字>").toString());
						}
					} else {
						sender.sendMessage(
								(new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("你没有权限").toString());
						return true;
					}
				}

				if (args[0].equals("health") && args.length == 5) {

					Player player = Bukkit.getPlayer(args[1]);
					double newHealth = (double) Integer.parseInt(args[2]);
					double newMaxHealth = (double) Integer.parseInt(args[3]);
					double newHealthScale = (double) Integer.parseInt(args[4]);

					player.setHealth(newHealth);
					player.setMaxHealth(newMaxHealth);
					player.setHealthScale(newHealthScale);

					return true;
				} else {
					sender.sendMessage(
							(new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("未知命令").toString());
					return true;
				}

			} else { // Help Menu
				sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA).append("帮助菜单").toString());

				return true;
			}

		return false;

	}

	public void sendInfo(CommandSender sender, String[] args, Player player) {
		sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA).append("玩家")
				.append(player.getName()).append("信息").toString());

		boolean isOp = player.isOp();
		if (player.isOp()) {
			player.setOp(false);
		}
		List<String> list = plugin.getConfig().getStringList("Groups");
		double addDamage = 0;
		for (int i = 0; i < list.size(); i++) {
			String tempPermission = "subtleRPG." + list.get(i);
			if (player.hasPermission(tempPermission)) {
				sender.sendMessage((new StringBuilder()).append(ChatColor.DARK_GREEN).append("职业: ")
						.append(plugin.getConfig().getString(list.get(i) + ".name")).toString());
				addDamage = plugin.getConfig().getDouble(list.get(i) + ".Attack.default")
						+ plugin.getConfig()
								.getDouble(list.get(i)
										+ ".Attack.increasePerPeriod")
						* (int) (player.getLevel() / plugin.getConfig().getDouble(list.get(i) + ".Attack.levPeriod"));
				if (addDamage > plugin.getConfig().getDouble(list.get(i) + ".Attack.max")) {
					addDamage = plugin.getConfig().getDouble(list.get(i) + ".Attack.max");
				}
				break;

			}
			if (i >= list.size() - 1) {
				sender.sendMessage((new StringBuilder()).append(ChatColor.DARK_GREEN).append("ta没有职业").toString());
			}
		}

		sender.sendMessage((new StringBuilder()).append(ChatColor.DARK_RED).append("HP: ").append(player.getHealth())
				.append(" / ").append(player.getMaxHealth()).toString());

		sender.sendMessage(
				(new StringBuilder()).append(ChatColor.YELLOW).append("攻击加成: ").append(addDamage).toString());

		player.setOp(isOp);
	}

}
