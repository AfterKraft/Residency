package com.gabizou.residency.economy.rent;

import java.util.HashMap;
import java.util.Map;

public class RentedLand {

    public String player = "";
    public long startTime = 0L;
    public long endTime = 0L;
    public boolean AutoPay = true;

    public Map<String, Object> save() {
        Map<String, Object> rentables = new HashMap<>();
        rentables.put("Player", this.player);
        rentables.put("StartTime", this.startTime);
        rentables.put("EndTime", this.endTime);
        rentables.put("AutoRefresh", this.AutoPay);
        return rentables;
    }
}
