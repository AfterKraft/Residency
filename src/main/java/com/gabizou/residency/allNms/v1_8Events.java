package com.gabizou.residency.allNms;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.protection.FlagPermissions;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class v1_8Events implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractAtArmoStand(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (Residence.getInstance().isResAdminOn(player)) {
            return;
        }

        Entity ent = event.getRightClicked();
        if (!Residence.getInstance().getNms().isArmorStandEntity(ent.getType())) {
            return;
        }

        FlagPermissions perms = Residence.getInstance().getPermsByLocForPlayer(ent.getLocation(), player);

        if (!perms.playerHas(player, Flags.container, perms.playerHas(player, Flags.use, true))) {
            event.setCancelled(true);
            Residence.getInstance().msg(player, lm.Flag_Deny, Flags.container.getName());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockExplodeEvent(BlockExplodeEvent event) {

        Location loc = event.getBlock().getLocation();

        if (Residence.getInstance().isDisabledWorldListener(loc.getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        FlagPermissions world = Residence.getInstance().getWorldFlags().getPerms(loc.getWorld().getName());
        List<Block> preserve = new ArrayList<Block>();
        for (Block block : event.blockList()) {
            FlagPermissions blockperms = Residence.getInstance().getPermsByLoc(block.getLocation());
            if (!blockperms.has(Flags.explode, world.has(Flags.explode, true))) {
                preserve.add(block);
            }
        }
        for (Block block : preserve) {
            event.blockList().remove(block);
        }
    }
}
