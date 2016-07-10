package cc.isotopestudio.SubtleRPG.subtlerpg;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.List;

class SubtleRPGPermission {
    private final SubtleRPG plugin;

    SubtleRPGPermission(SubtleRPG plugin) {
        this.plugin = plugin;
    }

    void playerAddPermission(Player player, List<String> permissionList) {
        PermissionAttachment attachment = player.addAttachment(plugin);
        for (String temp : permissionList) {
            if (temp.startsWith("-"))
                attachment.setPermission(temp.substring(1), false);
            else
                attachment.setPermission(temp, true);
        }
    }

    void playerRemovePermission(Player player, List<String> permissionList) {
        PermissionAttachment attachment = player.addAttachment(plugin);
        permissionList.forEach(attachment::unsetPermission);
    }
}
