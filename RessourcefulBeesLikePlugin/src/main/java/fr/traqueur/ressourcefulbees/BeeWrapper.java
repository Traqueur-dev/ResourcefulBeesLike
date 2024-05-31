package fr.traqueur.ressourcefulbees;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class BeeWrapper {

    @RuntimeType
    public static Object intercept(@AllArguments Object[] args,
                                   @SuperCall Callable<?> zuper,
                                   @Origin Method method) {
        Object result = null;
        try {
            System.out.println("Avant l'exécution de " + method.getName());
            // Exécuter la méthode originale
            result = zuper.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
