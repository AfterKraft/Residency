package com.gabizou.residency.listeners;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.FlagPermissions;
import com.gabizou.residency.protection.FlagPermissions.FlagCombo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Witch;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResidenceEntityListener implements Listener {

    Residence plugin;

    public ResidenceEntityListener(Residence plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("incomplete-switch")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMinecartHopperItemMove(InventoryMoveItemEvent event) {
        if (!(event.getInitiator().getHolder() instanceof HopperMinecart)) {
            return;
        }
        HopperMinecart hopper = (HopperMinecart) event.getInitiator().getHolder();
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(hopper.getWorld())) {
            return;
        }
        Block block = hopper.getLocation().getBlock();
        switch (block.getType()) {
            case ACTIVATOR_RAIL:
            case DETECTOR_RAIL:
            case POWERED_RAIL:
            case RAILS:
                return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEndermanChangeBlock(EntityChangeBlockEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        if (event.getEntityType() != EntityType.ENDERMAN) {
            return;
        }
        FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
        if (!perms.has(Flags.destroy, true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityInteract(EntityInteractEvent event) {
        // disabling event on world
        Block block = event.getBlock();
        if (block == null) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(block.getWorld())) {
            return;
        }
        Material mat = block.getType();
        Entity entity = event.getEntity();
        FlagPermissions perms = this.plugin.getPermsByLoc(block.getLocation());
        boolean hastrample = perms.has(Flags.trample, perms.has(Flags.build, true));
        if (!hastrample && !(entity.getType() == EntityType.FALLING_BLOCK) && (mat == Material.SOIL || mat == Material.SOUL_SAND)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void AnimalKilling(EntityDamageByEntityEvent event) {
        // disabling event on world
        Entity entity = event.getEntity();
        if (entity == null) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(entity.getWorld())) {
            return;
        }
        if (!this.plugin.getNms().isAnimal(entity)) {
            return;
        }

        Entity damager = event.getDamager();

        if (!(damager instanceof Arrow) && !(damager instanceof Player)) {
            return;
        }

        if (damager instanceof Arrow && !(((Arrow) damager).getShooter() instanceof Player)) {
            return;
        }

        Player cause = null;

        if (damager instanceof Player) {
            cause = (Player) damager;
        } else {
            cause = (Player) ((Arrow) damager).getShooter();
        }

        if (cause == null) {
            return;
        }

        if (this.plugin.isResAdminOn(cause)) {
            return;
        }

        ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(entity.getLocation());

        if (res == null) {
            return;
        }

        if (res.getPermissions().playerHas(cause, Flags.animalkilling, FlagCombo.OnlyFalse)) {
            this.plugin.msg(cause, lm.Residence_FlagDeny, Flags.animalkilling.getName(), res.getName());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void AnimalKillingByFlame(EntityCombustByEntityEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }

        Entity entity = event.getEntity();
        if (entity == null) {
            return;
        }
        if (!this.plugin.getNms().isAnimal(entity)) {
            return;
        }

        ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(entity.getLocation());

        if (res == null) {
            return;
        }

        Entity damager = event.getCombuster();

        if (!(damager instanceof Arrow) && !(damager instanceof Player)) {
            return;
        }

        if (damager instanceof Arrow && !(((Arrow) damager).getShooter() instanceof Player)) {
            return;
        }

        Player cause = null;

        if (damager instanceof Player) {
            cause = (Player) damager;
        } else {
            cause = (Player) ((Arrow) damager).getShooter();
        }

        if (cause == null) {
            return;
        }

        if (this.plugin.isResAdminOn(cause)) {
            return;
        }

        if (res.getPermissions().playerHas(cause, Flags.animalkilling, FlagCombo.OnlyFalse)) {
            this.plugin.msg(cause, lm.Residence_FlagDeny, Flags.animalkilling.getName(), res.getName());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void AnimalDamageByMobs(EntityDamageByEntityEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }

        Entity entity = event.getEntity();
        if (entity == null) {
            return;
        }
        if (!this.plugin.getNms().isAnimal(entity)) {
            return;
        }

        Entity damager = event.getDamager();

        if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player || damager instanceof Player) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLoc(entity.getLocation());
        FlagPermissions world = this.plugin.getWorldFlags().getPerms(entity.getWorld().getName());
        if (!perms.has(Flags.animalkilling, world.has(Flags.animalkilling, true))) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnEntityDeath(EntityDeathEvent event) {
        // disabling event on world
        LivingEntity ent = event.getEntity();
        if (ent == null) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(ent.getWorld())) {
            return;
        }
        if (ent instanceof Player) {
            return;
        }
        Location loc = ent.getLocation();
        FlagPermissions perms = this.plugin.getPermsByLoc(loc);
        if (!perms.has(Flags.mobitemdrop, true)) {
            event.getDrops().clear();
        }
        if (!perms.has(Flags.mobexpdrop, true)) {
            event.setDroppedExp(0);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void VehicleDestroy(VehicleDestroyEvent event) {
        // disabling event on world
        Entity damager = event.getAttacker();
        if (damager == null) {
            return;
        }

        if (this.plugin.isDisabledWorldListener(damager.getWorld())) {
            return;
        }

        Vehicle vehicle = event.getVehicle();

        if (vehicle == null) {
            return;
        }

        if (damager instanceof Projectile && !(((Projectile) damager).getShooter() instanceof Player) || !(damager instanceof Player)) {
            FlagPermissions perms = this.plugin.getPermsByLoc(vehicle.getLocation());
            if (!perms.has(Flags.vehicledestroy, true)) {
                event.setCancelled(true);
                return;
            }
        }

        Player cause = null;

        if (damager instanceof Player) {
            cause = (Player) damager;
        } else if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player) {
            cause = (Player) ((Projectile) damager).getShooter();
        }

        if (cause == null) {
            return;
        }

        if (this.plugin.isResAdminOn(cause)) {
            return;
        }

        ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(vehicle.getLocation());

        if (res == null) {
            return;
        }

        if (res.getPermissions().playerHas(cause, Flags.vehicledestroy, FlagCombo.OnlyFalse)) {
            this.plugin.msg(cause, lm.Residence_FlagDeny, Flags.vehicledestroy.getName(), res.getName());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void MonsterKilling(EntityDamageByEntityEvent event) {
        // disabling event on world
        Entity entity = event.getEntity();
        if (entity == null) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(entity.getWorld())) {
            return;
        }
        if (!isMonster(entity)) {
            return;
        }

        Entity damager = event.getDamager();

        if (!(damager instanceof Arrow) && !(damager instanceof Player)) {
            return;
        }

        if (damager instanceof Arrow && !(((Arrow) damager).getShooter() instanceof Player)) {
            return;
        }

        Player cause = null;

        if (damager instanceof Player) {
            cause = (Player) damager;
        } else {
            cause = (Player) ((Arrow) damager).getShooter();
        }

        if (cause == null) {
            return;
        }

        if (this.plugin.isResAdminOn(cause)) {
            return;
        }

        ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(entity.getLocation());

        if (res == null) {
            return;
        }

        if (res.getPermissions().playerHas(cause, Flags.mobkilling, FlagCombo.OnlyFalse)) {
            this.plugin.msg(cause, lm.Residence_FlagDeny, Flags.mobkilling.getName(), res.getName());
            event.setCancelled(true);
        }
    }

    public static boolean isMonster(Entity ent) {
        return (ent instanceof Monster || ent instanceof Slime || ent instanceof Ghast);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void AnimalLeash(PlayerLeashEntityEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        Player player = event.getPlayer();

        Entity entity = event.getEntity();

        if (!this.plugin.getNms().isAnimal(entity)) {
            return;
        }

        if (this.plugin.isResAdminOn(player)) {
            return;
        }

        ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(entity.getLocation());

        if (res == null) {
            return;
        }

        if (res.getPermissions().playerHas(player, Flags.leash, FlagCombo.OnlyFalse)) {
            this.plugin.msg(player, lm.Residence_FlagDeny, Flags.leash, res.getName());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWitherSpawn(CreatureSpawnEvent event) {
        // disabling event on world
        Entity ent = event.getEntity();
        if (ent == null) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(ent.getWorld())) {
            return;
        }

        if (ent.getType() != EntityType.WITHER) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLoc(event.getLocation());
        if (perms.has(Flags.witherspawn, FlagCombo.OnlyFalse)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // disabling event on world
        Entity ent = event.getEntity();
        if (ent == null) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(ent.getWorld())) {
            return;
        }
        FlagPermissions perms = this.plugin.getPermsByLoc(event.getLocation());
        if (this.plugin.getNms().isAnimal(ent)) {
            if (!perms.has(Flags.animals, true)) {
                event.setCancelled(true);
                return;
            }
            switch (event.getSpawnReason()) {
                case BUILD_WITHER:
                    break;
                case BUILD_IRONGOLEM:
                case BUILD_SNOWMAN:
                case CUSTOM:
                case DEFAULT:
                    if (perms.has(Flags.canimals, FlagCombo.OnlyFalse)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                case BREEDING:
                case CHUNK_GEN:
                case CURED:
                case DISPENSE_EGG:
                case EGG:
                case JOCKEY:
                case MOUNT:
                case VILLAGE_INVASION:
                case VILLAGE_DEFENSE:
                case NETHER_PORTAL:
                case OCELOT_BABY:
                case NATURAL:
                    if (perms.has(Flags.nanimals, FlagCombo.OnlyFalse)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                case SPAWNER_EGG:
                case SPAWNER:
                    if (perms.has(Flags.sanimals, FlagCombo.OnlyFalse)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                default:
                    break;
            }
        } else if (isMonster(ent)) {
            if (perms.has(Flags.monsters, FlagCombo.OnlyFalse)) {
                event.setCancelled(true);
                return;
            }
            switch (event.getSpawnReason()) {
                case BUILD_WITHER:
                case CUSTOM:
                case DEFAULT:
                    if (perms.has(Flags.cmonsters, FlagCombo.OnlyFalse)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                case CHUNK_GEN:
                case CURED:
                case DISPENSE_EGG:
                case INFECTION:
                case JOCKEY:
                case MOUNT:
                case NETHER_PORTAL:
                case SILVERFISH_BLOCK:
                case SLIME_SPLIT:
                case LIGHTNING:
                case NATURAL:
                    if (perms.has(Flags.nmonsters, FlagCombo.OnlyFalse)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                case SPAWNER_EGG:
                case SPAWNER:
                    if (perms.has(Flags.smonsters, FlagCombo.OnlyFalse)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {

        // disabling event on world
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(player.getWorld())) {
            return;
        }
        if (this.plugin.isResAdminOn(player)) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLocForPlayer(event.getEntity().getLocation(), player);
        if (!perms.playerHas(player, Flags.place, perms.playerHas(player, Flags.build, true))) {
            event.setCancelled(true);
            this.plugin.msg(player, lm.Flag_Deny, Flags.place);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (event.getEntity().getShooter() instanceof Player) {
            if (this.plugin.isResAdminOn((Player) event.getEntity().getShooter())) {
                return;
            }
        }
        FlagPermissions perms = this.plugin.getPermsByLoc(event.getEntity().getLocation());
        if (perms.has(Flags.shoot, FlagCombo.OnlyFalse)) {
            event.setCancelled(true);
            if (event.getEntity().getShooter() instanceof Player) {
                this.plugin.msg((Player) event.getEntity().getShooter(), lm.Flag_Deny, Flags.shoot);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        // disabling event on world
        Hanging ent = event.getEntity();
        if (ent == null) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(ent.getWorld())) {
            return;
        }

        if (!(event.getRemover() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getRemover();
        if (this.plugin.isResAdminOn(player)) {
            return;
        }

        if (this.plugin.getResidenceManager().isOwnerOfLocation(player, ent.getLocation())) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLocForPlayer(ent.getLocation(), player);
        if (!perms.playerHas(player, Flags.destroy, perms.playerHas(player, Flags.build, true))) {
            event.setCancelled(true);
            this.plugin.msg(player, lm.Flag_Deny, Flags.destroy);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreakEvent(HangingBreakEvent event) {
        // disabling event on world
        Hanging ent = event.getEntity();
        if (ent == null) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(ent.getWorld())) {
            return;
        }

        if (!event.getEntity().getType().equals(EntityType.ITEM_FRAME)) {
            return;
        }

        if (!event.getCause().equals(RemoveCause.PHYSICS)) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLoc(ent.getLocation());
        if (!perms.has(Flags.destroy, perms.has(Flags.build, true))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        // disabling event on world
        Hanging ent = event.getEntity();
        if (ent == null) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(ent.getWorld())) {
            return;
        }

        if (event.getRemover() instanceof Player) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLoc(ent.getLocation());
        if (!perms.has(Flags.destroy, perms.has(Flags.build, true))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityCombust(EntityCombustEvent event) {
        // disabling event on world
        Entity ent = event.getEntity();
        if (ent == null) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(ent.getWorld())) {
            return;
        }
        FlagPermissions perms = this.plugin.getPermsByLoc(ent.getLocation());
        if (!perms.has(Flags.burn, true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        // disabling event on world
        Entity ent = event.getEntity();
        if (ent == null) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(ent.getWorld())) {
            return;
        }
        EntityType entity = event.getEntityType();
        FlagPermissions perms = this.plugin.getPermsByLoc(ent.getLocation());

        switch (entity) {
            case CREEPER:
                if (!perms.has(Flags.creeper, perms.has(Flags.explode, true))) {
                    if (this.plugin.getConfigManager().isCreeperExplodeBelow()) {
                        if (ent.getLocation().getBlockY() >= this.plugin.getConfigManager().getCreeperExplodeBelowLevel()) {
                            event.setCancelled(true);
                            ent.remove();
                        } else {
                            ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(ent.getLocation());
                            if (res != null) {
                                event.setCancelled(true);
                                ent.remove();
                            }
                        }
                    } else {
                        event.setCancelled(true);
                        ent.remove();
                    }
                }
                break;
            case PRIMED_TNT:
            case MINECART_TNT:
                if (!perms.has(Flags.tnt, perms.has(Flags.explode, true))) {
                    if (this.plugin.getConfigManager().isTNTExplodeBelow()) {
                        if (ent.getLocation().getBlockY() >= this.plugin.getConfigManager().getTNTExplodeBelowLevel()) {
                            event.setCancelled(true);
                            ent.remove();
                        } else {
                            ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(ent.getLocation());
                            if (res != null) {
                                event.setCancelled(true);
                                ent.remove();
                            }
                        }
                    } else {
                        event.setCancelled(true);
                        ent.remove();
                    }
                }
                break;
            case SMALL_FIREBALL:
            case FIREBALL:
                if (perms.has(Flags.explode, FlagCombo.OnlyFalse) || perms.has(Flags.fireball, FlagCombo.OnlyFalse)) {
                    event.setCancelled(true);
                    ent.remove();
                }
                break;
            case WITHER_SKULL:
                if (perms.has(Flags.explode, FlagCombo.OnlyFalse) || perms.has(Flags.witherdestruction, FlagCombo.OnlyFalse)) {
                    event.setCancelled(true);
                    ent.remove();
                }
                break;
            case WITHER:
                break;
            default:
                if (perms.has(Flags.destroy, FlagCombo.OnlyFalse)) {
                    event.setCancelled(true);
                    ent.remove();
                }
                break;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        // disabling event on world
        Location loc = event.getLocation();
        if (this.plugin.isDisabledWorldListener(loc.getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }

        Entity ent = event.getEntity();

        Boolean cancel = false;
        Boolean remove = true;
        FlagPermissions perms = this.plugin.getPermsByLoc(loc);
        FlagPermissions world = this.plugin.getWorldFlags().getPerms(loc.getWorld().getName());

        if (ent != null) {
            switch (event.getEntityType()) {
                case CREEPER:
                    if (!perms.has(Flags.creeper, perms.has(Flags.explode, true))) {
                        if (this.plugin.getConfigManager().isCreeperExplodeBelow()) {
                            if (loc.getBlockY() >= this.plugin.getConfigManager().getCreeperExplodeBelowLevel()) {
                                cancel = true;
                            } else {
                                ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(loc);
                                if (res != null) {
                                    cancel = true;
                                }
                            }
                        } else {
                            cancel = true;
                        }
                    }
                    break;
                case PRIMED_TNT:
                case MINECART_TNT:
                    if (!perms.has(Flags.tnt, perms.has(Flags.explode, true))) {
                        if (this.plugin.getConfigManager().isTNTExplodeBelow()) {
                            if (loc.getBlockY() >= this.plugin.getConfigManager().getTNTExplodeBelowLevel()) {
                                cancel = true;
                            } else {
                                ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(loc);
                                if (res != null) {
                                    cancel = true;
                                }
                            }
                        } else {
                            cancel = true;
                        }
                    }
                    break;
                case SMALL_FIREBALL:
                case FIREBALL:
                    if (perms.has(Flags.explode, FlagCombo.OnlyFalse) || perms.has(Flags.fireball, FlagCombo.OnlyFalse)) {
                        cancel = true;
                    }
                    break;
                case WITHER:
                case WITHER_SKULL:
                    if (perms.has(Flags.explode, FlagCombo.OnlyFalse) || perms.has(Flags.witherdestruction, FlagCombo.OnlyFalse)) {
                        cancel = true;
                    }
                    break;
                case ENDER_DRAGON:
                    remove = false;
                    break;
                default:
                    if (!perms.has(Flags.destroy, world.has(Flags.destroy, true))) {
                        cancel = true;
                        remove = false;
                    }
                    break;
            }
        } else if (!perms.has(Flags.destroy, world.has(Flags.destroy, true))) {
            cancel = true;
        }

        if (cancel) {
            event.setCancelled(true);
            if (ent != null && remove) {
                if (!event.getEntityType().equals(EntityType.WITHER)) {
                    ent.remove();
                }
            }
            return;
        }

        List<Block> preserve = new ArrayList<Block>();
        for (Block block : event.blockList()) {
            FlagPermissions blockperms = this.plugin.getPermsByLoc(block.getLocation());

            if (ent != null) {
                switch (event.getEntityType()) {
                    case CREEPER:
                        if (!blockperms.has(Flags.creeper, blockperms.has(Flags.explode, true))) {
                            if (this.plugin.getConfigManager().isCreeperExplodeBelow()) {
                                if (block.getY() >= this.plugin.getConfigManager().getCreeperExplodeBelowLevel()) {
                                    preserve.add(block);
                                } else {
                                    ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(block.getLocation());
                                    if (res != null) {
                                        preserve.add(block);
                                    }
                                }
                            } else {
                                preserve.add(block);
                            }
                        }
                        continue;
                    case PRIMED_TNT:
                    case MINECART_TNT:
                        if (!blockperms.has(Flags.tnt, blockperms.has(Flags.explode, true))) {
                            if (this.plugin.getConfigManager().isTNTExplodeBelow()) {
                                if (block.getY() >= this.plugin.getConfigManager().getTNTExplodeBelowLevel()) {
                                    preserve.add(block);
                                } else {
                                    ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(block.getLocation());
                                    if (res != null) {
                                        preserve.add(block);
                                    }
                                }
                            } else {
                                preserve.add(block);
                            }
                        }
                        continue;
                    case ENDER_DRAGON:
                        if (blockperms.has(Flags.dragongrief, FlagCombo.OnlyFalse)) {
                            preserve.add(block);
                        }
                        break;
                    case ENDER_CRYSTAL:
                        if (blockperms.has(Flags.explode, FlagCombo.OnlyFalse)) {
                            preserve.add(block);
                        }
                        continue;
                    case SMALL_FIREBALL:
                    case FIREBALL:
                        if (blockperms.has(Flags.explode, FlagCombo.OnlyFalse) || perms.has(Flags.fireball, FlagCombo.OnlyFalse)) {
                            preserve.add(block);
                        }
                        continue;
                    case WITHER:
                    case WITHER_SKULL:
                        if (blockperms.has(Flags.explode, FlagCombo.OnlyFalse) || blockperms.has(Flags.witherdestruction, FlagCombo.OnlyFalse)) {
                            preserve.add(block);
                        }
                        break;
                    default:
                        if (blockperms.has(Flags.destroy, FlagCombo.OnlyFalse)) {
                            preserve.add(block);
                        }
                        continue;
                }
            } else {
                if (!blockperms.has(Flags.destroy, world.has(Flags.destroy, true))) {
                    preserve.add(block);
                }
            }
        }

        for (Block block : preserve) {
            event.blockList().remove(block);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSplashPotion(EntityChangeBlockEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }

        Entity ent = event.getEntity();

        if (ent.getType() != EntityType.WITHER) {
            return;
        }

        if (!this.plugin.getPermsByLoc(ent.getLocation()).has(Flags.witherdestruction, FlagCombo.OnlyFalse)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSplashPotion(PotionSplashEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }

        ProjectileSource shooter = event.getPotion().getShooter();

        if (shooter instanceof Witch) {
            return;
        }

        boolean harmfull = false;
        mein:
        for (PotionEffect one : event.getPotion().getEffects()) {
            for (String oneHarm : this.plugin.getConfigManager().getNegativePotionEffects()) {
                if (oneHarm.equalsIgnoreCase(one.getType().getName())) {
                    harmfull = true;
                    break mein;
                }
            }
        }
        if (!harmfull) {
            return;
        }

        Entity ent = event.getEntity();
        boolean srcpvp = this.plugin.getPermsByLoc(ent.getLocation()).has(Flags.pvp, FlagCombo.TrueOrNone);
        Iterator<LivingEntity> it = event.getAffectedEntities().iterator();
        while (it.hasNext()) {
            LivingEntity target = it.next();
            if (target.getType() != EntityType.PLAYER) {
                continue;
            }
            Boolean tgtpvp = this.plugin.getPermsByLoc(target.getLocation()).has(Flags.pvp, FlagCombo.TrueOrNone);
            if (!srcpvp || !tgtpvp) {
                event.setIntensity(target, 0);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerKillingByFlame(EntityCombustByEntityEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity == null) {
            return;
        }
        if (!(entity instanceof Player)) {
            return;
        }

        ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(entity.getLocation());

        if (res == null) {
            return;
        }

        Entity damager = event.getCombuster();

        if (!(damager instanceof Arrow) && !(damager instanceof Player)) {
            return;
        }

        if (damager instanceof Arrow && !(((Arrow) damager).getShooter() instanceof Player)) {
            return;
        }

        Player cause = null;

        if (damager instanceof Player) {
            cause = (Player) damager;
        } else {
            cause = (Player) ((Arrow) damager).getShooter();
        }

        if (cause == null) {
            return;
        }

        if (this.plugin.isResAdminOn(cause)) {
            return;
        }

        Boolean srcpvp = this.plugin.getPermsByLoc(cause.getLocation()).has(Flags.pvp, FlagCombo.TrueOrNone);
        Boolean tgtpvp = this.plugin.getPermsByLoc(entity.getLocation()).has(Flags.pvp, FlagCombo.TrueOrNone);
        if (!srcpvp || !tgtpvp) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnFallDamage(EntityDamageEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        if (event.getCause() != DamageCause.FALL) {
            return;
        }
        Entity ent = event.getEntity();
        if (!(ent instanceof Player)) {
            return;
        }

        if (!this.plugin.getPermsByLoc(ent.getLocation()).has(Flags.falldamage, FlagCombo.TrueOrNone)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnArmorStandFlameDamage(EntityDamageEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        if (event.getCause() != DamageCause.FIRE_TICK) {
            return;
        }
        Entity ent = event.getEntity();
        if (!this.plugin.getNms().isArmorStandEntity(ent.getType()) && !(ent instanceof Arrow)) {
            return;
        }

        if (!this.plugin.getPermsByLoc(ent.getLocation()).has(Flags.destroy, true)) {
            event.setCancelled(true);
            ent.setFireTicks(0);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityCatchingFire(EntityDamageByEntityEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }

        if (event.getEntity() == null || !(event.getEntity() instanceof Player)) {
            return;
        }

        Arrow arrow = (Arrow) event.getDamager();

        FlagPermissions perms = this.plugin.getPermsByLoc(arrow.getLocation());

        if (!perms.has(Flags.pvp, FlagCombo.TrueOrNone)) {
            arrow.setFireTicks(0);
        }
    }

    @EventHandler
    public void OnPlayerDamageByLightning(EntityDamageEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        if (event.getCause() != DamageCause.LIGHTNING) {
            return;
        }
        Entity ent = event.getEntity();
        if (!(ent instanceof Player)) {
            return;
        }
        if (!this.plugin.getPermsByLoc(ent.getLocation()).has(Flags.pvp, FlagCombo.TrueOrNone)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByFireballEvent(EntityDamageByEntityEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }

        Entity dmgr = event.getDamager();
        if (dmgr.getType() != EntityType.SMALL_FIREBALL && dmgr.getType() != EntityType.FIREBALL) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLoc(event.getEntity().getLocation());
        if (perms.has(Flags.fireball, FlagCombo.OnlyFalse)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByWitherEvent(EntityDamageByEntityEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }

        Entity dmgr = event.getDamager();
        if (dmgr.getType() != EntityType.WITHER && dmgr.getType() != EntityType.WITHER_SKULL) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLoc(event.getEntity().getLocation());
        if (perms.has(Flags.witherdamage, FlagCombo.OnlyFalse)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }

        if (event.getEntityType() != EntityType.ENDER_CRYSTAL && event.getEntityType() != EntityType.ITEM_FRAME && !this.plugin.getNms()
            .isArmorStandEntity(event
                .getEntityType())) {
            return;
        }

        Entity dmgr = event.getDamager();

        Player player = null;
        if (dmgr instanceof Player) {
            player = (Player) event.getDamager();
        } else if (dmgr instanceof Projectile && ((Projectile) dmgr).getShooter() instanceof Player) {
            player = (Player) ((Projectile) dmgr).getShooter();
        } else if ((dmgr instanceof Projectile) && (!(((Projectile) dmgr).getShooter() instanceof Player))) {
            Location loc = event.getEntity().getLocation();
            FlagPermissions perm = this.plugin.getPermsByLoc(loc);
            if (perm.has(Flags.destroy, FlagCombo.OnlyFalse)) {
                event.setCancelled(true);
            }
            return;
        } else if (dmgr.getType() == EntityType.PRIMED_TNT || dmgr.getType() == EntityType.MINECART_TNT) {
            FlagPermissions perms = this.plugin.getPermsByLoc(event.getEntity().getLocation());
            if (perms.has(Flags.explode, FlagCombo.OnlyFalse)) {
                event.setCancelled(true);
                return;
            }
        } else if (dmgr.getType() == EntityType.WITHER_SKULL || dmgr.getType() == EntityType.WITHER) {
            FlagPermissions perms = this.plugin.getPermsByLoc(event.getEntity().getLocation());
            if (perms.has(Flags.witherdamage, FlagCombo.OnlyFalse)) {
                event.setCancelled(true);
                return;
            }
        }

        Location loc = event.getEntity().getLocation();
        ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(loc);
        if (res == null) {
            return;
        }

        if (isMonster(dmgr) && !res.getPermissions().has(Flags.destroy, false)) {
            event.setCancelled(true);
            return;
        }

        if (player == null) {
            return;
        }

        if (this.plugin.isResAdminOn(player)) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLocForPlayer(loc, player);

        if (event.getEntityType() == EntityType.ITEM_FRAME) {
            ItemFrame it = (ItemFrame) event.getEntity();
            if (it.getItem() != null) {
                if (!perms.playerHas(player, Flags.container, true)) {
                    event.setCancelled(true);
                    this.plugin.msg(player, lm.Flag_Deny, Flags.container);
                }
                return;
            }
        }

        if (!perms.playerHas(player, Flags.destroy, perms.playerHas(player, Flags.build, true))) {
            event.setCancelled(true);
            this.plugin.msg(player, lm.Flag_Deny, Flags.destroy.getName());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getEntity().getWorld())) {
            return;
        }
        Entity ent = event.getEntity();
        if (ent.hasMetadata("NPC")) {
            return;
        }

        boolean tamedAnimal = isTamed(ent);
        ClaimedResidence area = this.plugin.getResidenceManager().getByLoc(ent.getLocation());
        /* Living Entities */
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent attackevent = (EntityDamageByEntityEvent) event;
            Entity damager = attackevent.getDamager();

            if (area != null && ent instanceof Player && damager instanceof Player) {
                if (area.getPermissions().has(Flags.overridepvp, false) || this.plugin.getConfigManager().isOverridePvp() && area.getPermissions()
                    .has(Flags.pvp,
                        FlagCombo.OnlyFalse)) {
                    Player player = (Player) event.getEntity();
                    Damageable damage = player;
                    damage.damage(event.getDamage());
                    damage.setVelocity(damager.getLocation().getDirection());
                    event.setCancelled(true);
                    return;
                }
            }

            ClaimedResidence srcarea = null;
            if (damager != null) {
                srcarea = this.plugin.getResidenceManager().getByLoc(damager.getLocation());
            }
            boolean srcpvp = true;
            boolean allowSnowBall = false;
            boolean isSnowBall = false;
            boolean isOnFire = false;
            if (srcarea != null) {
                srcpvp = srcarea.getPermissions().has(Flags.pvp, FlagCombo.TrueOrNone);
            }
            ent = attackevent.getEntity();
            if ((ent instanceof Player || tamedAnimal) && (damager instanceof Player || (damager instanceof Projectile && (((Projectile) damager)
                                                                                                                               .getShooter() instanceof Player)))
                && event.getCause() != DamageCause.FALL) {
                Player attacker = null;
                if (damager instanceof Player) {
                    attacker = (Player) damager;
                } else if (damager instanceof Projectile) {
                    Projectile project = (Projectile) damager;
                    if (project.getType() == EntityType.SNOWBALL && srcarea != null) {
                        isSnowBall = true;
                        allowSnowBall = srcarea.getPermissions().has(Flags.snowball, FlagCombo.TrueOrNone);
                    }
                    if (project.getFireTicks() > 0) {
                        isOnFire = true;
                    }

                    attacker = (Player) ((Projectile) damager).getShooter();
                }

                if (!(ent instanceof Player)) {
                    return;
                }

                if (!srcpvp && !isSnowBall || !allowSnowBall && isSnowBall) {
                    if (attacker != null) {
                        this.plugin.msg(attacker, lm.General_NoPVPZone);
                    }
                    if (isOnFire) {
                        ent.setFireTicks(0);
                    }
                    event.setCancelled(true);
                    return;
                }

                /* Check for Player vs Player */
                if (area == null) {
                    /* World PvP */
                    if (damager != null) {
                        if (!this.plugin.getWorldFlags().getPerms(damager.getWorld().getName()).has(Flags.pvp, FlagCombo.TrueOrNone)) {
                            if (attacker != null) {
                                this.plugin.msg(attacker, lm.General_WorldPVPDisabled);
                            }
                            if (isOnFire) {
                                ent.setFireTicks(0);
                            }
                            event.setCancelled(true);
                            return;
                        }
                    }

                    /* Attacking from safe zone */
                    if (attacker != null) {
                        FlagPermissions aPerm = this.plugin.getPermsByLoc(attacker.getLocation());
                        if (!aPerm.has(Flags.pvp, FlagCombo.TrueOrNone)) {
                            this.plugin.msg(attacker, lm.General_NoPVPZone);
                            if (isOnFire) {
                                ent.setFireTicks(0);
                            }
                            event.setCancelled(true);
                            return;
                        }
                    }
                } else {
                    /* Normal PvP */
                    if (!isSnowBall && !area.getPermissions().has(Flags.pvp, FlagCombo.TrueOrNone) || isSnowBall && !allowSnowBall) {
                        if (attacker != null) {
                            this.plugin.msg(attacker, lm.General_NoPVPZone);
                        }
                        if (isOnFire) {
                            ent.setFireTicks(0);
                        }
                        event.setCancelled(true);
                        return;
                    }
                }
                return;
            } else if ((ent instanceof Player || tamedAnimal) && (damager instanceof Creeper)) {
                if (area == null && !this.plugin.getWorldFlags().getPerms(damager.getWorld().getName()).has(Flags.creeper, true)) {
                    event.setCancelled(true);
                } else if (area != null && !area.getPermissions().has(Flags.creeper, true)) {
                    event.setCancelled(true);
                }
            }
        }
        if (area == null) {
            if (!this.plugin.getWorldFlags().getPerms(ent.getWorld().getName()).has(Flags.damage, true) && (ent instanceof Player || tamedAnimal)) {
                event.setCancelled(true);
            }
        } else {
            if (!area.getPermissions().has(Flags.damage, true) && (ent instanceof Player || tamedAnimal)) {
                event.setCancelled(true);
            }
        }
        if (event.isCancelled()) {
            /* Put out a fire on a player */
            if ((ent instanceof Player || tamedAnimal) && (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event
                                                                                                                         .getCause()
                                                                                                                     == EntityDamageEvent.DamageCause.FIRE_TICK)) {
                ent.setFireTicks(0);
            }
        }
    }

    private static boolean isTamed(Entity ent) {
        return (ent instanceof Tameable ? ((Tameable) ent).isTamed() : false);
    }
}
