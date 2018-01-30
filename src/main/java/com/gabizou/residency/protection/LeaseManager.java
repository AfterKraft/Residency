package com.gabizou.residency.protection;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.ResidencePlayer;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.economy.EconomyInterface;
import com.gabizou.residency.event.ResidenceDeleteEvent;
import com.gabizou.residency.event.ResidenceDeleteEvent.DeleteCause;
import com.gabizou.residency.permissions.PermissionGroup;
import com.gabizou.residency.utils.GetTime;
import org.bukkit.entity.Player;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LeaseManager {

    ResidenceManager manager;
    private Set<ClaimedResidence> leaseExpireTime;
    private Residence plugin;

    public LeaseManager(Residence plugin) {
        this.plugin = plugin;
        this.manager = plugin.getResidenceManager();
        this.leaseExpireTime = new HashSet<ClaimedResidence>();
    }

    @Deprecated
    public boolean leaseExpires(ClaimedResidence res) {
        return isLeased(res);
    }

    public boolean isLeased(ClaimedResidence res) {
        if (res == null) {
            return false;
        }
        return res.getLeaseExpireTime() != null;
    }

    @Deprecated
    public boolean leaseExpires(String area) {
        return isLeased(this.plugin.getResidenceManager().getByName(area));
    }

    @Deprecated
    public String getExpireTime(String area) {
        return getExpireTime(this.plugin.getResidenceManager().getByName(area));
    }

    public String getExpireTime(ClaimedResidence res) {
        if (res == null) {
            return null;
        }
        Long time = res.getLeaseExpireTime();
        if (time != null) {
            return GetTime.getTime(time);
        }
        return null;
    }

    @Deprecated
    public void removeExpireTime(String area) {
        removeExpireTime(this.plugin.getResidenceManager().getByName(area));
    }

    public void removeExpireTime(ClaimedResidence res) {
        if (res == null) {
            return;
        }
        this.leaseExpireTime.remove(res);
    }

    @Deprecated
    public void setExpireTime(String area, int days) {
        this.setExpireTime(null, area, days);
    }

    @Deprecated
    public void setExpireTime(Player player, String area, int days) {
        setExpireTime(player, this.plugin.getResidenceManager().getByName(area), days);
    }

    public void setExpireTime(Player player, ClaimedResidence res, int days) {
        if (res == null) {
            if (player != null) {
                this.plugin.msg(player, lm.Invalid_Area);
            }
            return;
        }

        res.setLeaseExpireTime(daysToMs(days) + System.currentTimeMillis());
        this.leaseExpireTime.add(res);
        if (player != null) {
            this.plugin.msg(player, lm.Economy_LeaseRenew, getExpireTime(res));
        }
    }

    private static long daysToMs(int days) {
        return ((days) * 24L * 60L * 60L * 1000L);
    }

    @Deprecated
    public void renewArea(String area, Player player) {
        renewArea(this.plugin.getResidenceManager().getByName(area), player);
    }

//    @Deprecated
//    public int getRenewCost(ClaimedResidence res) {
//	double cost = res.getOwnerGroup().getLeaseRenewCost();
//	int amount = (int) Math.ceil(res.getTotalSize() * cost);
//	return amount;
//    }

    public void renewArea(ClaimedResidence res, Player player) {
        if (res == null) {
            return;
        }
        if (!isLeased(res)) {
            this.plugin.msg(player, lm.Economy_LeaseNotExpire);
            return;
        }

        ResidencePlayer rPlayer = this.plugin.getPlayerManager().getResidencePlayer(player);
        PermissionGroup group = rPlayer.getGroup();
        int max = group.getMaxLeaseTime();
        int add = group.getLeaseGiveTime();
        int rem = daysRemaining(res);
        EconomyInterface econ = this.plugin.getEconomyManager();
        if (econ != null) {
            double cost = group.getLeaseRenewCost();
            int amount = (int) Math.ceil(res.getTotalSize() * cost);
            if (cost != 0D) {
                //Account account = iConomy.getBank().getAccount(player.getName());
                if (econ.canAfford(player.getName(), amount)/*account.hasEnough(amount)*/) {
                    econ.subtract(player.getName(), amount);
                    econ.add("Lease Money", amount);
                    this.plugin.msg(player, lm.Economy_MoneyCharged, this.plugin.getEconomyManager().format(amount), econ.getName());
                } else {
                    this.plugin.msg(player, lm.Economy_NotEnoughMoney);
                    return;
                }
            }
        }
        if (rem + add > max) {
            setExpireTime(player, res, max);
            this.plugin.msg(player, lm.Economy_LeaseRenewMax);
            return;
        }
        Long get = res.getLeaseExpireTime();
        if (get != null) {
            get = get + daysToMs(add);
            res.setLeaseExpireTime(get);

            this.leaseExpireTime.add(res);
        } else {
            res.setLeaseExpireTime(daysToMs(add));
            this.leaseExpireTime.add(res);
        }
        this.plugin.msg(player, lm.Economy_LeaseRenew, getExpireTime(res));
    }

    private static int daysRemaining(ClaimedResidence res) {
        if (res == null) {
            return 999;
        }
        Long get = res.getLeaseExpireTime();
        if (get <= System.currentTimeMillis()) {
            return 0;
        }
        return msToDays((int) (get - System.currentTimeMillis()));
    }

    private static int msToDays(long ms) {
        return (int) Math.ceil((((ms / 1000D) / 60D) / 60D) / 24D);
    }

    public void doExpirations() {

        Set<ClaimedResidence> t = new HashSet<ClaimedResidence>(this.leaseExpireTime);

        for (ClaimedResidence res : t) {
            if (res == null) {
                this.leaseExpireTime.remove(res);
                continue;
            }
            if (res.getLeaseExpireTime() > System.currentTimeMillis()) {
                continue;
            }

            String resname = res.getName();
            boolean renewed = false;
            String owner = res.getPermissions().getOwner();

            PermissionGroup group = res.getOwnerGroup();

            double cost = this.getRenewCostD(res);
            if (this.plugin.getConfigManager().enableEconomy() && this.plugin.getConfigManager().autoRenewLeases()) {
                if (cost == 0) {
                    renewed = true;
                } else if (res.getBank().hasEnough(cost)) {
                    res.getBank().subtract(cost);
                    renewed = true;
                    if (this.plugin.getConfigManager().debugEnabled()) {
                        System.out.println("Lease Renewed From Residence Bank: " + resname);
                    }
                } else if (this.plugin.getEconomyManager().canAfford(owner, cost)) {
                    if (this.plugin.getEconomyManager().subtract(owner, cost)) {
                        renewed = true;
                        if (this.plugin.getConfigManager().debugEnabled()) {
                            System.out.println("Lease Renewed From Economy: " + resname);
                        }
                    }
                }
            }
            if (!renewed) {
                if (!this.plugin.getConfigManager().enabledRentSystem() || !this.plugin.getRentManager().isRented(resname)) {
                    ResidenceDeleteEvent resevent = new ResidenceDeleteEvent(null, res, DeleteCause.LEASE_EXPIRE);
                    this.plugin.getServ().getPluginManager().callEvent(resevent);
                    if (!resevent.isCancelled()) {
                        this.manager.removeResidence(res);
                        this.leaseExpireTime.remove(res);
                        if (this.plugin.getConfigManager().debugEnabled()) {
                            System.out.println("Lease NOT removed, Removing: " + resname);
                        }
                    }
                }
            } else {
                if (this.plugin.getConfigManager().enableEconomy() && this.plugin.getConfigManager().enableLeaseMoneyAccount()) {
                    this.plugin.getEconomyManager().add("Lease Money", cost);
                }
                if (this.plugin.getConfigManager().debugEnabled()) {
                    System.out.println("Lease Renew Old: " + res.getName());
                }
                res.setLeaseExpireTime(System.currentTimeMillis() + daysToMs(group.getLeaseGiveTime()));
                if (this.plugin.getConfigManager().debugEnabled()) {
                    System.out.println("Lease Renew New: " + res.getName());
                }
            }

        }
    }

    public double getRenewCostD(ClaimedResidence res) {
        double cost = res.getOwnerGroup().getLeaseRenewCost();
        double amount = res.getTotalSize() * cost;
        amount = Math.round(amount * 100) / 100D;
        return amount;
    }

    public void resetLeases() {
        this.leaseExpireTime.clear();
        String[] list = this.manager.getResidenceList();
        for (String item : list) {
            if (item == null) {
                continue;
            }
            ClaimedResidence res = this.plugin.getResidenceManager().getByName(item);
            if (res != null) {
                setExpireTime(null, res, res.getOwnerGroup().getLeaseGiveTime());
            }

        }
        System.out.println("[Residence] - Set default leases.");
    }

    public Map<String, Long> save() {
        Map<String, Long> m = new HashMap<String, Long>();
        for (ClaimedResidence one : this.leaseExpireTime) {
            m.put(one.getName(), one.getLeaseExpireTime());
        }
        return m;
    }

    @SuppressWarnings("unchecked")
    public LeaseManager load(@SuppressWarnings("rawtypes") Map root) {
        LeaseManager l = new LeaseManager(this.plugin);
        if (root == null) {
            return l;
        }

        for (Object val : root.values()) {
            if (!(val instanceof Long)) {
                root.remove(val);
            }
        }

        Map<String, Long> m = root;

        for (Entry<String, Long> one : m.entrySet()) {
            ClaimedResidence res = this.plugin.getResidenceManager().getByName(one.getKey());
            if (res == null) {
                continue;
            }
            res.setLeaseExpireTime(one.getValue());
            l.leaseExpireTime.add(res);
        }

        return l;
    }
}
