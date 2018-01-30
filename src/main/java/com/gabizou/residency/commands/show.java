package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.Visualizer;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class show implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 3300)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        ClaimedResidence res = null;

        if (args.length == 2) {
            res = plugin.getResidenceManager().getByName(args[1]);
        } else {
            res = plugin.getResidenceManager().getByLoc(player.getLocation());
        }

        if (res == null) {
            plugin.msg(sender, lm.Invalid_Residence);
            return true;
        }

        Visualizer v = new Visualizer(player);
        v.setAreas(res.getAreaArray());
        plugin.getSelectionManager().showBounds(player, v);

        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Show residence boundaries");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res show <residence>"));
        Residence.getInstance().getLocaleManager().CommandTab.put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[residence]"));
    }
}
