package com.gabizou.residency.listeners;

import com.gabizou.residency.Residence;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.ResidencePlayer;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.permissions.PermissionGroup;
import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.FlagPermissions;
import com.gabizou.residency.protection.FlagPermissions.FlagCombo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.util.Tristate;

import java.util.ArrayList;
import java.util.List;

public class ResidenceBlockListener {

    public static final String SourceResidenceName = "SourceResidenceName";
    private List<String> MessageInformed = new ArrayList<String>();
    private List<String> ResCreated = new ArrayList<String>();
    private Residence plugin;

    public ResidenceBlockListener(Residence residence) {
        this.plugin = residence;
    }

    @SuppressWarnings("deprecation")
    @Listener
    public void onAnvilInventoryClick(ClickInventoryEvent e) {
        Inventory inv = e.getInventory();

        try {
            if (inv == null || inv.getType() != InventoryType.ANVIL || e.getInventory().getLocation() == null) {
                return;
            }
        } catch (Exception | NoSuchMethodError ex) {
            return;
        }
        Block b = e.getInventory().getLocation().getBlock();
        if (b == null || b.getType() != Material.ANVIL) {
            return;
        }

        ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(e.getInventory().getLocation());
        if (res == null) {
            return;
        }
        // Fix anvil only when item is picked up
        if (e.getRawSlot() != 2) {
            return;
        }
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (!res.getPermissions().has(Flags.anvilbreak, FlagCombo.OnlyFalse)) {
            return;
        }

        b.setData((byte) 1);
    }

