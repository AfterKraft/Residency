package com.gabizou.residency.event;

import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.FlagPermissions.FlagState;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ResidenceFlagChangeEvent extends CancellableResidencePlayerFlagEvent {

    private static final HandlerList handlers = new HandlerList();
    FlagState newstate;

    public ResidenceFlagChangeEvent(ClaimedResidence resref, Player player, String flag, FlagType type, FlagState newState, String target) {
        super("RESIDENCE_FLAG_CHANGE", resref, player, flag, type, target);
        this.newstate = newState;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public FlagState getNewState() {
        return this.newstate;
    }

}
