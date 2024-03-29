package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class listhidden implements cmd {

    @Override
    @CommandAnnotation(simple = false, priority = 4800)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        int page = 1;
        try {
            if (args.length > 0) {
                page = Integer.parseInt(args[args.length - 1]);
            }
        } catch (Exception ex) {
        }
        if (!resadmin) {
            plugin.msg(sender, lm.General_NoPermission);
            return true;
        }
        if (args.length == 1) {
            plugin.getResidenceManager().listResidences(sender, 1, true, true);
            return true;
        } else if (args.length == 2) {
            try {
                Integer.parseInt(args[1]);
                plugin.getResidenceManager().listResidences(sender, page, true, true);
            } catch (Exception ex) {
                plugin.getResidenceManager().listResidences(sender, args[1], 1, true, true, resadmin);
            }
            return true;
        } else if (args.length == 3) {
            plugin.getResidenceManager().listResidences(sender, args[1], page, true, true, resadmin);
            return true;
        }
        return false;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "List Hidden Residences");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res listhidden <player> <page>", "Lists hidden residences for a player."));
        Residence.getInstance().getLocaleManager().CommandTab.put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[playername]"));
    }
}
