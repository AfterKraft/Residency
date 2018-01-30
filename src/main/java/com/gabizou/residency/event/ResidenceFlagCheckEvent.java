package com.gabizou.residency.event;

import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.event.HandlerList;

public class ResidenceFlagCheckEvent extends ResidenceFlagEvent {

    private static final HandlerList handlers = new HandlerList();
    boolean defaultvalue;
    private boolean override;
    private boolean overridevalue;
    public ResidenceFlagCheckEvent(ClaimedResidence resref, String flag, FlagType type, String target, boolean defaultValue) {
        super("RESIDENCE_FLAG_CHECK", resref, flag, type, target);
        this.defaultvalue = defaultValue;
        this.override = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean isOverriden() {
        return this.override;
    }

    public void overrideCheck(boolean flagval) {
        this.overridevalue = flagval;
        this.override = true;
    }

    public boolean getOverrideValue() {
        return this.overridevalue;
    }

    public boolean getDefaultValue() {
        return this.defaultvalue;
    }
}
