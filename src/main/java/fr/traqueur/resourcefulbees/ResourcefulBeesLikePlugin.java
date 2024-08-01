package fr.traqueur.resourcefulbees;

import fr.traqueur.commands.api.CommandManager;
import fr.traqueur.resourcefulbees.api.ResourcefulBeesLike;
import fr.traqueur.resourcefulbees.api.lang.Formatter;
import fr.traqueur.resourcefulbees.api.lang.LangKey;
import fr.traqueur.resourcefulbees.api.managers.*;
import fr.traqueur.resourcefulbees.api.models.BeeTools;
import fr.traqueur.resourcefulbees.api.models.BeeType;
import fr.traqueur.resourcefulbees.api.models.BeehiveUpgrade;
import fr.traqueur.resourcefulbees.api.nms.NmsVersion;
import fr.traqueur.resourcefulbees.api.utils.BeeLogger;
import fr.traqueur.resourcefulbees.api.constants.ConfigKeys;
import fr.traqueur.resourcefulbees.api.utils.MessageUtils;
import fr.traqueur.resourcefulbees.commands.BeeGiveCommand;
import fr.traqueur.resourcefulbees.commands.ResourcefulBeesHandler;
import fr.traqueur.resourcefulbees.commands.arguments.BeeTypeArgument;
import fr.traqueur.resourcefulbees.commands.arguments.ToolsArgument;
import fr.traqueur.resourcefulbees.commands.arguments.UpgradeArgument;
import fr.traqueur.resourcefulbees.managers.*;
import fr.traqueur.resourcefulbees.platform.spigot.SpigotUtils;
import fr.traqueur.resourcefulbees.platform.paper.PaperUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;


public final class ResourcefulBeesLikePlugin extends ResourcefulBeesLike {

    private MessageUtils messageUtils;
    private CommandManager commandManager;
    private List<Saveable> saveables;

    private Set<LangKey> langKeys;
    private HashMap<String, YamlConfiguration> languages;
    private String lang;
    @Override
    public void onLoad() {
        this.commandManager = new CommandManager(this);
        this.commandManager.setMessageHandler(new ResourcefulBeesHandler(this));
        this.saveables = new ArrayList<>();
        this.languages = new HashMap<>();
        this.langKeys = new HashSet<>();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new Metrics(this, 22825);

        this.messageUtils = this.isPaperVersion() ? new PaperUtils() : new SpigotUtils();

        for (LangKeys value : LangKeys.values()) {
            this.registerLanguageKey(value);
        }

        this.registerManager(new ResourcefulBeeTypeManager(this), BeeTypeManager.class);
        this.commandManager.registerConverter(BeeType.class, "beetype", new BeeTypeArgument(this.getManager(BeeTypeManager.class)));

        this.registerManager(new ResourcefulBeesManager(this), BeesManager.class);
        this.registerManager(new ResourcefulToolsManager(this), ToolsManager.class);
        this.commandManager.registerConverter(BeeTools.class, "beetools", new ToolsArgument());

        this.registerManager(new ResourcefulBreedsManager(this), BreedsManager.class);
        this.registerManager(new ResourcefulMutationsManager(this), MutationsManager.class);
        this.registerManager(new ResourcefulUpgradesManager(this), UpgradesManager.class);
        this.commandManager.registerConverter(BeehiveUpgrade.class, "upgrade", new UpgradeArgument(this.getManager(UpgradesManager.class)));

        this.registerManager(new ResourcefulBeehivesManager(this), BeehivesManager.class);

        this.saveables.forEach(saveable -> {
            this.saveOrUpdateConfiguration(saveable.getFile(), saveable.getFile());
            BeeLogger.info("&eLoaded " + saveable.getClass().getSimpleName() + " config file: " + saveable.getFile() + ".");
            saveable.loadData();
        });

        commandManager.registerCommand(new BeeGiveCommand(this));

        this.getScheduler().runNextTick((task) -> {
            this.saveOrUpdateConfiguration("languages" + File.separator + "languages.yml", "languages" + File.separator + "languages.yml");
            YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "languages" + File.separator + "languages.yml"));
            langConfig.getMapList(ConfigKeys.LANGUAGE).forEach(map -> {
                String key = (String) map.keySet().iterator().next();
                String path = (String) map.get(key);
                try {
                    this.registerLanguage(key, path);
                } catch (NoSuchElementException e) {
                    BeeLogger.severe("&c" + e.getMessage());
                }

            });
            BeeLogger.info("&aLoaded languages files. (" + this.languages.size() + " languages)");
            if(this.languages.isEmpty()) {
                getServer().getPluginManager().disablePlugin(this);
                throw new NoSuchElementException("No languages loaded.");
            }

            this.lang = langConfig.getString(ConfigKeys.USED_LANG);
            if(!this.languages.containsKey(this.lang)) {
                getServer().getPluginManager().disablePlugin(this);
                throw new NoSuchElementException("The language file " + this.lang + " does not exist.");
            }

           this.getManager(BeeTypeManager.class).setupRecipes();

        });

        BeeLogger.info("RessourcefulBees Plugin enabled successfully !");
    }

    @Override
    public void onDisable() {
        this.saveables.forEach(Saveable::saveData);
        this.getServer().clearRecipes();
    }

    @Override
    public <T> T getManager(Class<T> clazz) {
        RegisteredServiceProvider<T> provider = getServer().getServicesManager().getRegistration(clazz);
        if (provider == null) {
            throw new NoSuchElementException("No provider found for " + clazz.getSimpleName() + " class.");
        }
        return provider.getProvider();
    }

    @Override
    public <I, T extends I> void registerManager(T instance, Class<I> clazz) {
        if(instance instanceof Saveable saveable) {
            this.saveables.add(saveable);
        }

        getServer().getServicesManager().register(clazz, instance, this, ServicePriority.Normal);
        BeeLogger.info("&eManager registered: " + clazz.getSimpleName());
    }

    @Override
    public void registerLanguage(String key, String path) {
        this.saveOrUpdateConfiguration("languages" + File.separator + path, "languages" + File.separator + path);
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "languages" + File.separator + path));
        for (LangKey value : this.langKeys) {
            if(!langConfig.contains(value.getKey())) {
                throw new NoSuchElementException("The language file " + path + " does not contain the key " + value.getKey() + ".");
            }
        }

        this.languages.put(key, langConfig);
    }

    @Override
    public void sendMessage(Player player, String message) {
        this.messageUtils.sendMessage(player, message);
    }

    @Override
    public void success(Player player, String s) {
        this.messageUtils.success(player, s);
    }

    @Override
    public void error(Player player, String s) {
        this.messageUtils.error(player, s);
    }

    @Override
    public String reset(String s) {
        return this.messageUtils.reset(s);
    }

    @Override
    public void registerLanguageKey(LangKey langKey) {
        if(this.langKeys.stream().anyMatch(msg -> msg.getKey().equals(langKey.getKey()))) {
            throw new IllegalArgumentException("The message " + langKey.getKey() + " is already registered.");
        }
        this.langKeys.add(langKey);
    }

    @Override
    public String translate(String key, Formatter... formatters) {
        if(!this.languages.get(this.lang).contains(key)) {
            throw new NoSuchElementException("The key " + key + " does not exist in the language file " + this.lang + ".");
        }
        String message = this.languages.get(this.lang).getString(key);
        for (Formatter formatter : formatters) {
            message = formatter.handle(this, message);
        }
        return message;
    }
}
