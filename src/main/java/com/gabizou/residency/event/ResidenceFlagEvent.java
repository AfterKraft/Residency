package com.gabizou.residency.event;

import com.gabizou.residency.api.event.ResidenceEvent;
import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.FlagPermissions.FlagState;
import org.bukkit.event.HandlerList;

public class ResidenceFlagEvent extends ResidenceEvent {

    private static final HandlerList handlers = new HandlerList();
    String flagname;
    FlagType flagtype;
    FlagState flagstate;
    String flagtarget;
    public ResidenceFlagEvent(String eventName, ClaimedResidence resref, String flag, FlagType type, String target) {
        super(eventName, resref);
        this.flagname = flag;
        this.flagtype = type;
        this.flagtarget = target;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public String getFlag() {
        return this.flagname;
    }

    public FlagType getFlagType() {
        return this.flagtype;
    }

    public String getFlagTargetPlayerOrGroup() {
        return this.flagtarget;
    }

    public enum FlagType {
        RESIDENCE, GROUP, PLAYER
    }
}
