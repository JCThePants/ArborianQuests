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


package com.jcwhatever.arborianquests.quests;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.arborianquests.quests.QuestStatus.CurrentQuestStatus;
import com.jcwhatever.nucleus.mixins.IHierarchyNode;
import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Represents a quest and players within the quest.
 */
public abstract class Quest implements INamed, IHierarchyNode<Quest> {

    private static Multimap<UUID, Quest> _playerQuests =
            MultimapBuilder.hashKeys(100).hashSetValues().build();

    private final String _questName;
    private String _displayName;
    private final IDataNode _dataNode;
    private final IDataNode _playerNodes;
    private final IDataNode _questNodes;
    private final ObjectiveDescriptions _objectives;
    private final Map<String, Quest> _subQuests = new HashMap<>(5);

    /**
     * Get an unmodifiable {@link java.util.Set} of {@link Quest}'s that
     * the player is in.
     *
     * @param player  The player.
     */
    public static Set<Quest> getPlayerQuests(Player player) {
        return CollectionUtils.unmodifiableSet(_playerQuests.get(player.getUniqueId()));
    }

    /**
     * Get a quest using a quest path.
     *
     * @param questPath  The quest path.
     *
     * @return  The quest or null if not found.
     */
    @Nullable
    public static Quest getQuestFromPath(String questPath) {
        PreCon.notNull(questPath, "questPath");

        String[] pathComponents = TextUtils.PATTERN_DOT.split(questPath);

        Quest quest = ArborianQuests.getQuestManager().getQuest(pathComponents[0]);
        if (quest == null)
            return null;

        for (int i=1; i < pathComponents.length; i++) {
            quest = quest.getQuest(pathComponents[i]);
            if (quest == null)
                return null;
        }

        return quest;
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
        _playerNodes = dataNode.getNode("players");
        _questNodes = dataNode.getNode("quests");
        _objectives = new ObjectiveDescriptions(dataNode.getNode("objectives"), _playerNodes);
    }

    /**
     * Get the quests data node name.
     */
    @Override
    public String getName() {
        return _questName;
    }

    public String getPathName() {
        return _questName;
    }

    /**
     * Get the quests display name.
     */
    public String getDisplayName() {
        return _displayName;
    }

    /**
     * Set the quests display name.
     *
     * @param displayName  The display name.
     */
    public void setDisplayName(String displayName) {
        _displayName = displayName;
    }

    /**
     * Get the quests objective descriptions manager.
     */
    public ObjectiveDescriptions getObjectives() {
        return _objectives;
    }

    /**
     * Get a sub quest of the quest by name.
     *
     * @param questName  The name of the sub quest.
     */
    @Nullable
    public Quest getQuest(String questName) {
        PreCon.notNullOrEmpty(questName);

        return _subQuests.get(questName.toLowerCase());
    }

    /**
     * Get all sub quests.
     */
    public List<Quest> getQuests() {
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

        Quest quest = _subQuests.get(questName);
        if (quest != null) {
            quest.setDisplayName(displayName);
            return quest;
        }

        IDataNode node = _dataNode.getNode("quests." + questName);

        quest = new SubQuest(this, questName, displayName, node);
        node.set("display", displayName);
        node.save();

        _subQuests.put(questName, quest);

        return quest;
    }

    /**
     * Remove a sub quest of the quest.
     *
     * @param questName The name of the quest to remove.
     *
     * @return  True if found and removed.
     */
    public boolean removeQuest(String questName) {
        PreCon.notNullOrEmpty(questName);

        questName = questName.toLowerCase();

        Quest quest = _subQuests.remove(questName);
        if (quest == null)
            return false;

        IDataNode node = _dataNode.getNode("quests." + questName);
        node.remove();
        node.save();

        return true;
    }

    /**
     * Get a players current status in the quest.
     *
     * @param player  The player to check.
     */
    public QuestStatus getStatus(Player player) {
        //noinspection ConstantConditions
        return getStatus(player.getUniqueId());
    }

