package fr.traqueur.resourcefulbees.commands;

import fr.traqueur.commands.api.messages.MessageHandler;
import fr.traqueur.resourcefulbees.LangKeys;
import fr.traqueur.resourcefulbees.api.ResourcefulBeesLikeAPI;

public class ResourcefulBeesHandler implements MessageHandler {

    private final ResourcefulBeesLikeAPI api;

    public ResourcefulBeesHandler(ResourcefulBeesLikeAPI api) {
        this.api = api;
    }

    @Override
    public String getNoPermissionMessage() {
        return api.translate(LangKeys.NO_PERMISSION);
    }

    @Override
    public String getOnlyInGameMessage() {
        return api.translate(LangKeys.ONLY_PLAYER);
    }

    @Override
    public String getMissingArgsMessage() {
        return api.translate(LangKeys.MISSING_ARGS);
    }

    @Override
    public String getArgNotRecognized() {
        return api.translate(LangKeys.ARG_NOT_RECOGNIZED);
    }

    @Override
    public String getRequirementMessage() { return api.translate(LangKeys.REQUIREMENT_MESSAGE); }
}
