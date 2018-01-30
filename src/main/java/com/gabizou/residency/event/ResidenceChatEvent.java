package com.gabizou.residency.event;

import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ResidenceChatEvent extends CancellableResidencePlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    protected String message;
    ChatColor color = ChatColor.WHITE;
    private String prefix = "";
    public ResidenceChatEvent(ClaimedResidence resref, Player player, String prefix, String message, ChatColor color) {
        super("RESIDENCE_CHAT_EVENT", resref, player);
        this.message = message;
        this.prefix = prefix;
        this.color = color;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public String getChatMessage() {
        return this.message;
    }

    public void setChatMessage(String newmessage) {
        this.message = newmessage;
    }

    public String getChatprefix() {
        return ChatColor.translateAlternateColorCodes('&', this.prefix);
    }

    public void setChatprefix(String prefix) {
        this.prefix = prefix;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public void setColor(ChatColor c) {
        this.color = c;
    }
}
