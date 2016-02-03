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
	SubtleRPGPermission per;

	public SubtleRPGCommand(SubtleRPG plugin) {
		this.plugin = plugin;
		per = new SubtleRPGPermission(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("SubtleRPG")) {
			if (args.length > 0)
				args[0].toLowerCase();
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
					if (sender.hasPermission("subtleRPG.join") || sender.isOp()) {
						if (args.length == 3) {
							Player player = (Bukkit.getServer().getPlayer(args[1]));
							if (player == null) {
								sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("玩家")
										.append(args[1]).append("不存在").toString());
								return true;
							} else { // Core
								List<String> list = plugin.getConfig().getStringList("Groups");
								String newJob = null, newJobName = null;
								for (int i = 0; i < list.size(); i++) {
									if (list.get(i).equals(args[2])) {
										newJob = list.get(i);
										newJobName = plugin.getConfig().getString(newJob + ".name");
										break;
									}
									if (i == list.size() - 1) {
										sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
												.append("职业").append(args[2]).append("不存在").toString());
										return true;
									}
								}
								if (newJob.equals(
										plugin.getPlayersData().getString("Players." + player.getName() + ".group"))) {
									sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
											.append("你已经是").append(newJobName).append("了").toString());
									return true;
								}

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

										// Delete playersData
										plugin.getPlayersData().set("Players." + player.getName() + ".subGroup" + count,
												null);
									}
								}
								// Add Permission
								List<String> permissionList = plugin.getConfig().getStringList(newJob + ".Perrmission");
								if (permissionList.size() > 0)
									per.playerAddPermission(player, permissionList);

								plugin.getPlayersData().set("Players." + player.getName() + ".group", newJob);
								plugin.savePlayersData();

								if (!player.getName().equals(sender.getName())) {
									sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA)
											.append("你加入了职业").append(newJobName).append("！").toString());
								}
								sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA)
										.append(player.getName()).append("加入了").append(newJobName).append("！")
										.toString());
								return true;
							}
						} else {
							sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
									.append("/subtleRPG join <玩家名字> <职业>").toString());
							return true;
						}
					} else {
						sender.sendMessage(
								(new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("你没有权限").toString());
						return true;
					}
				}

				if (args[0].equals("joinsub")) {
					if (sender.hasPermission("subtleRPG.join") || sender.isOp()) {
						if (args.length == 3) {
							Player player = (Bukkit.getServer().getPlayer(args[1]));
							if (player == null) {
								sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("玩家")
										.append(args[1]).append("不存在").toString());
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
											.append(plugin.getConfig().getString(group + ".name")).append("没有子职业")
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
												.append(plugin.getConfig().getString(group + ".name")).append("的子职业")
												.append(args[2]).append("不存在").toString());
										return true;
									}
								}
								String newJobName = plugin.getConfig().getString(newSubGroup + ".name");

								List<String> permissionList = plugin.getConfig()
										.getStringList(newSubGroup + ".Perrmission");
								if (permissionList != null)
									per.playerAddPermission(player, permissionList);

								plugin.getPlayersData().set("Players." + player.getName() + ".subGroup" + count,
										newSubGroup);
								plugin.savePlayersData();

								if (!player.getName().equals(sender.getName())) {
									sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA)
											.append("你加入了子职业").append(newJobName).append("！").toString());
								}

								sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA)
										.append(player.getName()).append("加入了子职业").append(newJobName).append("！")
										.toString());
								return true;
							}
						} else {
							sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
									.append("/subtleRPG joinsub <玩家名字> <子职业>").toString());
							return true;
						}
					} else {
						sender.sendMessage(
								(new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("你没有权限").toString());
						return true;
					}
				}

				if (args[0].equals("list")) {
					if (args.length == 1) {
						List<String> groupList = plugin.getConfig().getStringList("Groups");
						sender.sendMessage(
								(new StringBuilder(plugin.prefix)).append(ChatColor.AQUA).append("职业列表").toString());
						for (int i = 0; i < groupList.size(); i++) {
							String temp = groupList.get(i);
							sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append(" - ")
									.append(ChatColor.GREEN).append(plugin.getConfig().getString(temp + ".name"))
									.append(ChatColor.GRAY).append("(" + temp + ")").toString());
						}
						return true;
					} else if (args.length == 2) {
						List<String> groupList = plugin.getConfig().getStringList(args[1] + ".Children");
						if (groupList.size() == 0) {
							sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
									.append(args[1] + "没有子职业").toString());
							return true;
						}
						sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA)
								.append(plugin.getConfig().getString(args[1] + ".name")).append(ChatColor.GRAY)
								.append("(" + args[1] + ")").append(ChatColor.AQUA).append("子职业列表").toString());
						for (int i = 0; i < groupList.size(); i++) {
							String temp = groupList.get(i);
							sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append(" - ")
									.append(ChatColor.GREEN).append(plugin.getConfig().getString(temp + ".name"))
									.append(ChatColor.GRAY).append("(" + temp + ")").toString());
						}
						return true;
					} else {
						sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.RED)
								.append("/subtleRPG list [职业]").toString());
						return true;
					}
				}

				if (args[0].equals("reload")) {
					if (sender instanceof Player && !((Player) sender).isOp()) {
						sender.sendMessage(
								(new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("你没有权限").toString());
						return true;
					}
					plugin.onReload();
					sender.sendMessage(
							(new StringBuilder(plugin.prefix)).append(ChatColor.BLUE).append("重载成功").toString());
					return true;
				}

				if (args[0].equals("about")) {
					sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("---- " + plugin.prefix)
							.append(ChatColor.RESET).append(ChatColor.DARK_GRAY).append(" " + plugin.version)
							.append(ChatColor.GRAY).append(" ----").toString());
					sender.sendMessage((new StringBuilder()).append(ChatColor.BLUE).append(ChatColor.ITALIC)
							.append("为服务器制作的RPG职业插件").toString());
					sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append(ChatColor.BOLD)
							.append("制作： ").append(ChatColor.RESET).append(ChatColor.GREEN)
							.append("Mars (ISOTOPE Studio)").toString());
					sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append(ChatColor.BOLD)
							.append("网址： ").append(ChatColor.RESET).append(ChatColor.GREEN)
							.append("http://isotopestudio.cc").toString());
					return true;
				}

				// Wrong args0
				else {
					sender.sendMessage(
							(new StringBuilder(plugin.prefix)).append(ChatColor.RED).append("未知命令").toString());
					return true;
				}

			} else { // Help Menu
				sender.sendMessage(
						(new StringBuilder(plugin.prefix)).append(ChatColor.AQUA).append("== 帮助菜单 ==").toString());
				sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/subtleRPG info [玩家名字]")
						.append(ChatColor.GRAY).append(" - ").append(ChatColor.LIGHT_PURPLE).append("查看玩家职业信息")
						.toString());
				sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/subtleRPG join <玩家名字> <职业>")
						.append(ChatColor.GRAY).append(" - ").append(ChatColor.LIGHT_PURPLE).append("加入一个职业")
						.toString());
				sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD)
						.append("/subtleRPG joinsub <玩家名字> <子职业>").append(ChatColor.GRAY).append(" - ")
						.append(ChatColor.LIGHT_PURPLE).append("加入一个子职业").toString());
				sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("/subtleRPG list [职业]")
						.append(ChatColor.GRAY).append(" - ").append(ChatColor.LIGHT_PURPLE).append("查看（子）职业列表")
						.toString());
				sender.sendMessage(
						(new StringBuilder()).append(ChatColor.GOLD).append("/subtleRPG reload").append(ChatColor.GRAY)
								.append(" - ").append(ChatColor.LIGHT_PURPLE).append("重载插件配置文件").toString());
				sender.sendMessage(
						(new StringBuilder()).append(ChatColor.GOLD).append("/subtleRPG about").append(ChatColor.GRAY)
								.append(" - ").append(ChatColor.LIGHT_PURPLE).append("查看插件信息").toString());
				return true;
			}
		}
		return false;

	}

	private void sendInfo(CommandSender sender, String[] args, Player player) {
		sender.sendMessage((new StringBuilder(plugin.prefix)).append(ChatColor.AQUA).append("玩家")
				.append(player.getName()).append("信息").toString());

		boolean isOp = player.isOp();
		if (player.isOp()) {
			player.setOp(false);
		}
		double addDamage = 0, Defence = 0;

		String group = plugin.getPlayersData().getString("Players." + player.getName() + ".group");
		if (group == null) {
			sender.sendMessage((new StringBuilder()).append(ChatColor.DARK_GREEN).append("ta没有职业").toString());
			return;
		}
		addDamage = plugin.getConfig().getDouble(group + ".Attack.default")
				+ plugin.getConfig().getDouble(group + ".Attack.increasePerPeriod")
						* (int) (player.getLevel() / plugin.getConfig().getDouble(group + ".Attack.levPeriod"));
		Defence = plugin.getConfig().getDouble(group + ".Defence.default")
				+ plugin.getConfig().getDouble(group + ".Defence.increasePerPeriod")
						* (int) (player.getLevel() / plugin.getConfig().getDouble(group + ".Defence.levPeriod"));
		sender.sendMessage((new StringBuilder()).append(ChatColor.DARK_GREEN).append("职业: ")
				.append(plugin.getConfig().getString(group + ".name")).toString());

		String temp = "";
		int count = 0;
		while (temp != null) {
			count++;
			temp = plugin.getPlayersData().getString("Players." + player.getName() + ".subGroup" + count);
			if (temp != null && count == 1) {
				sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("子职业: ").toString());
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
				(new StringBuilder()).append(ChatColor.DARK_GREEN).append("等级: ").append(player.getLevel()).toString());
		sender.sendMessage(
				(new StringBuilder()).append(ChatColor.YELLOW).append("攻击加成: ").append(addDamage).toString());
		sender.sendMessage((new StringBuilder()).append(ChatColor.YELLOW).append("防御: ").append(Defence).toString());

		player.setOp(isOp);

	}
}