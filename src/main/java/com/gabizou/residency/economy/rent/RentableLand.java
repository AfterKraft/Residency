package com.gabizou.residency.economy.rent;

import com.gabizou.residency.Residence;

import java.util.HashMap;
import java.util.Map;

public class RentableLand {

    public int days = 0;
    public int cost = Integer.MAX_VALUE;
    public boolean AllowRenewing = Residence.getInstance().getConfigManager().isRentAllowRenewing();
    public boolean StayInMarket = Residence.getInstance().getConfigManager().isRentStayInMarket();
    public boolean AllowAutoPay = Residence.getInstance().getConfigManager().isRentAllowAutoPay();

    public RentableLand() {
    }

    public Map<String, Object> save() {
        Map<String, Object> rented = new HashMap<>();
        rented.put("Days", this.days);
        rented.put("Cost", this.cost);
        rented.put("Repeatable", this.AllowRenewing);
        rented.put("StayInMarket", this.StayInMarket);
        rented.put("AllowAutoPay", this.AllowAutoPay);
        return rented;
    }
}
