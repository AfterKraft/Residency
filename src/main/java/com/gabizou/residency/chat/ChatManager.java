package com.gabizou.residency.chat;

import com.gabizou.residency.Residence;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.Server;
import org.spongepowered.api.Server;

import java.util.HashMap;
import java.util.Map;

public class ChatManager implements ChatInterface {

    protected Map<String, ChatChannel> channelmap;
    protected Server server;

    public ChatManager() {
        this.server = Residence.getInstance().getServ();
        this.channelmap = new HashMap<String, ChatChannel>();
    }

    @Override
    public boolean setChannel(String player, String resName) {
        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByName(resName);
        if (res == null) {
            return false;
        }
        return setChannel(player, res);
    }

    @Override
    public boolean setChannel(String player, ClaimedResidence res) {
        this.removeFromChannel(player);
        if (!this.channelmap.containsKey(res.getName())) {
            this.channelmap.put(res.getName(), new ChatChannel(res.getName(), res.getChatPrefix(), res.getChannelColor()));
        }
        this.channelmap.get(res.getName()).join(player);
        return true;
    }

    @Override
    public boolean removeFromChannel(String player) {
        for (ChatChannel chan : this.channelmap.values()) {
            if (chan.hasMember(player)) {
                chan.leave(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public ChatChannel getChannel(String channel) {
        return this.channelmap.get(channel);
    }

    @Override
    public ChatChannel getPlayerChannel(String player) {
        for (ChatChannel chan : this.channelmap.values()) {
            if (chan.hasMember(player)) {
                return chan;
            }
        }
        return null;
    }
}
