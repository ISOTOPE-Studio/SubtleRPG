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
		saveDefaultConfig();
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new SubtleRPGListener(this), this);
		this.getCommand("subtleRPG").setExecutor(new SubtleRPGCommand(this));
		
		getLogger().info("SubtleRPG 成功加载!");
	}

	public void onReload() {
		this.reloadConfig();
	}

	@Override
	public void onDisable() {
		getLogger().info("SubtleRPG 成功卸载!");
	}

	
}