    @Listener
    @IsCancelled(Tristate.FALSE)
    public void onBlockChange(ChangeBlockEvent.Post event) {
        final Cause cause = event.getCause();
        event.getTransactions().stream()
            .filter(Transaction::isValid)
            .filter(transaction -> !this.plugin.isDisabledWorldListener(transaction.getOriginal().getWorldUniqueId()))
            .forEach(transaction -> {
                Flags flag = Flags.retrieveFlag(transaction, cause);
                if (!this.plugin.getPermsByLoc(transaction).has(flag, true)) {
                    transaction.setValid(false);
                }
            });

    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlantGrow(BlockGrowEvent event) {
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
        if (!perms.has(Flags.grow, true)) {
            event.setCancelled(true);
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onVineGrow(BlockSpreadEvent event) {
        if (event.getSource().getType() != Material.VINE) {
            return;
        }
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
        if (!perms.has(Flags.grow, true)) {
            event.setCancelled(true);
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onleaveDecay(LeavesDecayEvent event) {
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
        if (!perms.has(Flags.decay, true)) {
            event.setCancelled(true);
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onTreeGrowt(StructureGrowEvent event) {
        if (this.plugin.isDisabledWorldListener(event.getWorld())) {
            return;
        }
        FlagPermissions perms = this.plugin.getPermsByLoc(event.getLocation());
        if (!perms.has(Flags.grow, true)) {
            event.setCancelled(true);
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onTreeGrow(StructureGrowEvent event) {

        if (this.plugin.isDisabledWorldListener(event.getWorld())) {
            return;
        }

        ClaimedResidence startRes = this.plugin.getResidenceManager().getByLoc(event.getLocation());
        List<BlockState> blocks = event.getBlocks();
        int i = 0;
        for (BlockState one : blocks) {
            ClaimedResidence targetRes = this.plugin.getResidenceManager().getByLoc(one.getLocation());
            if (startRes == null && targetRes != null ||
                targetRes != null && startRes != null && !startRes.getName().equals(targetRes.getName()) && !startRes.isOwner(targetRes.getOwner())) {
                BlockState matas = blocks.get(i);
                matas.setType(Material.AIR);
                blocks.set(i, matas);
            }
            i++;
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        Player player = event.getPlayer();
        if (this.plugin.isResAdminOn(player)) {
            return;
        }

        Block block = event.getBlock();
        Material mat = block.getType();
        String world = block.getWorld().getName();

        ResidencePlayer resPlayer = this.plugin.getPlayerManager().getResidencePlayer(player);
        PermissionGroup group = resPlayer.getGroup();
        if (this.plugin.getItemManager().isIgnored(mat, group, world)) {
            return;
        }
        ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(block.getLocation());
        if (this.plugin.getConfigManager().enabledRentSystem() && res != null) {
            String resname = res.getName();
            if (this.plugin.getConfigManager().preventRentModify() && this.plugin.getRentManager().isRented(resname)) {
                this.plugin.msg(player, lm.Rent_ModifyDeny);
                event.setCancelled(true);
                return;
            }
        }
        FlagPermissions perms = this.plugin.getPermsByLocForPlayer(block.getLocation(), player);
        if (res != null && res.getItemIgnoreList().isListed(mat)) {
            return;
        }

        boolean hasdestroy = perms.playerHas(player, Flags.destroy, perms.playerHas(player, Flags.build, true));
        boolean hasContainer = perms.playerHas(player, Flags.container, true);
        if (!hasdestroy && !player.hasPermission("residence.bypass.destroy")) {
            this.plugin.msg(player, lm.Flag_Deny, Flags.destroy);
            event.setCancelled(true);
        } else if (!hasContainer && mat == Material.CHEST) {
            this.plugin.msg(player, lm.Flag_Deny, Flags.container);
            event.setCancelled(true);
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        if (!(event instanceof EntityBlockFormEvent)) {
            return;
        }

        if (((EntityBlockFormEvent) event).getEntity() instanceof Snowman) {
            FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
            if (!perms.has(Flags.snowtrail, true)) {
                event.setCancelled(true);
            }
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onIceForm(BlockFormEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }

        Material ice = Material.getMaterial("FROSTED_ICE");

        if (event.getNewState().getType() != Material.SNOW && event.getNewState().getType() != Material.ICE && ice != null && ice != event
            .getNewState().getType()) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
        if (!perms.has(Flags.iceform, true)) {
            event.setCancelled(true);
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onIceMelt(BlockFadeEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }

        if (event.getNewState().getType() != Material.STATIONARY_WATER && event.getBlock().getState().getType() != Material.SNOW
            && event.getBlock().getState()
                   .getType() != Material.SNOW_BLOCK) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
        if (!perms.has(Flags.icemelt, true)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() != EntityType.FALLING_BLOCK) {
            return;
        }
        Entity ent = event.getEntity();
        if (!ent.hasMetadata(SourceResidenceName)) {
            ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(ent.getLocation());
            String resName = res == null ? "NULL" : res.getName();
            ent.setMetadata(SourceResidenceName, new FixedMetadataValue(this.plugin, resName));
        } else {
            String saved = ent.getMetadata(SourceResidenceName).get(0).asString();
            ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(ent.getLocation());

            if (res != null && res.getPermissions().has(Flags.fallinprotection, FlagCombo.OnlyFalse)) {
                return;
            }

            String resName = res == null ? "NULL" : res.getName();
            if (!saved.equalsIgnoreCase(resName)) {
                event.setCancelled(true);
                ent.remove();
            }
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockFall(EntityChangeBlockEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        if (!this.plugin.getConfigManager().isBlockFall()) {
            return;
        }

        if ((event.getEntityType() != EntityType.FALLING_BLOCK)) {
            return;
        }

        if (event.getTo().hasGravity()) {
            return;
        }

        Block block = event.getBlock();

        if (block == null) {
            return;
        }

        if (!this.plugin.getConfigManager().getBlockFallWorlds().contains(block.getLocation().getWorld().getName())) {
            return;
        }

        if (block.getY() <= this.plugin.getConfigManager().getBlockFallLevel()) {
            return;
        }

        ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(block.getLocation());
        Location loc = new Location(block.getLocation().getWorld(), block.getX(), block.getY(), block.getZ());
        for (int i = loc.getBlockY() - 1; i >= this.plugin.getConfigManager().getBlockFallLevel() - 1; i--) {
            loc.setY(i);
            if (loc.getBlock().getType() != Material.AIR) {
                ClaimedResidence targetRes = this.plugin.getResidenceManager().getByLoc(loc);
                if (targetRes == null) {
                    continue;
                }
                if (res != null && !res.getName().equals(targetRes.getName())) {
                    if (targetRes.getPermissions().has(Flags.fallinprotection, FlagCombo.OnlyFalse)) {
                        continue;
                    }
                    event.setCancelled(true);
                    block.setType(Material.AIR);
                }
                return;
            }
        }
    }

    @Listener(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChestPlace(BlockPlaceEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        if (!this.plugin.getConfigManager().ShowNoobMessage()) {
            return;
        }

        Player player = event.getPlayer();
        if (this.plugin.isResAdminOn(player)) {
            return;
        }
        Block block = event.getBlock();
        if (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) {
            return;
        }

        if (!player.hasPermission("residence.newguyresidence")) {
            return;
        }

        ArrayList<String> list = this.plugin.getPlayerManager().getResidenceList(player.getName());
        if (list.size() != 0) {
            return;
        }

        if (this.MessageInformed.contains(player.getName())) {
            return;
        }

        this.plugin.msg(player, lm.General_NewPlayerInfo);

        this.MessageInformed.add(player.getName());
    }

    @Listener(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChestPlaceCreateRes(BlockPlaceEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        if (!this.plugin.getConfigManager().isNewPlayerUse()) {
            return;
        }

        Player player = event.getPlayer();
        if (this.plugin.isResAdminOn(player)) {
            return;
        }
        Block block = event.getBlock();
        if (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) {
            return;
        }

        ArrayList<String> list = this.plugin.getPlayerManager().getResidenceList(player.getName());
        if (list.size() != 0) {
            return;
        }

        if (this.ResCreated.contains(player.getName())) {
            return;
        }

        Location loc = block.getLocation();

        this.plugin.getSelectionManager()
            .placeLoc1(player, new Location(loc.getWorld(), loc.getBlockX() - this.plugin.getConfigManager().getNewPlayerRangeX(), loc
                                                                                                                                       .getBlockY()
                                                                                                                                   - this.plugin
                                                                                                                                       .getConfigManager()
                                                                                                                                       .getNewPlayerRangeY(),
                loc.getBlockZ() - this.plugin.getConfigManager().getNewPlayerRangeZ()), true);
        this.plugin.getSelectionManager()
            .placeLoc2(player, new Location(loc.getWorld(), loc.getBlockX() + this.plugin.getConfigManager().getNewPlayerRangeX(), loc
                                                                                                                                       .getBlockY()
                                                                                                                                   + this.plugin
                                                                                                                                       .getConfigManager()
                                                                                                                                       .getNewPlayerRangeY(),
                loc.getBlockZ() + this.plugin.getConfigManager().getNewPlayerRangeZ()), true);

        boolean created = this.plugin.getResidenceManager().addResidence(player, player.getName(),
            this.plugin.getSelectionManager().getPlayerLoc1(player.getName()),
            this.plugin.getSelectionManager().getPlayerLoc2(player.getName()), this.plugin.getConfigManager().isNewPlayerFree());
        if (created) {
            this.ResCreated.add(player.getName());
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        Player player = event.getPlayer();
        if (this.plugin.isResAdminOn(player)) {
            return;
        }
        Block block = event.getBlock();
        Material mat = block.getType();
        String world = block.getWorld().getName();

        ResidencePlayer resPlayer = this.plugin.getPlayerManager().getResidencePlayer(player);
        PermissionGroup group = resPlayer.getGroup();
        if (this.plugin.getItemManager().isIgnored(mat, group, world)) {
            return;
        }
        ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(block.getLocation());
        if (this.plugin.getConfigManager().enabledRentSystem() && res != null) {
            String resname = res.getName();
            if (this.plugin.getConfigManager().preventRentModify() && this.plugin.getRentManager().isRented(resname)) {
                this.plugin.msg(player, lm.Rent_ModifyDeny);
                event.setCancelled(true);
                return;
            }
        }
        if (res != null && !res.getItemBlacklist().isAllowed(mat)) {
            this.plugin.msg(player, lm.General_ItemBlacklisted);
            event.setCancelled(true);
            return;
        }
        FlagPermissions perms = this.plugin.getPermsByLocForPlayer(block.getLocation(), player);
        boolean hasplace = perms.playerHas(player, Flags.place, perms.playerHas(player, Flags.build, true));
        if (!hasplace && !player.hasPermission("residence.bypass.build")) {
            event.setCancelled(true);
            this.plugin.msg(player, lm.Flag_Deny, Flags.place.getName());
            return;
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        Location loc = event.getBlock().getLocation();
        FlagPermissions perms = this.plugin.getPermsByLoc(loc);
        if (!perms.has(Flags.spread, true)) {
            event.setCancelled(true);
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
        if (!perms.has(Flags.piston, true)) {
            event.setCancelled(true);
            return;
        }

        List<Block> blocks = this.plugin.getNms().getPistonRetractBlocks(event);

        if (!event.isSticky()) {
            return;
        }

        ClaimedResidence pistonRes = this.plugin.getResidenceManager().getByLoc(event.getBlock().getLocation());

        for (Block block : blocks) {
            Location locFrom = block.getLocation();
            ClaimedResidence blockFrom = this.plugin.getResidenceManager().getByLoc(locFrom);
            if (blockFrom == null) {
                continue;
            }
            if (blockFrom == pistonRes) {
                continue;
            }
            if (pistonRes != null && blockFrom.isOwner(pistonRes.getOwner())) {
                continue;
            }
            if (!blockFrom.getPermissions().has(Flags.pistonprotection, FlagCombo.OnlyTrue)) {
                continue;
            }
            event.setCancelled(true);
            break;
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
        if (!perms.has(Flags.piston, true)) {
            event.setCancelled(true);
            return;
        }

        ClaimedResidence pistonRes = this.plugin.getResidenceManager().getByLoc(event.getBlock().getLocation());

        BlockFace dir = event.getDirection();
        for (Block block : event.getBlocks()) {
            Location locFrom = block.getLocation();
            Location locTo = new Location(block.getWorld(), block.getX() + dir.getModX(), block.getY() + dir.getModY(), block.getZ() + dir.getModZ());
            ClaimedResidence blockFrom = this.plugin.getResidenceManager().getByLoc(locFrom);
            ClaimedResidence blockTo = this.plugin.getResidenceManager().getByLoc(locTo);

            if (pistonRes == null && blockTo != null && blockTo.getPermissions().has(Flags.pistonprotection, FlagCombo.OnlyTrue)) {
                event.setCancelled(true);
                return;
            } else if (blockTo != null && blockFrom == null && blockTo.getPermissions().has(Flags.pistonprotection, FlagCombo.OnlyTrue)) {
                event.setCancelled(true);
                return;
            } else if (blockTo != null && blockFrom != null && (pistonRes != null && !blockTo.isOwner(pistonRes.getOwner()) || !blockTo
                .isOwner(blockFrom.getOwner()))
                       && blockTo.getPermissions().has(Flags.pistonprotection, FlagCombo.OnlyTrue)) {
                event.setCancelled(true);
                return;
            }

        }

    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }

        ClaimedResidence fromRes = this.plugin.getResidenceManager().getByLoc(event.getBlock().getLocation());
        ClaimedResidence toRes = this.plugin.getResidenceManager().getByLoc(event.getToBlock().getLocation());

        FlagPermissions perms = this.plugin.getPermsByLoc(event.getToBlock().getLocation());
        boolean hasflow = perms.has(Flags.flow, FlagCombo.TrueOrNone);
        Material mat = event.getBlock().getType();

        if (perms.has(Flags.flowinprotection, FlagCombo.TrueOrNone)) {
            if (fromRes == null && toRes != null || fromRes != null && toRes != null && !fromRes.equals(toRes) && !fromRes
                .isOwner(toRes.getOwner())) {
                event.setCancelled(true);
                return;
            }
        }

        if (perms.has(Flags.flow, FlagCombo.OnlyFalse)) {
            event.setCancelled(true);
            return;
        }

        if (mat == Material.LAVA || mat == Material.STATIONARY_LAVA) {
            if (!perms.has(Flags.lavaflow, hasflow)) {
                event.setCancelled(true);
            }
            return;
        }
        if (mat == Material.WATER || mat == Material.STATIONARY_WATER) {
            if (!perms.has(Flags.waterflow, hasflow)) {
                event.setCancelled(true);
            }
            return;
        }
    }

    @SuppressWarnings({"deprecation"})
    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLandDryFade(BlockFadeEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }

        Material mat = event.getBlock().getType();
        if (mat != Material.SOIL) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLoc(event.getNewState().getLocation());
        if (!perms.has(Flags.dryup, true)) {
            event.getBlock().setData((byte) 7);
            event.setCancelled(true);
            return;
        }
    }

    @SuppressWarnings("deprecation")
    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLandDryPhysics(BlockPhysicsEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }

        Material mat = event.getBlock().getType();
        if (mat != Material.SOIL) {
            return;
        }

        FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
        if (!perms.has(Flags.dryup, true)) {
            event.getBlock().setData((byte) 7);
            event.setCancelled(true);
            return;
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDispense(BlockDispenseEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }

        Location
            location =
            new Location(event.getBlock().getWorld(), event.getVelocity().getBlockX(), event.getVelocity().getBlockY(),
                event.getVelocity().getBlockZ());

        ClaimedResidence targetres = this.plugin.getResidenceManager().getByLoc(location);

        if (targetres == null && location.getBlockY() >= this.plugin.getConfigManager().getPlaceLevel() && this.plugin.getConfigManager()
            .getNoPlaceWorlds().contains(location
                .getWorld().getName())) {
            ItemStack mat = event.getItem();
            if (this.plugin.getConfigManager().isNoLavaPlace()) {
                if (mat.getType() == Material.LAVA_BUCKET) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (this.plugin.getConfigManager().isNoWaterPlace()) {
                if (mat.getType() == Material.WATER_BUCKET) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        ClaimedResidence sourceres = this.plugin.getResidenceManager().getByLoc(event.getBlock().getLocation());

        if ((sourceres == null && targetres != null || sourceres != null && targetres == null || sourceres != null && targetres != null && !sourceres
            .getName().equals(
                targetres.getName())) && (event.getItem().getType() == Material.LAVA_BUCKET || event.getItem().getType() == Material.WATER_BUCKET)) {
            event.setCancelled(true);
        }
    }

    @Listener(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLavaWaterFlow(BlockFromToEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        Material mat = event.getBlock().getType();

        Location location = event.getToBlock().getLocation();
        if (!this.plugin.getConfigManager().getNoFlowWorlds().contains(location.getWorld().getName())) {
            return;
        }

        if (location.getBlockY() < this.plugin.getConfigManager().getFlowLevel()) {
            return;
        }

        ClaimedResidence res = this.plugin.getResidenceManager().getByLoc(location);

        if (res != null) {
            return;
        }

        if (this.plugin.getConfigManager().isNoLava()) {
            if (mat == Material.LAVA || mat == Material.STATIONARY_LAVA) {
                event.setCancelled(true);
                return;
            }
        }

        if (this.plugin.getConfigManager().isNoWater()) {
            if (mat == Material.WATER || mat == Material.STATIONARY_WATER) {
                event.setCancelled(true);
                return;
            }
        }
    }


    @Listener
    @IsCancelled()
    public void onBlockBurn(BlockBurnEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
        if (!perms.has(Flags.firespread, true)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onBlockIgnite(BlockIgniteEvent event) {
        // disabling event on world
        if (this.plugin.isDisabledWorldListener(event.getBlock().getWorld())) {
            return;
        }
        IgniteCause cause = event.getCause();
        if (cause == IgniteCause.SPREAD) {
            FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
            if (!perms.has(Flags.firespread, true)) {
                event.setCancelled(true);
            }
        } else if (cause == IgniteCause.FLINT_AND_STEEL) {
            Player player = event.getPlayer();
            FlagPermissions perms = this.plugin.getPermsByLocForPlayer(event.getBlock().getLocation(), player);
            if (player != null && !perms.playerHas(player, Flags.ignite, true) && !this.plugin.isResAdminOn(player)) {
                event.setCancelled(true);
                this.plugin.msg(player, lm.Flag_Deny, Flags.ignite.getName());
            }
        } else {
            FlagPermissions perms = this.plugin.getPermsByLoc(event.getBlock().getLocation());
            if (!perms.has(Flags.ignite, true)) {
                event.setCancelled(true);
            }
        }
    }
}
