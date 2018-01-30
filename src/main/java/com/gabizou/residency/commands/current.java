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

public class current implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 3100)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            return false;
        }

        ClaimedResidence res = plugin.getResidenceManager().getByLoc(player.getLocation());
        if (res == null) {
            plugin.msg(player, lm.Residence_NotIn);
        } else {
            plugin.msg(player, lm.Residence_In, res.getName());
        }
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Show residence your currently in.");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res current"));
    }

}
