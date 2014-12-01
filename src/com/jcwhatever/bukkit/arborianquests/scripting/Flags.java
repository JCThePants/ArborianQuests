package com.jcwhatever.bukkit.arborianquests.scripting;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.quests.Quest;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApiObject;
import com.jcwhatever.bukkit.generic.utils.PlayerUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.entity.Player;

public class Flags {

    private ApiObject _api;

    public IScriptApiObject getApiObject(@SuppressWarnings("unused") IEvaluatedScript script) {
        if (_api == null)
            _api = new ApiObject();

        return _api;
    }

    public void reset() {
        if (_api != null)
            _api.dispose();
    }

    public static class ApiObject implements IScriptApiObject {

        ApiObject() {

        }

        @Override
        public boolean isDisposed() {
            return false;
        }

        @Override
        public void dispose() {
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

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p);

            Quest quest = ArborianQuests.getQuestManager().get(questName);
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

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p);

            Quest quest = ArborianQuests.getQuestManager().get(questName);
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

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p);

            Quest quest = ArborianQuests.getQuestManager().get(questName);
            if (quest == null)
                return false;

            quest.clearFlag(p.getUniqueId(), flagName);
            return true;
        }
    }
}
