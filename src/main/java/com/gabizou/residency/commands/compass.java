package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.CuboidArea;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class compass implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 3200)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            player.setCompassTarget(player.getWorld().getSpawnLocation());
            plugin.msg(player, lm.General_CompassTargetReset);
            return true;
        }

        if (!plugin.hasPermission(player, "residence.compass")) {
            return true;
        }

        ClaimedResidence res = plugin.getResidenceManager().getByName(args[1]);

        if (res != null) {
            if (res.getWorld().equalsIgnoreCase(player.getWorld().getName())) {
                CuboidArea area = res.getMainArea();
                if (area == null) {
                    return false;
                }
                Location loc = res.getTeleportLocation();
                if (loc == null) {
                    return false;
                }
                player.setCompassTarget(loc);
                plugin.msg(player, lm.General_CompassTargetSet, args[1]);
            }
        } else {
            plugin.msg(player, lm.Invalid_Residence);
        }

        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Set compass ponter to residence location");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res compass <residence>"));
        Residence.getInstance().getLocaleManager().CommandTab.put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[residence]"));
    }
}
