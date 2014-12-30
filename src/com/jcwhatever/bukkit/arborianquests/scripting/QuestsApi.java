/* This file is part of ArborianQuests for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.arborianquests.scripting;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.Msg;
import com.jcwhatever.bukkit.arborianquests.quests.Quest;
import com.jcwhatever.bukkit.arborianquests.quests.QuestStatus;
import com.jcwhatever.bukkit.arborianquests.quests.QuestStatus.CurrentQuestStatus;
import com.jcwhatever.bukkit.arborianquests.quests.QuestStatus.QuestCompletionStatus;
import com.jcwhatever.nucleus.collections.timed.LifespanEndAction;
import com.jcwhatever.nucleus.collections.timed.TimedHashSet;
import com.jcwhatever.nucleus.commands.response.CommandRequests;
import com.jcwhatever.nucleus.commands.response.IResponseHandler;
import com.jcwhatever.nucleus.commands.response.ResponseRequest;
import com.jcwhatever.nucleus.commands.response.ResponseType;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.ScriptApiInfo;
import com.jcwhatever.nucleus.scripting.api.NucleusScriptApi;
import com.jcwhatever.nucleus.scripting.api.IScriptApiObject;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Provide scripts with API for quests.
 */
@ScriptApiInfo(
        variableName = "quests",
        description = "Provide scripts API access for quests.")
public class QuestsApi extends NucleusScriptApi {

    private static TimedHashSet<ResponseRequest> _requests = new TimedHashSet<ResponseRequest>(20, 600);

    private static Flags _flagsApi = new Flags();
    private static Items _itemsApi = new Items();
    private static Locations _locationsApi = new Locations();
    private static Meta _metaApi = new Meta(ArborianQuests.getPlugin());
    private static Regions _regionsApi = new Regions();

    /**
     * Constructor.
     *
     * @param plugin  required plugin constructor for script api repository.
     */
    public QuestsApi(@SuppressWarnings("unused") Plugin plugin) {

        // quests is always the owning plugin.
        super(ArborianQuests.getPlugin());
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        return new ApiObject(script);
    }

    public static class ApiObject implements IScriptApiObject {

        private boolean _isDisposed;

        public final IScriptApiObject flags;
        public final IScriptApiObject items;
        public final IScriptApiObject locations;
        public final IScriptApiObject meta;
        public final IScriptApiObject regions;

        ApiObject(IEvaluatedScript script) {
            _requests.addOnLifespanEnd(new LifespanEndAction<ResponseRequest>() {
                @Override
                public void onEnd(ResponseRequest item) {
                    CommandRequests.cancel(item);
                }
            });

            flags = _flagsApi.getApiObject(script);
            items = _itemsApi.getApiObject(script);
            locations = _locationsApi.getApiObject(script);
            meta = _metaApi.getApiObject(script);
            regions = _regionsApi.getApiObject(script);
        }

        @Override
        public boolean isDisposed() {
            return _isDisposed;
        }

        @Override
        public void dispose() {
            for (ResponseRequest request : _requests)
                CommandRequests.cancel(request);

            flags.dispose();
            items.dispose();
            locations.dispose();
            meta.dispose();
            regions.dispose();

            _isDisposed = true;
        }

        /**
         * Create a primary quest via script. Quests are not stored and must be
         * created by the script each time it is loaded.
         *
         * @param name        The name of the quest.
         * @param displayName The displayName of the quest.
         */
        public Quest create(String name, String displayName) {
            PreCon.notNullOrEmpty(name, "Quest name cannot be null", "Quest name cannot be empty");
            PreCon.notNullOrEmpty(displayName, "Display name cannot be null", "Display name cannot be empty");

            return ArborianQuests.getQuestManager().createPrimary(name, displayName);
        }

        /**
         * Create a sub quest of a primary quest.
         *
         * @param primaryName  The primary quest name.
         * @param name         The sub quest name.
         * @param displayName  The sub quest display name.
         */
        public Quest createSub(String primaryName, String name, String displayName) {
            PreCon.notNullOrEmpty(primaryName, "Primary quest name null", "Primary quest name empty");
            PreCon.notNullOrEmpty(name, "Quest name null", "Quest name empty");
            PreCon.notNullOrEmpty(displayName, "Display name null", "Display name empty");

            Quest primary = ArborianQuests.getQuestManager().getPrimary(primaryName);
            PreCon.notNull(primary, "Primary quest '{0}' not found.", primaryName);

            return primary.createQuest(name, displayName);
        }

        /**
         * Remove a quest.
         *
         * @param quest The quest to discard.
         */
        public boolean dispose(Quest quest) {
            PreCon.notNull(quest, "Quest null");

            return ArborianQuests.getQuestManager().dispose(quest);
        }

