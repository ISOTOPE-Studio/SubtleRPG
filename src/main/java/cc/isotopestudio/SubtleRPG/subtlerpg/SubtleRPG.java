/**
 * 
 */
package cc.isotopestudio.SubtleRPG.subtlerpg;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import cc.isotopestudio.SubtleRPG.subtlerpg.listener.*;

/**
 * @author Mars
 *
 */
public final class SubtleRPG extends JavaPlugin implements Listener {

	public FileConfiguration groupsFile;

	public final String prefix = (new StringBuilder()).append(ChatColor.GREEN).append("[").append(ChatColor.ITALIC)
			.append(ChatColor.BOLD).append("SubtleRPG").append(ChatColor.RESET).append(ChatColor.GREEN).append("]")
			.append(ChatColor.RESET).toString();

	public void createFile(String name) {
		File file;
		file = new File(getDataFolder(), name + ".yml");
		if (!file.exists()) {
			getConfig().options().copyDefaults(true);
			try {
				getConfig().save(file);
			} catch (IOException ex) {
				getLogger().severe("配置文件保存出错");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
	}

	@Override
	public void onEnable() {
		getLogger().info("加载配置文件中");
		saveDefaultConfig();
		// createFile("config");
		createFile("groups");
		groupsFile = YamlConfiguration.loadConfiguration(getResource("groups.yml"));

		// createFile("playersData");

		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new TestListener(this), this);

		getLogger().info("SubtleRPG 成功加载!");
	}

	public void onReload() {
		this.reloadConfig();
	}

	@Override
	public void onDisable() {
		getLogger().info("SubtleRPG 成功卸载!");
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
								sender.sendMessage((new StringBuilder(prefix)).append(ChatColor.RED).append("玩家")
										.append(args[1]).append("不存在").toString());
								return true;
							} else {
								sendInfo(sender, args, player);
								return true;
							}
						} else {
							sendInfo(sender, args, (Player) sender);
							return true;
						}

					} else {
						sender.sendMessage((new StringBuilder(prefix)).append(ChatColor.RED)
								.append("/subtleRPG info <玩家名字>").toString());
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
					sender.sendMessage((new StringBuilder(prefix)).append(ChatColor.RED).append("未知命令").toString());
					return true;
				}
			} else

			{
				sender.sendMessage((new StringBuilder(prefix)).append(ChatColor.AQUA).append("帮助菜单").toString());

				return true;
			}

		return false;
	}

	public void sendInfo(CommandSender sender, String[] args, Player player) {
		sender.sendMessage((new StringBuilder(prefix)).append(ChatColor.AQUA).append("玩家").append(player.getName())
				.append("信息").toString());
		List<String> list = getConfig().getStringList("Groups");
		double addDamage = 0;
		for (int i = 0; i < list.size(); i++) {
			String tempPermission = "subtleRPG." + list.get(i);
			if (player.hasPermission(tempPermission)) {
				sender.sendMessage((new StringBuilder()).append(ChatColor.DARK_GREEN).append("职业: ")
						.append(groupsFile.getString(list.get(i) + ".name")).toString());
				addDamage = groupsFile.getDouble(list.get(i) + ".Attack.default")
						+ groupsFile.getDouble(list.get(i) + ".Attack.increasePerPeriod")
								* (int) (player.getLevel() / groupsFile.getDouble(list.get(i) + ".Attack.levPeriod"));
				if (addDamage > groupsFile.getDouble(list.get(i) + ".Attack.max")) {
					addDamage = groupsFile.getDouble(list.get(i) + ".Attack.max");
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

	}

}
