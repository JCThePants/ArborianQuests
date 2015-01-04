package com.jcwhatever.bukkit.arborianquests.scripting;

import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.api.IScriptApiObject;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

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
         * @param questPath  The path name of the quest.
         * @param flagName   The name of the flag
         *
         * @return  True if the flag is set.
         */
        public boolean has(Object player, String questPath, String flagName) {
            PreCon.notNull(player, "player");
            PreCon.notNullOrEmpty(questPath, "questPath");
            PreCon.notNullOrEmpty(flagName, "flagName");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            return QuestsApi.getQuest(questPath, false).hasFlag(p.getUniqueId(), flagName);
        }

        /**
         * Set a flag on a player.
         *
         * @param player     The player.
         * @param questPath  The path name of the quest.
         * @param flagName   The name of the flag.
         */
        public void set(Object player, String questPath, String flagName) {
            PreCon.notNull(player, "player");
            PreCon.notNullOrEmpty(questPath, "questPath");
            PreCon.notNullOrEmpty(flagName, "flagName");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            QuestsApi.getQuest(questPath, false).setFlag(p.getUniqueId(), flagName);
        }

        /**
         * Clear a flag on a player.
         *
         * @param player     The player.
         * @param questPath  The path name of the quest.
         * @param flagName   The name of the flag.
         */
        public void clear(Object player, String questPath, String flagName) {
            PreCon.notNull(player, "player");
            PreCon.notNullOrEmpty(questPath, "questPath");
            PreCon.notNullOrEmpty(flagName, "flagName");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            QuestsApi.getQuest(questPath, false).clearFlag(p.getUniqueId(), flagName);
        }

    }
}
