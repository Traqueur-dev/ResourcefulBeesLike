package fr.traqueur.ressourcefulbees;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLike;
import fr.traqueur.ressourcefulbees.api.Saveable;
import fr.traqueur.ressourcefulbees.api.managers.IBeeTypeManager;
import fr.traqueur.ressourcefulbees.api.managers.IBeesManager;
import fr.traqueur.ressourcefulbees.api.managers.IBreedsManager;
import fr.traqueur.ressourcefulbees.api.managers.IToolsManager;
import fr.traqueur.ressourcefulbees.api.models.IBeeType;
import fr.traqueur.ressourcefulbees.api.utils.BeeLogger;
import fr.traqueur.ressourcefulbees.commands.api.CommandManager;
import fr.traqueur.ressourcefulbees.commands.arguments.BeeTypeArgument;
import fr.traqueur.ressourcefulbees.managers.BeeTypeManager;
import fr.traqueur.ressourcefulbees.managers.BeesManager;
import fr.traqueur.ressourcefulbees.managers.BreedsManager;
import fr.traqueur.ressourcefulbees.managers.ToolsManager;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;


public final class RessourcefulBeesLikePlugin extends RessourcefulBeesLike {

    private CommandManager commandManager;
    private Set<Saveable> saveables;

    @Override
    public void onLoad() {
        this.commandManager = new CommandManager(this);
        this.saveables = new HashSet<>();
    }

    @Override
    public void onEnable() {

        ByteBuddyAgent.install();

        this.registerManager(new BeeTypeManager(this), IBeeTypeManager.class);
        this.commandManager.registerConverter(IBeeType.class, "beetype", new BeeTypeArgument(this.getManager(IBeeTypeManager.class)));

        this.registerManager(new BeesManager(this), IBeesManager.class);
        this.registerManager(new ToolsManager(this), IToolsManager.class);
        this.registerManager(new BreedsManager(this), IBreedsManager.class);

        this.saveables.forEach(saveable -> {
            this.saveOrUpdateConfiguration(saveable.getFile(), saveable.getFile());
            BeeLogger.info("Loaded " + saveable.getClass().getSimpleName() + " config file: " + saveable.getFile() + ".");
            saveable.loadData();
        });

        BeeLogger.info("RessourcefulBees Plugin enabled successfully !");
    }

    @Override
    public void onDisable() {
        this.saveables.forEach(Saveable::saveData);
    }

    public CommandManager getCommandManager() {
        return commandManager;
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
        BeeLogger.info("Manager registered: " + clazz.getSimpleName());
    }
}
