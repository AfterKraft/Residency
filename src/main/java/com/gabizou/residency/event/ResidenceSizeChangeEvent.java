package com.gabizou.residency.event;

import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.CuboidArea;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ResidenceSizeChangeEvent extends CancellableResidencePlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    protected String resname;
    CuboidArea oldarea;
    CuboidArea newarea;
    ClaimedResidence res;
    public ResidenceSizeChangeEvent(Player player, ClaimedResidence res, CuboidArea oldarea, CuboidArea newarea) {
        super("RESIDENCE_SIZE_CHANGE", res, player);
        this.resname = res.getName();
        this.res = res;
        this.oldarea = oldarea;
        this.newarea = newarea;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public ClaimedResidence getResidence() {
        return this.res;
    }

    public String getResidenceName() {
        return this.resname;
    }

    public CuboidArea getOldArea() {
        return this.oldarea;
    }

    public CuboidArea getNewArea() {
        return this.newarea;
    }
}
