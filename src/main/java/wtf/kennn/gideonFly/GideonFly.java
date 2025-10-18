package wtf.kennn.gideonFly;

import co.aikar.commands.PaperCommandManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.kennn.gideonFly.Apis.LuckPermsHook;
import wtf.kennn.gideonFly.Commands.FlyCommand;
import wtf.kennn.gideonFly.Commands.JoinEffectCommand;
import wtf.kennn.gideonFly.Commands.SetupCommand;
import wtf.kennn.gideonFly.Listeners.JoinListener;
import wtf.kennn.gideonFly.Listeners.LeaveListener;
import wtf.kennn.gideonFly.Listeners.PrefixChatListener;
import wtf.kennn.gideonFly.Listeners.VolcanoCleanupListener;
import wtf.kennn.gideonFly.Managers.ConfigManager;
import wtf.kennn.gideonFly.Managers.PlayerDataManager;

import static wtf.kennn.gideonFly.Utils.ChatUtil.log;

public final class GideonFly extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private static GideonFly instance;
    private ConfigManager configManager;
    private PaperCommandManager commandManager;
    private LuckPerms luckPermsAPI;
    @Override
    public void onEnable() {
        instance = this;

        LuckPermsHook.init(this);
        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager(this);

        try {
            commandManager = new PaperCommandManager(this);
        } catch (Exception e) {
            getLogger().severe("❌ Failed to initialize Aikar Command Framework: " + e.getMessage());
            e.printStackTrace();
            return;
        }


        commandManager.registerCommand(new FlyCommand());
        commandManager.registerCommand(new SetupCommand());
        commandManager.registerCommand(new JoinEffectCommand());


        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PrefixChatListener(), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new VolcanoCleanupListener(this), this);




        // ✅ Logs bonitos
        log("");
        log("   &fGideonFly &e| &bCreated by Gideon Studio");
        log("&bVersion: &f" + getDescription().getVersion());
        log("&bAuthor: &f" + getDescription().getAuthors());
        log("&bLuckPerms: " + (luckPermsAPI != null ? "&aDetected ✓" : "&cNot Found ✗"));
        log("&bStatus: &aEnabled");
        log("&bRegistered: &fCommands, Configs, Listeners, etc.");
        log("");
    }

    @Override
    public void onDisable() {
        log("");
        log("   &fGideonFly &e| &bCreated by Gideon Studio");
        log("&bVersion: &f" + getDescription().getVersion());
        log("&bAuthor: &f" + getDescription().getAuthors());
        log("&bLuckPerms: " + (luckPermsAPI != null ? "&aDetected ✓" : "&cNot Found ✗"));
        log("&bStatus: &cDisabled");
        log("&bUnregistered: &fCommands, Configs, Listeners, etc.");
        log("");
    }

    // === GETTERS ===
    public static GideonFly getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public LuckPerms getLuckPermsAPI() {
        return luckPermsAPI;
    }
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