    /**
     * Get a players current status in the quest.
     *
     * @param playerId  The id of the player to check.
     */
    public QuestStatus getStatus(UUID playerId) {
        //noinspection ConstantConditions
        return _playerNodes.getEnum(playerId.toString() + ".status", QuestStatus.NONE, QuestStatus.class);
    }

    /**
     * Accept the player into the quest.
     *
     * @param player  The player to accept.
     */
    public void accept(Player player) {
        accept(player.getUniqueId());
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
     * @param player  The player to flag.
     */
    public void finish(Player player) {
        finish(player.getUniqueId());
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
                // fall through
            case INCOMPLETE:
                setStatus(playerId, QuestStatus.COMPLETED);
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
     * @param player  The player.
     */
    public void cancel(Player player) {
        cancel(player.getUniqueId());
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

        return _playerNodes.getBoolean(playerId.toString() + ".flags." + flagName, false);
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

        _playerNodes.set(playerId.toString() + ".flags." + flagName, true);
        _playerNodes.save();
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

        _playerNodes.remove(playerId.toString() + ".flags." + flagName);
        _playerNodes.save();
    }

    /**
     * Clear all flags set on a player.
     *
     * @param playerId  The ID of the player.
     */
    public void clearFlags(final UUID playerId) {
        PreCon.notNull(playerId);

        cancel(playerId);

        _playerNodes.remove(playerId.toString());
        _playerNodes.save();

        for (Quest quest : _subQuests.values()) {
            quest.clearFlags(playerId);
            quest.setStatus(playerId, QuestStatus.NONE);
        }
    }

    /**
     * Get the flags set on a player.
     *
     * @param playerId  The unique ID of the player.
     */
    public Collection<String> getFlags(UUID playerId) {
        PreCon.notNull(playerId);

        IDataNode flagNode = _playerNodes.getNode(playerId.toString() + ".flags");

        return flagNode.getSubNodeNames();
    }

    /**
     * Get the flags set on a player.
     *
     * @param playerId  The unique ID of the player.
     */
    public <T extends Collection<String>> T getFlags(UUID playerId, T output) {
        PreCon.notNull(playerId);
        PreCon.notNull(output);

        IDataNode flagNode = _playerNodes.getNode(playerId.toString() + ".flags");

        return flagNode.getSubNodeNames(output);
    }

    // Set the quest status of a player
    private void setStatus(UUID playerId, QuestStatus status) {

        if (status == QuestStatus.NONE) {
            _playerNodes.remove(playerId.toString());
        }
        else {
            _playerNodes.set(playerId.toString() + ".status", status);
        }

        if (status.getCurrentStatus() == CurrentQuestStatus.NONE) {
            _playerQuests.remove(playerId, this);
        }
        else if (status.getCurrentStatus() == CurrentQuestStatus.IN_PROGRESS) {
            _playerQuests.put(playerId, this);
        }

        _playerNodes.save();
    }

    // initial settings load
    private void loadSettings() {

        // load players
        for (IDataNode playerNode : _playerNodes) {
            UUID id = TextUtils.parseUUID(playerNode.getName());
            if (id == null)
                continue;

            QuestStatus status = playerNode.getEnum("", QuestStatus.NONE, QuestStatus.class);
            //noinspection ConstantConditions
            if (status.getCurrentStatus() != CurrentQuestStatus.IN_PROGRESS)
                continue;

            _playerQuests.put(id, this);
        }

        // load sub quests
        for (IDataNode questNode : _questNodes) {

            String questName = questNode.getName();

            assert questName != null;

            String displayName = questNode.getString("display", questName);
            if (displayName == null)
                throw new AssertionError();

            SubQuest quest = new SubQuest(this, questName, displayName, questNode);

            _subQuests.put(questName.toLowerCase(), quest);
        }
    }
}
