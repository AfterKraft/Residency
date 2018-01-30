package com.gabizou.residency.commands;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.CommandAnnotation;
import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.cmd;
import com.gabizou.residency.containers.lm;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class signupdate implements cmd {

    @Override
    @CommandAnnotation(simple = false, priority = 5700)
    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender) {
        if (args.length == 1) {
            if (!resadmin) {
                plugin.msg(sender, lm.General_NoPermission);
                return true;
            }
            int number = plugin.getSignUtil().updateAllSigns();
            plugin.msg(sender, lm.Sign_Updated, number);
            return true;
        }
        return false;
    }

    @Override
    public void getLocale(ConfigReader c, String path) {
        c.get(path + "Description", "Updated residence signs");
        c.get(path + "Info", Arrays.asList("&eUsage: &6/res signupdate"));
    }
}
