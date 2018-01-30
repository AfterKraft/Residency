package com.gabizou.residency.event;

import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.CuboidArea;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ResidenceCreationEvent extends CancellableResidencePlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    protected String resname;
    CuboidArea area;

    public ResidenceCreationEvent(Player player, String newname, ClaimedResidence resref, CuboidArea resarea) {
        super("RESIDENCE_CREATE", resref, player);
        this.resname = newname;
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

//    public void setResidenceName(String name) {
//	resname = name;
//    }

    public CuboidArea getPhysicalArea() {
        return this.area;
    }

//    public void setPhysicalArea(CuboidArea newarea) {
//	area = newarea;
//    }
}
