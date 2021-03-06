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
import com.jcwhatever.arborianquests.Lang;
import com.jcwhatever.arborianquests.Msg;
import com.jcwhatever.arborianquests.quests.Quest;
import com.jcwhatever.arborianquests.quests.QuestStatus;
import com.jcwhatever.arborianquests.quests.QuestStatus.CurrentQuestStatus;
import com.jcwhatever.arborianquests.quests.QuestStatus.QuestCompletionStatus;
import com.jcwhatever.arborianquests.quests.SubQuest;
import com.jcwhatever.nucleus.collections.observer.subscriber.SubscriberArrayDeque;
import com.jcwhatever.nucleus.managed.commands.response.IRequestContext;
import com.jcwhatever.nucleus.managed.commands.response.ResponseRequestor;
import com.jcwhatever.nucleus.managed.commands.response.ResponseType;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.utils.text.components.IChatClickable.ClickAction;
import com.jcwhatever.nucleus.utils.text.components.IChatHoverable.HoverAction;
import com.jcwhatever.nucleus.utils.text.format.args.ClickableArgModifier;
import com.jcwhatever.nucleus.utils.text.format.args.HoverableArgModifier;
import com.jcwhatever.nucleus.utils.text.format.args.TextArg;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Provide scripts with API for quests.
 */
public class QuestsApi implements IDisposable {

    private static final SubscriberArrayDeque<IUpdateSubscriber> REQUESTS = new SubscriberArrayDeque<>(10);
    private static final Map<String, Quest> PATH_CACHE = new HashMap<>(10);

    private static final TextArg ACCEPT_CLICK_ARG = new TextArg("{AQUA}Click here",
            new ClickableArgModifier(ClickAction.RUN_COMMAND, "/accept"),
            new HoverableArgModifier(HoverAction.SHOW_TEXT, "{YELLOW}Click to accept quest."));

    private static final TextArg ACCEPT_SUGGESTION_CLICK_ARG = new TextArg("{GOLD}/accept",
            new ClickableArgModifier(ClickAction.SUGGEST_COMMAND, "/accept"));

    private static final TextArg QUEST_INFO_CLICK_ARG = new TextArg("{GOLD}/q",
            new ClickableArgModifier(ClickAction.SUGGEST_COMMAND, "/q"));

    @Localizable static final String _QUEST_QUERY =
                    "{GREEN}------------------------------------------\n" +
                    "{WHITE}{0: click here} to accept quest or type {1: command}\n" +
                    "{YELLOW}Expires in 15 seconds.\n" +
                    "{GREEN}------------------------------------------";

    @Localizable static final String _QUEST_ACCEPTED =
            "{GREEN}{0: quest name} quest Accepted.\n{GRAY}Type '{1}' anytime to get current quest info.";

    @Localizable static final String _QUERY =
            "{WHITE}Type '{YELLOW}/yes{WHITE}' or ignore.\n" +
            "Expires in 15 seconds.";

    @Localizable static final String _REQUEST_EXPIRED = "{YELLOW}Request expired.";

    /**
     * Utility to get quest from quest path.
     *
     * @param questPath      The quest path.
     * @param isNullAllowed  True to return null if not found, False to throw exception.
     */
    public static Quest getQuest(String questPath, boolean isNullAllowed) {
        PreCon.notNullOrEmpty(questPath, "questPath");

        Quest quest = PATH_CACHE.get(questPath);
        if (quest != null)
            return quest;

        quest = Quest.getQuestFromPath(questPath);
        if (quest == null && isNullAllowed)
            return null;

        PreCon.isValid(quest != null, "Quest path '{0}' not found.", questPath);

        PATH_CACHE.put(questPath, quest);

        return quest;
    }

    private boolean _isDisposed;

    public final IDisposable flags;
    public final IDisposable items;
    public final IDisposable locations;
    public final IDisposable waypoints;
    public final IDisposable meta;
    public final IDisposable regions;
    public final IDisposable dialogs;
    public final IDisposable npcClick;

    public QuestsApi() {

        flags = new Flags();
        items = new Items();
        locations = new Locations();
        waypoints = new Waypoints();
        meta = new Meta();
        regions = new Regions();
        dialogs = new Dialog();
        npcClick = new NpcClick();
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        synchronized (REQUESTS) {
            while (!REQUESTS.isEmpty()) {
                REQUESTS.remove().dispose();
            }
        }

        flags.dispose();
        items.dispose();
        locations.dispose();
        waypoints.dispose();
        meta.dispose();
        regions.dispose();
        npcClick.dispose();

        PATH_CACHE.clear();

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
        assert parent != null;

        return parent.createQuest(subQuestName, displayName);
    }

    /**
     * Get the players current objective description for the specified quest.
     *
     * @param player     The player to check.
     * @param questPath  The quests path name.
     */
    @Nullable
    public String getObjective(Object player, String questPath) {
        PreCon.notNull(player, "player");
        PreCon.notNullOrEmpty(questPath, "questPath");

        Player p = PlayerUtils.getPlayer(player);
        PreCon.isValid(p != null, "Invalid player object.");

        Quest quest = getQuest(questPath, false);
        assert quest != null;

        return quest.getObjectives().getPlayerObjective(p.getUniqueId());
    }

