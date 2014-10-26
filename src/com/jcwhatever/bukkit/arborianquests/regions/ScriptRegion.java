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


package com.jcwhatever.bukkit.arborianquests.regions;

import com.jcwhatever.bukkit.arborianquests.ArborianQuests;
import com.jcwhatever.bukkit.arborianquests.quests.Quest;
import com.jcwhatever.bukkit.arborianquests.quests.QuestManager;
import com.jcwhatever.bukkit.generic.collections.StackedMap;
import com.jcwhatever.bukkit.generic.regions.Region;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A region used by scripts for region related events.
 */
public class ScriptRegion extends Region {

    private List<IScriptRegionResult> _onEnter = new ArrayList<>(10);
    private List<IScriptRegionResult> _onLeave = new ArrayList<>(10);
    private Map<Quest, IScriptRegionResult> _onQuestEnter = new StackedMap<>(30);
    private Map<Quest, IScriptRegionResult> _onQuestLeave = new StackedMap<>(30);

    /**
     * Constructor.
     *
     * @param name      The name of the region.
     * @param settings  The data node to load and save settings.
     */
    public ScriptRegion(String name, IDataNode settings) {
        super(ArborianQuests.getInstance(), name, settings);
    }

    /**
     * Remove all handlers
     */
    public void clearHandlers() {
        _onEnter.clear();
        _onLeave.clear();
        _onQuestEnter.clear();
        _onQuestLeave.clear();
        setIsPlayerWatcher(false);
    }

    /**
     * Add a handler to be run whenever a player enters the region.
     *
     * @param handler  The handler.
     */
    public boolean addOnEnter(IScriptRegionResult handler) {
        PreCon.notNull(handler);

        _onEnter.add(handler);
        setIsPlayerWatcher(true);
        return true;
    }

    /**
     * Add a handler to be run whenever a player enters the region and
     * is on the specified quest.
     *
     * @param questName  The name of the quest.
     * @param handler    The handler.
     */
    public boolean addOnQuestEnter(String questName, IScriptRegionResult handler) {
        PreCon.notNullOrEmpty(questName);
        PreCon.notNull(handler);

        questName = questName.toLowerCase();

        Quest quest = QuestManager.get(questName);
        if (quest == null)
            return false;

        _onQuestEnter.put(quest, handler);
        setIsPlayerWatcher(true);
        return true;
    }

    /**
     * Add a handler to be run whenever a player leaves the region.
     *
     * @param handler  The handler.
     */
    public boolean addOnLeave(IScriptRegionResult handler) {
        PreCon.notNull(handler);

        _onLeave.add(handler);
        setIsPlayerWatcher(true);
        return true;
    }

    /**
     * Add a handler to be run whenever a player leaves the region.
     *
     * @param handler  The handler.
     */
    public boolean addOnQuestLeave(String questName, IScriptRegionResult handler) {
        PreCon.notNullOrEmpty(questName);
        PreCon.notNull(handler);

        questName = questName.toLowerCase();

        Quest quest = QuestManager.get(questName);
        if (quest == null)
            return false;

        _onQuestLeave.put(quest, handler);
        setIsPlayerWatcher(true);
        return true;
    }


    @Override
    protected void onPlayerEnter(Player p) {

        // run global handlers
        for (IScriptRegionResult subscriber : _onEnter) {
            subscriber.call(p, this);
        }

        // run quest handlers
        Set<Quest> quests = Quest.getPlayerQuests(p);
        if (quests != null && !quests.isEmpty()) {

            for (Quest quest : quests) {
                IScriptRegionResult handler = _onQuestEnter.get(quest);
                if (handler != null) {
                    handler.call(p, this);
                    break;
                }
            }
        }
    }

    @Override
    protected void onPlayerLeave(Player p) {

        // run global handlers
        for (IScriptRegionResult subscriber : _onLeave) {
            subscriber.call(p, this);
        }

        // run quest handlers
        Set<Quest> quests = Quest.getPlayerQuests(p);
        if (quests != null && !quests.isEmpty()) {

            for (Quest quest : quests) {
                IScriptRegionResult handler = _onQuestLeave.get(quest);
                if (handler != null) {
                    handler.call(p, this);
                    break;
                }
            }
        }
    }

    @Override
    protected boolean canDoPlayerEnter(Player p) {
        return !_onEnter.isEmpty() && !_onQuestEnter.isEmpty();
    }

    @Override
    protected boolean canDoPlayerLeave(Player p) {
        return !_onLeave.isEmpty() && !_onQuestLeave.isEmpty();
    }
}
