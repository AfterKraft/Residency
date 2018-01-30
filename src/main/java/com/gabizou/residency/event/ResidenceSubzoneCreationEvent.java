package com.gabizou.residency.event;

import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.CuboidArea;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ResidenceSubzoneCreationEvent extends CancellableResidencePlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    protected String resname;
    CuboidArea area;

    public ResidenceSubzoneCreationEvent(Player player, String name, ClaimedResidence resref, CuboidArea resarea) {
        super("RESIDENCE_SUBZONE_CREATE", resref, player);
        this.resname = name;
        this.area = resarea;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public String getResidenceName() {
        return this.resname;
    }

    public void setResidenceName(String name) {
        this.resname = name;
    }

    public CuboidArea getPhysicalArea() {
        return this.area;
    }

    public void setPhysicalArea(CuboidArea newarea) {
        this.area = newarea;
    }
}
