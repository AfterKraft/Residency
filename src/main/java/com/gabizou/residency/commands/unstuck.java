package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.ResidencePlayer;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.permissions.PermissionGroup;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class unstuck implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 4000)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            return false;
        }

        ResidencePlayer rPlayer = plugin.getPlayerManager().getResidencePlayer(player);
        PermissionGroup group = rPlayer.getGroup();
        if (!group.hasUnstuckAccess()) {
            plugin.msg(player, lm.General_NoPermission);
            return true;
        }
        ClaimedResidence res = plugin.getResidenceManager().getByLoc(player.getLocation());
        if (res == null) {
            plugin.msg(player, lm.Residence_NotIn);
        } else {
            plugin.msg(player, lm.General_Moved);
            player.teleport(res.getOutsideFreeLoc(player.getLocation(), player));
        }
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Teleports outside of residence");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res unstuck"));
    }
}
