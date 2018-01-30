package com.gabizou.residency.economy;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.ResidencePlayer;
import com.gabizou.residency.containers.Visualizer;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.permissions.PermissionGroup;
import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.CuboidArea;
import com.gabizou.residency.text.help.PageInfo;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TransactionManager implements MarketBuyInterface {

    private Set<ClaimedResidence> sellAmount;
    private Residence plugin;

    public TransactionManager(Residence plugin) {
        this.plugin = plugin;
        this.sellAmount = new HashSet<ClaimedResidence>();
    }

    public boolean chargeEconomyMoney(Player player, double chargeamount) {
        EconomyInterface econ = this.plugin.getEconomyManager();
        if (econ == null) {
            this.plugin.msg(player, lm.Economy_MarketDisabled);
            return false;
        }
        if (!econ.canAfford(player.getName(), chargeamount)) {
            this.plugin.msg(player, lm.Economy_NotEnoughMoney);
            return false;
        }
        econ.subtract(player.getName(), chargeamount);
        try {
            this.plugin.msg(player, lm.Economy_MoneyCharged, chargeamount, econ.getName());
        } catch (Exception e) {
        }
        return true;
    }

    public boolean giveEconomyMoney(Player player, double amount) {
        if (player == null) {
            return false;
        }
        if (amount == 0) {
            return true;
        }
        EconomyInterface econ = this.plugin.getEconomyManager();
        if (econ == null) {
            this.plugin.msg(player, lm.Economy_MarketDisabled);
            return false;
        }

        econ.add(player.getName(), amount);
        this.plugin.msg(player, lm.Economy_MoneyAdded, this.plugin.getEconomyManager().format(amount), econ.getName());
        return true;
    }

    public void putForSale(String areaname, Player player, int amount, boolean resadmin) {
        ClaimedResidence res = this.plugin.getResidenceManager().getByName(areaname);
        putForSale(res, player, amount, resadmin);
    }

    public void putForSale(ClaimedResidence res, Player player, int amount, boolean resadmin) {

        if (res == null) {
            this.plugin.msg(player, lm.Invalid_Residence);
            return;
        }

        if (this.plugin.getConfigManager().enabledRentSystem()) {
            if (!resadmin) {
                if (res.isForRent()) {
                    this.plugin.msg(player, lm.Economy_RentSellFail);
                    return;
                }
                if (res.isSubzoneForRent()) {
                    this.plugin.msg(player, lm.Economy_SubzoneRentSellFail);
                    return;
                }
                if (res.isParentForRent()) {
                    this.plugin.msg(player, lm.Economy_ParentRentSellFail);
                    return;
                }
            }
        }

        if (!this.plugin.getConfigManager().isSellSubzone()) {
            if (res.isSubzone()) {
                this.plugin.msg(player, lm.Economy_SubzoneSellFail);
                return;
            }
        }

        if (!resadmin) {
            if (!this.plugin.getConfigManager().enableEconomy() || this.plugin.getEconomyManager() == null) {
                this.plugin.msg(player, lm.Economy_MarketDisabled);
                return;
            }

            ResidencePlayer rPlayer = this.plugin.getPlayerManager().getResidencePlayer(player);

            if (!resadmin && !(rPlayer.getGroup().canSellLand() || player.hasPermission("residence.sell"))) {
                this.plugin.msg(player, lm.General_NoPermission);
                return;
            }
            if (amount <= 0) {
                this.plugin.msg(player, lm.Invalid_Amount);
                return;
            }
        }

        if (!res.isOwner(player) && !resadmin) {
            this.plugin.msg(player, lm.General_NoPermission);
            return;
        }
        if (this.sellAmount.contains(res)) {
            this.plugin.msg(player, lm.Economy_AlreadySellFail);
            return;
        }
        res.setSellPrice(amount);
        this.sellAmount.add(res);
        this.plugin.getSignUtil().CheckSign(res);
        this.plugin.msg(player, lm.Residence_ForSale, res.getName(), amount);
    }

    public void removeFromSale(Player player, String areaname, boolean resadmin) {
        ClaimedResidence res = this.plugin.getResidenceManager().getByName(areaname);
        removeFromSale(player, res, resadmin);
    }

    public void removeFromSale(Player player, ClaimedResidence res, boolean resadmin) {
        if (res == null) {
            this.plugin.msg(player, lm.Invalid_Area);
            return;
        }

        if (!res.isForSell()) {
            this.plugin.msg(player, lm.Residence_NotForSale);
            return;
        }
        if (res.isOwner(player) || resadmin) {
            removeFromSale(res);
            this.plugin.getSignUtil().CheckSign(res);
            this.plugin.msg(player, lm.Residence_StopSelling);
        } else {
            this.plugin.msg(player, lm.General_NoPermission);
        }
    }

    public boolean viewSaleInfo(String areaname, Player player) {
        ClaimedResidence res = this.plugin.getResidenceManager().getByName(areaname);
        return viewSaleInfo(res, player);
    }

    public boolean viewSaleInfo(ClaimedResidence res, Player player) {

        if (res == null || !res.isForSell()) {
            return false;
        }

        if (!this.sellAmount.contains(res)) {
            return false;
        }

        this.plugin.msg(player, lm.General_Separator);
        this.plugin.msg(player, lm.Area_Name, res.getName());
        this.plugin.msg(player, lm.Economy_SellAmount, res.getSellPrice());
        if (this.plugin.getConfigManager().useLeases()) {
            String etime = this.plugin.getLeaseManager().getExpireTime(res);
            if (etime != null) {
                this.plugin.msg(player, lm.Economy_LeaseExpire, etime);
            }
        }
        this.plugin.msg(player, lm.General_Separator);
        return true;
    }

    public void printForSaleResidences(Player player, int page) {
        List<ClaimedResidence> toRemove = new ArrayList<ClaimedResidence>();
        this.plugin.msg(player, lm.Economy_LandForSale);
        StringBuilder sbuild = new StringBuilder();
        sbuild.append(ChatColor.GREEN);

        PageInfo pi = new PageInfo(10, this.sellAmount.size(), page);

        int position = -1;
        for (ClaimedResidence res : this.sellAmount) {
            position++;
            if (position > pi.getEnd()) {
                break;
            }
            if (!pi.isInRange(position)) {
                continue;
            }

            if (res == null) {
                toRemove.add(res);
                continue;
            }
            this.plugin.msg(player, lm.Economy_SellList, pi.getPositionForOutput(position), res.getName(), res.getSellPrice(), res.getOwner());
        }

        for (ClaimedResidence one : toRemove) {
            this.sellAmount.remove(one);
        }
        this.plugin.getInfoPageManager().ShowPagination(player, pi.getTotalPages(), page, "/res market list sell");
    }

    public void clearSales() {
        for (ClaimedResidence res : this.sellAmount) {
            if (res == null) {
                continue;
            }
            res.setSellPrice(-1);
        }
        this.sellAmount.clear();
        System.out.println("[Residence] - ReInit land selling.");
    }

    public void load(Map<String, Integer> root) {
        if (root == null) {
            return;
        }

        for (Entry<String, Integer> one : root.entrySet()) {
            ClaimedResidence res = this.plugin.getResidenceManager().getByName(one.getKey());
            if (res == null) {
                continue;
            }
            res.setSellPrice(one.getValue());
            this.sellAmount.add(res);
        }
    }

    public Map<String, Integer> save() {
        return getBuyableResidences();
    }

    @Override
    public Map<String, Integer> getBuyableResidences() {
        Map<String, Integer> list = new HashMap<String, Integer>();
        for (ClaimedResidence res : this.sellAmount) {
            if (res == null) {
                continue;
            }
            list.put(res.getName(), res.getSellPrice());
        }
        return list;
    }

    @Override
    public boolean putForSale(String areaname, int amount) {
        ClaimedResidence res = this.plugin.getResidenceManager().getByName(areaname);
        return putForSale(res, amount);
    }

    public boolean putForSale(ClaimedResidence res, int amount) {
        if (res == null) {
            return false;
        }

        if (this.plugin.getConfigManager().enabledRentSystem() && (res.isForRent() || res.isSubzoneForRent() || res.isParentForRent())) {
            return false;
        }

        if (this.sellAmount.contains(res)) {
            return false;
        }

        res.setSellPrice(amount);
        this.sellAmount.add(res);
        return true;
    }

    @Override
    public void buyPlot(String areaname, Player player, boolean resadmin) {
        ClaimedResidence res = this.plugin.getResidenceManager().getByName(areaname);
        buyPlot(res, player, resadmin);
    }

    public void buyPlot(ClaimedResidence res, Player player, boolean resadmin) {
        if (res == null || !res.isForSell()) {
            this.plugin.msg(player, lm.Invalid_Residence);
            return;
        }

        ResidencePlayer rPlayer = this.plugin.getPlayerManager().getResidencePlayer(player);
        PermissionGroup group = rPlayer.getGroup();
        if (!resadmin) {
            if (!this.plugin.getConfigManager().enableEconomy() || this.plugin.getEconomyManager() == null) {
                this.plugin.msg(player, lm.Economy_MarketDisabled);
                return;
            }
            boolean canbuy = group.canBuyLand() || player.hasPermission("residence.buy");
            if (!canbuy && !resadmin) {
                this.plugin.msg(player, lm.General_NoPermission);
                return;
            }
        }

        if (res.getPermissions().getOwner().equals(player.getName())) {
            this.plugin.msg(player, lm.Economy_OwnerBuyFail);
            return;
        }
        if (this.plugin.getResidenceManager().getOwnedZoneCount(player.getName()) >= rPlayer.getMaxRes() && !resadmin) {
            this.plugin.msg(player, lm.Residence_TooMany);
            return;
        }
        Server serv = this.plugin.getServ();
        int amount = res.getSellPrice();

        if (!resadmin && !group.buyLandIgnoreLimits()) {
            CuboidArea[] areas = res.getAreaArray();
            for (CuboidArea thisarea : areas) {
                if (!res.isSubzone() && !res.isSmallerThanMax(player, thisarea, resadmin) || res.isSubzone() && !res
                    .isSmallerThanMaxSubzone(player, thisarea,
                        resadmin)) {
                    this.plugin.msg(player, lm.Residence_BuyTooBig);
                    return;
                }
            }
        }

        EconomyInterface econ = this.plugin.getEconomyManager();
        if (econ == null) {
            this.plugin.msg(player, lm.Economy_MarketDisabled);
            return;
        }

        String buyerName = player.getName();
        String sellerName = res.getPermissions().getOwner();
        Player sellerNameFix = this.plugin.getServ().getPlayer(sellerName);
        if (sellerNameFix != null) {
            sellerName = sellerNameFix.getName();
        }

        if (econ.canAfford(buyerName, amount)) {
            if (!econ.transfer(buyerName, sellerName, amount)) {
                player.sendMessage(ChatColor.RED + "Error, could not transfer " + amount + " from " + buyerName + " to " + sellerName);
                return;
            }
            res.getPermissions().setOwner(player, true);
            res.getPermissions().applyDefaultFlags();
            removeFromSale(res);

            if (this.plugin.getConfigManager().isRemoveLwcOnBuy()) {
                this.plugin.getResidenceManager().removeLwcFromResidence(player, res);
            }

            this.plugin.getSignUtil().CheckSign(res);

            Visualizer v = new Visualizer(player);
            v.setAreas(res);
            this.plugin.getSelectionManager().showBounds(player, v);

            this.plugin.msg(player, lm.Economy_MoneyCharged, this.plugin.getEconomyManager().format(amount), econ.getName());
            this.plugin.msg(player, lm.Residence_Bought, res.getResidenceName());
            Player seller = serv.getPlayer(sellerName);
            if (seller != null && seller.isOnline()) {
                seller.sendMessage(this.plugin.msg(lm.Residence_Buy, player.getName(), res.getResidenceName()));
                seller.sendMessage(this.plugin.msg(lm.Economy_MoneyCredit, this.plugin.getEconomyManager().format(amount), econ.getName()));
            }
        } else {
            this.plugin.msg(player, lm.Economy_NotEnoughMoney);
        }

    }

    public void removeFromSale(ClaimedResidence res) {
        if (res == null) {
            return;
        }
        this.sellAmount.remove(res);
        this.plugin.getSignUtil().removeSign(res);
    }

    @Override
    public void removeFromSale(String areaname) {
        ClaimedResidence res = this.plugin.getResidenceManager().getByName(areaname);
        removeFromSale(res);
    }

    @Override
    public boolean isForSale(String areaname) {
        ClaimedResidence res = this.plugin.getResidenceManager().getByName(areaname);
        return isForSale(res);
    }

    public boolean isForSale(ClaimedResidence res) {
        if (res == null) {
            return false;
        }
        return this.sellAmount.contains(res);
    }

    @Override
    public int getSaleAmount(String areaname) {
        ClaimedResidence res = this.plugin.getResidenceManager().getByName(areaname);
        return getSaleAmount(res);
    }

    public int getSaleAmount(ClaimedResidence res) {
        if (res == null) {
            return -1;
        }
        return res.getSellPrice();
    }
}
