package com.gabizou.residency.event;

import com.gabizou.residency.Residence;
import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.entity.Player;

public class ResidencePlayerFlagEvent extends ResidenceFlagEvent implements ResidencePlayerEventInterface {

    Player p;

    public ResidencePlayerFlagEvent(String eventName, ClaimedResidence resref, Player player, String flag, FlagType type, String target) {
        super(eventName, resref, flag, type, target);
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
