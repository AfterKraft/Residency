package com.gabizou.residency.permissions;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.ResidencePlayer;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.protection.FlagPermissions;
import com.gabizou.residency.protection.FlagPermissions.FlagState;
import ninja.leaping.configurate.ConfigurationNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PermissionGroup {

    protected int xmax;
    protected int ymax;
    protected int zmax;

    protected int xmin;
    protected int ymin;
    protected int zmin;

    protected int Subzonexmax;
    protected int Subzoneymax;
    protected int Subzonezmax;

    protected int Subzonexmin;
    protected int Subzoneymin;
    protected int Subzonezmin;

    protected int resmax;
    protected double costperarea;
    protected double sellperarea = 0;
    protected boolean tpaccess;
    protected int subzonedepth;
    protected int maxSubzones;
    protected FlagPermissions flagPerms;
    protected Map<String, Boolean> creatorDefaultFlags;
    protected Map<String, Map<String, Boolean>> groupDefaultFlags;
    protected Map<String, Boolean> residenceDefaultFlags;
    protected boolean messageperms;
    protected String defaultEnterMessage;
    protected String defaultLeaveMessage;
    protected int maxLeaseTime;
    protected int leaseGiveTime;
    protected double renewcostperarea;
    protected boolean canBuy;
    protected boolean canSell;
    protected boolean buyIgnoreLimits;
    protected boolean cancreate;
    protected String groupname;
    protected int maxPhysical;
    protected boolean unstuck;
    protected boolean kick;
    protected int minHeight;
    protected int maxHeight;
    protected int maxRents;
    protected int MaxRentDays = -1;
    protected int maxRentables;
    protected boolean selectCommandAccess;
    protected boolean itemListAccess;
    protected int priority = 0;

    public PermissionGroup(String name, ConfigurationNode node, FlagPermissions parentFlagPerms) {
        this(name, node);
        this.flagPerms.setParent(parentFlagPerms);
    }

    public PermissionGroup(String name, ConfigurationNode node) {
        this(name);
        this.parseGroup(node);
    }

    public PermissionGroup(String name) {
        this.flagPerms = new FlagPermissions();
        this.creatorDefaultFlags = new HashMap<String, Boolean>();
        this.residenceDefaultFlags = new HashMap<String, Boolean>();
        this.groupDefaultFlags = new HashMap<String, Map<String, Boolean>>();
        this.groupname = name;
    }

    private void parseGroup(ConfigurationNode limits) {
        if (limits == null) {
            return;
        }
        this.cancreate = limits.getBoolean("Residence.CanCreate", false);
        this.resmax = limits.getInt("Residence.MaxResidences", 0);
        this.maxPhysical = limits.getInt("Residence.MaxAreasPerResidence", 2);

        this.xmax = limits.getInt("Residence.MaxEastWest", 0);
        this.xmin = limits.getInt("Residence.MinEastWest", 0);
        this.xmin = this.xmin > this.xmax ? this.xmax : this.xmin;

        this.ymax = limits.getInt("Residence.MaxUpDown", 0);
        this.ymin = limits.getInt("Residence.MinUpDown", 0);
        this.ymin = this.ymin > this.ymax ? this.ymax : this.ymin;

        this.zmax = limits.getInt("Residence.MaxNorthSouth", 0);
        this.zmin = limits.getInt("Residence.MinNorthSouth", 0);
        this.zmin = this.zmin > this.zmax ? this.zmax : this.zmin;

        this.minHeight = limits.getInt("Residence.MinHeight", 0);
        this.maxHeight = limits.getInt("Residence.MaxHeight", 255);
        this.tpaccess = limits.getBoolean("Residence.CanTeleport", false);

        this.maxSubzones = limits.getInt("Residence.MaxSubzonesInArea", 3);

        this.subzonedepth = limits.getInt("Residence.SubzoneDepth", 0);

        this.Subzonexmax = limits.getInt("Residence.SubzoneMaxEastWest", this.xmax);
        this.Subzonexmax = this.xmax < this.Subzonexmax ? this.xmax : this.Subzonexmax;
        this.Subzonexmin = limits.getInt("Residence.SubzoneMinEastWest", 0);
        this.Subzonexmin = this.Subzonexmin > this.Subzonexmax ? this.Subzonexmax : this.Subzonexmin;

        this.Subzoneymax = limits.getInt("Residence.SubzoneMaxUpDown", this.ymax);
        this.Subzoneymax = this.ymax < this.Subzoneymax ? this.ymax : this.Subzoneymax;
        this.Subzoneymin = limits.getInt("Residence.SubzoneMinUpDown", 0);
        this.Subzoneymin = this.Subzoneymin > this.Subzoneymax ? this.Subzoneymax : this.Subzoneymin;

        this.Subzonezmax = limits.getInt("Residence.SubzoneMaxNorthSouth", this.zmax);
        this.Subzonezmax = this.zmax < this.Subzonezmax ? this.zmax : this.Subzonezmax;
        this.Subzonezmin = limits.getInt("Residence.SubzoneMinNorthSouth", 0);
        this.Subzonezmin = this.Subzonezmin > this.Subzonezmax ? this.Subzonezmax : this.Subzonezmin;

        this.messageperms = limits.getBoolean("Messaging.CanChange", false);
        this.defaultEnterMessage = limits.getString("Messaging.DefaultEnter", null);
        this.defaultLeaveMessage = limits.getString("Messaging.DefaultLeave", null);
        this.maxLeaseTime = limits.getInt("Lease.MaxDays", 16);
        this.leaseGiveTime = limits.getInt("Lease.RenewIncrement", 14);
        this.maxRents = limits.getInt("Rent.MaxRents", 0);

        if (limits.contains("Rent.MaxRentDays")) {
            this.MaxRentDays = limits.getInt("Rent.MaxRentDays", -1);
        }

        this.maxRentables = limits.getInt("Rent.MaxRentables", 0);
        this.renewcostperarea = limits.getDouble("Economy.RenewCost", 0.02D);
        this.canBuy = limits.getBoolean("Economy.CanBuy", false);
        this.canSell = limits.getBoolean("Economy.CanSell", false);
        this.buyIgnoreLimits = limits.getBoolean("Economy.IgnoreLimits", false);
        this.costperarea = limits.getDouble("Economy.BuyCost", 0);

        if (limits.contains("Economy.SellCost")) {
            this.sellperarea = limits.getDouble("Economy.SellCost", 0);
        }

        this.unstuck = limits.getBoolean("Residence.Unstuck", false);
        this.kick = limits.getBoolean("Residence.Kick", false);
        this.selectCommandAccess = limits.getBoolean("Residence.SelectCommandAccess", true);
        this.itemListAccess = limits.getBoolean("Residence.ItemListAccess", true);
        ConfigurationSection node = limits.getConfigurationSection("Flags.Permission");
        Set<String> flags = null;
        if (node != null) {
            flags = node.getKeys(false);
        }
        if (flags != null) {
            Iterator<String> flagit = flags.iterator();
            while (flagit.hasNext()) {
                String flagname = flagit.next();
                boolean access = limits.getBoolean("Flags.Permission." + flagname, false);
                this.flagPerms.setFlag(flagname, access ? FlagState.TRUE : FlagState.FALSE);
            }
        }
        node = limits.getConfigurationSection("Flags.CreatorDefault");
        if (node != null) {
            flags = node.getKeys(false);
        }
        if (flags != null) {
            Iterator<String> flagit = flags.iterator();
            while (flagit.hasNext()) {
                String flagname = flagit.next();
                boolean access = limits.getBoolean("Flags.CreatorDefault." + flagname, false);
                this.creatorDefaultFlags.put(flagname, access);
            }

        }
        node = limits.getConfigurationSection("Flags.Default");
        if (node != null) {
            flags = node.getKeys(false);
        }
        if (flags != null) {
            Iterator<String> flagit = flags.iterator();
            while (flagit.hasNext()) {
                String flagname = flagit.next();
                boolean access = limits.getBoolean("Flags.Default." + flagname, false);
                this.residenceDefaultFlags.put(flagname, access);
            }
        }
        node = limits.getConfigurationSection("Flags.GroupDefault");
        Set<String> groupDef = null;
        if (node != null) {
            groupDef = node.getKeys(false);
        }
        if (groupDef != null) {
            Iterator<String> groupit = groupDef.iterator();
            while (groupit.hasNext()) {
                String name = groupit.next();
                Map<String, Boolean> gflags = new HashMap<String, Boolean>();
                flags = limits.getConfigurationSection("Flags.GroupDefault." + name).getKeys(false);
                Iterator<String> flagit = flags.iterator();
                while (flagit.hasNext()) {
                    String flagname = flagit.next();
                    boolean access = limits.getBoolean("Flags.GroupDefault." + name + "." + flagname, false);
                    gflags.put(flagname, access);
                }
                this.groupDefaultFlags.put(name, gflags);
            }
        }
    }

    public PermissionGroup(String name, ConfigurationSection node, FlagPermissions parentFlagPerms, int priority) {
        this(name, node);
        this.flagPerms.setParent(parentFlagPerms);
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int number) {
        this.priority = number;
    }

    public int getMaxX() {
        return this.xmax;
    }

    public int getMaxY() {
        return this.ymax;
    }

    public int getMaxZ() {
        return this.zmax;
    }

    public int getMinX() {
        return this.xmin;
    }

    public int getMinY() {
        return this.ymin;
    }

    public int getMinZ() {
        return this.zmin;
    }

    public int getSubzoneMaxX() {
        return this.Subzonexmax;
    }

    public int getSubzoneMaxY() {
        return this.Subzoneymax;
    }

    public int getSubzoneMaxZ() {
        return this.Subzonezmax;
    }

    public int getSubzoneMinX() {
        return this.Subzonexmin;
    }

    public int getSubzoneMinY() {
        return this.Subzoneymin;
    }

    public int getSubzoneMinZ() {
        return this.Subzonezmin;
    }

    public int getMinHeight() {
        return this.minHeight;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public int getMaxZones() {
        return this.resmax;
    }

    public double getCostPerBlock() {
        return this.costperarea;
    }

    public double getSellPerBlock() {
        return this.sellperarea;
    }

    public boolean hasTpAccess() {
        return this.tpaccess;
    }

    public int getMaxSubzoneDepth() {
        return this.subzonedepth;
    }

    public int getMaxSubzones() {
        return this.maxSubzones;
    }

    public boolean canSetEnterLeaveMessages() {
        return this.messageperms;
    }

    public String getDefaultEnterMessage() {
        return this.defaultEnterMessage;
    }

    public String getDefaultLeaveMessage() {
        return this.defaultLeaveMessage;
    }

    public int getMaxLeaseTime() {
        return this.maxLeaseTime;
    }

    public int getLeaseGiveTime() {
        return this.leaseGiveTime;
    }

    public double getLeaseRenewCost() {
        return this.renewcostperarea;
    }

    public boolean canBuyLand() {
        return this.canBuy;
    }

    public boolean canSellLand() {
        return this.canSell;
    }

    public int getMaxRents() {
        return this.maxRents;
    }

    public int getMaxRentables() {
        return this.maxRentables;
    }

    public boolean buyLandIgnoreLimits() {
        return this.buyIgnoreLimits;
    }

    public boolean hasUnstuckAccess() {
        return this.unstuck;
    }

    public boolean hasKickAccess() {
        return this.kick;
    }

    public int getMaxPhysicalPerResidence() {
        return this.maxPhysical;
    }

    public Set<Entry<String, Boolean>> getDefaultResidenceFlags() {
        return this.residenceDefaultFlags.entrySet();
    }

    public Set<Entry<String, Boolean>> getDefaultCreatorFlags() {
        return this.creatorDefaultFlags.entrySet();
    }

    public Set<Entry<String, Map<String, Boolean>>> getDefaultGroupFlags() {
        return this.groupDefaultFlags.entrySet();
    }

    public boolean hasFlagAccess(Flags flag) {
        return this.flagPerms.has(flag, false);
    }

    @Deprecated
    public boolean hasFlagAccess(String flag) {
        return this.flagPerms.has(flag, false);
    }

    public boolean selectCommandAccess() {
        return this.selectCommandAccess;
    }

    public boolean itemListAccess() {
        return this.itemListAccess;
    }

    public void printLimits(CommandSender player, OfflinePlayer target, boolean resadmin) {

        ResidencePlayer rPlayer = Residence.getInstance().getPlayerManager().getResidencePlayer(target.getName());
        PermissionGroup group = rPlayer.getGroup();

        Residence.getInstance().msg(player, lm.General_Separator);
        Residence.getInstance().msg(player, lm.Limits_PGroup, Residence.getInstance().getPermissionManager().getPermissionsGroup(target.getName(),
            target.isOnline() ? Bukkit.getPlayer(target.getName()).getWorld().getName()
                              : Residence.getInstance().getConfigManager().getDefaultWorld()));
        Residence.getInstance().msg(player, lm.Limits_RGroup, group.getGroupName());
        if (target.isOnline() && resadmin) {
            Residence.getInstance().msg(player, lm.Limits_Admin, Residence.getInstance().getPermissionManager().isResidenceAdmin(player));
        }
        Residence.getInstance().msg(player, lm.Limits_CanCreate, group.canCreateResidences());
        Residence.getInstance().msg(player, lm.Limits_MaxRes, rPlayer.getMaxRes());
        Residence.getInstance().msg(player, lm.Limits_NumberOwn, rPlayer.getResAmount());
        Residence.getInstance().msg(player, lm.Limits_MaxEW, group.xmin + "-" + group.xmax);
        Residence.getInstance().msg(player, lm.Limits_MaxNS, group.zmin + "-" + group.zmax);
        Residence.getInstance().msg(player, lm.Limits_MaxUD, group.ymin + "-" + group.ymax);
        Residence.getInstance().msg(player, lm.Limits_MinMax, group.minHeight, group.maxHeight);
        Residence.getInstance().msg(player, lm.Limits_MaxSubzones, rPlayer.getMaxSubzones());
        Residence.getInstance().msg(player, lm.Limits_MaxSubDepth, rPlayer.getMaxSubzoneDepth());
        Residence.getInstance().msg(player, lm.Limits_MaxRents,
            rPlayer.getMaxRents() + (getMaxRentDays() != -1 ? Residence.getInstance().msg(lm.Limits_MaxRentDays, getMaxRentDays())
                                                            : ""));
        Residence.getInstance().msg(player, lm.Limits_EnterLeave, group.messageperms);
        if (Residence.getInstance().getEconomyManager() != null) {
            Residence.getInstance().msg(player, lm.Limits_Cost, group.costperarea);
            Residence.getInstance().msg(player, lm.Limits_Sell, group.sellperarea);
        }
        Residence.getInstance().msg(player, lm.Limits_Flag, group.flagPerms.listFlags());
        if (Residence.getInstance().getConfigManager().useLeases()) {
            Residence.getInstance().msg(player, lm.Limits_MaxDays, group.maxLeaseTime);
            Residence.getInstance().msg(player, lm.Limits_LeaseTime, group.leaseGiveTime);
            Residence.getInstance().msg(player, lm.Limits_RenewCost, group.renewcostperarea);
        }
        Residence.getInstance().msg(player, lm.General_Separator);
    }

//    public boolean inLimits(CuboidArea area) {
//	if (area.getXSize() > xmax || area.getYSize() > ymax || area.getZSize() > zmax) {
//	    return false;
//	}
//	return true;
//    }
//
//    public boolean inLimitsSubzone(CuboidArea area) {
//	if (area.getXSize() > Subzonexmax || area.getYSize() > Subzoneymax || area.getZSize() > Subzonezmax) {
//	    return false;
//	}
//	return true;
//    }

    public String getGroupName() {
        return this.groupname;
    }

    public boolean canCreateResidences() {
        return this.cancreate;
    }

    public int getMaxRentDays() {
        return this.MaxRentDays;
    }

}
