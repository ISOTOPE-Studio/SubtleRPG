/**
 * 
 */
package cc.isotopestudio.SubtleRPG.subtlerpg;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
		createFile("config");
		createFile("groups");
		createFile("playersData");

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
			} else {
				sender.sendMessage((new StringBuilder(prefix)).append(ChatColor.AQUA).append("帮助菜单").toString());

				return true;
			}

		return false;
	}

}
