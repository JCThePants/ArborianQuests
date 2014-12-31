package com.jcwhatever.bukkit.arborianquests.scripting;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.quests.Quest;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.api.IScriptApiObject;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

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
         * @param player            The player to check
         * @param primaryQuestName  The name of the primary quest the flag is for.
         * @param subQuestName      The name of the sub quest.
         * @param flagName          The name of the flag
         *
         * @return  True if the flag is set.
         */
        public boolean has(Object player,
                           String primaryQuestName, @Nullable String subQuestName, String flagName) {
            PreCon.notNull(player, "player");
            PreCon.notNullOrEmpty(primaryQuestName, "primaryQuestName");
            PreCon.notNullOrEmpty(flagName, "flagName");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            return getQuest(primaryQuestName, subQuestName).hasFlag(p.getUniqueId(), flagName);
        }

        /**
         * Set a flag on a player.
         *
         * @param player            The player.
         * @param primaryQuestName  The name of the quest the flag is for.
         * @param subQuestName      The name of the sub quest.
         * @param flagName          The name of the flag.
         */
        public void set(Object player,
                           String primaryQuestName, @Nullable String subQuestName, String flagName) {
            PreCon.notNull(player, "player");
            PreCon.notNullOrEmpty(primaryQuestName, "primaryQuestName");
            PreCon.notNullOrEmpty(flagName, "flagName");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            getQuest(primaryQuestName, subQuestName).setFlag(p.getUniqueId(), flagName);
        }

        /**
         * Clear a flag on a player.
         *
         * @param player     The player.
         * @param primaryQuestName  The name of the quest the flag is for.
         * @param flagName   The name of the flag.
         */
        public boolean clear(Object player, String primaryQuestName, @Nullable String subQuestName, String flagName) {
            PreCon.notNull(player, "player");
            PreCon.notNullOrEmpty(primaryQuestName, "questName");
            PreCon.notNullOrEmpty(flagName, "flagName");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            getQuest(primaryQuestName, subQuestName).clearFlag(p.getUniqueId(), flagName);
            return true;
        }


        private Quest getQuest(String primaryQuestName, @Nullable String subQuestName) {
            Quest quest = ArborianQuests.getQuestManager().getPrimary(primaryQuestName);
            PreCon.isValid(quest != null, "Quest named '{0}' not found.", primaryQuestName);

            if (subQuestName != null) {
                quest = quest.getQuest(subQuestName);
                PreCon.isValid(quest != null, "Subquest named '{0}' not found in quest '{1}'.",
                        subQuestName, primaryQuestName);
            }

            return quest;
        }
    }
}
