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

import com.jcwhatever.arborianquests.ArborianQuests;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Manages quests.
 */
public class QuestManager implements IPluginOwned {

    private final Plugin _plugin;
    private final IDataNode _dataNode;
    private final Map<String, Quest> _quests = new HashMap<>(20);
    private final Map<String, Quest> _created = new HashMap<>(20);

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  The managers data node.
     */
    public QuestManager(Plugin plugin, IDataNode dataNode) {
        PreCon.notNull(plugin);
        PreCon.notNull(dataNode);

        _plugin = plugin;
        _dataNode = dataNode;

        load();
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the managers data node.
     */
    public IDataNode getDataNode() {
        return _dataNode;
    }

    /**
     * Create or retrieve a primary quest.
     *
     * <p>If a quest by the specified name already exists, it is
     * returned. Otherwise a new primary quest is created with the
     * specified display name.</p>
     *
     * @param questName    The name of the quest. Should be a name capable
     *                     of being stored in a data node.
     * @param displayName  The quests display name.
     *
     * @return  The quest instance.
     */
    public Quest createPrimary(String questName, String displayName) {
        PreCon.notNullOrEmpty(questName);
        PreCon.notNull(displayName);

        questName = questName.toLowerCase();

        Quest quest = _quests.get(questName);
        if (quest != null)
            return quest;

        IDataNode dataNode = DataStorage.get(ArborianQuests.getPlugin(), new DataPath("quests." + questName));
        dataNode.load();

        dataNode.set("display", displayName);
        dataNode.save();

        quest = new PrimaryQuest(questName, displayName, dataNode);

        _quests.put(questName, quest);
        _created.put(questName, quest);

        return quest;
    }

    /**
     * Dispose a quest.
     *
     * <p>Removes the quest from any parent quests and deletes its data nodes.</p>
     *
     * @param quest  The quest to dispose.
     *
     * @return  True if the quest was found and disposed.
     */
    public boolean dispose(Quest quest) {
        PreCon.notNull(quest);

        if (quest instanceof PrimaryQuest) {

            DataStorage.remove(ArborianQuests.getPlugin(), new DataPath("quests." + quest.getName()));
            _created.remove(quest.getName());
            return _quests.remove(quest.getName()) != null;
        }
        else if (quest instanceof SubQuest) {

            SubQuest subQuest = (SubQuest)quest;

            Quest parent = subQuest.getParent();
            return parent.removeQuest(subQuest.getName());
        }
        else {
            throw new AssertionError();
        }
    }

    /**
     * Get a quest by name. If the quest is a sub quest, the name should be
     * include its full path delimited with periods.
     *
     * <p>i.e. primaryQuestName.subQuest1.subQuest2</p>
     *
     * @param questName  The name of the quest.
     *
     * @return  The quest instance or null if not found.
     */
    @Nullable
    public Quest getQuest(String questName) {
        PreCon.notNullOrEmpty(questName);

        if (questName.indexOf('.') != -1) {
            return getSubQuest(questName);
        }

        return _quests.get(questName.toLowerCase());
    }

    /**
     * Get all current quests. These are the quests that
     * are created by the scripts during the server session.
     */
    public Collection<Quest> getQuests() {
        return Collections.unmodifiableCollection(_quests.values());
    }

    /**
     * Get all saved quests, even ones not re-generated by scripts.
     */
    public Collection<Quest> getCreatedQuests() {
        return Collections.unmodifiableCollection(_created.values());
    }

    private void load() {

        // load quests
        List<String> questNames = _dataNode.getStringList("quests", null);
        if (questNames != null) {

            for (String questName : questNames) {

                IDataNode node = DataStorage.get(_plugin, new DataPath("quests." + questName));
                node.load();

                String displayName = node.getString("display", questName);
                if (displayName == null)
                    throw new AssertionError();

                PrimaryQuest quest = new PrimaryQuest(questName, displayName, node);

                _created.put(questName, quest);
            }
        }
    }

    @Nullable
    private Quest getSubQuest(String questName) {

        String[] names = TextUtils.PATTERN_DOT.split(questName);

        Quest current = getQuest(names[0]);
        if (current == null)
            return null;

        for (int i= 1; i < names.length; i++) {
            current = current.getQuest(names[i]);
            if (current == null)
                return null;
        }

        return current;
    }
}
