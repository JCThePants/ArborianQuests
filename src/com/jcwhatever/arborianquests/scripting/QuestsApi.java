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


package com.jcwhatever.arborianquests.scripting;

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.arborianquests.Msg;
import com.jcwhatever.arborianquests.quests.Quest;
import com.jcwhatever.arborianquests.quests.QuestStatus;
import com.jcwhatever.arborianquests.quests.QuestStatus.CurrentQuestStatus;
import com.jcwhatever.arborianquests.quests.QuestStatus.QuestCompletionStatus;
import com.jcwhatever.arborianquests.quests.SubQuest;
import com.jcwhatever.nucleus.collections.timed.TimedHashSet;
import com.jcwhatever.nucleus.commands.response.CommandRequests;
import com.jcwhatever.nucleus.commands.response.ResponseRequest;
import com.jcwhatever.nucleus.commands.response.ResponseType;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.ScriptApiInfo;
import com.jcwhatever.nucleus.scripting.api.IScriptApiObject;
import com.jcwhatever.nucleus.scripting.api.NucleusScriptApi;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.result.FutureSubscriber;
import com.jcwhatever.nucleus.utils.observer.result.Result;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Provide scripts with API for quests.
 */
@ScriptApiInfo(
        variableName = "quests",
        description = "Provide scripts API access for quests.")
public class QuestsApi extends NucleusScriptApi {

    private static final TimedHashSet<ResponseRequest> _requests
            = new TimedHashSet<ResponseRequest>(ArborianQuests.getPlugin(), 20, 600);

    private static final Map<String, Quest> _pathCache = new HashMap<>(10);

    private static final Flags _flagsApi = new Flags();
    private static final Items _itemsApi = new Items();
    private static final Locations _locationsApi = new Locations();
    private static final Meta _metaApi = new Meta(ArborianQuests.getPlugin());
    private static final Regions _regionsApi = new Regions();

