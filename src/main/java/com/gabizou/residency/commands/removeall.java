package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class removeall implements cmd {

    @Override
    @CommandAnnotation(simple = false, priority = 5100)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (args.length != 2 && args.length != 1) {
            return false;
        }

        String target = args.length == 2 ? args[1] : sender.getName();

        if (resadmin) {
            plugin.getResidenceManager().removeAllByOwner(target);
            plugin.msg(sender, lm.Residence_RemovePlayersResidences, target);
        } else {
            plugin.msg(sender, lm.General_NoPermission);
        }
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Remove all residences owned by a player.");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res removeall [owner]",
            "Removes all residences owned by a specific player.'", "Requires /resadmin if you use it on anyone besides yourself."));
        Residence.getInstance().getLocaleManager().CommandTab.put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[playername]"));
    }

}
