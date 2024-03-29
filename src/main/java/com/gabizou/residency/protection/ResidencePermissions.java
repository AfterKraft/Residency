package com.gabizou.residency.protection;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.ResidencePlayer;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.event.ResidenceFlagChangeEvent;
import com.gabizou.residency.event.ResidenceFlagCheckEvent;
import com.gabizou.residency.event.ResidenceFlagEvent.FlagType;
import com.gabizou.residency.event.ResidenceOwnerChangeEvent;
import com.gabizou.residency.permissions.PermissionGroup;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class ResidencePermissions extends FlagPermissions {

    private UUID ownerUUID;
    private String ownerLastKnownName;
    private String world;
    private ClaimedResidence residence;

    public ResidencePermissions(ClaimedResidence res, String creator, String inworld) {
        this(res);
        this.ownerUUID = Residence.getInstance().getPlayerUUID(creator);
        if (this.ownerUUID == null) {
            this.ownerUUID = UUID.fromString(Residence.getInstance().getTempUserUUID());
        }
        this.ownerLastKnownName = creator;
        this.world = inworld;
    }

    private ResidencePermissions(ClaimedResidence res) {
        super();
        this.residence = res;
    }

    public static ResidencePermissions load(String worldName, ClaimedResidence res, Map<String, Object> root) throws Exception {
        ResidencePermissions newperms = new ResidencePermissions(res);
        //newperms.owner = (String) root.get("Owner");
        if (root.containsKey("OwnerUUID") || root.containsKey("OwnerLastKnownName")) {
            if (!root.containsKey("OwnerUUID")) {
                newperms.ownerUUID = UUID.fromString(Residence.getInstance().getTempUserUUID());//get empty owner UUID
            } else {
                newperms.ownerUUID = UUID.fromString((String) root.get("OwnerUUID"));//get owner UUID
            }
            //			String name = Residence.getPlayerName(newperms.ownerUUID); //try to find the current name of the owner
            newperms.ownerLastKnownName = (String) root.get("OwnerLastKnownName");//otherwise load last known name from file

            User p = null;
            if (newperms.ownerLastKnownName == null) {
                p = Residence.getInstance().getOfflinePlayer(newperms.ownerUUID);
            }

            if (p != null) {
                newperms.ownerLastKnownName = p.getName();
            }

            if (newperms.ownerLastKnownName == null) {
                return newperms;
            }

            if (newperms.ownerLastKnownName.equalsIgnoreCase("Server land") || newperms.ownerLastKnownName
                .equalsIgnoreCase(Residence.getInstance().getServerLandname())) {
                newperms.ownerUUID = UUID.fromString(Residence.getInstance().getServerLandUUID());//UUID for server land
                newperms.ownerLastKnownName = Residence.getInstance().getServerLandname();
            } else if (newperms.ownerUUID.toString().equals(Residence.getInstance().getTempUserUUID())) //check for fake UUID
            {
                UUID
                    realUUID =
                    Residence.getInstance().getPlayerUUID(newperms.ownerLastKnownName);//try to find the real UUID of the player if possible now
                if (realUUID != null) {
                    newperms.ownerUUID = realUUID;
                }
            }
        } else if (root.containsKey("Owner")) //convert old owner name save format into uuid format
        {
            String owner = (String) root.get("Owner");
            newperms.ownerLastKnownName = owner;
            newperms.ownerUUID = Residence.getInstance().getPlayerUUID(owner);
            if (newperms.ownerUUID == null) {
                newperms.ownerUUID =
                    UUID.fromString(Residence.getInstance().getTempUserUUID());//set fake UUID until we can find real one for last known player
            }
        } else {
            newperms.ownerUUID =
                UUID.fromString(
                    Residence.getInstance().getServerLandUUID());//cant determine owner name or UUID... setting zero UUID which is server land
            newperms.ownerLastKnownName = Residence.getInstance().getServerLandname();
        }
        newperms.world = worldName;
        FlagPermissions.load(root, newperms);

        if (newperms.getOwner() == null || newperms.world == null || newperms.playerFlags == null || newperms.groupFlags == null
            || newperms.cuboidFlags == null) {
            throw new Exception("Invalid Residence Permissions...");
        }
        return newperms;
    }

    public boolean playerHas(CommandSender sender, Flags flag, boolean def) {
        if (sender instanceof Player) {
            return playerHas((Player) sender, flag, def);
        }
        return true;
    }

    @Deprecated
    public boolean playerHas(Player player, String flag, boolean def) {
        return this.playerHas(player.getName(), this.world, flag, def);
    }

    public boolean playerHas(String player, Flags flag, boolean def) {
        return playerHas(player, flag.getName(), def);
    }

    @Deprecated
    public boolean playerHas(String player, String flag, boolean def) {
        return this.playerHas(player, this.world, flag, def);
    }

    @Deprecated
    public boolean has(String flag, FlagCombo f) {
        return has(flag, f, true);
    }

//    public boolean playerHas(String player, Flags flag, FlagCombo f) {
//	switch (f) {
//	case FalseOrNone:
//	    return !this.playerHas(player, world, flag, false);
//	case OnlyFalse:
//	    return !this.playerHas(player, world, flag, true);
//	case OnlyTrue:
//	    return this.playerHas(player, world, flag, false);
//	case TrueOrNone:
//	    return this.playerHas(player, world, flag, true);
//	default:
//	    return false;
//	}
//    }

    @Deprecated
    public boolean has(String flag, FlagCombo f, boolean checkParent) {
        switch (f) {
            case FalseOrNone:
                return !has(flag, false, checkParent);
            case OnlyFalse:
                return !has(flag, true, checkParent);
            case OnlyTrue:
                return has(flag, false, checkParent);
            case TrueOrNone:
                return has(flag, true, checkParent);
            default:
                return false;
        }
    }

    public boolean hasApplicableFlag(String player, String flag) {
        return super.inheritanceIsPlayerSet(player, flag) ||
               super.inheritanceIsGroupSet(Residence.getInstance().getPlayerManager().getResidencePlayer(player).getGroup(this.world).getGroupName(),
                   flag) ||
               super.inheritanceIsSet(flag);
    }

    public void applyTemplate(Player player, FlagPermissions list, boolean resadmin) {
        if (player != null) {
            if (!resadmin) {
                if (!Residence.getInstance().getConfigManager().isOfflineMode() && !player.getUniqueId().toString()
                    .equals(this.ownerUUID.toString())) {
                    Residence.getInstance().msg(player, lm.General_NoPermission);
                    return;
                } else if (!player.getName().equals(this.ownerLastKnownName)) {
                    Residence.getInstance().msg(player, lm.General_NoPermission);
                    return;
                }
            }
        } else {
            resadmin = true;
        }
        ResidencePlayer rPlayer = Residence.getInstance().getPlayerManager().getResidencePlayer(this.getOwner());
        PermissionGroup group = rPlayer.getGroup(this.world);

        for (Entry<String, Boolean> flag : list.cuboidFlags.entrySet()) {
            if (group.hasFlagAccess(flag.getKey()) || resadmin) {
                this.cuboidFlags.put(flag.getKey(), flag.getValue());
            } else {
                if (player != null) {
                    Residence.getInstance().msg(player, lm.Flag_SetDeny, flag.getKey());
                }
            }
        }
        for (Entry<String, Map<String, Boolean>> plists : list.playerFlags.entrySet()) {
            Map<String, Boolean> map = this.getPlayerFlags(plists.getKey(), true);
            for (Entry<String, Boolean> flag : plists.getValue().entrySet()) {
                if (group.hasFlagAccess(flag.getKey()) || resadmin) {
                    map.put(flag.getKey(), flag.getValue());
                } else {
                    if (player != null) {
                        Residence.getInstance().msg(player, lm.Flag_SetDeny, flag.getKey());
                    }
                }
            }
        }
        for (Entry<String, Map<String, Boolean>> glists : list.groupFlags.entrySet()) {
            for (Entry<String, Boolean> flag : glists.getValue().entrySet()) {
                if (group.hasFlagAccess(flag.getKey()) || resadmin) {
                    if (!this.groupFlags.containsKey(glists.getKey())) {
                        this.groupFlags.put(glists.getKey(), Collections.synchronizedMap(new HashMap<String, Boolean>()));
                    }
                    this.groupFlags.get(glists.getKey()).put(flag.getKey(), flag.getValue());
                } else {
                    if (player != null) {
                        Residence.getInstance().msg(player, lm.Flag_SetDeny, flag.getKey());
                    }
                }
            }
        }
        if (player != null) {
            Residence.getInstance().msg(player, lm.Residence_PermissionsApply);
        }
    }

    public String getOwner() {
        if (Residence.getInstance().getConfigManager().isOfflineMode()) {
            return this.ownerLastKnownName;
        }
        if (this.ownerUUID.toString().equals(Residence.getInstance().getServerLandUUID())) //check for server land
        {
            return Residence.getInstance().getServerLandname();
        }
        String name = Residence.getInstance().getPlayerName(this.ownerUUID);//try to find the owner's name
        if (name == null) {
            return this.ownerLastKnownName;//return last known if we cannot find it
        }
        this.ownerLastKnownName = name;//update last known if we did find it
        return name;
    }

    private boolean checkCanSetFlag(CommandSender sender, String flag, FlagState state, boolean globalflag, boolean resadmin) {
        if (!checkValidFlag(flag, globalflag)) {
            Residence.getInstance().msg(sender, lm.Invalid_Flag);
            return false;
        }
        if (state == FlagState.INVALID) {
            Residence.getInstance().msg(sender, lm.Invalid_FlagState);
            return false;
        }
        if (!resadmin) {
            if (!this.hasResidencePermission(sender, false)) {
                Residence.getInstance().msg(sender, lm.General_NoPermission);
                return false;
            }
            if (!hasFlagAccess(this.getOwner(), flag) && !sender.hasPermission("residence.flag." + flag.toLowerCase())) {
                Residence.getInstance().msg(sender, lm.Flag_SetFailed, flag);
                return false;
            }
        }
        return true;
    }

    private boolean hasFlagAccess(String player, String flag) {
        ResidencePlayer rPlayer = Residence.getInstance().getPlayerManager().getResidencePlayer(player);
        PermissionGroup group = rPlayer.getGroup(this.world);
        return group.hasFlagAccess(flag);
    }

    public boolean setPlayerFlag(CommandSender sender, String targetPlayer, String flag, String flagstate, boolean resadmin, boolean Show) {

        if (Residence.getInstance().getPlayerUUID(targetPlayer) == null) {
            sender.sendMessage("no player by this name");
            return false;
        }

        if (validFlagGroups.containsKey(flag)) {
            return this.setFlagGroupOnPlayer(sender, targetPlayer, flag, flagstate, resadmin);
        }
        FlagState state = FlagPermissions.stringToFlagState(flagstate);
        if (checkCanSetFlag(sender, flag, state, false, resadmin)) {
            ResidenceFlagChangeEvent fc = new ResidenceFlagChangeEvent(this.residence, sender instanceof Player ? (Player) sender : null, flag,
                ResidenceFlagChangeEvent.FlagType.PLAYER, state, targetPlayer);
            Residence.getInstance().getServ().getPluginManager().callEvent(fc);
            if (fc.isCancelled()) {
                return false;
            }
            if (super.setPlayerFlag(targetPlayer, flag, state)) {
                if (Show) {
                    Residence.getInstance().msg(sender, lm.Flag_Set, flag, this.residence.getName(), flagstate);
                }
                return true;
            }
        }
        return false;
    }

    public boolean setGroupFlag(Player player, String group, String flag, String flagstate, boolean resadmin) {
        group = group.toLowerCase();
        if (validFlagGroups.containsKey(flag)) {
            return this.setFlagGroupOnGroup(player, flag, group, flagstate, resadmin);
        }
        FlagState state = FlagPermissions.stringToFlagState(flagstate);
        if (checkCanSetFlag(player, flag, state, false, resadmin)) {
            if (Residence.getInstance().getPermissionManager().hasGroup(group)) {
                ResidenceFlagChangeEvent
                    fc =
                    new ResidenceFlagChangeEvent(this.residence, player, flag, ResidenceFlagChangeEvent.FlagType.GROUP, state, group);
                Residence.getInstance().getServ().getPluginManager().callEvent(fc);
                if (fc.isCancelled()) {
                    return false;
                }
                if (super.setGroupFlag(group, flag, state)) {
                    Residence.getInstance().msg(player, lm.Flag_Set, flag, this.residence.getName(), flagstate);
                    return true;
                }
            } else {
                Residence.getInstance().msg(player, lm.Invalid_Group);
                return false;
            }
        }
        return false;
    }

    public boolean setFlag(CommandSender sender, String flag, String flagstate, boolean resadmin) {
        if (validFlagGroups.containsKey(flag)) {
            return this.setFlagGroup(sender, flag, flagstate, resadmin);
        }

        FlagState state = FlagPermissions.stringToFlagState(flagstate);

        if (Residence.getInstance().getConfigManager().isPvPFlagPrevent()) {
            for (String oneFlag : Residence.getInstance().getConfigManager().getProtectedFlagsList()) {
                if (!flag.equalsIgnoreCase(oneFlag)) {
                    continue;
                }

                ArrayList<Player> players = this.residence.getPlayersInResidence();
                if (!resadmin && (players.size() > 1 || players.size() == 1 && !players.get(0).getName().equals(this.getOwner()))) {
                    int size = 0;
                    for (Player one : players) {
                        if (!one.getName().equals(this.getOwner())) {
                            size++;
                        }
                    }
                    Residence.getInstance().msg(sender, lm.Flag_ChangeDeny, flag, size);
                    return false;
                }
            }
        }

        if (checkCanSetFlag(sender, flag, state, true, resadmin)) {
            ResidenceFlagChangeEvent fc = new ResidenceFlagChangeEvent(this.residence, sender instanceof Player ? (Player) sender : null, flag,
                ResidenceFlagChangeEvent.FlagType.RESIDENCE, state, null);
            Residence.getInstance().getServ().getPluginManager().callEvent(fc);
            if (fc.isCancelled()) {
                return false;
            }
            if (super.setFlag(flag, state)) {
                Residence.getInstance().msg(sender, lm.Flag_Set, flag, this.residence.getName(), flagstate);
                return true;
            }
        }
        return false;
    }

    public boolean removeAllPlayerFlags(CommandSender sender, String targetPlayer, boolean resadmin) {
        if (this.hasResidencePermission(sender, false) || resadmin) {
            ResidenceFlagChangeEvent fc = new ResidenceFlagChangeEvent(this.residence, sender instanceof Player ? (Player) sender : null, "ALL",
                ResidenceFlagChangeEvent.FlagType.RESIDENCE, FlagState.NEITHER, null);
            Residence.getInstance().getServ().getPluginManager().callEvent(fc);
            if (fc.isCancelled()) {
                return false;
            }
            super.removeAllPlayerFlags(targetPlayer);
            Residence.getInstance().msg(sender, lm.Flag_RemovedAll, targetPlayer, this.residence.getName());
            return true;
        }
        return false;
    }

    public boolean hasResidencePermission(CommandSender sender, boolean requireOwner) {
        if (!(sender instanceof Player)) {
            return true;
        }

        ClaimedResidence par = this.residence.getParent();
        Player player = (Player) sender;
        if (par != null) {
            if (par.getPermissions().playerHas(player, Flags.admin, FlagCombo.OnlyTrue)) {
                return true;
            }
        }

        if (Residence.getInstance().getConfigManager().enabledRentSystem()) {
            String resname = this.residence.getName();
            if (Residence.getInstance().getRentManager().isRented(resname)) {
                if (requireOwner) {
                    return false;
                }
                String renter = Residence.getInstance().getRentManager().getRentingPlayer(resname);
                if (sender.getName().equals(renter)) {
                    return true;
                }
                return (playerHas(player, Flags.admin, FlagCombo.OnlyTrue));
            }
        }
        if (requireOwner) {
            return (this.getOwner().equals(sender.getName()));
        }
        return (playerHas(player, Flags.admin, FlagCombo.OnlyTrue) || this.getOwner().equals(sender.getName()));
    }

    public boolean playerHas(Player player, Flags flag, FlagCombo f) {
        switch (f) {
            case FalseOrNone:
                return !this.playerHas(player, this.world, flag, false);
            case OnlyFalse:
                return !this.playerHas(player, this.world, flag, true);
            case OnlyTrue:
                return this.playerHas(player, this.world, flag, false);
            case TrueOrNone:
                return this.playerHas(player, this.world, flag, true);
            default:
                return false;
        }
    }

    public boolean removeAllGroupFlags(Player player, String group, boolean resadmin) {
        if (this.hasResidencePermission(player, false) || resadmin) {
            ResidenceFlagChangeEvent
                fc =
                new ResidenceFlagChangeEvent(this.residence, player, "ALL", ResidenceFlagChangeEvent.FlagType.GROUP, FlagState.NEITHER, null);
            Residence.getInstance().getServ().getPluginManager().callEvent(fc);
            if (fc.isCancelled()) {
                return false;
            }
            super.removeAllGroupFlags(group);
            Residence.getInstance().msg(player, lm.Flag_RemovedGroup, group, this.residence.getName());
            return true;
        }
        return false;
    }

    public void applyDefaultFlags(Player player, boolean resadmin) {
        if (this.hasResidencePermission(player, true) || resadmin) {
            this.applyDefaultFlags();
            Residence.getInstance().msg(player, lm.Flag_Default);
        } else {
            Residence.getInstance().msg(player, lm.General_NoPermission);
        }
    }

    public void applyDefaultFlags() {
        ResidencePlayer rPlayer = Residence.getInstance().getPlayerManager().getResidencePlayer(this.getOwner());
        PermissionGroup group = rPlayer.getGroup(this.world);
        Set<Entry<String, Boolean>> dflags = group.getDefaultResidenceFlags();
//	Set<Entry<String, Boolean>> dcflags = group.getDefaultCreatorFlags();
        Set<Entry<String, Map<String, Boolean>>> dgflags = group.getDefaultGroupFlags();
        this.applyGlobalDefaults();
        for (Entry<String, Boolean> next : dflags) {
            if (this.checkValidFlag(next.getKey(), true)) {
                this.setFlag(next.getKey(), next.getValue() ? FlagState.TRUE : FlagState.FALSE);
            }
        }

//	for (Entry<String, Boolean> next : dcflags) {
//	    if (this.checkValidFlag(next.getKey(), false)) {
//		if (next.getValue()) {
//		    this.setPlayerFlag(this.getOwner(), next.getKey(), FlagState.TRUE);
//		} else {
//		    this.setPlayerFlag(this.getOwner(), next.getKey(), FlagState.FALSE);
//		}
//	    }
//	}
        for (Entry<String, Map<String, Boolean>> entry : dgflags) {
            Map<String, Boolean> value = entry.getValue();
            for (Entry<String, Boolean> flag : value.entrySet()) {
                this.setGroupFlag(entry.getKey(), flag.getKey(), flag.getValue() ? FlagState.TRUE : FlagState.FALSE);
            }
        }
    }

    public void applyGlobalDefaults() {
        this.clearFlags();
        FlagPermissions gRD = Residence.getInstance().getConfigManager().getGlobalResidenceDefaultFlags();
        FlagPermissions gCD = Residence.getInstance().getConfigManager().getGlobalCreatorDefaultFlags();
        Map<String, FlagPermissions> gGD = Residence.getInstance().getConfigManager().getGlobalGroupDefaultFlags();
        for (Entry<String, Boolean> entry : gRD.cuboidFlags.entrySet()) {
            if (entry.getValue()) {
                this.setFlag(entry.getKey(), FlagState.TRUE);
            } else {
                this.setFlag(entry.getKey(), FlagState.FALSE);
            }
        }
        for (Entry<String, Boolean> entry : gCD.cuboidFlags.entrySet()) {
            if (entry.getValue()) {
                this.setPlayerFlag(this.getOwner(), entry.getKey(), FlagState.TRUE);
            } else {
                this.setPlayerFlag(this.getOwner(), entry.getKey(), FlagState.FALSE);
            }
        }
        for (Entry<String, FlagPermissions> entry : gGD.entrySet()) {
            for (Entry<String, Boolean> flag : entry.getValue().cuboidFlags.entrySet()) {
                if (flag.getValue()) {
                    this.setGroupFlag(entry.getKey(), flag.getKey(), FlagState.TRUE);
                } else {
                    this.setGroupFlag(entry.getKey(), flag.getKey(), FlagState.FALSE);
                }
            }
        }
    }

    @Override
    public boolean setFlag(String flag, FlagState state) {
        ResidenceFlagChangeEvent
            fc =
            new ResidenceFlagChangeEvent(this.residence, null, flag, ResidenceFlagChangeEvent.FlagType.RESIDENCE, state, null);
        Residence.getInstance().getServ().getPluginManager().callEvent(fc);
        if (fc.isCancelled()) {
            return false;
        }
        return super.setFlag(flag, state);
    }

    @Override
    public boolean setPlayerFlag(String player, String flag, FlagState state) {
        ResidenceFlagChangeEvent
            fc =
            new ResidenceFlagChangeEvent(this.residence, null, flag, ResidenceFlagChangeEvent.FlagType.PLAYER, state, player);
        Residence.getInstance().getServ().getPluginManager().callEvent(fc);
        if (fc.isCancelled()) {
            return false;
        }
        return super.setPlayerFlag(player, flag, state);
    }

    @Override
    public boolean setGroupFlag(String group, String flag, FlagState state) {
        ResidenceFlagChangeEvent fc = new ResidenceFlagChangeEvent(this.residence, null, flag, ResidenceFlagChangeEvent.FlagType.GROUP, state, group);
        Residence.getInstance().getServ().getPluginManager().callEvent(fc);
        if (fc.isCancelled()) {
            return false;
        }
        return super.setGroupFlag(group, flag, state);
    }

    @Override
    public boolean playerHas(Player player, Flags flag, boolean def) {
        return playerHas(player, flag.getName(), def);
    }

    @Override
    public boolean playerHas(Player player, String world, Flags flag, boolean def) {
        if (player == null) {
            return false;
        }
        ResidenceFlagCheckEvent fc = new ResidenceFlagCheckEvent(this.residence, flag.getName(), FlagType.PLAYER, player.getName(), def);
        Residence.getInstance().getServ().getPluginManager().callEvent(fc);
        if (fc.isOverriden()) {
            return fc.getOverrideValue();
        }
        return super.playerHas(player, world, flag, def);
    }

    @Override
    @Deprecated
    public boolean playerHas(String player, String world, String flag, boolean def) {
        ResidenceFlagCheckEvent fc = new ResidenceFlagCheckEvent(this.residence, flag, FlagType.PLAYER, player, def);

        Residence.getInstance().getServ().getPluginManager().callEvent(fc);
        if (fc.isOverriden()) {
            return fc.getOverrideValue();
        }
        return super.playerHas(player, world, flag, def);
    }

    @Override
    public boolean groupHas(String group, String flag, boolean def) {
        ResidenceFlagCheckEvent fc = new ResidenceFlagCheckEvent(this.residence, flag, FlagType.GROUP, group, def);
        Residence.getInstance().getServ().getPluginManager().callEvent(fc);
        if (fc.isOverriden()) {
            return fc.getOverrideValue();
        }
        return super.groupHas(group, flag, def);
    }

    @Override
    public boolean has(Flags flag, FlagCombo f) {
        return has(flag, f, true);
    }

    public boolean has(Flags flag, FlagCombo f, boolean checkParent) {
        switch (f) {
            case FalseOrNone:
                return !has(flag, false, checkParent);
            case OnlyFalse:
                return !has(flag, true, checkParent);
            case OnlyTrue:
                return has(flag, false, checkParent);
            case TrueOrNone:
                return has(flag, true, checkParent);
            default:
                return false;
        }
    }

    @Override
    public boolean has(Flags flag, boolean def) {
        return has(flag.getName(), def);
    }

    @Override
    public boolean has(String flag, boolean def) {
        return has(flag, def, true);
    }

    @Override
    public boolean has(String flag, boolean def, boolean checkParent) {
        ResidenceFlagCheckEvent fc = new ResidenceFlagCheckEvent(this.residence, flag, FlagType.RESIDENCE, null, def);
        Residence.getInstance().getServ().getPluginManager().callEvent(fc);
        if (fc.isOverriden()) {
            return fc.getOverrideValue();
        }
        return super.has(flag, def, checkParent);
    }

    @Override
    public Map<String, Object> save(String world) {
        Map<String, Object> root = super.save(this.world);
        if (!this.ownerUUID.toString().equals(Residence.getInstance().getTempUserUUID())) {
            root.put("OwnerUUID", this.ownerUUID.toString());
        }
        root.put("OwnerLastKnownName", this.ownerLastKnownName);
//	root.put("World", world);
        return root;
    }

    public boolean setOwner(Player player, boolean resetFlags) {

        ResidenceOwnerChangeEvent ownerchange = new ResidenceOwnerChangeEvent(this.residence, player);
        Residence.getInstance().getServ().getPluginManager().callEvent(ownerchange);

        // Dont change owner if event is canceled
        if (ownerchange.isCancelled()) {
            return false;
        }

        Residence.getInstance().getPlayerManager().removeResFromPlayer(this.residence.getOwnerUUID(), this.residence);
        Residence.getInstance().getPlayerManager().addResidence(player, this.residence);

        this.ownerLastKnownName = player.getName();
        this.ownerUUID = player.getUniqueId();

        if (resetFlags) {
            this.applyDefaultFlags();
        }

        return true;
    }

    public void setOwner(String newOwner, boolean resetFlags) {

        ResidenceOwnerChangeEvent ownerchange = new ResidenceOwnerChangeEvent(this.residence, newOwner);
        Residence.getInstance().getServ().getPluginManager().callEvent(ownerchange);

        // Dont change owner if event is canceled
        if (ownerchange.isCancelled()) {
            return;
        }

        Residence.getInstance().getPlayerManager().removeResFromPlayer(this.residence.getOwnerUUID(), this.residence);

        this.ownerLastKnownName = newOwner;
        ResidencePlayer rPlayer = Residence.getInstance().getPlayerManager().getResidencePlayer(newOwner);
        if (rPlayer != null) {
            this.ownerUUID = rPlayer.getUuid();
        }

        if (newOwner.equalsIgnoreCase("Server Land") || newOwner.equalsIgnoreCase(Residence.getInstance().getServerLandname())) {
            this.ownerUUID = UUID.fromString(Residence.getInstance().getServerLandUUID());// the UUID for server owned land
        } else {
            UUID playerUUID = Residence.getInstance().getPlayerUUID(newOwner);
            if (playerUUID != null) {
                this.ownerUUID = playerUUID;
            } else {
                this.ownerUUID =
                    UUID.fromString(Residence.getInstance()
                        .getTempUserUUID());//the fake UUID used when unable to find the real one, will be updated with players real UUID when its possible to find it
            }
        }
        Residence.getInstance().getPlayerManager().addResidence(newOwner, this.residence);
        if (resetFlags) {
            this.applyDefaultFlags();
        }
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public String getWorld() {
        return this.world;
    }

    public boolean setFlagGroup(CommandSender sender, String flaggroup, String state, boolean resadmin) {
        if (FlagPermissions.validFlagGroups.containsKey(flaggroup)) {
            ArrayList<String> flags = FlagPermissions.validFlagGroups.get(flaggroup);
            boolean changed = false;
            for (String flag : flags) {
                if (this.setFlag(sender, flag, state, resadmin)) {
                    changed = true;
                }
            }
            return changed;
        }
        return false;
    }

    public boolean setFlagGroupOnGroup(Player player, String flaggroup, String group, String state, boolean resadmin) {
        if (FlagPermissions.validFlagGroups.containsKey(flaggroup)) {
            ArrayList<String> flags = FlagPermissions.validFlagGroups.get(flaggroup);
            boolean changed = false;
            for (String flag : flags) {
                if (this.setGroupFlag(player, group, flag, state, resadmin)) {
                    changed = true;
                }
            }
            return changed;
        }
        return false;
    }

    public boolean setFlagGroupOnPlayer(CommandSender sender, String target, String flaggroup, String state, boolean resadmin) {
        if (FlagPermissions.validFlagGroups.containsKey(flaggroup)) {
            ArrayList<String> flags = FlagPermissions.validFlagGroups.get(flaggroup);
            boolean changed = false;
            String flagString = "";
            int i = 0;
            for (String flag : flags) {
                i++;
                if (this.setPlayerFlag(sender, target, flag, state, resadmin, false)) {
                    changed = true;
                    flagString += flag;
                    if (i < flags.size() - 1) {
                        flagString += ", ";
                    }
                }
            }
            if (flagString.length() > 0) {
                Residence.getInstance().msg(sender, lm.Flag_Set, flagString, target, state);
            }
            return changed;
        }
        return false;
    }

    public String getOwnerLastKnownName() {
        return this.ownerLastKnownName;
    }

    public void setOwnerLastKnownName(String ownerLastKnownName) {
        this.ownerLastKnownName = ownerLastKnownName;
    }
}
