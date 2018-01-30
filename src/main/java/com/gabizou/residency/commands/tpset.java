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

public class tpset implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 200)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
		if (!(sender instanceof Player)) {
			return false;
		}

        Player player = (Player) sender;
        ClaimedResidence res = plugin.getResidenceManager().getByLoc(player.getLocation());
        if (res != null) {
            res.setTpLoc(player, resadmin);
        } else {
            plugin.msg(player, lm.Invalid_Residence);
        }
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        // Main command
        c.get(path + "Description", "Set the teleport location of a Residence");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res tpset", "This will set the teleport location for a residence to where your standing.",
            "You must be standing in the residence to use this command.", "You must also be the owner or have the +admin flag for the residence."));
    }
}
