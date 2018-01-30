package com.gabizou.residency.event;

import com.gabizou.residency.Residence;
import com.gabizou.residency.api.event.ResidenceEvent;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.entity.Player;

public class ResidencePlayerEvent extends ResidenceEvent implements ResidencePlayerEventInterface {

    Player p;

    public ResidencePlayerEvent(String eventName, ClaimedResidence resref, Player player) {
        super(eventName, resref);
        this.res = resref;
        this.p = player;
    }

    @Override
    public boolean isAdmin() {
        if (isPlayer()) {
            return Residence.getInstance().getPermissionManager().isResidenceAdmin(this.p);
        }
        return true;
    }

    @Override
    public boolean isPlayer() {
        return this.p != null;
    }

    @Override
    public Player getPlayer() {
        return this.p;
    }
}
