package com.gabizou.residency.chat;

import com.gabizou.residency.Residence;
import com.gabizou.residency.event.ResidenceChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.spongepowered.api.Server;

import java.util.ArrayList;
import java.util.List;

public class ChatChannel {

    protected String channelName;
    protected List<String> members;
    protected String ChatPrefix = "";
    protected ChatColor ChannelColor = ChatColor.WHITE;

    public ChatChannel(String channelName, String ChatPrefix, ChatColor chatColor) {
        this.channelName = channelName;
        this.ChatPrefix = ChatPrefix;
        this.ChannelColor = chatColor;
        this.members = new ArrayList<String>();
    }

    public String getChannelName() {
        return this.channelName;
    }

    public void setChatPrefix(String ChatPrefix) {
        this.ChatPrefix = ChatPrefix;
    }

    public void setChannelColor(ChatColor ChannelColor) {
        this.ChannelColor = ChannelColor;
    }

    public void chat(String sourcePlayer, String message) {
        Server serv = Residence.getInstance().getServ();
        ResidenceChatEvent
            cevent =
            new ResidenceChatEvent(Residence.getInstance().getResidenceManager().getByName(this.channelName), serv.getPlayer(sourcePlayer),
                this.ChatPrefix, message,
                this.ChannelColor);
        Residence.getInstance().getServ().getPluginManager().callEvent(cevent);
		if (cevent.isCancelled()) {
			return;
		}
        for (String member : this.members) {
            Player player = serv.getPlayer(member);

            Residence.getInstance().msg(player,
                cevent.getChatprefix() + " " + Residence.getInstance().getConfigManager().getChatColor() + sourcePlayer + ": " + cevent.getColor()
                + cevent
                    .getChatMessage());
        }
        Bukkit.getConsoleSender()
            .sendMessage("ResidentialChat[" + this.channelName + "] - " + sourcePlayer + ": " + ChatColor.stripColor(cevent.getChatMessage()));
    }

    public void join(String player) {
		if (!this.members.contains(player)) {
			this.members.add(player);
		}
    }

    public void leave(String player) {
        this.members.remove(player);
    }

    public boolean hasMember(String player) {
        return this.members.contains(player);
    }

    public int memberCount() {
        return this.members.size();
    }
}
