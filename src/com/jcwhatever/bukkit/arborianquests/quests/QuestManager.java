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

        IDataNode dataNode = DataStorage.getStorage(ArborianQuests.getInstance(), new DataPath("quests." + questName));
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

        DataStorage.removeStorage(ArborianQuests.getInstance(), new DataPath("quests." + questName));

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
