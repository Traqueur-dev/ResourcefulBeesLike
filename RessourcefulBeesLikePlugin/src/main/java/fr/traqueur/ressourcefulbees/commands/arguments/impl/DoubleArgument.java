package fr.traqueur.ressourcefulbees.commands.arguments.impl;


import fr.traqueur.ressourcefulbees.commands.arguments.ArgumentConverter;

/**
 * Cette classe implémente l'interface ArgumentConverter pour convertir une chaîne de caractères en un objet Double.
 */
public class DoubleArgument implements ArgumentConverter<Double> {

    /**
     * Convertit une chaîne de caractères en un objet Double.
     * @param input La chaîne de caractères représentant le nombre à convertir.
     * @return L'objet Double correspondant à la chaîne de caractères spécifiée, ou null si la conversion échoue.
     */
    @Override
    public Double apply(String input) {
        try {
            return Double.valueOf(input);
        } catch (NumberFormatException e){
            return null;
        }
    }
}
