package fr.traqueur.ressourcefulbees;

import fr.traqueur.ressourcefulbees.api.RessourcefulBeesLike;
import fr.traqueur.ressourcefulbees.api.Saveable;
import fr.traqueur.ressourcefulbees.api.managers.IBeesManager;
import fr.traqueur.ressourcefulbees.api.utils.BeeLogger;
import fr.traqueur.ressourcefulbees.commands.CommandManager;
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

        this.registerManager(new BeesManager(this), IBeesManager.class);

        this.getManager(IBeesManager.class).test();

        this.saveables.forEach(saveable -> {
            this.saveOrUpdateConfiguration(saveable.getFile(), saveable.getFile());
            saveable.loadData();
        });

        BeeLogger.info("RessourcefulBees Plugin enabled successfully !");
    }

    public <I, T extends I> void registerAndPublishManager(T instance, Class<I> clazz) {
        this.registerManager(instance, clazz);
        this.getServer().getServicesManager().register(clazz, instance, this, ServicePriority.Normal);
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
