package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.cmd;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class flags implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 1200)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        int page = 1;
        try {
            if (args.length > 0) {
                page = Integer.parseInt(args[args.length - 1]);
            }
        } catch (Exception ex) {
        }
        if (resadmin) {
            Bukkit.dispatchCommand(sender, "resadmin flags ? " + page);
        } else {
            Bukkit.dispatchCommand(sender, "res flags ? " + page);
        }
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "List of flags");
        c.get(path + "Info", Arrays.asList("For flag values, usually true allows the action, and false denys the action."));

        for (Flags fl : Flags.values()) {
            String pt = path + "SubCommands." + fl.getName();
            c.get(pt + ".Description", fl.getDesc());
            String forSet = "set/pset";
            switch (fl.getFlagMode()) {
                case Group:
                case Player:
                    forSet = "pset";
                    break;
                case Residence:
                    forSet = "set";
                    break;
                case Both:
                default:
                    break;
            }
            c.get(pt + ".Info", Arrays.asList("&eUsage: &6/res " + forSet + " <residence> " + fl.getName() + " true/false/remove"));
        }

        Residence.getInstance().getLocaleManager().CommandTab
            .put(Arrays.asList(this.getClass().getSimpleName(), "pset"), Arrays.asList("[residence]", "[flag]",
                "[true%%false%%remove]"));
        Residence.getInstance().getLocaleManager().CommandTab
            .put(Arrays.asList(this.getClass().getSimpleName(), "set"), Arrays.asList("[residence]", "[flag]",
                "[true%%false%%remove]"));
    }
}
