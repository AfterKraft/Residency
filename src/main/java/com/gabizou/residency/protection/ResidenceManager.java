package com.gabizou.residency.protection;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.MinimizeFlags;
import com.gabizou.residency.containers.MinimizeMessages;
import com.gabizou.residency.containers.ResidencePlayer;
import com.gabizou.residency.containers.Visualizer;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.economy.rent.RentableLand;
import com.gabizou.residency.economy.rent.RentedLand;
import com.gabizou.residency.event.ResidenceCreationEvent;
import com.gabizou.residency.event.ResidenceDeleteEvent;
import com.gabizou.residency.event.ResidenceDeleteEvent.DeleteCause;
import com.gabizou.residency.event.ResidenceRenameEvent;
import com.gabizou.residency.permissions.PermissionGroup;
import com.gabizou.residency.protection.FlagPermissions.FlagCombo;
import com.gabizou.residency.utils.GetTime;
import com.gabizou.residency.utils.RawMessage;
import com.griefcraft.cache.ProtectionCache;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResidenceManager implements ResidenceInterface {

    protected SortedMap<String, ClaimedResidence> residences;
    protected Map<String, Map<ChunkRef, List<ClaimedResidence>>> chunkResidences;
    protected List<ClaimedResidence> shops = new ArrayList<ClaimedResidence>();
    // Optimizing save file
    HashMap<String, List<MinimizeMessages>> optimizeMessages = new HashMap<String, List<MinimizeMessages>>();
    HashMap<String, List<MinimizeFlags>> optimizeFlags = new HashMap<String, List<MinimizeFlags>>();
    HashMap<String, HashMap<Integer, MinimizeMessages>> cacheMessages = new HashMap<String, HashMap<Integer, MinimizeMessages>>();
    HashMap<String, HashMap<Integer, MinimizeFlags>> cacheFlags = new HashMap<String, HashMap<Integer, MinimizeFlags>>();
    private Residence plugin;

    public ResidenceManager(Residence plugin) {
        this.residences = new TreeMap<String, ClaimedResidence>();
        this.chunkResidences = new HashMap<String, Map<ChunkRef, List<ClaimedResidence>>>();
        this.shops = new ArrayList<ClaimedResidence>();
        this.plugin = plugin;
    }

    public boolean isOwnerOfLocation(Player player, Location<?> loc) {
        ClaimedResidence res = getByLoc(loc);
        if (res != null && res.isOwner(player)) {
            return true;
        }
        return false;
    }

    @Override
    public ClaimedResidence getByLoc(Location<World> loc) {
        if (loc == null) {
            return null;
        }
        World world = loc.getExtent();
        if (world == null) {
            return null;
        }
        String worldName = world.getName();
        if (worldName == null) {
            return null;
        }
        ClaimedResidence res = null;
        ChunkRef chunk = new ChunkRef(loc);
        if (!this.chunkResidences.containsKey(worldName)) {
            return null;
        }

        Map<ChunkRef, List<ClaimedResidence>> ChunkMap = this.chunkResidences.get(worldName);

        if (ChunkMap.containsKey(chunk)) {
            for (ClaimedResidence entry : ChunkMap.get(chunk)) {
                if (entry == null) {
                    continue;
                }
                if (entry.containsLoc(loc)) {
                    res = entry;
                    break;
                }
            }
        }

        if (res == null) {
            return null;
        }

        ClaimedResidence subres = res.getSubzoneByLoc(loc);
        if (subres == null) {
            return res;
        }
        return subres;
    }

    @Override
    public ClaimedResidence getByName(String name) {
        if (name == null) {
            return null;
        }
        String[] split = name.split("\\.");
        if (split.length == 1) {
            return this.residences.get(name.toLowerCase());
        }
        ClaimedResidence res = this.residences.get(split[0].toLowerCase());
        for (int i = 1; i < split.length; i++) {
            if (res != null) {
                res = res.getSubzone(split[i].toLowerCase());
            } else {
                return null;
            }
        }
        return res;
    }

    @Override
    public String getSubzoneNameByRes(ClaimedResidence res) {
        Set<Entry<String, ClaimedResidence>> set = this.residences.entrySet();
        for (Entry<String, ClaimedResidence> check : set) {
            if (check.getValue() == res) {
                return check.getKey();
            }
            String n = check.getValue().getSubzoneNameByRes(res);
            if (n != null) {
                return n;
            }
        }
        return null;
    }

    @Override
    public void addShop(ClaimedResidence res) {
        this.shops.add(res);
    }

    @Override
    public void addShop(String resName) {
        ClaimedResidence res = getByName(resName);
        if (res != null) {
            this.shops.add(res);
        }
    }

    @Override
    public void removeShop(ClaimedResidence res) {
        this.shops.remove(res);
    }

    @Override
    public void removeShop(String resName) {
        for (ClaimedResidence one : this.shops) {
            if (one.getName().equalsIgnoreCase(resName)) {
                this.shops.remove(one);
                break;
            }
        }
    }

    @Override
    public List<ClaimedResidence> getShops() {
        return this.shops;
    }

    @Override
    public boolean addResidence(String name, Location loc1, Location loc2) {
        return this.addResidence(name, this.plugin.getServerLandname(), loc1, loc2);
    }

    @Override
    public boolean addResidence(String name, String owner, Location loc1, Location loc2) {
        return this.addResidence(null, owner, name, loc1, loc2, true);
    }

    @Override
    public boolean addResidence(Player player, String name, Location loc1, Location loc2, boolean resadmin) {
        return this.addResidence(player, player.getName(), name, loc1, loc2, resadmin);
    }

    public boolean addResidence(Player player, String owner, String name, Location loc1, Location loc2, boolean resadmin) {
        if (!this.plugin.validName(name)) {
            this.plugin.msg(player, lm.Invalid_NameCharacters);
            return false;
        }
        if (loc1 == null || loc2 == null || !loc1.getWorld().getName().equals(loc2.getWorld().getName())) {
            this.plugin.msg(player, lm.Select_Points);
            return false;
        }

        ResidencePlayer rPlayer = this.plugin.getPlayerManager().getResidencePlayer(player);

        PermissionGroup group = rPlayer.getGroup();
//	PermissionGroup group = plugin.getPermissionManager().getGroup(owner, loc1.getWorld().getName());
        if (!resadmin && !group.canCreateResidences() && !this.plugin.hasPermission(player, "residence.create", false)) {
            this.plugin.msg(player, lm.General_NoPermission);
            return false;
        }

        if (!resadmin && !this.plugin.hasPermission(player, "residence.create")) {
            return false;
        }

        if (rPlayer.getResAmount() >= rPlayer.getMaxRes() && !resadmin) {
            this.plugin.msg(player, lm.Residence_TooMany);
            return false;
        }

        CuboidArea newArea = new CuboidArea(loc1, loc2);
        ClaimedResidence newRes = new ClaimedResidence(owner, loc1.getWorld().getName(), this.plugin);
        newRes.getPermissions().applyDefaultFlags();
        newRes.setEnterMessage(group.getDefaultEnterMessage());
        newRes.setLeaveMessage(group.getDefaultLeaveMessage());
        newRes.setName(name);
        newRes.setCreateTime();

        if (this.residences.containsKey(name.toLowerCase())) {
            this.plugin.msg(player, lm.Residence_AlreadyExists, this.residences.get(name.toLowerCase()).getResidenceName());
            return false;
        }

        newRes.BlockSellPrice = group.getSellPerBlock();

        if (!newRes.addArea(player, newArea, "main", resadmin, false)) {
            return false;
        }

        ResidenceCreationEvent resevent = new ResidenceCreationEvent(player, name, newRes, newArea);
        this.plugin.getServ().getPluginManager().callEvent(resevent);
        if (resevent.isCancelled()) {
            return false;
        }

        if (!newRes.isSubzone() && this.plugin.getConfigManager().enableEconomy() && !resadmin) {
            double chargeamount = Math.ceil(newArea.getSize() * group.getCostPerBlock());
            if (!this.plugin.getTransactionManager().chargeEconomyMoney(player, chargeamount)) {
                // Need to remove area if we can't create residence
                newRes.removeArea("main");
                return false;
            }
        }

        this.residences.put(name.toLowerCase(), newRes);

        calculateChunks(name);
        this.plugin.getLeaseManager().removeExpireTime(newRes);
        this.plugin.getPlayerManager().addResidence(newRes.getOwner(), newRes);

        if (player != null) {
            Visualizer v = new Visualizer(player);
            v.setAreas(newArea);
            this.plugin.getSelectionManager().showBounds(player, v);
            this.plugin.getAutoSelectionManager().getList().remove(player.getName().toLowerCase());
            this.plugin.msg(player, lm.Area_Create, "main");
            this.plugin.msg(player, lm.Residence_Create, name);
        }
        if (this.plugin.getConfigManager().useLeases()) {
            this.plugin.getLeaseManager().setExpireTime(player, newRes, group.getLeaseGiveTime());
        }
        return true;

    }

    public void calculateChunks(String name) {
        if (name == null) {
            return;
        }
        name = name.toLowerCase();
        ClaimedResidence res = this.residences.get(name);
        if (res == null) {
            return;
        }
        String world = res.getWorld();
        if (this.chunkResidences.get(world) == null) {
            this.chunkResidences.put(world, new HashMap<ChunkRef, List<ClaimedResidence>>());
        }
        for (ChunkRef chunk : getChunks(res)) {
            List<ClaimedResidence> ress = new ArrayList<>();
            if (this.chunkResidences.get(world).containsKey(chunk)) {
                ress.addAll(this.chunkResidences.get(world).get(chunk));
            }
            ress.add(res);
            this.chunkResidences.get(world).put(chunk, ress);
        }
    }

    private static List<ChunkRef> getChunks(ClaimedResidence res) {
        List<ChunkRef> chunks = new ArrayList<>();
        for (CuboidArea area : res.getAreaArray()) {
            chunks.addAll(area.getChunks());
        }
        return chunks;
    }

    public void listResidences(CommandSender sender) {
        this.listResidences(sender, sender.getName(), 1);
    }

    public void listResidences(CommandSender sender, boolean resadmin) {
        this.listResidences(sender, sender.getName(), 1, false, false, resadmin);
    }

    public void listResidences(CommandSender sender, String targetplayer, int page, boolean showhidden, boolean onlyHidden, boolean resadmin) {
        this.listResidences(sender, targetplayer, page, showhidden, onlyHidden, resadmin, null);
    }

    public void listResidences(CommandSender sender, String targetplayer, int page, boolean showhidden, boolean onlyHidden, boolean resadmin,
        World world) {
        if (targetplayer == null) {
            targetplayer = sender.getName();
        }
        if (showhidden && !this.plugin.isResAdminOn(sender) && !sender.getName().equalsIgnoreCase(targetplayer)) {
            showhidden = false;
        } else if (sender.getName().equalsIgnoreCase(targetplayer)) {
            showhidden = true;
        }
        boolean hidden = showhidden;
        TreeMap<String, ClaimedResidence> ownedResidences = this.plugin.getPlayerManager().getResidencesMap(targetplayer, hidden, onlyHidden, world);
        ownedResidences.putAll(this.plugin.getRentManager().getRentsMap(targetplayer, onlyHidden, world));
        this.plugin.getInfoPageManager().printListInfo(sender, targetplayer, ownedResidences, page, resadmin);
    }

    public void listResidences(CommandSender sender, String targetplayer, boolean showhidden) {
        this.listResidences(sender, targetplayer, 1, showhidden, false, showhidden);
    }

    public void listResidences(CommandSender sender, String targetplayer, int page) {
        this.listResidences(sender, targetplayer, page, false, false, false);
    }

    public void listResidences(CommandSender sender, int page, boolean showhidden) {
        this.listResidences(sender, sender.getName(), page, showhidden, false, showhidden);
    }

    public void listResidences(CommandSender sender, int page, boolean showhidden, boolean onlyHidden) {
        this.listResidences(sender, sender.getName(), page, showhidden, onlyHidden, showhidden);
    }

    public void listResidences(CommandSender sender, String string, int page, boolean showhidden) {
        this.listResidences(sender, string, page, showhidden, false, showhidden);
    }

    public void listAllResidences(CommandSender sender, int page) {
        this.listAllResidences(sender, page, false);
    }

    public void listAllResidences(CommandSender sender, int page, boolean showhidden) {
        this.listAllResidences(sender, page, showhidden, false);
    }

    public void listAllResidences(CommandSender sender, int page, boolean showhidden, boolean onlyHidden) {
        TreeMap<String, ClaimedResidence> list = getFromAllResidencesMap(showhidden, onlyHidden, null);
        this.plugin.getInfoPageManager().printListInfo(sender, null, list, page, showhidden);
    }

    public TreeMap<String, ClaimedResidence> getFromAllResidencesMap(boolean showhidden, boolean onlyHidden, World world) {
        TreeMap<String, ClaimedResidence> list = new TreeMap<String, ClaimedResidence>();
        for (Entry<String, ClaimedResidence> res : this.residences.entrySet()) {
            boolean hidden = res.getValue().getPermissions().has("hidden", false);
            if (onlyHidden && !hidden) {
                continue;
            }
            if (world != null && !world.getName().equalsIgnoreCase(res.getValue().getWorld())) {
                continue;
            }
            if ((showhidden) || (!showhidden && !hidden)) {
                list.put(res.getKey(), res.getValue());
            }
        }
        return list;
    }

    public void listAllResidences(CommandSender sender, int page, boolean showhidden, World world) {
        TreeMap<String, ClaimedResidence> list = getFromAllResidencesMap(showhidden, false, world);
        this.plugin.getInfoPageManager().printListInfo(sender, null, list, page, showhidden);
    }

    public String[] getResidenceList() {
        return this.getResidenceList(true, true).toArray(new String[0]);
    }

    public Map<String, ClaimedResidence> getResidenceMapList(String targetplayer, boolean showhidden) {
        Map<String, ClaimedResidence> temp = new HashMap<String, ClaimedResidence>();
        for (Entry<String, ClaimedResidence> res : this.residences.entrySet()) {
            if (res.getValue().isOwner(targetplayer)) {
                boolean hidden = res.getValue().getPermissions().has("hidden", false);
                if ((showhidden) || (!showhidden && !hidden)) {
                    temp.put(res.getValue().getName().toLowerCase(), res.getValue());
                }
            }
        }
        return temp;
    }

    public ArrayList<String> getResidenceList(boolean showhidden, boolean showsubzones) {
        return this.getResidenceList(null, showhidden, showsubzones, false);
    }

    public ArrayList<String> getResidenceList(String targetplayer, boolean showhidden, boolean showsubzones) {
        return this.getResidenceList(targetplayer, showhidden, showsubzones, false, false);
    }

    public ArrayList<String> getResidenceList(String targetplayer, boolean showhidden, boolean showsubzones, boolean onlyHidden) {
        return this.getResidenceList(targetplayer, showhidden, showsubzones, false, onlyHidden);
    }

    public ArrayList<String> getResidenceList(String targetplayer, boolean showhidden, boolean showsubzones, boolean formattedOutput,
        boolean onlyHidden) {
        ArrayList<String> list = new ArrayList<>();
        for (Entry<String, ClaimedResidence> res : this.residences.entrySet()) {
            this.getResidenceList(targetplayer, showhidden, showsubzones, "", res.getKey(), res.getValue(), list, formattedOutput, onlyHidden);
        }
        return list;
    }

    public ArrayList<ClaimedResidence> getFromAllResidences(boolean showhidden, boolean onlyHidden, World world) {
        ArrayList<ClaimedResidence> list = new ArrayList<>();
        for (Entry<String, ClaimedResidence> res : this.residences.entrySet()) {
            boolean hidden = res.getValue().getPermissions().has("hidden", false);
            if (onlyHidden && !hidden) {
                continue;
            }
            if (world != null && !world.getName().equalsIgnoreCase(res.getValue().getWorld())) {
                continue;
            }
            if ((showhidden) || (!showhidden && !hidden)) {
                list.add(res.getValue());
            }
        }
        return list;
    }

    private void getResidenceList(String targetplayer, boolean showhidden, boolean showsubzones, String parentzone, String resname,
        ClaimedResidence res,
        ArrayList<String> list, boolean formattedOutput, boolean onlyHidden) {
        boolean hidden = res.getPermissions().has("hidden", false);

        if (onlyHidden && !hidden) {
            return;
        }

        if ((showhidden) || (!showhidden && !hidden)) {
            if (targetplayer == null || res.getPermissions().getOwner().equals(targetplayer)) {
                if (formattedOutput) {
                    list.add(this.plugin.msg(lm.Residence_List, parentzone, resname, res.getWorld()) +
                             (hidden ? this.plugin.msg(lm.Residence_Hidden) : ""));
                } else {
                    list.add(parentzone + resname);
                }
            }
            if (showsubzones) {
                for (Entry<String, ClaimedResidence> sz : res.subzones.entrySet()) {
                    this.getResidenceList(targetplayer, showhidden, showsubzones, parentzone + resname + ".", sz.getKey(), sz.getValue(), list,
                        formattedOutput,
                        onlyHidden);
                }
            }
        }
    }

    public String checkAreaCollision(CuboidArea newarea, ClaimedResidence parentResidence) {
        Set<Entry<String, ClaimedResidence>> set = this.residences.entrySet();
        for (Entry<String, ClaimedResidence> entry : set) {
            ClaimedResidence check = entry.getValue();
            if (check != parentResidence && check.checkCollision(newarea)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public ClaimedResidence collidesWithResidence(CuboidArea newarea) {
        Set<Entry<String, ClaimedResidence>> set = this.residences.entrySet();
        for (Entry<String, ClaimedResidence> entry : set) {
            ClaimedResidence check = entry.getValue();
            if (check.checkCollision(newarea)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void removeResidence(ClaimedResidence res) {
        this.removeResidence(null, res.getName(), true);
    }

    public void removeResidence(String name) {
        this.removeResidence(null, name, true);
    }

    public void removeResidence(CommandSender sender, String name, boolean resadmin) {
        if (sender instanceof Player) {
            removeResidence((Player) sender, name, resadmin);
        } else {
            removeResidence(null, name, true);
        }
    }

    @SuppressWarnings("deprecation")
    public void removeResidence(Player player, String name, boolean resadmin) {

        ClaimedResidence res = this.getByName(name);
        if (res == null) {
            this.plugin.msg(player, lm.Invalid_Residence);
            return;
        }

        name = res.getName();

        if (this.plugin.getConfigManager().isRentPreventRemoval() && !resadmin) {
            ClaimedResidence rented = res.getRentedSubzone();
            if (rented != null) {
                this.plugin.msg(player, lm.Residence_CantRemove, res.getName(), rented.getName(), rented.getRentedLand().player);
                return;
            }
        }

        if (player != null && !resadmin) {
            if (!res.getPermissions().hasResidencePermission(player, true) && !resadmin && res.getParent() != null && !res.getParent()
                .isOwner(player)) {
                this.plugin.msg(player, lm.General_NoPermission);
                return;
            }
        }

        ResidenceDeleteEvent resevent = new ResidenceDeleteEvent(player, res, player == null ? DeleteCause.OTHER : DeleteCause.PLAYER_DELETE);
        this.plugin.getServ().getPluginManager().callEvent(resevent);
        if (resevent.isCancelled()) {
            return;
        }

        ClaimedResidence parent = res.getParent();
        if (parent == null) {
            removeChunkList(name);

            this.residences.remove(name.toLowerCase());

            if (this.plugin.getConfigManager().isUseClean() && this.plugin.getConfigManager().getCleanWorlds().contains(res.getWorld())) {
                for (CuboidArea area : res.getAreaArray()) {

                    Location low = area.getLowLoc();
                    Location high = area.getHighLoc();

                    if (high.getBlockY() > this.plugin.getConfigManager().getCleanLevel()) {

                        if (low.getBlockY() < this.plugin.getConfigManager().getCleanLevel()) {
                            low.setY(this.plugin.getConfigManager().getCleanLevel());
                        }

                        World world = low.getWorld();

                        Location temploc = new Location(world, low.getBlockX(), low.getBlockY(), low.getBlockZ());

                        for (int x = low.getBlockX(); x <= high.getBlockX(); x++) {
                            temploc.setX(x);
                            for (int y = low.getBlockY(); y <= high.getBlockY(); y++) {
                                temploc.setY(y);
                                for (int z = low.getBlockZ(); z <= high.getBlockZ(); z++) {
                                    temploc.setZ(z);
                                    if (this.plugin.getConfigManager().getCleanBlocks().contains(temploc.getBlock().getTypeId())) {
                                        temploc.getBlock().setTypeId(0);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (this.plugin.getConfigManager().isRemoveLwcOnDelete()) {
                removeLwcFromResidence(player, res);
            }

            this.plugin.msg(player, lm.Residence_Remove, name);

        } else {
            String[] split = name.split("\\.");
            if (player != null) {
                parent.removeSubzone(player, split[split.length - 1], true);
            } else {
                parent.removeSubzone(split[split.length - 1]);
            }
        }

        this.plugin.getLeaseManager().removeExpireTime(res);

        for (ClaimedResidence oneSub : res.getSubzones()) {
            this.plugin.getPlayerManager().removeResFromPlayer(res.getOwnerUUID(), oneSub);
            this.plugin.getRentManager().removeRentable(name + "." + oneSub.getResidenceName());
            this.plugin.getTransactionManager().removeFromSale(name + "." + oneSub.getResidenceName());
        }

        this.plugin.getPlayerManager().removeResFromPlayer(res.getOwnerUUID(), res);
        this.plugin.getRentManager().removeRentable(name);
        this.plugin.getTransactionManager().removeFromSale(name);

        if (parent == null && this.plugin.getConfigManager().enableEconomy() && this.plugin.getConfigManager().useResMoneyBack()) {
            double chargeamount = Math.ceil(res.getTotalSize() * res.getBlockSellPrice());
            this.plugin.getTransactionManager().giveEconomyMoney(player, chargeamount);
        }
    }

    public void removeChunkList(String name) {
        if (name == null) {
            return;
        }
        name = name.toLowerCase();
        ClaimedResidence res = this.residences.get(name);
        if (res == null) {
            return;
        }
        String world = res.getWorld();
        if (this.chunkResidences.get(world) == null) {
            return;
        }
        for (ChunkRef chunk : getChunks(res)) {
            List<ClaimedResidence> ress = new ArrayList<>();
            if (this.chunkResidences.get(world).containsKey(chunk)) {
                ress.addAll(this.chunkResidences.get(world).get(chunk));
            }

            ress.remove(res);
            this.chunkResidences.get(world).put(chunk, ress);
        }

    }

    public void removeLwcFromResidence(final Player player, final ClaimedResidence res) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                LWC lwc = ResidenceManager.this.plugin.getLwc();
                if (lwc == null) {
                    return;
                }
                if (res == null) {
                    return;
                }
                int i = 0;

                ProtectionCache cache = lwc.getProtectionCache();

                List<Material> list = ResidenceManager.this.plugin.getConfigManager().getLwcMatList();

                try {
                    for (CuboidArea area : res.getAreaArray()) {
                        Location low = area.getLowLoc();
                        Location high = area.getHighLoc();
                        World world = low.getWorld();
                        for (int x = low.getBlockX(); x <= high.getBlockX(); x++) {
                            for (int y = low.getBlockY(); y <= high.getBlockY(); y++) {
                                for (int z = low.getBlockZ(); z <= high.getBlockZ(); z++) {
                                    Block b = world.getBlockAt(x, y, z);
                                    if (!list.contains(b.getType())) {
                                        continue;
                                    }
                                    Protection prot = cache.getProtection(b);
                                    if (prot == null) {
                                        continue;
                                    }
                                    prot.remove();
                                    i++;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
                if (i > 0) {
                    ResidenceManager.this.plugin.msg(player, lm.Residence_LwcRemoved, i, System.currentTimeMillis() - time);
                }
                return;
            }
        });
    }

    public void removeAllByOwner(String owner) {
        ArrayList<String> list = this.plugin.getPlayerManager().getResidenceList(owner);
        for (String oneRes : list) {
            removeResidence(null, oneRes, true);
        }
    }

    public int getOwnedZoneCount(String player) {
        ResidencePlayer rPlayer = this.plugin.getPlayerManager().getResidencePlayer(player);
        return rPlayer.getResAmount();
    }

    public boolean hasMaxZones(String player, int target) {
        return getOwnedZoneCount(player) < target;
    }

    public void printAreaInfo(String areaname, CommandSender sender) {
        printAreaInfo(areaname, sender, false);
    }

    public void printAreaInfo(String areaname, CommandSender sender, boolean resadmin) {
        ClaimedResidence res = this.getByName(areaname);
        if (res == null) {
            this.plugin.msg(sender, lm.Invalid_Residence);
            return;
        }

        areaname = res.getName();

        this.plugin.msg(sender, lm.General_Separator);

        ResidencePermissions perms = res.getPermissions();

        String resNameOwner = "&e" + this.plugin.msg(lm.Residence_Line, areaname);
        resNameOwner += this.plugin.msg(lm.General_Owner, perms.getOwner());
        if (this.plugin.getConfigManager().enableEconomy()) {
            if (res.isOwner(sender) || !(sender instanceof Player) || resadmin) {
                resNameOwner += this.plugin.msg(lm.Bank_Name, res.getBank().getStoredMoneyFormated());
            }
        }
        resNameOwner = ChatColor.translateAlternateColorCodes('&', resNameOwner);

        String worldInfo = this.plugin.msg(lm.General_World, perms.getWorld());

        if (res.getPermissions().has(Flags.hidden, FlagCombo.FalseOrNone) && res.getPermissions().has(Flags.coords, FlagCombo.TrueOrNone)
            || resadmin) {
            worldInfo += "&6 (&3";
            CuboidArea area = res.getAreaArray()[0];
            worldInfo +=
                this.plugin.msg(lm.General_CoordsTop, area.getHighLoc().getBlockX(), area.getHighLoc().getBlockY(), area.getHighLoc().getBlockZ());
            worldInfo += "&6; &3";
            worldInfo +=
                this.plugin.msg(lm.General_CoordsBottom, area.getLowLoc().getBlockX(), area.getLowLoc().getBlockY(), area.getLowLoc().getBlockZ());
            worldInfo += "&6)";
            worldInfo = ChatColor.translateAlternateColorCodes('&', worldInfo);
        }

        worldInfo += "\n" + this.plugin.msg(lm.General_CreatedOn, GetTime.getTime(res.createTime));

        String ResFlagList = perms.listFlags(5);
        if (!(sender instanceof Player)) {
            ResFlagList = perms.listFlags();
        }
        String ResFlagMsg = this.plugin.msg(lm.General_ResidenceFlags, ResFlagList);

        if (perms.getFlags().size() > 2 && sender instanceof Player) {
            ResFlagMsg = this.plugin.msg(lm.General_ResidenceFlags, perms.listFlags(5, 3)) + "...";
        }

        if (sender instanceof Player) {
            RawMessage rm = new RawMessage();
            rm.add(resNameOwner, worldInfo);
            rm.show(sender);

            rm.clear();

            rm.add(ResFlagMsg, ResFlagList);
            rm.show(sender);
        } else {
            this.plugin.msg(sender, resNameOwner);
            this.plugin.msg(sender, worldInfo);
            this.plugin.msg(sender, ResFlagMsg);
        }

        if (!this.plugin.getConfigManager().isShortInfoUse() || !(sender instanceof Player)) {
            sender.sendMessage(this.plugin.msg(lm.General_PlayersFlags, perms.listPlayersFlags()));
        } else if (this.plugin.getConfigManager().isShortInfoUse() || sender instanceof Player) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName() + " " + perms.listPlayersFlagsRaw(sender.getName(),
                this.plugin.msg(
                    lm.General_PlayersFlags, "")));
        }

        String groupFlags = perms.listGroupFlags();
        if (groupFlags.length() > 0) {
            this.plugin.msg(sender, lm.General_GroupFlags, groupFlags);
        }

        String msg = "";
        msg += this.plugin.msg(lm.General_TotalResSize, res.getTotalSize(), res.getXZSize());

        this.plugin.msg(sender, ChatColor.translateAlternateColorCodes('&', msg));

        if (this.plugin.getEconomyManager() != null) {
            this.plugin.msg(sender, lm.General_TotalWorth, (int) ((res.getTotalSize() * res.getOwnerGroup().getCostPerBlock())
                                                                  * 100) / 100.0,
                (int) ((res.getTotalSize() * res.getBlockSellPrice()) * 100) / 100.0);
        }

        if (res.getSubzonesAmount(false) > 0) {
            this.plugin.msg(sender, lm.General_TotalSubzones, res.getSubzonesAmount(false), res.getSubzonesAmount(true));
        }

        if (this.plugin.getConfigManager().useLeases() && this.plugin.getLeaseManager().isLeased(res)) {
            String time = this.plugin.getLeaseManager().getExpireTime(res);
            if (time != null) {
                this.plugin.msg(sender, lm.Economy_LeaseExpire, time);
            }
        }

        if (this.plugin.getConfigManager().enabledRentSystem() && this.plugin.getRentManager().isForRent(areaname) && !this.plugin.getRentManager()
            .isRented(areaname)) {
            String forRentMsg = this.plugin.msg(lm.Rent_isForRent);

            RentableLand rentable = this.plugin.getRentManager().getRentableLand(areaname);
            StringBuilder rentableString = new StringBuilder();
            if (rentable != null) {
                rentableString.append(this.plugin.msg(lm.General_Cost, rentable.cost, rentable.days) + "\n");
                rentableString.append(this.plugin.msg(lm.Rentable_AllowRenewing, rentable.AllowRenewing) + "\n");
                rentableString.append(this.plugin.msg(lm.Rentable_StayInMarket, rentable.StayInMarket) + "\n");
                rentableString.append(this.plugin.msg(lm.Rentable_AllowAutoPay, rentable.AllowAutoPay));
            }
            if (sender instanceof Player) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "tellraw " + sender.getName() + " " + convertToRaw(null, forRentMsg, rentableString.toString()));
            } else {
                this.plugin.msg(sender, forRentMsg);
            }
        } else if (this.plugin.getConfigManager().enabledRentSystem() && this.plugin.getRentManager().isRented(areaname)) {
            String RentedMsg = this.plugin.msg(lm.Residence_RentedBy, this.plugin.getRentManager().getRentingPlayer(areaname));

            RentableLand rentable = this.plugin.getRentManager().getRentableLand(areaname);
            RentedLand rented = this.plugin.getRentManager().getRentedLand(areaname);

            StringBuilder rentableString = new StringBuilder();
            if (rented != null) {
                rentableString.append(this.plugin.msg(lm.Rent_Expire, GetTime.getTime(rented.endTime)) + "\n");
                if (rented.player.equals(sender.getName()) || resadmin || res.isOwner(sender)) {
                    rentableString.append((rented.AutoPay ? this.plugin.msg(lm.Rent_AutoPayTurnedOn) : this.plugin.msg(lm.Rent_AutoPayTurnedOff))
                                          + "\n");
                }
            }

            if (rentable != null) {
                rentableString.append(this.plugin.msg(lm.General_Cost, rentable.cost, rentable.days) + "\n");
                rentableString.append(this.plugin.msg(lm.Rentable_AllowRenewing, rentable.AllowRenewing) + "\n");
                rentableString.append(this.plugin.msg(lm.Rentable_StayInMarket, rentable.StayInMarket) + "\n");
                rentableString.append(this.plugin.msg(lm.Rentable_AllowAutoPay, rentable.AllowAutoPay));
            }

            if (sender instanceof Player) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "tellraw " + sender.getName() + " " + convertToRaw(null, RentedMsg, rentableString.toString()));
            } else {
                this.plugin.msg(sender, RentedMsg);
            }
        } else if (this.plugin.getTransactionManager().isForSale(areaname)) {
            int amount = this.plugin.getTransactionManager().getSaleAmount(areaname);
            String SellMsg = this.plugin.msg(lm.Economy_LandForSale) + " " + amount;
            this.plugin.msg(sender, SellMsg);
        }

        this.plugin.msg(sender, lm.General_Separator);
    }

    public String convertToRaw(String preText, String text, String hover) {
        return convertToRaw(preText, text, hover, null);
    }

    public String convertToRaw(String preText, String text, String hover, String command) {
        StringBuilder msg = new StringBuilder();
        String cmd = "";
        if (command != null) {
            cmd = ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/" + command + "\"}";
        }
        msg.append("[\"\",");
        if (preText != null) {
            msg.append("{\"text\":\"" + preText + "\"}");
        }
        msg.append(
            "{\"text\":\"" + text + "\"" + cmd + ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + hover
            + "\"}]}}}");
        msg.append("]");
        return msg.toString();
    }

    public void mirrorPerms(Player reqPlayer, String targetArea, String sourceArea, boolean resadmin) {
        ClaimedResidence reciever = this.getByName(targetArea);
        ClaimedResidence source = this.getByName(sourceArea);
        if (source == null || reciever == null) {
            this.plugin.msg(reqPlayer, lm.Invalid_Residence);
            return;
        }
        if (!resadmin) {
            if (!reciever.getPermissions().hasResidencePermission(reqPlayer, true) || !source.getPermissions()
                .hasResidencePermission(reqPlayer, true)) {
                this.plugin.msg(reqPlayer, lm.General_NoPermission);
                return;
            }
        }
        reciever.getPermissions().applyTemplate(reqPlayer, source.getPermissions(), resadmin);
    }

    public Map<String, Object> save() {
        clearSaveChache();
        Map<String, Object> worldmap = new LinkedHashMap<>();
        for (World world : this.plugin.getServ().getWorlds()) {
            Map<String, Object> resmap = new LinkedHashMap<>();
            for (Entry<String, ClaimedResidence> res : (new TreeMap<String, ClaimedResidence>(this.residences)).entrySet()) {
                if (!res.getValue().getWorld().equals(world.getName())) {
                    continue;
                }

                try {
                    resmap.put(res.getValue().getResidenceName(), res.getValue().save());
                } catch (Exception ex) {
                    Bukkit.getConsoleSender()
                        .sendMessage(this.plugin.getPrefix() + ChatColor.RED + " Failed to save residence (" + res.getKey() + ")!");
                    Logger.getLogger(ResidenceManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            worldmap.put(world.getName(), resmap);
        }
        return worldmap;
    }

    private void clearSaveChache() {
        this.optimizeMessages.clear();
        this.optimizeFlags.clear();
    }

    public MinimizeMessages addMessageToTempCache(String world, String enter, String leave) {
        List<MinimizeMessages> ls = this.optimizeMessages.get(world);
        if (ls == null) {
            ls = new ArrayList<MinimizeMessages>();
        }
        for (MinimizeMessages one : ls) {
            if (!one.add(enter, leave)) {
                continue;
            }
            return one;
        }
        MinimizeMessages m = new MinimizeMessages(ls.size() + 1, enter, leave);
        ls.add(m);
        this.optimizeMessages.put(world, ls);
        return m;
    }

    public HashMap<Integer, Object> getMessageCatch(String world) {
        HashMap<Integer, Object> t = new HashMap<Integer, Object>();
        List<MinimizeMessages> ls = this.optimizeMessages.get(world);
        if (ls == null) {
            return null;
        }
        for (MinimizeMessages one : ls) {
            Map<String, Object> root = new HashMap<>();
            root.put("EnterMessage", one.getEnter());
            root.put("LeaveMessage", one.getLeave());
            t.put(one.getId(), root);
        }
        return t;
    }

    public MinimizeFlags addFlagsTempCache(String world, Map<String, Boolean> map) {
        if (world == null) {
            return null;
        }
        List<MinimizeFlags> ls = this.optimizeFlags.get(world);
        if (ls == null) {
            ls = new ArrayList<MinimizeFlags>();
        }
        for (MinimizeFlags one : ls) {
            if (!one.add(map)) {
                continue;
            }
            return one;
        }
        MinimizeFlags m = new MinimizeFlags(ls.size() + 1, map);
        ls.add(m);
        this.optimizeFlags.put(world, ls);
        return m;
    }

    public HashMap<Integer, Object> getFlagsCatch(String world) {
        HashMap<Integer, Object> t = new HashMap<Integer, Object>();
        List<MinimizeFlags> ls = this.optimizeFlags.get(world);
        if (ls == null) {
            return null;
        }
        for (MinimizeFlags one : ls) {
            t.put(one.getId(), one.getFlags());
        }
        return t;
    }

    public HashMap<String, HashMap<Integer, MinimizeMessages>> getCacheMessages() {
        return this.cacheMessages;
    }

    public HashMap<String, HashMap<Integer, MinimizeFlags>> getCacheFlags() {
        return this.cacheFlags;
    }

    public String getChacheMessageEnter(String world, int id) {
        HashMap<Integer, MinimizeMessages> c = this.cacheMessages.get(world);
        if (c == null) {
            return null;
        }
        MinimizeMessages m = c.get(id);
        if (m == null) {
            return null;
        }
        return m.getEnter();
    }

    public String getChacheMessageLeave(String world, int id) {
        HashMap<Integer, MinimizeMessages> c = this.cacheMessages.get(world);
        if (c == null) {
            return null;
        }
        MinimizeMessages m = c.get(id);
        if (m == null) {
            return null;
        }
        return m.getLeave();
    }

    public Map<String, Boolean> getChacheFlags(String world, int id) {
        HashMap<Integer, MinimizeFlags> c = this.cacheFlags.get(world);
        if (c == null) {
            return null;
        }
        MinimizeFlags m = c.get(id);
        if (m == null) {
            return null;
        }
        return m.getFlags();
    }

    public void load(Map<String, Object> root) throws Exception {
        if (root == null) {
            return;
        }
        this.residences.clear();
        for (World world : this.plugin.getServ().getWorlds()) {
            long time = System.currentTimeMillis();

            @SuppressWarnings("unchecked")
            Map<String, Object> reslist = (Map<String, Object>) root.get(world.getName());
            Bukkit.getConsoleSender().sendMessage(this.plugin.getPrefix() + " Loading " + world.getName() + " data into memory...");
            if (reslist != null) {
                try {
                    this.chunkResidences.put(world.getName(), loadMap(world.getName(), reslist));
                } catch (Exception ex) {
                    Bukkit.getConsoleSender()
                        .sendMessage(this.plugin.getPrefix() + ChatColor.RED + "Error in loading save file for world: " + world.getName());
                    if (this.plugin.getConfigManager().stopOnSaveError()) {
                        throw (ex);
                    }
                }
            }

            long pass = System.currentTimeMillis() - time;
            String PastTime = pass > 1000 ? String.format("%.2f", (pass / 1000F)) + " sec" : pass + " ms";

            Bukkit.getConsoleSender().sendMessage(this.plugin.getPrefix() + " Loaded " + world.getName() + " data into memory. (" + PastTime + ")");
        }

        clearLoadChache();
    }

    public Map<ChunkRef, List<ClaimedResidence>> loadMap(String worldName, Map<String, Object> root) throws Exception {
        Map<ChunkRef, List<ClaimedResidence>> retRes = new HashMap<>();
        if (root == null) {
            return retRes;
        }

        int i = 0;
        int y = 0;
        for (Entry<String, Object> res : root.entrySet()) {
            if (i == 100 & this.plugin.getConfigManager().isUUIDConvertion()) {
                Bukkit.getConsoleSender()
                    .sendMessage(this.plugin.getPrefix() + " " + worldName + " UUID conversion done: " + y + " of " + root.size());
            }
            if (i >= 100) {
                i = 0;
            }
            i++;
            y++;
            try {
                @SuppressWarnings("unchecked")
                ClaimedResidence residence = ClaimedResidence.load(worldName, (Map<String, Object>) res.getValue(), null, this.plugin);
                if (residence == null) {
                    continue;
                }

                if (residence.getPermissions().getOwnerUUID().toString().equals(this.plugin.getServerLandUUID()) &&
                    !residence.getOwner().equalsIgnoreCase("Server land") &&
                    !residence.getOwner().equalsIgnoreCase(this.plugin.getServerLandname())) {
                    continue;
                }

                if (residence.getOwner().equalsIgnoreCase("Server land")) {
                    residence.getPermissions().setOwner(this.plugin.getServerLandname(), false);
                }
                String resName = res.getKey().toLowerCase();

                // Checking for duplicated residence names and renaming them
                int increment = getNameIncrement(resName);

                if (residence.getResidenceName() == null) {
                    residence.setName(res.getKey());
                }

                if (increment > 0) {
                    residence.setName(residence.getResidenceName() + increment);
                    resName += increment;
                }

                for (ChunkRef chunk : getChunks(residence)) {
                    List<ClaimedResidence> ress = new ArrayList<>();
                    if (retRes.containsKey(chunk)) {
                        ress.addAll(retRes.get(chunk));
                    }
                    ress.add(residence);
                    retRes.put(chunk, ress);
                }

                this.plugin.getPlayerManager().addResidence(residence.getOwner(), residence);

                this.residences.put(resName.toLowerCase(), residence);

            } catch (Exception ex) {
                Bukkit.getConsoleSender().sendMessage(
                    this.plugin.getPrefix() + ChatColor.RED + " Failed to load residence (" + res.getKey() + ")! Reason:" + ex.getMessage()
                    + " Error Log:");
                Logger.getLogger(ResidenceManager.class.getName()).log(Level.SEVERE, null, ex);
                if (this.plugin.getConfigManager().stopOnSaveError()) {
                    throw (ex);
                }
            }
        }

        return retRes;
    }

    private void clearLoadChache() {
        this.cacheMessages.clear();
        this.cacheFlags.clear();
    }

    private int getNameIncrement(String name) {
        String orName = name;
        int i = 0;
        while (i < 1000) {
            if (this.residences.containsKey(name.toLowerCase())) {
                i++;
                name = orName + i;
            } else {
                break;
            }
        }
        return i;
    }

    public boolean renameResidence(String oldName, String newName) {
        return this.renameResidence(null, oldName, newName, true);
    }

    public boolean renameResidence(Player player, String oldName, String newName, boolean resadmin) {
        if (!this.plugin.hasPermission(player, "residence.rename")) {
            return false;
        }

        if (!this.plugin.validName(newName)) {
            this.plugin.msg(player, lm.Invalid_NameCharacters);
            return false;
        }
        ClaimedResidence res = this.getByName(oldName);
        if (res == null) {
            this.plugin.msg(player, lm.Invalid_Residence);
            return false;
        }
        oldName = res.getName();
        if (res.getPermissions().hasResidencePermission(player, true) || resadmin) {
            if (res.getParent() == null) {
                if (this.residences.containsKey(newName.toLowerCase())) {
                    this.plugin.msg(player, lm.Residence_AlreadyExists, newName);
                    return false;
                }

                ResidenceRenameEvent resevent = new ResidenceRenameEvent(res, newName, oldName);
                this.plugin.getServ().getPluginManager().callEvent(resevent);
                removeChunkList(oldName);
                res.setName(newName);

                this.residences.put(newName.toLowerCase(), res);
                this.residences.remove(oldName.toLowerCase());

                calculateChunks(newName);

                this.plugin.getSignUtil().updateSignResName(res);

                this.plugin.msg(player, lm.Residence_Rename, oldName, newName);

                return true;
            }
            String[] oldname = oldName.split("\\.");
            ClaimedResidence parent = res.getParent();

            boolean feed = parent.renameSubzone(player, oldname[oldname.length - 1], newName, resadmin);

            this.plugin.getSignUtil().updateSignResName(res);

            return feed;
        }

        this.plugin.msg(player, lm.General_NoPermission);

        return false;
    }

    public void giveResidence(Player reqPlayer, String targPlayer, String residence, boolean resadmin) {
        giveResidence(reqPlayer, targPlayer, residence, resadmin, false);
    }

    public void giveResidence(Player reqPlayer, String targPlayer, String residence, boolean resadmin, boolean includeSubzones) {
        giveResidence(reqPlayer, targPlayer, getByName(residence), resadmin, includeSubzones);
    }

    public void giveResidence(Player reqPlayer, String targPlayer, ClaimedResidence res, boolean resadmin, boolean includeSubzones) {

        if (res == null) {
            this.plugin.msg(reqPlayer, lm.Invalid_Residence);
            return;
        }

        String residence = res.getName();

        if (!res.getPermissions().hasResidencePermission(reqPlayer, true) && !resadmin) {
            this.plugin.msg(reqPlayer, lm.General_NoPermission);
            return;
        }
        Player giveplayer = this.plugin.getServ().getPlayer(targPlayer);
        if (giveplayer == null || !giveplayer.isOnline()) {
            this.plugin.msg(reqPlayer, lm.General_NotOnline);
            return;
        }
        CuboidArea[] areas = res.getAreaArray();

        ResidencePlayer rPlayer = this.plugin.getPlayerManager().getResidencePlayer(giveplayer);
        PermissionGroup group = rPlayer.getGroup();

        if (areas.length > group.getMaxPhysicalPerResidence() && !resadmin) {
            this.plugin.msg(reqPlayer, lm.Residence_GiveLimits);
            return;
        }
        if (!hasMaxZones(giveplayer.getName(), rPlayer.getMaxRes()) && !resadmin) {
            this.plugin.msg(reqPlayer, lm.Residence_GiveLimits);
            return;
        }
        if (!resadmin) {
            for (CuboidArea area : areas) {
                if (!res.isSubzone() && !res.isSmallerThanMax(giveplayer, area, resadmin) || res.isSubzone() && !res
                    .isSmallerThanMaxSubzone(giveplayer, area,
                        resadmin)) {
                    this.plugin.msg(reqPlayer, lm.Residence_GiveLimits);
                    return;
                }
            }
        }

        if (!res.getPermissions().setOwner(giveplayer, true)) {
            return;
        }
        // Fix phrases here
        this.plugin.msg(reqPlayer, lm.Residence_Give, residence, giveplayer.getName());
        this.plugin.msg(giveplayer, lm.Residence_Recieve, residence, reqPlayer.getName());

        if (includeSubzones) {
            for (ClaimedResidence one : res.getSubzones()) {
                giveResidence(reqPlayer, targPlayer, one, resadmin, includeSubzones);
            }
        }
    }

    public void removeAllFromWorld(CommandSender sender, String world) {
        int count = 0;
        Iterator<ClaimedResidence> it = this.residences.values().iterator();
        while (it.hasNext()) {
            ClaimedResidence next = it.next();
            if (next.getWorld().equals(world)) {
                it.remove();
                count++;
            }
        }
        this.chunkResidences.remove(world);
        this.chunkResidences.put(world, new HashMap<ChunkRef, List<ClaimedResidence>>());
        if (count == 0) {
            sender.sendMessage(ChatColor.RED + "No residences found in world: " + ChatColor.YELLOW + world);
        } else {
            sender.sendMessage(
                ChatColor.RED + "Removed " + ChatColor.YELLOW + count + ChatColor.RED + " residences in world: " + ChatColor.YELLOW + world);
        }

//	plugin.getPlayerManager().fillList();
    }

    public int getResidenceCount() {
        return this.residences.size();
    }

    public Map<String, ClaimedResidence> getResidences() {
        return this.residences;
    }

    public static final class ChunkRef {

        private final int z;
        private final int x;
        public ChunkRef(Location loc) {
            this.x = getChunkCoord(loc.getBlockX());
            this.z = getChunkCoord(loc.getBlockZ());
        }

        public static int getChunkCoord(final int val) {
            // For more info, see CraftBukkit.CraftWorld.getChunkAt( Location )
            return val >> 4;
        }

        public ChunkRef(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public int hashCode() {
            return this.x ^ this.z;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ChunkRef other = (ChunkRef) obj;
            return this.x == other.x && this.z == other.z;
        }

        /**
         * Useful for debug
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{ x: ").append(this.x).append(", z: ").append(this.z).append(" }");
            return sb.toString();
        }

        public int getZ() {
            return this.z;
        }

        public int getX() {
            return this.x;
        }
    }

}
