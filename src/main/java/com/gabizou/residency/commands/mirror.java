package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class mirror implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 3700)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (args.length != 3) {
            return false;
        }

        plugin.getResidenceManager().mirrorPerms(player, args[2], args[1], resadmin);
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Mirrors Flags");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res mirror [Source Residence] [Target Residence]",
            "Mirrors flags from one residence to another.  You must be owner of both or a admin to do this."));
        Residence.getInstance().getLocaleManager().CommandTab
            .put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[residence]", "[residence]"));
    }
}
