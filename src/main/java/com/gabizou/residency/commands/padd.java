package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class padd implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 400)
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
            if (!plugin.isPlayerExist(player, args[1], true)) {
                return false;
            }
            player.performCommand(baseCmd + " pset " + args[1] + " trusted true");
            return true;
        }
        if (args.length == 3) {
            if (!plugin.isPlayerExist(player, args[2], true)) {
                return false;
            }
            player.performCommand(baseCmd + " pset " + args[1] + " " + args[2] + " trusted true");
            return true;
        }
        return false;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        // Main command
        c.get(path + "Description", "Add player to residence.");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res padd <residence name> [player]", "Adds essential flags for player"));
        Residence.getInstance().getLocaleManager().CommandTab
            .put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[residence]%%[playername]", "[playername]"));
    }
}
