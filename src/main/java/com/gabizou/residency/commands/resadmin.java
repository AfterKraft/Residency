package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class resadmin implements cmd {

    @Override
    @CommandAnnotation(simple = false, priority = 5300)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length != 2) {
            return true;
        }

        Player player = (Player) sender;
        if (args[1].equals("on")) {
            plugin.resadminToggle.add(player.getName());
            plugin.msg(player, lm.General_AdminToggleTurnOn);
        } else if (args[1].equals("off")) {
            plugin.resadminToggle.remove(player.getName());
            plugin.msg(player, lm.General_AdminToggleTurnOff);
        }
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Enabled or disable residence admin");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res resadmin [on/off]"));
        Residence.getInstance().getLocaleManager().CommandTab.put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("on%%off"));
    }
}
