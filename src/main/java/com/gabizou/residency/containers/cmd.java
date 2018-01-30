package com.gabizou.residency.containers;

import com.gabizou.residency.Residence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface cmd {

    public boolean perform(Residence plugin, String[] args, boolean resadmin, Command command, CommandSender sender);

    public void getLocale(ConfigReader c, String path);

}
