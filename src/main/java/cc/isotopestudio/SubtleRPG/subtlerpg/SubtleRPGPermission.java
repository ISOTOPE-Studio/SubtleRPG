package cc.isotopestudio.SubtleRPG.subtlerpg;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class SubtleRPGPermission {
	private final SubtleRPG plugin;

	public SubtleRPGPermission(SubtleRPG plugin) {
		this.plugin = plugin;
	}

	public void playerAddPermission(Player player, List<String> permissionList) {
		PermissionAttachment attachment = player.addAttachment(plugin);
		for (int i = 0; i < permissionList.size(); i++) {
			String temp = permissionList.get(i);
			if (temp.startsWith("-"))
				attachment.setPermission(temp.substring(1), false);
			else
				attachment.setPermission(temp, true);
		}
	}

	public void playerRemovePermission(Player player, List<String> permissionList) {
		PermissionAttachment attachment = player.addAttachment(plugin);
		for (int i = 0; i < permissionList.size(); i++) {
			String temp = permissionList.get(i);
			attachment.unsetPermission(temp);
		}
	}
}
