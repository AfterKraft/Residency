package com.gabizou.residency.containers;

import com.gabizou.residency.Residence;
import com.gabizou.residency.permissions.PermissionGroup;
import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.vaultinterface.ResidenceVaultAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ResidencePlayer {

    private String userName = null;
    private Player player = null;
    private OfflinePlayer ofPlayer = null;
    private UUID uuid = null;

    private Set<ClaimedResidence> ResidenceList = new HashSet<ClaimedResidence>();
    private ClaimedResidence mainResidence = null;

    private PlayerGroup groups = null;

    private int maxRes = -1;
    private int maxRents = -1;
    private int maxSubzones = -1;
    private int maxSubzoneDepth = -1;

    private int maxValue = 9999;

    public ResidencePlayer(OfflinePlayer off) {
        if (off == null) {
            return;
        }
        this.uuid = off.getUniqueId();
        this.userName = off.getName();
        Residence.getInstance().addOfflinePlayerToChache(off);
        this.updatePlayer();
        this.RecalculatePermissions();
    }

    public ResidencePlayer(Player player) {
        if (player == null) {
            return;
        }
        Residence.getInstance().addOfflinePlayerToChache(player);
        this.updatePlayer(player);
        this.RecalculatePermissions();
    }

    public ResidencePlayer(String userName, UUID uuid) {
        this.userName = userName;
        this.uuid = uuid;
        if (this.isOnline()) {
            RecalculatePermissions();
        }
    }

    public ResidencePlayer(String userName) {
        this.userName = userName;
        if (this.isOnline()) {
            RecalculatePermissions();
        }
    }

    public boolean isOnline() {
        this.updatePlayer();
        if (this.player != null && this.player.isOnline()) {
            return true;
        }
        return false;
    }

    public ClaimedResidence getMainResidence() {
        if (this.mainResidence == null) {
            for (ClaimedResidence one : this.ResidenceList) {
                if (one == null) {
                    continue;
                }
                if (one.isMainResidence()) {
                    this.mainResidence = one;
                    return this.mainResidence;
                }
            }
            for (String one : Residence.getInstance().getRentManager().getRentedLands(this.userName)) {
                ClaimedResidence res = Residence.getInstance().getResidenceManager().getByName(one);
                if (res != null) {
                    this.mainResidence = res;
                    return this.mainResidence;
                }
            }
            for (ClaimedResidence one : this.ResidenceList) {
                if (one == null) {
                    continue;
                }
                this.mainResidence = one;
                return this.mainResidence;
            }
        }
        return this.mainResidence;
    }

    public void setMainResidence(ClaimedResidence res) {
        if (this.mainResidence != null) {
            this.mainResidence.setMainResidence(false);
        }
        this.mainResidence = res;
    }

    public void RecalculatePermissions() {
        getGroup();
        recountMaxRes();
        recountMaxRents();
        recountMaxSubzones();
    }

    public void recountMaxRes() {
        if (this.getGroup() != null) {
            this.maxRes = this.getGroup().getMaxZones();
        }
        this.maxRes = this.maxRes == -1 ? this.maxValue : this.maxRes;

        if (this.player != null && this.player.isOnline()) {
            if (this.player.isPermissionSet("residence.max.res.unlimited")) {
                this.maxRes = this.maxValue;
                return;
            }
        } else if (this.ofPlayer != null) {
            if (ResidenceVaultAdapter
                .hasPermission(this.ofPlayer, "residence.max.res.unlimited", Residence.getInstance().getConfigManager().getDefaultWorld())) {
                this.maxRes = this.maxValue;
                return;
            }
        }

        for (int i = 1; i <= Residence.getInstance().getConfigManager().getMaxResCount(); i++) {
            if (this.player != null && this.player.isOnline()) {
                if (this.player.isPermissionSet("residence.max.res." + i)) {
                    this.maxRes = i;
                }
            } else if (this.ofPlayer != null) {
                if (ResidenceVaultAdapter
                    .hasPermission(this.ofPlayer, "residence.max.res." + i, Residence.getInstance().getConfigManager().getDefaultWorld())) {
                    this.maxRes = i;
                }
            }
        }
    }

    public int getMaxRents() {
        recountMaxRents();
        return this.maxRents;
    }

    public void recountMaxRents() {
        if (this.player != null) {
            if (this.player.isPermissionSet("residence.max.rents.unlimited")) {
                this.maxRents = this.maxValue;
                return;
            }
        } else {
            if (this.ofPlayer != null) {
                if (ResidenceVaultAdapter
                    .hasPermission(this.ofPlayer, "residence.max.rents.unlimited", Residence.getInstance().getConfigManager().getDefaultWorld())) {
                    this.maxRents = this.maxValue;
                    return;
                }
            }
        }
        for (int i = 1; i <= Residence.getInstance().getConfigManager().getMaxRentCount(); i++) {
            if (this.player != null) {
                if (this.player.isPermissionSet("residence.max.rents.unlimited" + i)) {
                    this.maxRents = i;
                }
            } else {
                if (this.ofPlayer != null) {
                    if (ResidenceVaultAdapter
                        .hasPermission(this.ofPlayer, "residence.max.rents." + i, Residence.getInstance().getConfigManager().getDefaultWorld())) {
                        this.maxRents = i;
                    }
                }
            }
        }

        int m = this.getGroup().getMaxRents();
        m = m == -1 ? this.maxValue : m;
        if (this.maxRents < m) {
            this.maxRents = m;
        }
    }

    public PermissionGroup getGroup() {
        updatePlayer();
        return getGroup(this.player != null ? this.player.getWorld().getName() : Residence.getInstance().getConfigManager().getDefaultWorld());
    }

    private void updatePlayer() {
        this.player = Bukkit.getPlayer(this.uuid);
        if (this.player != null) {
            updatePlayer(this.player);
        }
        if (this.player != null && this.player.isOnline()) {
            return;
        }
        if (this.uuid != null && Bukkit.getPlayer(this.uuid) != null) {
            this.player = Bukkit.getPlayer(this.uuid);
            this.userName = this.player.getName();
            return;
        }

        if (this.userName != null) {
            this.player = Bukkit.getPlayer(this.userName);
        }
        if (this.player != null) {
            this.userName = this.player.getName();
            this.uuid = this.player.getUniqueId();
            this.ofPlayer = this.player;
            return;
        }
        if (this.player == null && this.ofPlayer == null) {
            this.ofPlayer = Residence.getInstance().getOfflinePlayer(this.userName);
        }
        if (this.ofPlayer != null) {
            this.userName = this.ofPlayer.getName();
            this.uuid = this.ofPlayer.getUniqueId();
            return;
        }
    }

    public PermissionGroup getGroup(String world) {
        if (this.groups == null) {
            this.groups = new PlayerGroup(this);
        }
        this.groups.updateGroup(world, false);
        PermissionGroup group = this.groups.getGroup(world);
        if (group == null) {
            group = Residence.getInstance().getPermissionManager().getDefaultGroup();
        }
        return group;
    }

    public ResidencePlayer updatePlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.userName = player.getName();
        this.ofPlayer = player;
        return this;
    }

    public int getMaxSubzones() {
        recountMaxSubzones();
        return this.maxSubzones;
    }

    public void recountMaxSubzones() {
        if (this.player != null) {
            if (this.player.isPermissionSet("residence.max.subzones.unlimited")) {
                this.maxSubzones = this.maxValue;
                return;
            }
        } else {
            if (this.ofPlayer != null) {
                if (ResidenceVaultAdapter
                    .hasPermission(this.ofPlayer, "residence.max.subzones.unlimited", Residence.getInstance().getConfigManager().getDefaultWorld())) {
                    this.maxSubzones = this.maxValue;
                    return;
                }
            }
        }
        for (int i = 1; i <= Residence.getInstance().getConfigManager().getMaxSubzonesCount(); i++) {
            if (this.player != null) {
                if (this.player.isPermissionSet("residence.max.subzones." + i)) {
                    this.maxSubzones = i;
                }
            } else {
                if (this.ofPlayer != null) {
                    if (ResidenceVaultAdapter
                        .hasPermission(this.ofPlayer, "residence.max.subzones." + i, Residence.getInstance().getConfigManager().getDefaultWorld())) {
                        this.maxSubzones = i;
                    }
                }
            }
        }

        int m = this.getGroup().getMaxSubzones();
        m = m == -1 ? this.maxValue : m;
        if (this.maxSubzones < m) {
            this.maxSubzones = m;
        }
    }

    public int getMaxSubzoneDepth() {
        recountMaxSubzoneDepth();
        return this.maxSubzoneDepth;
    }

    public void recountMaxSubzoneDepth() {
        if (this.player != null) {
            if (this.player.isPermissionSet("residence.max.subzonedepth.unlimited")) {
                this.maxSubzoneDepth = this.maxValue;
                return;
            }
        } else {
            if (this.ofPlayer != null) {
                if (ResidenceVaultAdapter.hasPermission(this.ofPlayer, "residence.max.subzonedepth.unlimited",
                    Residence.getInstance().getConfigManager().getDefaultWorld())) {
                    this.maxSubzoneDepth = this.maxValue;
                    return;
                }
            }
        }
        for (int i = 1; i <= Residence.getInstance().getConfigManager().getMaxSubzoneDepthCount(); i++) {
            if (this.player != null) {
                if (this.player.isPermissionSet("residence.max.subzonedepth." + i)) {
                    this.maxSubzoneDepth = i;
                }
            } else {
                if (this.ofPlayer != null) {
                    if (ResidenceVaultAdapter.hasPermission(this.ofPlayer, "residence.max.subzonedepth." + i,
                        Residence.getInstance().getConfigManager().getDefaultWorld())) {
                        this.maxSubzoneDepth = i;
                    }
                }
            }
        }

        int m = this.getGroup().getMaxSubzoneDepth();
        m = m == -1 ? this.maxValue : m;
        if (this.maxSubzoneDepth < m) {
            this.maxSubzoneDepth = m;
        }
    }

    public int getMaxRes() {
        recountMaxRes();
        PermissionGroup g = getGroup();
        if (this.maxRes < g.getMaxZones()) {
            return g.getMaxZones();
        }
        return this.maxRes;
    }

    public void addResidence(ClaimedResidence residence) {
        if (residence == null) {
            return;
        }
        // Exclude subzones
        if (residence.isSubzone()) {
            return;
        }
        residence.getPermissions().setOwnerUUID(this.uuid);
        if (this.userName != null) {
            residence.getPermissions().setOwnerLastKnownName(this.userName);
        }
        this.ResidenceList.add(residence);
    }

    public void removeResidence(ClaimedResidence residence) {
        if (residence == null) {
            return;
        }
        boolean rem = this.ResidenceList.remove(residence);
        // in case its fails to remove, double check by name
        if (rem == false) {
            Iterator<ClaimedResidence> iter = this.ResidenceList.iterator();
            while (iter.hasNext()) {
                ClaimedResidence one = iter.next();
                if (one.getName().equalsIgnoreCase(residence.getName())) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    public int getResAmount() {
        int i = 0;
        for (ClaimedResidence one : this.ResidenceList) {
            if (one.isSubzone()) {
                continue;
            }
            i++;
        }
        return i;
    }

    public List<ClaimedResidence> getResList() {
        List<ClaimedResidence> ls = new ArrayList<ClaimedResidence>();
        ls.addAll(this.ResidenceList);
        return ls;
    }

    public String getPlayerName() {
        this.updatePlayer();
        return this.userName;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Player getPlayer() {
        this.updatePlayer();
        return this.player;
    }
}
