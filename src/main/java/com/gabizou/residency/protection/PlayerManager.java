package com.gabizou.residency.protection;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.ResidencePlayer;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.permissions.PermissionGroup;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager implements ResidencePlayerInterface {

    private ConcurrentHashMap<String, ResidencePlayer> players = new ConcurrentHashMap<String, ResidencePlayer>();
    private ConcurrentHashMap<UUID, ResidencePlayer> playersUuid = new ConcurrentHashMap<UUID, ResidencePlayer>();
    private Residence plugin;

    public PlayerManager(Residence plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(ResidencePlayer resPlayer) {
        if (resPlayer == null) {
            return;
        }
        addPlayer(resPlayer.getPlayerName(), resPlayer.getUuid(), resPlayer);
    }

    public void addPlayer(Player player, ResidencePlayer resPlayer) {
        if (player == null) {
            return;
        }
        addPlayer(player.getName(), player.getUniqueId(), resPlayer);
    }

    public void addPlayer(String name, UUID uuid, ResidencePlayer resPlayer) {
        if (name != null) {
            this.players.put(name.toLowerCase(), resPlayer);
        }
        if (uuid != null) {
            this.playersUuid.put(uuid, resPlayer);
        }
    }

    public ResidencePlayer playerJoin(Player player) {
        return playerJoin(player, true);
    }

    public ResidencePlayer playerJoin(Player player, boolean recalculate) {
        ResidencePlayer resPlayer = this.playersUuid.get(player.getUniqueId());
        if (resPlayer == null) {
            resPlayer = new ResidencePlayer(player);
            addPlayer(resPlayer);
        } else {
            resPlayer.updatePlayer(player);
            if (recalculate) {
                resPlayer.RecalculatePermissions();
            }
        }
        return resPlayer;
    }

    public ResidencePlayer playerJoin(UUID uuid) {
        ResidencePlayer resPlayer = this.playersUuid.get(uuid);
        if (resPlayer != null) {
            resPlayer.RecalculatePermissions();
        } else {
            User off = Sponge.getOfflinePlayer(uuid);
            if (off != null) {
                resPlayer = new ResidencePlayer(off);
                addPlayer(resPlayer);
            }
        }
        return resPlayer;
    }

    public ResidencePlayer playerJoin(String player) {
        if (!this.players.containsKey(player.toLowerCase())) {
            ResidencePlayer resPlayer = new ResidencePlayer(player);
            addPlayer(resPlayer);
            return resPlayer;
        }
        return null;
    }

    public ResidencePlayer playerJoin(String player, UUID uuid) {
        if (!this.players.containsKey(player.toLowerCase())) {
            ResidencePlayer resPlayer = new ResidencePlayer(player, uuid);
            addPlayer(resPlayer);
            return resPlayer;
        }
        return null;
    }

    @Override
    public ArrayList<String> getResidenceList(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            return getResidenceList(player.getUniqueId());
        }
        ArrayList<String> temp = new ArrayList<String>();
        ResidencePlayer resPlayer = this.getResidencePlayer(name.toLowerCase());
        if (resPlayer != null) {
            for (ClaimedResidence one : resPlayer.getResList()) {
                temp.add(one.getName());
            }
            return temp;
        }
        return temp;
    }

    @Override
    public ArrayList<String> getResidenceList(String player, boolean showhidden) {
        return getResidenceList(player, showhidden, false);
    }

    @Override
    public PermissionGroup getGroup(String player) {
        ResidencePlayer resPlayer = getResidencePlayer(player);
        if (resPlayer != null) {
            return resPlayer.getGroup();
        }
        return null;
    }

    @Override
    public int getMaxResidences(String player) {
        ResidencePlayer resPlayer = getResidencePlayer(player);
        if (resPlayer != null) {
            return resPlayer.getMaxRes();
        }
        return -1;
    }

    @Override
    public int getMaxSubzones(String player) {
        ResidencePlayer resPlayer = getResidencePlayer(player);
        if (resPlayer != null) {
            return resPlayer.getMaxSubzones();
        }
        return -1;
    }

    @Override
    public int getMaxRents(String player) {
        ResidencePlayer resPlayer = getResidencePlayer(player);
        if (resPlayer != null) {
            return resPlayer.getMaxRents();
        }
        return -1;
    }

    @Override
    public ResidencePlayer getResidencePlayer(String player) {
        return getResidencePlayer(player, false);
    }

    @Override
    public int getMaxSubzoneDepth(String player) {
        ResidencePlayer resPlayer = getResidencePlayer(player);
        if (resPlayer != null) {
            return resPlayer.getMaxSubzoneDepth();
        }
        return -1;
    }

    @Override
    public ArrayList<String> getResidenceList(UUID uuid) {
        ArrayList<String> temp = new ArrayList<String>();
//	playerJoin(player, false);
        ResidencePlayer resPlayer = this.playersUuid.get(uuid);
        if (resPlayer != null) {
            for (ClaimedResidence one : resPlayer.getResList()) {
                temp.add(one.getName());
            }
            return temp;
        }
        return temp;
    }

    public ArrayList<String> getResidenceList(String player, boolean showhidden, boolean onlyHidden) {
        ArrayList<String> temp = new ArrayList<String>();
//	playerJoin(player, false);
        ResidencePlayer resPlayer = this.getResidencePlayer(player.toLowerCase());
        if (resPlayer == null) {
            return temp;
        }
        for (ClaimedResidence one : resPlayer.getResList()) {
            boolean hidden = one.getPermissions().has("hidden", false);
            if (!showhidden && hidden) {
                continue;
            }

            if (onlyHidden && !hidden) {
                continue;
            }

            temp.add(this.plugin.msg(lm.Residence_List, "", one.getName(), one.getWorld()) +
                     (hidden ? this.plugin.msg(lm.Residence_Hidden) : ""));
        }
        Collections.sort(temp, String.CASE_INSENSITIVE_ORDER);
        return temp;
    }

    public ArrayList<ClaimedResidence> getResidences(String player, boolean showhidden) {
        return getResidences(player, showhidden, false);
    }

    public ArrayList<ClaimedResidence> getResidences(String player, boolean showhidden, boolean onlyHidden) {
        return getResidences(player, showhidden, onlyHidden, null);
    }

    public ArrayList<ClaimedResidence> getResidences(String player, boolean showhidden, boolean onlyHidden, World world) {
        ArrayList<ClaimedResidence> temp = new ArrayList<ClaimedResidence>();
        ResidencePlayer resPlayer = this.getResidencePlayer(player.toLowerCase());
        if (resPlayer == null) {
            return temp;
        }
        for (ClaimedResidence one : resPlayer.getResList()) {
            boolean hidden = one.getPermissions().has("hidden", false);
            if (!showhidden && hidden) {
                continue;
            }
            if (onlyHidden && !hidden) {
                continue;
            }
            if (world != null && !world.getName().equalsIgnoreCase(one.getWorld())) {
                continue;
            }
            temp.add(one);
        }
        return temp;
    }

    public TreeMap<String, ClaimedResidence> getResidencesMap(String player, boolean showhidden, boolean onlyHidden, World world) {
        TreeMap<String, ClaimedResidence> temp = new TreeMap<String, ClaimedResidence>();

        ResidencePlayer resPlayer = this.getResidencePlayer(player.toLowerCase());
        if (resPlayer == null) {
            return temp;
        }

        for (ClaimedResidence one : resPlayer.getResList()) {
            boolean hidden = one.getPermissions().has(Flags.hidden, false);
            if (!showhidden && hidden) {
                continue;
            }
            if (onlyHidden && !hidden) {
                continue;
            }
            if (world != null && !world.getName().equalsIgnoreCase(one.getWorld())) {
                continue;
            }
            temp.put(one.getName(), one);
        }
        return temp;
    }

    public ResidencePlayer getResidencePlayer(Player player) {
        ResidencePlayer resPlayer = null;
        if (player == null) {
            return null;
        }
        if (this.playersUuid.containsKey(player.getUniqueId())) {
            resPlayer = this.playersUuid.get(player.getUniqueId());
            resPlayer.updatePlayer(player);
            resPlayer.RecalculatePermissions();
        } else {
            resPlayer = playerJoin(player);
        }
        return resPlayer;
    }

    public ResidencePlayer getResidencePlayer(String player, boolean recalculate) {
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            return getResidencePlayer(p);
        }
        ResidencePlayer resPlayer = null;
        if (this.players.containsKey(player.toLowerCase())) {
            resPlayer = this.players.get(player.toLowerCase());
            if (recalculate || resPlayer.isOnline()) {
                resPlayer.RecalculatePermissions();
            }
        } else {
            resPlayer = playerJoin(player);
        }
        return resPlayer;
    }

    public ResidencePlayer getResidencePlayer(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            return getResidencePlayer(p);
        }
        ResidencePlayer resPlayer = null;
        if (this.playersUuid.containsKey(uuid)) {
            resPlayer = this.playersUuid.get(uuid);
        } else {
            resPlayer = playerJoin(uuid);
        }
        return resPlayer;
    }

    public ResidencePlayer getResidencePlayer(String name, UUID uuid) {
        if (uuid.toString().equals(this.plugin.getServerLandUUID())) {
            return null;
        }
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            return getResidencePlayer(p);
        }
        ResidencePlayer resPlayer = null;
        if (this.playersUuid.containsKey(uuid)) {
            resPlayer = this.playersUuid.get(uuid);
        } else if ((name != null) && (this.players.containsKey(name.toLowerCase()))) {
            resPlayer = this.players.get(name.toLowerCase());
        } else {
            resPlayer = playerJoin(name, uuid);
        }
        return resPlayer;
    }

    public void addResidence(UUID uuid, ClaimedResidence residence) {
        ResidencePlayer resPlayer = getResidencePlayer(uuid);
        if (resPlayer != null) {
            resPlayer.addResidence(residence);
        }
        return;
    }

    public void addResidence(Player player, ClaimedResidence residence) {
        addResidence(player.getUniqueId(), residence);
    }

    public void addResidence(String player, ClaimedResidence residence) {
        ResidencePlayer resPlayer = getResidencePlayer(player, residence.getOwnerUUID());
        if (resPlayer != null) {
            resPlayer.addResidence(residence);
        }
    }

    public void removeResFromPlayer(ClaimedResidence residence) {
        if (residence == null) {
            return;
        }
        removeResFromPlayer(residence.getOwnerUUID(), residence);
    }

    public void removeResFromPlayer(UUID uuid, ClaimedResidence residence) {
        ResidencePlayer resPlayer = this.playersUuid.get(uuid);
        if (resPlayer != null) {
            resPlayer.removeResidence(residence);
        }
    }

    public void removeResFromPlayer(OfflinePlayer player, ClaimedResidence residence) {
        removeResFromPlayer(player.getUniqueId(), residence);
    }

    public void removeResFromPlayer(Player player, ClaimedResidence residence) {
        removeResFromPlayer(player.getUniqueId(), residence);
    }

    public void removeResFromPlayer(String player, ClaimedResidence residence) {
        ResidencePlayer resPlayer = this.getResidencePlayer(player.toLowerCase());
        if (resPlayer != null) {
            resPlayer.removeResidence(residence);
        }
        return;
    }
}
