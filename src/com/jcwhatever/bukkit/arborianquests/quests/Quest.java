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

import com.jcwhatever.bukkit.arborianquests.quests.QuestStatus.CurrentQuestStatus;
import com.jcwhatever.bukkit.generic.collections.SetMap;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a quest and players within the quest.
 */
public class Quest {

    private String _questName;
    private String _displayName;
    private IDataNode _playerNode;

    private static SetMap<UUID, Quest> _playerQuests = new SetMap<>(100);

    @Nullable
    public static Set<Quest> getPlayerQuests(Player p) {
        return _playerQuests.getAll(p.getUniqueId());
    }

    public Quest(String questName, String displayName, IDataNode dataNode) {
        PreCon.notNullOrEmpty(questName);
        PreCon.notNull(displayName);
        PreCon.notNull(dataNode);

        _questName = questName;
        _displayName = displayName;
        _playerNode = dataNode.getNode("players");
    }

    public String getName() {
        return _questName;
    }

    public String getDisplayName() {
        return _displayName;
    }

    public QuestStatus getStatus(Player p) {
        //noinspection ConstantConditions
        return _playerNode.getEnum(p.getUniqueId().toString(), QuestStatus.NONE, QuestStatus.class);
    }

    public void acceptQuest(Player p) {

        QuestStatus status = getStatus(p);

        switch (status) {
            case NONE:
                setStatus(p, QuestStatus.INCOMPLETE);
                break;

            case COMPLETED:
                setStatus(p, QuestStatus.RERUN);
                break;

            case INCOMPLETE:
                // fall through
            case RERUN:
                // do nothing
                break;


        }
    }

    public void finishQuest(Player p) {

        QuestStatus status = getStatus(p);

        switch (status) {
            case RERUN:
                setStatus(p, QuestStatus.COMPLETED);
                break;

            case INCOMPLETE:
                setStatus(p, QuestStatus.NONE);
                break;

            case NONE:
                // fall through
            case COMPLETED:
                break;
        }
    }

    public void cancelQuest(Player p) {

        QuestStatus status = getStatus(p);

        switch (status) {
            case RERUN:
                // fall through
            case COMPLETED:
                setStatus(p, QuestStatus.COMPLETED);
                break;

            case NONE:
                // fall through
            case INCOMPLETE:
                setStatus(p, QuestStatus.NONE);
                break;
        }
    }

    private void setStatus(Player p, QuestStatus status) {

        if (status == QuestStatus.NONE) {
            _playerNode.remove(p.getUniqueId().toString());
        }
        else {
            _playerNode.set(p.getUniqueId().toString(), status);
        }

        if (status.getCurrentStatus() == CurrentQuestStatus.NONE) {
            _playerQuests.removeValue(p.getUniqueId(), this);
        }
        else if (status.getCurrentStatus() == CurrentQuestStatus.IN_PROGRESS) {
            _playerQuests.put(p.getUniqueId(), this);
        }

        _playerNode.saveAsync(null);
    }

    private void loadSettings() {
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

    }

}
