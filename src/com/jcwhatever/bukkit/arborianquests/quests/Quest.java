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


package com.jcwhatever.bukkit.arborianquests.quests;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.bukkit.arborianquests.quests.QuestStatus.CurrentQuestStatus;
import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Utils;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Represents a quest and players within the quest.
 */
public abstract class Quest implements INamed {

    private static Multimap<UUID, Quest> _playerQuests =
            MultimapBuilder.hashKeys(100).hashSetValues().build();

    private final String _questName;
    private String _displayName;
    private final IDataNode _dataNode;
    private final IDataNode _playerNode;
    private final IDataNode _questNode;
    private final Map<String, SubQuest> _subQuests = new HashMap<>(5);

    /**
     * Get an unmodifiable {@code Set} of {@code Quest}'s that
     * the player is in
     * .
     * @param p  The player.
     */
    public static Set<Quest> getPlayerQuests(Player p) {
        return CollectionUtils.unmodifiableSet(_playerQuests.get(p.getUniqueId()));
    }

    /**
     * Constructor.
     *
     * @param questName    The name of the quest.
     * @param displayName  The quest display name.
     * @param dataNode     The quest data node.
     */
    public Quest(String questName, String displayName, IDataNode dataNode) {
        PreCon.notNullOrEmpty(questName);
        PreCon.notNullOrEmpty(displayName);
        PreCon.notNull(dataNode);

        _questName = questName;
        _displayName = displayName;
        _dataNode = dataNode;
        _playerNode = dataNode.getNode("players");
        _questNode = dataNode.getNode("quests");
    }

    @Override
    public String getName() {
        return _questName;
    }

    public String getFullName() {
        return _questName;
    }

    public String getDisplayName() {
        return _displayName;
    }

    public void setDisplayName(String displayName) {
        _displayName = displayName;
    }

    /**
     * Get a sub quest of the quest by name.
     *
     * @param questName  The name of the sub quest.
     */
    @Nullable
    public SubQuest getQuest(String questName) {
        PreCon.notNullOrEmpty(questName);

        return _subQuests.get(questName.toLowerCase());
    }

    /**
     * Get all sub quests.
     */
    public List<SubQuest> getQuests() {
        return new ArrayList<>(_subQuests.values());
    }

    /**
     * Get or create a sub quest of the quest.
     *
     * @param questName    The quest name.
     * @param displayName  The quest display name.
     */
    public Quest createQuest(String questName, String displayName) {
        PreCon.notNullOrEmpty(questName);
        PreCon.notNullOrEmpty(displayName);

        questName = questName.toLowerCase();

        SubQuest quest = _subQuests.get(questName);
        if (quest != null) {
            quest.setDisplayName(displayName);
            return quest;
        }

        IDataNode node = _dataNode.getNode("quests." + questName);

        quest = new SubQuest(this, questName, displayName, node);
        node.set("display", displayName);
        node.saveAsync(null);

        _subQuests.put(questName, quest);

        return quest;
    }

    public boolean removeQuest(String questName) {
        PreCon.notNullOrEmpty(questName);

        questName = questName.toLowerCase();

        SubQuest quest = _subQuests.remove(questName);
        if (quest == null)
            return false;

        IDataNode node = _dataNode.getNode("quests." + questName);
        node.remove();
        node.saveAsync(null);

        return true;
    }

    /**
     * Get a players current status in the quest.
     *
     * @param p  The player to check.
     */
    public QuestStatus getStatus(Player p) {
        //noinspection ConstantConditions
        return getStatus(p.getUniqueId());
    }

    /**
     * Get a players current status in the quest.
     *
     * @param playerId  The id of the player to check.
     */
    public QuestStatus getStatus(UUID playerId) {
        //noinspection ConstantConditions
        return _playerNode.getEnum(playerId.toString() + ".status", QuestStatus.NONE, QuestStatus.class);
    }

    /**
     * Accept the player into the quest.
     *
     * @param p  The player to accept.
     */
    public void accept(Player p) {
        accept(p.getUniqueId());
    }

    /**
     * Accept the player into the quest.
     *
     * @param playerId  The id of the player.
     */
    public void accept(UUID playerId) {

        QuestStatus status = getStatus(playerId);

        switch (status) {
            case NONE:
                setStatus(playerId, QuestStatus.INCOMPLETE);
                break;

            case COMPLETED:
                setStatus(playerId, QuestStatus.RERUN);
                break;

            case INCOMPLETE:
                // fall through
            case RERUN:
                // do nothing
                break;
        }
    }

