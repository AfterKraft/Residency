package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class pdel implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 500)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        String baseCmd = "res";
        if (resadmin) {
            baseCmd = "resadmin";
        }
        if (args.length == 2) {
            Bukkit.dispatchCommand(player, baseCmd + " pset " + args[1] + " trusted remove");
            return true;
        }
        if (args.length == 3) {
            Bukkit.dispatchCommand(player, baseCmd + " pset " + args[1] + " " + args[2] + " trusted remove");
            return true;
        }
        return false;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        // Main command
        c.get(path + "Description", "Remove player from residence.");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res pdel <residence name> [player]", "Removes essential flags from player"));
        Residence.getInstance().getLocaleManager().CommandTab
            .put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[residence]%%[playername]", "[playername]"));
    }

}
