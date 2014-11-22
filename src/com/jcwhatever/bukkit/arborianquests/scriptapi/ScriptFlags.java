package com.jcwhatever.bukkit.arborianquests.scriptapi;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.quests.Quest;
import com.jcwhatever.bukkit.arborianquests.quests.QuestManager;
import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.ScriptApiInfo;
import com.jcwhatever.bukkit.generic.scripting.api.GenericsScriptApi;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@ScriptApiInfo(
        variableName = "questFlags",
        description = "Provide script access to ArborianQuests flags.")
public class ScriptFlags extends GenericsScriptApi {

    private ApiObject _api;

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin The owning plugin
     */
    public ScriptFlags(Plugin plugin) {

        // Arborian Quests is always the owning plugin
        super(ArborianQuests.getPlugin());
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        if (_api == null)
            _api = new ApiObject();

        return _api;
    }

    public void reset() {
        if (_api != null)
            _api.reset();
    }

    public static class ApiObject implements IScriptApiObject {

        ApiObject() {

        }

        @Override
        public void reset() {
            // do nothing
        }

        /**
         * Determine if a player has a flag set.
         *
         * @param player     The player to check
         * @param questName  The name of the quest the flag is for.
         * @param flagName   The name of the flag
         *
         * @return  True if the flag is set.
         */
        public boolean has(Object player, String questName, String flagName) {
            PreCon.notNull(player);
            PreCon.notNullOrEmpty(questName);
            PreCon.notNullOrEmpty(flagName);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            Quest quest = QuestManager.get(questName);
            return quest != null && quest.hasFlag(p.getUniqueId(), flagName);
        }

        /**
         * Set a flag on a player.
         *
         * @param player     The player.
         * @param questName  The name of the quest the flag is for.
         * @param flagName   The name of the flag.
         */
        public boolean set(Object player, String questName, String flagName) {
            PreCon.notNull(player);
            PreCon.notNullOrEmpty(questName);
            PreCon.notNullOrEmpty(flagName);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            Quest quest = QuestManager.get(questName);
            if (quest == null)
                return false;

            quest.setFlag(p.getUniqueId(), flagName);
            return true;
        }

        /**
         * Clear a flag on a player.
         *
         * @param player     The player.
         * @param questName  The name of the quest the flag is for.
         * @param flagName   The name of the flag.
         */
        public boolean clear(Object player, String questName, String flagName) {
            PreCon.notNull(player);
            PreCon.notNullOrEmpty(questName);
            PreCon.notNullOrEmpty(flagName);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            Quest quest = QuestManager.get(questName);
            if (quest == null)
                return false;

            quest.clearFlag(p.getUniqueId(), flagName);
            return true;
        }
    }
}
