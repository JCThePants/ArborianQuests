/*
 * This file is part of ArborianQuests for Bukkit, licensed under the MIT License (MIT).
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

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Manages a quests assignment descriptions which are used by players to get a summary
 * of their current objective.
 */
public class ObjectiveDescriptions {

    private final IDataNode _dataNode;
    private final IDataNode _playerNodes;

    public ObjectiveDescriptions(IDataNode dataNode, IDataNode playerNode) {
        PreCon.notNull(dataNode);
        PreCon.notNull(playerNode);

        _dataNode = dataNode;
        _playerNodes = playerNode;
    }

    /**
     * Clear an assignment text by key.
     *
     * @param assignmentKey  The assignment key of the text to clear.
     */
    public void clearDescription(String assignmentKey) {
        PreCon.notNullOrEmpty(assignmentKey);

        _dataNode.remove(assignmentKey);
        _dataNode.save();
    }

    /**
     * Set an assignment text that can be retrieved with the specified key.
     *
     * @param assignmentKey  The unique key name.
     * @param text           The assignment text.
     */
    public void setDescription(String assignmentKey, @Nullable String text) {
        PreCon.notNullOrEmpty(assignmentKey);

        _dataNode.set(assignmentKey, text);
        _dataNode.save();
    }

    /**
     * Clear a players objective description.
     *
     * @param playerId  The ID of the player.
     */
    public void clearPlayerObjective(UUID playerId) {
        PreCon.notNull(playerId);

        IDataNode node = _playerNodes.getNode(playerId.toString());
        node.set("assignment", null);
        node.save();
    }

    /**
     * Get the current assignment description. This
     * is used to explain what the player needs to
     * do in order to complete their current objective.
     *
     * @param playerId  The ID of the player to get an assignment description for.
     *
     * @return  The text or null if not set.
     */
    @Nullable
    public String getPlayerObjective(UUID playerId) {
        PreCon.notNull(playerId);

        String assignmentKey = _playerNodes.getNode(playerId.toString()).getString("assignment");
        if (assignmentKey == null)
            return null;

        IDataNode assignments = _dataNode.getNode("assignments");

        return assignments.getString(assignmentKey);
    }

    /**
     * Set the players current assignment description.
     *
     * @param playerId       The ID of the player to set assignment text for.
     * @param assignmentKey  The unique key that identifies the assignment text to use.
     */
    public void setPlayerObjective(UUID playerId, String assignmentKey) {
        PreCon.notNull(playerId);
        PreCon.notNullOrEmpty(assignmentKey);

        if (!_playerNodes.hasNode(playerId.toString()))
            return;

        _playerNodes.getNode(playerId.toString()).set("assignment", assignmentKey);
        _playerNodes.save();
    }

    /**
     * Set the players current assignment description.
     *
     * @param playerId        The ID of the player to set assignment text for.
     * @param assignmentKey   The unique key that identifies the assignment text to use.
     * @param assignmentText  The text to use if the assignment key is not already set.
     */
    public void setPlayerObjective(UUID playerId, String assignmentKey, String assignmentText) {
        PreCon.notNull(playerId);
        PreCon.notNullOrEmpty(assignmentKey);
        PreCon.notNull(assignmentText);

        if (!_playerNodes.hasNode(playerId.toString()))
            return;

        IDataNode assignments = _dataNode.getNode("assignments");

        String assignment = assignments.getString(assignmentKey);

        if (assignment == null) {
            assignments.set(assignmentKey, assignmentText);
            assignments.save();
        }

        _playerNodes.getNode(playerId.toString()).set("assignment", assignmentKey);
        _playerNodes.save();
    }
}
