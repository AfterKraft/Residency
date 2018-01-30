package com.gabizou.residency.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ResidenceCommandEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    protected boolean cancelled;
    protected String cmd;
    protected String arglist[];
    CommandSender commandsender;
    public ResidenceCommandEvent(String command, String args[], CommandSender sender) {
        super();
        this.cancelled = false;
        this.arglist = args;
        this.cmd = command;
        this.commandsender = sender;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean bln) {
        this.cancelled = bln;
    }

    public String getCommand() {
        return this.cmd;
    }

    public String[] getArgs() {
        return this.arglist;
    }

    public CommandSender getSender() {
        return this.commandsender;
    }

}
