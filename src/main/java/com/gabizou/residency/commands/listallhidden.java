package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class listallhidden implements cmd {

    @Override
    @CommandAnnotation(simple = false, priority = 4700)
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
            plugin.getResidenceManager().listAllResidences(sender, 1, true, true);
        } else if (args.length == 2) {
            try {
                plugin.getResidenceManager().listAllResidences(sender, page, true, true);
            } catch (Exception ex) {
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "List All Hidden Residences");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res listhidden <page>", "Lists all hidden residences on the server."));
    }
}
