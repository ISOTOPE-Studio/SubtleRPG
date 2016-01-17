/**
 * 
 */
package cc.isotopestudio.SubtleRPG.subtlerpg;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import cc.isotopestudio.SubtleRPG.subtlerpg.listener.*;

/**
 * @author Mars
 *
 */
public final class SubtleRPG extends JavaPlugin {
	@Override
	public void onEnable() {

		 PluginManager pm = this.getServer().getPluginManager();
		 pm.registerEvents(new TestListener(this), this);

		getLogger().info("SubtleRPG 成功加载!");
	}

	@Override
	public void onDisable() {
		getLogger().info("SubtleRPG 成功卸载!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("SubtleRPG")) { // If the player typed /basic then do the following...
			// doSomething
			getLogger().info("玩家输入命令");
			sender.sendMessage("red.");
			return true;
		}
		return false; 
	}

}