    /**
     * Flag a player as having completed the quest.
     *
     * @param p  The player to flag.
     */
    public void finish(Player p) {
        finish(p.getUniqueId());
    }

    /**
     * Flag a player as having completed the quest.
     *
     * @param playerId  The id of the player to flag.
     */
    public void finish(UUID playerId) {

        QuestStatus status = getStatus(playerId);

        switch (status) {
            case RERUN:
                setStatus(playerId, QuestStatus.COMPLETED);
                break;

            case INCOMPLETE:
                setStatus(playerId, QuestStatus.NONE);
                break;

            case NONE:
                // fall through
            case COMPLETED:
                break;
        }
    }

    /**
     * Cancel the quest for a player.
     *
     * @param p  The player.
     */
    public void cancel(Player p) {
        cancel(p.getUniqueId());
    }

    /**
     * Cancel the quest for a player.
     *
     * @param playerId  The id of the player.
     */
    public void cancel(UUID playerId) {

        QuestStatus status = getStatus(playerId);

        switch (status) {
            case RERUN:
                // fall through
            case COMPLETED:
                setStatus(playerId, QuestStatus.COMPLETED);
                break;

            case NONE:
                // fall through
            case INCOMPLETE:
                setStatus(playerId, QuestStatus.NONE);
                break;
        }
    }

    /**
     * Determine if a player has a flag set.
     *
     * @param playerId  The ID of the player to check
     * @param flagName  The name of the flag
     *
     * @return  True if the flag is set.
     */
    public boolean hasFlag(UUID playerId, String flagName) {
        PreCon.notNull(playerId);
        PreCon.notNullOrEmpty(flagName);

        return _playerNode.getBoolean(playerId.toString() + ".flags." + flagName, false);
    }

    /**
     * Set a flag on a player.
     *
     * @param playerId  The players ID.
     * @param flagName  The name of the flag.
     */
    public void setFlag(UUID playerId, String flagName) {
        PreCon.notNull(playerId);
        PreCon.notNullOrEmpty(flagName);

        _playerNode.set(playerId.toString() + ".flags." + flagName, true);
        _playerNode.saveAsync(null);
    }

    /**
     * Clear a flag on a player.
     *
     * @param playerId  The players ID.
     * @param flagName  The name of the flag.
     */
    public void clearFlag(UUID playerId, String flagName) {
        PreCon.notNull(playerId);
        PreCon.notNullOrEmpty(flagName);

        _playerNode.remove(playerId.toString() + ".flags." + flagName);
        _playerNode.saveAsync(null);
    }

    /**
     * Clear all flags set on a player.
     *
     * @param playerId  The ID of the player.
     */
    public void clearFlags(UUID playerId) {
        PreCon.notNull(playerId);

        cancel(playerId);

        _playerNode.remove(playerId.toString() + ".flags");
        _playerNode.saveAsync(null);
    }

    // Set the quest status of a player
    private void setStatus(UUID playerId, QuestStatus status) {

        if (status == QuestStatus.NONE) {
            _playerNode.remove(playerId.toString() + ".status");
        }
        else {
            _playerNode.set(playerId.toString() + ".status", status);
        }

        if (status.getCurrentStatus() == CurrentQuestStatus.NONE) {
            _playerQuests.remove(playerId, this);
        }
        else if (status.getCurrentStatus() == CurrentQuestStatus.IN_PROGRESS) {
            _playerQuests.put(playerId, this);
        }

        _playerNode.saveAsync(null);
    }

    // intitial settings load
    private void loadSettings() {

        // load players
        Set<String> rawPlayerIds = _playerNode.getSubNodeNames();

        for (String rawId : rawPlayerIds) {
            UUID id = Utils.getId(rawId);
            if (id == null)
                continue;

            QuestStatus status = _playerNode.getEnum(rawId, QuestStatus.NONE, QuestStatus.class);
            //noinspection ConstantConditions
            if (status.getCurrentStatus() != CurrentQuestStatus.IN_PROGRESS)
                continue;

            _playerQuests.put(id, this);
        }

        // load sub quests
        Set<String> questNames = _questNode.getSubNodeNames();

        for (String questName : questNames) {

            IDataNode node = _questNode.getNode(questName);

            String displayName = node.getString("display", questName);
            if (displayName == null)
                throw new AssertionError();

            SubQuest quest = new SubQuest(this, questName, displayName, node);

            _subQuests.put(questName.toLowerCase(), quest);
        }
    }
}