    /**
     * Set the current assignment description for the specified quest.
     *
     * @param player        The player to set objective text for.
     * @param questPath     The quests path name.
     * @param objectiveKey  The unique key that identifies the description to use.
     *
     * @return  True if the assignment was set. False if the player node was not found or
     * the player is already assigned to the specified assignment.
     */
    public boolean setObjectiveKey(Object player, String questPath, String objectiveKey) {
        PreCon.notNull(player, "player");
        PreCon.notNullOrEmpty(questPath, "questPath");
        PreCon.notNullOrEmpty(objectiveKey, "objectiveKey");

        Player p = PlayerUtils.getPlayer(player);
        PreCon.isValid(p != null, "Invalid player object.");

        Quest quest = getQuest(questPath, false);
        assert quest != null;

        return quest.getObjectives().setPlayerObjective(p.getUniqueId(), objectiveKey);
    }

    /**
     * Set the current assignment description for the specified quest.
     *
     * <p>Provide the description which will be added and retrieved using the specified
     * objective key if it is not already set.</p>
     *
     * @param player        The player to set objective text for.
     * @param questPath     The quests path name.
     * @param objectiveKey  The unique key that identifies the description to use.
     * @param description   The description to add if the objectiveKey does not yet exist.
     *
     * @return  True if the assignment was set. False if the player node was not found or
     * the player is already assigned to the specified assignment.
     */
    public boolean setObjective(Object player, String questPath, String objectiveKey, String description) {
        PreCon.notNull(player, "player");
        PreCon.notNullOrEmpty(questPath, "questPath");
        PreCon.notNullOrEmpty(objectiveKey, "objectiveKey");
        PreCon.notNull(description, "description");

        Player p = PlayerUtils.getPlayer(player);
        PreCon.isValid(p != null, "Invalid player object.");

        Quest quest = getQuest(questPath, false);
        assert quest != null;

        return quest.getObjectives().setPlayerObjective(p.getUniqueId(), objectiveKey, description);
    }

    /**
     * Clear a players objective description for the specified quest.
     *
     * @param player     The player.
     * @param questPath  The path of the quest.
     */
    public void clearObjective(Object player, String questPath) {
        PreCon.notNull(player, "player");
        PreCon.notNullOrEmpty(questPath, "questPath");

        Player p = PlayerUtils.getPlayer(player);
        PreCon.isValid(p != null, "Invalid player object.");

        Quest quest = getQuest(questPath, false);
        assert quest != null;

        quest.getObjectives().clearPlayerObjective(p.getUniqueId());
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
        assert quest != null;

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
        assert quest != null;

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
        assert quest != null;

        if (quest instanceof SubQuest) {

            SubQuest subQuest = (SubQuest)quest;
            QuestStatus status = subQuest.getParent().getStatus(p);

            PreCon.isValid(status.getCurrentStatus() == CurrentQuestStatus.IN_PROGRESS,
                    "Cannot join a sub quest unless already joined to the parent quest.");
        }

        UpdateSubscriber<IRequestContext> subscriber = new UpdateSubscriber<IRequestContext>() {
            @Override
            public void on(IRequestContext argument) {
                quest.accept(p);
                onAccept.run();
            }
        };

        REQUESTS.add(subscriber);

        ResponseRequestor.contextBuilder(ArborianQuests.getPlugin())
                .name(questPath)
                .timeout(15)
                .response(ResponseType.ACCEPT)
                .build(p)
                .onRespond(subscriber)
                .onRespond(new UpdateSubscriber<IRequestContext>() {
                    @Override
                    public void on(IRequestContext argument) {
                        Msg.tell(p, Lang.get(_QUEST_ACCEPTED, quest.getDisplayName(), QUEST_INFO_CLICK_ARG));
                    }
                })
                .onTimeout(new UpdateSubscriber<IRequestContext>() {
                    @Override
                    public void on(IRequestContext argument) {
                        Msg.tell(p, Lang.get(_REQUEST_EXPIRED));
                    }
                })
                .sendRequest();

        Msg.tell(p, Lang.get(_QUEST_QUERY, ACCEPT_CLICK_ARG, ACCEPT_SUGGESTION_CLICK_ARG));
    }

    /**
     * Ask the player a question.
     *
     * @param player    The player to ask.
     * @param context   The name of the context.
     * @param question  The question to ask the player.
     * @param onAccept  Runnable to run if the player accepts.
     */
    public void query(Object player, String context, CharSequence question, final Runnable onAccept) {
        PreCon.notNull(player, "player");
        PreCon.notNullOrEmpty(context, "context");
        PreCon.notNull(question, "question");
        PreCon.notNull(onAccept, "onAccept");

        final Player p = PlayerUtils.getPlayer(player);
        PreCon.isValid(p != null, "Invalid player");

        UpdateSubscriber<IRequestContext> subscriber = new UpdateSubscriber<IRequestContext>() {
            @Override
            public void on(IRequestContext argument) {
                onAccept.run();
            }
        };

        REQUESTS.add(subscriber);

        ResponseRequestor.contextBuilder(ArborianQuests.getPlugin())
                .name(context)
                .timeout(15)
                .response(ResponseType.YES)
                .build(p)
                .onRespond(subscriber)
                .onTimeout(new UpdateSubscriber<IRequestContext>() {
                    @Override
                    public void on(IRequestContext argument) {
                        Msg.tell(p, Lang.get(_REQUEST_EXPIRED));
                    }
                })
                .sendRequest();

        Msg.tell(p, question);
        Msg.tell(p, Lang.get(_QUERY));
    }
}

