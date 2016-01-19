/**
 * 
 */
package cc.isotopestudio.SubtleRPG.subtlerpg;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.material.Lever;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Mars
 *
 */
public final class SubtleRPG extends JavaPlugin implements Listener {
	private File dataFile = null;
	public FileConfiguration data = null;
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

		data = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "playersData.yml"));

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
/*
	public void reloadCustomConfig() {
		if (dataFile == null) {
			dataFile = new File(getDataFolder(), "playersDaya.yml");
		}
		data = YamlConfiguration.loadConfiguration(dataFile);

		// Look for defaults in the jar
		Reader defConfigStream = new InputStreamReader(this.getResource("customConfig.yml"), "UTF8");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			data.setDefaults(defConfig);
		}
	}

	public FileConfiguration getCustomConfig() {
		if (data == null) {
			reloadCustomConfig();
		}
		return data;
	}

	public void saveCustomConfig() {
		if (data == null || dataFile == null) {
			return;
		}
		try {
			getCustomConfig().save(dataFile);
		} catch (IOException ex) {
			getLogger().log(Lever.SEVERE, "Could not save config to " + dataFile, ex);
		}
	}
*/
}
