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

public class gset implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 4500)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (args.length == 4) {
            ClaimedResidence area = plugin.getResidenceManager().getByLoc(player.getLocation());
            if (area != null) {
                area.getPermissions().setGroupFlag(player, args[1], args[2], args[3], resadmin);
            } else {
                plugin.msg(player, lm.Invalid_Area);
            }
            return true;
        } else if (args.length == 5) {
            ClaimedResidence area = plugin.getResidenceManager().getByName(args[1]);
            if (area != null) {
                area.getPermissions().setGroupFlag(player, args[2], args[3], args[4], resadmin);
            } else {
                plugin.msg(player, lm.Invalid_Residence);
            }
            return true;
        }
        return false;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Set flags on a specific group for a Residence.");
        c.get(path + "Info",
            Arrays.asList("&eUsage: &6/res gset <residence> [group] [flag] [true/false/remove]", "To see a list of flags, use /res flags ?"));
        Residence.getInstance().getLocaleManager().CommandTab.put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[residence]"));
    }
}
