package pl.anacode.costume;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.anacode.costume.commands.KostiumCommand;
import pl.anacode.costume.commands.ParrotCommand;
import pl.anacode.costume.listeners.*;
import pl.anacode.costume.managers.*;
import pl.anacode.costume.placeholders.CostumePlaceholders;
import pl.anacode.costume.tasks.EffectRefreshTask;

import java.io.File;

public final class CostumePlugin extends JavaPlugin {

    private static CostumePlugin instance;

    private DataManager dataManager;
    private CostumeManager costumeManager;
    private CooldownManager cooldownManager;
    private PetManager petManager;
    private ParrotManager parrotManager;
    private ParrotSpawnManager parrotSpawnManager;
    private DisguiseManager disguiseManager;
    private HeartManager heartManager;
    private GlowingManager glowingManager;
    private BelowNamePreviewManager belowNamePreviewManager;
    private NametagLineManager nametagLineManager;
    private PlaceholderManager placeholderManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResourceIfNotExists("messages.yml");
        saveResourceIfNotExists("kostiumConfiguration.yml");

        this.dataManager = new DataManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.disguiseManager = new DisguiseManager(this);
        this.heartManager = new HeartManager(this);
        this.glowingManager = new GlowingManager(this);
        this.belowNamePreviewManager = new BelowNamePreviewManager(this);
        this.nametagLineManager = new NametagLineManager(this);
        this.placeholderManager = new PlaceholderManager(this);

        this.costumeManager = new CostumeManager(this);
        this.petManager = new PetManager(this);
        this.parrotManager = new ParrotManager(this);
        this.parrotSpawnManager = new ParrotSpawnManager(this);

        this.dataManager.loadAll();

        registerListeners();
        registerCommands();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CostumePlaceholders(this).register();
        }

        new EffectRefreshTask(this).runTaskTimer(this, 20L, 20L);

        getLogger().info("CostumePlugin zostal pomyslnie wlaczony!");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);

        if (this.glowingManager != null) {
            this.glowingManager.cleanupAll();
        }

        if (this.parrotSpawnManager != null) {
            this.parrotSpawnManager.despawnAll();
        }

        if (this.heartManager != null) {
            this.heartManager.resetAll();
        }

        if (this.disguiseManager != null) {
            this.disguiseManager.removeAllDisguises();
        }

        if (this.dataManager != null) {
            this.dataManager.saveAll();
        }

        getLogger().info("CostumePlugin zostal pomyslnie wylaczony!");
    }

    private void registerListeners() {
        var pm = Bukkit.getPluginManager();
        pm.registerEvents(new ArmorPreviewListener(this), this);
        pm.registerEvents(new CostumeUseListener(this), this);
        pm.registerEvents(new DamageListener(this), this);
        pm.registerEvents(new DolphinGraceListener(this), this);
        pm.registerEvents(new DoubleJumpListener(this), this);
        pm.registerEvents(new DripstoneListener(this), this);
        pm.registerEvents(new ExtraPetListener(this), this);
        pm.registerEvents(new GUIClickListener(this), this);
        pm.registerEvents(new InfectionListener(this), this);
        pm.registerEvents(new MarkingListener(this), this);
        pm.registerEvents(new NegativeEffectListener(this), this);
        pm.registerEvents(new ParrotInteractionListener(this), this);
        pm.registerEvents(new ParrotProtectionListener(this), this);
        pm.registerEvents(new ParrotShoulderListener(this), this);
        pm.registerEvents(new ParrotUseListener(this), this);
        pm.registerEvents(new PetHungerListener(this), this);
        pm.registerEvents(new PlayerJoinQuitListener(this), this);
        pm.registerEvents(new ShiftListener(this), this);
        pm.registerEvents(new ShulkerOpenListener(this), this);
        pm.registerEvents(new SnowTrailListener(this), this);
        pm.registerEvents(new TotemListener(this), this);
    }

    private void registerCommands() {
        if (getCommand("kostium") != null) {
            KostiumCommand kostiumCommand = new KostiumCommand(this);
            getCommand("kostium").setExecutor(kostiumCommand);
            getCommand("kostium").setTabCompleter(kostiumCommand);
        }

        if (getCommand("papuga") != null) {
            ParrotCommand parrotCommand = new ParrotCommand(this);
            getCommand("papuga").setExecutor(parrotCommand);
            getCommand("papuga").setTabCompleter(parrotCommand);
        }
    }

    public void reloadPlugin() {
        reloadConfig();
        this.placeholderManager.reload();
        this.costumeManager.reload();
        this.dataManager.reload();
    }

    private void saveResourceIfNotExists(String resourceName) {
        File file = new File(getDataFolder(), resourceName);
        if (!file.exists()) {
            saveResource(resourceName, false);
        }
    }

    public static CostumePlugin getInstance() {
        return instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public CostumeManager getCostumeManager() {
        return costumeManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public PetManager getPetManager() {
        return petManager;
    }

    public ParrotManager getParrotManager() {
        return parrotManager;
    }

    public ParrotSpawnManager getParrotSpawnManager() {
        return parrotSpawnManager;
    }

    public DisguiseManager getDisguiseManager() {
        return disguiseManager;
    }

    public HeartManager getHeartManager() {
        return heartManager;
    }

    public GlowingManager getGlowingManager() {
        return glowingManager;
    }

    public BelowNamePreviewManager getBelowNamePreviewManager() {
        return belowNamePreviewManager;
    }

    public NametagLineManager getNametagLineManager() {
        return nametagLineManager;
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }
}
