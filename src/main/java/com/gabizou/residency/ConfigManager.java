package com.gabizou.residency;

import com.gabizou.residency.containers.ConfigReader;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.RandomTeleport;
import com.gabizou.residency.protection.FlagPermissions;
import com.gabizou.residency.utils.ParticleEffects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigManager {

    // DynMap
    public boolean DynMapUse;
    public boolean DynMapShowFlags;
    public boolean DynMapHideHidden;
    public boolean DynMapLayer3dRegions;
    public int DynMapLayerSubZoneDepth;
    public String DynMapBorderColor;
    public double DynMapBorderOpacity;
    public int DynMapBorderWeight;
    public String DynMapFillColor;
    public double DynMapFillOpacity;
    public String DynMapFillForRent;
    public String DynMapFillRented;
    public String DynMapFillForSale;
    public List<String> DynMapVisibleRegions;
    public List<String> DynMapHiddenRegions;
    // Schematics
    public boolean RestoreAfterRentEnds;
    public boolean SchematicsSaveOnFlagChange;
    // Global chat
    public boolean GlobalChatEnabled;
    public boolean GlobalChatSelfModify;
    public String GlobalChatFormat;
    protected String defaultGroup;
    protected boolean useLeases;
    protected boolean ResMoneyBack;
    protected boolean enableEconomy;
    protected boolean ExtraEnterMessage;
    protected boolean adminsOnly;
    protected boolean allowEmptyResidences;
    protected boolean NoLava;
    protected boolean NoWater;
    protected boolean NoLavaPlace;
    protected boolean useBlockFall;
    protected boolean NoWaterPlace;
    protected boolean AutoCleanUp;
    protected boolean SellSubzone;
    protected boolean LwcOnDelete;
    protected boolean LwcOnBuy;
    protected boolean LwcOnUnrent;
    protected List<Material> LwcMatList = new ArrayList<Material>();
    protected boolean UseClean;
    protected boolean PvPFlagPrevent;
    protected boolean OverridePvp;
    protected boolean BlockAnyTeleportation;
    protected int infoToolId;
    protected int AutoCleanUpDays;
    protected int selectionToolId;
    protected boolean adminOps;
    protected boolean AdminFullAccess;
    protected String multiworldPlugin;
    protected boolean enableRentSystem;
    protected boolean RentPreventRemoval;
    protected boolean RentInformOnEnding;
    protected boolean RentAllowRenewing;
    protected boolean RentStayInMarket;
    protected boolean RentAllowAutoPay;
    protected boolean RentPlayerAutoPay;
    protected boolean leaseAutoRenew;
    protected boolean ShortInfoUse;
    protected boolean OnlyLike;
    protected int RentInformBefore;
    protected int RentInformDelay;
    protected int rentCheckInterval;
    protected int chatPrefixLength;
    protected int leaseCheckInterval;
    protected int autoSaveInt;
    protected boolean NewSaveMechanic;
    // Backup stuff
    protected boolean BackupAutoCleanUpUse;
    protected int BackupAutoCleanUpDays;
    protected boolean UseZipBackup;
    protected boolean BackupWorldFiles;
    protected boolean BackupforsaleFile;
    protected boolean BackupleasesFile;
    protected boolean BackuppermlistsFile;
    protected boolean BackuprentFile;
    protected boolean BackupflagsFile;
    protected boolean BackupgroupsFile;
    protected boolean BackupconfigFile;
    protected int FlowLevel;
    protected int PlaceLevel;
    protected int BlockFallLevel;
    protected int CleanLevel;
    protected int NewPlayerRangeX;
    protected int NewPlayerRangeY;
    protected int NewPlayerRangeZ;
    protected int VisualizerRange;
    protected int VisualizerShowFor;
    protected int VisualizerUpdateInterval;
    protected int TeleportDelay;
    protected boolean TeleportTitleMessage;
    protected int VisualizerRowSpacing;
    protected int VisualizerCollumnSpacing;
    protected int VisualizerSkipBy;
    protected boolean flagsInherit;
    protected ChatColor chatColor;
    protected boolean chatEnable;
    protected boolean actionBar;
    protected boolean titleMessage;
    protected boolean ActionBarOnSelection;
    protected boolean visualizer;
    protected int minMoveUpdate;
    protected int MaxResCount;
    protected int MaxRentCount;
    protected int MaxSubzonesCount;
    protected int MaxSubzoneDepthCount;
    protected int VoteRangeFrom;
    protected int HealInterval;
    protected int FeedInterval;
    protected int VoteRangeTo;
    protected FlagPermissions globalCreatorDefaults;
    protected FlagPermissions globalResidenceDefaults;
    protected Map<String, FlagPermissions> globalGroupDefaults;
    protected String language;
    protected String DefaultWorld;
    protected String DateFormat;
    protected String TimeZone;
    protected boolean preventBuildInRent;
    protected boolean PreventSubZoneRemoval;
    protected boolean stopOnSaveError;
    protected boolean legacyperms;
    protected String namefix;
    protected boolean showIntervalMessages;
    protected boolean ShowNoobMessage;
    protected boolean NewPlayerUse;
    protected boolean NewPlayerFree;
    protected boolean spoutEnable;
    protected boolean AutoMobRemoval;
    protected boolean BounceAnimation;

    //Town
//    private boolean TownEnabled = false;
//    private int TownMinRange = 0;

//    protected boolean DisableNoFlagMessageUse;
//    protected List<String> DisableNoFlagMessageWorlds = new ArrayList<String>();
    protected boolean useFlagGUI;
    protected int AutoMobRemovalInterval;
    protected boolean enableLeaseMoneyAccount;
    protected boolean CouldronCompatability;
    protected boolean enableDebug = false;
    protected boolean versionCheck = true;
    protected boolean UUIDConvertion = true;
    protected boolean OfflineMode = false;
    protected boolean SelectionIgnoreY = false;
    protected boolean SelectionIgnoreYInSubzone = false;
    protected boolean NoCostForYBlocks = false;
    protected boolean useVisualizer;
    protected boolean DisableListeners;
    protected boolean DisableCommands;
    protected boolean TNTExplodeBelow;
    protected int TNTExplodeBelowLevel;
    protected boolean CreeperExplodeBelow;
    protected int CreeperExplodeBelowLevel;
    protected List<Integer> customContainers;
    protected List<Integer> customBothClick;
    protected List<Integer> customRightClick;
    protected List<Integer> CleanBlocks;
    protected List<String> NoFlowWorlds;
    protected List<String> AutoCleanUpWorlds;
    protected List<String> NoPlaceWorlds;
    protected List<String> BlockFallWorlds;
    protected List<String> CleanWorlds;
    protected List<String> FlagsList;
    protected List<String> NegativePotionEffects;
    protected List<String> NegativeLingeringPotionEffects;
    protected Location KickLocation;
    protected Location FlyLandLocation;
    protected List<RandomTeleport> RTeleport = new ArrayList<RandomTeleport>();
    protected List<String> DisabledWorldsList = new ArrayList<String>();
    protected int rtCooldown;
    protected int rtMaxTries;
    protected ItemStack GuiTrue;
    protected ItemStack GuiFalse;
    protected ItemStack GuiRemove;
    protected ParticleEffects SelectedFrame;
    protected ParticleEffects SelectedSides;
    protected ParticleEffects OverlapFrame;
    protected ParticleEffects OverlapSides;
    protected Effect SelectedSpigotFrame;
    protected Effect SelectedSpigotSides;
    protected Effect OverlapSpigotFrame;
    protected Effect OverlapSpigotSides;
    private int ItemPickUpDelay;
    private boolean AutomaticResidenceCreationCheckCollision;
    private String AutomaticResidenceCreationIncrementFormat;
    private boolean ConsoleLogsShowFlagChanges = true;
    // DynMap
    private int VisualizerFrameCap;
    private int VisualizerSidesCap;
    // Schematics
    private Double WalkSpeed1;
    private Double WalkSpeed2;
    private boolean enforceAreaInsideArea;
    // Global chat
    private Residence plugin;

    public ConfigManager(Residence plugin) {
//	FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Residence.dataFolder, "config.yml"));
        this.plugin = plugin;
        this.globalCreatorDefaults = new FlagPermissions();
        this.globalResidenceDefaults = new FlagPermissions();
        this.globalGroupDefaults = new HashMap<String, FlagPermissions>();
        UpdateConfigFile();
        this.loadFlags();
    }

    @SuppressWarnings("deprecation")
    public void UpdateConfigFile() {

        File f = new File(this.plugin.getDataFolder(), "config.yml");

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        if (in == null) {
            return;
        }

        String defaultWorldName = Bukkit.getServer().getWorlds().size() > 0 ? Bukkit.getServer().getWorlds().get(0).getName() : "World";

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(in);
        CommentedYamlConfiguration writer = new CommentedYamlConfiguration();
        conf.options().copyDefaults(true);
        ConfigReader c = new ConfigReader(conf, writer);

        c.getW().addComment("Global", "These are Global Settings for Residence.");

        c.getW()
            .addComment("Global.UUIDConvertion", "Starts UUID conversion on plugin startup", "DONT change this if you are not sure what you doing");
        this.UUIDConvertion = c.get("Global.UUIDConvertion", true);

        c.getW().addComment("Global.OfflineMode",
            "If you running offline server, better to check this as true. This will help to solve issues with changing players UUID.");
        this.OfflineMode = c.get("Global.OfflineMode", false);

        c.getW().addComment("Global.versionCheck",
            "Players with residence.versioncheck permission node will be noticed about new residence version on login");
        this.versionCheck = c.get("Global.versionCheck", true);

        c.getW().addComment("Global.Language", "This loads the <language>.yml file in the Residence Language folder",
            "All Residence text comes from this file. (NOT DONE YET)");
        this.language = c.get("Global.Language", "English", false);

        c.getW().addComment("Global.SelectionToolId", "Wooden Hoe is the default selection tool for Residence.",
            "You can change it to another item ID listed here: http://www.minecraftwiki.net/wiki/Data_values");
        this.selectionToolId = c.get("Global.SelectionToolId", Material.WOOD_HOE.getId());

        c.getW().addComment("Global.Selection.IgnoreY",
            "By setting this to true, all selections will be made from bedrock to sky ignoring Y coordinates");
        this.SelectionIgnoreY = c.get("Global.Selection.IgnoreY", false);

        c.getW().addComment("Global.Selection.IgnoreYInSubzone",
            "When this set to true, selections inside existing residence will be from bottom to top of that residence",
            "When this set to false, selections inside existing residence will be exactly as they are");
        this.SelectionIgnoreYInSubzone = c.get("Global.Selection.IgnoreYInSubzone", false);

        c.getW().addComment("Global.Selection.NoCostForYBlocks", "By setting this to true, player will only pay for x*z blocks ignoring height",
            "This will lower residence price by up to 256 times, so adjust block price BEFORE enabling this");
        this.NoCostForYBlocks = c.get("Global.Selection.NoCostForYBlocks", false);

        c.getW().addComment("Global.InfoToolId", "This determins which tool you can use to see info on residences, default is String.",
            "Simply equip this tool and hit a location inside the residence and it will display the info for it.");
        this.infoToolId = c.get("Global.InfoToolId", Material.STRING.getId());

        c.getW().addComment("Global.Optimizations.DefaultWorld",
            "Name of your main residence world. Usually normal starting world 'World'. Capitalization essential");
        this.DefaultWorld = c.get("Global.Optimizations.DefaultWorld", defaultWorldName, false);

        c.getW().addComment("Global.Optimizations.DisabledWorlds.List", "List Of Worlds where this plugin is disabled");
        this.DisabledWorldsList = c.get("Global.Optimizations.DisabledWorlds.List", new ArrayList<String>());

        c.getW().addComment("Global.Optimizations.DisabledWorlds.DisableListeners", "Disables all listeners in included worlds");
        this.DisableListeners = c.get("Global.Optimizations.DisabledWorlds.DisableListeners", true);
        c.getW().addComment("Global.Optimizations.DisabledWorlds.DisableCommands", "Disabled any command usage in included worlds");
        this.DisableCommands = c.get("Global.Optimizations.DisabledWorlds.DisableCommands", true);

        c.getW().addComment("Global.Optimizations.ItemPickUpDelay", "Delay in seconds between item pickups after residence flag prevents it",
            "Keep it at arround 10 sec to lower unesecery checks");
        this.ItemPickUpDelay = c.get("Global.Optimizations.ItemPickUpDelay", 10);

        c.getW().addComment("Global.Optimizations.AutomaticResidenceCreation.CheckCollision",
            "When set to true /res auto command will check for new area collision with other residences to avoid overlapping.",
            "Set it to false to gain some performace but new residence can often overlap with old ones");
        this.AutomaticResidenceCreationCheckCollision = c.get("Global.Optimizations.AutomaticResidenceCreation.CheckCollision", true);

        c.getW().addComment("Global.Optimizations.AutomaticResidenceCreation.IncrementFormat",
            "Defines new residence name increment when using automatic residence creation command if residence with that name already exist");
        this.AutomaticResidenceCreationIncrementFormat = c.get("Global.Optimizations.AutomaticResidenceCreation.IncrementFormat", "_[number]");

//	c.getW().addComment("Global.Optimizations.DisabledNoFlagMessage.Use", "Enable if you want to hide no flag error messages in particular worlds",
//	    "You can bypass this with residence.checkbadflags permission node");
//	DisableNoFlagMessageUse = c.get("Global.Optimizations.DisabledNoFlagMessage.Use", false);
//	c.getW().addComment("Global.Optimizations.DisabledNoFlagMessage.Worlds", "List Of Worlds where player wont get error messages");
//	DisableNoFlagMessageWorlds = c.get("Global.Optimizations.DisabledNoFlagMessage.Worlds", Arrays.asList(Bukkit.getWorlds().get(0).getName()));

        c.getW().addComment("Global.Optimizations.GlobalChat.Enabled",
            "Enables or disables chat modification by including players main residence name");
        this.GlobalChatEnabled = c.get("Global.Optimizations.GlobalChat.Enabled", false);
        c.getW().addComment("Global.Optimizations.GlobalChat.SelfModify",
            "Modifys chat to add chat titles.  If you're using a chat manager, you may add the tag {residence} to your chat format and disable this.");
        this.GlobalChatSelfModify = c.get("Global.Optimizations.GlobalChat.SelfModify", true);
        this.GlobalChatFormat = c.get("Global.Optimizations.GlobalChat.Format", "&c[&e%1&c]", true);

        c.getW().addComment("Global.Optimizations.BlockAnyTeleportation",
            "When this set to true, any teleportation to residence where player dont have tp flag, action will be denyied",
            "This can prevent from teleporting players to residence with 3rd party plugins like esentials /tpa");
        this.BlockAnyTeleportation = c.get("Global.Optimizations.BlockAnyTeleportation", true);

        c.getW().addComment("Global.Optimizations.MaxResCount",
            "Set this as low as posible depending of residence.max.res.[number] permission you are using",
            "In example if you are giving max number of 10 for players, set it to 15, if its 30, set it to 35 just to have some small buffer in case");
        this.MaxResCount = c.get("Global.Optimizations.MaxResCount", 30);
        c.getW().addComment("Global.Optimizations.MaxRentCount",
            "Set this as low as posible depending of residence.max.rents.[number] permission you are using",
            "In example if you are giving max number of 10 for players, set it to 15, if its 30, set it to 35 just to have some small buffer in case");
        this.MaxRentCount = c.get("Global.Optimizations.MaxRentCount", 10);
        c.getW().addComment("Global.Optimizations.MaxSubzoneCount",
            "Set this as low as posible depending of residence.max.subzones.[number] permission you are using",
            "In example if you are giving max number of 10 for players, set it to 15, if its 30, set it to 35 just to have some small buffer in case");
        this.MaxSubzonesCount = c.get("Global.Optimizations.MaxSubzoneCount", 5);
        c.getW().addComment("Global.Optimizations.MaxSubzoneDepthCount",
            "Set this as low as posible depending of residence.max.subzonedepth.[number] permission you are using",
            "In example if you are giving max number of 10 for players, set it to 15, if its 30, set it to 35 just to have some small buffer in case");
        this.MaxSubzoneDepthCount = c.get("Global.Optimizations.MaxSubzoneDepthCount", 5);

        c.getW().addComment("Global.Optimizations.OverridePvp", "By setting this to true, regular pvp flag will be acting as overridepvp flag",
            "Overridepvp flag tries to ignore any pvp protection in that residence by any other plugin");
        this.OverridePvp = c.get("Global.Optimizations.OverridePvp", false);

        // residence kick location
        c.getW().addComment("Global.Optimizations.KickLocation.Use",
            "By setting this to true, when player kicks another player from residence, he will be teleported to this location instead of getting outside residence");
        Boolean UseKick = c.get("Global.Optimizations.KickLocation.Use", false);
        String KickLocationWorld = c.get("Global.Optimizations.KickLocation.World", defaultWorldName);
        Double KickLocationX = c.get("Global.Optimizations.KickLocation.X", 0.5);
        Double KickLocationY = c.get("Global.Optimizations.KickLocation.Y", 63.0);
        Double KickLocationZ = c.get("Global.Optimizations.KickLocation.Z", 0.5);
        c.getW().addComment("Global.Optimizations.KickLocation.Pitch", "Less than 0 - head up, more than 0 - head down. Range from -90 to 90");
        Double KickPitch = c.get("Global.Optimizations.KickLocation.Pitch", 0.0);
        c.getW().addComment("Global.Optimizations.KickLocation.Yaw", "Head position to left and right. Range from -180 to 180");
        Double KickYaw = c.get("Global.Optimizations.KickLocation.Yaw", 0.0);
        if (UseKick) {
            World world = Bukkit.getWorld(KickLocationWorld);
            if (world != null) {
                this.KickLocation = new Location(world, KickLocationX, KickLocationY, KickLocationZ);
                this.KickLocation.setPitch(KickPitch.floatValue());
                this.KickLocation.setYaw(KickYaw.floatValue());
            }
        }

        c.getW().addComment("Global.Optimizations.FlyLandLocation.World",
            "Used when players fly state is being turned to false because of fly flag and there is no solid land where to land for player");
        String FlyLocationWorld = c.get("Global.Optimizations.FlyLandLocation.World", defaultWorldName);
        Double FlyLocationX = c.get("Global.Optimizations.FlyLandLocation.X", 0.5);
        Double FlyLocationY = c.get("Global.Optimizations.FlyLandLocation.Y", 63.0);
        Double FlyLocationZ = c.get("Global.Optimizations.FlyLandLocation.Z", 0.5);
        c.getW().addComment("Global.Optimizations.FlyLandLocation.Pitch", "Less than 0 - head up, more than 0 - head down. Range from -90 to 90");
        Double FlyPitch = c.get("Global.Optimizations.FlyLandLocation.Pitch", 0.0);
        c.getW().addComment("Global.Optimizations.FlyLandLocation.Yaw", "Head position to left and right. Range from -180 to 180");
        Double FlyYaw = c.get("Global.Optimizations.FlyLandLocation.Yaw", 0.0);
        World world = Bukkit.getWorld(FlyLocationWorld);
        if (world != null) {
            this.FlyLandLocation = new Location(world, FlyLocationX, FlyLocationY, FlyLocationZ);
            this.FlyLandLocation.setPitch(FlyPitch.floatValue());
            this.FlyLandLocation.setYaw(FlyYaw.floatValue());
        }

        c.getW().addComment("Global.Optimizations.ShortInfo.Use",
            "By setting this to true, when checking residence info with /res info, you will get only names in list, by hovering on them, you will get flag list");
        this.ShortInfoUse = c.get("Global.Optimizations.ShortInfo.Use", false);

        // Vote range
        c.getW().addComment("Global.Optimizations.Vote.RangeFrom", "Range players can vote to, by default its from 0 to 10 points");
        this.VoteRangeFrom = c.get("Global.Optimizations.Vote.RangeFrom", 0);
        this.VoteRangeTo = c.get("Global.Optimizations.Vote.RangeTo", 10);

        c.getW().addComment("Global.Optimizations.Vote.OnlyLike", "If this true, players can only give like for shop instead of point voting");
        this.OnlyLike = c.get("Global.Optimizations.Vote.OnlyLike", false);

        c.getW().addComment("Global.Optimizations.ConsoleLogs.ShowFlagChanges", "If this true, flag changes throw GUI will be recorded in console");
        this.ConsoleLogsShowFlagChanges = c.get("Global.Optimizations.ConsoleLogs.ShowFlagChanges", true);

        // Healing/Feed interval
        c.getW().addComment("Global.Optimizations.Intervals.Heal", "How often in seconds to heal/feed players in residence with appropriate flag",
            "Bigger numbers can save some resources");
        this.HealInterval = c.get("Global.Optimizations.Intervals.Heal", 1);
        this.FeedInterval = c.get("Global.Optimizations.Intervals.Feed", 5);

        // negative potion effect list
        c.getW().addComment("Global.Optimizations.NegativePotionEffects",
            "Potions containing one of thos effects will be ignored if residence dont have pvp true flag set");
        this.NegativePotionEffects =
            c.get("Global.Optimizations.NegativePotionEffects", Arrays.asList("blindness", "confusion", "harm", "hunger", "poison", "slow",
                "slow_digging", "weakness", "wither"));

        this.NegativeLingeringPotionEffects =
            c.get("Global.Optimizations.NegativeLingeringPotions", Arrays.asList("slowness", "instant_damage", "poison",
                "slowness"));

        c.getW().addComment("Global.Optimizations.WalkSpeed",
            "Defines speed for particular wspeed1 and wspeed2 flags. It can be from 0 up to 5");
        this.WalkSpeed1 = c.get("Global.Optimizations.WalkSpeed.1", 0.5D);
        this.WalkSpeed1 = this.WalkSpeed1 < 0 ? 0 : this.WalkSpeed1;
        this.WalkSpeed1 = this.WalkSpeed1 > 5 ? 5 : this.WalkSpeed1;
        this.WalkSpeed1 = this.WalkSpeed1 / 5.0;
        this.WalkSpeed2 = c.get("Global.Optimizations.WalkSpeed.2", 2D);
        this.WalkSpeed2 = this.WalkSpeed2 < 0 ? 0 : this.WalkSpeed2;
        this.WalkSpeed2 = this.WalkSpeed2 > 5 ? 5 : this.WalkSpeed2;
        this.WalkSpeed2 = this.WalkSpeed2 / 5.0;

        c.getW().addComment("Global.MoveCheckInterval", "The interval, in milliseconds, between movement checks.",
            "Reducing this will increase the load on the server.",
            "Increasing this will allow players to move further in movement restricted zones before they are teleported out.");
        this.minMoveUpdate = c.get("Global.MoveCheckInterval", 500);

        c.getW().addComment("Global.Tp.TeleportDelay", "The interval, in seconds, for teleportation.", "Use 0 to disable");
        this.TeleportDelay = c.get("Global.Tp.TeleportDelay", 3);
        c.getW().addComment("Global.Tp.TeleportTitleMessage", "Show aditional message in title message area when player is teleporting to residence");
        this.TeleportTitleMessage = c.get("Global.Tp.TeleportTitleMessage", true);

        Set<World> worlds = new HashSet<World>();
        worlds.addAll(Bukkit.getWorlds());

        boolean commented = false;
        if (c.getC().isConfigurationSection("Global.RandomTeleportation.Worlds")) {
            ConfigurationSection sec = c.getC().getConfigurationSection("Global.RandomTeleportation.Worlds");
            if (sec != null) {
                for (String one : sec.getKeys(false)) {
                    String path = "Global.RandomTeleportation.Worlds." + one + ".";

                    boolean enabled = c.get(path + "Enabled", true);

                    if (!commented) {
                        c.getW().addComment("Global.RandomTeleportation.Worlds." + one,
                            "World name to use this feature. Add annother one with appropriate name to enable random teleportation");
                    }

                    if (!commented) {
                        c.getW().addComment(path + "MaxCoord",
                            "Max coordinate to teleport, setting to 1000, player can be teleported between -1000 and 1000 coordinates");
                    }
                    int MaxCoord = c.get(path + "MaxCoord", 1000);

                    if (!commented) {
                        c.getW().addComment(path + "MinCord",
                            "If maxcord set to 1000 and mincord to 500, then player can be teleported between -1000 to -500 and 1000 to 500 coordinates");
                    }
                    int MinCord = c.get(path + "MinCord", 500);
                    int CenterX = c.get(path + "CenterX", 0);
                    int CenterZ = c.get(path + "CenterZ", 0);

                    World w = getWorld(one);

                    if (w == null) {
                        this.plugin.consoleMessage("&cCan't find world with (" + one + ") name");
                        continue;
                    }

                    commented = true;
                    worlds.remove(w);

                    if (!enabled) {
                        continue;
                    }
                    this.RTeleport.add(new RandomTeleport(w, MaxCoord, MinCord, CenterX, CenterZ));
                }
            }
        }
        for (World one : worlds) {
            String name = one.getName();
            name = name.replace(".", "_");

            String path = "Global.RandomTeleportation.Worlds." + name + ".";
            boolean enabled = c.get(path + "Enabled", true);
            int MaxCoord = c.get(path + "MaxCoord", 1000);
            int MinCord = c.get(path + "MinCord", 500);
            int CenterX = c.get(path + "CenterX", 0);
            int CenterZ = c.get(path + "CenterZ", 0);

            if (!enabled) {
                continue;
            }
            this.RTeleport.add(new RandomTeleport(one, MaxCoord, MinCord, CenterX, CenterZ));
        }

        c.getW().addComment("Global.RandomTeleportation.Cooldown", "How long force player to wait before using command again.");
        this.rtCooldown = c.get("Global.RandomTeleportation.Cooldown", 5);

        c.getW().addComment("Global.RandomTeleportation.MaxTries", "How many times to try find correct location for teleportation.",
            "Keep it at low number, as player always can try again after delay");
        this.rtMaxTries = c.get("Global.RandomTeleportation.MaxTries", 20);

        c.getW().addComment("Global.SaveInterval", "The interval, in minutes, between residence saves.");
        this.autoSaveInt = c.get("Global.SaveInterval", 10);
        c.getW().addComment("Global.NewSaveMechanic", "New save mechanic can minimize save file couple times and speedup save/load time in general",
            "Bigger files have bigger impact");
        this.NewSaveMechanic = c.get("Global.NewSaveMechanic", false);

        c.getW().addComment("Global.Backup.AutoCleanUp.Use",
            "Do you want to automaticaly remove backup files from main backup folder if they are older than defined day amount");
        this.BackupAutoCleanUpUse = c.get("Global.Backup.AutoCleanUp.Use", false);
        this.BackupAutoCleanUpDays = c.get("Global.Backup.AutoCleanUp.Days", 30);

        c.getW().addComment("Global.Backup.UseZip", "Do you want to backup files by creating zip files in main residence folder in backup folder",
            "This wont have effect on regular backuped files made in save folder");
        this.UseZipBackup = c.get("Global.Backup.UseZip", true);

        this.BackupWorldFiles = c.get("Global.Backup.IncludeFiles.Worlds", true);
        this.BackupforsaleFile = c.get("Global.Backup.IncludeFiles.forsale", true);
        this.BackupleasesFile = c.get("Global.Backup.IncludeFiles.leases", true);
        this.BackuppermlistsFile = c.get("Global.Backup.IncludeFiles.permlists", true);
        this.BackuprentFile = c.get("Global.Backup.IncludeFiles.rent", true);
        this.BackupflagsFile = c.get("Global.Backup.IncludeFiles.flags", true);
        this.BackupgroupsFile = c.get("Global.Backup.IncludeFiles.groups", true);
        this.BackupconfigFile = c.get("Global.Backup.IncludeFiles.config", true);

        // Auto remove old residences
        c.getW().addComment("Global.AutoCleanUp.Use", "HIGHLY EXPERIMENTAL residence cleaning on server startup if player is offline for x days.",
            "Players can bypass this with residence.cleanbypass permission node");
        this.AutoCleanUp = c.get("Global.AutoCleanUp.Use", false);
        c.getW().addComment("Global.AutoCleanUp.Days", "For how long player should be offline to delete hes residence");
        this.AutoCleanUpDays = c.get("Global.AutoCleanUp.Days", 60);
        c.getW().addComment("Global.AutoCleanUp.Worlds", "Worlds to be included in check list");
        this.AutoCleanUpWorlds = c.get("Global.AutoCleanUp.Worlds", Arrays.asList(defaultWorldName));

        c.getW().addComment("Global.Lwc.OnDelete", "Removes lwc protection from all defined objects when removing residence");
        this.LwcOnDelete = c.get("Global.Lwc.OnDelete", true);
        c.getW().addComment("Global.Lwc.OnBuy", "Removes lwc protection from all defined objects when buying residence");
        this.LwcOnBuy = c.get("Global.Lwc.OnBuy", true);
        c.getW().addComment("Global.Lwc.OnUnrent", "Removes lwc protection from all defined objects when unrenting residence");
        this.LwcOnUnrent = c.get("Global.Lwc.OnUnrent", true);

        c.getW().addComment("Global.Lwc.MaterialList", "List of blocks you want to remove protection from");
        for (String oneName : c.get("Global.Lwc.MaterialList", Arrays.asList("CHEST", "TRAPPED_CHEST", "furnace", "dispenser"))) {
            Material mat = Material.getMaterial(oneName.toUpperCase());
            if (mat != null) {
                this.LwcMatList.add(mat);
            } else {
                Bukkit.getConsoleSender().sendMessage("Incorrect Lwc material name for " + oneName);
            }
        }

        // TNT explosions below 63
        c.getW().addComment("Global.AntiGreef.TNT.ExplodeBelow",
            "When set to true will allow tnt and minecart with tnt to explode below 62 (default) level outside of residence",
            "This will allow mining with tnt and more vanilla play");
        this.TNTExplodeBelow = c.get("Global.AntiGreef.TNT.ExplodeBelow", false);
        this.TNTExplodeBelowLevel = c.get("Global.AntiGreef.TNT.level", 62);
        // Creeper explosions below 63
        c.getW().addComment("Global.AntiGreef.Creeper.ExplodeBelow",
            "When set to true will allow Creeper explode below 62 (default) level outside of residence",
            "This will give more realistic game play");
        this.CreeperExplodeBelow = c.get("Global.AntiGreef.Creeper.ExplodeBelow", false);
        this.CreeperExplodeBelowLevel = c.get("Global.AntiGreef.Creeper.level", 62);
        // Flow
        c.getW().addComment("Global.AntiGreef.Flow.Level", "Level from which one to start lava and water flow blocking",
            "This dont have effect in residence area");
        this.FlowLevel = c.get("Global.AntiGreef.Flow.Level", 63);
        c.getW().addComment("Global.AntiGreef.Flow.NoLavaFlow", "With this set to true, lava flow outside residence is blocked");
        this.NoLava = c.get("Global.AntiGreef.Flow.NoLavaFlow", true);
        c.getW().addComment("Global.AntiGreef.Flow.NoWaterFlow", "With this set to true, water flow outside residence is blocked");
        this.NoWater = c.get("Global.AntiGreef.Flow.NoWaterFlow", true);
        this.NoFlowWorlds = c.get("Global.AntiGreef.Flow.Worlds", Arrays.asList(defaultWorldName));

        // Place
        c.getW().addComment("Global.AntiGreef.Place.Level", "Level from which one to start block lava and water place",
            "This don't have effect in residence area");
        this.PlaceLevel = c.get("Global.AntiGreef.Place.Level", 63);
        c.getW().addComment("Global.AntiGreef.Place.NoLavaPlace", "With this set to true, playrs cant place lava outside residence");
        this.NoLavaPlace = c.get("Global.AntiGreef.Place.NoLavaPlace", true);
        c.getW().addComment("Global.AntiGreef.Place.NoWaterPlace", "With this set to true, playrs cant place water outside residence");
        this.NoWaterPlace = c.get("Global.AntiGreef.Place.NoWaterPlace", true);
        this.NoPlaceWorlds = c.get("Global.AntiGreef.Place.Worlds", Arrays.asList(defaultWorldName));

        // Sand fall
        c.getW().addComment("Global.AntiGreef.BlockFall.Use",
            "With this set to true, falling blocks will be deleted if they will land in different area");
        this.useBlockFall = c.get("Global.AntiGreef.BlockFall.Use", true);
        c.getW().addComment("Global.AntiGreef.BlockFall.Level", "Level from which one to start block block's fall",
            "This don't have effect in residence area or outside");
        this.BlockFallLevel = c.get("Global.AntiGreef.BlockFall.Level", 62);
        this.BlockFallWorlds = c.get("Global.AntiGreef.BlockFall.Worlds", Arrays.asList(defaultWorldName));

        // Res cleaning
        c.getW().addComment("Global.AntiGreef.ResCleaning.Use",
            "With this set to true, after player removes its residence, all blocks listed below, will be replaced with air blocks",
            "Effective way to prevent residence creating near greefing target and then remove it");
        this.UseClean = c.get("Global.AntiGreef.ResCleaning.Use", true);
        c.getW().addComment("Global.AntiGreef.ResCleaning.Level", "Level from whichone you want to replace blocks");
        this.CleanLevel = c.get("Global.AntiGreef.ResCleaning.Level", 63);
        c.getW().addComment("Global.AntiGreef.ResCleaning.Blocks", "Block list to be replaced", "By default only water and lava will be replaced");
        this.CleanBlocks = c.getIntList("Global.AntiGreef.ResCleaning.Blocks", Arrays.asList(8, 9, 10, 11));
        this.CleanWorlds = c.get("Global.AntiGreef.ResCleaning.Worlds", Arrays.asList(defaultWorldName));

        c.getW().addComment("Global.AntiGreef.Flags.Prevent",
            "By setting this to true flags from list will be protected from change while there is some one inside residence besides owner",
            "Protects in example from people inviting some one and changing pvp flag to true to kill them");
        this.PvPFlagPrevent = c.get("Global.AntiGreef.Flags.Prevent", true);
        this.FlagsList = c.get("Global.AntiGreef.Flags.list", Arrays.asList("pvp"));

        c.getW().addComment("Global.DefaultGroup", "The default group to use if Permissions fails to attach or your not using Permissions.");
        this.defaultGroup = c.get("Global.DefaultGroup", "default");

        c.getW().addComment("Global.UseLeaseSystem", "Enable / Disable the Lease System.");
        this.useLeases = c.get("Global.UseLeaseSystem", false);

        c.getW().addComment("Global.DateFormat", "Sets date format when shown in example lease or rent expire date",
            "How to use it properly, more information can be found at http://www.tutorialspoint.com/java/java_date_time.htm");
        this.DateFormat = c.get("Global.DateFormat", "E yyyy.MM.dd 'at' hh:mm:ss a zzz");

        c.getW().addComment("Global.TimeZone",
            "Sets time zone for showing date, usefull when server is in different country then main server player base",
            "Full list of posible time zones can be found at http://www.mkyong.com/java/java-display-list-of-timezone-with-gmt/");
        this.TimeZone = c.get("Global.TimeZone", Calendar.getInstance().getTimeZone().getID());

        c.getW().addComment("Global.ResMoneyBack", "Enable / Disable money returning on residence removal.");
        this.ResMoneyBack = c.get("Global.ResMoneyBack", false);

        c.getW().addComment("Global.LeaseCheckInterval", "The interval, in minutes, between residence lease checks (if leases are enabled).");
        this.leaseCheckInterval = c.get("Global.LeaseCheckInterval", 10);

        c.getW().addComment("Global.LeaseAutoRenew",
            "Allows leases to automatically renew so long as the player has the money, if economy is disabled, this setting does nothing.");
        this.leaseAutoRenew = c.get("Global.LeaseAutoRenew", true);

        c.getW().addComment("Global.EnablePermissions", "Whether or not to use the Permissions system in conjunction with this config.");
        c.get("Global.EnablePermissions", true);

        c.getW().addComment("Global.LegacyPermissions",
            "Set to true if NOT using Permissions or PermissionsBukkit, or using a really old version of Permissions");
        this.legacyperms = c.get("Global.LegacyPermissions", false);

        c.getW().addComment("Global.EnableEconomy",
            "Enable / Disable Residence's Economy System (iConomy, MineConomy, Essentials, BOSEconomy, and RealEconomy supported).");
        this.enableEconomy = c.get("Global.EnableEconomy", true);

        c.getW().addComment("Global.ExtraEnterMessage",
            "When enabled extra message will apear in chat if residence is for rent or for sell to inform how he can rent/buy residence with basic information.");
        this.ExtraEnterMessage = c.get("Global.ExtraEnterMessage", true);

        c.getW().addComment("Global.Sell.Subzone", "If set to true, this will allow to sell subzones. Its recommended to keep it false tho");
        this.SellSubzone = c.get("Global.Sell.Subzone", false);

        c.getW().addComment("Global.EnableRentSystem", "Enables or disables the Rent System");
        this.enableRentSystem = c.get("Global.EnableRentSystem", true);

//	TownEnabled = c.get("Global.Town.Enabled", true);
//	c.getW().addComment("Global.Town.MinRange", "Range between residences","Protects from building residence near another residence if owner not belonging to same town");
//	TownMinRange = c.get("Global.Town.MinRange", 16);

        c.getW().addComment("Global.Rent.PreventRemoval", "Prevents residence/subzone removal if its subzone is still rented by some one");
        this.RentPreventRemoval = c.get("Global.Rent.PreventRemoval", true);
        c.getW().addComment("Global.Rent.Inform.OnEnding", "Informs players on rent time ending");
        this.RentInformOnEnding = c.get("Global.Rent.Inform.OnEnding", true);
        c.getW().addComment("Global.Rent.Inform.Before", "Time range in minutes when to start informing about ending rent");
        this.RentInformBefore = c.get("Global.Rent.Inform.Before", 1440);
        c.getW()
            .addComment("Global.Rent.Inform.Delay", "Time range in seconds for how long to wait after player logs in to inform about ending rents");
        this.RentInformDelay = c.get("Global.Rent.Inform.Delay", 60);

        c.getW().addComment("Global.Rent.DefaultValues.AllowRenewing", "Default values used when putting residence for rent");
        this.RentAllowRenewing = c.get("Global.Rent.DefaultValues.AllowRenewing", true);
        this.RentStayInMarket = c.get("Global.Rent.DefaultValues.StayInMarket", true);
        this.RentAllowAutoPay = c.get("Global.Rent.DefaultValues.AllowAutoPay", true);
        c.getW().addComment("Global.Rent.DefaultValues.PlayerAutoPay",
            "If set to true, when player is not defining auto pay on renting, then this value will be used");
        this.RentPlayerAutoPay = c.get("Global.Rent.DefaultValues.PlayerAutoPay", true);

        c.getW().addComment("Global.Rent.Schematics.RestoreAfterRentEnds",
            "EXPERIMENTAL!!! If set to true, residence will be restored to state it was when backup flag was set to true",
            "For securoty reassons only players with aditional residence.backup permission node can set backup flag");
        this.RestoreAfterRentEnds = c.get("Global.Rent.Schematics.RestoreAfterRentEnds", true);
        c.getW().addComment("Global.Rent.Schematics.SaveOnFlagChange",
            "When set to true, area state will be saved only when setting backup to true value",
            "When set to false, area state will be saved before each renting to have always up to date area look",
            "Keep in mind that when its set to false, there is slightly bigger server load as it has to save area each time when some one rents it");
        this.SchematicsSaveOnFlagChange = c.get("Global.Rent.Schematics.SaveOnFlagChange", true);

        c.getW().addComment("Global.RentCheckInterval",
            "The interval, in minutes, between residence rent expiration checks (if the rent system is enabled).");
        this.rentCheckInterval = c.get("Global.RentCheckInterval", 10);

        c.getW().addComment("Global.ResidenceChatEnable", "Enable or disable residence chat channels.");
        this.chatEnable = c.get("Global.ResidenceChatEnable", true);

        c.getW().addComment("Global.ActionBar.General", "True for ActionBar - new component in 1.8",
            "False for old Messaging in chat enter/leave Residence messages");
        this.actionBar = c.get("Global.ActionBar.General", true);
        c.getW().addComment("Global.TitleBar.EnterLeave", "When set to true enter/leave messages will be shown in title/subtitle slots",
            "Subtitle can be defined with %subtitle% while setting enter/leave messages");
        this.titleMessage = c.get("Global.TitleBar.EnterLeave", false);
        this.ActionBarOnSelection = c.get("Global.ActionBar.ShowOnSelection", true);

        c.getW().addComment("Global.ResidenceChatColor", "Color of residence chat.");
        try {
            this.chatColor = ChatColor.valueOf(c.get("Global.ResidenceChatColor", "DARK_PURPLE", true));
        } catch (Exception ex) {
            this.chatColor = ChatColor.DARK_PURPLE;
        }

        c.getW().addComment("Global.ResidenceChatPrefixLenght", "Max lenght of residence chat prefix including color codes");
        this.chatPrefixLength = c.get("Global.ResidenceChatPrefixLength", 16);

        c.getW().addComment("Global.AdminOnlyCommands",
            "Whether or not to ignore the usual Permission flags and only allow OPs and groups with 'residence.admin' to change residences.");
        this.adminsOnly = c.get("Global.AdminOnlyCommands", false);

        c.getW().addComment("Global.AdminOPs", "Setting this to true makes server OPs admins.");
        this.adminOps = c.get("Global.AdminOPs", true);

        c.getW().addComment("Global.AdminFullAccess",
            "Setting this to true server administration wont need to use /resadmin command to access admin command if they are op or have residence.admin permission node.");
        this.AdminFullAccess = c.get("Global.AdminFullAccess", false);

        c.getW().addComment("Global.MultiWorldPlugin",
            "This is the name of the plugin you use for multiworld, if you dont have a multiworld plugin you can safely ignore this.",
            "The only thing this does is check to make sure the multiworld plugin is enabled BEFORE Residence, to ensure properly loading residences for other worlds.");
        this.multiworldPlugin = c.get("Global.MultiWorldPlugin", "Multiverse-Core");

        c.getW().addComment("Global.ResidenceFlagsInherit", "Setting this to true causes subzones to inherit flags from their parent zones.");
        this.flagsInherit = c.get("Global.ResidenceFlagsInherit", true);

        c.getW().addComment("Global.PreventRentModify", "Setting this to false will allow rented residences to be modified by the renting player.");
        this.preventBuildInRent = c.get("Global.PreventRentModify", true);

        c.getW().addComment("Global.PreventSubZoneRemoval",
            "Setting this to true will prevent subzone deletion when subzone owner is not same as parent zone owner.");
        this.PreventSubZoneRemoval = c.get("Global.PreventSubZoneRemoval", true);

        c.getW().addComment("Global.StopOnSaveFault",
            "Setting this to false will cause residence to continue to load even if a error is detected in the save file.");
        this.stopOnSaveError = c.get("Global.StopOnSaveFault", true);

        c.getW().addComment(
            "This is the residence name filter, that filters out invalid characters.  Google 'Java RegEx' or 'Java Regular Expressions' for more info on how they work.");
        this.namefix = c.get("Global.ResidenceNameRegex", "[^a-zA-Z0-9\\-\\_]");

        c.getW().addComment("Global.ShowIntervalMessages",
            "Setting this to true sends a message to the console every time Residence does a rent expire check or a lease expire check.");
        this.showIntervalMessages = c.get("Global.ShowIntervalMessages", false);

        c.getW()
            .addComment("Global.ShowNoobMessage", "Setting this to true sends a tutorial message to the new player when he places chest on ground.");
        this.ShowNoobMessage = c.get("Global.ShowNoobMessage", true);

        c.getW().addComment("Global.NewPlayer", "Setting this to true creates residence around players placed chest if he don't have any.",
            "Only once every server restart if he still don't have any residence");
        this.NewPlayerUse = c.get("Global.NewPlayer.Use", false);
        c.getW().addComment("Global.NewPlayer.Free", "Setting this to true, residence will be created for free",
            "By setting to false, money will be taken from player, if he has them");
        this.NewPlayerFree = c.get("Global.NewPlayer.Free", true);
        c.getW()
            .addComment("Global.NewPlayer.Range", "Range from placed chest o both sides. By setting to 5, residence will be 5+5+1 = 11 blocks wide");
        this.NewPlayerRangeX = c.get("Global.NewPlayer.Range.X", 5);
        this.NewPlayerRangeY = c.get("Global.NewPlayer.Range.Y", 5);
        this.NewPlayerRangeZ = c.get("Global.NewPlayer.Range.Z", 5);

        c.getW().addComment("Global.CustomContainers",
            "Experimental - The following settings are lists of block IDs to be used as part of the checks for the 'container' and 'use' flags when using mods.");
        this.customContainers = c.getIntList("Global.CustomContainers", new ArrayList<Integer>());
        this.customBothClick = c.getIntList("Global.CustomBothClick", new ArrayList<Integer>());
        this.customRightClick = c.getIntList("Global.CustomRightClick", new ArrayList<Integer>());

        c.getW().addComment("Global.Visualizer.Use", "With this enabled player will see particle effects to mark selection boundries");
        this.useVisualizer = c.get("Global.Visualizer.Use", true);
        c.getW().addComment("Global.Visualizer.Range", "Range in blocks to draw particle effects for player",
            "Keep it no more as 30, as player cant see more than 16 blocks");
        this.VisualizerRange = c.get("Global.Visualizer.Range", 16);
        c.getW().addComment("Global.Visualizer.ShowFor", "For how long in miliseconds (5000 = 5sec) to show particle effects");
        this.VisualizerShowFor = c.get("Global.Visualizer.ShowFor", 5000);
        c.getW().addComment("Global.Visualizer.updateInterval", "How often in ticks to update particles for player");
        this.VisualizerUpdateInterval = c.get("Global.Visualizer.updateInterval", 5);
        c.getW().addComment("Global.Visualizer.RowSpacing", "Spacing in blocks between particle effects for rows");
        this.VisualizerRowSpacing = c.get("Global.Visualizer.RowSpacing", 2);
        if (this.VisualizerRowSpacing < 1) {
            this.VisualizerRowSpacing = 1;
        }
        c.getW().addComment("Global.Visualizer.CollumnSpacing", "Spacing in blocks between particle effects for collums");
        this.VisualizerCollumnSpacing = c.get("Global.Visualizer.CollumnSpacing", 2);
        if (this.VisualizerCollumnSpacing < 1) {
            this.VisualizerCollumnSpacing = 1;
        }

        c.getW().addComment("Global.Visualizer.SkipBy",
            "Defines by how many particles we need to skip",
            "This will create moving particle effect and will improve overall look of selection",
            "By increasing this number, you can decrease update interval");
        this.VisualizerSkipBy = c.get("Global.Visualizer.SkipBy", 5);
        if (this.VisualizerSkipBy < 1) {
            this.VisualizerSkipBy = 1;
        }

        c.getW().addComment("Global.Visualizer.FrameCap", "Maximum amount of frame particles to show for one player");
        this.VisualizerFrameCap = c.get("Global.Visualizer.FrameCap", 500);
        if (this.VisualizerFrameCap < 1) {
            this.VisualizerFrameCap = 1;
        }

        c.getW().addComment("Global.Visualizer.SidesCap", "Maximum amount of sides particles to show for one player");
        this.VisualizerSidesCap = c.get("Global.Visualizer.SidesCap", 2000);
        if (this.VisualizerSidesCap < 1) {
            this.VisualizerSidesCap = 1;
        }

        String effectsList = "";
        for (Effect one : Effect.values()) {
            if (one == null) {
                continue;
            }
            if (one.name() == null) {
                continue;
            }
            effectsList += one.name().toLowerCase() + ", ";
        }

        c.getW().addComment("Global.Visualizer.Selected",
            "Particle effect names. Posible: explode, largeexplode, hugeexplosion, fireworksSpark, splash, wake, crit, magicCrit",
            " smoke, largesmoke, spell, instantSpell, mobSpell, mobSpellAmbient, witchMagic, dripWater, dripLava, angryVillager, happyVillager, townaura",
            " note, portal, enchantmenttable, flame, lava, footstep, cloud, reddust, snowballpoof, snowshovel, slime, heart, barrier",
            " droplet, take, mobappearance",
            "",
            "If using spigot based server different particales can be used:", effectsList);

        // Frame
        String efname = c.get("Global.Visualizer.Selected.Frame", "happyVillager");
        this.SelectedFrame = ParticleEffects.fromName(efname);
        if (this.SelectedFrame == null) {
            this.SelectedFrame = ParticleEffects.VILLAGER_HAPPY;
            Bukkit.getConsoleSender().sendMessage("Can't find effect for Selected Frame with this name, it was set to default");
        }
        efname = efname.equalsIgnoreCase("reddust") ? "COLOURED_DUST" : efname;
        for (Effect one : Effect.values()) {
            if (one.name().replace("_", "").equalsIgnoreCase(efname.replace("_", ""))) {
                this.SelectedSpigotFrame = one;
                break;
            }
        }

        if (this.plugin.isSpigot()) {
            if (this.SelectedSpigotFrame == null) {
                this.SelectedSpigotFrame = Effect.getByName("HAPPY_VILLAGER");
                if (this.SelectedSpigotFrame == null) {
                    this.SelectedSpigotFrame = Effect.values()[0];
                }
                Bukkit.getConsoleSender().sendMessage("Can't find effect for Selected Frame with this name, it was set to default");
            }
        }

        // Sides
        efname = c.get("Global.Visualizer.Selected.Sides", "reddust");
        this.SelectedSides = ParticleEffects.fromName(efname);
        if (this.SelectedSides == null) {
            this.SelectedSides = ParticleEffects.REDSTONE;
            Bukkit.getConsoleSender().sendMessage("Can't find effect for Selected Sides with this name, it was set to default");
        }
        efname = efname.equalsIgnoreCase("reddust") ? "COLOURED_DUST" : efname;
        for (Effect one : Effect.values()) {
            if (one.name().replace("_", "").equalsIgnoreCase(efname.replace("_", ""))) {
                this.SelectedSpigotSides = one;
                break;
            }
        }

        if (this.plugin.isSpigot()) {
            if (this.SelectedSpigotSides == null) {
                this.SelectedSpigotSides = Effect.getByName("COLOURED_DUST");
                if (this.SelectedSpigotSides == null) {
                    this.SelectedSpigotSides = Effect.values()[0];
                }

                Bukkit.getConsoleSender().sendMessage("Can't find effect for Selected Frame with this name, it was set to default");
            }
        }

        efname = c.get("Global.Visualizer.Overlap.Frame", "FLAME");
        this.OverlapFrame = ParticleEffects.fromName(efname);
        if (this.OverlapFrame == null) {
            this.OverlapFrame = ParticleEffects.FLAME;
            Bukkit.getConsoleSender().sendMessage("Can't find effect for Overlap Frame with this name, it was set to default");
        }

        efname = efname.equalsIgnoreCase("reddust") ? "COLOURED_DUST" : efname;
        for (Effect one : Effect.values()) {
            if (one.name().replace("_", "").equalsIgnoreCase(efname.replace("_", ""))) {
                this.OverlapSpigotFrame = one;
                break;
            }
        }

        if (this.plugin.isSpigot()) {
            if (this.OverlapSpigotFrame == null) {
                this.OverlapSpigotFrame = Effect.getByName("FLAME");
                if (this.OverlapSpigotFrame == null) {
                    this.OverlapSpigotFrame = Effect.values()[0];
                }
                Bukkit.getConsoleSender().sendMessage("Can't find effect for Selected Frame with this name, it was set to default");
            }
        }

        efname = c.get("Global.Visualizer.Overlap.Sides", "FLAME");
        this.OverlapSides = ParticleEffects.fromName(efname);
        if (this.OverlapSides == null) {
            this.OverlapSides = ParticleEffects.FLAME;
            Bukkit.getConsoleSender().sendMessage("Can't find effect for Selected Sides with this name, it was set to default");
        }
        efname = efname.equalsIgnoreCase("reddust") ? "COLOURED_DUST" : efname;
        for (Effect one : Effect.values()) {
            if (one.name().replace("_", "").equalsIgnoreCase(efname.replace("_", ""))) {
                this.OverlapSpigotSides = one;
                break;
            }
        }

        if (this.plugin.isSpigot()) {
            if (this.OverlapSpigotSides == null) {
                this.OverlapSpigotSides = Effect.getByName("FLAME");
                if (this.OverlapSpigotSides == null) {
                    this.OverlapSpigotSides = Effect.values()[0];
                }
                Bukkit.getConsoleSender().sendMessage("Can't find effect for Selected Frame with this name, it was set to default");
            }
        }

        c.getW().addComment("Global.BounceAnimation", "Shows particle effect when player are being pushed back");
        this.BounceAnimation = c.get("Global.BounceAnimation", true);

        c.getW().addComment("Global.GUI.Enabled", "Enable or disable flag GUI");
        this.useFlagGUI = c.get("Global.GUI.Enabled", true);

        c.getW().addComment("Global.GUI.setTrue", "Item id and data to use when flag is set to true");

        int id = c.get("Global.GUI.setTrue.Id", 35);
        int data = c.get("Global.GUI.setTrue.Data", 13);

        Material Mat = Material.getMaterial(id);
        if (Mat == null) {
            Mat = Material.STONE;
        }
        this.GuiTrue = new ItemStack(Mat, 1, (short) data);

        c.getW().addComment("Global.GUI.setFalse", "Item id and data to use when flag is set to false");
        id = c.get("Global.GUI.setFalse.Id", 35);
        data = c.get("Global.GUI.setFalse.Data", 14);

        Mat = Material.getMaterial(id);
        if (Mat == null) {
            Mat = Material.STONE;
        }
        this.GuiFalse = new ItemStack(Mat, 1, (short) data);

        c.getW().addComment("Global.GUI.setRemove", "Item id and data to use when flag is set to remove");
        id = c.get("Global.GUI.setRemove.Id", 35);
        data = c.get("Global.GUI.setRemove.Data", 8);

        Mat = Material.getMaterial(id);
        if (Mat == null) {
            Mat = Material.STONE;
        }
        this.GuiRemove = new ItemStack(Mat, 1, (short) data);

        c.getW().addComment("Global.AutoMobRemoval",
            "Default = false. Enabling this, residences with flag nomobs will be cleared from monsters in regular intervals.",
            "This is quite heavy on server side, so enable only if you really need this feature");
        this.AutoMobRemoval = c.get("Global.AutoMobRemoval.Use", false);
        c.getW()
            .addComment("Global.AutoMobRemoval.Interval", "How often in seconds to check for monsters in residences. Keep it at reasonable amount");
        this.AutoMobRemovalInterval = c.get("Global.AutoMobRemoval.Interval", 3);

        this.enforceAreaInsideArea = c.get("Global.EnforceAreaInsideArea", false);
        this.spoutEnable = c.get("Global.EnableSpout", false);
        this.enableLeaseMoneyAccount = c.get("Global.EnableLeaseMoneyAccount", true);

        c.getW().addComment("Global.CouldronCompatability",
            "By setting this to true, partial compatability for kCouldron servers will be anabled. Action bar messages and selection visualizer will be disabled automaticaly as off incorrect compatability");
        this.CouldronCompatability = c.get("Global.CouldronCompatability", false);
        if (this.CouldronCompatability) {
            this.useVisualizer = false;
            this.actionBar = false;
            this.titleMessage = false;
            this.ActionBarOnSelection = false;
        }

        c.getW().addComment("DynMap.Use", "Enables or disable DynMap Support");
        this.DynMapUse = c.get("DynMap.Use", false);
        c.getW().addComment("DynMap.ShowFlags", "Shows or hides residence flags");
        this.DynMapShowFlags = c.get("DynMap.ShowFlags", true);
        c.getW().addComment("DynMap.HideHidden", "If set true, residence with hidden flag set to true will be hidden from dynmap");
        this.DynMapHideHidden = c.get("DynMap.HideHidden", true);

        c.getW().addComment("DynMap.Layer.3dRegions", "Enables 3D zones");
        this.DynMapLayer3dRegions = c.get("DynMap.Layer.3dRegions", true);
        c.getW().addComment("DynMap.Layer.SubZoneDepth", "How deep to go into subzones to show");
        this.DynMapLayerSubZoneDepth = c.get("DynMap.Layer.SubZoneDepth", 2);

        c.getW().addComment("DynMap.Border.Color", "Color of border. Pick color from this page http://www.w3schools.com/colors/colors_picker.asp");
        this.DynMapBorderColor = c.get("DynMap.Border.Color", "#FF0000");
        c.getW().addComment("DynMap.Border.Opacity", "Transparency. 0.3 means that only 30% of color will be visible");
        this.DynMapBorderOpacity = c.get("DynMap.Border.Opacity", 0.3);
        c.getW().addComment("DynMap.Border.Weight", "Border thickness");
        this.DynMapBorderWeight = c.get("DynMap.Border.Weight", 3);
        this.DynMapFillOpacity = c.get("DynMap.Fill.Opacity", 0.3);
        this.DynMapFillColor = c.get("DynMap.Fill.Color", "#FFFF00");
        this.DynMapFillForRent = c.get("DynMap.Fill.ForRent", "#33cc33");
        this.DynMapFillRented = c.get("DynMap.Fill.Rented", "#99ff33");
        this.DynMapFillForSale = c.get("DynMap.Fill.ForSale", "#0066ff");

        c.getW().addComment("DynMap.VisibleRegions", "Shows only regions on this list");
        this.DynMapVisibleRegions = c.get("DynMap.VisibleRegions", new ArrayList<String>());
        c.getW().addComment("DynMap.HiddenRegions", "Hides region on map even if its not hidden ingame");
        this.DynMapHiddenRegions = c.get("DynMap.HiddenRegions", new ArrayList<String>());

        try {
            c.getW().save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFlags() {
        FileConfiguration flags = YamlConfiguration.loadConfiguration(new File(this.plugin.dataFolder, "flags.yml"));
        this.globalCreatorDefaults = FlagPermissions.parseFromConfigNode("CreatorDefault", flags.getConfigurationSection("Global"));
        this.globalResidenceDefaults = FlagPermissions.parseFromConfigNode("ResidenceDefault", flags.getConfigurationSection("Global"));
        loadGroups();
    }

    public World getWorld(String name) {
        name = name.replace("_", "").replace(".", "");
        for (World one : Bukkit.getWorlds()) {
            if (one.getName().replace("_", "").replace(".", "").equalsIgnoreCase(name)) {
                return one;
            }
        }
        return null;
    }

    public void loadGroups() {
        FileConfiguration groups = YamlConfiguration.loadConfiguration(new File(this.plugin.dataFolder, "groups.yml"));
        ConfigurationSection node = groups.getConfigurationSection("Global.GroupDefault");
        if (node != null) {
            Set<String> keys = node.getConfigurationSection(this.defaultGroup).getKeys(false);
            if (keys != null) {
                for (String key : keys) {
                    this.globalGroupDefaults.put(key, FlagPermissions.parseFromConfigNodeAsList(this.defaultGroup, "false"));
                }
            }
        }
    }

    public static List<String> ColorsArray(List<String> text, Boolean colorize) {
        List<String> temp = new ArrayList<String>();
        for (String part : text) {
            if (colorize) {
                part = Colors(part);
            }
            temp.add(Colors(part));
        }
        return temp;
    }

    public static String Colors(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public void ChangeConfig(String path, Boolean stage) {
        File f = new File(this.plugin.getDataFolder(), "config.yml");

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

        if (!conf.isBoolean(path)) {
            return;
        }

        conf.set(path, stage);

        try {
            conf.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.plugin.getConfigManager().UpdateConfigFile();
    }

    void UpdateFlagFile() {

        File f = new File(this.plugin.getDataFolder(), "flags.yml");
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

        for (Flags fl : Flags.values()) {
            if (conf.isBoolean("Global.FlagPermission." + fl.getName())) {
                continue;
            }
            conf.createSection("Global.FlagPermission." + fl.getName());
            conf.set("Global.FlagPermission." + fl.getName(), fl.isEnabled());
        }

        if (!conf.isConfigurationSection("Global.FlagGui")) {
            conf.createSection("Global.FlagGui");
        }

        ConfigurationSection guiSection = conf.getConfigurationSection("Global.FlagGui");

        for (Flags fl : Flags.values()) {
            guiSection.set(fl.getName() + ".Id", fl.getId());
            guiSection.set(fl.getName() + ".Data", fl.getData());
        }

        try {
            conf.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void UpdateGroupedFlagsFile() {

        File f = new File(this.plugin.getDataFolder(), "flags.yml");
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

        if (!conf.isConfigurationSection("Global.GroupedFlags")) {
            conf.createSection("Global.GroupedFlags");
            conf.set("Global.GroupedFlags.redstone", Arrays.asList(
                Flags.note.getName(),
                Flags.pressure.getName(),
                Flags.lever.getName(),
                Flags.button.getName(),
                Flags.diode.getName()));
            conf.set("Global.GroupedFlags.craft", Arrays.asList(
                Flags.brew.getName(),
                Flags.table.getName(),
                Flags.enchant.getName()));
            conf.set("Global.GroupedFlags.trusted", Arrays.asList(
                Flags.use.getName(),
                Flags.tp.getName(),
                Flags.build.getName(),
                Flags.container.getName(),
                Flags.move.getName(),
                Flags.leash.getName(),
                Flags.animalkilling.getName(),
                Flags.mobkilling.getName(),
                Flags.shear.getName(),
                Flags.chat.getName()));
            conf.set("Global.GroupedFlags.fire", Arrays.asList(
                Flags.ignite.getName(),
                Flags.firespread.getName()));

            try {
                conf.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String oneGroup : conf.getConfigurationSection("Global.GroupedFlags").getKeys(false)) {
            for (String OneFlag : conf.getStringList("Global.GroupedFlags." + oneGroup)) {
                FlagPermissions.addFlagToFlagGroup(oneGroup, OneFlag);
            }
        }
    }

    public boolean isGlobalChatEnabled() {
        return this.GlobalChatEnabled;
    }

    public boolean isGlobalChatSelfModify() {
        return this.GlobalChatSelfModify;
    }

    public String getGlobalChatFormat() {
        return this.GlobalChatFormat;
    }

    public int getRentInformDelay() {
        return this.RentInformDelay;
    }

    public int getRentInformBefore() {
        return this.RentInformBefore;
    }

    public boolean isRentAllowAutoPay() {
        return this.RentAllowAutoPay;
    }

    public boolean isRentPlayerAutoPay() {
        return this.RentPlayerAutoPay;
    }

    public boolean isRentStayInMarket() {
        return this.RentStayInMarket;
    }

    public boolean isSellSubzone() {
        return this.SellSubzone;
    }

    public boolean isRentAllowRenewing() {
        return this.RentAllowRenewing;
    }

    public boolean isRentPreventRemoval() {
        return this.RentPreventRemoval;
    }

    public boolean isRentInformOnEnding() {
        return this.RentInformOnEnding;
    }

    public boolean isTNTExplodeBelow() {
        return this.TNTExplodeBelow;
    }

    public int getTNTExplodeBelowLevel() {
        return this.TNTExplodeBelowLevel;
    }

    public boolean isCreeperExplodeBelow() {
        return this.CreeperExplodeBelow;
    }

    public int getCreeperExplodeBelowLevel() {
        return this.CreeperExplodeBelowLevel;
    }

    public boolean useVisualizer() {
        return this.useVisualizer;
    }

    public int getVisualizerRange() {
        return this.VisualizerRange;
    }

    public int getVisualizerShowFor() {
        return this.VisualizerShowFor;
    }

    public int getNewPlayerRangeX() {
        return this.NewPlayerRangeX;
    }

    public int getNewPlayerRangeY() {
        return this.NewPlayerRangeY;
    }

    public int getNewPlayerRangeZ() {
        return this.NewPlayerRangeZ;
    }

    public int getVisualizerRowSpacing() {
        return this.VisualizerRowSpacing;
    }

    public int getVisualizerCollumnSpacing() {
        return this.VisualizerCollumnSpacing;
    }

    public int getVisualizerSkipBy() {
        return this.VisualizerSkipBy;
    }

    public int getVisualizerUpdateInterval() {
        return this.VisualizerUpdateInterval;
    }

    public ParticleEffects getSelectedFrame() {
        return this.SelectedFrame;
    }

    public ParticleEffects getSelectedSides() {
        return this.SelectedSides;
    }

    public ParticleEffects getOverlapFrame() {
        return this.OverlapFrame;
    }

    public ParticleEffects getOverlapSides() {
        return this.OverlapSides;
    }

    public Effect getSelectedSpigotFrame() {
        return this.SelectedSpigotFrame;
    }

    public Effect getSelectedSpigotSides() {
        return this.SelectedSpigotSides;
    }

    public Effect getOverlapSpigotFrame() {
        return this.OverlapSpigotFrame;
    }

    public Effect getOverlapSpigotSides() {
        return this.OverlapSpigotSides;
    }

    public int getTeleportDelay() {
        return this.TeleportDelay;
    }

    public boolean isTeleportTitleMessage() {
        return this.TeleportTitleMessage;
    }

    public boolean useLegacyPermissions() {
        return this.legacyperms;
    }

    public String getDefaultGroup() {
        return this.defaultGroup;
    }

    public String getResidenceNameRegex() {
        return this.namefix;
    }

    public boolean isExtraEnterMessage() {
        return this.ExtraEnterMessage;
    }

    public boolean enabledRentSystem() {
        return this.enableRentSystem && enableEconomy();
    }

    public boolean enableEconomy() {
        return this.enableEconomy && this.plugin.getEconomyManager() != null;
    }

    public boolean useLeases() {
        return this.useLeases;
    }

    public boolean useResMoneyBack() {
        return this.ResMoneyBack;
    }

    public boolean allowAdminsOnly() {
        return this.adminsOnly;
    }

    public boolean allowEmptyResidences() {
        return this.allowEmptyResidences;
    }

    public boolean isNoLava() {
        return this.NoLava;
    }

    public boolean isNoWater() {
        return this.NoWater;
    }

    public boolean isNoLavaPlace() {
        return this.NoLavaPlace;
    }

    public boolean isBlockFall() {
        return this.useBlockFall;
    }

    public boolean isNoWaterPlace() {
        return this.NoWaterPlace;
    }

    public List<Material> getLwcMatList() {
        return this.LwcMatList;
    }

    public boolean isRemoveLwcOnUnrent() {
        return this.LwcOnUnrent;
    }

    public boolean isRemoveLwcOnBuy() {
        return this.LwcOnBuy;
    }

    public boolean isRemoveLwcOnDelete() {
        return this.LwcOnDelete;
    }

    public boolean isUseResidenceFileClean() {
        return this.AutoCleanUp;
    }

    public int getResidenceFileCleanDays() {
        return this.AutoCleanUpDays;
    }

    public boolean isUseClean() {
        return this.UseClean;
    }

    public boolean isPvPFlagPrevent() {
        return this.PvPFlagPrevent;
    }

    public boolean isOverridePvp() {
        return this.OverridePvp;
    }

    public boolean isBlockAnyTeleportation() {
        return this.BlockAnyTeleportation;
    }

    public int getInfoToolID() {
        return this.infoToolId;
    }

    public int getSelectionTooldID() {
        return this.selectionToolId;
    }

    public boolean getOpsAreAdmins() {
        return this.adminOps;
    }

    public boolean getAdminFullAccess() {
        return this.AdminFullAccess;
    }

    public String getMultiworldPlugin() {
        return this.multiworldPlugin;
    }

    public boolean autoRenewLeases() {
        return this.leaseAutoRenew;
    }

    public boolean isShortInfoUse() {
        return this.ShortInfoUse;
    }

    public boolean isOnlyLike() {
        return this.OnlyLike;
    }

    public int getRentCheckInterval() {
        return this.rentCheckInterval;
    }

    public int getChatPrefixLength() {
        return this.chatPrefixLength;
    }

    public int getLeaseCheckInterval() {
        return this.leaseCheckInterval;
    }

    public int getAutoSaveInterval() {
        return this.autoSaveInt;
    }

    public boolean isNewSaveMechanic() {
        return this.NewSaveMechanic;
    }

    // backup stuff   
    public boolean BackupAutoCleanUpUse() {
        return this.BackupAutoCleanUpUse;
    }

    public int BackupAutoCleanUpDays() {
        return this.BackupAutoCleanUpDays;
    }

    public boolean UseZipBackup() {
        return this.UseZipBackup;
    }

    public boolean BackupWorldFiles() {
        return this.BackupWorldFiles;
    }

    public boolean BackupforsaleFile() {
        return this.BackupforsaleFile;
    }

    public boolean BackupleasesFile() {
        return this.BackupleasesFile;
    }

    public boolean BackuppermlistsFile() {
        return this.BackuppermlistsFile;
    }

    public boolean BackuprentFile() {
        return this.BackuprentFile;
    }

    public boolean BackupflagsFile() {
        return this.BackupflagsFile;
    }

    public boolean BackupgroupsFile() {
        return this.BackupgroupsFile;
    }

    public boolean BackupconfigFile() {
        return this.BackupconfigFile;
    }
    // backup stuff

    public int getFlowLevel() {
        return this.FlowLevel;
    }

    public int getPlaceLevel() {
        return this.PlaceLevel;
    }

    public int getBlockFallLevel() {
        return this.BlockFallLevel;
    }

    public int getCleanLevel() {
        return this.CleanLevel;
    }

    public boolean flagsInherit() {
        return this.flagsInherit;
    }

    public boolean chatEnabled() {
        return this.chatEnable;
    }

    public boolean useActionBar() {
        return this.actionBar;
    }

    public boolean useTitleMessage() {
        return this.titleMessage;
    }

    public boolean useActionBarOnSelection() {
        return this.ActionBarOnSelection;
    }

    public ChatColor getChatColor() {
        return this.chatColor;
    }

    public int getMinMoveUpdateInterval() {
        return this.minMoveUpdate;
    }

    public int getMaxResCount() {
        return this.MaxResCount;
    }

    public int getMaxRentCount() {
        return this.MaxRentCount;
    }

    public int getMaxSubzonesCount() {
        return this.MaxSubzonesCount;
    }

    public int getMaxSubzoneDepthCount() {
        return this.MaxSubzoneDepthCount;
    }

    public int getVoteRangeFrom() {
        return this.VoteRangeFrom;
    }

    public int getHealInterval() {
        return this.HealInterval;
    }

    public int getFeedInterval() {
        return this.FeedInterval;
    }

    public int getVoteRangeTo() {
        return this.VoteRangeTo;
    }

    public FlagPermissions getGlobalCreatorDefaultFlags() {
        return this.globalCreatorDefaults;
    }

    public FlagPermissions getGlobalResidenceDefaultFlags() {
        return this.globalResidenceDefaults;
    }

    public Map<String, FlagPermissions> getGlobalGroupDefaultFlags() {
        return this.globalGroupDefaults;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getDefaultWorld() {
        return this.DefaultWorld;
    }

    public String getDateFormat() {
        return this.DateFormat;
    }

    public String getTimeZone() {
        return this.TimeZone;
    }

    public boolean preventRentModify() {
        return this.preventBuildInRent;
    }

    public boolean isPreventSubZoneRemoval() {
        return this.PreventSubZoneRemoval;
    }

    public boolean stopOnSaveError() {
        return this.stopOnSaveError;
    }

    public boolean showIntervalMessages() {
        return this.showIntervalMessages;
    }

    public boolean ShowNoobMessage() {
        return this.ShowNoobMessage;
    }

    public boolean isNewPlayerUse() {
        return this.NewPlayerUse;
    }

    public boolean isNewPlayerFree() {
        return this.NewPlayerFree;
    }

    public boolean enableSpout() {
        return this.spoutEnable;
    }

    public boolean AutoMobRemoval() {
        return this.AutoMobRemoval;
    }

    public int AutoMobRemovalInterval() {
        return this.AutoMobRemovalInterval;
    }

    public boolean enableLeaseMoneyAccount() {
        return this.enableLeaseMoneyAccount;
    }

    public boolean CouldronCompatability() {
        return this.CouldronCompatability;
    }

    public boolean debugEnabled() {
        return this.enableDebug;
    }

    public boolean isSelectionIgnoreY() {
        return this.SelectionIgnoreY;
    }

    public boolean isSelectionIgnoreYInSubzone() {
        return this.SelectionIgnoreYInSubzone;
    }

    public boolean isNoCostForYBlocks() {
        return this.NoCostForYBlocks;
    }

    public boolean versionCheck() {
        return this.versionCheck;
    }

    public boolean isUUIDConvertion() {
        return this.UUIDConvertion;
    }

    public boolean isOfflineMode() {
        return this.OfflineMode;
    }

    public List<Integer> getCustomContainers() {
        return this.customContainers;
    }

    public List<Integer> getCustomBothClick() {
        return this.customBothClick;
    }

    public List<Integer> getCustomRightClick() {
        return this.customRightClick;
    }

    public List<Integer> getCleanBlocks() {
        return this.CleanBlocks;
    }

    public List<String> getNoFlowWorlds() {
        return this.NoFlowWorlds;
    }

    public List<String> getAutoCleanUpWorlds() {
        return this.AutoCleanUpWorlds;
    }

    public List<String> getNoPlaceWorlds() {
        return this.NoPlaceWorlds;
    }

    public List<String> getBlockFallWorlds() {
        return this.BlockFallWorlds;
    }

    public List<String> getNegativePotionEffects() {
        return this.NegativePotionEffects;
    }

    public List<String> getNegativeLingeringPotionEffects() {
        return this.NegativeLingeringPotionEffects;
    }

    public List<String> getCleanWorlds() {
        return this.CleanWorlds;
    }

    public List<String> getProtectedFlagsList() {
        return this.FlagsList;
    }

    public boolean getEnforceAreaInsideArea() {
        return this.enforceAreaInsideArea;
    }

    public ItemStack getGuiTrue() {
        return this.GuiTrue;
    }

    public ItemStack getGuiFalse() {
        return this.GuiFalse;
    }

    public ItemStack getGuiRemove() {
        return this.GuiRemove;
    }

    public List<RandomTeleport> getRandomTeleport() {
        return this.RTeleport;
    }

    public int getrtCooldown() {
        return this.rtCooldown;
    }

    public Location getKickLocation() {
        return this.KickLocation;
    }

    public Location getFlyLandLocation() {
        return this.FlyLandLocation;
    }

    public int getrtMaxTries() {
        return this.rtMaxTries;
    }

    public boolean useFlagGUI() {
        return this.useFlagGUI;
    }

    public boolean BounceAnimation() {
        return this.BounceAnimation;
    }

    public int getVisualizerFrameCap() {
        return this.VisualizerFrameCap;
    }

    public int getVisualizerSidesCap() {
        return this.VisualizerSidesCap;
    }

    public Double getWalkSpeed1() {
        return this.WalkSpeed1;
    }

    public Double getWalkSpeed2() {
        return this.WalkSpeed2;
    }

    public int getItemPickUpDelay() {
        return this.ItemPickUpDelay;
    }

    public boolean isAutomaticResidenceCreationCheckCollision() {
        return this.AutomaticResidenceCreationCheckCollision;
    }

    public String AutomaticResidenceCreationIncrementFormat() {
        return this.AutomaticResidenceCreationIncrementFormat;
    }

    public boolean isConsoleLogsShowFlagChanges() {
        return this.ConsoleLogsShowFlagChanges;
    }

//    public int getTownMinRange() {
//	return TownMinRange;
//    }
//
//    public boolean isTownEnabled() {
//	return TownEnabled;
//    }
}
