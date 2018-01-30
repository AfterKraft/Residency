package com.gabizou.residency.signsStuff;

import com.gabizou.residency.protection.ClaimedResidence;
import org.bukkit.Location;

public class Signs {

    int Category = 0;
    ClaimedResidence Residence = null;

    Location loc = null;

    public Signs() {
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    public Location GetLocation() {
        return this.loc;
    }

    public void setCategory(int Category) {
        this.Category = Category;
    }

    public int GetCategory() {
        return this.Category;
    }

    public void setResidence(ClaimedResidence Residence) {
        this.Residence = Residence;
    }

    public ClaimedResidence GetResidence() {
        return this.Residence;
    }

}
