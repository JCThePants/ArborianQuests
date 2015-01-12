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


package com.jcwhatever.bukkit.arborianquests;

import com.jcwhatever.bukkit.arborianquests.commands.QuestsCommandDispatcher;
import com.jcwhatever.bukkit.arborianquests.items.ScriptItemManager;
import com.jcwhatever.bukkit.arborianquests.locations.ScriptLocationManager;
import com.jcwhatever.bukkit.arborianquests.quests.QuestManager;
import com.jcwhatever.bukkit.arborianquests.regions.ScriptRegionManager;
import com.jcwhatever.bukkit.arborianquests.scripting.QuestsApi;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.storage.DataStorage;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.text.TextColor;

public class ArborianQuests extends NucleusPlugin {

    private static ArborianQuests _instance;

    public static ArborianQuests getPlugin() {
        return _instance;
    }

    private QuestManager _questManager;
    private ScriptRegionManager _scriptRegionManager;
    private ScriptLocationManager _scriptLocationManager;
    private ScriptItemManager _scriptItemManager;

    private IDataNode _metaNode;

    /**
     * Constructor.
     */
    public ArborianQuests() {
        super();

        _instance = this;
    }

    public static QuestManager getQuestManager() {
        return _instance._questManager;
    }

    public static ScriptRegionManager getScriptRegionManager() {
        return _instance._scriptRegionManager;
    }

    public static ScriptLocationManager getScriptLocationManager() {
        return _instance._scriptLocationManager;
    }

    public static ScriptItemManager getScriptItemManager() {
        return _instance._scriptItemManager;
    }

    @Override
    public String getChatPrefix() {
        return TextColor.WHITE + "[" + TextColor.BLUE + "Quests" + TextColor.WHITE + "] ";
    }

    @Override
    public String getConsolePrefix() {
        return "[ArborianQuests] ";
    }

    public IDataNode getMetaDataNode() {
        return _metaNode;
    }

    @Override
    protected void onEnablePlugin() {

        _metaNode = DataStorage.get(ArborianQuests.getPlugin(), new DataPath("meta"));
        _metaNode.load();

        IDataNode regionNode = DataStorage.get(this, new DataPath("regions"));
        regionNode.load();

        IDataNode locationNode = DataStorage.get(this, new DataPath("locations"));
        locationNode.load();

        IDataNode itemsNode = DataStorage.get(this, new DataPath("items"));
        itemsNode.load();

        _questManager = new QuestManager(this, getDataNode());
        _scriptRegionManager = new ScriptRegionManager(regionNode);
        _scriptLocationManager = new ScriptLocationManager(locationNode);
        _scriptItemManager = new ScriptItemManager(itemsNode);

        registerCommands(new QuestsCommandDispatcher());

        Nucleus.getScriptApiRepo().registerApiType(this, QuestsApi.class);
    }

    @Override
    protected void onDisablePlugin() {

        Nucleus.getScriptApiRepo().unregisterApiType(this, QuestsApi.class);
    }
}
