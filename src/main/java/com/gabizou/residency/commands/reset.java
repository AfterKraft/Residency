package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class reset implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 4400)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            return false;
        }

        ClaimedResidence res = plugin.getResidenceManager().getByName(args[1]);
        res.getPermissions().applyDefaultFlags(player, resadmin);
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Reset residence to default flags.");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res reset <residence>",
            "Resets the flags on a residence to their default.  You must be the owner or an admin to do this."));
        Residence.getInstance().getLocaleManager().CommandTab.put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[residence]"));
    }
}
