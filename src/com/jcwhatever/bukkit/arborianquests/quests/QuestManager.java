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

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestManager {

    private static Map<String, Quest> _quests = new HashMap<>(20);

    public static Quest create(String questName, String displayName) {
        PreCon.notNullOrEmpty(questName);
        PreCon.notNull(displayName);

        questName = questName.toLowerCase();

        Quest quest = _quests.get(questName);
        if (quest != null)
            return quest;

        IDataNode dataNode = DataStorage.getStorage(ArborianQuests.getPlugin(), new DataPath("quests." + questName));
        dataNode.load();
        dataNode.set("quest-name", questName);
        dataNode.set("display-name", displayName);
        dataNode.saveAsync(null);

        quest = new Quest(questName, displayName, dataNode);

        _quests.put(questName, quest);

        return quest;
    }

    public static boolean dispose(String questName) {
        PreCon.notNullOrEmpty(questName);

        questName = questName.toLowerCase();

        DataStorage.removeStorage(ArborianQuests.getPlugin(), new DataPath("quests." + questName));

        return _quests.remove(questName) != null;
    }

    @Nullable
    public static Quest get(String questName) {
        PreCon.notNullOrEmpty(questName);

        return _quests.get(questName.toLowerCase());
    }

    public static List<Quest> getQuests() {
        return new ArrayList<>(_quests.values());
    }

}
