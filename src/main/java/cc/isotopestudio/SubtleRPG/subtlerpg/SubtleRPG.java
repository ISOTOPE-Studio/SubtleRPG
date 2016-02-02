package cc.isotopestudio.SubtleRPG.subtlerpg;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Mars
 *
 */
public final class SubtleRPG extends JavaPlugin implements Listener {
	
	public final String version = "v1.2.2.1-debug";

	public final String prefix = (new StringBuilder()).append(ChatColor.GREEN).append("[").append(ChatColor.ITALIC)
			.append(ChatColor.BOLD).append("SubtleRPG").append(ChatColor.RESET).append(ChatColor.GREEN).append("]")
			.append(ChatColor.RESET).toString();

	public void createFile(String name) {

		File file;
		file = new File(getDataFolder(), name + ".yml");
		if (!file.exists()) {
			saveDefaultConfig();
		}
	}

	@Override
	public void onEnable() {
		getLogger().info("加载配置文件中");

		createFile("config");
		try {
			getPlayersData().save(dataFile);
		} catch (IOException e) {
		}

		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new SubtleRPGListener(this), this);
		this.getCommand("subtleRPG").setExecutor(new SubtleRPGCommand(this));

		getLogger().info("SubtleRPG 成功加载!");
	}

	public void onReload() {
		reloadPlayersData();
		this.reloadConfig();
	}

	@Override
	public void onDisable() {
		savePlayersData();
		getLogger().info("SubtleRPG 成功卸载!");
	}

	private File dataFile = null;
	private FileConfiguration data = null;

	public void reloadPlayersData() {
		if (dataFile == null) {
			dataFile = new File(getDataFolder(), "playersData.yml");
		}
		data = YamlConfiguration.loadConfiguration(dataFile);
	}

	public FileConfiguration getPlayersData() {
		if (data == null) {
			reloadPlayersData();
		}
		return data;
	}

	public void savePlayersData() {
		if (data == null || dataFile == null) {
			return;
		}
		try {
			getPlayersData().save(dataFile);
		} catch (IOException ex) {
			getLogger().info("玩家文件保存失败！");
		}
	}

}
