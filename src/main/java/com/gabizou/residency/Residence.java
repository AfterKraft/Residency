package com.gabizou.residency;

import com.earth2me.essentials.Essentials;
import com.gabizou.residency.allNms.v1_10Events;
import com.gabizou.residency.allNms.v1_8Events;
import com.gabizou.residency.allNms.v1_9Events;
import com.gabizou.residency.chat.ChatManager;
import com.gabizou.residency.containers.ABInterface;
import com.gabizou.residency.containers.Flags;
import com.gabizou.residency.containers.MinimizeFlags;
import com.gabizou.residency.containers.MinimizeMessages;
import com.gabizou.residency.containers.NMS;
import com.gabizou.residency.containers.lm;
import com.gabizou.residency.dynmap.DynMapListeners;
import com.gabizou.residency.dynmap.DynMapManager;
import com.gabizou.residency.economy.BlackHoleEconomy;
import com.gabizou.residency.economy.EconomyInterface;
import com.gabizou.residency.economy.TransactionManager;
import com.gabizou.residency.economy.rent.RentManager;
import com.gabizou.residency.gui.FlagUtil;
import com.gabizou.residency.itemlist.WorldItemManager;
import com.gabizou.residency.listeners.ResidenceBlockListener;
import com.gabizou.residency.listeners.ResidenceEntityListener;
import com.gabizou.residency.listeners.ResidenceFixesListener;
import com.gabizou.residency.listeners.ResidencePlayerListener;
import com.gabizou.residency.listeners.SpigotListener;
import com.gabizou.residency.permissions.PermissionManager;
import com.gabizou.residency.persistance.YMLSaveHelper;
import com.gabizou.residency.protection.ClaimedResidence;
import com.gabizou.residency.protection.FlagPermissions;
import com.gabizou.residency.protection.FlagPermissions.FlagCombo;
import com.gabizou.residency.protection.LeaseManager;
import com.gabizou.residency.protection.PermissionListManager;
import com.gabizou.residency.protection.PlayerManager;
import com.gabizou.residency.protection.ResidenceManager;
import com.gabizou.residency.protection.ResidencePermissions;
import com.gabizou.residency.protection.WorldFlagManager;
import com.gabizou.residency.selection.AutoSelection;
import com.gabizou.residency.selection.KingdomsUtil;
import com.gabizou.residency.selection.SchematicsManager;
import com.gabizou.residency.selection.SelectionManager;
import com.gabizou.residency.selection.WorldEditSelectionManager;
import com.gabizou.residency.selection.WorldGuardUtil;
import com.gabizou.residency.shopStuff.ShopListener;
import com.gabizou.residency.shopStuff.ShopSignUtil;
import com.gabizou.residency.signsStuff.SignUtil;
import com.gabizou.residency.text.Language;
import com.gabizou.residency.text.help.HelpEntry;
import com.gabizou.residency.text.help.InformationPager;
import com.gabizou.residency.utils.ActionBar;
import com.gabizou.residency.utils.CrackShot;
import com.gabizou.residency.utils.FileCleanUp;
import com.gabizou.residency.utils.RandomTp;
import com.gabizou.residency.utils.RawMessage;
import com.gabizou.residency.utils.Sorting;
import com.gabizou.residency.utils.TabComplete;
import com.gabizou.residency.utils.VersionChecker;
import com.gabizou.residency.utils.VersionChecker.Version;
import com.gabizou.residency.utils.YmlMaker;
import com.gabizou.residency.vaultinterface.ResidenceVaultAdapter;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.residence.mcstats.Metrics;
import com.residence.zip.ZipLibrary;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import cosine.boseconomy.BOSEconomy;
import fr.crafter.tickleman.realeconomy.RealEconomy;
import fr.crafter.tickleman.realplugin.RealPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.kingdoms.main.Kingdoms;
import org.kingdoms.manager.game.GameManagement;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.world.World;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

//import com.gabizou.residency.towns.TownManager;

/**
 * @author Gary Smoak - bekvon
 */
public class Residence extends JavaPlugin {

