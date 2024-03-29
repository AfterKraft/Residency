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

public class tp implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 1400)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (args.length != 2) {
            return false;
        }

        ClaimedResidence res = plugin.getResidenceManager().getByName(args[1]);

        if (res == null) {
            plugin.msg(player, lm.Invalid_Residence);
            return true;
        }
        res.tpToResidence(player, player, resadmin);
        return true;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Teleport to a residence");
        c.get(path + "Info",
            Arrays.asList("&eUsage: &6/res tp [residence]", "Teleports you to a residence, you must have +tp flag access or be the owner.",
                "Your permission group must also be allowed to teleport by the server admin."));
        Residence.getInstance().getLocaleManager().CommandTab.put(Arrays.asList(this.getClass().getSimpleName()), Arrays.asList("[residence]"));
    }
}
