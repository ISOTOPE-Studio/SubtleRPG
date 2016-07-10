package cc.isotopestudio.SubtleRPG.subtlerpg;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * @author Mars
 */
public final class SubtleRPG extends JavaPlugin {

    static final String version = "v1.4.0";

    final String prefix = (new StringBuilder()).append(ChatColor.GREEN).append("[").append(ChatColor.ITALIC)
            .append(ChatColor.BOLD).append("SubtleRPG").append(ChatColor.RESET).append(ChatColor.GREEN).append("]")
            .append(ChatColor.RESET).toString();

    @Override
    public void onEnable() {
        getLogger().info("加载配置文件中");

        File file;
        file = new File(getDataFolder(), "config" + ".yml");
        if (!file.exists()) {
            saveDefaultConfig();
        }
        try {
            getPlayersData().save(dataFile);
        } catch (IOException ignored) {
        }

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new SubtleRPGListener(this), this);
        this.getCommand("subtleRPG").setExecutor(new SubtleRPGCommand(this));

        getLogger().info("SubtleRPG 成功加载!");
        getLogger().info("SubtleRPG 由ISOTOPE Studio制作!");
        getLogger().info("http://isotopestudio.cc");
    }

    void onReload() {
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

    private void reloadPlayersData() {
        if (dataFile == null) {
            dataFile = new File(getDataFolder(), "playersData.yml");
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    FileConfiguration getPlayersData() {
        if (data == null) {
            reloadPlayersData();
        }
        return data;
    }

    void savePlayersData() {
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