    /**
     * Utility to get quest from quest path.
     *
     * @param questPath      The quest path.
     * @param isNullAllowed  True to return null if not found, False to throw exception.
     */
    public static Quest getQuest(String questPath, boolean isNullAllowed) {
        PreCon.notNullOrEmpty(questPath, "questPath");

        Quest quest = _pathCache.get(questPath);
        if (quest != null)
            return quest;

        quest = Quest.getQuestFromPath(questPath);
        if (quest == null && isNullAllowed)
            return null;

        PreCon.isValid(quest != null, "Quest path '{0}' not found.", questPath);

        _pathCache.put(questPath, quest);

        return quest;
    }

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
            _requests.onLifespanEnd(new UpdateSubscriber<ResponseRequest>() {
                @Override
                public void on(ResponseRequest request) {
                    CommandRequests.cancel(request);
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

            synchronized (_requests) {
                for (ResponseRequest request : _requests)
                    CommandRequests.cancel(request);
            }

            flags.dispose();
            items.dispose();
            locations.dispose();
            meta.dispose();
            regions.dispose();

            _pathCache.clear();

            _isDisposed = true;
        }

        /**
         * Create a primary quest via script. Quests are not stored and must be
         * created by the script each time it is loaded.
         *
         * @param questName        The name of the quest.
         * @param displayName The displayName of the quest.
         */
        public Quest create(String questName, String displayName) {
            PreCon.notNullOrEmpty(questName, "questName");
            PreCon.notNullOrEmpty(displayName, "displayName");

            return ArborianQuests.getQuestManager().createPrimary(questName, displayName);
        }

        /**
         * Create a sub quest of a primary quest.
         *
         * @param parentQuestPath  The path to the parent quest.
         * @param subQuestName     The sub quest name.
         * @param displayName      The sub quest display name.
         */
        public Quest createSub(String parentQuestPath, String subQuestName, String displayName) {
            PreCon.notNullOrEmpty(parentQuestPath, "parentQuestPath");
            PreCon.notNullOrEmpty(subQuestName, "subQuestName");
            PreCon.notNullOrEmpty(displayName, "displayName");

            Quest parent = getQuest(parentQuestPath, false);
            return parent.createQuest(subQuestName, displayName);
        }

        /**
         * Get the current assignment description for the specified quest.
         *
         * @param questPath  The quests path name.
         */
        @Nullable
        public String getAssignment(String questPath) {
            PreCon.notNullOrEmpty(questPath, "questPath");

            Quest quest = getQuest(questPath, false);
            return quest.getAssignment();
        }

        /**
         * Set the current assignment description for the specified quest.
         *
         * @param questPath   The quests path name.
         * @param assignment  The assignment description to set.
         */
        public void setAssignment(String questPath, @Nullable String assignment) {
            PreCon.notNullOrEmpty(questPath, "questPath");

            Quest quest = getQuest(questPath, false);
            quest.setAssignment(assignment);
        }

        /**
         * Remove a quest.
         *
         * @param quest The quest to discard.
         */
        public boolean dispose(Quest quest) {
            PreCon.notNull(quest, "quest");

            return ArborianQuests.getQuestManager().dispose(quest);
        }

        /**
         * Determine if a player has accepted a quest.
         *
         * @param player     The player to check.
         * @param questPath  The name of the quest.
         *
         * @return  True if the player is in the quest.
         */
        public boolean isInQuest(Object player , String questPath) {
            PreCon.notNull(player, "player");
            PreCon.notNullOrEmpty(questPath, "questPath");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            Quest quest = getQuest(questPath, true);
            return quest != null && quest.getStatus(p).getCurrentStatus() == CurrentQuestStatus.IN_PROGRESS;
        }

        /**
         * Determine if a player has already completed a quest.
         *
         * @param player     The player to check.
         * @param questPath  The path name of the quest.
         */
        public boolean hasCompleted(Object player, String questPath) {
            PreCon.notNull(player, "player");
            PreCon.notNullOrEmpty(questPath, "questPath");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            Quest quest = getQuest(questPath, true);
            return quest != null && quest.getStatus(p).getCompletionStatus() == QuestCompletionStatus.COMPLETED;
        }

        /**
         * Mark a players quest status as complete.
         *
         * @param player     The player.
         * @param questPath  The path name of the quest.
         *
         * @return True if the player quest is completed.
         */
        public boolean complete(Object player, String questPath) {
            PreCon.notNull(player, "player");
            PreCon.notNullOrEmpty(questPath, "questPath");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            Quest quest = getQuest(questPath, false);

            QuestStatus status = quest.getStatus(p);

            if (status.getCompletionStatus() != QuestCompletionStatus.NOT_COMPLETED)
                return true;

            quest.finish(p);

            return true;
        }

        /**
         * Join player to a quest.
         *
         * @param player     The player.
         * @param questPath  The path name of the quest.
         *
         * @return True if quest was found and player joined.
         */
        public boolean joinQuest(Object player, String questPath) {
            PreCon.notNull(player, "player");
            PreCon.notNullOrEmpty(questPath, "questPath");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            Quest quest = getQuest(questPath, false);

            if (quest instanceof SubQuest) {

                SubQuest subQuest = (SubQuest)quest;
                QuestStatus status = subQuest.getParent().getStatus(p);

                PreCon.isValid(status.getCurrentStatus() == CurrentQuestStatus.IN_PROGRESS,
                        "Cannot join a sub quest unless already joined to the parent quest.");
            }

            quest.accept(p);

            return true;
        }

        /**
         * Ask the player to accept a quest.
         *
         * @param player     The player to ask.
         * @param questPath  The path name of the quest.
         * @param onAccept   Runnable to run if the player accepts.
         */
        public void queryQuest(Object player, String questPath, final Runnable onAccept) {
            PreCon.notNull(player, "player");
            PreCon.notNullOrEmpty(questPath, "questPath");
            PreCon.notNull(onAccept, "onAccept");

            final Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            final Quest quest = getQuest(questPath, false);

            if (quest instanceof SubQuest) {

                SubQuest subQuest = (SubQuest)quest;
                QuestStatus status = subQuest.getParent().getStatus(p);

                PreCon.isValid(status.getCurrentStatus() == CurrentQuestStatus.IN_PROGRESS,
                        "Cannot join a sub quest unless already joined to the parent quest.");
            }

            ResponseRequest request = CommandRequests.request(ArborianQuests.getPlugin(),
                    questPath, p, ResponseType.ACCEPT)
                    .onSuccess(new FutureSubscriber<ResponseType>() {
                        @Override
                        public void on(Result<ResponseType> result) {
                            quest.accept(p);
                            onAccept.run();
                        }
                    });

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
        public void query(Object player, String context, String question, final Runnable onAccept) {
            PreCon.notNull(player, "player");
            PreCon.notNullOrEmpty(context, "context");
            PreCon.notNull(question, "question");
            PreCon.notNull(onAccept, "onAccept");

            Player p = PlayerUtils.getPlayer(player);
            PreCon.isValid(p != null, "Invalid player");

            ResponseRequest request = CommandRequests.request(ArborianQuests.getPlugin(),
                    context, p, ResponseType.YES)
                    .onSuccess(new FutureSubscriber<ResponseType>() {
                        @Override
                        public void on(Result<ResponseType> result) {
                            onAccept.run();
                        }
                    });

            _requests.add(request);

            Msg.tell(p, question);
            Msg.tell(p, "{WHITE}Type '{YELLOW}/yes{WHITE}' or ignore.");
            Msg.tell(p, "Expires in 30 seconds.");
        }


    }
}
