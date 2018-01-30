package com.gabizou.residency.listeners;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.protection.FlagPermissions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class SpigotListener implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent event) {
        // disabling event on world
        if (Residence.getInstance().isDisabledWorldListener(event.getPlayer().getWorld())) {
            return;
        }
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        FlagPermissions perms = Residence.getInstance().getPermsByLoc(loc);
        if (perms.has(Flags.nodurability, false)) {
            ItemStack held = Residence.getInstance().getNms().itemInMainHand(player);
            if (held.getType() != Material.AIR) {
                held.setDurability(held.getDurability());
                player.setItemInHand(held);
                event.setDamage(0);
                event.setCancelled(true);
            }
        }
    }
}
