package fr.traqueur.resourcefulbees.models;

import fr.traqueur.resourcefulbees.api.models.BeehiveCraft;

import java.util.Map;

public record ResourcefulBeehiveCraft(String[] pattern, Map<String, String> ingredients) implements BeehiveCraft {
    @Override
    public String[] getPattern() {
        return pattern;
    }

    @Override
    public Map<String, String> getIngredients() {
        return ingredients;
    }
}
