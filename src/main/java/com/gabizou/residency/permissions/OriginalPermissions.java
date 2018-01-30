package com.gabizou.residency.permissions;

import com.nijiko.permissions.PermissionHandler;
import org.bukkit.entity.Player;

public class OriginalPermissions implements PermissionsInterface {

    PermissionHandler authority;

    public OriginalPermissions(PermissionHandler perms) {
        this.authority = perms;
    }

    @Override
    public String getPlayerGroup(Player player) {
        return this.getPlayerGroup(player.getName(), player.getWorld().getName());
    }

    @Override
    public String getPlayerGroup(String player, String world) {
        String group = this.authority.getPrimaryGroup(world, player);
        if (group != null) {
            return group.toLowerCase();
        }
        return null;
    }

}