        /**
         * Determine if a player has accepted a quest.
         *
         * @param player    The player to check.
         * @param questName The name of the quest.
         *
         * @return  True if the player is in the quest.
         */
        public boolean isInQuest(Object player , String questName) {
            PreCon.notNull(player, "Player null");
            PreCon.notNullOrEmpty(questName, "Quest name null", "Quest name empty");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p, "Invalid player");

            Quest quest = ArborianQuests.getQuestManager().getPrimary(questName);
            return quest != null && quest.getStatus(p).getCurrentStatus() == CurrentQuestStatus.IN_PROGRESS;
        }

        /**
         * Determine if a player has already completed a quest.
         *
         * @param player     The player to check.
         * @param questName  The name of the quest.
         */
        public boolean hasCompleted(Object player, String questName) {
            PreCon.notNull(player, "Player null");
            PreCon.notNullOrEmpty(questName, "Quest name null", "Quest name empty");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p);

            Quest quest = ArborianQuests.getQuestManager().getPrimary(questName);
            return quest != null && quest.getStatus(p).getCompletionStatus() == QuestCompletionStatus.COMPLETED;
        }

        /**
         * Mark a players quest status as complete.
         *
         * @param player     The player.
         * @param questName  The name of the quest.
         *
         * @return True if the player quest is completed.
         */
        public boolean complete(Object player, String questName) {
            PreCon.notNull(player, "Player null");
            PreCon.notNullOrEmpty(questName, "Quest name null", "Quest name is empty");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p, "Invalid player");

            Quest quest = ArborianQuests.getQuestManager().getPrimary(questName);
            if (quest == null)
                return false;

            QuestStatus status = quest.getStatus(p);

            if (status.getCompletionStatus() != QuestCompletionStatus.NOT_COMPLETED)
                return true;

            quest.finish(p);

            return true;
        }

        /**
         * Join player to a quest.
         *
         * @param player      The player.
         * @param questName   The name of the quest.
         *
         * @return True if quest was found and player joined.
         */
        public boolean joinQuest(Object player, String questName) {
            PreCon.notNull(player, "Player null");
            PreCon.notNullOrEmpty(questName, "Quest name null", "Quest name empty");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p, "Invalid player");

            final Quest quest = ArborianQuests.getQuestManager().getPrimary(questName);
            if (quest == null)
                return false;

            quest.accept(p);

            return true;
        }

        /**
         * Ask the player to accept a quest.
         *
         * @param player     The player to ask.
         * @param questName  The name of the quest.
         * @param onAccept   Runnable to run if the player accepts.
         */
        public void queryQuest(Object player, String questName, @Nullable final Runnable onAccept) {
            PreCon.notNull(player, "Player null");
            PreCon.notNullOrEmpty(questName, "Quest name null", "Quest name empty");

            final Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p, "Invalid player");

            final Quest quest = ArborianQuests.getQuestManager().getPrimary(questName);
            PreCon.notNull(quest, "Quest named '{0}' not found", questName);

            IResponseHandler handler = new IResponseHandler() {

                @Override
                public void onResponse(ResponseType response) {

                    if (response == ResponseType.ACCEPT) {

                        quest.accept(p);

                        if (onAccept != null)
                            onAccept.run();
                    }
                }
            };

            ResponseRequest request = CommandRequests.request(ArborianQuests.getPlugin(),
                    questName, p, handler, ResponseType.ACCEPT);

            _requests.add(request);

            Msg.tell(p, "{WHITE}Type '{YELLOW}/accept{WHITE}' to accept the quest.");
            Msg.tell(p, "Expires in 30 seconds.");
        }

        /**
         * Ask the player a question.
         *
         * @param player    The player to ask.
         * @param context   The name of the context.
         * @param question  The question to ask the player.
         * @param onAccept  Runnable to run if the player accepts.
         */
        public void query(Object player, String context, String question,final Runnable onAccept) {
            PreCon.notNull(player, "Player null");
            PreCon.notNullOrEmpty(context, "Context null", "Context empty");
            PreCon.notNull(question, "Question null", "Question empty");
            PreCon.notNull(onAccept, "onAccept null");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p, "Invalid player");

            IResponseHandler handler = new IResponseHandler() {

                @Override
                public void onResponse(ResponseType response) {

                    if (response == ResponseType.YES) {
                        onAccept.run();
                    }
                }
            };

            ResponseRequest request = CommandRequests.request(ArborianQuests.getPlugin(),
                    context, p, handler, ResponseType.YES);

            _requests.add(request);

            Msg.tell(p, question);
            Msg.tell(p, "{WHITE}Type '{YELLOW}/yes{WHITE}' or ignore.");
            Msg.tell(p, "Expires in 30 seconds.");
        }
    }
}
