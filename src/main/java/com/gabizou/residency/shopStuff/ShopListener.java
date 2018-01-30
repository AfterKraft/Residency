package com.gabizou.residency.shopStuff;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.event.ResidenceCreationEvent;
import com.gabizou.residency.event.ResidenceDeleteEvent;
import com.gabizou.residency.event.ResidenceFlagChangeEvent;
import com.gabizou.residency.event.ResidenceRenameEvent;
import com.gabizou.residency.protection.FlagPermissions.FlagState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class ShopListener implements Listener {

    public static List<String> Delete = new ArrayList<String>();
    private Residence plugin;

    public ShopListener(Residence residence) {
        this.plugin = residence;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignInteract(PlayerInteractEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getPlayer().getWorld())) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        if (!(block.getState() instanceof Sign)) {
            return;
        }

        Player player = event.getPlayer();

        Location loc = block.getLocation();

        if (Delete.contains(player.getName())) {
            Board Found = null;
            for (Board one : this.plugin.getShopSignUtilManager().GetAllBoards()) {
                for (Location location : one.GetLocations()) {

                    if (!loc.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) {
                        continue;
                    }
                    if (loc.getBlockX() != location.getBlockX()) {
                        continue;
                    }
                    if (loc.getBlockY() != location.getBlockY()) {
                        continue;
                    }
                    if (loc.getBlockZ() != location.getBlockZ()) {
                        continue;
                    }

                    Found = one;
                    break;
                }

                if (Found != null) {
                    break;
                }
            }
            if (Found != null) {
                this.plugin.getShopSignUtilManager().GetAllBoards().remove(Found);
                this.plugin.getShopSignUtilManager().saveSigns();
                this.plugin.msg(player, lm.Shop_DeletedBoard);
            } else {
                this.plugin.msg(player, lm.Shop_IncorrectBoard);
            }
            Delete.remove(player.getName());
            return;
        }

        String resName = null;
        for (Board one : this.plugin.getShopSignUtilManager().GetAllBoards()) {
            resName = one.getResNameByLoc(loc);
            if (resName != null) {
                break;
            }
        }

        if (resName != null) {
            Bukkit.dispatchCommand(event.getPlayer(), "res tp " + resName);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFlagChangeShop(ResidenceFlagChangeEvent event) {

        if (event.isCancelled()) {
            return;
        }

        if (!event.getFlag().equalsIgnoreCase("shop")) {
            return;
        }

        switch (event.getNewState()) {
            case NEITHER:
            case FALSE:
                this.plugin.getResidenceManager().removeShop(event.getResidence());
                this.plugin.getShopSignUtilManager().BoardUpdate();
                this.plugin.getShopSignUtilManager().saveSigns();
                break;
            case INVALID:
                break;
            case TRUE:
                this.plugin.getResidenceManager().addShop(event.getResidence().getName());
                event.getResidence().getPermissions().setFlag("tp", FlagState.TRUE);
                event.getResidence().getPermissions().setFlag("move", FlagState.TRUE);
                event.getResidence().getPermissions().setFlag("pvp", FlagState.FALSE);
                this.plugin.getShopSignUtilManager().BoardUpdate();
                this.plugin.getShopSignUtilManager().saveSigns();
                break;
            default:
                break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onResidenceRename(ResidenceRenameEvent event) {
        if (!event.getResidence().GetShopVotes().isEmpty()) {
            this.plugin.getResidenceManager().addShop(event.getResidence());
            this.plugin.getResidenceManager().removeShop(event.getOldResidenceName());
            this.plugin.getShopSignUtilManager().saveShopVotes();
            this.plugin.getShopSignUtilManager().BoardUpdateDelayed();
            this.plugin.getShopSignUtilManager().saveSigns();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFlagChange(ResidenceFlagChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getFlag().equalsIgnoreCase("tp") && event.getNewState() == FlagState.TRUE) {
            return;
        }

        if (event.getFlag().equalsIgnoreCase("move") && event.getNewState() == FlagState.TRUE) {
            return;
        }

        if (event.getFlag().equalsIgnoreCase("pvp") && event.getNewState() == FlagState.FALSE) {
            return;
        }

        if (!event.getFlag().equalsIgnoreCase("move") && !event.getFlag().equalsIgnoreCase("tp") && !event.getFlag().equalsIgnoreCase("pvp")) {
            return;
        }

        if (!event.getResidence().getPermissions().has("shop", false)) {
            return;
        }

        event.setCancelled(true);

        this.plugin.msg(event.getPlayer(), ChatColor.YELLOW + "Can't change while shop flag is set to true");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onResidenceCreate(ResidenceCreationEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!event.getResidence().getPermissions().has("shop", false)) {
            return;
        }

        this.plugin.getResidenceManager().addShop(event.getResidence().getName());

        this.plugin.getShopSignUtilManager().BoardUpdate();
        this.plugin.getShopSignUtilManager().saveSigns();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onResidenceRemove(ResidenceDeleteEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!event.getResidence().getPermissions().has("shop", true)) {
            return;
        }

        this.plugin.getResidenceManager().removeShop(event.getResidence());
        this.plugin.getShopSignUtilManager().BoardUpdate();
        this.plugin.getShopSignUtilManager().saveSigns();
    }
}