    private final static Set<String>
        validLanguages =
        new HashSet<String>(Arrays.asList("English", "Czech", "Chinese", "ChineseTW", "French", "Spanish"));
    private static Residence instance;
    public PermissionManager gmanager;
    public WorldItemManager imanager;
    public WorldFlagManager wmanager;
    public HelpEntry helppages;
    public File dataFolder;
    public Map<String, String> deleteConfirm;
    public Map<String, String> UnrentConfirm = new HashMap<String, String>();
    public List<String> resadminToggle;
    public HashMap<String, Long> rtMap = new HashMap<String, Long>();
    public List<String> teleportDelayMap = new ArrayList<String>();
    public HashMap<String, ClaimedResidence> teleportMap = new HashMap<String, ClaimedResidence>();
    protected String ResidenceVersion;
    protected List<String> authlist;
    protected ResidenceManager rmanager;
    protected SelectionManager smanager;
    protected ConfigManager cmanager;
    protected boolean spigotPlatform = false;
    protected SignUtil signmanager;
    protected ResidenceBlockListener blistener;
    protected ResidencePlayerListener plistener;
    protected ResidenceEntityListener elistener;
    protected ResidenceFixesListener flistener;
    protected SpigotListener spigotlistener;
    protected ShopListener shlistener;
    protected TransactionManager tmanager;
    protected PermissionListManager pmanager;
    protected LeaseManager leasemanager;
    protected RentManager rentmanager;
    protected ChatManager chatmanager;
    protected Server server;
    protected LocaleManager LocaleManager;
    protected Language NewLanguageManager;
    protected PlayerManager PlayerManager;
    protected FlagUtil FlagUtilManager;
    protected ShopSignUtil ShopSignUtilManager;
    //    private TownManager townManager;
    protected RandomTp RandomTpManager;
    protected DynMapManager DynManager;
    protected Sorting SortingManager;
    protected ActionBar ABManager;
    protected AutoSelection AutoSelectionManager;
    protected SchematicsManager SchematicManager;
    protected CommandFiller cmdFiller;
    protected ZipLibrary zip;
    protected boolean firstenable = true;
    protected EconomyInterface economy;
    protected int leaseBukkitId = -1;
    protected int rentBukkitId = -1;
    protected int healBukkitId = -1;
    protected int feedBukkitId = -1;
    protected int DespawnMobsBukkitId = -1;
    protected int autosaveBukkitId = -1;
    protected VersionChecker versionChecker;
    protected boolean initsuccess = false;
    private InformationPager InformationPagerManager;
    private WorldGuardUtil worldGuardUtil;
    private KingdomsUtil kingdomsUtil;
    private int saveVersion = 1;
    private ConcurrentHashMap<String, User> OfflinePlayerList = new ConcurrentHashMap<>();
    private Map<UUID, User> cachedPlayerNameUUIDs = new HashMap<>();
    private WorldEditPlugin wep = null;
    private WorldGuardPlugin wg = null;
    private int wepid;
    private String ServerLandname = "Server_Land";
    private String ServerLandUUID = "00000000-0000-0000-0000-000000000000";
    private String TempUserUUID = "ffffffff-ffff-ffff-ffff-ffffffffffff";
    private ABInterface ab;
    private NMS nms;
    private String prefix = ChatColor.GREEN + "[" + ChatColor.GOLD + "Residence" + ChatColor.GREEN + "]" + ChatColor.GRAY;
    // API
    private ResidenceApi API = new ResidenceApi();
    private MarketBuyInterface MarketBuyAPI = null;
    private MarketRentInterface MarketRentAPI = null;
    private ResidencePlayerInterface PlayerAPI = null;
    private ResidenceInterface ResidenceAPI = null;
    private ChatInterface ChatAPI = null;
    private Runnable doHeals = () -> Residence.this.plistener.doHeals();
    private Runnable doFeed = () -> Residence.this.plistener.feed();
    private Runnable DespawnMobs = () -> Residence.this.plistener.DespawnMobs();
    private Runnable rentExpire = () -> {
        Residence.this.rentmanager.checkCurrentRents();
        if (Residence.this.cmanager.showIntervalMessages()) {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " - Rent Expirations checked!");
        }
    };
    private Runnable leaseExpire = () -> {
        Residence.this.leasemanager.doExpirations();
        if (Residence.this.cmanager.showIntervalMessages()) {
            if (Sponge.isServerAvailable()) {
                Sponge.getServer().getBroadcastChannel().send(getPrefix() + " - Lease Expirations checked!");
            }
        }
    };
    private Runnable autoSave = () -> {
        try {
            if (Residence.this.initsuccess) {
                Task.builder().async()
                    .execute(() -> {
                        try {
                            saveYml();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .submit(this.getPlugin());
            }
        } catch (Exception ex) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, getPrefix() + " SEVERE SAVE ERROR", ex);
        }
    };
    private GameManagement kingdomsmanager = null;

    private static void remove(File newGroups, List<String> list) throws IOException {

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(newGroups);
        conf.options().copyDefaults(true);

        for (String one : list) {
            conf.set(one, null);
        }
        try {
            conf.save(newGroups);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copy(File source, File target) throws IOException {
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(target);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static Residence getInstance() {
        return instance;
    }
    // API end

    public boolean isSpigot() {
        return this.spigotPlatform;
    }

    public HashMap<String, ClaimedResidence> getTeleportMap() {
        return this.teleportMap;
    }

    public List<String> getTeleportDelayMap() {
        return this.teleportDelayMap;
    }

    public HashMap<String, Long> getRandomTeleportMap() {
        return this.rtMap;
    }

    public ResidencePlayerInterface getPlayerManagerAPI() {
        if (this.PlayerAPI == null) {
            this.PlayerAPI = this.PlayerManager;
        }
        return this.PlayerAPI;
    }

    public ResidenceInterface getResidenceManagerAPI() {
        if (this.ResidenceAPI == null) {
            this.ResidenceAPI = this.rmanager;
        }
        return this.ResidenceAPI;
    }

    public MarketRentInterface getMarketRentManagerAPI() {
        if (this.MarketRentAPI == null) {
            this.MarketRentAPI = this.rentmanager;
        }
        return this.MarketRentAPI;
    }

    public MarketBuyInterface getMarketBuyManagerAPI() {
        if (this.MarketBuyAPI == null) {
            this.MarketBuyAPI = this.tmanager;
        }
        return this.MarketBuyAPI;

    }

    public ChatInterface getResidenceChatAPI() {
        if (this.ChatAPI == null) {
            this.ChatAPI = this.chatmanager;
        }
        return this.ChatAPI;
    }

    public ResidenceApi getAPI() {
        return this.API;
    }

    public NMS getNms() {
        return this.nms;
    }

    public ABInterface getAB() {
        return this.ab;
    }

    public void reloadPlugin() {
        this.onDisable();
        this.reloadConfig();
        this.onEnable();
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        this.server.getScheduler().cancelTask(this.autosaveBukkitId);
        this.server.getScheduler().cancelTask(this.healBukkitId);
        this.server.getScheduler().cancelTask(this.feedBukkitId);

        this.server.getScheduler().cancelTask(this.DespawnMobsBukkitId);

        if (this.cmanager.useLeases()) {
            this.server.getScheduler().cancelTask(this.leaseBukkitId);
        }
        if (this.cmanager.enabledRentSystem()) {
            this.server.getScheduler().cancelTask(this.rentBukkitId);
        }

        if (getDynManager() != null && getDynManager().getMarkerSet() != null) {
            getDynManager().getMarkerSet().deleteMarkerSet();
        }

        if (this.initsuccess) {
            try {
                saveYml();
                if (this.zip != null) {
                    this.zip.backup();
                }
            } catch (Exception ex) {
                Logger.getLogger("Minecraft").log(Level.SEVERE, "[Residence] SEVERE SAVE ERROR", ex);
            }

//	    File file = new File(this.getDataFolder(), "uuids.yml");
//	    YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
//	    if (!conf.isConfigurationSection("UUIDS"))
//		conf.createSection("UUIDS");
//	    for (Entry<UUID, String> one : getCachedPlayerNameUUIDs().entrySet()) {
//		conf.set("UUIDS." + one.getKey().toString(), one.getValue());
//	    }
//	    try {
//		conf.save(file);
//	    } catch (IOException e) {
//		e.printStackTrace();
//	    }

            Bukkit.getConsoleSender().sendMessage(getPrefix() + " Disabled!");
        }
    }

    public DynMapManager getDynManager() {
        return this.DynManager;
    }

    private void saveYml() throws IOException {
        File saveFolder = new File(this.dataFolder, "Save");
        File worldFolder = new File(saveFolder, "Worlds");
        worldFolder.mkdirs();
        YMLSaveHelper yml;
        Map<String, Object> save = this.rmanager.save();
        for (Entry<String, Object> entry : save.entrySet()) {
            File ymlSaveLoc = new File(worldFolder, "res_" + entry.getKey() + ".yml");
            File tmpFile = new File(worldFolder, "tmp_res_" + entry.getKey() + ".yml");
            yml = new YMLSaveHelper(tmpFile);
            yml.getRoot().put("Version", this.saveVersion);
            World world = this.server.getWorld(entry.getKey());
            if (world != null) {
                yml.getRoot().put("Seed", world.getSeed());
            }
            if (this.getResidenceManager().getMessageCatch(entry.getKey()) != null) {
                yml.getRoot().put("Messages", this.getResidenceManager().getMessageCatch(entry.getKey()));
            }
            if (this.getResidenceManager().getFlagsCatch(entry.getKey()) != null) {
                yml.getRoot().put("Flags", this.getResidenceManager().getFlagsCatch(entry.getKey()));
            }
            yml.getRoot().put("Residences", entry.getValue());
            yml.save();
            if (ymlSaveLoc.isFile()) {
                File backupFolder = new File(worldFolder, "Backup");
                backupFolder.mkdirs();
                File backupFile = new File(backupFolder, "res_" + entry.getKey() + ".yml");
                if (backupFile.isFile()) {
                    backupFile.delete();
                }
                ymlSaveLoc.renameTo(backupFile);
            }
            tmpFile.renameTo(ymlSaveLoc);
        }

        // For Sale save
        File ymlSaveLoc = new File(saveFolder, "forsale.yml");
        File tmpFile = new File(saveFolder, "tmp_forsale.yml");
        yml = new YMLSaveHelper(tmpFile);
        yml.save();
        yml.getRoot().put("Version", this.saveVersion);
        yml.getRoot().put("Economy", this.tmanager.save());
        yml.save();
        if (ymlSaveLoc.isFile()) {
            File backupFolder = new File(saveFolder, "Backup");
            backupFolder.mkdirs();
            File backupFile = new File(backupFolder, "forsale.yml");
            if (backupFile.isFile()) {
                backupFile.delete();
            }
            ymlSaveLoc.renameTo(backupFile);
        }
        tmpFile.renameTo(ymlSaveLoc);

        // Leases save
        ymlSaveLoc = new File(saveFolder, "leases.yml");
        tmpFile = new File(saveFolder, "tmp_leases.yml");
        yml = new YMLSaveHelper(tmpFile);
        yml.getRoot().put("Version", this.saveVersion);
        yml.getRoot().put("Leases", this.leasemanager.save());
        yml.save();
        if (ymlSaveLoc.isFile()) {
            File backupFolder = new File(saveFolder, "Backup");
            backupFolder.mkdirs();
            File backupFile = new File(backupFolder, "leases.yml");
            if (backupFile.isFile()) {
                backupFile.delete();
            }
            ymlSaveLoc.renameTo(backupFile);
        }
        tmpFile.renameTo(ymlSaveLoc);

        // permlist save
        ymlSaveLoc = new File(saveFolder, "permlists.yml");
        tmpFile = new File(saveFolder, "tmp_permlists.yml");
        yml = new YMLSaveHelper(tmpFile);
        yml.getRoot().put("Version", this.saveVersion);
        yml.getRoot().put("PermissionLists", this.pmanager.save());
        yml.save();
        if (ymlSaveLoc.isFile()) {
            File backupFolder = new File(saveFolder, "Backup");
            backupFolder.mkdirs();
            File backupFile = new File(backupFolder, "permlists.yml");
            if (backupFile.isFile()) {
                backupFile.delete();
            }
            ymlSaveLoc.renameTo(backupFile);
        }
        tmpFile.renameTo(ymlSaveLoc);

        // rent save
        ymlSaveLoc = new File(saveFolder, "rent.yml");
        tmpFile = new File(saveFolder, "tmp_rent.yml");
        yml = new YMLSaveHelper(tmpFile);
        yml.getRoot().put("Version", this.saveVersion);
        yml.getRoot().put("RentSystem", this.rentmanager.save());
        yml.save();
        if (ymlSaveLoc.isFile()) {
            File backupFolder = new File(saveFolder, "Backup");
            backupFolder.mkdirs();
            File backupFile = new File(backupFolder, "rent.yml");
            if (backupFile.isFile()) {
                backupFile.delete();
            }
            ymlSaveLoc.renameTo(backupFile);
        }
        tmpFile.renameTo(ymlSaveLoc);

        if (this.cmanager.showIntervalMessages()) {
            System.out.println("[Residence] - Saved Residences...");
        }
    }

    public String getPrefix() {
        return this.prefix;
    }

    public ResidenceManager getResidenceManager() {
        return this.rmanager;
    }

    @Listener
    public void onEnable(GameConstructionEvent event) {
        try {
            instance = this;
            this.initsuccess = false;
            this.versionChecker = new VersionChecker(this);
            this.deleteConfirm = new HashMap<String, String>();
            this.resadminToggle = new ArrayList<String>();
            this.server = this.getServer();
            this.dataFolder = this.getDataFolder();

            this.ResidenceVersion = this.getDescription().getVersion();
            this.authlist = this.getDescription().getAuthors();

            this.cmdFiller = new CommandFiller();
            this.cmdFiller.fillCommands();

            this.SortingManager = new Sorting();

            if (!this.dataFolder.isDirectory()) {
                this.dataFolder.mkdirs();
            }

            if (!new File(this.dataFolder, "groups.yml").isFile() && !new File(this.dataFolder, "flags.yml").isFile() && new File(this.dataFolder,
                "config.yml").isFile()) {
                this.ConvertFile();
            }

            if (!new File(this.dataFolder, "config.yml").isFile()) {
                this.writeDefaultConfigFromJar();
            }

            if (!new File(this.dataFolder, "uuids.yml").isFile()) {
                File file = new File(this.getDataFolder(), "uuids.yml");
                file.createNewFile();
            }

            if (!new File(this.dataFolder, "flags.yml").isFile()) {
                this.writeDefaultFlagsFromJar();
            }
            if (!new File(this.dataFolder, "groups.yml").isFile()) {
                this.writeDefaultGroupsFromJar();
            }
            this.getCommand("res").setTabCompleter(new TabComplete());
            this.getCommand("resadmin").setTabCompleter(new TabComplete());
            this.getCommand("residence").setTabCompleter(new TabComplete());

//	    Residence.getConfigManager().UpdateConfigFile();

//	    if (this.getConfig().getInt("ResidenceVersion", 0) == 0) {
//		this.writeDefaultConfigFromJar();
//		this.getConfig().load("config.yml");
//		System.out.println("[Residence] Config Invalid, wrote default...");
//	    }

            this.cmanager = new ConfigManager(this);
            String multiworld = this.cmanager.getMultiworldPlugin();
            if (multiworld != null) {
                Plugin plugin = this.server.getPluginManager().getPlugin(multiworld);
                if (plugin != null) {
                    if (!plugin.isEnabled()) {
                        Bukkit.getConsoleSender().sendMessage(getPrefix() + " - Enabling multiworld plugin: " + multiworld);
                        this.server.getPluginManager().enablePlugin(plugin);
                    }
                }
            }
            this.FlagUtilManager = new FlagUtil(this);
            getFlagUtilManager().load();

            try {
                Class<?> c = Class.forName("org.bukkit.entity.Player");
                for (Method one : c.getDeclaredMethods()) {
                    if (one.getName().equalsIgnoreCase("Spigot")) {
                        this.spigotPlatform = true;
                    }
                }
            } catch (Exception e) {
            }

            String version = this.versionChecker.getVersion().getShortVersion();
            try {
                Class<?> nmsClass;
                if (getConfigManager().CouldronCompatability()) {
                    nmsClass = Class.forName("com.gabizou.residency.allNms.v1_7_Couldron");
                } else {
                    nmsClass = Class.forName("com.gabizou.residency.allNms." + version);
                }
                if (NMS.class.isAssignableFrom(nmsClass)) {
                    this.nms = (NMS) nmsClass.getConstructor().newInstance();
                } else {
                    System.out.println("Something went wrong, please note down version and contact author v:" + version);
                    this.setEnabled(false);
                    Bukkit.shutdown();
                }
            } catch (SecurityException | NoSuchMethodException | InvocationTargetException | IllegalArgumentException | IllegalAccessException | InstantiationException
                | ClassNotFoundException e) {
                Bukkit.getConsoleSender().sendMessage(
                    ChatColor.RED + "Your server version is not compatible with this plugins version! Plugin will be disabled: " + version
                    + " and server will shutdown");
                this.setEnabled(false);
                Bukkit.shutdown();
                return;
            }

            this.ab = new ActionBar(this);

            this.gmanager = new PermissionManager(this);
            this.imanager = new WorldItemManager(this);
            this.wmanager = new WorldFlagManager(this);

            this.chatmanager = new ChatManager();
            this.rentmanager = new RentManager(this);

            this.LocaleManager = new LocaleManager(this);

            this.PlayerManager = new PlayerManager(this);
            this.ShopSignUtilManager = new ShopSignUtil(this);
            this.RandomTpManager = new RandomTp(this);
//	    townManager = new TownManager(this);

            this.InformationPagerManager = new InformationPager(this);

            this.zip = new ZipLibrary(this);

            Plugin lwcp = Bukkit.getPluginManager().getPlugin("LWC");
            if (lwcp != null) {
                lwc = ((LWCPlugin) lwcp).getLWC();
            }

            for (String lang : validLanguages) {
                YmlMaker langFile = new YmlMaker(this, "Language" + File.separator + lang + ".yml");
                langFile.saveDefaultConfig();
            }
            validLanguages.add(getConfigManager().getLanguage());

            for (String lang : validLanguages) {
                getLocaleManager().LoadLang(lang);
            }

            getConfigManager().UpdateFlagFile();

            try {
                File langFile = new File(new File(this.dataFolder, "Language"), this.cmanager.getLanguage() + ".yml");

                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(new FileInputStream(langFile), "UTF8"));
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }

                if (langFile.isFile()) {
                    FileConfiguration langconfig = new YamlConfiguration();
                    langconfig.load(in);
                    this.helppages = HelpEntry.parseHelp(langconfig, "CommandHelp");
                } else {
                    Bukkit.getConsoleSender().sendMessage(getPrefix() + " Language file does not exist...");
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " Failed to load language file: " + this.cmanager.getLanguage()
                                                      + ".yml setting to default - English");

                File langFile = new File(new File(this.dataFolder, "Language"), "English.yml");

                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(new FileInputStream(langFile), "UTF8"));
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }

                if (langFile.isFile()) {
                    FileConfiguration langconfig = new YamlConfiguration();
                    langconfig.load(in);
                    this.helppages = HelpEntry.parseHelp(langconfig, "CommandHelp");
                } else {
                    Bukkit.getConsoleSender().sendMessage(getPrefix() + " Language file does not exist...");
                }
                if (in != null) {
                    in.close();
                }
            }
            this.economy = null;
            if (this.getConfig().getBoolean("Global.EnableEconomy", false)) {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " Scanning for economy systems...");
                if (this.gmanager.getPermissionsPlugin() instanceof ResidenceVaultAdapter) {
                    ResidenceVaultAdapter vault = (ResidenceVaultAdapter) this.gmanager.getPermissionsPlugin();
                    if (vault.economyOK()) {
                        this.economy = vault;
                        Bukkit.getConsoleSender().sendMessage(getPrefix() + " Found Vault using economy system: " + vault.getEconomyName());
                    }
                }
                if (this.economy == null) {
                    this.loadVaultEconomy();
                }
                if (this.economy == null) {
                    this.loadBOSEconomy();
                }
                if (this.economy == null) {
                    this.loadEssentialsEconomy();
                }
                if (this.economy == null) {
                    this.loadRealEconomy();
                }
                if (this.economy == null) {
                    this.loadIConomy();
                }
                if (this.economy == null) {
                    Bukkit.getConsoleSender().sendMessage(getPrefix() + " Unable to find an economy system...");
                    this.economy = new BlackHoleEconomy();
                }
            }

            // Only fill if we need to convert player data
            if (getConfigManager().isUUIDConvertion()) {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " Loading (" + Bukkit.getOfflinePlayers().length + ") player data");
                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    if (player == null) {
                        continue;
                    }
                    String name = player.getName();
                    if (name == null) {
                        continue;
                    }
                    this.addOfflinePlayerToChache(player);
                }
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " Player data loaded: " + this.OfflinePlayerList.size());
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(this, (Runnable) () -> {
                    for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                        if (player == null) {
                            continue;
                        }
                        String name = player.getName();
                        if (name == null) {
                            continue;
                        }
                        addOfflinePlayerToChache(player);
                    }
                    return;
                });
            }

            this.rmanager = new ResidenceManager(this);

            this.leasemanager = new LeaseManager(this);

            this.tmanager = new TransactionManager(this);

            this.pmanager = new PermissionListManager(this);

            try {
                this.loadYml();
            } catch (Exception e) {
                this.getLogger().log(Level.SEVERE, "Unable to load save file", e);
                throw e;
            }

            this.signmanager = new SignUtil(this);
            getSignUtil().LoadSigns();

            if (getConfigManager().isUseResidenceFileClean()) {
                (new FileCleanUp(this)).cleanFiles();
            }

            if (this.firstenable) {
                if (!this.isEnabled()) {
                    return;
                }
                FlagPermissions.initValidFlags();

                setWorldEdit();
                setWorldGuard();

                setKingdoms();

                this.blistener = new ResidenceBlockListener(this);
                this.plistener = new ResidencePlayerListener(this);
                this.elistener = new ResidenceEntityListener(this);
                this.flistener = new ResidenceFixesListener();

                this.shlistener = new ShopListener(this);
                this.spigotlistener = new SpigotListener();

                PluginManager pm = getServer().getPluginManager();
                pm.registerEvents(this.blistener, this);
                pm.registerEvents(this.plistener, this);
                pm.registerEvents(this.elistener, this);
                pm.registerEvents(this.flistener, this);
                pm.registerEvents(this.shlistener, this);

                // 1.8 event
                if (getVersionChecker().isHigherEquals(Version.v1_8_R1)) {
                    pm.registerEvents(new v1_8Events(), this);
                }

                // 1.9 event
                if (getVersionChecker().isHigherEquals(Version.v1_9_R1)) {
                    pm.registerEvents(new v1_9Events(), this);
                }

                // 1.10 event
                if (getVersionChecker().isHigherEquals(Version.v1_10_R1)) {
                    pm.registerEvents(new v1_10Events(), this);
                }

                this.firstenable = false;
            } else {
                this.plistener.reload();
            }

            this.NewLanguageManager = new Language(this);
            getLM().LanguageReload();

            this.AutoSelectionManager = new AutoSelection(this);

            if (this.wep != null) {
                this.SchematicManager = new SchematicsManager(this);
            }

            try {
                Class.forName("org.bukkit.event.player.PlayerItemDamageEvent");
                getServer().getPluginManager().registerEvents(this.spigotlistener, this);
            } catch (Exception e) {
            }

            if (getServer().getPluginManager().getPlugin("CrackShot") != null) {
                getServer().getPluginManager().registerEvents(new CrackShot(this), this);
            }

            // DynMap
            Plugin dynmap = Bukkit.getPluginManager().getPlugin("dynmap");
            if (dynmap != null && getConfigManager().DynMapUse) {
                this.DynManager = new DynMapManager(this);
                getServer().getPluginManager().registerEvents(new DynMapListeners(this), this);
                getDynManager().api = (DynmapAPI) dynmap;
                getDynManager().activate();
            }

            int autosaveInt = this.cmanager.getAutoSaveInterval();
            if (autosaveInt < 1) {
                autosaveInt = 1;
            }
            autosaveInt = autosaveInt * 60 * 20;
            this.autosaveBukkitId = this.server.getScheduler().scheduleSyncRepeatingTask(this, this.autoSave, autosaveInt, autosaveInt);
            this.healBukkitId =
                this.server.getScheduler().scheduleSyncRepeatingTask(this, this.doHeals, 20, getConfigManager().getHealInterval() * 20);
            this.feedBukkitId =
                this.server.getScheduler().scheduleSyncRepeatingTask(this, this.doFeed, 20, getConfigManager().getFeedInterval() * 20);
            if (getConfigManager().AutoMobRemoval()) {
                this.DespawnMobsBukkitId = this.server
                    .getScheduler().scheduleSyncRepeatingTask(this, this.DespawnMobs, 20 * getConfigManager().AutoMobRemovalInterval(), 20
                                                                                                                                        * getConfigManager()
                                                                                                                                            .AutoMobRemovalInterval());
            }

            if (this.cmanager.useLeases()) {
                int leaseInterval = this.cmanager.getLeaseCheckInterval();
                if (leaseInterval < 1) {
                    leaseInterval = 1;
                }
                leaseInterval = leaseInterval * 60 * 20;
                this.leaseBukkitId = this.server.getScheduler().scheduleSyncRepeatingTask(this, this.leaseExpire, leaseInterval, leaseInterval);
            }
            if (this.cmanager.enabledRentSystem()) {
                int rentint = this.cmanager.getRentCheckInterval();
                if (rentint < 1) {
                    rentint = 1;
                }
                rentint = rentint * 60 * 20;
                this.rentBukkitId = this.server.getScheduler().scheduleSyncRepeatingTask(this, this.rentExpire, rentint, rentint);
            }
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (getPermissionManager().isResidenceAdmin(player)) {
                    turnResAdminOn(player);
                }
            }
            try {
                Metrics metrics = new Metrics(this);
                metrics.start();
            } catch (IOException e) {
                // Failed to submit the stats :-(
            }
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " Enabled! Version " + this.getDescription().getVersion() + " by Zrips");
            this.initsuccess = true;

        } catch (Exception ex) {
            this.initsuccess = false;
            getServer().getPluginManager().disablePlugin(this);
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " - FAILED INITIALIZATION! DISABLED! ERROR:");
            Logger.getLogger(Residence.class.getName()).log(Level.SEVERE, null, ex);
            Bukkit.getServer().shutdown();
        }

        getShopSignUtilManager().LoadShopVotes();
        getShopSignUtilManager().LoadSigns();
        getShopSignUtilManager().BoardUpdate();
        getVersionChecker().VersionCheck(null);

    }

    public SignUtil getSignUtil() {
        return this.signmanager;
    }

    public void consoleMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', getPrefix() + " " + message));
    }

    public boolean validName(String name) {
        if (name.contains(":") || name.contains(".") || name.contains("|")) {
            return false;
        }
        if (this.cmanager.getResidenceNameRegex() == null) {
            return true;
        }
        String namecheck = name.replaceAll(this.cmanager.getResidenceNameRegex(), "");
        if (!name.equals(namecheck)) {
            return false;
        }
        return true;
    }

    private void setWorldEdit() {
        Plugin plugin = this.server.getPluginManager().getPlugin("WorldEdit");
        if (plugin != null) {
            this.smanager = new WorldEditSelectionManager(this.server, this);
            this.wep = (WorldEditPlugin) plugin;
            this.wepid = this.getWorldEdit().getConfig().getInt("wand-item");
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " Found WorldEdit");
        } else {
            this.smanager = new SelectionManager(this.server, this);
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " WorldEdit NOT found!");
        }
    }

    private void setKingdoms() {
        if (Bukkit.getPluginManager().getPlugin("Kingdoms") != null) {
            try {
                this.kingdomsmanager = Kingdoms.getManagers();
            } catch (NoClassDefFoundError | Exception e) {

            }
        }
    }

    public GameManagement getKingdomsManager() {
        return this.kingdomsmanager;
    }

    private void setWorldGuard() {
        Plugin wgplugin = this.server.getPluginManager().getPlugin("WorldGuard");
        if (wgplugin != null) {
            try {
                Class.forName("com.sk89q.worldedit.BlockVector");
                Class.forName("com.sk89q.worldguard.protection.ApplicableRegionSet");
                Class.forName("com.sk89q.worldguard.protection.managers.RegionManager");
                Class.forName("com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion");
                Class.forName("com.sk89q.worldguard.protection.regions.ProtectedRegion");
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + ChatColor.RED
                                                      + " Found WorldGuard, but its not supported by Residence plugin. Please update WorldGuard to latest version");
                return;
            }
            this.wg = (WorldGuardPlugin) wgplugin;
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " Found WorldGuard");
        }
    }

    public Residence getPlugin() {
        return this;
    }

    public VersionChecker getVersionChecker() {
        return this.versionChecker;
    }

    public LWC getLwc() {
        return lwc;
    }

    public File getDataLocation() {
        return this.dataFolder;
    }

    public ShopSignUtil getShopSignUtilManager() {
        return this.ShopSignUtilManager;
    }

    public CommandFiller getCommandFiller() {
        if (this.cmdFiller == null) {
            this.cmdFiller = new CommandFiller();
            this.cmdFiller.fillCommands();
        }
        return this.cmdFiller;
    }

    public SelectionManager getSelectionManager() {
        return this.smanager;
    }

    public FlagUtil getFlagUtilManager() {
        return this.FlagUtilManager;
    }

    public PermissionManager getPermissionManager() {
        return this.gmanager;
    }

    public PermissionListManager getPermissionListManager() {
        return this.pmanager;
    }

    public SchematicsManager getSchematicManager() {
        return this.SchematicManager;
    }

    public AutoSelection getAutoSelectionManager() {
        return this.AutoSelectionManager;
    }

    public Sorting getSortingManager() {
        return this.SortingManager;
    }

    public RandomTp getRandomTpManager() {
        return this.RandomTpManager;
    }

    public EconomyInterface getEconomyManager() {
        return this.economy;
    }

    public LeaseManager getLeaseManager() {
        return this.leasemanager;
    }

    public PlayerManager getPlayerManager() {
        return this.PlayerManager;
    }

    public HelpEntry getHelpPages() {
        return this.helppages;
    }

    public TransactionManager getTransactionManager() {
        return this.tmanager;
    }

    public WorldItemManager getItemManager() {
        return this.imanager;
    }

    public WorldFlagManager getWorldFlags() {
        return this.wmanager;
    }

    public RentManager getRentManager() {
        return this.rentmanager;
    }

    public LocaleManager getLocaleManager() {
        return this.LocaleManager;
    }

    public ResidencePlayerListener getPlayerListener() {
        return this.plistener;
    }

    public ResidenceBlockListener getBlockListener() {
        return this.blistener;
    }

    public ResidenceEntityListener getEntityListener() {
        return this.elistener;
    }

    public ChatManager getChatManager() {
        return this.chatmanager;
    }

    public String getResidenceVersion() {
        return this.ResidenceVersion;
    }

    public List<String> getAuthors() {
        return this.authlist;
    }

    public FlagPermissions getPermsByLoc(Location loc) {
        ClaimedResidence res = this.rmanager.getByLoc(loc);
        if (res != null) {
            return res.getPermissions();
        }
        return this.wmanager.getPerms(loc.getWorld().getName());

    }

    public FlagPermissions getPermsByLocForPlayer(Location loc, Player player) {
        ClaimedResidence res = this.rmanager.getByLoc(loc);
        if (res != null) {
            return res.getPermissions();
        }
        if (player != null) {
            return this.wmanager.getPerms(player);
        }

        return this.wmanager.getPerms(loc.getWorld().getName());
    }

    private void loadIConomy() {
        Plugin p = getServer().getPluginManager().getPlugin("iConomy");
        if (p != null) {
            if (p.getDescription().getVersion().startsWith("6")) {
                this.economy = new IConomy6Adapter((com.iCo6.iConomy) p);
            } else if (p.getDescription().getVersion().startsWith("5")) {
                this.economy = new IConomy5Adapter();
            } else {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " UNKNOWN iConomy version!");
                return;
            }
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " Successfully linked with iConomy! Version: " + p.getDescription().getVersion());
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " iConomy NOT found!");
        }
    }

    private void loadBOSEconomy() {
        Plugin p = getServer().getPluginManager().getPlugin("BOSEconomy");
        if (p != null) {
            this.economy = new BOSEAdapter((BOSEconomy) p);
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " Successfully linked with BOSEconomy!");
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " BOSEconomy NOT found!");
        }
    }

    private void loadEssentialsEconomy() {
        Plugin p = getServer().getPluginManager().getPlugin("Essentials");
        if (p != null) {
            this.economy = new EssentialsEcoAdapter((Essentials) p);
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " Successfully linked with Essentials Economy!");
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " Essentials Economy NOT found!");
        }
    }

    private void loadRealEconomy() {
        Plugin p = getServer().getPluginManager().getPlugin("RealPlugin");
        if (p != null) {
            this.economy = new RealShopEconomy(new RealEconomy((RealPlugin) p));
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " Successfully linked with RealShop Economy!");
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " RealShop Economy NOT found!");
        }
    }

    private void loadVaultEconomy() {
        Plugin p = getServer().getPluginManager().getPlugin("Vault");
        if (p != null) {
            ResidenceVaultAdapter vault = new ResidenceVaultAdapter(getServer());
            if (vault.economyOK()) {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " Found Vault using economy: " + vault.getEconomyName());
                this.economy = vault;
            } else {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + " Found Vault, but Vault reported no usable economy system...");
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + " Vault NOT found!");
        }
    }

    public boolean isResAdminOn(CommandSender sender) {
        if (sender instanceof Player) {
            return isResAdminOn((Player) sender);
        }
        return true;
    }

    public boolean isResAdminOn(Player player) {
        if (this.resadminToggle.contains(player.getName())) {
            return true;
        }
        return false;
    }

    public void turnResAdminOn(Player player) {
        this.resadminToggle.add(player.getName());
    }

    public boolean isResAdminOn(String player) {
        if (this.resadminToggle.contains(player)) {
            return true;
        }
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected boolean loadYml() throws Exception {
        File saveFolder = new File(this.dataFolder, "Save");
        try {
            File worldFolder = new File(saveFolder, "Worlds");
            if (!saveFolder.isDirectory()) {
                this.getLogger().warning("Save directory does not exist...");
                this.getLogger().warning("Please restart server");
                return true;
            }
            long time;
            YMLSaveHelper yml;
            File loadFile;
            HashMap<String, Object> worlds = new HashMap<>();
            for (World world : getServ().getWorlds()) {
                loadFile = new File(worldFolder, "res_" + world.getName() + ".yml");
                if (loadFile.isFile()) {
                    time = System.currentTimeMillis();
                    Bukkit.getConsoleSender().sendMessage(getPrefix() + " Loading save data for world " + world.getName() + "...");

                    yml = new YMLSaveHelper(loadFile);
                    yml.load();

                    if (yml.getRoot().containsKey("Messages")) {
                        HashMap<Integer, MinimizeMessages> c = getResidenceManager().getCacheMessages().get(world.getName());
                        if (c == null) {
                            c = new HashMap<Integer, MinimizeMessages>();
                        }
                        Map<Integer, Object> ms = (Map<Integer, Object>) yml.getRoot().get("Messages");
                        if (ms != null) {
                            for (Entry<Integer, Object> one : ms.entrySet()) {
                                try {
                                    Map<String, String> msgs = (Map<String, String>) one.getValue();
                                    c.put(one.getKey(), new MinimizeMessages(one.getKey(), msgs.get("EnterMessage"), msgs.get("LeaveMessage")));
                                } catch (Exception e) {

                                }
                            }
                            getResidenceManager().getCacheMessages().put(world.getName(), c);
                        }
                    }

                    if (yml.getRoot().containsKey("Flags")) {
                        HashMap<Integer, MinimizeFlags> c = getResidenceManager().getCacheFlags().get(world.getName());
                        if (c == null) {
                            c = new HashMap<Integer, MinimizeFlags>();
                        }
                        Map<Integer, Object> ms = (Map<Integer, Object>) yml.getRoot().get("Flags");
                        if (ms != null) {
                            for (Entry<Integer, Object> one : ms.entrySet()) {
                                try {
                                    HashMap<String, Boolean> msgs = (HashMap<String, Boolean>) one.getValue();
                                    c.put(one.getKey(), new MinimizeFlags(one.getKey(), msgs));
                                } catch (Exception e) {

                                }
                            }
                            getResidenceManager().getCacheFlags().put(world.getName(), c);
                        }
                    }

                    worlds.put(world.getName(), yml.getRoot().get("Residences"));

                    int pass = (int) (System.currentTimeMillis() - time);
                    String PastTime = pass > 1000 ? String.format("%.2f", (pass / 1000F)) + " sec" : pass + " ms";

                    Bukkit.getConsoleSender().sendMessage(getPrefix() + " Loaded " + world.getName() + " data. (" + PastTime + ")");
                }
            }

            getResidenceManager().load(worlds);

            // Getting shop residences
            Map<String, ClaimedResidence> resList = this.rmanager.getResidences();
            for (Entry<String, ClaimedResidence> one : resList.entrySet()) {
                addShops(one.getValue());
            }

            if (getConfigManager().isUUIDConvertion()) {
                getConfigManager().ChangeConfig("Global.UUIDConvertion", false);
            }

            loadFile = new File(saveFolder, "forsale.yml");
            if (loadFile.isFile()) {
                yml = new YMLSaveHelper(loadFile);
                yml.load();
                this.tmanager = new TransactionManager(this);
                this.tmanager.load((Map) yml.getRoot().get("Economy"));
            }
            loadFile = new File(saveFolder, "leases.yml");
            if (loadFile.isFile()) {
                yml = new YMLSaveHelper(loadFile);
                yml.load();
                this.leasemanager = getLeaseManager().load((Map) yml.getRoot().get("Leases"));
            }
            loadFile = new File(saveFolder, "permlists.yml");
            if (loadFile.isFile()) {
                yml = new YMLSaveHelper(loadFile);
                yml.load();
                this.pmanager = getPermissionListManager().load((Map) yml.getRoot().get("PermissionLists"));
            }
            loadFile = new File(saveFolder, "rent.yml");
            if (loadFile.isFile()) {
                yml = new YMLSaveHelper(loadFile);
                yml.load();
//		rentmanager = new RentManager();
                this.rentmanager.load((Map) yml.getRoot().get("RentSystem"));
            }

//	    for (Player one : Bukkit.getOnlinePlayers()) {
//		ResidencePlayer rplayer = getPlayerManager().getResidencePlayer(one);
//		if (rplayer != null)
//		    rplayer.recountRes();
//	    }

            // System.out.print("[Residence] Loaded...");
            return true;
        } catch (Exception ex) {
            Logger.getLogger(Residence.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    private void addShops(ClaimedResidence res) {
        ResidencePermissions perms = res.getPermissions();
        if (perms.has(Flags.shop, FlagCombo.OnlyTrue, false)) {
            this.rmanager.addShop(res);
        }
        for (ClaimedResidence one : res.getSubzones()) {
            addShops(one);
        }
    }

    private void writeDefaultConfigFromJar() {
        if (this.writeDefaultFileFromJar(new File(this.getDataFolder(), "config.yml"), "config.yml", true)) {
            System.out.println("[Residence] Wrote default config...");
        }
    }

    private void writeDefaultGroupsFromJar() {
        if (this.writeDefaultFileFromJar(new File(this.getDataFolder(), "groups.yml"), "groups.yml", true)) {
            System.out.println("[Residence] Wrote default groups...");
        }
    }

    private void writeDefaultFlagsFromJar() {
        if (this.writeDefaultFileFromJar(new File(this.getDataFolder(), "flags.yml"), "flags.yml", true)) {
            System.out.println("[Residence] Wrote default flags...");
        }
    }

    private void ConvertFile() {
        File file = new File(this.getDataFolder(), "config.yml");

        File file_old = new File(this.getDataFolder(), "config_old.yml");

        File newfile = new File(this.getDataFolder(), "groups.yml");

        File newTempFlags = new File(this.getDataFolder(), "flags.yml");

        try {
            copy(file, file_old);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            copy(file, newfile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            copy(file, newTempFlags);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        File newGroups = new File(this.getDataFolder(), "config.yml");

        List<String> list = new ArrayList<String>();
        list.add("ResidenceVersion");
        list.add("Global.Flags");
        list.add("Global.FlagPermission");
        list.add("Global.ResidenceDefault");
        list.add("Global.CreatorDefault");
        list.add("Global.GroupDefault");
        list.add("Groups");
        list.add("GroupAssignments");
        list.add("ItemList");

        try {
            remove(newGroups, list);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File newConfig = new File(this.getDataFolder(), "groups.yml");
        list.clear();
        list = new ArrayList<String>();
        list.add("ResidenceVersion");
        list.add("Global");
        list.add("ItemList");

        try {
            remove(newConfig, list);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File newFlags = new File(this.getDataFolder(), "flags.yml");
        list.clear();
        list = new ArrayList<String>();
        list.add("ResidenceVersion");
        list.add("GroupAssignments");
        list.add("Groups");
        list.add("Global.Language");
        list.add("Global.SelectionToolId");
        list.add("Global.InfoToolId");
        list.add("Global.MoveCheckInterval");
        list.add("Global.SaveInterval");
        list.add("Global.DefaultGroup");
        list.add("Global.UseLeaseSystem");
        list.add("Global.LeaseCheckInterval");
        list.add("Global.LeaseAutoRenew");
        list.add("Global.EnablePermissions");
        list.add("Global.LegacyPermissions");
        list.add("Global.EnableEconomy");
        list.add("Global.EnableRentSystem");
        list.add("Global.RentCheckInterval");
        list.add("Global.ResidenceChatEnable");
        list.add("Global.UseActionBar");
        list.add("Global.ResidenceChatColor");
        list.add("Global.AdminOnlyCommands");
        list.add("Global.AdminOPs");
        list.add("Global.MultiWorldPlugin");
        list.add("Global.ResidenceFlagsInherit");
        list.add("Global.PreventRentModify");
        list.add("Global.StopOnSaveFault");
        list.add("Global.ResidenceNameRegex");
        list.add("Global.ShowIntervalMessages");
        list.add("Global.VersionCheck");
        list.add("Global.CustomContainers");
        list.add("Global.CustomBothClick");
        list.add("Global.CustomRightClick");

        try {
            remove(newFlags, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeDefaultFileFromJar(File writeName, String jarPath, boolean backupOld) {
        try {
            File fileBackup = new File(this.getDataFolder(), "backup-" + writeName);
            File jarloc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
            if (jarloc.isFile()) {
                JarFile jar = new JarFile(jarloc);
                JarEntry entry = jar.getJarEntry(jarPath);
                if (entry != null && !entry.isDirectory()) {
                    InputStream in = jar.getInputStream(entry);
                    InputStreamReader isr = new InputStreamReader(in, "UTF8");
                    if (writeName.isFile()) {
                        if (backupOld) {
                            if (fileBackup.isFile()) {
                                fileBackup.delete();
                            }
                            writeName.renameTo(fileBackup);
                        } else {
                            writeName.delete();
                        }
                    }
                    FileOutputStream out = new FileOutputStream(writeName);
                    OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
                    char[] tempbytes = new char[512];
                    int readbytes = isr.read(tempbytes, 0, 512);
                    while (readbytes > -1) {
                        osw.write(tempbytes, 0, readbytes);
                        readbytes = isr.read(tempbytes, 0, 512);
                    }
                    osw.close();
                    isr.close();
                    return true;
                }
                jar.close();
            }
            return false;
        } catch (Exception ex) {
            System.out.println("[Residence] Failed to write file: " + writeName);
            return false;
        }
    }

    public boolean isPlayerExist(CommandSender sender, String name, boolean inform) {
        if (getPlayerUUID(name) != null) {
            return true;
        }
        if (inform) {
            sender.sendMessage(msg(lm.Invalid_Player));
        }
        @SuppressWarnings("unused")
        String a = "%%__USER__%%";
        @SuppressWarnings("unused")
        String b = "%%__RESOURCE__%%";
        @SuppressWarnings("unused")
        String c = "%%__NONCE__%%";
        return false;

    }

//    private void writeDefaultLanguageFile(String lang) {
//	File outFile = new File(new File(this.getDataFolder(), "Language"), lang + ".yml");
//	outFile.getParentFile().mkdirs();
//	if (this.writeDefaultFileFromJar(outFile, "languagefiles/" + lang + ".yml", true)) {
//	    System.out.println("[Residence] Wrote default " + lang + " Language file...");
//	}
//    }
//
//    private boolean checkNewLanguageVersion(String lang) throws IOException, FileNotFoundException, InvalidConfigurationException {
//	File outFile = new File(new File(this.getDataFolder(), "Language"), lang + ".yml");
//	File checkFile = new File(new File(this.getDataFolder(), "Language"), "temp-" + lang + ".yml");
//	if (outFile.isFile()) {
//	    FileConfiguration testconfig = new YamlConfiguration();
//	    testconfig.load(outFile);
//	    int oldversion = testconfig.getInt("FieldsVersion", 0);
//	    if (!this.writeDefaultFileFromJar(checkFile, "languagefiles/" + lang + ".yml", false)) {
//		return false;
//	    }
//	    FileConfiguration testconfig2 = new YamlConfiguration();
//	    testconfig2.load(checkFile);
//	    int newversion = testconfig2.getInt("FieldsVersion", oldversion);
//	    if (checkFile.isFile()) {
//		checkFile.delete();
//	    }
//	    if (newversion > oldversion) {
//		return true;
//	    }
//	    return false;
//	}
//	return true;
//    }

    public UUID getPlayerUUID(String playername) {
//	if (Residence.getConfigManager().isOfflineMode())
//	    return null;
        Player p = getServ().getPlayer(playername);
        if (p == null) {
            OfflinePlayer po = this.OfflinePlayerList.get(playername.toLowerCase());
            if (po != null) {
                return po.getUniqueId();
            }
        } else {
            return p.getUniqueId();
        }
        return null;
    }

    public String msg(lm lm, Object... variables) {
        return getLM().getMessage(lm, variables);
    }

    public Server getServ() {
        return this.server;
    }

    public Language getLM() {
        return this.NewLanguageManager;
    }

    public User getOfflinePlayer(String Name) {
        if (Name == null) {
            return null;
        }
        OfflinePlayer offPlayer = this.OfflinePlayerList.get(Name.toLowerCase());
        if (offPlayer != null) {
            return offPlayer;
        }

        Player player = Bukkit.getPlayer(Name);
        if (player != null) {
            return player;
        }

//	offPlayer = Bukkit.getOfflinePlayer(Name);
//	if (offPlayer != null)
//	    addOfflinePlayerToChache(offPlayer);
        return offPlayer;
    }

    public String getPlayerUUIDString(String playername) {
        UUID playerUUID = getPlayerUUID(playername);
        if (playerUUID != null) {
            return playerUUID.toString();
        }
        return null;
    }

    public User getOfflinePlayer(UUID uuid) {
        User offPlayer = this.cachedPlayerNameUUIDs.get(uuid);
        if (offPlayer != null) {
            return offPlayer;
        }

        User player = Sponge.getServiceManager().provide(UserStorageService.class).get().get(uuid).orElse(null);
        if (player != null) {
            return player;
        }

//	offPlayer = Bukkit.getOfflinePlayer(uuid);
//	if (offPlayer != null)
//	    addOfflinePlayerToChache(offPlayer);
        return offPlayer;
    }

    public void addOfflinePlayerToChache(User player) {
        if (player == null) {
            return;
        }
        if (player.getName() != null) {
            this.OfflinePlayerList.put(player.getName().toLowerCase(), player);
        }
        if (player.getUniqueId() != null) {
            this.cachedPlayerNameUUIDs.put(player.getUniqueId(), player);
        }
    }

    public String getPlayerName(String uuid) {
        try {
            return getPlayerName(UUID.fromString(uuid));
        } catch (IllegalArgumentException ex) {
        }
        return null;
    }

    public String getPlayerName(UUID uuid) {
        OfflinePlayer p = getServ().getPlayer(uuid);
        if (p == null) {
            p = getServ().getOfflinePlayer(uuid);
        }
        if (p != null) {
            return p.getName();
        }
        return null;
    }

    public String getServerLandname() {
        return this.ServerLandname;
    }

    public String getServerLandUUID() {
        return this.ServerLandUUID;
    }

    public String getTempUserUUID() {
        return this.TempUserUUID;
    }

    public boolean isDisabledWorldListener(World world) {
        return isDisabledWorldListener(world.getName());
    }

    public boolean isDisabledWorldListener(UUID worldId) {
        this.isDisabledWorldListener(Sponge.getServer().getWorld(worldId).orElse(null));
    }

    public boolean isDisabledWorldListener(String worldname) {
        if (getConfigManager().DisabledWorldsList.contains(worldname) && getConfigManager().DisableListeners) {
            return true;
        }
        return false;
    }

    public ConfigManager getConfigManager() {
        return this.cmanager;
    }

//    public static void msg(Player player, String path, Object... variables) {
//	if (player != null)
//	    if (Residence.getLM().containsKey(path))
//		player.sendMessage(Residence.getLM().getMessage(path, variables));
//	    else
//		player.sendMessage(path);
//    }

    public void setConfigManager(ConfigManager cm) {
        this.cmanager = cm;
    }

    public boolean isDisabledWorldCommand(World world) {
        return isDisabledWorldCommand(world.getName());
    }

    public boolean isDisabledWorldCommand(String worldname) {
        if (getConfigManager().DisabledWorldsList.contains(worldname) && getConfigManager().DisableCommands) {
            return true;
        }
        return false;
    }

//    private boolean isWorldOk(CommandSender sender) {
//	if (!this.getConfigManager().DisableNoFlagMessageUse)
//	    return true;
//
//	if (sender.hasPermission("residence.checkbadflags"))
//	    return true;
//	
//	if (!(sender instanceof Player))
//	    return true;
//
//	Player player = (Player) sender;
//	String world = player.getWorld().getName();
//	
//	for (String one : this.getConfigManager().DisableNoFlagMessageWorlds) {
//	    if (one.equalsIgnoreCase(world))
//		return false;
//	}
//	return true;
//    }

    public String msg(String path) {
        return getLM().getMessage(path);
    }

    public void msg(CommandSender sender, String text) {
        if (sender != null && text.length() > 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
        }
    }

    public void msg(Player player, String text) {
        if (player != null && text.length() > 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
        }
    }

    public void msg(CommandSender sender, lm lm, Object... variables) {
//	if (!isWorldOk(sender))
//	    return;

        if (sender == null) {
            return;
        }

        if (getLM().containsKey(lm.getPath())) {
            String msg = getLM().getMessage(lm, variables);
            if (msg.length() > 0) {
                sender.sendMessage(msg);
            }
        } else {
            String msg = lm.getPath();
            if (msg.length() > 0) {
                sender.sendMessage(lm.getPath());
            }
        }
    }

    public List<String> msgL(lm lm) {
        return getLM().getMessageList(lm);
    }

    public InformationPager getInfoPageManager() {
        return this.InformationPagerManager;
    }

    public WorldEditPlugin getWorldEdit() {
        return this.wep;
    }

    public WorldGuardPlugin getWorldGuard() {
        return this.wg;
    }

    public int getWepid() {
        return this.wepid;
    }

    public WorldGuardUtil getWorldGuardUtil() {
        if (this.worldGuardUtil == null) {
            this.worldGuardUtil = new WorldGuardUtil(this);
        }
        return this.worldGuardUtil;
    }

    public KingdomsUtil getKingdomsUtil() {
        if (this.kingdomsUtil == null) {
            this.kingdomsUtil = new KingdomsUtil(this);
        }
        return this.kingdomsUtil;
    }

    public boolean hasPermission(CommandSender sender, String permision, boolean output) {
        return hasPermission(sender, permision, output, null);
    }

    public boolean hasPermission(CommandSender sender, String permision, Boolean output, String message) {
        if (sender == null) {
            return false;
        }
        if (sender instanceof ConsoleCommandSender) {
            return true;
        } else if (sender instanceof Player) {
            if (sender.hasPermission(permision)) {
                return true;
            }
            if (output) {
                String outMsg = getLM().getMessage(lm.General_NoPermission);
                if (message != null) {
                    outMsg = message;
                }

                RawMessage rm = new RawMessage();
                rm.add(outMsg, "�2" + permision);
                rm.show(sender);
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                console.sendMessage(ChatColor.RED + sender.getName() + " No permission -> " + permision);
            }
        }
        return false;
    }

    public boolean hasPermission(CommandSender sender, String permision) {
        return hasPermission(sender, permision, true, null);
    }

    public boolean hasPermission(CommandSender sender, String permision, String message) {
        return hasPermission(sender, permision, true, message);
    }

    public boolean hasPermission(CommandSender sender, String permision, lm message) {
        return hasPermission(sender, permision, true, getLM().getMessage(message));
    }

    public String[] reduceArgs(String[] args) {
        if (args.length <= 1) {
            return new String[0];
        }
        return Arrays.copyOfRange(args, 1, args.length);
    }
//    public TownManager getTownManager() {
//	return townManager;
//    }
}
