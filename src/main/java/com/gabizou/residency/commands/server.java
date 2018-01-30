package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class server implements cmd {

    @Override
    @CommandAnnotation(simple = false, priority = 5400)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!resadmin) {
            plugin.msg(player, lm.General_NoPermission);
            return true;
        }
        if (args.length == 2) {
            ClaimedResidence res = plugin.getResidenceManager().getByName(args[1]);
            if (res == null) {
                plugin.msg(player, lm.Invalid_Residence);
                return true;
            }
            res.getPermissions().setOwner(plugin.getServerLandname(), false);
            plugin.msg(player, lm.Residence_OwnerChange, args[1], plugin.getServerLandname());
            return true;
        }
        plugin.msg(player, lm.Invalid_Residence);
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {

        c.get(path + "Description", "Make land server owned.");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/resadmin server [residence]", "Make a residence server owned."));
        Residence.getInstance().getLocaleManager().CommandTab.put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[cresidence]"));
    }
}
